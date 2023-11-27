package com.example.peoplecenter.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * @Description:队伍与用户信息封装类（密码脱敏）
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.vo
 * @Project：friend-match-backend
 * @name：TeamUserVO
 * @Date：2023/11/23 13:36
 * @Filename：TeamUserVO
 */
@Data
public class UserVO {
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    private static final long serialVersionUID = 1L;
}