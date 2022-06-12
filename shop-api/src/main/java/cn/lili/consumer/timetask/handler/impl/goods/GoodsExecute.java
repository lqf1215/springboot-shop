package cn.lili.consumer.timetask.handler.impl.goods;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.lili.consumer.timetask.handler.EveryDayExecute;
import cn.lili.modules.goods.mapper.GoodsMapper;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.mapper.UserEvaluationMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 商品定时器
 *
 * @author Chopper
 * @since 2021/3/18 3:23 下午
 */
@Component
public class GoodsExecute implements EveryDayExecute {
    /**
     * 会员评价
     */
    @Resource
    private UserEvaluationMapper userEvaluationMapper;
    /**
     * 商品
     */
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 查询已上架的商品的评价数量并赋值
     */
    @Override
    public void execute() {

        //查询上次统计到本次的评价数量
        List<Map<String, Object>> list = userEvaluationMapper.memberEvaluationNum(
                new QueryWrapper<UserEvaluation>()
                        .between("create_time", DateUtil.yesterday(), new DateTime()));

        for (Map<String, Object> map : list) {
            goodsMapper.addGoodsCommentNum(Convert.toInt(map.get("num").toString()), map.get("goods_id").toString());
        }

    }
}
