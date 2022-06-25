package cn.lili.controller.admin.wallet;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.wallet.entity.vo.MemberWalletVO;
import cn.lili.modules.wallet.service.UserWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端,预存款接口
 *
 * @author pikachu
 * @since 2020/11/16 10:07 下午
 */
@RestController
@Api(tags = "管理端,预存款接口")
@RequestMapping("/manager/wallet/wallet")
public class MemberWalletManagerController {
    @Autowired
    private UserWalletService userWalletService;

    @GetMapping
    @ApiOperation(value = "查询会员预存款余额")
    @ApiImplicitParam(name = "userId", value = "会员ID", paramType = "query")
    public ResultMessage<MemberWalletVO> get(@RequestParam("userId") Long userId) {
        return ResultUtil.data(userWalletService.getMemberWallet(userId));
    }


}
