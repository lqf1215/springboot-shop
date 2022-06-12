package cn.lili.modules.wallet.service;


import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.wallet.entity.dos.MemberWallet;
import cn.lili.modules.wallet.entity.dos.MemberWithdrawApply;
import cn.lili.modules.wallet.entity.dto.MemberWalletUpdateDTO;
import cn.lili.modules.wallet.entity.vo.MemberWalletVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员预存款业务层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface UserWalletService extends IService<MemberWallet> {



    /**
     * 增加用户预存款余额
     *
     * @param memberWalletUpdateDTO 变动模型
     * @return 返回增加结果    true:成功    false:失败
     */
    Boolean increase(MemberWalletUpdateDTO memberWalletUpdateDTO);


    /**
     * 扣减用户预存款余额
     *
     * @param memberWalletUpdateDTO 变动模型
     * @return 操作状态 true:成功    false:失败
     */
    Boolean reduce(MemberWalletUpdateDTO memberWalletUpdateDTO);

    /**
     * 提现扣减余额到冻结金额
     *
     * @param memberWalletUpdateDTO 变动模型
     * @return 操作状态 true:成功    false:失败
     */
    Boolean reduceWithdrawal(MemberWalletUpdateDTO memberWalletUpdateDTO);

    /**
     * 提现扣减冻结金额
     *
     * @param memberWalletUpdateDTO 变动模型
     * @return 操作状态
     */
    Boolean reduceFrozen(MemberWalletUpdateDTO memberWalletUpdateDTO);



    /**
     * 会员注册添加会员预存款
     *
     * @param userId   会员id
     * @param userName 会员名称
     * @return 操作结果
     */
    MemberWallet save(String userId, String userName);



}