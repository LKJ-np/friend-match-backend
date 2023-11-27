package com.example.peoplecenter.once;


import com.example.peoplecenter.mapper.UserMapper;
import com.example.peoplecenter.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import javax.annotation.Resource;

/**
 * @author: lkj
 * @ClassName: friend-backend01
 * @Description:
 */
@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 循环插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE )
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假lkj");
            user.setUserAccount("假lkj");
            user.setAvatarUrl("");
            user.setProfile("一条咸鱼");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123456789108");
            user.setEmail("lkj@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("931");
            user.setTags("[]");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println( stopWatch.getLastTaskTimeMillis());

    }
}

