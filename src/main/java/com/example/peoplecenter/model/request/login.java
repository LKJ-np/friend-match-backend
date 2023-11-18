package com.example.peoplecenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体,实现序列化
 */
@Data
public class login implements Serializable {


//    private static final long serialVersionUID = -7608306909951675478L;

    private String userAccount;

    private String userPassword;



}
