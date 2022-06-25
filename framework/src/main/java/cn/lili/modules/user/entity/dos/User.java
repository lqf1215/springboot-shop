package cn.lili.modules.user.entity.dos;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Data
@TableName("t_user")
@ApiModel(value = "用户")
@NoArgsConstructor
public class User  implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private long id;


    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @LastModifiedBy
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者", hidden = true)
    private String updateBy;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;

    private String remark;
    private long version;
    private String name;
    private String pwd;
    private long pwdStrength;
    private String tradePwd;
    private String zone;
    private String phone;
    private long phoneAuth;
    private String email;
    private long emailAuth;
    private long status;
    private Date lastLoginTime;
    private long whiteIf;
    private long authType;
    private long robot;
    private String inviteCode;
    private long secretAuth;
    private String secret;
    private String handPwd;
    private long virtual;
    private long agency;
    private String address;
    private String tradeAccount;
    private String extractPwd;
    private long extractSwitch;
    private String avatarUrl;



}
