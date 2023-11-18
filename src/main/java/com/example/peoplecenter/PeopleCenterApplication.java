package com.example.peoplecenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.peoplecenter.mapper")
@EnableScheduling
public class  PeopleCenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(PeopleCenterApplication.class, args);
        for (String arg:args){
            System.out.println(arg);
        }
    }

}
