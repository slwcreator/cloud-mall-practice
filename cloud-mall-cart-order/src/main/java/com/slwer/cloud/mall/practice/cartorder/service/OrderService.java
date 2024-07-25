package com.slwer.cloud.mall.practice.cartorder.service;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.slwer.cloud.mall.practice.cartorder.model.vo.OrderVO;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo<OrderVO> listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    PageInfo<OrderVO> listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);
}
