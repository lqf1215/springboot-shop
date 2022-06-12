package cn.lili.controller.seller.member;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.OperationalJudgment;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dto.EvaluationQueryParams;
import cn.lili.modules.user.entity.vo.UserEvaluationListVO;
import cn.lili.modules.user.entity.vo.UserEvaluationVO;
import cn.lili.modules.user.service.UserEvaluationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 店铺端,商品评价管理接口
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "店铺端,商品评价管理接口")
@RequestMapping("/store/member/evaluation")
public class MemberEvaluationStoreController {

    @Autowired
    private UserEvaluationService userEvaluationService;

    @ApiOperation(value = "分页获取会员评论列表")
    @GetMapping
    public ResultMessage<IPage<UserEvaluationListVO>> getByPage(EvaluationQueryParams evaluationQueryParams) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        evaluationQueryParams.setStoreId(storeId);
        return ResultUtil.data(userEvaluationService.queryPage(evaluationQueryParams));
    }

    @ApiOperation(value = "通过id获取")
    @ApiImplicitParam(name = "id", value = "评价ID", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserEvaluationVO> get(@PathVariable String id) {
        return ResultUtil.data(OperationalJudgment.judgment(userEvaluationService.queryById(id)));
    }

    @ApiOperation(value = "回复评价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "评价ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "reply", value = "回复内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "replyImage", value = "回复图片", dataType = "String", paramType = "query")
    })
    @PutMapping(value = "/reply/{id}")
    public ResultMessage<UserEvaluationVO> reply(@PathVariable String id, @RequestParam String reply, @RequestParam String replyImage) {
        OperationalJudgment.judgment(userEvaluationService.queryById(id));
        userEvaluationService.reply(id, reply, replyImage);
        return ResultUtil.success();
    }
}
