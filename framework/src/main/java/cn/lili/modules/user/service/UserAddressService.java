package cn.lili.modules.user.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.UserAddress;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 收货地址业务层
 *
 * @author Chopper
 * @since 2020/11/18 9:45 上午
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 根据会员获取会员地址分页列表
     *
     * @param page     分页条件
     * @param userId 会员ID
     * @return 会员地址分页列表
     */
    IPage<UserAddress> getAddressByMember(PageVO page, Long userId);

    /**
     * 根据地址ID获取当前会员地址信息
     *
     * @param id 地址ID
     * @return 当前会员的地址信息
     */
    UserAddress getMemberAddress(String id);

    /**
     * 根据地址ID获取当前会员地址信息
     *
     * @return 当前会员的地址信息
     */
    UserAddress getDefaultMemberAddress();

    /**
     * 添加会员收货地址
     *
     * @param userAddress 收货地址
     * @return 操作状态
     */
    UserAddress saveMemberAddress(UserAddress userAddress);

    /**
     * 修改会员收货地址信息
     *
     * @param userAddress 收货地址
     * @return 操作状态
     */
    UserAddress updateMemberAddress(UserAddress userAddress);

    /**
     * 删除会员收货地址信息
     *
     * @param id 收货地址ID
     * @return 操作状态
     */
    boolean removeMemberAddress(String id);

}