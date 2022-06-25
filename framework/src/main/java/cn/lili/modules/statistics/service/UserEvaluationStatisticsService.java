package cn.lili.modules.statistics.service;

import cn.lili.modules.user.entity.dos.UserEvaluation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员商品评价统计
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserEvaluationStatisticsService extends IService<UserEvaluation> {

    /**
     * 获取今天新增的评价数量
     *
     * @return 今日评价数量
     */
    long todayMemberEvaluation();

    /**
     * 获取等待回复评价数量
     *
     * @return 等待回复评价数量
     */
    long getWaitReplyNum();

}