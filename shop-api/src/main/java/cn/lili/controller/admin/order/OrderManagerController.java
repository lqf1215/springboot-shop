package cn.lili.controller.admin.order;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.context.ThreadContextHolder;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.user.entity.dto.UserAddressDTO;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dto.OrderExportDTO;
import cn.lili.modules.order.order.entity.dto.OrderSearchParams;
import cn.lili.modules.order.order.entity.vo.OrderDetailVO;
import cn.lili.modules.order.order.entity.vo.OrderSimpleVO;
import cn.lili.modules.order.order.service.OrderPriceService;
import cn.lili.modules.order.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 管理端,订单API
 *
 * @author Chopper
 * @since 2020/11/17 4:34 下午
 */
@RestController
@RequestMapping("/manager/order/order")
@Api(tags = "管理端,订单API")
public class OrderManagerController {

    /**
     * 订单
     */
    @Autowired
    private OrderService orderService;
    /**
     * 订单价格
     */
    @Autowired
    private OrderPriceService orderPriceService;


    @ApiOperation(value = "查询订单列表分页")
    @GetMapping
    public ResultMessage<IPage<OrderSimpleVO>> queryMineOrder(OrderSearchParams orderSearchParams) {
        return ResultUtil.data(orderService.queryByParams(orderSearchParams));
    }

    @ApiOperation(value = "查询订单导出列表")
    @GetMapping("/queryExportOrder")
    public ResultMessage<List<OrderExportDTO>> queryExportOrder(OrderSearchParams orderSearchParams) {
        return ResultUtil.data(orderService.queryExportOrder(orderSearchParams));
    }


    @ApiOperation(value = "订单明细")
    @ApiImplicitParam(name = "orderSn", value = "订单编号", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/{orderSn}")
    public ResultMessage<OrderDetailVO> detail(@PathVariable String orderSn) {
        return ResultUtil.data(orderService.queryDetail(orderSn));
    }


    @PreventDuplicateSubmissions
    @ApiOperation(value = "确认收款")
    @ApiImplicitParam(name = "orderSn", value = "订单编号", required = true, dataType = "String", paramType = "path")
    @PostMapping(value = "/{orderSn}/pay")
    public ResultMessage<Object> payOrder(@PathVariable String orderSn) {
        orderPriceService.adminPayOrder(orderSn);
        return ResultUtil.success();
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "修改收货人信息")
    @ApiImplicitParam(name = "orderSn", value = "订单sn", required = true, dataType = "String", paramType = "path")
    @PostMapping(value = "/update/{orderSn}/consignee")
    public ResultMessage<Order> consignee(@NotNull(message = "参数非法") @PathVariable String orderSn,
                                          @Valid UserAddressDTO userAddressDTO) {
        return ResultUtil.data(orderService.updateConsignee(orderSn, userAddressDTO));
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "修改订单价格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单sn", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "price", value = "订单价格", required = true, dataType = "Double", paramType = "query"),
    })
    @PutMapping(value = "/update/{orderSn}/price")
    public ResultMessage<Order> updateOrderPrice(@PathVariable String orderSn,
                                                 @NotNull(message = "订单价格不能为空") @RequestParam Double price) {
        return ResultUtil.data(orderPriceService.updatePrice(orderSn, price));
    }


    @PreventDuplicateSubmissions
    @ApiOperation(value = "取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单编号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "reason", value = "取消原因", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/{orderSn}/cancel")
    public ResultMessage<Order> cancel(@ApiIgnore @PathVariable String orderSn, @RequestParam String reason) {
        return ResultUtil.data(orderService.cancel(orderSn, reason));
    }


    @ApiOperation(value = "查询物流踪迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单编号", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping(value = "/getTraces/{orderSn}")
    public ResultMessage<Object> getTraces(@NotBlank(message = "订单编号不能为空") @PathVariable String orderSn) {
        return ResultUtil.data(orderService.getTraces(orderSn));
    }


    @ApiOperation(value = "根据核验码获取订单信息")
    @ApiImplicitParam(name = "verificationCode", value = "核验码", required = true, paramType = "path")
    @GetMapping(value = "/getOrderByVerificationCode/{verificationCode}")
    public ResultMessage<Object> getOrderByVerificationCode(@PathVariable String verificationCode) {
        return ResultUtil.data(orderService.getOrderByVerificationCode(verificationCode));
    }

    @PreventDuplicateSubmissions
    @ApiOperation(value = "订单核验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "path"),
            @ApiImplicitParam(name = "verificationCode", value = "核验码", required = true, paramType = "path")
    })
    @PutMapping(value = "/take/{orderSn}/{verificationCode}")
    public ResultMessage<Object> take(@PathVariable String orderSn, @PathVariable String verificationCode) {
        return ResultUtil.data(orderService.take(orderSn, verificationCode));
    }


    @ApiOperation(value = "下载待发货的订单列表", produces = "application/octet-stream")
    @GetMapping(value = "/downLoadDeliverExcel")
    public void downLoadDeliverExcel() {
        HttpServletResponse response = ThreadContextHolder.getHttpResponse();
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();

        //下载订单批量发货Excel
        this.orderService.getBatchDeliverList(response);

    }

    @PostMapping(value = "/batchDeliver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传文件进行订单批量发货")
    public ResultMessage<Object> batchDeliver(@RequestPart("files") MultipartFile files) {
        orderService.batchDeliver(files);
        return ResultUtil.success(ResultCode.SUCCESS);
    }

}