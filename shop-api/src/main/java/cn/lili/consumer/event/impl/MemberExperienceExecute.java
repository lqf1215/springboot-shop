package cn.lili.consumer.event.impl;


import cn.lili.common.utils.CurrencyUtil;
import cn.lili.consumer.event.GoodsCommentCompleteEvent;
import cn.lili.consumer.event.MemberRegisterEvent;
import cn.lili.consumer.event.OrderStatusChangeEvent;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dto.OrderMessage;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.ExperienceSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 会员经验值
 *
 * @author Bulbasaur
 * @since 2021/5/16 11:16 下午
 */
//@Service
public class MemberExperienceExecute implements MemberRegisterEvent, GoodsCommentCompleteEvent, OrderStatusChangeEvent {

    /**
     * 配置
     */
    @Autowired
    private SettingService settingService;
    /**
     * 会员
     */
    @Autowired
    private UserService userService;
    /**
     * 订单
     */
    @Autowired
    private OrderService orderService;

    /**
     * 会员注册赠送经验值
     *
     * @param user 会员
     */
    @Override
    public void memberRegister(User user) {
        //获取经验值设置
        ExperienceSetting experienceSetting = getExperienceSetting();
        //赠送会员经验值
        userService.updateMemberPoint(Long.valueOf(experienceSetting.getRegister().longValue()), PointTypeEnum.INCREASE.name(), ""+user.getId(), "会员注册，赠送经验值" + experienceSetting.getRegister());
    }

    /**
     * 商品评价赠送经验值
     *
     * @param userEvaluation 会员评价
     */
    @Override
    public void goodsComment(UserEvaluation userEvaluation) {
        //获取经验值设置
        ExperienceSetting experienceSetting = getExperienceSetting();
        //赠送会员经验值
        userService.updateMemberPoint(Long.valueOf(experienceSetting.getComment().longValue()), PointTypeEnum.INCREASE.name(), userEvaluation.getUserId(), "会员评价，赠送经验值" + experienceSetting.getComment());
    }

    /**
     * 完成订单赠送经验值
     *
     * @param orderMessage 订单消息
     */
    @Override
    public void orderChange(OrderMessage orderMessage) {
        if (orderMessage.getNewStatus().equals(OrderStatusEnum.COMPLETED)) {
            //获取经验值设置
            ExperienceSetting experienceSetting = getExperienceSetting();
            //获取订单信息
            Order order = orderService.getBySn(orderMessage.getOrderSn());
            //计算赠送经验值数量
            Double point = CurrencyUtil.mul(experienceSetting.getMoney(), order.getFlowPrice(), 0);
            //赠送会员经验值
            userService.updateMemberPoint(point.longValue(), PointTypeEnum.INCREASE.name(), order.getUserId()+"", "会员下单，赠送经验值" + point + "分");
        }
    }

    /**
     * 获取经验值设置
     *
     * @return 经验值设置
     */
    private ExperienceSetting getExperienceSetting() {
        Setting setting = settingService.get(SettingEnum.EXPERIENCE_SETTING.name());
        return new Gson().fromJson(setting.getSettingValue(), ExperienceSetting.class);
    }
}
