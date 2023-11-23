package com.example.peoplecenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:数据预热
 * @Author：LKJ
 * @Package：com.example.peoplecenter.job
 * @Project：friend-match-backend
 * @name：PreCacheJob1
 * @Date：2023/11/20 16:42
 * @Filename：PreCacheJob1
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     *不加分布式锁的写法
     */
//    // 每天执行，预热推荐用户
//    @Scheduled(cron = "0 36 15 * * *")   //自己设置时间测试
//    public void doCacheRecommendUser() {
//        //查数据库
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
//        String redisKey = String.format("friend:user:recommend:%s",mainUserList);
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //写缓存,30s过期
//        try {
//            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
//        } catch (Exception e){
//            log.error("redis set key error",e);
//        }
//    }
    /**
     *加分布式锁的写法,自己设置过期时间
     */
        // 每天执行，预热推荐用户
//    @Scheduled(cron = "0 37 16 * * *")   //自己设置时间测试
//    public void doCacheRecommendUser() {
//        RLock lock = redissonClient.getLock("friend:precache:recommend:dosynccache");
//        try {
//            //只有一个线程可以取到锁
//            if (lock.tryLock(0,10000,TimeUnit.MILLISECONDS)){
//                System.out.println("线程号：" + Thread.currentThread().getId());
//                for (Long userId : mainUserList){
//                    //查数据库
//                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
//                    String redisKey = String.format("friend:user:recommend:%s",mainUserList);
//                    ValueOperations valueOperations = redisTemplate.opsForValue();
//                    //写缓存,30s过期
//                    try {
//                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
//                    } catch (Exception e){
//                        log.error("redis set key error",e);
//                    }
//                }
//            }
//        } catch (InterruptedException e) {
//            log.error("docacheRedcommendUser error",e);
//        } finally {
//            //只能释放自己的锁
//            if (lock.isHeldByCurrentThread()){
//                System.out.println("unlock:" + Thread.currentThread().getId());
//                lock.unlock();
//            }
//        }
//    }

    /**
     *加分布式锁的写法,看门狗机制，自动续期
     */
    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 03 10 * * *")   //自己设置时间测试
    public void doCacheRecommendUser1() {
        RLock lock = redissonClient.getLock("friend:precache:recommend:dosynccache");
        try {
            //只有一个线程可以取到锁
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("线程号：" + Thread.currentThread().getId());
                for (Long userId : mainUserList){
                    //查数据库
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("friend:user:recommend:%s",mainUserList);
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    //写缓存,30s过期
                    try {
                        valueOperations.set(redisKey,userPage,3000, TimeUnit.MILLISECONDS);
                    } catch (Exception e){
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("docacheRedcommendUser error",e);
        } finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
