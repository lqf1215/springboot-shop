package cn.lili.modules.user.service;


import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.security.token.Token;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dto.UserEditDTO;
import cn.lili.modules.user.entity.vo.UserSearchVO;
import cn.lili.modules.user.entity.vo.UserVO;
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
     * 默认密码
     */
    static String DEFAULT_PASSWORD = "111111";

    /**
     * 获取当前登录的用户信息
     *
     * @return 会员信息
     */
    User getUserInfo();

    UserVO getUserInfo(Integer userId);
    /**
     * 是否可以通过手机获取用户
     *
     * @param uuid   UUID
     * @param mobile 手机号
     * @return 操作状态
     */
    boolean findByMobile(String uuid, String mobile);

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
     * 刷新token
     *
     * @param refreshToken
     * @return Token
     */
    Token refreshToken(String refreshToken);



    /**
     * 会员积分变动
     *
     * @param point    变动积分
     * @param type     是否增加积分 INCREASE 增加  REDUCE 扣减
     * @param userId 会员id
     * @param content  变动日志
     * @return 操作结果
     */
    Boolean updateMemberPoint(Long point, String type, Long userId, String content);


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
     * @param memberIds 会员ids
     * @return 指定会员数据
     */
    List<Map<String, Object>> listFieldsByMemberIds(String columns, List<String> memberIds);

    /**
     * 登出
     *
     * @param userEnums token角色类型
     */
    void logout(UserEnums userEnums);

    /**
     * 获取所有会员的手机号
     *
     * @return 所有会员的手机号
     */
    List<String> getAllMemberMobile();


    /**
     * 获取用户VO
     *
     * @param id 会员id
     * @return 用户VO
     */
    UserVO getMember(String id);
}