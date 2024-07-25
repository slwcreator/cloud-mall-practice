package com.slwer.cloud.mall.practice.cartorder.service.impl;

import com.slwer.cloud.mall.practice.cartorder.feign.ProductFeignClient;
import com.slwer.cloud.mall.practice.cartorder.model.dao.CartMapper;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.Cart;
import com.slwer.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.slwer.cloud.mall.practice.cartorder.service.CartService;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Resource
    CartMapper cartMapper;

    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOList;
    }

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //该商品之前不在购物车，需要新增
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            //该商品之前已在购物车，则数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cart.setUpdateTime(new Date());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //该商品之前不在购物车，需要报错
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //该商品之前已在购物车，则设置对应的数量
            cart.setQuantity(count);
            cart.setUpdateTime(new Date());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //该商品之前不在购物车，需要报错
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        } else {
            //该商品之前已在购物车，则删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //该商品之前不在购物车，无法选中/不选中
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //该商品之前已在购物车，可以选中/不选中
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productFeignClient.detailForFeign(productId);
        //判断商品是否存在和是否上架
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (count > product.getStock()) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
        }
    }
}
