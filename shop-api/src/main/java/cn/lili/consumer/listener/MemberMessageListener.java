package cn.lili.consumer.listener;

import cn.hutool.json.JSONUtil;
import cn.lili.consumer.event.MemberLoginEvent;
import cn.lili.consumer.event.MemberPointChangeEvent;
import cn.lili.consumer.event.MemberRegisterEvent;
import cn.lili.consumer.event.MemberWithdrawalEvent;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserSign;
import cn.lili.modules.user.entity.dto.UserPointMessage;
import cn.lili.modules.user.service.UserSignService;
import cn.lili.modules.wallet.entity.dto.MemberWithdrawalMessage;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会员消息
 *
 * @author paulG
 * @since 2020/12/9
 **/
@Component
@Slf4j
@RocketMQMessageListener(topic = "${lili.data.rocketmq.member-topic}", consumerGroup = "${lili.data.rocketmq.member-group}")
public class MemberMessageListener implements RocketMQListener<MessageExt> {

    /**
     * 会员签到
     */
    @Autowired
    private UserSignService userSignService;
    /**
     * 会员积分变化
     */
    @Autowired
    private List<MemberPointChangeEvent> memberPointChangeEvents;
    /**
     * 会员提现
     */
    @Autowired
    private List<MemberWithdrawalEvent> memberWithdrawalEvents;
    /**
     * 会员注册
     */
    @Autowired
    private List<MemberRegisterEvent> memberSignEvents;

    /**
     * 会员注册
     */
    @Autowired
    private List<MemberLoginEvent> memberLoginEvents;


    @Override
    public void onMessage(MessageExt messageExt) {
        switch (MemberTagsEnum.valueOf(messageExt.getTags())) {
            //会员注册
            case MEMBER_REGISTER:
                for (MemberRegisterEvent memberRegisterEvent : memberSignEvents) {
                    try {
                        User user = JSONUtil.toBean(new String(messageExt.getBody()), User.class);
                        memberRegisterEvent.memberRegister(user);
                    } catch (Exception e) {
                        log.error("会员{},在{}业务中，状态修改事件执行异常",
                                new String(messageExt.getBody()),
                                memberRegisterEvent.getClass().getName(),
                                e);
                    }
                }
                break;

            case MEMBER_LOGIN:

                for (MemberLoginEvent memberLoginEvent : memberLoginEvents) {
                    try {
                        User user = JSONUtil.toBean(new String(messageExt.getBody()), User.class);
//                        memberLoginEvent.memberLogin(user);
                    } catch (Exception e) {
                        log.error("会员{},在{}业务中，状态修改事件执行异常",
                                new String(messageExt.getBody()),
                                memberLoginEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            //会员签到
            case MEMBER_SING:
                UserSign userSign = JSONUtil.toBean(new String(messageExt.getBody()), UserSign.class);
                userSignService.memberSignSendPoint(userSign.getUserId(), userSign.getSignDay());
                break;
            //会员积分变动
            case MEMBER_POINT_CHANGE:
                for (MemberPointChangeEvent memberPointChangeEvent : memberPointChangeEvents) {
                    try {
                        UserPointMessage userPointMessage = JSONUtil.toBean(new String(messageExt.getBody()), UserPointMessage.class);
                        memberPointChangeEvent.memberPointChange(userPointMessage);
                    } catch (Exception e) {
                        log.error("会员{},在{}业务中，状态修改事件执行异常",
                                new String(messageExt.getBody()),
                                memberPointChangeEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            //会员提现
            case MEMBER_WITHDRAWAL:
                for (MemberWithdrawalEvent memberWithdrawalEvent : memberWithdrawalEvents) {
                    try {
                        MemberWithdrawalMessage memberWithdrawalMessage = JSONUtil.toBean(new String(messageExt.getBody()), MemberWithdrawalMessage.class);
                        memberWithdrawalEvent.memberWithdrawal(memberWithdrawalMessage);
                    } catch (Exception e) {
                        log.error("会员{},在{}业务中，提现事件执行异常",
                                new String(messageExt.getBody()),
                                memberWithdrawalEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            default:
                break;
        }
    }
}
