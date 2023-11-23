package com.example.peoplecenter.model.dto;


import com.example.peoplecenter.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * @Description:队伍查询封装类
 * @Author：LKJ
 * @Package：com.example.peoplecenter.model.dto
 * @Project：friend-match-backend
 * @name：TeamQuery
 * @Date：2023/11/22 21:10
 * @Filename：TeamQuery
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

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
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

}