package com.slwer.cloud.mall.practice.cartorder.feign;

import com.slwer.cloud.mall.practice.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @GetMapping("/getUser")
    User getUser();
}
