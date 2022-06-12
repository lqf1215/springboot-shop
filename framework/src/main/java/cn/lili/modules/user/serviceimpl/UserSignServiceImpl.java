package cn.lili.modules.user.serviceimpl;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.CurrencyUtil;
import cn.lili.common.utils.DateUtil;
import cn.lili.modules.user.entity.dos.UserSign;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.mapper.UserSignMapper;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.user.service.UserSignService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.PointSetting;
import cn.lili.modules.system.entity.dto.PointSettingItem;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员签到业务层实现
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Service
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {

    /**
     * RocketMQ
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;
    /**
     * 配置
     */
    @Autowired
    private SettingService settingService;
    /**
     * 会员
     */
    @Autowired
    private UserService userService;


    @Override
    public Boolean memberSign() {
        //获取当前会员信息
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser != null) {
            //当前签到天数的前一天日期
            List<UserSign> signs = this.baseMapper.getBeforeMemberSign(authUser.getId());
            //构建参数
            UserSign userSign = new UserSign();
            userSign.setUserId(authUser.getId());
            userSign.setUserName(authUser.getUsername());
            //如果size大于0 说明昨天已经签到过，获取昨天的签到数，反之新签到
            if (!signs.isEmpty()) {
                //截止目前为止 签到总天数 不带今天
                Integer signDay = signs.get(0).getSignDay();
                userSign.setSignDay(CurrencyUtil.add(signDay, 1).intValue());
            } else {
                userSign.setSignDay(1);
            }

            userSign.setDay(DateUtil.getDayOfStart().intValue());
            try {
                this.baseMapper.insert(userSign);
                //签到成功后发送消息赠送积分
                String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_SING.name();
                rocketMQTemplate.asyncSend(destination, userSign, RocketmqSendCallbackBuilder.commonCallback());
                return true;
            } catch (Exception e) {
                throw new ServiceException(ResultCode.MEMBER_SIGN_REPEAT);
            }
        }
        throw new ServiceException(ResultCode.USER_NOT_LOGIN);
    }

    @Override
    public List<UserSign> getMonthSignDay(String time) {
        //获取当前会员
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser != null) {
            return this.baseMapper.getMonthMemberSign(authUser.getId(), time);
        }
        throw new ServiceException(ResultCode.USER_NOT_LOGIN);
    }

    @Override
    public void memberSignSendPoint(String userId, Integer day) {
        try {
            //获取签到积分赠送设置
            Setting setting = settingService.get(SettingEnum.POINT_SETTING.name());
            if (setting != null) {
                PointSetting pointSetting = new Gson().fromJson(setting.getSettingValue(), PointSetting.class);
                String content = "";
                //赠送积分
                Long point = null;
                List<PointSettingItem> pointSettingItems = pointSetting.getPointSettingItems();
                if (!pointSettingItems.isEmpty()) {
                    for (PointSettingItem item : pointSettingItems) {
                        if (item.getDay().equals(day)) {
                            point = item.getPoint().longValue();
                            content = "会员连续签到" + day + "天，赠送积分" + point + "分";
                        }
                    }
                }
                //如果他不处于连续赠送阶段，则只赠送签到积分数
                if (point == null && pointSetting.getSignIn() != null) {
                    point = Long.valueOf(pointSetting.getSignIn().toString());
                    content = "会员签到第" + day + "天，赠送积分" + point + "分";
                }
                //赠送会员积分
                userService.updateMemberPoint(point, PointTypeEnum.INCREASE.name(), userId, content);
            }
        } catch (Exception e) {
            log.error("会员签到错误", e);
        }
    }

}