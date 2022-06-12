package cn.lili.controller.buyer.user;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.entity.dto.EvaluationQueryParams;
import cn.lili.modules.user.entity.dto.UserEvaluationDTO;
import cn.lili.modules.user.entity.vo.EvaluationNumberVO;
import cn.lili.modules.user.entity.vo.UserEvaluationVO;
import cn.lili.modules.user.service.UserEvaluationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 买家端,会员商品评价接口
 *
 * @author Bulbasaur
 * @since 2020/11/16 10:08 下午
 */
@RestController
@Api(tags = "买家端,会员商品评价接口")
@RequestMapping("/buyer/user/evaluation")
public class UserEvaluationBuyerController {

    /**
     * 会员商品评价
     */
    @Autowired
    private UserEvaluationService userEvaluationService;

    @PreventDuplicateSubmissions
    @ApiOperation(value = "添加会员评价")
    @PostMapping
    public ResultMessage<UserEvaluationDTO> save(@Valid UserEvaluationDTO userEvaluationDTO) {
        return ResultUtil.data(userEvaluationService.addMemberEvaluation(userEvaluationDTO, true));
    }

    @ApiOperation(value = "查看会员评价详情")
    @ApiImplicitParam(name = "id", value = "评价ID", required = true, paramType = "path")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserEvaluationVO> save(@NotNull(message = "评价ID不能为空") @PathVariable("id") String id) {
        return ResultUtil.data(userEvaluationService.queryById(id));

    }

    @ApiOperation(value = "查看当前会员评价列表")
    @GetMapping
    public ResultMessage<IPage<UserEvaluation>> queryMineEvaluation(EvaluationQueryParams evaluationQueryParams) {
        //设置当前登录会员
        evaluationQueryParams.setUserId(UserContext.getCurrentUser().getId());
        return ResultUtil.data(userEvaluationService.managerQuery(evaluationQueryParams));
    }

    @ApiOperation(value = "查看某一个商品的评价列表")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/{goodsId}/goodsEvaluation")
    public ResultMessage<IPage<UserEvaluation>> queryGoodsEvaluation(EvaluationQueryParams evaluationQueryParams,
                                                                     @NotNull @PathVariable("goodsId") String goodsId) {
        //设置查询查询商品
        evaluationQueryParams.setGoodsId(goodsId);
        evaluationQueryParams.setStatus(SwitchEnum.OPEN.name());
        return ResultUtil.data(userEvaluationService.managerQuery(evaluationQueryParams));
    }

    @ApiOperation(value = "查看某一个商品的评价数量")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/{goodsId}/evaluationNumber")
    public ResultMessage<EvaluationNumberVO> queryEvaluationNumber(@NotNull @PathVariable("goodsId") String goodsId) {
        return ResultUtil.data(userEvaluationService.getEvaluationNumber(goodsId));
    }
}
