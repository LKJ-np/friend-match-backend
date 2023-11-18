package com.example.peoplecenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.mapper.UserMapper;
import com.example.peoplecenter.model.User;
import com.example.peoplecenter.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.peoplecenter.constant.UserContant.ADMIN_ROLE;
import static com.example.peoplecenter.constant.UserContant.USER_LOGIN_STATE;

/**
* @author PC
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-08 20:45:11
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private final String SALT = "LOVE";


    /**
     * 注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegist(String userAccount, String userPassword, String checkPassword ,String planetCode) {
//       非空
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)) {
            System.out.println("is null");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
//      账户不小于4位
        if (userAccount.length() < 4){
            System.out.println("userAccount xiaoyu 4");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户账户过短");

        }
        //      密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            System.out.println("userPassword xiaoyu 8 or checkPassword xiaoyu 8");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户密码过短");

        }
        if (planetCode.length() > 5){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"星球编号过长");
        }

        //     账户不能含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            System.out.println("userAccount is novalid");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号不合法");
        }

        //      密码与校验密码不相同
        if (!userPassword.equals(checkPassword)){
            System.out.println(" checkPassword  and userPassword are no same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"密码与校验密码不对");
        }
        //      账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        System.out.println("count:"+count);
        if (count > 0) {
            System.out.println(" userAccount  is same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账户重复");
        }
        //      星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        System.out.println("count:"+count);
        if (count > 0) {
            System.out.println(" planetCode  is same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"星球编号重复");
        }

//        密码加密
        String newpassword= DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newpassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult){
            System.out.println(" save  is false");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"保存失败");
        }
        return user.getId();
    }

    /**
     * 登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求
     * @return 脱敏后user对象
     */
    @Override
    public User userLogin(String userAccount, String userPassword,HttpServletRequest request) {
        //非空
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        //      账户不小于4位
        if (userAccount.length() < 4){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户账户过短");
        }
        //      密码不小于8位
        if (userPassword.length() < 8 ){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户密码过短");
        }
        //     账户不能含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号不合法");
        }
//      密码加密
        String newpassword= DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//      查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",newpassword);
        User user =userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info(" user login false");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号重复");
        }
//        用户脱敏
        User safetyUser = getSafetyUser(user);
//        记录用户的状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            throw  new BusinessException(ErrorCode.NULL_ERROR,"账号不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userlogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        throw  new BusinessException(ErrorCode.SUCCESS,"注销成功");
    }

    /**
     * 使用sql根据tags查询用户信息
     * @param tagNameList 标签列表
     * @return
     */
    @Override
    public List<User> sqlsearchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签为空");
        }
        long startTime =System.currentTimeMillis();
        //1.sql查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and 查询
        //like ’Java‘ and like ’Python‘
        for (String tagName : tagNameList){
            queryWrapper.like("tags",tagName);
        }
        List<User> userList =userMapper.selectList(queryWrapper);
        log.info("sql query time = " + (System.currentTimeMillis()-startTime));
        //返回脱敏后的用户信息
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 使用内存根据tags查询用户信息
     * @param tagNameList 标签列表
     * @return
     */
    @Override
    public List<User> memorysearchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签为空");
        }
        long startTime =System.currentTimeMillis();
        //2.内存查询
        //1.先查询所有用户,根据最多的tag来筛选第一次
        //测试与sql相比的时间，先做一次数据库连接
//        userMapper.selectCount(null);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        //userList.parallelStream().filter(user -> {//并行流，并发有线程池，默认用的线程池是forkjoinpull
        userList.stream().filter(user -> {
            String tags = user.getTags();
            //如果用户没有标签，返回false
            if (StringUtils.isBlank(tags)){
                return false;
            }
            //json转Java对象
            Set<String> tempjson = gson.fromJson(tags, new TypeToken<Set<String>>() {}.getType());
            //判空
            tempjson = Optional.ofNullable(tempjson).orElse(new HashSet<>());
            for (String tagname :tempjson){
                if (!tempjson.contains(tagname)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        log.info("sql query time = " + (System.currentTimeMillis()-startTime));
        return userList;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userobj;
        return user !=null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 当前登录用户是否为管理员
     * @param loginuser
     * @return
     */
    @Override
    public boolean isAdmin(User loginuser) {
        return loginuser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User user =(User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    /**
     * 更新用户信息
     * @param user
     * @param loginuser
     * @return
     */
    @Override
    public int updateUser(User user, User loginuser) {
        //查出当前的用户
        long id = user.getId();
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //2.校验权限
        //2.1管理员可以更新任何消息
        //2.2当前用户只能更新自己的消息
        if (!isAdmin(loginuser) && id !=loginuser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(user.getId());
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

}




