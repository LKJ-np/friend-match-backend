package com.example.peoplecenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:通用请求参数
 * @Author：LKJ
 * @Package：com.example.peoplecenter.common
 * @Project：friend-match-backend
 * @name：PageRequest
 * @Date：2023/11/22 21:07
 * @Filename：PageRequest
 */

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 971461507865818372L;

    /**
     * 当前页
     */
    protected int pageNum = 1;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

}
