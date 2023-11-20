package com.example.peoplecenter.service;

import com.example.peoplecenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 测试redis的增删改查
     */
    @Test
    void redisTest(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //增
        valueOperations.set("lkjString","lkj123");
        valueOperations.set("lkjInt",1);
        valueOperations.set("lkjDouble",2.0);
        User user = new User();
        user.setId(1);
        user.setUsername("redis增加的用户");
        valueOperations.set("lkjUser",user);

        //查
        Object lkjString = valueOperations.get("lkjString");
        Assert.assertTrue("lkj123".equals((String) lkjString));
        Object lkjInt = valueOperations.get("lkjInt");
        Assert.assertTrue(1 ==(Integer) lkjInt);
        Object lkjDouble = valueOperations.get("lkjDouble");
        Assert.assertTrue(2.0 == (Double) lkjDouble);
        System.out.println(valueOperations.get("lkjUser"));

//        //改
//        valueOperations.set("lkjString","lkj");
//        Object lkjString1 = valueOperations.get("lkjString");
//        Assert.assertTrue("lkj".equals((String) lkjString1));
//
//        //删
//        redisTemplate.delete("lkjString");
    }
}
