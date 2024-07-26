package com.slwer.cloud.mall.practice.cartorder.controller;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.cartorder.model.request.CreateOrderReq;
import com.slwer.cloud.mall.practice.cartorder.model.vo.OrderVO;
import com.slwer.cloud.mall.practice.cartorder.service.OrderService;
import com.slwer.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class OrderController {

    @Resource
    OrderService orderService;

    @PostMapping("/order/create")
    @ApiOperation("创建订单")
    public ApiRestResponse<String> create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/order/detail")
    @ApiOperation("前台订单详情")
    public ApiRestResponse<OrderVO> detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @GetMapping("/order/list")
    @ApiOperation("用户订单列表")
    public ApiRestResponse<PageInfo<OrderVO>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<OrderVO> pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("/order/cancel")
    @ApiOperation("前台取消订单")
    public ApiRestResponse<String> cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo, false);
        return ApiRestResponse.success();
    }

    @GetMapping("/order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse<String> qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @GetMapping("/pay")
    @ApiOperation("支付订单接口")
    public ApiRestResponse<String> pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
