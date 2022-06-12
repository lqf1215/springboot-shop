package cn.lili.modules.user.serviceimpl;

import cn.lili.modules.user.entity.dos.UserNoticeLog;
import cn.lili.modules.user.mapper.UserNoticeLogMapper;
import cn.lili.modules.user.service.UserNoticeLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会员消息业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 3:44 下午
 */
@Service
public class UserNoticeLogServiceImpl extends ServiceImpl<UserNoticeLogMapper, UserNoticeLog> implements UserNoticeLogService {
}