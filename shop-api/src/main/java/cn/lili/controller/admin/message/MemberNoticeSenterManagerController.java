package cn.lili.controller.admin.message;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.common.vo.SearchVO;
import cn.lili.modules.user.entity.dos.UserNoticeSenter;
import cn.lili.modules.user.service.UserNoticeSenterService;
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
@RequestMapping("/manager/message/memberNoticeSenter")
public class MemberNoticeSenterManagerController {
    @Autowired
    private UserNoticeSenterService userNoticeSenterService;

    @ApiOperation(value = "通过id获取")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserNoticeSenter> get(@PathVariable String id) {
        UserNoticeSenter userNoticeSenter = userNoticeSenterService.getById(id);
        return ResultUtil.data(userNoticeSenter);
    }

    @ApiOperation(value = "获取全部数据")
    @GetMapping(value = "/getAll")
    public ResultMessage<List<UserNoticeSenter>> getAll() {

        List<UserNoticeSenter> list = userNoticeSenterService.list();
        return ResultUtil.data(list);
    }

    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/getByPage")
    public ResultMessage<IPage<UserNoticeSenter>> getByPage(UserNoticeSenter entity,
                                                            SearchVO searchVo,
                                                            PageVO page) {
        IPage<UserNoticeSenter> data = userNoticeSenterService.page(PageUtil.initPage(page), PageUtil.initWrapper(entity, searchVo));
        return ResultUtil.data(data);
    }

    @ApiOperation(value = "编辑或更新数据")
    @PostMapping(value = "/insertOrUpdate")
    public ResultMessage<UserNoticeSenter> saveOrUpdate(UserNoticeSenter userNoticeSenter) {

        userNoticeSenterService.customSave(userNoticeSenter);
        return ResultUtil.data(userNoticeSenter);
    }

    @ApiOperation(value = "批量删除")
    @DeleteMapping(value = "/delByIds/{ids}")
    public ResultMessage<Object> delAllByIds(@PathVariable List ids) {
        userNoticeSenterService.removeByIds(ids);
        return ResultUtil.success();
    }
}
