package cn.lili.modules.user.serviceimpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.security.token.Token;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.vo.PageVO;

import cn.lili.modules.user.aop.annotation.PointLogPoint;

import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.*;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.mapper.UserMapper;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.user.token.UserTokenGenerate;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 会员接口业务层实现
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 会员token
     */
    @Autowired
    private UserTokenGenerate userTokenGenerate;
    /**
     * 商家token
     */
//    @Autowired
//    private StoreTokenGenerate storeTokenGenerate;
    /**
     * 联合登录
     */
//    @Autowired
//    private ConnectService connectService;
    /**
     * 店铺
     */
//    @Autowired
//    private StoreService storeService;
    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;
    /**
     * RocketMQ
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 缓存
     */
    @Autowired
    private Cache cache;

    @Override
    public User findByUsername(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        return this.baseMapper.selectOne(queryWrapper);
    }


    @Override
    public User getUserInfo() {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser != null) {
            return this.findByUsername(tokenUser.getUsername());
        }
        throw new ServiceException(ResultCode.USER_NOT_LOGIN);
    }


    @Override
    public UserVO getUserInfo(Integer userId) {
        return this.baseMapper.getUserInfo(userId);
    }

    @Override
    public boolean findByMobile(String uuid, String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_PHONE);
        }
        cache.put(CachePrefix.FIND_MOBILE + uuid, mobile, 300L);

        return true;
    }




    /**
     * 传递手机号或者用户名
     *
     * @param userName 手机号或者用户名
     * @return 会员信息
     */
    private User findMember(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName).or().eq("mobile", userName);
        return this.getOne(queryWrapper);
    }




    @Override
    public Token refreshToken(String refreshToken) {
        return userTokenGenerate.refreshToken(refreshToken);
    }





    @Override
    public User editOwn(UserEditDTO userEditDTO) {
        //查询会员信息
        User user = this.findByUsername("hello");
        //传递修改会员信息
        BeanUtil.copyProperties(userEditDTO, user);
        //修改会员
        this.updateById(user);
        return user;
    }




    @Override
    public IPage<UserVO> getMemberPage(UserSearchVO userSearchVO, PageVO page) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        //用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getUsername()), "name", userSearchVO.getUsername());
        //用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getNickName()), "name", userSearchVO.getNickName());
        //按照电话号码查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getMobile()), "phone", userSearchVO.getMobile());
        //按照会员状态查询
//        queryWrapper.eq(CharSequenceUtil.isNotBlank(userSearchVO.getDisabled()), "disabled",
//                userSearchVO.getDisabled().equals(SwitchEnum.OPEN.name()) ? 1 : 0);
        queryWrapper.orderByDesc("create_time");
        return this.baseMapper.pageByMemberVO(PageUtil.initPage(page), queryWrapper);
    }

    @Override
    @PointLogPoint
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMemberPoint(Long point, String type, Long userId, String content) {
        //获取当前会员信息
        User user = this.getById(userId);
        if (user != null) {
            //积分变动后的会员积分
            long currentPoint;
            //会员总获得积分
//            long totalPoint = user.getTotalPoint();
            //如果增加积分
            if (type.equals(PointTypeEnum.INCREASE.name())) {
//                currentPoint = user.getPoint() + point;
                //如果是增加积分 需要增加总获得积分
//                totalPoint = totalPoint + point;
            }
            //否则扣除积分
            else {
//                currentPoint = user.getPoint() - point < 0 ? 0 : user.getPoint() - point;
            }
//            user.setPoint(currentPoint);
//            user.setTotalPoint(totalPoint);
            boolean result = this.updateById(user);
            if (result) {
                //发送会员消息
                UserPointMessage userPointMessage = new UserPointMessage();
                userPointMessage.setPoint(point);
                userPointMessage.setType(type);
                userPointMessage.setUserId(userId);
                String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_POINT_CHANGE.name();
                rocketMQTemplate.asyncSend(destination, userPointMessage, RocketmqSendCallbackBuilder.commonCallback());
                return true;
            }
            return false;

        }
        throw new ServiceException(ResultCode.USER_NOT_EXIST);
    }



    /**
     * 根据手机号获取会员
     *
     * @param mobilePhone 手机号
     * @return 会员
     */
    private Long findMember(String mobilePhone, String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobilePhone)
                .or().eq("username", userName);
        return this.baseMapper.selectCount(queryWrapper);
    }

    /**
     * 获取cookie中的联合登录对象
     *
     * @param uuid uuid
     * @param type 状态
     * @return cookie中的联合登录对象
     */
//    private ConnectAuthUser getConnectAuthUser(String uuid, String type) {
//        Object context = cache.get(ConnectService.cacheKey(type, uuid));
//        if (context != null) {
//            return (ConnectAuthUser) context;
//        }
//        return null;
//    }

    /**
     * 成功登录，则检测cookie中的信息，进行会员绑定
     *
     * @param user  会员
     * @param unionId unionId
     * @param type    状态
     */
//    private void loginBindUser(User user, String unionId, String type) {
//        Connect connect = connectService.queryConnect(
//                ConnectQueryDTO.builder().unionId(unionId).unionType(type).build()
//        );
//        if (connect == null) {
//            connect = new Connect(user.getId(), unionId, type);
//            connectService.save(connect);
//        }
//    }

    /**
     * 成功登录，则检测cookie中的信息，进行会员绑定
     *
     * @param user 会员
     */
