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
@TableName("t_user_integral")
@ApiModel(value = "用户可用积分")
public class UserIntegral implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "可用数量 余额")
    private Double using;

}
