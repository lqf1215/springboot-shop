package cn.lili.consumer.listener;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lili.common.aop.annotation.RetryOperation;
import cn.lili.common.exception.RetryException;
import cn.lili.consumer.event.GoodsCommentCompleteEvent;
import cn.lili.modules.goods.entity.dos.*;
import cn.lili.modules.goods.entity.dto.GoodsCompleteMessage;
import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.*;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.service.FootprintService;
import cn.lili.modules.user.service.GoodsCollectionService;
import cn.lili.modules.promotion.entity.dos.BasePromotions;
import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import cn.lili.modules.promotion.entity.dto.search.PromotionGoodsSearchParams;
import cn.lili.modules.promotion.entity.enums.PromotionsScopeTypeEnum;
import cn.lili.modules.promotion.service.PromotionGoodsService;
import cn.lili.modules.promotion.service.PromotionService;
import cn.lili.rocketmq.tags.GoodsTagsEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品消息
 *
 * @author paulG
 * @since 2020/12/9
 **/
@Component
@Slf4j
@RocketMQMessageListener(topic = "${lili.data.rocketmq.goods-topic}", consumerGroup = "${lili.data.rocketmq.goods-group}")
public class GoodsMessageListener implements RocketMQListener<MessageExt> {


    /**
     * 商品
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * 商品Sku
     */
    @Autowired
    private GoodsSkuService goodsSkuService;

    /**
     * 商品收藏
     */
    @Autowired
    private GoodsCollectionService goodsCollectionService;
    /**
     * 商品评价
     */
    @Autowired
    private List<GoodsCommentCompleteEvent> goodsCommentCompleteEvents;

    /**
     * 分类
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * 店铺商品分类
     */
    @Autowired
    private StoreGoodsLabelService storeGoodsLabelService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionGoodsService promotionGoodsService;

