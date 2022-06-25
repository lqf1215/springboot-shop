package cn.lili.modules.user.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.UserPointsHistory;
import cn.lili.modules.user.entity.vo.UserPointsHistoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员积分历史业务层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserPointsHistoryService extends IService<UserPointsHistory> {

    /**
     * 获取会员积分VO
     *
     * @param userId 会员ID
     * @return 会员积分VO
     */
    UserPointsHistoryVO getMemberPointsHistoryVO(Long userId);

    /**
     * 会员积分历史
     *
     * @param page       分页
     * @param userId   会员ID
     * @param userName 会员名称
     * @return 积分历史分页
     */
    IPage<UserPointsHistory> MemberPointsHistoryList(PageVO page, Long userId, String userName);

}