package com.example.peoplecenter.service;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.service
 * @Project：friend-match-backend
 * @name：LambdaTest
 * @Date：2023/12/1 14:25
 * @Filename：LambdaTest
 */
@SpringBootTest
public class LambdaTest {


    public static void main(String[] args) {
       LambdaTest test = new LambdaTest();

        List<String> names1 = new ArrayList<String>();
        names1.add("Google ");
        names1.add("Runoob ");
        names1.add("Taobao ");
        names1.add("Baidu ");
        names1.add("Sina ");

        List<String> names2 = new ArrayList<String>();
        names2.add("Google ");
        names2.add("Runoob ");
        names2.add("Taobao ");
        names2.add("Baidu ");
        names2.add("Sina ");

        System.out.println("使用java7语法：");
        test.sortUsingjava7(names1);
        System.out.println(names1);

        System.out.println("使用Java8语法：");
        test.sortUsingjava8(names2);
        System.out.println(names2);

        MathOperation addtion = (int a, int b)-> a+b;

        MathOperation subtion =(a,b)-> a-b;

        MathOperation multion =(a,b)->{
            return a * b;
        };

        MathOperation divtion = (a,b)->{
            return  a / b;
        };
        System.out.println("10+5=" + test.operate(10,5,addtion));
        System.out.println("10-5=" + test.operate(10,5,subtion));
        System.out.println("10*5=" + test.operate(10,5,multion));
        System.out.println("10/5=" + test.operate(10,5,divtion));

        GreetingService greetingService1 =message -> System.out.println("hello" + message);

        GreetingService greetingService2 = (message) ->{
            System.out.println("hi" + message);
        };

        greetingService1.sayMessage("run");
        greetingService2.sayMessage("ni");
    }
    interface MathOperation {
        int operation(int a, int b);
    }

    interface GreetingService {
        void sayMessage(String message);
    }

    private int operate(int a, int b, MathOperation mathOperation){
        return mathOperation.operation(a, b);
    }


    private void sortUsingjava7(List<String> list){
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    private void sortUsingjava8(List<String> list){
        Collections.sort(list,(o1,o2)->{
            return o1.compareTo(o2);
        });
    }


}
