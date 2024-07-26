package com.slwer.cloud.mall.practice.cartorder.config;

import com.slwer.cloud.mall.practice.cartorder.model.pojo.Order;
import com.slwer.cloud.mall.practice.cartorder.service.OrderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobConfiguration {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedissonClient redissonClient;
    private final Logger log = LoggerFactory.getLogger(JobConfiguration.class);

    @Scheduled(cron = "0 0/5 * * * ?")
    public void cancelUnpaidOrders() {
        RLock redissonLock = redissonClient.getLock("redissonLock");
        boolean b = redissonLock.tryLock();
        if (b) {
            try {
                System.out.println("redisson锁+1");
                List<Order> unpaidOrders = orderService.getUnpaidOrders();
                for (Order order : unpaidOrders) {
                    orderService.cancel(order.getOrderNo(), true);
                }
            } finally {
                redissonLock.unlock();
                System.out.println("redisson锁已释放");
            }
        } else {
            System.out.println("redisson锁未获取到");
        }
    }
}
