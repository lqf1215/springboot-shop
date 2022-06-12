package cn.lili.consumer.event;

import cn.lili.modules.user.entity.dos.User;

/**
 * 会员注册消息
 *
 * @author Chopper
 * @since 2020/11/17 7:13 下午
 */
public interface MemberRegisterEvent {

    /**
     * 会员注册
     *
     * @param user 会员
     */
    void memberRegister(User user);
}