    @Override
    @RetryOperation
    public void onMessage(MessageExt messageExt) {

        switch (GoodsTagsEnum.valueOf(messageExt.getTags())) {
            //查看商品
            case VIEW_GOODS:

                break;
            //生成索引
            case GENERATOR_GOODS_INDEX:
                try {
                    String goodsId = new String(messageExt.getBody());
                    log.info("生成索引: {}", goodsId);
                    Goods goods = this.goodsService.getById(goodsId);
                    updateGoodsIndex(goods);
                } catch (Exception e) {
                    log.error("生成商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            case GENERATOR_STORE_GOODS_INDEX:
                try {
                    String storeId = new String(messageExt.getBody());
                    this.updateGoodsIndex(storeId);
                } catch (Exception e) {
                    log.error("生成店铺商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            case UPDATE_GOODS_INDEX_PROMOTIONS:
                this.updateGoodsIndexPromotions(new String(messageExt.getBody()));
                break;
            case DELETE_GOODS_INDEX_PROMOTIONS:
                JSONObject jsonObject = JSONUtil.parseObj(new String(messageExt.getBody()));
                String promotionKey = jsonObject.getStr("promotionKey");
                if (CharSequenceUtil.isEmpty(promotionKey)) {
                    break;
                }

                break;
            case UPDATE_GOODS_INDEX:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());
                    GoodsSearchParams searchParams = new GoodsSearchParams();
                    searchParams.setId(ArrayUtil.join(JSONUtil.toList(goodsIdsJsonStr, String.class).toArray(), ","));
//                    List<Goods> goodsList = goodsService.queryListByParams(searchParams);
//                    this.updateGoodsIndex(goodsList);
                } catch (Exception e) {
                    log.error("更新商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            case UPDATE_GOODS_INDEX_FIELD:
                try {
                    String updateIndexFieldsJsonStr = new String(messageExt.getBody());
                    JSONObject updateIndexFields = JSONUtil.parseObj(updateIndexFieldsJsonStr);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> queryFields = updateIndexFields.get("queryFields", Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> updateFields = updateIndexFields.get("updateFields", Map.class);
                } catch (Exception e) {
                    log.error("更新商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            case RESET_GOODS_INDEX:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());

                } catch (Exception e) {
                    log.error("重置商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            //审核商品
            case GOODS_AUDIT:
                Goods goods = JSONUtil.toBean(new String(messageExt.getBody()), Goods.class);
                updateGoodsIndex(goods);
                break;
            //删除商品
            case GOODS_DELETE:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());
                    for (String goodsId : JSONUtil.toList(goodsIdsJsonStr, String.class)) {
                        Goods goodsById = this.goodsService.getById(goodsId);
                        if (goodsById != null) {
                            List<String> skuIdsByGoodsId = this.goodsSkuService.getSkuIdsByGoodsId(goodsId);

                        }
                    }

                } catch (Exception e) {
                    log.error("删除商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            //规格删除
            case SKU_DELETE:
                String message = new String(messageExt.getBody());
                List<String> skuIds = JSONUtil.toList(message, String.class);
                goodsCollectionService.deleteSkuCollection(skuIds);
                break;
            case STORE_GOODS_DELETE:
                try {
                    String storeId = new String(messageExt.getBody());
                } catch (RetryException re) {
                    throw re;
                } catch (Exception e) {
                    log.error("删除店铺商品索引事件执行异常，商品信息: " + new String(messageExt.getBody()), e);
                }
                break;
            //商品评价
            case GOODS_COMMENT_COMPLETE:
                UserEvaluation userEvaluation = JSONUtil.toBean(new String(messageExt.getBody()), UserEvaluation.class);
                for (GoodsCommentCompleteEvent goodsCommentCompleteEvent : goodsCommentCompleteEvents) {
                    try {
                        goodsCommentCompleteEvent.goodsComment(userEvaluation);
                    } catch (Exception e) {
                        log.error("评价{},在{}业务中，状态修改事件执行异常",
                                new String(messageExt.getBody()),
                                goodsCommentCompleteEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            //购买商品完成
            case BUY_GOODS_COMPLETE:
                this.goodsBuyComplete(messageExt);
                break;
            default:
                log.error("商品执行异常：{}", new String(messageExt.getBody()));
                break;
        }
    }

    private void updateGoodsIndexPromotions(String promotionsJsonStr) {
        try {
            log.info("更新商品索引促销信息: {}", promotionsJsonStr);
            JSONObject jsonObject = JSONUtil.parseObj(promotionsJsonStr);
            BasePromotions promotions = (BasePromotions) jsonObject.get("promotions",
                    ClassLoaderUtil.loadClass(jsonObject.get("promotionsType").toString()));
            String esPromotionKey = jsonObject.get("esPromotionKey").toString();
            if (PromotionsScopeTypeEnum.PORTION_GOODS.name().equals(promotions.getScopeType())) {
                PromotionGoodsSearchParams searchParams = new PromotionGoodsSearchParams();
                searchParams.setPromotionId(promotions.getId());
                List<PromotionGoods> promotionGoodsList = this.promotionGoodsService.listFindAll(searchParams);
                List<String> skuIds = promotionGoodsList.stream().map(PromotionGoods::getSkuId).collect(Collectors.toList());
            } else if (PromotionsScopeTypeEnum.PORTION_GOODS_CATEGORY.name().equals(promotions.getScopeType())) {
                GoodsSearchParams searchParams = new GoodsSearchParams();
                searchParams.setCategoryPath(promotions.getScopeId());
                List<GoodsSku> goodsSkuByList = this.goodsSkuService.getGoodsSkuByList(searchParams);
                List<String> skuIds = goodsSkuByList.stream().map(GoodsSku::getId).collect(Collectors.toList());
            } else if (PromotionsScopeTypeEnum.ALL.name().equals(promotions.getScopeType())) {
            }
        } catch (Exception e) {
            log.error("生成商品索引促销信息执行异常", e);
        }
    }




    /**
     * 更新商品索引根据店铺id
     *
     * @param storeId 店铺id
     */
    private void updateGoodsIndex(String storeId) {
        //如果商品通过审核&&并且已上架
        GoodsSearchParams searchParams = new GoodsSearchParams();
        searchParams.setStoreId(storeId);
        for (Goods goods : this.goodsService.queryListByParams(searchParams)) {
            this.updateGoodsIndex(goods);
        }

    }

    /**
     * 更新商品索引
     *
     * @param goods 商品消息
     */
    private void updateGoodsIndex(Goods goods) {
        //如果商品通过审核&&并且已上架
        GoodsSearchParams searchParams = new GoodsSearchParams();
        searchParams.setGoodsId(goods.getId());
        List<GoodsSku> goodsSkuList = this.goodsSkuService.getGoodsSkuByList(searchParams);
        log.info("goods：{}", goods);
        log.info("goodsSkuList：{}", goodsSkuList);
        if (goods.getAuthFlag().equals(GoodsAuthEnum.PASS.name())
                && goods.getMarketEnable().equals(GoodsStatusEnum.UPPER.name())
                && Boolean.FALSE.equals(goods.getDeleteFlag())) {
        }
        //如果商品状态值不支持es搜索，那么将商品信息做下架处理
        else {

        }
    }







    /**
     * 商品购买完成
     * 1.更新商品购买数量
     * 2.更新SKU购买数量
     * 3.更新索引购买数量
     *
     * @param messageExt 信息体
     */
    private void goodsBuyComplete(MessageExt messageExt) {
        String goodsCompleteMessageStr = new String(messageExt.getBody());
        List<GoodsCompleteMessage> goodsCompleteMessageList = JSONUtil.toList(JSONUtil.parseArray(goodsCompleteMessageStr), GoodsCompleteMessage.class);
        for (GoodsCompleteMessage goodsCompleteMessage : goodsCompleteMessageList) {
            Goods goods = goodsService.getById(goodsCompleteMessage.getGoodsId());
            if (goods != null) {
                //更新商品购买数量
                if (goods.getBuyCount() == null) {
                    goods.setBuyCount(0);
                }
                int buyCount = goods.getBuyCount() + goodsCompleteMessage.getBuyNum();
                this.goodsService.updateGoodsBuyCount(goodsCompleteMessage.getGoodsId(), buyCount);
            } else {
                log.error("商品Id为[" + goodsCompleteMessage.getGoodsId() + "的商品不存在，更新商品失败！");
            }
            GoodsSku goodsSku = goodsSkuService.getById(goodsCompleteMessage.getSkuId());
            if (goodsSku != null) {
                //更新商品购买数量
                if (goodsSku.getBuyCount() == null) {
                    goodsSku.setBuyCount(0);
                }
                int buyCount = goodsSku.getBuyCount() + goodsCompleteMessage.getBuyNum();
                goodsSku.setBuyCount(buyCount);
                goodsSkuService.update(goodsSku);



            } else {
                log.error("商品SkuId为[" + goodsCompleteMessage.getGoodsId() + "的商品不存在，更新商品失败！");
            }
        }
    }
}
