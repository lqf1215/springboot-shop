package cn.lili.controller.buyer.user;

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
 * 买家端,会员地址接口
 *
 * @author Bulbasaur
 * @since 2020/11/16 10:07 下午
 */
@RestController
@Api(tags = "买家端,会员地址接口")
@RequestMapping("/buyer/user/address")
public class UserAddressBuyerController {

    /**
     * 会员收件地址
     */
    @Autowired
    private UserAddressService userAddressService;

    @ApiOperation(value = "获取会员收件地址分页列表")
    @GetMapping
    public ResultMessage<IPage<UserAddress>> page(PageVO page) {
        return ResultUtil.data(userAddressService.getAddressByMember(page,23L));
    }

    @ApiOperation(value = "根据ID获取会员收件地址")
    @ApiImplicitParam(name = "id", value = "会员地址ID", dataType = "String", paramType = "path")
    @GetMapping(value = "/get/{id}")
    public ResultMessage<UserAddress> getShippingAddress(@PathVariable String id) {
        return ResultUtil.data(userAddressService.getMemberAddress(id));
    }

    @ApiOperation(value = "获取当前会员默认收件地址")
    @GetMapping(value = "/get/default")
    public ResultMessage<UserAddress> getDefaultShippingAddress() {
        return ResultUtil.data(userAddressService.getDefaultMemberAddress());
    }

    @ApiOperation(value = "新增会员收件地址")
    @PostMapping
    public ResultMessage<UserAddress> addShippingAddress(@Valid @RequestBody UserAddress shippingAddress) {
        //添加会员地址
        shippingAddress.setUserId(23L);
        if(shippingAddress.getIsDefault()==null){
            shippingAddress.setIsDefault(false);
        }
        return ResultUtil.data(userAddressService.saveMemberAddress(shippingAddress));
    }

    @ApiOperation(value = "修改会员收件地址")
    @PutMapping
    public ResultMessage<UserAddress> editShippingAddress(@Valid @RequestBody UserAddress shippingAddress) {
        return ResultUtil.data(userAddressService.updateMemberAddress(shippingAddress));
    }

    @ApiOperation(value = "删除会员收件地址")
    @ApiImplicitParam(name = "id", value = "会员地址ID", dataType = "String", paramType = "path")
    @DeleteMapping(value = "/delById/{id}")
    public ResultMessage<Object> delShippingAddressById(@PathVariable String id) {
        userAddressService.getById(id);
        userAddressService.removeMemberAddress(id);
        return ResultUtil.success();
    }

}
