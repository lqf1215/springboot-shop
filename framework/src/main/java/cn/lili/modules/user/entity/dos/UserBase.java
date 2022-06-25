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
@TableName("t_user_base")
@ApiModel(value = "用户基础")
@NoArgsConstructor
public class UserBase implements Serializable {

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
    private long userId;
    private String nick;
    private String img;
    private String style;
    private String profile;
    private long sex;
    private long area;
    private String address;
    private long publishBanned;
    private long commentBanned;
    private long isAgent;
    private long isTrader;
    private long agentLevel;
    private String baseImg;
    private String background;
    private String firstWord;
    private long virtual;
    private long role;
    private long level;
    private long fixLevel;
    private String avatarUrl;



}
