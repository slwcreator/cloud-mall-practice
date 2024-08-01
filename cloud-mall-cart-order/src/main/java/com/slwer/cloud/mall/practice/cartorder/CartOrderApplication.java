package com.slwer.cloud.mall.practice.cartorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.slwer.cloud.mall.practice.cartorder.model.dao")
//@EnableRedisHttpSession
@EnableFeignClients
@ComponentScan({"com.slwer.cloud.mall.practice.cartorder","com.slwer.cloud.mall.practice.common"})
public class CartOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartOrderApplication.class, args);
    }
}
