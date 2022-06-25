package cn.lili.modules.payment.kit.plugin.wallet;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.CurrencyUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.payment.entity.RefundLog;
import cn.lili.modules.payment.entity.enums.CashierEnum;
import cn.lili.modules.payment.entity.enums.PaymentMethodEnum;
import cn.lili.modules.payment.kit.CashierSupport;
import cn.lili.modules.payment.kit.Payment;
import cn.lili.modules.payment.kit.dto.PayParam;
import cn.lili.modules.payment.kit.dto.PaymentSuccessParams;
import cn.lili.modules.payment.kit.params.dto.CashierParam;
import cn.lili.modules.payment.service.PaymentService;
import cn.lili.modules.payment.service.RefundLogService;
import cn.lili.modules.wallet.entity.dto.MemberWalletUpdateDTO;
import cn.lili.modules.wallet.entity.enums.DepositServiceTypeEnum;
import cn.lili.modules.wallet.service.UserWalletService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WalletPlugin
 *
 * @author Chopper
 * @version v1.0
 * 2021-02-20 10:14
 */
@Slf4j
@Component
public class WalletIntegralPlugin implements Payment {

    /**
     * 支付日志
     */
    @Autowired
    private PaymentService paymentService;
    /**
     * 退款日志
     */
    @Autowired
    private RefundLogService refundLogService;
    /**
     * 会员余额
     */
    @Autowired
    private UserWalletService userWalletService;
    /**
     * 收银台
     */
    @Autowired
    private CashierSupport cashierSupport;

    @Autowired
    private RedissonClient redisson;

    @Override
    public ResultMessage<Object> h5pay(HttpServletRequest request, HttpServletResponse response, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> jsApiPay(HttpServletRequest request, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> appPay(HttpServletRequest request, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> nativePay(HttpServletRequest request, PayParam payParam) {
        if (payParam.getOrderType().equals(CashierEnum.RECHARGE.name())) {
            throw new ServiceException(ResultCode.CAN_NOT_RECHARGE_WALLET);
        }
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> mpPay(HttpServletRequest request, PayParam payParam) {

        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public void cancel(RefundLog refundLog) {

        try {
            userWalletService.increase(new MemberWalletUpdateDTO(refundLog.getTotalAmount(),
                    refundLog.getUserId(),
                    "取消[" + refundLog.getOrderSn() + "]订单，退还金额[" + refundLog.getTotalAmount() + "]",
                    DepositServiceTypeEnum.WALLET_REFUND.name()));
            refundLog.setIsRefund(true);
            refundLogService.save(refundLog);
        } catch (Exception e) {
            log.error("订单取消错误", e);
        }
    }

    /**
     * 保存支付日志
     *
     * @param payParam 支付参数
     */
    private void savePaymentLog(PayParam payParam) {

        if (!payParam.getPayPwd().equals("666888")){
            throw new ServiceException(ResultCode.PAY_PASSWORD_ERROR);
        }
        //同一个会员如果在不同的客户端使用预存款支付，会存在同时支付，无法保证预存款的正确性，所以对会员加锁
        RLock lock = redisson.getLock(23 + "");
        lock.lock();
        try {
            //获取支付收银参数
            CashierParam cashierParam = cashierSupport.cashierParam(payParam);
            this.callBack(payParam, cashierParam);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void refund(RefundLog refundLog) {
        try {
            userWalletService.increase(new MemberWalletUpdateDTO(refundLog.getTotalAmount(),
                    refundLog.getUserId(),
                    "售后[" + refundLog.getAfterSaleNo() + "]审批，退还金额[" + refundLog.getTotalAmount() + "]",
                    DepositServiceTypeEnum.WALLET_REFUND.name()));
            refundLog.setIsRefund(true);
            refundLogService.save(refundLog);
        } catch (Exception e) {
            log.error("退款失败", e);
        }
    }

    /**
     * 支付订单
     *
     * @param payParam     支付参数
     * @param cashierParam 收银台参数
     */
    public void callBack(PayParam payParam, CashierParam cashierParam) {

        //支付信息
        try {
//            if (UserContext.getCurrentUser() == null) {
//                throw new ServiceException(ResultCode.USER_NOT_LOGIN);
//            }

            // 先计算 订单总价*0.9 得到要支付的积分
            Double point = CurrencyUtil.mul(cashierParam.getPrice(), 0.9);

            //计算 订单总价-要支付的积分= 得到要支付的余额
            Double payAmount = CurrencyUtil.sub(cashierParam.getPrice(), point);

            boolean result = userWalletService.reduceIntegral(new MemberWalletUpdateDTO(
                            cashierParam.getPrice(),
                           23L,
                            "订单[" + cashierParam.getOrderSns() + "],总订单支付[" + cashierParam.getPrice() + "]余额+积分===支付余额[" + payAmount+ "],积分[" + point + "]",
                            DepositServiceTypeEnum.WALLET_PAY.name()
                    )
            );
            if (result) {
                try {
                    PaymentSuccessParams paymentSuccessParams = new PaymentSuccessParams(
                            PaymentMethodEnum.WALLETINTEGRAL.name(),
                            "",
                            cashierParam.getPrice(),
                            payParam
                    );

                    paymentService.success(paymentSuccessParams);
                    log.info("支付回调通知：余额支付：{},余额+积分===支付余额：{}.积分：{}", payParam,payAmount,point);
                } catch (ServiceException e) {
                    //业务异常，则支付手动回滚
                    userWalletService.increase(new MemberWalletUpdateDTO(
                            cashierParam.getPrice(),
                           23L,
                            "订单[" + cashierParam.getOrderSns() + "]支付异常，余额返还[" + cashierParam.getPrice() + "]余额+积分===余额返还[" + payAmount + "]===积分返还[" + point + "]" ,
                            DepositServiceTypeEnum.WALLET_REFUND.name())
                    );
                    throw e;
                }
            } else {
                throw new ServiceException(ResultCode.WALLET_INSUFFICIENT);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.info("余额支付异常", e);
            throw new ServiceException(ResultCode.WALLET_INSUFFICIENT);
        }

    }

}
