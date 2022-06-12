package cn.lili.modules.user.mapper;


import cn.lili.modules.user.entity.dos.User;
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
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取所有的会员手机号
     * @return 会员手机号
     */
    @Select("select m.mobile from li_user m")
    List<String> getAllMemberMobile();

    @Select("select * from li_user ${ew.customSqlSegment}")
    IPage<UserVO> pageByMemberVO(IPage<UserVO> page, @Param(Constants.WRAPPER) Wrapper<User> queryWrapper);

    @Select("select u.id,u.name as user_name,u.phone,ub.nick ,ub.sex,ui.integral_mall,ub.avatar_url," +
            "uw.using,u.create_time from t_user u LEFT JOIN t_user_integral ui on ui.user_id=u.id LEFT JOIN t_user_base ub " +
            "on  ub.user_id=u.id LEFT JOIN t_user_wallet uw on uw.user_id=u.id and uw.currency_id=1 where u.id=#{userId}")
    UserVO getUserInfo(Integer userId);
}