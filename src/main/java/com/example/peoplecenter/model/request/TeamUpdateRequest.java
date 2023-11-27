package com.example.peoplecenter.model.request;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:用户更新队伍请求体
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.request
 * @Project：friend-match-backend
 * @name：TeamUpdateRequest
 * @Date：2023/11/23 16:21
 * @Filename：TeamUpdateRequest
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 570410894026534871L;
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
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}