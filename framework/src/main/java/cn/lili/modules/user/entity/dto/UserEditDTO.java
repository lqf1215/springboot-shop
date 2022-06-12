package cn.lili.modules.user.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 会员信息修改DTO
 *
 * @author Bulbasaur
 * @since 2020/12/11 14:39
 */
@Data
public class UserEditDTO {



    @ApiModelProperty(value = "会员头像")
    private String avatarUrl;
    private  String userNick;

}
