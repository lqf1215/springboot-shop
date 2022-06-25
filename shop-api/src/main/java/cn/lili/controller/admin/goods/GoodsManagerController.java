package cn.lili.controller.admin.goods;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.dto.GoodsOperationDTO;
import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.dto.GoodsSkuStockDTO;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.entity.vos.GoodsSkuVO;
import cn.lili.modules.goods.entity.vos.GoodsVO;
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
 * 管理端,商品管理接口
 *
 * @author pikachu
 * @since 2020-02-23 15:18:56
 */
@RestController
@Api(tags = "管理端,商品管理接口")
@RequestMapping("/manager/goods/goods")
public class GoodsManagerController {
    /**
     * 商品
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * 规格商品
     */
    @Autowired
    private GoodsSkuService goodsSkuService;

    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/list")
    public IPage<Goods> getByPage(GoodsSearchParams goodsSearchParams) {
        return goodsService.queryByParams(goodsSearchParams);
    }

    @ApiOperation(value = "分页获取商品列表")
    @GetMapping(value = "/sku/list")
    public ResultMessage<IPage<GoodsSku>> getSkuByPage(GoodsSearchParams goodsSearchParams) {
        return ResultUtil.data(goodsSkuService.getGoodsSkuByPage(goodsSearchParams));
    }

    @ApiOperation(value = "分页获取待审核商品")
    @GetMapping(value = "/auth/list")
    public IPage<Goods> getAuthPage(GoodsSearchParams goodsSearchParams) {

        goodsSearchParams.setAuthFlag(GoodsAuthEnum.TOBEAUDITED.name());
        return goodsService.queryByParams(goodsSearchParams);
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "管理员下架商品", notes = "管理员下架商品时使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "reason", value = "下架理由", required = true, paramType = "query")
    })
    @PutMapping(value = "/{goodsId}/under")
    public ResultMessage<Object> underGoods(@PathVariable String goodsId, @NotEmpty(message = "下架原因不能为空") @RequestParam String reason) {
        List<String> goodsIds = Arrays.asList(goodsId.split(","));
        if (Boolean.TRUE.equals(goodsService.managerUpdateGoodsMarketAble(goodsIds, GoodsStatusEnum.DOWN, reason))) {
            return ResultUtil.success();
        }
        throw new ServiceException(ResultCode.GOODS_UNDER_ERROR);
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "管理员审核商品", notes = "管理员审核商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品ID", required = true, paramType = "path", allowMultiple = true, dataType = "int"),
            @ApiImplicitParam(name = "authFlag", value = "审核结果", required = true, paramType = "query", dataType = "string")
    })
    @PutMapping(value = "{goodsIds}/auth")
    public ResultMessage<Object> auth(@PathVariable List<String> goodsIds, @RequestParam String authFlag) {
        //校验商品是否存在
        if (goodsService.auditGoods(goodsIds, GoodsAuthEnum.valueOf(authFlag))) {
            return ResultUtil.success();
        }
        throw new ServiceException(ResultCode.GOODS_AUTH_ERROR);
    }


    @PreventDuplicateSubmissions
    @ApiOperation(value = "管理员上架商品", notes = "管理员上架商品时使用")
    @PutMapping(value = "/{goodsId}/up")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, allowMultiple = true)
    })
    public ResultMessage<Object> unpGoods(@PathVariable List<String> goodsId) {
        if (goodsService.updateGoodsMarketAble(goodsId, GoodsStatusEnum.UPPER, "")) {
            return ResultUtil.success();
        }
        throw new ServiceException(ResultCode.GOODS_UPPER_ERROR);
    }


    @ApiOperation(value = "通过id获取商品详情")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<GoodsVO> get(@PathVariable String id) {
        GoodsVO goods = goodsService.getGoodsVO(id);
        return ResultUtil.data(goods);
    }
//    @ApiOperation(value = "分页获取库存告警商品列表")
//    @GetMapping(value = "/list/stock")
//    public ResultMessage<StockWarningVO> getWarningStockByPage(GoodsSearchParams goodsSearchParams) {
//        goodsSearchParams.setMarketEnable(GoodsStatusEnum.UPPER.name());
//        IPage<GoodsSku> goodsSku = goodsSkuService.getGoodsSkuByPage(goodsSearchParams);
//        StockWarningVO stockWarning = new StockWarningVO(stockWarnNum, goodsSku);
//        return ResultUtil.data(stockWarning);
//    }


    @ApiOperation(value = "新增商品")
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResultMessage<GoodsOperationDTO> save(@Valid @RequestBody GoodsOperationDTO goodsOperationDTO) {
        goodsService.addGoods(goodsOperationDTO);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "/update/{goodsId}", consumes = "application/json", produces = "application/json")
    public ResultMessage<GoodsOperationDTO> update(@RequestBody GoodsOperationDTO goodsOperationDTO, @PathVariable String goodsId) {
        goodsService.editGoods(goodsOperationDTO, goodsId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "下架商品", notes = "下架商品时使用")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", allowMultiple = true)
    @PutMapping(value = "/under")
    public ResultMessage<Object> underGoods(@RequestParam List<String> goodsId) {

        goodsService.updateGoodsMarketAble(goodsId, GoodsStatusEnum.DOWN, "商家下架");
        return ResultUtil.success();
    }

    @ApiOperation(value = "上架商品", notes = "上架商品时使用")
    @PutMapping(value = "/up")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", allowMultiple = true)
    public ResultMessage<Object> upGoods(@RequestParam List<String> goodsId) {
        goodsService.updateGoodsMarketAble(goodsId, GoodsStatusEnum.UPPER, "");
        return ResultUtil.success();
    }

    @ApiOperation(value = "删除商品")
    @PutMapping(value = "/delete")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", allowMultiple = true)
    public ResultMessage<Object> deleteGoods(@RequestParam List<String> goodsId) {
        goodsService.deleteGoods(goodsId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "设置商品运费模板")
    @PutMapping(value = "/freight")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "templateId", value = "运费模板ID", required = true, paramType = "query")
    })
    public ResultMessage<Object> freight(@RequestParam List<String> goodsId, @RequestParam String templateId) {
        goodsService.freight(goodsId, templateId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "根据goodsId分页获取商品规格列表")
    @GetMapping(value = "/sku/{goodsId}/list")
    public ResultMessage<List<GoodsSkuVO>> getSkuByList(@PathVariable String goodsId) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        return ResultUtil.data(goodsSkuService.getGoodsSkuVOList(goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().eq(GoodsSku::getGoodsId, goodsId).eq(GoodsSku::getStoreId, storeId))));
    }

    @ApiOperation(value = "修改商品库存")
    @PutMapping(value = "/update/stocks", consumes = "application/json")
    public ResultMessage<Object> updateStocks(@RequestBody List<GoodsSkuStockDTO> updateStockList) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        // 获取商品skuId集合
        List<String> goodsSkuIds = updateStockList.stream().map(GoodsSkuStockDTO::getSkuId).collect(Collectors.toList());
        // 根据skuId集合查询商品信息
        List<GoodsSku> goodsSkuList = goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().in(GoodsSku::getId, goodsSkuIds).eq(GoodsSku::getStoreId, storeId));
        // 过滤不符合当前店铺的商品
        List<String> filterGoodsSkuIds = goodsSkuList.stream().map(GoodsSku::getId).collect(Collectors.toList());
        List<GoodsSkuStockDTO> collect = updateStockList.stream().filter(i -> filterGoodsSkuIds.contains(i.getSkuId())).collect(Collectors.toList());
        goodsSkuService.updateStocks(collect);
        return ResultUtil.success();
    }

}