//    private void loginBindUser(User user) {
//        //获取cookie存储的信息
//        String uuid = CookieUtil.getCookie(ConnectService.CONNECT_COOKIE, ThreadContextHolder.getHttpRequest());
//        String connectType = CookieUtil.getCookie(ConnectService.CONNECT_TYPE, ThreadContextHolder.getHttpRequest());
//        //如果联合登陆存储了信息
//        if (CharSequenceUtil.isNotEmpty(uuid) && CharSequenceUtil.isNotEmpty(connectType)) {
//            try {
//                //获取信息
//                ConnectAuthUser connectAuthUser = getConnectAuthUser(uuid, connectType);
//                if (connectAuthUser == null) {
//                    return;
//                }
//                Connect connect = connectService.queryConnect(
//                        ConnectQueryDTO.builder().unionId(connectAuthUser.getUuid()).unionType(connectType).build()
//                );
//                if (connect == null) {
//                    connect = new Connect(user.getId(), connectAuthUser.getUuid(), connectType);
//                    connectService.save(connect);
//                }
//            } catch (ServiceException e) {
//                throw e;
//            } catch (Exception e) {
//                log.error("绑定第三方联合登陆失败：", e);
//            } finally {
//                //联合登陆成功与否，都清除掉cookie中的信息
//                CookieUtil.delCookie(ConnectService.CONNECT_COOKIE, ThreadContextHolder.getHttpResponse());
//                CookieUtil.delCookie(ConnectService.CONNECT_TYPE, ThreadContextHolder.getHttpResponse());
//            }
//        }
//
//    }


    /**
     * 检测是否可以绑定第三方联合登陆
     * 返回null原因
     * 包含原因1：redis中已经没有联合登陆信息  2：已绑定其他账号
     *
     * @return 返回对象则代表可以进行绑定第三方会员，返回null则表示联合登陆无法继续
     */
//    private ConnectAuthUser checkConnectUser() {
//        //获取cookie存储的信息
//        String uuid = CookieUtil.getCookie(ConnectService.CONNECT_COOKIE, ThreadContextHolder.getHttpRequest());
//        String connectType = CookieUtil.getCookie(ConnectService.CONNECT_TYPE, ThreadContextHolder.getHttpRequest());
//
//        //如果联合登陆存储了信息
//        if (CharSequenceUtil.isNotEmpty(uuid) && CharSequenceUtil.isNotEmpty(connectType)) {
//            //枚举 联合登陆类型获取
//            ConnectAuthEnum authInterface = ConnectAuthEnum.valueOf(connectType);
//
//            ConnectAuthUser connectAuthUser = getConnectAuthUser(uuid, connectType);
//            if (connectAuthUser == null) {
//                throw new ServiceException(ResultCode.USER_OVERDUE_CONNECT_ERROR);
//            }
//            //检测是否已经绑定过用户
//            Connect connect = connectService.queryConnect(
//                    ConnectQueryDTO.builder().unionType(connectType).unionId(connectAuthUser.getUuid()).build()
//            );
//            //没有关联则返回true，表示可以继续绑定
//            if (connect == null) {
//                connectAuthUser.setConnectEnum(authInterface);
//                return connectAuthUser;
//            } else {
//                throw new ServiceException(ResultCode.USER_CONNECT_BANDING_ERROR);
//            }
//        } else {
//            throw new ServiceException(ResultCode.USER_CONNECT_NOT_EXIST_ERROR);
//        }
//    }

    @Override
    public long getMemberNum(UserSearchVO userSearchVO) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        //用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getUsername()), "username", userSearchVO.getUsername());
        //按照电话号码查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getMobile()), "phone", userSearchVO.getMobile());
        //按照状态查询
//        queryWrapper.eq(CharSequenceUtil.isNotBlank(userSearchVO.getDisabled()), "disabled",
//                userSearchVO.getDisabled().equals(SwitchEnum.OPEN.name()) ? 1 : 0);
        queryWrapper.orderByDesc("create_time");
        return this.count(queryWrapper);
    }

    /**
     * 获取指定会员数据
     *
     * @param columns   指定获取的列
     * @param memberIds 会员ids
     * @return 指定会员数据
     */
    @Override
    public List<Map<String, Object>> listFieldsByMemberIds(String columns, List<String> memberIds) {
        return this.listMaps(new QueryWrapper<User>()
                .select(columns)
                .in(memberIds != null && !memberIds.isEmpty(), "id", memberIds));
    }

    /**
     * 登出
     */
    @Override
    public void logout(UserEnums userEnums) {
        String currentUserToken = UserContext.getCurrentUserToken();
        if (CharSequenceUtil.isNotEmpty(currentUserToken)) {
            cache.remove(CachePrefix.ACCESS_TOKEN.getPrefix(userEnums) + currentUserToken);
        }
    }

    /**
     * 获取所有会员的手机号
     *
     * @return 所有会员的手机号
     */
    @Override
    public List<String> getAllMemberMobile() {
        return this.baseMapper.getAllMemberMobile();
    }



    @Override
    public UserVO getMember(String id) {
        return new UserVO(this.getById(id));
    }

    /**
     * 检测会员
     *
     * @param userName    会员名称
     * @param mobilePhone 手机号
     */
    private void checkMember(String userName, String mobilePhone) {
        //判断手机号是否存在
        if (findMember(userName, mobilePhone) > 0) {
            throw new ServiceException(ResultCode.USER_EXIST);
        }
    }
}