package com.example.peoplecenter.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: Redisson操作redis
 * @Author：LKJ
 * @Package：com.example.peoplecenter.config
 * @Project：friend-match-backend
 * @name：RedissonConfig
 * @Date：2023/11/21 15:56
 * @Filename：RedissonConfig
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    @Bean
    public RedissonClient redissonClientbendi() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
//          使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);

        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;

    }

    /**
     * 远程连接redisson
     * @return
     */
//    @Bean
//    public RedissonClient redissonClientyuancheng() {
//        // 1. 创建配置
//        Config config = new Config();
//        String redisAddress = String.format("redis://%s:%s", host, port);
////          使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
//        config.useSingleServer().setAddress(redisAddress).setDatabase(1).setPassword(password);
//
//        // 2. 创建实例
//        RedissonClient redisson = Redisson.create(config);
//        return redisson;
//
//    }
}
