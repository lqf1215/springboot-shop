package cn.lili.modules.user.serviceimpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.aop.annotation.PointLogPoint;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.*;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.mapper.UserMapper;
import cn.lili.modules.user.service.UserService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.alipay.api.domain.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 会员接口业务层实现
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {



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


    /**
     * 注册方法抽象
     *
     * @param user
     */
    private void registerHandler(User user) {
        user.setId(Integer.valueOf(SnowFlake.getIdStr()));
        //保存会员
        this.save(user);
        String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_REGISTER.name();
        rocketMQTemplate.asyncSend(destination, user, RocketmqSendCallbackBuilder.commonCallback());
    }

    @Override
    public User editOwn(UserEditDTO userEditDTO) {
        //查询会员信息
        User user = this.findByUsername(Objects.requireNonNull(UserContext.getCurrentUser()).getUsername());
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
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getUsername()), "username", userSearchVO.getUsername());
        //用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getNickName()), "nick_name", userSearchVO.getNickName());
        //按照电话号码查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getMobile()), "mobile", userSearchVO.getMobile());
        //按照会员状态查询
        queryWrapper.eq(CharSequenceUtil.isNotBlank(userSearchVO.getDisabled()), "disabled",
                userSearchVO.getDisabled().equals(SwitchEnum.OPEN.name()) ? 1 : 0);
        queryWrapper.orderByDesc("create_time");
        return this.baseMapper.pageByMemberVO(PageUtil.initPage(page), queryWrapper);
    }

    @Override
    @PointLogPoint
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMemberPoint(Long point, String type, String userId, String content) {
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





    @Override
    public long getMemberNum(UserSearchVO userSearchVO) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        //用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getUsername()), "username", userSearchVO.getUsername());
        //按照电话号码查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(userSearchVO.getMobile()), "mobile", userSearchVO.getMobile());
        //按照状态查询
        queryWrapper.eq(CharSequenceUtil.isNotBlank(userSearchVO.getDisabled()), "disabled",
                userSearchVO.getDisabled().equals(SwitchEnum.OPEN.name()) ? 1 : 0);
        queryWrapper.orderByDesc("create_time");
        return this.count(queryWrapper);
    }

    /**
     * 获取指定会员数据
     *
     * @param columns   指定获取的列
     * @param userIds 会员ids
     * @return 指定会员数据
     */
    @Override
    public List<Map<String, Object>> listFieldsByMemberIds(String columns, List<String> userIds) {
        return this.listMaps(new QueryWrapper<User>()
                .select(columns)
                .in(userIds != null && !userIds.isEmpty(), "id", userIds));
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

    /**
     * 更新会员登录时间为最新时间
     *
     * @param userId 会员id
     * @return 是否更新成功
     */
    @Override
    public boolean updateMemberLoginTime(String userId) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);
        updateWrapper.set(User::getUpdateTime, new Date());
        return this.update(updateWrapper);
    }

    @Override
    public UserVO getMember(String id) {
        return new UserVO(this.getById(id));
    }


}