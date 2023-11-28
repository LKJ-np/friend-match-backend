package com.example.peoplecenter.service;

import com.example.peoplecenter.utlis.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:算法工具测试类
 * @Author：LKJ
 * @Package：com.example.peoplecenter.service
 * @Project：friend-match-backend
 * @name：AlgorithmUtilsTest
 * @Date：2023/11/28 21:13
 * @Filename：AlgorithmUtilsTest
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void test() {
        String str1 = "东子是狗";
        String str2 = "东子不是狗";
        String str3 = "东子是梅不是狗";
//        String str4 = "鱼皮是猫";
        // 1
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        // 3
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        System.out.println(score1);
        System.out.println(score2);
    }

    @Test
    void testTags() {
        List<String> list1 = Arrays.asList("java", "大一", "男");
        List<String> list2 = Arrays.asList("java", "大二", "男");
        List<String> list3 = Arrays.asList("python", "大二", "女");

        int score1 = AlgorithmUtils.minDistance(list1, list2);
        // 3
        int score2 = AlgorithmUtils.minDistance(list3, list1);
        System.out.println(score1);
        System.out.println(score2);
    }
}
