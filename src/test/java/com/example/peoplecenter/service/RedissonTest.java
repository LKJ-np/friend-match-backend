package com.example.peoplecenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.service
 * @Project：friend-match-backend
 * @name：RedissonTest
 * @Date：2023/11/21 16:13
 * @Filename：RedissonTest
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void RedissonTest(){
        //List，数据存在本地JVM内存中
//        List<String> list = new LinkedList<>();
//        list.add("lkj");
//        list.get(0);
        //Map
        HashMap<String,Object> map = new HashMap<>();
        map.put("lkj",123);
        System.out.println(map.get("lkj"));
        //Set

        //Redisson，数据存在Redis内存中
//        RList<Object> redissonClientList = redissonClient.getList("friend:user:recommend:Redisson");
//        redissonClientList.add("lkj");
//        redissonClientList.get(0);
//        redissonClientList.remove(0);

        RMap<Object, Object> redissonClientMap = redissonClient.getMap("friend:user:recommend:Redisson");
        redissonClientMap.put("lkj",123);
        redissonClientMap.get("lkj");
    }
}
