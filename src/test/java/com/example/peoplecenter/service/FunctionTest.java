package com.example.peoplecenter.service;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.example.peoplecenter.model.domain.User;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.service
 * @Project：friend-match-backend
 * @name：FunctionTest
 * @Date：2023/12/1 14:58
 * @Filename：FunctionTest
 */
@SpringBootTest
public class FunctionTest {

    public static void main(String[] args) {
        User user =new User();
        user.setUsername("lambdaTest");
        user.setUserAccount("123");
        user.setGender(1);

        Function<User,String> fuc =e->e.getUsername();
        System.out.println(fuc.apply(user));

        System.out.println("*************");
        Function<User,Integer> fuc1 =User::getGender;
        System.out.println(fuc1.apply(user));

    }
}
