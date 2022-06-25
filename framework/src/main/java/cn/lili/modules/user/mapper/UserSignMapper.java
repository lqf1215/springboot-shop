package cn.lili.modules.user.mapper;


import cn.lili.modules.user.entity.dos.UserSign;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员签到数据处理层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface UserSignMapper extends BaseMapper<UserSign> {

    /**
     * 获取会员之前签到信息
     *
     * @param userId 会员ID
     * @return 会员签到列表
     */
    @Select("SELECT * FROM li_user_sign WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time) = 1 and user_id = #{userId}")
    List<UserSign> getBeforeMemberSign(Long userId);

    /**
     * 获取会员签到
     *
     * @param queryWrapper 查询条件
     * @return 会员签到列表
     */
    @Select("select * from li_user_sign ${ew.customSqlSegment}")
    List<UserSign> getTodayMemberSign(@Param(Constants.WRAPPER) Wrapper<UserSign> queryWrapper);

    /**
     * 获取当月的会员签到记录
     *
     * @param userId 会员ID
     * @param time     时间
     * @return 会员签到列表
     */
    @Select("SELECT * FROM li_user_sign WHERE DATE_FORMAT(create_time,'%Y%m') = #{time} and user_id = #{userId}")
    List<UserSign> getMonthMemberSign(Long userId, String time);

}