package com.example.peoplecenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.mapper.UserMapper;
import com.example.peoplecenter.model.domain.User;
import com.example.peoplecenter.service.UserService;
import com.example.peoplecenter.utlis.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.lucene.util.RamUsageEstimator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;
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
        safetyUser.setTags(originUser.getTags());
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

    /**
     * 推荐匹配用户
     *匹配时间：22777ms
     * pairList的内存大小：654.3MB
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUser(long num, User loginUser) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.last("limit 10");
//        List<User> userList = this.list(queryWrapper);
//         或者用page分页查询，自己输入或默认数值，但这样匹配就有限制了
//        List<User> userList = this.page(new Page<>(pageNum,pageSize),queryWrapper);
//		这里查了所有用户，近100万条
//        List<User> userList = this.list();

        //开始时间
        long start = System.currentTimeMillis();
        //优化查找1：查询选取所需要的列，id，tag
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        //筛选出所有的用户
        List<User> userList = this.list(queryWrapper);

        //获取当前用的tag
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //将json字符串转化为列表形式
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 与 相似度
        List<Pair<User, Long>> pairList = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //优化查找2：去除空tags
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数（当前用户的tagList和除去空tag和自己的其他成员的userTagList）
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            pairList.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = pairList.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 经过编辑距离排序后的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        //从库里查询用户的真实信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //使用in查询符合userIdList的查询结果
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        //使用in查询后没有顺序，所以将编辑距离排序后的顺序返回
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        long end = System.currentTimeMillis();
        System.out.println("匹配时间：" + (end - start) + "ms");
        System.out.println("pairList内存大小：" + RamUsageEstimator.humanSizeOf(pairList));

        for (Pair<User, Long> userPair : topUserPairList) {
            System.out.println("用户id:" + userPair.getKey().getId() + "，距离：" + userPair.getValue());
        }
        System.out.println();
        return finalUserList;
    }

    /**
     * 推荐匹配用户：改进算法一（使用优先队列存储编辑距离较小的n个元素）
     *堆是一种实用的数据结构，常用来求解 Top K 问题，比如 如何快速获取点赞数量最多的十篇文章，本文接口目标是取出编辑距离最小（即匹配度最高）的十个用户。
     * 实现思路：
     * 维护一个节点数量为10的大顶堆，节点的值为编辑距离，堆是一颗完全二叉树，堆中节点的值都大于等于其子节点的值，则堆顶元素的编辑距离最大。
     * 想要维护十个编辑距离最小的元素，只需要在遍历元素的时候，判断新元素的编辑距离是否小于堆顶元素的编辑距离，
     * 若小于，则踢出堆顶元素，加入新元素即可。在java中，可以使用优先队列 PriorityQueue 来实现大顶堆的操作。
     *
     * 匹配时间：20121ms
     * pairList的内存大小：25.8 MB
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUser1(int num, User loginUser) {

        //开始时间
        long start = System.currentTimeMillis();

        //一、优化查找1：从数据库中查询所有用户数据（选取所需要的列，id，tag，并且tag不为空）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);

        //二、获取当前用户的标签信息
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //将json字符串反序列化为集合
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        //三、创建优先队列，用来存储前num个距离最小（距离越小，匹配度越高）的用户
        //创建比较器（按照编辑距离降序排列）
        Comparator<Pair<User,Long>> comparator = new Comparator<Pair<User, Long>>() {
            @Override
            public int compare(Pair<User, Long> o1, Pair<User, Long> o2) {
                return -Long.compare(o1.getValue(),o2.getValue());
            }
        };
        //堆的初始容量
        int initialCapacity = num;
        //维护一个大顶堆，堆的顶部元素最大，在迭代的时候，如果新的编辑距离比堆顶元素小，则将堆顶元素踢出，添加新的元素
        PriorityQueue<Pair<User,Long>> priorityQueue = new PriorityQueue<Pair<User,Long>>(initialCapacity,comparator);

        //四、先将前面num个元素添加到优先队列中
        int userListSize = userList.size();
        //计算提前插入量（用户数量不一定有查询数量多）
        int advanceInsertAmount = Math.min(initialCapacity,userListSize);
        //已经插入优先队列的元素数量
        int insertNUm = 0;
        //记录当前所迭代到用户的索引
        int index = 0;
        while (insertNUm < advanceInsertAmount && index < userListSize -1){
            //index++,是先get，之后才执行+1逻辑
            User user = userList.get(index++);
            String userTags = user.getTags();
            //排除无标签用户和自己
            if (user.getId() == loginUser.getId() || StringUtils.isBlank(userTags)){
                continue;
            }else {
                List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                }.getType());
                //计算编辑距离
                long distance = AlgorithmUtils.minDistance(tagList, userTagList);
                //添加元素到堆中
                priorityQueue.add(new Pair<>(user,distance));
                insertNUm++;
            }
        }
        //五、依次计算剩余所有用户的编辑距离，并更新优先队列的元素
        for (int i = index; i < userListSize ; i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //排除无用标签用户和自己
            if ( StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算编辑距离
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            //获取堆顶元素的编辑距离
            Long biggestDistance = priorityQueue.peek().getValue();
            if (distance < biggestDistance){
                //删除堆顶元素（删除距离最大的元素）
                priorityQueue.poll();
                //添加距离更小的元素
                priorityQueue.add(new Pair<>(user,distance));
            }
        }
        //六、获取用户的详细信息
        List<Long> userIdList = priorityQueue.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        List<User> finalUserList = this.list(new QueryWrapper<User>().in("id", userIdList))
                .stream()
                //用户数据脱敏
                .map(user -> getSafetyUser(user)).collect(Collectors.toList());

        long end = System.currentTimeMillis();

        System.out.println("匹配时间：" + (end - start) +"ms");
        System.out.println("priorityQueue内存大小：" + RamUsageEstimator.humanSizeOf(priorityQueue));

        for (Pair<User,Long> userpair : priorityQueue){
            System.out.println("用户id：" + userpair.getKey().getId() + "距离：" + userpair.getValue());
        }
        return finalUserList;

    }

    /**
     * 推荐匹配用户：改进算法二（使用优先队列存储编辑距离较小的n个元素+数据分批查询、分批处理）
     * ，priorityQueue的内存占用是25MB左右，远远小于未改进前的600MB。但是不要被迷惑了，
     * 上述代码一开始还是使用userList来接收所有用户数据，因此峰值内存并没有减少。
     * 实现思路：
     *既然一开始使用userList来接收所有用户数据会占用不少内存，那是否可以对此进行优化呢？
     * 答案显然是可以的，那就是对数据进行分批查询（分页查询）即可，
     * 查询一批就处理一批，处理完直接将数据丢掉即可，具体操作可以查看下面的代码。
     * 匹配时间：76202ms
     * 中途的userlist内存占用的峰值内存占用就减下来了，但是接口调用时间却翻了三倍，妥妥的“时间换空间”了。
     * pairList的内存大小：24.4MB
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUser2(int num, User loginUser) {

        //开始时间
        long start = System.currentTimeMillis();

        //将数据分批，每批所要处理的数据量
        int batchSize = 400000;
        int current = 1;

        //一、优化查找1：从数据库中查询所有用户数据（选取所需要的列，id，tag，并且tag不为空）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");


        //二、获取当前用户的标签信息
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //将json字符串反序列化为集合
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        //三、创建优先队列，用来存储前num个距离最小（距离越小，匹配度越高）的用户
        //创建比较器（按照编辑距离降序排列）
        Comparator<Pair<User,Long>> comparator = new Comparator<Pair<User, Long>>() {
            @Override
            public int compare(Pair<User, Long> o1, Pair<User, Long> o2) {
                return -Long.compare(o1.getValue(),o2.getValue());
            }
        };
        //堆的初始容量
        int initialCapacity = num;
        //维护一个大顶堆，堆的顶部元素最大，在迭代的时候，如果新的编辑距离比堆顶元素小，则将堆顶元素踢出，添加新的元素
        PriorityQueue<Pair<User,Long>> priorityQueue = new PriorityQueue<Pair<User,Long>>(initialCapacity,comparator);

        while (true){
            System.out.println("current:" + current);
            Page<User> userPage = this.page(new Page<>(current, batchSize), queryWrapper);
            List<User> userList = userPage.getRecords();
            System.out.println("userList内存大小：" + RamUsageEstimator.humanSizeOf(userList));
            if (userList.size() == 0){
                break;
            }
            System.out.println("当前用户id:" + userList.get(0).getId());
            //四、先将前面num个元素添加到优先队列中
            //记录当前所迭代到用户的索引
            int index = 0;
            if (current == 1) {
                int userListSize = userList.size();
                //计算提前插入量（用户数量不一定有查询数量多）
                int advanceInsertAmount = Math.min(initialCapacity, userListSize);
                //已经插入优先队列的元素数量
                int insertNUm = 0;
                while (insertNUm < advanceInsertAmount && index < userListSize - 1) {
                    //index++,是先get，之后才执行+1逻辑
                    User user = userList.get(index++);
                    String userTags = user.getTags();
                    //排除无标签用户和自己
                    if (user.getId() == loginUser.getId() || StringUtils.isBlank(userTags)) {
                        continue;
                    } else {
                        List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                        }.getType());
                        //计算编辑距离
                        long distance = AlgorithmUtils.minDistance(tagList, userTagList);
                        //添加元素到堆中
                        priorityQueue.add(new Pair<>(user, distance));
                        insertNUm++;
                    }
                }
            }
            //五、依次计算剩余所有用户的编辑距离，并更新优先队列的元素
            for (int i = index; i < userList.size(); i++) {
                User user = userList.get(i);
                String userTags = user.getTags();
                //排除无用标签用户和自己
                if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                    continue;
                }
                List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                }.getType());
                //计算编辑距离
                long distance = AlgorithmUtils.minDistance(tagList, userTagList);
                //获取堆顶元素的编辑距离
                Long biggestDistance = priorityQueue.peek().getValue();
                if (distance < biggestDistance) {
                    //删除堆顶元素（删除距离最大的元素）
                    priorityQueue.poll();
                    //添加距离更小的元素
                    priorityQueue.add(new Pair<>(user, distance));
                }
            }
            current++;
        }
        //六、获取用户的详细信息
        List<Long> userIdList = priorityQueue.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        List<User> finalUserList = this.list(new QueryWrapper<User>().in("id", userIdList))
                .stream()
                //用户数据脱敏
                .map(user -> getSafetyUser(user)).collect(Collectors.toList());

        long end = System.currentTimeMillis();

        System.out.println("匹配时间：" + (end - start) +"ms");
        System.out.println("队列长度：" + priorityQueue.size());
        System.out.println("priorityQueue内存大小：" + RamUsageEstimator.humanSizeOf(priorityQueue));

        for (Pair<User,Long> userpair : priorityQueue){
            System.out.println("用户id：" + userpair.getKey().getId() + "距离：" + userpair.getValue());
        }
        return finalUserList;

    }

    /**
     * 推荐匹配用户：改进算法三（使用优先队列存储编辑距离较小的n个元素+数据多线程分批查询、分批处理）
     * 分批处理过程中，userList的内存占用，这样峰值内存占用就减下来了，但是接口调用时间却翻了一倍，妥妥的“时间换空间”了。
     * 实现思路：
     * 既然数据都分批处理了，那为何不想办法让多个线程同时处理呢，这样接口调用时间就可以减下来了。
     * 要注意的是：PriorityQueue是线程不安全的，在使用多线程的时候，应该使用其好兄弟PriorityBlockingQueue。
     * 匹配时间：
     * pairList的内存大小：
     * @param num
     * @param loginUser
     * @return
     */
    //创建自定义线程池
    private ExecutorService executor = new ThreadPoolExecutor(40,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
    @Override
    public List<User> matchUser3(int num, User loginUser) {

        //开始时间
        long start = System.currentTimeMillis();

        //将数据分批，每批所要处理的数据量
        int batchSize = 400000;

        //一、优化查找1：从数据库中查询所有用户数据（选取所需要的列，id，tag，并且tag不为空）
        long totalUserNum = baseMapper.selectCount(new QueryWrapper<>());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");

        //二、获取当前用户的标签信息
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //将json字符串反序列化为集合
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        //三、创建优先队列，用来存储前num个距离最小（距离越小，匹配度越高）的用户
        //创建比较器（按照编辑距离降序排列）
        Comparator<Pair<User,Long>> comparator = new Comparator<Pair<User, Long>>() {
            @Override
            public int compare(Pair<User, Long> o1, Pair<User, Long> o2) {
                return -Long.compare(o1.getValue(),o2.getValue());
            }
        };
        //堆的初始容量
        int initialCapacity = num;
        //维护一个大顶堆，堆的顶部元素最大，在迭代的时候，如果新的编辑距离比堆顶元素小，则将堆顶元素踢出，添加新的元素
        PriorityQueue<Pair<User,Long>> priorityQueue = new PriorityQueue<Pair<User,Long>>(initialCapacity,comparator);

        //计算分批数
        int batchNum = totalUserNum / batchSize +(totalUserNum % batchSize) > 0 ? 1 : 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
       for (int current =1; current <= batchNum; current++){
           //异步执行
           int finalCurrent = current;
           CompletableFuture<Void> future = CompletableFuture.runAsync(()-> {
               Page<User> userPage = this.page(new Page<>(finalCurrent, batchSize), queryWrapper);
               List<User> userList = userPage.getRecords();
               // 四、先将前面num个元素添加到优先队列中
               // 记录当前所迭代到用户的索引
               int index = 0;
               if (finalCurrent == 1) {
                   int userListSize = userList.size();
                   // 计算提前插入量（用户数量还不一定有查询数量多）
                   int advanceInsertAmount = Math.min(initialCapacity, userListSize);
                   // 已经插入优先队列的元素数量
                   int insertNum = 0;
                   while (insertNum < advanceInsertAmount && index < userListSize - 1) {
                       //index++,是先get，之后才执行+1逻辑
                       User user = userList.get(index++);
                       String userTags = user.getTags();
                       //排除无标签用户和自己
                       if (user.getId() == loginUser.getId() || StringUtils.isBlank(userTags)) {
                           continue;
                       } else {
                           List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                           }.getType());
                           //计算编辑距离
                           long distance = AlgorithmUtils.minDistance(tagList, userTagList);
                           //添加元素到堆中
                           priorityQueue.add(new Pair<>(user, distance));
                           insertNum++;
                       }
                   }
               }
               //五、依次计算剩余所有用户的编辑距离，并更新优先队列的元素
               for (int i = index; i < userList.size(); i++) {
                   User user = userList.get(i);
                   String userTags = user.getTags();
                   //排除无用标签用户和自己
                   if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                       continue;
                   }
                   List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                   }.getType());
                   //计算编辑距离
                   long distance = AlgorithmUtils.minDistance(tagList, userTagList);
                   //获取堆顶元素的编辑距离
                   Long biggestDistance = priorityQueue.peek().getValue();
                   if (distance < biggestDistance) {
                       //删除堆顶元素（删除距离最大的元素）
                       priorityQueue.poll();
                       //添加距离更小的元素
                       priorityQueue.add(new Pair<>(user, distance));
                   }
               }
           },executor);
           futureList.add(future);
       }
        //阻塞，等待所有线程执行完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //六、获取用户的详细信息
        List<Long> userIdList = priorityQueue.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        List<User> finalUserList = this.list(new QueryWrapper<User>().in("id", userIdList))
                .stream()
                //用户数据脱敏
                .map(user -> getSafetyUser(user)).collect(Collectors.toList());

        long end = System.currentTimeMillis();

        System.out.println("匹配时间：" + (end - start) +"ms");
        System.out.println("队列长度：" + priorityQueue.size());
        System.out.println("priorityQueue内存大小：" + RamUsageEstimator.humanSizeOf(priorityQueue));

        for (Pair<User,Long> userpair : priorityQueue){
            System.out.println("用户id：" + userpair.getKey().getId() + "距离：" + userpair.getValue());
        }
        return finalUserList;
    }
}


