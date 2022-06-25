package cn.lili.modules.wallet.serviceimpl;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.CurrencyUtil;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.vo.UserVO;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.WithdrawalSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import cn.lili.modules.wallet.entity.dos.UserWallet;
import cn.lili.modules.wallet.entity.dos.UserWithdrawApply;
import cn.lili.modules.wallet.entity.dos.WalletLog;
import cn.lili.modules.wallet.entity.dto.MemberWalletUpdateDTO;
import cn.lili.modules.wallet.entity.dto.MemberWithdrawalMessage;
import cn.lili.modules.wallet.entity.enums.DepositServiceTypeEnum;
import cn.lili.modules.wallet.entity.enums.MemberWithdrawalDestinationEnum;
import cn.lili.modules.wallet.entity.enums.WithdrawStatusEnum;
import cn.lili.modules.wallet.entity.vo.MemberWalletVO;
import cn.lili.modules.wallet.mapper.UserWalletMapper;
import cn.lili.modules.wallet.service.UserWalletService;
import cn.lili.modules.wallet.service.UserWithdrawApplyService;
import cn.lili.modules.wallet.service.WalletLogService;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * 会员余额业务层实现
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    /**
     * 预存款日志
     */
    @Autowired
    private WalletLogService walletLogService;
    /**
     * 设置
     */
    @Autowired
    private SettingService settingService;
    /**
     * 会员
     */
    @Autowired
    private UserService userService;
    /**
     * 会员提现申请
     */
    @Autowired
    private UserWithdrawApplyService userWithdrawApplyService;

    @Override
    public MemberWalletVO getMemberWallet(Long userId) {
        //构建查询条件
        QueryWrapper<UserWallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        //执行查询
        UserWallet userWallet = this.baseMapper.selectOne(queryWrapper);
        //如果没有钱包，则创建钱包
        if (userWallet == null) {
            userWallet = this.save(userId, userService.getById(userId).getName());
        }
        //返回查询数据
//        return new MemberWalletVO(userWallet.getUsing(), userWallet.getMemberFrozenWallet());
        return new MemberWalletVO(userWallet.getUsing(),0D);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean increaseWithdrawal(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        //检测会员预存款讯息是否存在，如果不存在则新建
        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());

        UserVO userInfo = userService.getUserInfo(23);

        //余额变动
        userWallet.setUsing(CurrencyUtil.add(userWallet.getUsing(), memberWalletUpdateDTO.getMoney()));
//        userWallet.setMemberFrozenWallet(CurrencyUtil.sub(userWallet.getMemberFrozenWallet(), memberWalletUpdateDTO.getMoney()));
        this.updateById(userWallet);
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO);
        walletLogService.save(walletLog);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean increase(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        UserVO userInfo = userService.getUserInfo(23);
        //检测会员预存款讯息是否存在，如果不存在则新建
        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());
        //新增预存款
        userWallet.setUsing(CurrencyUtil.add(userWallet.getUsing(), memberWalletUpdateDTO.getMoney()));
        this.baseMapper.updateById(userWallet);
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO);
        walletLogService.save(walletLog);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reduce(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        //检测会员预存款讯息是否存在，如果不存在则新建
//        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());
        UserVO userInfo = userService.getUserInfo(Integer.valueOf(String.valueOf(memberWalletUpdateDTO.getUserId())));
        //减少预存款，需要校验 如果不够扣减预存款
        if (0 > CurrencyUtil.sub(userInfo.getUsing(), memberWalletUpdateDTO.getMoney())) {
            return false;
        }
        Double using = CurrencyUtil.sub(userInfo.getUsing(), memberWalletUpdateDTO.getMoney());

        //保存记录
        this.baseMapper.updateWalletByUserId(using,memberWalletUpdateDTO.getUserId());
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO, true);
        walletLogService.save(walletLog);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reduceIntegral(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        //检测会员预存款讯息是否存在，如果不存在则新建
//        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());
        UserVO userInfo = userService.getUserInfo(Integer.valueOf(String.valueOf(memberWalletUpdateDTO.getUserId())));
        // 先计算 订单总价*0.9 得到要支付的积分
        Double point = CurrencyUtil.mul(memberWalletUpdateDTO.getMoney(), 0.9);

        //计算 订单总价-要支付的积分= 得到要支付的余额
        Double payAmount = CurrencyUtil.sub(memberWalletUpdateDTO.getMoney(), point);
        //减少余额+积分，需要校验 如果不够扣减预存款+积分则结束
        Double totalUsing = CurrencyUtil.sub(userInfo.getUsing(), payAmount);
        Double totalIntegralMall = CurrencyUtil.sub(userInfo.getIntegralMall(), point);
        if (0 > totalUsing) {
            throw new ServiceException(ResultCode.WALLET_INSUFFICIENT);
//            return false;
        }
        if (0 >totalIntegralMall) {
            throw new ServiceException(ResultCode.USER_POINTS_ERROR);
//            return false;
        }

        //保存记录
        this.baseMapper.updateWalletByUserId(totalUsing,memberWalletUpdateDTO.getUserId());
        this.baseMapper.updateIntegralByUserId(totalIntegralMall,memberWalletUpdateDTO.getUserId());
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO, true,totalIntegralMall);
        walletLogService.save(walletLog);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reduceWithdrawal(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        //检测会员预存款讯息是否存在，如果不存在则新建
        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());
        UserVO userInfo = userService.getUserInfo(23);
        //减少预存款，需要校验 如果不够扣减预存款
        if (0 > CurrencyUtil.sub(userWallet.getUsing(), memberWalletUpdateDTO.getMoney())) {
            throw new ServiceException(ResultCode.WALLET_WITHDRAWAL_INSUFFICIENT);
        }
        userWallet.setUsing(CurrencyUtil.sub(userWallet.getUsing(), memberWalletUpdateDTO.getMoney()));
