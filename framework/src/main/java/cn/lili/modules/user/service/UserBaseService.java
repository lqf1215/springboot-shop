package cn.lili.modules.user.service;


import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserBase;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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