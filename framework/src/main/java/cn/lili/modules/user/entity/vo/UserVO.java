package cn.lili.modules.user.entity.vo;

import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.common.utils.BeanUtil;
import cn.lili.modules.user.entity.dos.User;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author paulG
 * @since 2021/11/8
 **/
@Data
@NoArgsConstructor
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1810890757303309436L;

    @ApiModelProperty(value = "唯一标识", hidden = true)
    private Long id;

    @ApiModelProperty(value = "会员用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nick;

    @ApiModelProperty(value = "会员性别,0无设置  1为男，2为女")
    private Integer sex;



    @ApiModelProperty(value = "手机号码", required = true)
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;

    @ApiModelProperty(value = "商城积分")
    private Double integralMall;

    @ApiModelProperty(value = "我的余额")
    private Double using;

    @ApiModelProperty(value = "头像")
    private String avatarUrl;
    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;


    public UserVO(User user) {
        BeanUtil.copyProperties(user, this);
    }
}
