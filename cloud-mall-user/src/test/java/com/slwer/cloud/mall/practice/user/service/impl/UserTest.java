package com.slwer.cloud.mall.practice.user.service.impl;

import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import com.slwer.cloud.mall.practice.user.model.dao.UserMapper;
import com.slwer.cloud.mall.practice.user.model.pojo.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    UserMapper userMapper;

    @Test
    @Transactional
    @Rollback(value = true)//事务自动回滚，默认为true
    public void testUpdateInformation() {
        User user = new User();
        user.setId(1);
        user.setPersonalizedSignature("新签名");

        //更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
        User newUser = userMapper.selectByPrimaryKey(user.getId());
        Assert.assertEquals(newUser.getPersonalizedSignature(), user.getPersonalizedSignature());
    }
}