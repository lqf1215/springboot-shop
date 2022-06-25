package cn.lili.modules.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员积分
 *
 * @author Bulbasaur
 * @since 2020/12/14 16:31
 */
@Data
public class UserPointMessage {

    @ApiModelProperty(value = "积分")
    private Long point;

    @ApiModelProperty(value = "是否增加积分")
    private String type;

    @ApiModelProperty(value = "会员id")
    private Long userId;
}
