package cn.lili.consumer.event;

import cn.lili.modules.user.entity.dos.UserEvaluation;

/**
 * 订单状态改变事件
 *
 * @author Chopper
 * @since 2020/11/17 7:13 下午
 */
public interface GoodsCommentCompleteEvent {

    /**
     * 商品评价
     * @param userEvaluation 会员评价
     */
    void goodsComment(UserEvaluation userEvaluation);
}
