package cn.lili.modules.goods.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

/**
 * 轮播查询条件
 *
 * @author pikachu
 * @since 2020-02-24 19:27:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CarouselSearchParams extends PageVO {

    private static final long serialVersionUID = 2544015852728566887L;



    @ApiModelProperty(value = "轮播id")
    private String id;



    @ApiModelProperty(value = "轮播名称 支持模糊")
    private String name;



    /**
     * @see GoodsStatusEnum
     */
    @ApiModelProperty(value = "是否启用")
    private Integer enable;



    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (CharSequenceUtil.isNotEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (CharSequenceUtil.isNotEmpty(id)) {
            queryWrapper.in("id", Arrays.asList(id.split(",")));
        }


        if (enable != null) {
            queryWrapper.eq("enable", enable);
        }

        queryWrapper.eq("delete_flag", false);
        return queryWrapper;
    }




}
