package cn.lili.modules.search.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author paulG
 * @since 2020/10/15
 **/
@Data
public class EsGoodsSearchDTO {

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "分类")
    private String categoryId;



    @ApiModelProperty("是否为推荐商品")
    private Boolean recommend;

    @ApiModelProperty(value = "价格", example = "10_30")
    private String price;

    @ApiModelProperty(value = "属性:参数名_参数值@参数名_参数值", example = "屏幕类型_LED@屏幕尺寸_15英寸")
    private String prop;

    @ApiModelProperty(value = "规格项列表")
    private List<String> nameIds;

    @ApiModelProperty(value = "卖家id，搜索店铺商品的时候使用")
    private String storeId;

    @ApiModelProperty(value = "商家分组id，搜索店铺商品的时候使用")
    private String storeCatId;

    @ApiModelProperty(hidden = true)
    private Map<String, List<String>> notShowCol = new HashMap<>();

}