package cn.lili.controller.buyer.user;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.UserPointsHistory;
import cn.lili.modules.user.entity.vo.UserPointsHistoryVO;
import cn.lili.modules.user.service.UserPointsHistoryService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 买家端,会员积分历史接口
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "买家端,会员积分历史接口")
@RequestMapping("/buyer/member/memberPointsHistory")
public class PointsHistoryBuyerController {
    @Autowired
    private UserPointsHistoryService userPointsHistoryService;

    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/getByPage")
    public ResultMessage<IPage<UserPointsHistory>> getByPage(PageVO page) {

        LambdaQueryWrapper<UserPointsHistory> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserPointsHistory::getUserId, UserContext.getCurrentUser().getId());
        queryWrapper.orderByDesc(UserPointsHistory::getCreateTime);
        return ResultUtil.data(userPointsHistoryService.page(PageUtil.initPage(page), queryWrapper));
    }

    @ApiOperation(value = "获取会员积分VO")
    @GetMapping(value = "/getMemberPointsHistoryVO")
    public ResultMessage<UserPointsHistoryVO> getMemberPointsHistoryVO() {
        return ResultUtil.data(userPointsHistoryService.getMemberPointsHistoryVO(UserContext.getCurrentUser().getId()));
    }


}
