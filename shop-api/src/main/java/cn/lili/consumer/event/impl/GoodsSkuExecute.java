package cn.lili.consumer.event.impl;


import cn.lili.consumer.event.GoodsCommentCompleteEvent;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品SKU变化
 *
 * @author Chopper
 * @since 2020-07-03 11:20
 */
@Service
public class GoodsSkuExecute implements GoodsCommentCompleteEvent {

    /**
     * 商品
     */
    @Autowired
    private GoodsSkuService goodsSkuService;


    @Override
    public void goodsComment(UserEvaluation userEvaluation) {
        goodsSkuService.updateGoodsSkuCommentNum(userEvaluation.getSkuId());
    }
}
