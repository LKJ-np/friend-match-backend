package com.example.peoplecenter.service;

import com.example.peoplecenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.peoplecenter.model.request.TeamJoinRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author PC
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-08 20:45:11
*/
public interface UserService extends IService<User> {

    /**
     *用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegist(String userAccount , String userPassword ,String checkPassword,String planetCode);

    /**
     *用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用戶注銷
     * @param request
     * @return
     */
    int userlogout(HttpServletRequest request);

    /**
     * 根据所有标签来查询用户（and,sql查询）
     * @param tagNameList 标签列表
     * @return
     */
    List<User> sqlsearchUserByTags(List<String> tagNameList);

    /**
     * 根据所有标签来查询用户（内存查询）
     * @param tagNameList 标签列表
     * @return
     */
    List<User> memorysearchUserByTags(List<String> tagNameList);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginuser
     * @return
     */
    boolean isAdmin(User loginuser);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 更新当前用户
     * @param user
     * @param loginuser
     * @return
     */
    int updateUser(User user,User loginuser);

}
