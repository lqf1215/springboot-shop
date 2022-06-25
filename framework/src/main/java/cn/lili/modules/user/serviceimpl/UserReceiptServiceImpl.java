package cn.lili.modules.user.serviceimpl;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserReceipt;
import cn.lili.modules.user.entity.vo.UserReceiptAddVO;
import cn.lili.modules.user.entity.vo.UserReceiptVO;
import cn.lili.modules.user.mapper.UserReceiptMapper;
import cn.lili.modules.user.service.UserReceiptService;
import cn.lili.modules.user.service.UserService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 会员发票业务层实现
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Service
public class UserReceiptServiceImpl extends ServiceImpl<UserReceiptMapper, UserReceipt> implements UserReceiptService {
    @Autowired
    private UserService userService;

    @Override
    public IPage<UserReceipt> getPage(UserReceiptVO userReceiptVO, PageVO pageVO) {
        return this.page(PageUtil.initPage(pageVO), userReceiptVO.lambdaQueryWrapper());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addMemberReceipt(UserReceiptAddVO userReceiptAddVO, Long userId) {
        //校验发票抬头是否重复
        List<UserReceipt> receipts = this.baseMapper.selectList(new QueryWrapper<UserReceipt>()
                .eq("user_id", userId)
                .eq("receipt_title", userReceiptAddVO.getReceiptTitle())
        );
        if (!receipts.isEmpty()) {
            throw new ServiceException(ResultCode.USER_RECEIPT_REPEAT_ERROR);
        }
        //参数封装
        UserReceipt userReceipt = new UserReceipt();
        BeanUtil.copyProperties(userReceiptAddVO, userReceipt);
        //根据会员信息查询会员
        User user = userService.getById(userId);
        if (user != null) {
            userReceipt.setUserId(userId);
            userReceipt.setUserName(user.getName());
            //设置发票默认
            List<UserReceipt> list = this.baseMapper.selectList(new QueryWrapper<UserReceipt>().eq("user_id", userId));
            //如果当前会员只有一个发票则默认为默认发票，反之需要校验参数默认值，做一些处理
            if (list.isEmpty()) {
                userReceipt.setIsDefault(1);
            } else {
                if (userReceiptAddVO.getIsDefault().equals(1)) {
                    //如果参数传递新添加的发票信息为默认，则需要把其他发票置为非默认
                    this.update(new UpdateWrapper<UserReceipt>().eq("user_id", userId));
                    //设置当前发票信息为默认
                    userReceipt.setIsDefault(userReceiptAddVO.getIsDefault());
                } else {
                    userReceiptAddVO.setIsDefault(0);
                }
            }
            return this.baseMapper.insert(userReceipt) > 0 ? true : false;
        }
        throw new ServiceException(ResultCode.USER_RECEIPT_NOT_EXIST);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editMemberReceipt(UserReceiptAddVO userReceiptAddVO, Long userId) {
        //根据会员id查询发票信息
        UserReceipt userReceiptDb = this.baseMapper.selectById(userReceiptAddVO.getId());
        if (userReceiptDb != null) {
            //检验是否有权限修改
            if (!userReceiptDb.getUserId().equals(userId)) {
                throw new ServiceException(ResultCode.USER_AUTHORITY_ERROR);
            }
            //校验发票抬头是否重复
            List<UserReceipt> receipts = this.baseMapper.selectList(new QueryWrapper<UserReceipt>()
                    .eq("user_id", userId)
                    .eq("receipt_title", userReceiptAddVO.getReceiptTitle())
                    .ne("id", userReceiptAddVO.getId())
            );
            if (!receipts.isEmpty()) {
                throw new ServiceException(ResultCode.USER_RECEIPT_REPEAT_ERROR);
            }
            BeanUtil.copyProperties(userReceiptAddVO, userReceiptDb);
            //对发票默认进行处理  如果参数传递新添加的发票信息为默认，则需要把其他发票置为非默认
            if (userReceiptAddVO.getIsDefault().equals(1)) {
                this.update(new UpdateWrapper<UserReceipt>().eq("user_id", userId));
            }
            return this.baseMapper.updateById(userReceiptDb) > 0 ? true : false;
        }
        throw new ServiceException(ResultCode.USER_RECEIPT_NOT_EXIST);
    }

    @Override
    public Boolean deleteMemberReceipt(String id) {
        //根据会员id查询发票信息
        UserReceipt userReceiptDb = this.baseMapper.selectById(id);
        if (userReceiptDb != null) {
            //如果会员发票信息不为空 则逻辑删除此发票信息
            userReceiptDb.setDeleteFlag(false);
            this.baseMapper.updateById(userReceiptDb);
        }
        return true;
    }
}