package com.slwer.cloud.mall.practice.cartorder.feign;

import com.slwer.cloud.mall.practice.cartorder.model.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cloud-mall-category-product")
public interface ProductFeignClient {
    @GetMapping("/product/detailForFeign")
    Product detailForFeign(@RequestParam Integer id);

    @PostMapping("/product/updateStock")
    void updateStock(@RequestParam Integer productId, @RequestParam Integer stock);
}
