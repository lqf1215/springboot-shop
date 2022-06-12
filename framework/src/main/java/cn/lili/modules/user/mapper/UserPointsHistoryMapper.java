package cn.lili.modules.user.mapper;

import cn.lili.modules.user.entity.dos.UserPointsHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * 会员积分历史数据处理层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserPointsHistoryMapper extends BaseMapper<UserPointsHistory> {

    /**
     * 获取所有用户的积分历史VO
     *
     * @param pointType 积分类型
     * @return
     */
    @Select("SELECT SUM( variable_point ) FROM li_user_points_history WHERE point_type = #{pointType}")
    Long getALLMemberPointsHistoryVO(String pointType);

    /**
     * 获取用户的积分数量
     *
     * @param pointType 积分类型
     * @param userId  会员ID
     * @return 积分数量
     */
    @Select("SELECT SUM( variable_point ) FROM li_user_points_history WHERE point_type = #{pointType} AND user_id=#{userId}")
    Long getMemberPointsHistoryVO(String pointType, String userId);


}