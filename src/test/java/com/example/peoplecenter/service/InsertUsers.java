//package com.example.peoplecenter.service;
//
//
//import com.example.peoplecenter.model.domain.User;
//import org.apache.tomcat.util.threads.ThreadPoolExecutor;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.StopWatch;
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author: lkj
// * @ClassName: friend-backend01
// * @Description:  用户单元测试，注意打包要删掉或者忽略，不然打包一次插入一次
// */
//
//@SpringBootTest
//public class InsertUsers {
//
//    @Resource
//    private UserService userService;
//
//    /**
//     * 线性插入
//     */
//    @Test
//    public void doInsertUser() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 1000000;
//        List<User> userList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假lkj");
//            user.setUserAccount("假lkj");
//            user.setAvatarUrl("");
//            user.setProfile("一条咸鱼");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setPhone("123456789108");
//            user.setEmail("lkj@qq.com");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setPlanetCode("931");
//            user.setTags("[]");
//            userList.add(user);
//        }
//        userService.saveBatch(userList,10000);
//        stopWatch.stop();
//        System.out.println(stopWatch.getLastTaskTimeMillis());
//    }
//
//    /**
//     * 并发插入，使用默认线程池
//     */
//    @Test
//    public void syncdoInsertUser() {
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        //分十组
//        int j = 0;
//        //批量插入的数据大小
//        int batchsize = 10000;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        //i根据数据量和插入批量来计算循环的次数，i就是线程数
//        for (int i = 0; i < 40; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假lkj");
//                user.setUserAccount("假lkj");
//                user.setAvatarUrl("");
//                user.setProfile("一条咸鱼");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("123456789108");
//                user.setEmail("lkj@qq.com");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode("931");
//                user.setTags("[]");
//                userList.add(user);
//                if (j % batchsize == 0) {
//                    break;
//                }
//            }
//            //异步执行
//            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
//                System.out.println("ThreadName：" + Thread.currentThread().getName());
//                userService.saveBatch(userList,batchsize);
//            });
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//            stopWatch.stop();
//            System.out.println(stopWatch.getLastTaskTimeMillis());
//
//        }
//
//
//    /**
//     * 并发插入，使用自定义线程池
//     */
//    @Test
//    public void syncdomaindoInsertUser() {
//        //自定义线程池，如果不自定义，则使用默认的线程池
//        //int corePoolSize,//线程池的核心线程数量
//        //int maximumPoolSize,//线程池的最大线程数
//        //long keepAliveTime,//当线程数大于核心线程数时，多余的空闲线程存活的最长时间
//        //TimeUnit unit,//时间单位
//        //BlockingQueue<Runnable> workQueue,//任务队列，用来储存等待执行任务的队列
//        //ThreadFactory threadFactory,//线程工厂，用来创建线程，一般默认即可
//        //RejectedExecutionHandler handler//拒绝策略，当提交的任务过多而不能及时处理时，我们可以定制策略来处理任务
//
//        ExecutorService executorService =new ThreadPoolExecutor(40,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        //分十组
//        int j = 0;
//        //批量插入的数据大小
//        int batchsize = 25000;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        //i就是线程
//        for (int i = 0; i < 40; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假lkj");
//                user.setUserAccount("假lkj");
//                user.setAvatarUrl("");
//                user.setProfile("一条咸鱼");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("123456789108");
//                user.setEmail("lkj@qq.com");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode("931");
//                user.setTags("[]");
//                userList.add(user);
//                if (j % batchsize == 0) {
//                    break;
//                }
//            }
//            //异步执行
//            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
//                System.out.println("ThreadName：" + Thread.currentThread().getName());
//                userService.saveBatch(userList,batchsize);
//            },executorService);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getLastTaskTimeMillis());
//
//    }
//}
//
//
