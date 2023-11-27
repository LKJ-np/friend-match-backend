package com.example.peoplecenter.model.request;

/**
 * @Description:用户添加队伍请求体
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.request
 * @Project：friend-match-backend
 * @name：TeamAddRequest
 * @Date：2023/11/22 21:55
 * @Filename：TeamAddRequest
 */

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户添加队伍请求体
 *
 * @author yupi
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = -3588256839043815378L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}