package cn.lili.modules.goods.entity.dos;

import cn.lili.modules.goods.entity.dto.CarouselDTO;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 商品轮播
 *
 * @author pikachu
 * @since 2020-02-18 15:18:56
 */
@Data
@TableName("li_carousel")
@ApiModel(value = "轮播")
@AllArgsConstructor
@NoArgsConstructor
public class Carousel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "轮播名称不能为空")
    @ApiModelProperty(value = "轮播名称")
    private String name;

    @ApiModelProperty(value = "排序值")
    private Integer sort;


    @ApiModelProperty(value = "轮播图标")
    private String image;

    @ApiModelProperty(value = "是否启用")
    private Boolean enable;


   public Carousel(CarouselDTO carouselDTO){
       this.image=carouselDTO.getImage();
       this.name=carouselDTO.getName();
       this.sort=carouselDTO.getSort();
       this.enable=carouselDTO.getEnable();
   }
}