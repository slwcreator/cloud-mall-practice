package com.slwer.cloud.mall.practice.categoryproduct.mq;

import com.slwer.cloud.mall.practice.categoryproduct.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "queue1")
public class Receiver {

    @Autowired
    ProductService productService;

    @RabbitHandler
    public void process(String message) {
        String[] msg = message.split(",");
        productService.updateStock(Integer.valueOf(msg[0]), Integer.valueOf(msg[1]));
    }
}
