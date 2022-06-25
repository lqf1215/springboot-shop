package cn.lili.controller.buyer.passport;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.service.UserBaseService;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.verification.service.VerificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 买家端,会员接口
 *
 * @author Chopper
 * @since 2020/11/16 10:07 下午
 */
@RestController
@Api(tags = "买家端,会员接口")
@RequestMapping("/buyer/passport/user")
public class UserBuyerController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserBaseService userBaseService;




    @ApiOperation(value = "获取当前登录用户接口")
    @GetMapping
    public ResultMessage<UserVO> getUserInfo() {

//        return ResultUtil.data(userService.getUserInfo());
        return ResultUtil.data(userService.getUserInfo(23));
    }




//    @ApiOperation(value = "修改用户自己资料")
//    @PutMapping("/editOwn")
//    public ResultMessage<User> editOwn(MemberEditDTO memberEditDTO) {
//
//        return ResultUtil.data(userService.editOwn(memberEditDTO));
//    }


    @ApiOperation(value = "修改用户自己资料")
    @PutMapping("/editOwn")
    public ResultMessage editOwn(UserEditDTO userEditDTO) {

        return ResultUtil.data(userBaseService.editOwn(userEditDTO));
    }



    @ApiOperation(value = "刷新token")
    @GetMapping("/refresh/{refreshToken}")
    public ResultMessage<Object> refreshToken(@NotNull(message = "刷新token不能为空") @PathVariable String refreshToken) {
        return ResultUtil.data(this.userService.refreshToken(refreshToken));
    }

}
