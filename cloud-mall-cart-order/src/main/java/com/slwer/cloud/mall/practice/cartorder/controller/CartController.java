package com.slwer.cloud.mall.practice.cartorder.controller;

import com.slwer.cloud.mall.practice.cartorder.filter.UserFilter;
import com.slwer.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.slwer.cloud.mall.practice.cartorder.service.CartService;
import com.slwer.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    CartService cartService;

    @GetMapping("/list")
    @ApiOperation("获取购物车列表")
    public ApiRestResponse<List<CartVO>> list() {
        //内部获取用户 ID，防止横向越权
        List<CartVO> cartVOList = cartService.list(UserFilter.userThreadLocal.get().getId());
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse<List<CartVO>> add(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOList = cartService.add(UserFilter.userThreadLocal.get().getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车中某个商品的数量")
    public ApiRestResponse<List<CartVO>> update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOList = cartService.update(UserFilter.userThreadLocal.get().getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车中的某个商品")
    public ApiRestResponse<List<CartVO>> delete(@RequestParam Integer productId) {
        List<CartVO> cartVOList = cartService.delete(UserFilter.userThreadLocal.get().getId(), productId);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/select")
    @ApiOperation("选中/不选中购物车中的某个商品")
    public ApiRestResponse<List<CartVO>> select(@RequestParam Integer productId, @RequestParam Integer selected) {
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.userThreadLocal.get().getId(), productId, selected);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全选中/全不选中购物车中的某个商品")
    public ApiRestResponse<List<CartVO>> selectAll(@RequestParam Integer selected) {
        List<CartVO> cartVOList = cartService.selectAllOrNot(UserFilter.userThreadLocal.get().getId(), selected);
        return ApiRestResponse.success(cartVOList);
    }
}
