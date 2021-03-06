package cn.lili.modules.user.entity.dos;

import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.user.entity.dto.UserEvaluationDTO;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

/**
 * 会员商品评价
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Data
@TableName("li_user_evaluation")
@ApiModel(value = "会员商品评价")
@NoArgsConstructor
public class UserEvaluation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员ID")
    private Long userId;

    @NotNull
    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    @NotNull
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @NotNull
    @ApiModelProperty(value = "商品ID")
    private String goodsId;

    @NotNull
    @ApiModelProperty(value = " SKU ID")
    private String skuId;

    @NotNull
    @ApiModelProperty(value = "会员名称")
    private String userName;

    @NotNull
    @ApiModelProperty(value = "会员头像")
    private String memberProfile;

    @NotNull
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @NotNull
    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @NotNull
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @NotNull
    @ApiModelProperty(value = "好中差评 , GOOD：好评，MODERATE：中评，WORSE：差评", allowableValues = "GOOD,MODERATE,WORSE")
    private String grade;

    @NotNull
    @ApiModelProperty(value = " 评价内容")
    private String content;

    @ApiModelProperty(value = "评价图片")
    private String images;

    @NotNull
    @ApiModelProperty(value = "状态  OPEN 正常 ,CLOSE 关闭 ")
    private String status;

    @ApiModelProperty(value = "评价回复")
    private String reply;

    @ApiModelProperty(value = "评价回复图片")
    private String replyImage;

    @ApiModelProperty(value = "评论是否有图片 true 有 ,false 没有")
    private Boolean haveImage;

    @ApiModelProperty(value = "回复是否有图片 true 有 ,false 没有")
    private Boolean haveReplyImage;

    @ApiModelProperty(value = "回复状态")
    private boolean replyStatus;

    @ApiModelProperty(value = "物流评分")
    private Integer deliveryScore;

    @ApiModelProperty(value = "服务评分")
    private Integer serviceScore;

    @ApiModelProperty(value = "描述评分")
    private Integer descriptionScore;


    public UserEvaluation(UserEvaluationDTO userEvaluationDTO, GoodsSku goodsSku, cn.lili.modules.user.entity.dos.User user, Order order){
        //复制评价信息
        BeanUtils.copyProperties(userEvaluationDTO, this);
        //设置会员
        this.userId= user.getId();
        //会员名称
        this.userName= user.getName();
        //设置会员头像
        this.memberProfile= user.getAvatarUrl();
        //商品名称
        this.goodsName=goodsSku.getGoodsName();
        //商品图片
        this.goodsImage=goodsSku.getThumbnail();
        //设置店铺ID
        this.storeId=order.getStoreId();
        //设置店铺名称
        this.storeName=order.getStoreName();
        //设置订单编号
        this.orderNo=order.getSn();
        //是否包含图片
        this.haveImage=StringUtils.isNotEmpty(userEvaluationDTO.getImages());
        //默认开启评价
        this.status=SwitchEnum.OPEN.name();
    }
}