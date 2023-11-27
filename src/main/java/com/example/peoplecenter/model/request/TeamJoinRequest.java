package com.example.peoplecenter.model.request;

import lombok.Data;
import java.io.Serializable;


/**
 * @Description:用户加入队伍请求体
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.request
 * @Project：friend-match-backend
 * @name：TeamUpdateRequest
 * @Date：2023/11/23 16:21
 * @Filename：TeamUpdateRequest
 */
@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = 8008955934520070903L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;


}