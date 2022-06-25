package cn.lili.modules.user.service;


import cn.lili.modules.user.entity.dos.UserBase;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员业务层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserBaseService extends IService<UserBase> {

    /**
     * 修改会员信息
     *
     * @param userEditDTO 会员修改信息
     * @return 修改后的会员
     */
    Boolean editOwn(UserEditDTO userEditDTO);

}