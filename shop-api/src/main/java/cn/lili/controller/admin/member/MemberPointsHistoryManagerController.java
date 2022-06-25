package cn.lili.controller.admin.member;
 
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.UserPointsHistory;
import cn.lili.modules.user.entity.vo.UserPointsHistoryVO;
import cn.lili.modules.user.service.UserPointsHistoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端,会员积分历史接口
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "管理端,会员积分历史接口")
@RequestMapping("/manager/member/memberPointsHistory")
public class MemberPointsHistoryManagerController {
    @Autowired
    private UserPointsHistoryService userPointsHistoryService;

    @ApiOperation(value = "分页获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "会员ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "会员名称", required = true, paramType = "query")
    })
    @GetMapping(value = "/getByPage")
    public ResultMessage<IPage<UserPointsHistory>> getByPage(PageVO page, Long userId, String userName) {
        return ResultUtil.data(userPointsHistoryService.MemberPointsHistoryList(page, userId, userName));
    }

    @ApiOperation(value = "获取会员积分VO")
    @ApiImplicitParam(name = "userId", value = "会员ID", paramType = "query")
    @GetMapping(value = "/getMemberPointsHistoryVO")
    public ResultMessage<UserPointsHistoryVO> getMemberPointsHistoryVO(Long userId) {
        return ResultUtil.data(userPointsHistoryService.getMemberPointsHistoryVO(userId));
    }


}
