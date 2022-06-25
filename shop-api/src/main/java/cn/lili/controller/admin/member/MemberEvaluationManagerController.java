package cn.lili.controller.admin.member;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.OperationalJudgment;
import cn.lili.common.vo.PageVO;
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

import javax.validation.constraints.NotNull;

/**
 * 管理端,会员商品评价接口
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "管理端,会员商品评价接口")
@RequestMapping("/manager/member/evaluation")
public class MemberEvaluationManagerController {
    @Autowired
    private UserEvaluationService userEvaluationService;

    @PreventDuplicateSubmissions
    @ApiOperation(value = "通过id获取评论")
    @ApiImplicitParam(name = "id", value = "评价ID", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserEvaluationVO> get(@PathVariable String id) {

        return ResultUtil.data(userEvaluationService.queryById(id));
    }

    @ApiOperation(value = "获取评价分页")
    @GetMapping(value = "/getByPage")
    public ResultMessage<IPage<UserEvaluationListVO>> getByPage(EvaluationQueryParams evaluationQueryParams, PageVO page) {

        return ResultUtil.data(userEvaluationService.queryPage(evaluationQueryParams));
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "修改评价状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "评价ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "status", value = "显示状态,OPEN 正常 ,CLOSE 关闭", required = true, paramType = "query")
    })
    @GetMapping(value = "/updateStatus/{id}")
    public ResultMessage<Object> updateStatus(@PathVariable String id, @NotNull String status) {
        userEvaluationService.updateStatus(id, status);
        return ResultUtil.success();
    }

    @ApiOperation(value = "删除评论")
    @ApiImplicitParam(name = "id", value = "评价ID", required = true, dataType = "String", paramType = "path")
    @PutMapping(value = "/delete/{id}")
    public ResultMessage<IPage<Object>> delete(@PathVariable String id) {
        userEvaluationService.delete(id);
        return ResultUtil.success();
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
