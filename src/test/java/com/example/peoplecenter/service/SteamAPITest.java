package com.example.peoplecenter.service;

import com.example.peoplecenter.model.domain.User;
import org.springframework.boot.test.context.SpringBootTest;
import sun.java2d.marlin.DPathConsumer2D;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
public class SteamAPITest {

    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("test1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("test2");

        User user3 = new User();
        user3.setId(100);
        user3.setUsername("test22222222222");

        User user4 = new User();
        user4.setId(101);
        user4.setUsername("test22222222222");

        User user5 = new User();
        user5.setId(0);
        user5.setUsername("test22222222222");

        User user6 = new User();
        user6.setId(77);
        user6.setUsername("test22222222222");

        User user7 = new User();
        user7.setId(102);
        user7.setUsername("test2222222");

        List<User> userList =new ArrayList();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userList.add(user6);
        userList.add(user7);
        //        System.out.println(userList);
        //一、stream 实例化
        // 方法1.集合创建，返回一个顺序流或者一个并行流
        //顺序流
//        Stream<User> stream = userList.stream();
//        System.out.println(stream);
        //并行流
//        Stream<User> stream1 = userList.parallelStream();
//        System.out.println(stream1);

        // 方法2.数组创建
//        int[] dp = new int[]{1,2,3};
//        IntStream stream2 = Arrays.stream(dp);

        //二、stream中间操作
        //1.筛选与切片
//
//        //filter(predicate),从流中排除某些元素
//        Stream<User> stream = userList.stream();
//        stream.filter(user -> user.getId() > 10).forEach(System.out::println);
//        System.out.println("*****************");
//
//        //limit(n),使其元素不超过给定数量
//        userList.stream().limit(2).forEach(System.out::println);
//        System.out.println("*****************");
//
//        //skip(n),跳过元素
//        userList.stream().skip(1).forEach(System.out::println);
//        System.out.println("*****************");
//
//        //distinct(),筛选，去除重复元素
//        userList.stream().distinct().forEach(System.out::println);

        //2.映射
        //map(function f),接收一个函数作为参数，将元素转换成其他形式或提取信息，该函数会被应用到每个元素上，并将其映射成一个新的元素。
//        userList<String> userList1 = Arrays.asuserList("aa","bb","cc","dd");
//        userList1.stream().map(str -> str.toUpperCase()).forEach(System.out::println);

//        Stream<String> stringStream = userList.stream().map(user -> user.getUsername());
//        stringStream.filter(name -> name.length() > 6).forEach(System.out::println);

        //flatmap(function f),接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流

        //3.排序
        //sorted().自然排序
//        userList<Integer> integeruserList =Arrays.asuserList(5,4,3,2,1);
//        integeruserList.stream().sorted().forEach(System.out::print);

//        抛异常：com.example.peoplecenter.model.domain.User cannot be cast to java.lang.Comparable
//        原因：User没有实现Comparable接口
//        userList<User> userList =new ArrayuserList();
//        userList.stream().sorted().forEach(System.out::println);

        //sorted(Comparator com),定制排序
//        userList.stream().sorted((e1,e2)->{
//            return Long.compare(e1.getId(),e2.getId());
//        }).forEach(System.out::println);

        //三、終止操作
        //1.匹配与查找
        //allMatch(predicate p),检查是否匹配所有元素
//        boolean b = userList.stream().allMatch(e -> e.getId() > 11);
//        System.out.println(b);

        //anyMatch(predicate p),检查是否至少匹配一个元素
//        boolean b = userList.stream().anyMatch(e -> e.getId() > 100);
//        System.out.println(b);

        //noneMatch(predicate p),检查是否没有匹配一个元素
//        boolean b = userList.stream().noneMatch(e -> e.getId() > 101);
//        System.out.println(b);

        //findfirst(),查找第一个元素
//        Optional<User> first = userList.stream().findFirst();
//        System.out.println(first);

        //findAny(),查找任意一个元素
//        Optional<User> any = userList.parallelStream().findAny();
//        System.out.println(any);

        //count(),返回流中元素的个数
//        long count = userList.stream().filter(e->e.getId()>100).count();
//        System.out.println(count);

        //max(),返回流中元素的最大值
//        Stream<Long> longStream = userList.stream().map(e -> e.getId());
//        Optional<Long> max = longStream.max(Long::compare);
//        System.out.println(max);

        //min(),返回流中元素的最小值
//        Optional<User> min = userList.stream().min((e1, e2) -> {
//            return Long.compare(e1.getId(), e2.getId());
//        });
//        System.out.println(min);

        //foreach(consumer c),内部迭代
//        userList.stream().forEach(System.out::println);

        //2.归约
        //reduce(T identity,BinaryOperator),可以将流中元素反复结合起来，得到一个值。返回T
//        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        Integer reduce = list.stream().reduce(0, Integer::sum);
//        System.out.println(reduce);

        //reduce(BinaryOperator),可以将流中元素反复结合起来，得到一个值。返回Optional<T>
//        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        Optional<Integer> reduce = list.stream().reduce(Integer::sum);
//        System.out.println(reduce);

        //3.收集
        //collect(Collector c)将流转换为其他形式，接收一个Collector接口的实现，用于给Stream中元素做汇总的方法,转换为set，list，map

//        List<User> userList1 = userList.stream().filter(e -> e.getId() > 101).collect(Collectors.toList());
//        Set<User> collect = userList.stream().filter(e -> e.getId() > 101).collect(Collectors.toSet());
//        userList1.forEach(System.out::println);
//        collect.forEach(System.out::println);


    }
}
