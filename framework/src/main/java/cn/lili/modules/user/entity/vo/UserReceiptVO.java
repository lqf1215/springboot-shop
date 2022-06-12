package cn.lili.modules.user.entity.vo;

import cn.lili.common.utils.StringUtils;
import cn.lili.modules.user.entity.dos.UserReceipt;
import cn.lili.modules.user.entity.enums.UserReceiptEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 会员发票查询VO
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Data
@ApiModel(value = "会员发票")
public class UserReceiptVO {

    private static final long serialVersionUID = -8210927982915677995L;

    @ApiModelProperty(value = "会员ID")
    private String userId;

    @ApiModelProperty(value = "会员名称")
    private String userName;

    /**
     * @see UserReceiptEnum
     */
    @ApiModelProperty(value = "发票类型")
    private String receiptType;

    public LambdaQueryWrapper<UserReceipt> lambdaQueryWrapper() {
        LambdaQueryWrapper<UserReceipt> queryWrapper = new LambdaQueryWrapper<>();

        //会员名称查询
        if (StringUtils.isNotEmpty(userName)) {
            queryWrapper.like(UserReceipt::getUserName, userName);
        }
        //会员id查询
        if (StringUtils.isNotEmpty(userId)) {
            queryWrapper.eq(UserReceipt::getUserId, userId);
        }
        //会员id查询
        if (StringUtils.isNotEmpty(receiptType)) {
            queryWrapper.eq(UserReceipt::getReceiptType, receiptType);
        }
        queryWrapper.eq(UserReceipt::getDeleteFlag, true);
        queryWrapper.orderByDesc(UserReceipt::getCreateTime);
        return queryWrapper;
    }

}
