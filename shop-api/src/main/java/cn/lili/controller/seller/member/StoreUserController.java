package cn.lili.controller.seller.member;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 店铺端,管理员接口
 *
 * @author Chopper
 * @since 2020/11/16 10:57
 */
@RestController
@Api(tags = "店铺端,管理员接口")
@RequestMapping("/store/member/user")
public class StoreUserController {
    @Autowired
    private UserService userService;


    @GetMapping(value = "/info")
    @ApiOperation(value = "获取当前登录用户接口")
    public ResultMessage<User> getUserInfo() {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser != null) {
            User user = userService.findByUsername(tokenUser.getUsername());
            user.setPwd(null);
            return ResultUtil.data(user);
        }
        throw new ServiceException(ResultCode.USER_NOT_LOGIN);
    }


}
