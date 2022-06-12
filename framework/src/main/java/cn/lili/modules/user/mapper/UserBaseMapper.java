package cn.lili.modules.user.mapper;


import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserBase;
import cn.lili.modules.user.entity.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员数据处理层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserBaseMapper extends BaseMapper<UserBase> {


}