package cn.lili.modules.wallet.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 会员预存款
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Data
@TableName("t_user_wallet")
@ApiModel(value = "用户余额")
public class UserWallet implements Serializable {

    private static final long serialVersionUID = 1L;


    private long id;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String remark;
    private long version;
    @ApiModelProperty(value = "会员ID")
    private long userId;
    private long currencyId;
    private double using;
    private double freeze;
    private long walletType;
    private double lock;
    private double release;
    private double releaseLock;
    private double lockA;

}
