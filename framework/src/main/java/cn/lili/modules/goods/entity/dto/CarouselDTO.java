package cn.lili.modules.goods.entity.dto;

import cn.lili.common.validation.EnumValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 商品编辑DTO
 *
 * @author pikachu
 * @since 2020-02-24 19:27:20
 */
@Data
@ToString
public class CarouselDTO implements Serializable {

    private static final long serialVersionUID = -509667581371776913L;

    @ApiModelProperty(hidden = true)
    private String id;

    @ApiModelProperty(value = "轮播名称", required = true)
    @NotNull(message = "轮播名称不能为空")
    private String name;


    @ApiModelProperty(value = "顺序")
    @Min(value = 0, message = "顺序不能为负数")
    private Integer sort;

    @ApiModelProperty(value = "是否启用")
    private Boolean enable;

    @ApiModelProperty(value = "轮播图片")
    @NotNull(message = "轮播图片不能为空")
    private String image;

}
