package cn.lili.modules.user.serviceimpl;


import cn.lili.modules.user.entity.dos.UserBase;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.mapper.UserBaseMapper;
import cn.lili.modules.user.service.UserBaseService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 会员接口业务层实现
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Service
public class UserBaseServiceImpl extends ServiceImpl<UserBaseMapper, UserBase> implements UserBaseService {


    @Override
    public Boolean editOwn(UserEditDTO userEditDTO) {
        LambdaUpdateWrapper<UserBase> updateWrapper = new LambdaUpdateWrapper<>();
        if (userEditDTO.getUserNick() != null || userEditDTO.getAvatarUrl() != null) {
            updateWrapper.eq(UserBase::getUserId, 23);
            if (userEditDTO.getUserNick() != null && !Objects.equals(userEditDTO.getUserNick(), "")) {
                updateWrapper.set(UserBase::getNick, userEditDTO.getUserNick());
            }
            if (userEditDTO.getAvatarUrl() != null && !Objects.equals(userEditDTO.getAvatarUrl(), "")) {
                updateWrapper.set(UserBase::getAvatarUrl, userEditDTO.getAvatarUrl());
            }
            return this.update(updateWrapper);
        }
        return false;

    }


}