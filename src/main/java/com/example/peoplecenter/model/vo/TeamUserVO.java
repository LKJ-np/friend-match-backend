package com.example.peoplecenter.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;


/**
 * @Description:队伍与用户信息封装类（脱敏）
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.vo
 * @Project：friend-match-backend
 * @name：TeamQueryRequest
 * @Date：2023/11/23 15:00
 * @Filename：TeamQueryRequest
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = -6571911266382627359L;
    /**
     * id
     */
    private Long id;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;

}
