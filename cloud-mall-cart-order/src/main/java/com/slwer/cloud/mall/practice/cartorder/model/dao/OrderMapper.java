package com.slwer.cloud.mall.practice.cartorder.model.dao;

import com.slwer.cloud.mall.practice.cartorder.model.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(String orderNo);

    List<Order> selectOrderForCustomer(Integer userId);

    List<Order> selectAllForAdmin();

    List<Order> selectUnpaidOrders(@Param("begTime") Date begTime, @Param("endTime") Date endTime);
}