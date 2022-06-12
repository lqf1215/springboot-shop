package cn.lili.controller.buyer.goods;

import cn.lili.common.enums.ResultUtil;

import cn.lili.common.vo.ResultMessage;

import cn.lili.modules.goods.entity.vos.CarouselVO;

import cn.lili.modules.goods.service.CarouselService;

import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 买家端,轮播接口
 *
 * @author Chopper
 * @since 2020/11/16 10:06 下午
 */
@Slf4j
@Api(tags = "买家端,轮播接口")
@RestController
@RequestMapping("/buyer/goods/carousel")
public class CarouselBuyerController {

    /**
     * 商品
     */
    @Autowired
    private CarouselService carouselService;


    @ApiOperation(value = "获取轮播列表")
    @GetMapping
    public ResultMessage<List<CarouselVO>> getByPage() {
        return ResultUtil.data(carouselService.findByAllBySort());
    }


}
