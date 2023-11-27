package com.example.peoplecenter.service;

import com.example.peoplecenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.model.dto.TeamQuery;
import com.example.peoplecenter.model.request.TeamJoinRequest;
import com.example.peoplecenter.model.request.TeamUpdateRequest;
import com.example.peoplecenter.model.vo.TeamUserVO;

import java.util.List;


/**
* @author PC
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-11-22 20:00:03
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @return
     */
    long addTeam(Team team,User loginuser);

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param team
     * @param currentUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest team, User currentUser);
}
