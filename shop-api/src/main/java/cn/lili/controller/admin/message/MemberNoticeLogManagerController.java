package cn.lili.controller.admin.message;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.UserNoticeLog;
import cn.lili.modules.user.service.UserNoticeLogService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端,会员消息接口
 *
 * @author Chopper
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "管理端,会员消息接口")
@RequestMapping("/manager/message/memberNoticeLog")
public class MemberNoticeLogManagerController {
    @Autowired
    private UserNoticeLogService userNoticeLogService;

    @ApiOperation(value = "通过id获取")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserNoticeLog> get(@PathVariable String id) {
        UserNoticeLog userNoticeLog = userNoticeLogService.getById(id);
        return ResultUtil.data(userNoticeLog);
    }

    @ApiOperation(value = "获取全部数据")
    @GetMapping(value = "/getAll")
    public ResultMessage<List<UserNoticeLog>> getAll() {
        List<UserNoticeLog> list = userNoticeLogService.list();
        return ResultUtil.data(list);
    }

    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/getByPage")
    public ResultMessage<IPage<UserNoticeLog>> getByPage(PageVO page) {
        IPage<UserNoticeLog> data = userNoticeLogService.page(PageUtil.initPage(page));
        return ResultUtil.data(data);
    }

    @ApiOperation(value = "编辑或更新数据")
    @PostMapping(value = "/insertOrUpdate")
    public ResultMessage<UserNoticeLog> saveOrUpdate(UserNoticeLog userNoticeLog) {
        userNoticeLogService.saveOrUpdate(userNoticeLog);
        return ResultUtil.data(userNoticeLog);
    }

    @ApiOperation(value = "批量删除")
    @DeleteMapping(value = "/delByIds/{ids}")
    public ResultMessage<Object> delAllByIds(@PathVariable List ids) {
        userNoticeLogService.removeByIds(ids);
        return ResultUtil.success();
    }
}
