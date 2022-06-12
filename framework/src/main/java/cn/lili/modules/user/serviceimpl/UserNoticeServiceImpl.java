package cn.lili.modules.user.serviceimpl;

import cn.lili.modules.user.entity.dos.UserNotice;
import cn.lili.modules.user.mapper.UserNoticeMapper;
import cn.lili.modules.user.service.UserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会员站内信业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 3:44 下午
 */
@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements UserNoticeService {

}