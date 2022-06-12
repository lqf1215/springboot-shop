package cn.lili.controller.admin.distribution;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.OperationalJudgment;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.distribution.entity.dos.DistributionGoods;
import cn.lili.modules.distribution.entity.dos.DistributionSelectedGoods;
import cn.lili.modules.distribution.entity.dto.DistributionGoodsSearchParams;
import cn.lili.modules.distribution.entity.vos.DistributionGoodsVO;
import cn.lili.modules.distribution.service.DistributionGoodsService;
import cn.lili.modules.distribution.service.DistributionSelectedGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 管理端,分销商品管理接口
 *
 * @author pikachu
 * @since 2020-03-14 23:04:56
 */
@RestController
@Api(tags = "管理端,分销商品管理接口")
@RequestMapping("/manager/distribution/goods")
public class DistributionGoodsManagerController {

    @Autowired
    private DistributionGoodsService distributionGoodsService;
    /**
     * 已选择分销商品
     */
    @Autowired
    private DistributionSelectedGoodsService distributionSelectedGoodsService;

    @GetMapping(value = "/getByPage")
    @ApiOperation(value = "分页获取")
    public ResultMessage<IPage<DistributionGoodsVO>> getByPage(DistributionGoodsSearchParams distributionGoodsSearchParams) {
        return ResultUtil.data(distributionGoodsService.goodsPage(distributionGoodsSearchParams));
    }


    @DeleteMapping(value = "/delByIds/{ids}")
    @ApiOperation(value = "批量删除")
    public ResultMessage<Object> delAllByIds(@PathVariable List ids) {

        distributionGoodsService.removeByIds(ids);
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取分销商商品列表")
    @GetMapping
    public ResultMessage<IPage<DistributionGoodsVO>> distributionGoods(DistributionGoodsSearchParams distributionGoodsSearchParams) {

        return ResultUtil.data(distributionGoodsService.goodsPage(distributionGoodsSearchParams));
    }

    @ApiOperation(value = "选择商品参与分销")
    @ApiImplicitParam(name = "skuId", value = "规格ID", required = true, dataType = "String", paramType = "path")
    @PutMapping(value = "/checked/{skuId}")
    public ResultMessage<DistributionGoods> distributionCheckGoods(@NotNull(message = "规格ID不能为空") @PathVariable String skuId,
                                                                   @NotNull(message = "佣金金额不能为空") @RequestParam Double commission) {

        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        return ResultUtil.data(distributionGoodsService.checked(skuId, commission, storeId));
    }

    @ApiOperation(value = "取消分销商品")
    @ApiImplicitParam(name = "id", value = "分销商商品ID", required = true, paramType = "path")
    @DeleteMapping(value = "/cancel/{id}")
    public ResultMessage<Object> cancel(@NotNull @PathVariable String id) {
        OperationalJudgment.judgment(distributionGoodsService.getById(id));
        //清除分销商已选择分销商品
        distributionSelectedGoodsService.remove(new QueryWrapper<DistributionSelectedGoods>().eq("distribution_goods_id", id));
        //清除分销商品
        distributionGoodsService.removeById(id);
        return ResultUtil.success();
    }

}