//        userWallet.setMemberFrozenWallet(CurrencyUtil.add(userWallet.getMemberFrozenWallet(), memberWalletUpdateDTO.getMoney()));
        //修改余额
        this.updateById(userWallet);
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO, true);
        walletLogService.save(walletLog);
        return true;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reduceFrozen(MemberWalletUpdateDTO memberWalletUpdateDTO) {
        //检测会员预存款讯息是否存在，如果不存在则新建

        UserWallet userWallet = this.checkMemberWallet(memberWalletUpdateDTO.getUserId());

        UserVO userInfo = userService.getUserInfo(23);

        //校验此金额是否超过冻结金额
        if (0 > CurrencyUtil.sub(userWallet.getUsing(), memberWalletUpdateDTO.getMoney())) {
            throw new ServiceException(ResultCode.WALLET_WITHDRAWAL_INSUFFICIENT);
        }
//        userWallet.setMemberFrozenWallet(CurrencyUtil.sub(userWallet.getMemberFrozenWallet(), memberWalletUpdateDTO.getMoney()));
        this.updateById(userWallet);
        //新增预存款日志
        WalletLog walletLog = new WalletLog(userInfo.getUsername(), memberWalletUpdateDTO, true);
        walletLogService.save(walletLog);
        return true;
    }

    /**
     * 检测会员预存款是否存在，如果不存在则新建
     *
     * @param userId 会员id
     */
    private UserWallet checkMemberWallet(Long userId) {

        UserWallet userWallet = this.getWalletByUserId(userId);
        //如果会员预存款信息不存在则同步重新建立预存款信息
//        if (userWallet == null) {
//            User user = userService.getById(userId);
//            if (user != null) {
//                userWallet = this.save(userId, user.getName());
//            } else {
//                throw new ServiceException(ResultCode.USER_AUTHORITY_ERROR);
//            }
//        }
        return userWallet;
    }

    @Override
    public void setMemberWalletPassword(User user, String password) {
        //对密码进行加密
        String pwd = new BCryptPasswordEncoder().encode(password);
        //校验会员预存款是否存在
        QueryWrapper<UserWallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        UserWallet userWallet = this.getOne(queryWrapper);
        //如果 预存款信息不为空 执行设置密码
        if (userWallet != null) {
//            userWallet.setWalletPassword(pwd);
            this.updateById(userWallet);
        }
    }


    @Override
    public Boolean checkPassword() {
        //获取当前登录会员
        AuthUser authUser = UserContext.getCurrentUser();
        //构建查询条件
        QueryWrapper<UserWallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", authUser.getId());
        UserWallet wallet = this.getOne(queryWrapper);
        return wallet != null ;
    }

    @Override
    public UserWallet save(Long userId, String userName) {
        //获取会员预存款信息
        UserWallet userWallet = this.getOne(new QueryWrapper<UserWallet>().eq("user_id", userId));
        if (userWallet != null) {
            return userWallet;
        }
        userWallet = new UserWallet();
        userWallet.setUserId(userId);
//        userWallet.setUserName(userName);
//        userWallet.setMemberWallet(0D);
//        userWallet.setMemberFrozenWallet(0D);
        this.save(userWallet);
        return userWallet;
    }

    /**
     * 提现方法
     * 1、先执行平台逻辑，平台逻辑成功后扣减第三方余额，顺序问题为了防止第三方提现成功，平台逻辑失败导致第三方零钱已提现，而我们商城余额未扣减
     * 2、如果余额扣减失败 则抛出异常，事务回滚
     *
     * @param price 提现金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean applyWithdrawal(Double price) {
        MemberWithdrawalMessage memberWithdrawalMessage = new MemberWithdrawalMessage();
        AuthUser authUser = UserContext.getCurrentUser();
        //构建审核参数
        UserWithdrawApply userWithdrawApply = new UserWithdrawApply();
        userWithdrawApply.setUserId(authUser.getId());
        userWithdrawApply.setUserName(authUser.getNickName());
        userWithdrawApply.setApplyMoney(price);
        userWithdrawApply.setApplyStatus(WithdrawStatusEnum.APPLY.name());
        userWithdrawApply.setSn("W" + SnowFlake.getId());
        //校验该次提现是否需要审核,如果未进行配置 默认是需要审核
        Setting setting = settingService.get(SettingEnum.WITHDRAWAL_SETTING.name());
        if (setting != null) {
            //如果不需要审核则审核自动通过
            WithdrawalSetting withdrawalSetting = new Gson().fromJson(setting.getSettingValue(), WithdrawalSetting.class);
            if (Boolean.FALSE.equals(withdrawalSetting.getApply())) {
                userWithdrawApply.setApplyStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                userWithdrawApply.setInspectRemark("系统自动审核通过");
                //校验金额是否满足提现，因为是从余额扣减，所以校验的是余额
                MemberWalletVO memberWalletVO = this.getMemberWallet(userWithdrawApply.getUserId());
                if (memberWalletVO.getMemberWallet() < price) {
                    throw new ServiceException(ResultCode.WALLET_WITHDRAWAL_INSUFFICIENT);
                }
                memberWithdrawalMessage.setStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                //微信零钱提现
                Boolean result = withdrawal(userWithdrawApply);
                if (Boolean.TRUE.equals(result)) {
                    this.reduce(new MemberWalletUpdateDTO(price, authUser.getId(), "余额提现成功", DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()));
                }
            } else {
                memberWithdrawalMessage.setStatus(WithdrawStatusEnum.APPLY.name());
                //扣减余额到冻结金额
                this.reduceWithdrawal(new MemberWalletUpdateDTO(price, authUser.getId(), "提现金额已冻结，审核成功后到账", DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()));
            }
            //发送余额提现申请消息

            memberWithdrawalMessage.setUserId(authUser.getId());
            memberWithdrawalMessage.setPrice(price);
            memberWithdrawalMessage.setDestination(MemberWithdrawalDestinationEnum.WECHAT.name());
            String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_WITHDRAWAL.name();
            rocketMQTemplate.asyncSend(destination, memberWithdrawalMessage, RocketmqSendCallbackBuilder.commonCallback());
        }
        return userWithdrawApplyService.save(userWithdrawApply);
    }

    @Override
    public Boolean withdrawal(UserWithdrawApply userWithdrawApply) {
        userWithdrawApply.setInspectTime(new Date());
        //保存或者修改零钱提现
        this.userWithdrawApplyService.saveOrUpdate(userWithdrawApply);
        //TODO 调用自动提现接口
        boolean result = true;
        //如果微信提现失败 则抛出异常 回滚数据
        if (!result) {
            throw new ServiceException(ResultCode.WALLET_ERROR_INSUFFICIENT);
        }
        return result;
    }

    @Override
    public UserWallet getWalletByUserId(Long userId) {
        UserWallet walletByUserId = baseMapper.getWalletByUserId(userId);
        System.out.println("walletByUse==========="+walletByUserId.toString());
        return walletByUserId;
    }

}