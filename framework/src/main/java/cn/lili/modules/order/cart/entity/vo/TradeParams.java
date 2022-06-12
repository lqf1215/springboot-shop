package cn.lili.modules.order.cart.entity.vo;

import cn.lili.common.enums.ClientTypeEnum;
import cn.lili.modules.order.cart.entity.dto.StoreRemarkDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易参数
 *
 * @author paulG
 * @since 2021/2/23
 **/
@Data
public class TradeParams implements Serializable {

    private static final long serialVersionUID = -8383072817737513063L;

    @ApiModelProperty(value = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT")
    private String way;





}
