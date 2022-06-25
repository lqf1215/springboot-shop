package cn.lili.consumer.event.impl;

import cn.lili.consumer.event.MemberLoginEvent;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员自身业务
 *
 * @author Chopper
 * @version v1.0
 * 2022-01-11 11:08
 */
@Service
public class MemberExecute implements MemberLoginEvent {
    @Autowired
    private UserService userService;

//    @Override
//    public void memberLogin(User user) {
//        userService.updateMemberLoginTime(user.getId());
//    }
}
