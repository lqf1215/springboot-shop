package cn.lili.modules.goods.entity.vos;

import cn.hutool.core.bean.BeanUtil;
import cn.lili.modules.goods.entity.dos.Carousel;
import cn.lili.modules.goods.entity.dos.Category;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 分类VO
 *
 * @author paulG
 * @since 2020/12/1
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselVO  {

    private static final long serialVersionUID = 3775766246075838410L;
    @ApiModelProperty(value = "轮播id")
    private String id;
    @ApiModelProperty(value = "轮播名称")
    private String name;

    @ApiModelProperty(value = "排序值")
    private Integer sort;


    @ApiModelProperty(value = "轮播图标")
    private String image;

    @ApiModelProperty(value = "是否启用")
    private Boolean enable;


    public CarouselVO(Carousel carousel) {
        BeanUtil.copyProperties(carousel, this);
    }


}
