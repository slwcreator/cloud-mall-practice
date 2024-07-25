package com.slwer.cloud.mall.practice.cartorder.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生成订单编号工具类
 */
public class OrderCodeFactory {
    private static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    private static Integer getRandom() {
        Random random = new Random();
        return random.nextInt(90000) + 10000;
    }

    public static String getOrderCode() {
        return getDateTime() + getRandom();
    }
}
