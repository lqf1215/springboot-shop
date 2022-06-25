package cn.lili.consumer.event.impl;


import cn.lili.consumer.event.MemberRegisterEvent;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.wallet.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员钱包创建
 *
 * @author Chopper
 * @since 2020-07-03 11:20
 */
@Service
public class MemberWalletExecute implements MemberRegisterEvent {

    @Autowired
    private UserWalletService userWalletService;

    @Override
    public void memberRegister(User user) {
        // 有些情况下，会同时创建一个user_id的两条数据
//        memberWalletService.save(member.getId(),member.getName());
    }
}
