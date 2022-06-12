package cn.lili.modules.user.service;


import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.security.token.Token;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.connect.entity.dto.ConnectAuthUser;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.UserMemberEditDTO;
import cn.lili.modules.user.entity.dto.UserAddDTO;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
import com.alipay.api.domain.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 会员业务层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserService extends IService<User> {


    /**
     * 获取当前登录的用户信息
     *
     * @return 会员信息
     */
    User getUserInfo();


    UserVO getUserInfo(Integer userId);

    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @return 会员信息
     */
    User findByUsername(String username);



    /**
     * 修改会员信息
     *
     * @param userEditDTO 会员修改信息
     * @return 修改后的会员
     */
    User editOwn(UserEditDTO userEditDTO);



    /**
     * 获取会员分页
     *
     * @param userSearchVO 会员搜索VO
     * @param page           分页
     * @return 会员分页
     */
    IPage<UserVO> getMemberPage(UserSearchVO userSearchVO, PageVO page);





    /**
     * 会员积分变动
     *
     * @param point    变动积分
     * @param type     是否增加积分 INCREASE 增加  REDUCE 扣减
     * @param userId 会员id
     * @param content  变动日志
     * @return 操作结果
     */
    Boolean updateMemberPoint(Long point, String type, String userId, String content);



    /**
     * 根据条件查询会员总数
     *
     * @param userSearchVO
     * @return 会员总数
     */
    long getMemberNum(UserSearchVO userSearchVO);

    /**
     * 获取指定会员数据
     *
     * @param columns   指定获取的列
     * @param userIds 会员ids
     * @return 指定会员数据
     */
    List<Map<String, Object>> listFieldsByMemberIds(String columns, List<String> userIds);



    /**
     * 获取所有会员的手机号
     *
     * @return 所有会员的手机号
     */
    List<String> getAllMemberMobile();

    /**
     * 更新会员登录时间为最新时间
     *
     * @param userId 会员id
     * @return 是否更新成功
     */
    boolean updateMemberLoginTime(String userId);

    /**
     * 获取用户VO
     *
     * @param id 会员id
     * @return 用户VO
     */
    UserVO getMember(String id);
}