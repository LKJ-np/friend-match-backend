package com.example.peoplecenter.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户注册请求体,实现序列化
 */
@Data
public class regist implements Serializable {

    private static final long serialVersionUID = 5383909796055571791L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

}
