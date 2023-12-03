package com.example.peoplecenter.service;

import com.example.peoplecenter.model.domain.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.service
 * @Project：friend-match-backend
 * @name：TestGson
 * @Date：2023/12/3 9:51
 * @Filename：TestGson
 */
@SpringBootTest
public class TestGson {

    @Test
    void TestGson(){
        Gson gson = new Gson();

        //将对象转换为json
        User user = new User();
        user.setId(1);
        user.setUsername("gsonTest");
        String s = gson.toJson(user);
        System.out.println(s);

       //将json转化为对象
        Gson gson1 = new Gson();
        String Userjson ="{\"id\":1,\"username\":\"gsontest\"}";
        User user1 = gson1.fromJson(Userjson, User.class);
        System.out.println(user1);
    }
}
