package com.example.peoplecenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.peoplecenter.model.domain.UserTeam;
import com.example.peoplecenter.service.UserTeamService;
import com.example.peoplecenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-11-22 20:00:03
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




