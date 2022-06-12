package cn.lili.controller.admin.passport;

import cn.lili.common.aop.annotation.DemoSite;
import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.UserMemberEditDTO;
import cn.lili.modules.user.entity.dto.UserAddDTO;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端,会员接口
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "管理端,会员接口")
@RequestMapping("/manager/passport/member")
public class MemberManagerController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "会员分页列表")
    @GetMapping
    public ResultMessage<IPage<UserVO>> getByPage(UserSearchVO userSearchVO, PageVO page) {
        return ResultUtil.data(userService.getMemberPage(userSearchVO, page));
    }


    @ApiOperation(value = "通过ID获取会员信息")
    @ApiImplicitParam(name = "id", value = "会员ID", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/{id}")
    public ResultMessage<UserVO> get(@PathVariable String id) {

        return ResultUtil.data(userService.getMember(id));
    }




    @ApiOperation(value = "根据条件查询会员总数")
    @GetMapping("/num")
    public ResultMessage<Long> getByPage(UserSearchVO userSearchVO) {
        return ResultUtil.data(userService.getMemberNum(userSearchVO));
    }


}
