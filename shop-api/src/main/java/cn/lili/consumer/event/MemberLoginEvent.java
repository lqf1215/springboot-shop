package cn.lili.consumer.event;

import cn.lili.modules.user.entity.dos.User;

/**
 * 会员登录消息
 *
 * @author Chopper
 * @since 2020/11/17 7:13 下午
 */
public interface MemberLoginEvent {

    /**
     * 会员登录
     *
     * @param user 会员
     */
    void memberLogin(User user);
}
