package cn.lili.modules.user.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 会员站内信
 *
 * @author Chopper
 * @since 2020-02-25 14:10:16
 */
@Data
@TableName("li_user_notice")
@ApiModel(value = "会员站内信")
public class UserNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员id")
    private String userId;

    @ApiModelProperty(value = "是否已读")
    private Boolean isRead;

    @ApiModelProperty(value = "阅读时间")
    private Long receiveTime;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "站内信内容")
    private String content;

}