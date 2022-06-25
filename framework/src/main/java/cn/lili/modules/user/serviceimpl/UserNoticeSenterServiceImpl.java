package cn.lili.modules.user.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserNotice;
import cn.lili.modules.user.entity.dos.UserNoticeSenter;
import cn.lili.modules.user.entity.enums.SendTypeEnum;
import cn.lili.modules.user.mapper.UserNoticeSenterMapper;
import cn.lili.modules.user.service.UserNoticeSenterService;
import cn.lili.modules.user.service.UserNoticeService;
import cn.lili.modules.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员消息业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 3:44 下午
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserNoticeSenterServiceImpl extends ServiceImpl<UserNoticeSenterMapper, UserNoticeSenter> implements UserNoticeSenterService {

    /**
     * 会员
     */
    @Autowired
    private UserService userService;
    /**
     * 会员站内信
     */
    @Autowired
    private UserNoticeService userNoticeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean customSave(UserNoticeSenter userNoticeSenter) {

        if (this.saveOrUpdate(userNoticeSenter)) {
            List<UserNotice> userNotices = new ArrayList<>();
            //如果是选中会员发送
            if (userNoticeSenter.getSendType().equals(SendTypeEnum.SELECT.name())) {
                //判定消息是否有效
                if (!CharSequenceUtil.isEmpty(userNoticeSenter.getMemberIds())) {
                    String[] ids = userNoticeSenter.getMemberIds().split(",");
                    UserNotice userNotice;
                    for (String id : ids) {
                        userNotice = new UserNotice();
                        userNotice.setIsRead(false);
                        userNotice.setContent(userNoticeSenter.getContent());
                        userNotice.setUserId(Long.valueOf(id));
                        userNotice.setTitle(userNoticeSenter.getTitle());
                        userNotices.add(userNotice);
                    }
                } else {
                    return true;
                }
            } //否则是全部会员发送
            else {
                List<User> users = userService.list();
                UserNotice userNotice;
                for (User user : users) {
                    userNotice = new UserNotice();
                    userNotice.setIsRead(false);
                    userNotice.setContent(userNoticeSenter.getContent());
                    userNotice.setUserId(user.getId());
                    userNotice.setTitle(userNoticeSenter.getTitle());
                    userNotices.add(userNotice);
                }
            }
            //防止没有会员导致报错
            if (!userNotices.isEmpty()) {
                //批量保存
                if (userNoticeService.saveBatch(userNotices)) {
                    return true;
                } else {
                    throw new ServiceException(ResultCode.NOTICE_SEND_ERROR);
                }
            }
        }
        return true;
    }
}