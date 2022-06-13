package cn.lili.controller.seller.settings;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.common.vo.SearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * 店铺端,日志管理接口
 *
 * @author Chopper
 * @since 2020/11/22 14:23
 */
@RestController
@Api(tags = "店铺端,日志管理接口")
@RequestMapping("/store/settings/log")
public class LogStoreController {


}
