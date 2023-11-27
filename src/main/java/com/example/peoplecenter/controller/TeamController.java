package com.example.peoplecenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.peoplecenter.common.BaseResponse;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.common.ResultUtil;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.model.domain.Team;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.model.dto.TeamQuery;
import com.example.peoplecenter.model.request.TeamAddRequest;
import com.example.peoplecenter.model.request.TeamJoinRequest;
import com.example.peoplecenter.model.request.TeamQuitRequest;
import com.example.peoplecenter.model.request.TeamUpdateRequest;
import com.example.peoplecenter.model.vo.TeamUserVO;
import com.example.peoplecenter.service.TeamService;
import com.example.peoplecenter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author：LKJ
 * @Package：com.example.peoplecenter.controller
 * @Project：friend-match-backend
 * @name：TeamController
 * @Date：2023/11/23 22:07
 * @Filename：TeamController
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    TeamService teamService;

    @Resource
    UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long addTeam = teamService.addTeam(team,currentUser);
        return ResultUtil.success(addTeam);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id,HttpServletRequest request){
        if (id <=0 ){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result = teamService.deleteTeam(id,currentUser);
        if (!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"删除失败");
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result =  teamService.updateTeam(teamUpdateRequest,currentUser);
        if (!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"更新失败");
        }
        return ResultUtil.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"搜索失败");
        }
        return ResultUtil.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getlistTeam(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
//        Team team = new Team();
//        //将teamQuery的东西复制到team里
//        BeanUtils.copyProperties(teamQuery,team);
        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery,admin);
        return ResultUtil.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getlistPageTeam(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = new Team();
        //将teamQuery的东西复制到team里
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtil.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        if (teamJoinRequest == null ){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest,currentUser);
        return ResultUtil.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null ){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest,currentUser);
        return ResultUtil.success(result);
    }
}
