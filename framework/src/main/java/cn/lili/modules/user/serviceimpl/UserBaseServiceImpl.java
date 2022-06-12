package cn.lili.modules.user.serviceimpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.user.aop.annotation.PointLogPoint;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserBase;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.entity.dto.UserPointMessage;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.mapper.UserBaseMapper;
import cn.lili.modules.user.mapper.UserMapper;
import cn.lili.modules.user.service.UserBaseService;
import cn.lili.modules.user.service.UserService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        if (userEditDTO.getUserNick() != null || userEditDTO.getUserNick() != null) {
            updateWrapper.eq(UserBase::getUserId, 23);
            if (userEditDTO.getUserNick() != null && !Objects.equals(userEditDTO.getUserNick(), "")) {
                updateWrapper.set(UserBase::getNick, userEditDTO.getUserNick());
            }
            if (userEditDTO.getUserNick() != null && !Objects.equals(userEditDTO.getUserNick(), "")) {
                updateWrapper.set(UserBase::getAvatarUrl, userEditDTO.getAvatarUrl());
            }
            return this.update(updateWrapper);
        }
        return false;

    }


}