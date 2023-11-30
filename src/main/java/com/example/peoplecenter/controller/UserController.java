package com.example.peoplecenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.peoplecenter.common.BaseResponse;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.common.ResultUtil;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.model.request.login;
import com.example.peoplecenter.model.request.regist;
import com.example.peoplecenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.example.peoplecenter.common.ErrorCode.*;
import static com.example.peoplecenter.constant.UserContant.USER_LOGIN_STATE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Slf4j
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = {"http://8.134.201.204:3000"},allowCredentials = "true")
//@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
//@CrossOrigin(origins = "*",allowCredentials="true",allowedHeaders = "*",methods = {POST,GET})
public class UserController {

    @Resource
    UserService userService;

    @Resource
    RedisTemplate<String,Object> redisTemplate;

    /**
     * 用户注册
     * @param regist
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody regist regist){
        if (regist == null){
           throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String userAccount = regist.getUserAccount();
        String userPassword = regist.getUserPassword();
        String checkPassword = regist.getCheckPassword();
        String planetCode = regist.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        long result = userService.userRegist(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtil.success(result);
    }

    /**
     * 用户登录
     * @param login
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> UserLogin(@RequestBody login login, HttpServletRequest request){
        if (login == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String userAccount = login.getUserAccount();
        String userPassword = login.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }

    /**
     * 用户注銷
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> UserLogout( HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        int result = userService.userlogout(request);
        return ResultUtil.success(result);
    }

    /**
     * 获取登录状态
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> current(HttpServletRequest request){
        Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentuser =(User) userobj;
        if (currentuser == null){
            throw new BusinessException(NOT_LOGIN);
        }
        Long id = currentuser.getId();
//        todo 校验用户是否合法
        User safetyUser = userService.getById(id);
        User result = userService.getSafetyUser(safetyUser);
        return ResultUtil.success(result);
    }

    /**
     * 用户查询
     * @param username 用户名字
     * @return
     */
    @GetMapping("/search")
    public BaseResponse <List<User>> usersearch(String username,HttpServletRequest request){
        boolean admin =userService.isAdmin(request);
        if (!admin){
            throw new BusinessException(NO_AUTH,"缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper =new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(result);
    }

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersBytags (@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(PARAM_ERROR);
        }
        List<User> userList = userService.sqlsearchUserByTags(tagNameList);
        return ResultUtil.success(userList);
    }

    /**
     * 用户推荐，分页查询，设置缓存，先缓存一批数据
     * @param request
     * @return
     */
    // todo 推荐多个，未实现
    @GetMapping("/recommend")
    public BaseResponse <Page<User>> recomendUsers(long pageSize, long pageNum, HttpServletRequest request){
        //todo 将这段代码写入业务层
        User loginUser = userService.getCurrentUser(request);
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtil.success(userPage);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        //写缓存,如果缓存写入失败，将返回的值返回前端
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtil.success(userPage);
    }

    /**
     * 更新用户信息
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        if (user == null){
            throw new BusinessException(PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        int result = userService.updateUser(user, currentUser);
        return ResultUtil.success(result);
    }

    /**
     * 用户删除
     * @param id 用户id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse <Boolean> userdelete(@RequestBody int id, HttpServletRequest request){
        boolean admin =userService.isAdmin(request);
        if (!admin){
            throw new BusinessException(NO_AUTH);
        }
        if (id <= 0){
            throw new BusinessException(PARAM_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }

    /**
     * 获取最匹配的用户
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num,HttpServletRequest request){
        if (num <= 0 || num > 20){
            throw new BusinessException(PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        return ResultUtil.success(userService.matchUser(num,currentUser));
    }
}
