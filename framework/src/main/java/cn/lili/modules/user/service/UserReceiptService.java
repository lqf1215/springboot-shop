package cn.lili.modules.user.service;


import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.UserReceipt;
import cn.lili.modules.user.entity.vo.UserReceiptAddVO;
import cn.lili.modules.user.entity.vo.UserReceiptVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 会员发票业务层
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
public interface UserReceiptService extends IService<UserReceipt> {

    /**
     * 查询会员发票列表
     *
     * @param userReceiptVO 会员发票信息
     * @param pageVO          分页信息
     * @return 会员发票分页
     */
    IPage<UserReceipt> getPage(UserReceiptVO userReceiptVO, PageVO pageVO);

    /**
     * 添加会员发票信息
     *
     * @param userReceiptAddVO 会员发票信息
     * @param userId           会员ID
     * @return 操作状态
     */
    Boolean addMemberReceipt(UserReceiptAddVO userReceiptAddVO, Long userId);

    /**
     * 修改会员发票信息
     *
     * @param userReceiptAddVO 会员发票信息
     * @param userId           会员ID
     * @return 操作状态
     */
    Boolean editMemberReceipt(UserReceiptAddVO userReceiptAddVO, Long userId);

    /**
     * 删除会员发票信息
     *
     * @param id 发票ID
     * @return 操作状态
     */
    Boolean deleteMemberReceipt(String id);

}