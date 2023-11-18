package com.example.peoplecenter.service;

import com.example.peoplecenter.model.User;
import org.apache.catalina.connector.Request;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
//
//    @Test
//    public void testInsert(){
//
//        User user = new User();
//        user.setUsername("lkjyy");
//        user.setUserAccount("1234567");
//        user.setAvatarUrl("hhhhhhhhhhhhhhhhhhh");
//        user.setGender(0);
//        user.setUserPassword("1234567");
//        user.setPhone("123");
//        user.setEmail("123");
//        user.setUserStatus(0);
//        boolean result = userService.save(user);
//        System.out.println(user.getId());
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void userRegist() {
////        测试非空
////        String userAccount = "";
////        String userPassword = "12345678";
////        String checkPassword ="12345678";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
////        //        账户不小于4位
////        String userAccount = "123";
////        String userPassword = "12345678";
////        String checkPassword ="12345678";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
////        //        密码不小于8位
////        String userAccount = "123456";
////        String userPassword = "123";
////        String checkPassword ="123";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
//
////        //        账户不能含特殊字符
////        String userAccount = "12345678@#￥";
////        String userPassword = "12355555555";
////        String checkPassword ="12355555555";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
//
////        //      密码与校验密码不相同
////        String userAccount = "12345678";
////        String userPassword = "1235555555";
////        String checkPassword ="12355555555";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
//
//        //       账户不能重复
////        String userAccount = "1234567lkj";
////        String userPassword = "123456789";
////        String checkPassword ="123456789";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
//
//        //       账户不能重复
////        String userAccount = "yupi";
////        String userPassword = "12345678";
////        String checkPassword ="12345678";
////        long l = userService.userRegist(userAccount, userPassword, checkPassword);
////        System.out.println(l);
//    }
//
////    @Test
////    User  userlogin() {
////        //       账户不能重复
////        String userAccount = "1234567lkj";
////        String userPassword = "12345678";
////        HttpServletRequest httpServletRequest = null;
////        User user = userService.userLogin(userAccount, userPassword, httpServletRequest);
////        System.out.println(user);
////        return user;
////    }

    @Test
    void sqlsearchUserByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.sqlsearchUserByTags(tagNameList);
        Assert.assertNotNull(userList);

    }
    @Test
    void memorysearchUserByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.memorysearchUserByTags(tagNameList);
        Assert.assertNotNull(userList);
    }
}