package cn.lili.modules.wallet.serviceimpl;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.wallet.entity.dos.UserWithdrawApply;
import cn.lili.modules.wallet.entity.dto.MemberWalletUpdateDTO;
import cn.lili.modules.wallet.entity.dto.MemberWithdrawalMessage;
import cn.lili.modules.wallet.entity.enums.DepositServiceTypeEnum;
import cn.lili.modules.wallet.entity.enums.MemberWithdrawalDestinationEnum;
import cn.lili.modules.wallet.entity.enums.WithdrawStatusEnum;
import cn.lili.modules.wallet.entity.vo.MemberWalletVO;
import cn.lili.modules.wallet.entity.vo.MemberWithdrawApplyQueryVO;
import cn.lili.modules.wallet.mapper.UserWithdrawApplyMapper;
import cn.lili.modules.wallet.service.UserWalletService;
import cn.lili.modules.wallet.service.UserWithdrawApplyService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * 会员提现申请业务层实现
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Service
public class UserWithdrawApplyServiceImpl extends ServiceImpl<UserWithdrawApplyMapper, UserWithdrawApply> implements UserWithdrawApplyService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    /**
     * 会员余额
     */
    @Autowired
    private UserWalletService userWalletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean audit(String applyId, Boolean result, String remark) {
        MemberWithdrawalMessage memberWithdrawalMessage = new MemberWithdrawalMessage();
        //查询申请记录
        UserWithdrawApply userWithdrawApply = this.getById(applyId);
        userWithdrawApply.setInspectRemark(remark);
        userWithdrawApply.setInspectTime(new Date());
        if (userWithdrawApply != null) {
            //获取账户余额
            MemberWalletVO memberWalletVO = userWalletService.getMemberWallet(userWithdrawApply.getUserId());
            //校验金额是否满足提现，因为是从冻结金额扣减，所以校验的是冻结金额
            if (memberWalletVO.getMemberFrozenWallet() < userWithdrawApply.getApplyMoney()) {
                throw new ServiceException(ResultCode.WALLET_WITHDRAWAL_FROZEN_AMOUNT_INSUFFICIENT);
            }
            //如果审核通过 则微信直接提现，反之则记录审核状态
            if (Boolean.TRUE.equals(result)) {
                userWithdrawApply.setApplyStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                //提现，微信提现成功后扣减冻结金额
                Boolean bool = userWalletService.withdrawal(userWithdrawApply);
                if (Boolean.TRUE.equals(bool)) {
                    memberWithdrawalMessage.setStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                    //保存修改审核记录
                    this.updateById(userWithdrawApply);
                    //记录日志
                    userWalletService.reduceFrozen(
                            new MemberWalletUpdateDTO(userWithdrawApply.getApplyMoney(), userWithdrawApply.getUserId(), "审核通过，余额提现", DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()))
                    ;
                } else {
                    //如果提现失败则无法审核
                    throw new ServiceException(ResultCode.WALLET_APPLY_ERROR);
                }
            } else {
                memberWithdrawalMessage.setStatus(WithdrawStatusEnum.FAIL_AUDITING.name());
                //如果审核拒绝 审核备注必填
                if (StringUtils.isEmpty(remark)) {
                    throw new ServiceException(ResultCode.WALLET_REMARK_ERROR);
                }
                userWithdrawApply.setApplyStatus(WithdrawStatusEnum.FAIL_AUDITING.name());
                //保存修改审核记录
                this.updateById(userWithdrawApply);
                //需要从冻结金额扣减到余额
                userWalletService.increaseWithdrawal(new MemberWalletUpdateDTO(userWithdrawApply.getApplyMoney(), userWithdrawApply.getUserId(), "审核拒绝，提现金额解冻到余额", DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()));
            }
            //发送审核消息
            memberWithdrawalMessage.setUserId(userWithdrawApply.getUserId());
            memberWithdrawalMessage.setPrice(userWithdrawApply.getApplyMoney());
            memberWithdrawalMessage.setDestination(MemberWithdrawalDestinationEnum.WECHAT.name());

            String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_WITHDRAWAL.name();
            rocketMQTemplate.asyncSend(destination, memberWithdrawalMessage, RocketmqSendCallbackBuilder.commonCallback());
            return true;
        }
        throw new ServiceException(ResultCode.WALLET_APPLY_ERROR);
    }


    @Override
    public IPage<UserWithdrawApply> getMemberWithdrawPage(PageVO pageVO, MemberWithdrawApplyQueryVO memberWithdrawApplyQueryVO) {
        //构建查询条件
        QueryWrapper<UserWithdrawApply> queryWrapper = new QueryWrapper<>();
        //会员名称
        queryWrapper.like(!StringUtils.isEmpty(memberWithdrawApplyQueryVO.getUserName()), "user_name", memberWithdrawApplyQueryVO.getUserName());
        //充值订单号
        queryWrapper.eq(!StringUtils.isEmpty(memberWithdrawApplyQueryVO.getSn()), "sn", memberWithdrawApplyQueryVO.getSn());
        //会员id
        queryWrapper.eq(!StringUtils.isEmpty(""+memberWithdrawApplyQueryVO.getUserId()), "user_id", memberWithdrawApplyQueryVO.getUserId());
        //已付款的充值订单
        queryWrapper.eq(!StringUtils.isEmpty(memberWithdrawApplyQueryVO.getApplyStatus()), "apply_status", memberWithdrawApplyQueryVO.getApplyStatus());
        //开始时间和结束时间
        if (!StringUtils.isEmpty(memberWithdrawApplyQueryVO.getStartDate()) && !StringUtils.isEmpty(memberWithdrawApplyQueryVO.getEndDate())) {
            Date start = cn.hutool.core.date.DateUtil.parse(memberWithdrawApplyQueryVO.getStartDate());
            Date end = cn.hutool.core.date.DateUtil.parse(memberWithdrawApplyQueryVO.getEndDate());
            queryWrapper.between("create_time", start, end);
        }
        queryWrapper.orderByDesc("create_time");
        //查询返回数据
        return this.baseMapper.selectPage(PageUtil.initPage(pageVO), queryWrapper);
    }
}