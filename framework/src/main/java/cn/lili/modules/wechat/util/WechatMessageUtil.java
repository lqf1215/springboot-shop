package cn.lili.modules.wechat.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ClientTypeEnum;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.DateUtil;
import cn.lili.common.utils.HttpUtils;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.connect.entity.Connect;
import cn.lili.modules.connect.entity.enums.ConnectEnum;
import cn.lili.modules.user.entity.dto.ConnectQueryDTO;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.service.OrderItemService;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.modules.wechat.entity.dos.WechatMPMessage;
import cn.lili.modules.wechat.entity.dos.WechatMessage;
import cn.lili.modules.wechat.entity.enums.WechatMessageItemEnums;
import cn.lili.modules.wechat.service.WechatMPMessageService;
import cn.lili.modules.wechat.service.WechatMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 微信消息
 *
 * @author Chopper
 * @version v1.0
 * 2020-12-10 19:12
 */
@Slf4j
@Component
public class WechatMessageUtil {


    @Autowired
    private OrderItemService orderItemService;

    /**
     * 构造数据中所需的内容
     *
     * @param order
     * @param wechatMessage
     * @return
     */
    private String createData(Order order, WechatMessage wechatMessage) {
        WechatMessageData wechatMessageData = new WechatMessageData();
        wechatMessageData.setFirst(wechatMessage.getFirst());
        wechatMessageData.setRemark(wechatMessage.getRemark());
        String[] paramArray = wechatMessage.getKeywords().split(",");
        LinkedList params = new LinkedList();

        for (String param : paramArray) {
            WechatMessageItemEnums wechatMessageItemEnums = WechatMessageItemEnums.valueOf(param);
            //初始化参数内容
            String val = getParams(wechatMessageItemEnums, order);
            params.add(val);
        }
        wechatMessageData.setMessageData(params);
        return wechatMessageData.createData();
    }

    /**
     * 构造数据中所需的内容
     *
     * @param order
     * @param wechatMPMessage
     * @return
     */
    private Map<String, Map<String, String>> createData(Order order, WechatMPMessage wechatMPMessage) {
        WechatMessageData wechatMessageData = new WechatMessageData();
        List<String> paramArray = JSONUtil.toList(wechatMPMessage.getKeywords(), String.class);
        List<String> texts = JSONUtil.toList(wechatMPMessage.getKeywordsText(), String.class);
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < paramArray.size(); i++) {
            WechatMessageItemEnums wechatMessageItemEnums = WechatMessageItemEnums.valueOf(paramArray.get(i));
            //初始化参数内容
            String val = getParams(wechatMessageItemEnums, order);
            val = StringUtils.subStringLength(val, 20);
            params.put(texts.get(i), val);
        }
        wechatMessageData.setMpMessageData(params);
        return wechatMessageData.createMPData();
    }

    /**
     * 获取具体参数
     *
     * @param itemEnums
     * @param order
     * @return
     */
    private String getParams(WechatMessageItemEnums itemEnums, Order order) {
        switch (itemEnums) {
            case PRICE:
                return order.getPriceDetailDTO().getFlowPrice().toString();
            case ORDER_SN:
                return order.getSn();
            case SHOP_NAME:
                return order.getStoreName();
            case GOODS_INFO:
                List<OrderItem> orderItems = orderItemService.getByOrderSn(order.getSn());
                StringBuffer stringBuffer = new StringBuffer();
                orderItems.forEach(orderItem -> {
                    stringBuffer.append(orderItem.getGoodsName() + "*" + orderItem.getNum() + "  ");
                });
                return stringBuffer.toString();
            case MEMBER_NAME:
                return order.getUserName();
            case LOGISTICS_NO:
                return order.getLogisticsNo();
            case LOGISTICS_NAME:
                return order.getLogisticsName();
            case LOGISTICS_TIME:
                return DateUtil.toString(order.getLogisticsTime(), DateUtil.STANDARD_FORMAT);
            default:
                return "";
        }
    }

    /**
     * 如果返回信息有错误
     *
     * @param jsonObject 返回消息
     */
    public static void wechatHandler(JSONObject jsonObject) {
        if (jsonObject.containsKey("errmsg")) {
            if (("ok").equals(jsonObject.getStr("errmsg"))) {
                return;
            }
            log.error("微信接口异常，错误码" + jsonObject.get("errcode") + "，" + jsonObject.getStr("errmsg"));
            throw new ServiceException(ResultCode.WECHAT_ERROR);
        }
    }

    /**
     * 如果返回信息有错误
     *
     * @param string 返回消息
     */
    public static String wechatHandler(String string) {
        JSONObject jsonObject = new JSONObject();
        wechatHandler(jsonObject);
        return string;
    }

}
