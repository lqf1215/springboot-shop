package cn.lili.controller.buyer.passport.connect;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.UuidUtils;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.connect.request.AuthRequest;
import cn.lili.modules.connect.util.ConnectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 买家端,web联合登录
 *
 * @author Chopper
 */
@Slf4j
@RestController
@Api(tags = "买家端,web联合登录")
@RequestMapping("/buyer/passport/connect/connect")
public class ConnectBuyerWebController {



    @Autowired
    private ConnectUtil connectUtil;


    @GetMapping("/login/web/{type}")
    @ApiOperation(value = "WEB信任登录授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "登录方式:QQ,微信,微信_PC",
                    allowableValues = "QQ,WECHAT,WECHAT_PC", paramType = "path")
    })
    public ResultMessage<String> webAuthorize(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = connectUtil.getAuthRequest(type);
        String authorizeUrl = authRequest.authorize(UuidUtils.getUUID());
        response.sendRedirect(authorizeUrl);
        return ResultUtil.data(authorizeUrl);
    }

    @ApiOperation(value = "信任登录响应结果获取")
    @GetMapping("/result")
    public ResultMessage<Object> callBackResult(String state) {
        if (state == null) {
            throw new ServiceException(ResultCode.USER_CONNECT_LOGIN_ERROR);
        }
        return connectUtil.getResult(state);
    }


}
