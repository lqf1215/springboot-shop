package cn.lili.controller.admin.member;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dos.UserAddress;
import cn.lili.modules.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理端,会员地址API
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@RestController
@Api(tags = "管理端,会员地址API")
@RequestMapping("/manager/member/address")
public class MemberAddressManagerController {
    @Autowired
    private UserAddressService userAddressService;

    @ApiOperation(value = "会员地址分页列表")
    @GetMapping("/{userId}")
    public ResultMessage<IPage<UserAddress>> getByPage(PageVO page, @PathVariable("userId") Long userId) {
        return ResultUtil.data(userAddressService.getAddressByMember(page, userId));
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "删除会员收件地址")
    @ApiImplicitParam(name = "id", value = "会员地址ID", dataType = "String", paramType = "path")
    @DeleteMapping(value = "/delById/{id}")
    public ResultMessage<Object> delShippingAddressById(@PathVariable String id) {
        userAddressService.removeMemberAddress(id);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改会员收件地址")
    @PutMapping
    public ResultMessage<UserAddress> editShippingAddress(@Valid UserAddress shippingAddress) {
        //修改会员地址
        return ResultUtil.data(userAddressService.updateMemberAddress(shippingAddress));
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "新增会员收件地址")
    @PostMapping
    public ResultMessage<UserAddress> addShippingAddress(@Valid UserAddress shippingAddress) {
        //添加会员地址
        return ResultUtil.data(userAddressService.saveMemberAddress(shippingAddress));
    }


}
