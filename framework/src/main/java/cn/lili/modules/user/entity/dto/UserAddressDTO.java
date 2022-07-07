package cn.lili.modules.user.entity.dto;

import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.common.validation.Phone;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 会员地址DTO
 *
 * @author Bulbasaur
 * @since 2020/12/14 16:31
 */
@Data
public class UserAddressDTO {

    @NotEmpty(message = "收货人姓名不能为空")
    @ApiModelProperty(value = "收货人姓名")
    private String consigneeName;

//    @Phone
    @ApiModelProperty(value = "手机号码")
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String consigneeMobile;

    @NotBlank(message = "地址不能为空")
    @ApiModelProperty(value = "地址名称， '，'分割")
    private String consigneeAddressPath;

    @NotBlank(message = "地址不能为空")
    @ApiModelProperty(value = "地址id，'，'分割 ")
    private String consigneeAddressIdPath;

    @NotEmpty(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址")
    private String consigneeDetail;
}
