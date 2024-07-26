package com.slwer.cloud.mall.practice.cartorder.service;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.Order;
import com.slwer.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.slwer.cloud.mall.practice.cartorder.model.vo.OrderVO;

import java.util.List;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo<OrderVO> listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo, boolean isFromSystem);

    String qrcode(String orderNo);

    PageInfo<OrderVO> listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);

    List<Order> getUnpaidOrders();
}
