package com.example.peoplecenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.request
 * @Project：friend-match-backend
 * @name：TeamQuitRequest
 * @Date：2023/11/27 15:07
 * @Filename：TeamQuitRequest
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 4063478153405020509L;

    /**
     * id
     */
    private Long teamId;
}
