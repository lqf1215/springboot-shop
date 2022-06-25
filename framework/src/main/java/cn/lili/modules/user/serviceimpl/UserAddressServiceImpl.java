package cn.lili.modules.user.serviceimpl;

import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.UserAddress;
import cn.lili.modules.user.mapper.UserAddressMapper;
import cn.lili.modules.user.service.UserAddressService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 收货地址业务层实现
 *
 * @author Chopper
 * @since 2020/11/18 9:44 上午
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public IPage<UserAddress> getAddressByMember(PageVO page, Long userId) {
        return this.page(PageUtil.initPage(page),
                new QueryWrapper<UserAddress>()
                        .eq("user_id", userId));

    }

    @Override
    public UserAddress getMemberAddress(String id) {
        return this.getOne(
                new QueryWrapper<UserAddress>()
                        .eq("user_id", 23)
                        .eq("id", id));
    }

    /**
     * 根据地址ID获取当前会员地址信息
     *
     * @return 当前会员的地址信息
     */
    @Override
    public UserAddress getDefaultMemberAddress() {
        return this.getOne(
                new QueryWrapper<UserAddress>()
                        .eq("user_id", 23)
                        .eq("is_default", true));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress saveMemberAddress(UserAddress userAddress) {
        //判断当前地址是否为默认地址，如果为默认需要将其他的地址修改为非默认
        removeDefaultAddress(userAddress);
        //添加会员地址
        this.save(userAddress);

        return userAddress;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress updateMemberAddress(UserAddress userAddress) {
        UserAddress originalUserAddress = this.getMemberAddress(userAddress.getId());
        if (originalUserAddress != null &&
                originalUserAddress.getUserId()==23L ){

            if (userAddress.getIsDefault() == null) {
                userAddress.setIsDefault(false);
            }
            //判断当前地址是否为默认地址，如果为默认需要将其他的地址修改为非默认
            removeDefaultAddress(userAddress);
            this.saveOrUpdate(userAddress);
        }

        return userAddress;
    }

    @Override
    public boolean removeMemberAddress(String id) {
        return this.remove(new QueryWrapper<UserAddress>()
                .eq("id", id));
    }

    /**
     * 修改会员默认收件地址
     *
     * @param userAddress 收件地址
     */
    private void removeDefaultAddress(UserAddress userAddress) {
        //如果不是默认地址不需要处理
        if (Boolean.TRUE.equals(userAddress.getIsDefault())) {
            //将会员的地址修改为非默认地址
            LambdaUpdateWrapper<UserAddress> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.set(UserAddress::getIsDefault, false);
            lambdaUpdateWrapper.eq(UserAddress::getUserId, userAddress.getUserId());
            this.update(lambdaUpdateWrapper);
        }

    }
}