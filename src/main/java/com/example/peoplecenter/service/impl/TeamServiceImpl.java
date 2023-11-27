package com.example.peoplecenter.service.impl;

import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.common.TeamStatusEnum;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.model.domain.Team;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.model.domain.UserTeam;
import com.example.peoplecenter.model.dto.TeamQuery;
import com.example.peoplecenter.model.request.TeamJoinRequest;
import com.example.peoplecenter.model.request.TeamUpdateRequest;
import com.example.peoplecenter.model.vo.TeamUserVO;
import com.example.peoplecenter.model.vo.UserVO;
import com.example.peoplecenter.service.TeamService;
import com.example.peoplecenter.mapper.TeamMapper;
import com.example.peoplecenter.service.UserService;
import com.example.peoplecenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author PC
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-11-22 20:00:03
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;

    /**
     * 添加队伍
     * @param team
     * @param loginuser
     * @return
     */
    @Override
    public long addTeam(Team team,User loginuser) {
        //判断参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //是否登录，未登录不允许创建
        if (loginuser == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        final long id = loginuser.getId();
        //3.校验信息
            //a.队伍人数 >1,<20
        Integer maxNum =Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum >20){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍人数不符合要求");
        }
            //b.队伍标题 <=20
        String name = team.getName();
        if (name.length() > 20 || StringUtils.isBlank(name)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍标题不符合要求");
        }
            //c.描述<=512
        String description = team.getDescription();
        if (description.length() >512 && StringUtils.isNotBlank(description)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍描述过长");
        }
            //d.status是否公开，不传默认为0公开
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍状态不满足要求");
        }
            //e.如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(status)){
            if (password.length() >32 || StringUtils.isBlank(password)){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"密码不正确");
            }
        }
            //f.过期时间超时
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"过期时间超时");
        }
            //g.校验用户最多创建5个队伍
            //todo 这里有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",id);
        long count = this.count(queryWrapper);
        if (count >=5){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"最多创建5个队伍");
        }
        //4.插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(id);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"创建队伍失败");
        }
        //5.插入用户与队伍关系到 用户_队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(id);
        userTeam.setJoinTime(new Date());
        boolean save = userTeamService.save(userTeam);
        if (!save){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQuery != null){
            Long id = teamQuery.getId();
            if (id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)){
                queryWrapper.eq("id",idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw->qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            //查询最大人数相等的
            if (maxNum != null && maxNum >0){
                queryWrapper.eq("maxNum",maxNum);
            }
            Long userId = teamQuery.getUserId();
            //根据创建人来查询
            if (userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            //根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
            if (enumByValue == null){
                enumByValue = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && enumByValue.equals(TeamStatusEnum.PRIVATE)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",enumByValue.getValue());
        }
        //不展示已过期的队伍
        //expireTime is null or expireTime > now()
        queryWrapper.and(qw->qw.gt("expireTime",new Date()).or().isNotNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(teamList)){
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOS = new ArrayList<>();
        //关联查询创建人的用户信息
        for (
                Team team : teamList){
            Long userId = team.getUserId();
            if (userId == null){
                continue;
            }
        User user = userService.getById(userId);
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(team,teamUserVO);
        //脱敏用户信息
            if (user !=null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOS.add(teamUserVO);
        }
        return teamUserVOS;
    }

    /**
     * 更新队伍
     * @param team
     * @param currentUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest team, User currentUser) {
        if (team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long id = team.getId();
        if (id <= 0 || id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍不存在");
        }
        //只有管理员或者队伍的创建者可以修改
        if (oldTeam.getUserId() != currentUser.getId() && !userService.isAdmin(currentUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(oldTeam.getStatus());
        if (enumByValue.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"加密房间必须设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(oldTeam,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User currentUser) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0){
            throw  new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw  new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"密码错误");
            }
        }
        //该用户已加入的队伍数量
        long userId = currentUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"最多创建和加入5个队伍");
        }
        //不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户已加入该队伍");
        }
        //已加入队伍的人数
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍已满");
        }
        //加入，修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }
}




