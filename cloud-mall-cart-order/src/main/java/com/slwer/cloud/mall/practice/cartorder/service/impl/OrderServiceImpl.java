package com.slwer.cloud.mall.practice.cartorder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.slwer.cloud.mall.practice.cartorder.feign.ProductFeignClient;
import com.slwer.cloud.mall.practice.cartorder.filter.UserFilter;
import com.slwer.cloud.mall.practice.cartorder.model.dao.CartMapper;
import com.slwer.cloud.mall.practice.cartorder.model.dao.OrderItemMapper;
import com.slwer.cloud.mall.practice.cartorder.model.dao.OrderMapper;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.Order;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.OrderItem;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.Product;
import com.slwer.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.slwer.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.slwer.cloud.mall.practice.cartorder.model.vo.OrderItemVO;
import com.slwer.cloud.mall.practice.cartorder.model.vo.OrderVO;
import com.slwer.cloud.mall.practice.cartorder.service.CartService;
import com.slwer.cloud.mall.practice.cartorder.service.OrderService;
import com.slwer.cloud.mall.practice.cartorder.util.OrderCodeFactory;
import com.slwer.cloud.mall.practice.cartorder.util.PageUtils;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import com.slwer.cloud.mall.practice.common.util.QRCodeGenerator;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    CartService cartService;

    @Resource
    ProductFeignClient productFeignClient;

    @Resource
    CartMapper cartMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Value("${file.upload.dir}")
    String FILE_UPLOAD_DIR;

    @Value("${file.upload.uri}")
    String uri;

    @Value("${file.upload.uri.scheme}")
    String scheme;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户 ID
        Integer userId = UserFilter.userThreadLocal.get().getId();
        //从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在、上下架的状态、库存
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转为订单 item 对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productFeignClient.detailForFeign(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
            productFeignClient.updateStock(product.getId(), stock);
        }
        //把购物车中的已勾选商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号
        String orderNo = OrderCodeFactory.getOrderCode();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        orderMapper.insertSelective(order);

        //循环保存每个商品到 order_item 表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        //把结果返回
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productFeignClient.detailForFeign(cartVO.getProductId());
            //判断商品是否存在和是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //订单存在还需要判断所属
        Integer userId = UserFilter.userThreadLocal.get().getId();
        if (!order.getUserId().equals(userId)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        return getOrderVO(order);
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //获取订单对应的 orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(order.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo<OrderVO> listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.userThreadLocal.get().getId();

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectOrderForCustomer(userId);
        PageInfo<Order> pageInfoPO = new PageInfo<>(orderList);

        //当前页的记录数，起始行号，结束行号
        int size = pageInfoPO.getSize();
        int startRow = pageInfoPO.getStartRow();
        int endRow = pageInfoPO.getEndRow();

        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo<OrderVO> pageInfoVO = PageUtils.pageInfo2PageInfoVO(pageInfoPO);
        pageInfoVO.setList(orderVOList);
        pageInfoVO.setSize(size);
        pageInfoVO.setStartRow(startRow);
        pageInfoVO.setEndRow(endRow);
        return pageInfoVO;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public void cancel(String orderNo, boolean isFromSystem) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //订单存在还需要判断所属
        if (!isFromSystem) {
            Integer userId = UserFilter.userThreadLocal.get().getId();
            if (!order.getUserId().equals(userId)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
            }
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.CANCEL_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) {
        String address = uri;
        String payUrl = scheme + address + "/cart-order/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generateQRCode(payUrl, 350, 350, FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
        return scheme + address + "/cart-order/images/" + orderNo + ".png";
    }

    @Override
    public PageInfo<OrderVO> listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        PageInfo<Order> pageInfoPO = new PageInfo<>(orderList);

        //当前页的记录数，起始行号，结束行号
        int size = pageInfoPO.getSize();
        int startRow = pageInfoPO.getStartRow();
        int endRow = pageInfoPO.getEndRow();

        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo<OrderVO> pageInfoVO = PageUtils.pageInfo2PageInfoVO(pageInfoPO);
        pageInfoVO.setList(orderVOList);
        pageInfoVO.setSize(size);
        pageInfoVO.setStartRow(startRow);
        pageInfoVO.setEndRow(endRow);
        return pageInfoVO;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (Objects.equals(order.getOrderStatus(), Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.PAY_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (Objects.equals(order.getOrderStatus(), Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELIVER_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }

        //如果是普通用户，要校验订单所属
        if (UserFilter.userThreadLocal.get().getRole().equals(1) && !order.getUserId().equals(UserFilter.userThreadLocal.get().getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //发货后可以完结订单
        if (Objects.equals(order.getOrderStatus(), Constant.OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.FINISH_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public List<Order> getUnpaidOrders() {
        Date curTime = new Date();
        Date endTime = DateUtils.addDays(curTime, -1);
        Date begTime = DateUtils.addMinutes(endTime, -5);
        return orderMapper.selectUnpaidOrders(begTime, endTime);
    }
}
