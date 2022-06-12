package cn.lili.controller.admin.goods;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.goods.entity.dos.Carousel;
import cn.lili.modules.goods.entity.dos.Category;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.dto.*;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.entity.vos.GoodsSkuVO;
import cn.lili.modules.goods.entity.vos.GoodsVO;
import cn.lili.modules.goods.service.CarouselService;
import cn.lili.modules.goods.service.GoodsService;
import cn.lili.modules.goods.service.GoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端,轮播管理接口
 *
 * @author pikachu
 * @since 2020-02-23 15:18:56
 */
@RestController
@Api(tags = "管理端,轮播管理接口")
@RequestMapping("/manager/goods/carousel")
public class CarouselManagerController {
    /**
     * 轮播
     */
    @Autowired
    private CarouselService carouselService;


    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/list")
    public IPage<Carousel> getByPage(CarouselSearchParams carouselSearchParams) {
        return carouselService.queryByParams(carouselSearchParams);
    }


    @ApiOperation(value = "新增轮播")
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResultMessage<CarouselDTO> save(@Valid @RequestBody CarouselDTO carouselDTO) {
        carouselService.addCarousel(carouselDTO);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改轮播")
    @PutMapping(value = "/update/{id}", consumes = "application/json", produces = "application/json")
    public ResultMessage<CarouselDTO> update(@RequestBody CarouselDTO carouselDTO, @PathVariable String id) {
        carouselService.editCarousel(carouselDTO, id);
        return ResultUtil.success();
    }


    @ApiOperation(value = "删除轮播")
    @DeleteMapping(value = "/delete/{id}")
    @ApiImplicitParam(name = "id", value = "轮播ID", required = true, paramType = "query", allowMultiple = true)
    public ResultMessage<Object> deleteGoods(@RequestParam Integer id) {
        carouselService.removeById(id);
        return ResultUtil.success();
    }

    @PutMapping(value = "/enableOperations/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "轮播ID", required = true, paramType = "path", dataType = "String")
    })
    @ApiOperation(value = "后台 禁用/启用 轮播")
    public ResultMessage<Object> enableOperations(@PathVariable String id, @RequestParam Boolean enableOperations) {

        Carousel carousel = carouselService.getById(id);
        if (carousel == null) {
            throw new ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        }
        carouselService.updateCarouselEnable(id, enableOperations);
        return ResultUtil.success();
    }


}
