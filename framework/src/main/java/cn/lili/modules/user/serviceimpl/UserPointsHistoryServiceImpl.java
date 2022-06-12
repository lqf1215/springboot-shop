package cn.lili.modules.user.serviceimpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserPointsHistory;
import cn.lili.modules.user.entity.vo.UserPointsHistoryVO;
import cn.lili.modules.user.mapper.UserPointsHistoryMapper;
import cn.lili.modules.user.service.UserPointsHistoryService;
import cn.lili.modules.user.service.UserService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员积分历史业务层实现
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Service
public class UserPointsHistoryServiceImpl extends ServiceImpl<UserPointsHistoryMapper, UserPointsHistory> implements UserPointsHistoryService {


    @Autowired
    private UserService userService;

    @Override
    public UserPointsHistoryVO getMemberPointsHistoryVO(String userId) {
        //获取会员积分历史
        User user = userService.getById(userId);
        UserPointsHistoryVO userPointsHistoryVO = new UserPointsHistoryVO();
        if (user != null) {
//            userPointsHistoryVO.setPoint(user.getPoint());
//            userPointsHistoryVO.setTotalPoint(user.getTotalPoint());
            return userPointsHistoryVO;
        }
        return new UserPointsHistoryVO();
    }

    @Override
    public IPage<UserPointsHistory> MemberPointsHistoryList(PageVO page, String userId, String userName) {
        LambdaQueryWrapper<UserPointsHistory> lambdaQueryWrapper = new LambdaQueryWrapper<UserPointsHistory>()
                .eq(CharSequenceUtil.isNotEmpty(userId), UserPointsHistory::getUserId, userId)
                .like(CharSequenceUtil.isNotEmpty(userName), UserPointsHistory::getUserName, userName);
        //如果排序为空，则默认创建时间倒序
        if (CharSequenceUtil.isEmpty(page.getSort())) {
            page.setSort("createTime");
            page.setOrder("desc");
        }
        return this.page(PageUtil.initPage(page), lambdaQueryWrapper);
    }

}