//查询10个相关的用户
//原始：28s
//匹配时间：20634ms
//pairList内存大小：654.3 MB
//用户id:16，距离：1
//用户id:2，距离：2
//用户id:20，距离：2
//用户id:21，距离：2
//用户id:17，距离：3
//用户id:19，距离：3
//用户id:22，距离：3
//用户id:23，距离：3
//用户id:24，距离：3
//用户id:25，距离：3
//
//改进一：优先队列：18s
//匹配时间：18540ms
//priorityQueue内存大小：25.9 MB
//用户id：24距离：3
//用户id：17距离：3
//用户id：2距离：2
//用户id：22距离：3
//用户id：19距离：3
//用户id：20距离：2
//用户id：21距离：2
//用户id：16距离：1
//用户id：23距离：3
//用户id：25距离：3
//
//改进二：优先队列+分批查询+处理 ：74s
//userList内存大小：16 bytes（从50mb变为16bytes）
//匹配时间：74348ms
//队列长度：10
//priorityQueue内存大小：28.4 MB
//用户id：24距离：3
//用户id：17距离：3
//用户id：2距离：2
//用户id：22距离：3
//用户id：19距离：3
//用户id：20距离：2
//用户id：21距离：2
//用户id：16距离：1
//用户id：23距离：3
//用户id：25距离：3
//
//改进三：优先队列+多线程分批查询+处理：7s
//匹配时间：7667ms
//队列长度：10
//priorityQueue内存大小：28.4 MB
//用户id：24距离：3
//用户id：17距离：3
//用户id：2距离：2
//用户id：22距离：3
//用户id：19距离：3
//用户id：20距离：2
//用户id：21距离：2
//用户id：16距离：1
//用户id：23距离：3
//用户id：25距离：3

