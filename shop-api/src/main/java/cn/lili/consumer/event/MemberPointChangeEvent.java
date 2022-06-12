package cn.lili.consumer.event;

import cn.lili.modules.user.entity.dto.UserPointMessage;

/**
 * 会员积分改变消息
 *
 * @author Chopper
 * @since 2020/11/17 7:13 下午
 */
public interface MemberPointChangeEvent {

    /**
     * 会员积分改变消息
     *
     * @param userPointMessage 会员积分消息
     */
    void memberPointChange(UserPointMessage userPointMessage);
}
