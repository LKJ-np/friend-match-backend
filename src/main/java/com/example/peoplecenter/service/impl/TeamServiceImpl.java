package com.example.peoplecenter.service.impl;

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


}




