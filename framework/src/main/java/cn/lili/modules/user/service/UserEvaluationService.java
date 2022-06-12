package cn.lili.modules.user.service;

import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.entity.dto.EvaluationQueryParams;
import cn.lili.modules.user.entity.dto.UserEvaluationDTO;
import cn.lili.modules.user.entity.vo.EvaluationNumberVO;
import cn.lili.modules.user.entity.vo.UserEvaluationListVO;
import cn.lili.modules.user.entity.vo.UserEvaluationVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员商品评价业务层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface UserEvaluationService extends IService<UserEvaluation> {

    /**
     * 查询会员的评价分页列表
     *
     * @param evaluationQueryParams 评价查询
     * @return 评价分页
     */
    IPage<UserEvaluation> managerQuery(EvaluationQueryParams evaluationQueryParams);

    /**
     * 查询评价分页列表
     *
     * @param evaluationQueryParams 评价查询条件
     * @return 评价分页列表
     */
    IPage<UserEvaluationListVO> queryPage(EvaluationQueryParams evaluationQueryParams);

    /**
     * 添加会员评价
     * 1.检测用户是否重复评价
     * 2.获取评价相关信息添加评价
     * 3.修改子订单为已评价状态
     * 4.发送用户评价消息修改商品的评价数量以及好评率
     *
     * @param userEvaluationDTO 评论
     * @param isSelf              是否自己操作（true：买家操作/false 系统操作）
     * @return 操作状态
     */
    UserEvaluationDTO addMemberEvaluation(UserEvaluationDTO userEvaluationDTO, Boolean isSelf);

    /**
     * 根据ID查询会员评价
     *
     * @param id 评价ID
     * @return 会员评价
     */
    UserEvaluationVO queryById(String id);

    /**
     * 更改评论状态
     *
     * @param id     评价ID
     * @param status 状态
     * @return 会员评价
     */
    boolean updateStatus(String id, String status);

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 操作状态
     */
    boolean delete(String id);

    /**
     * 商家回复评价
     *
     * @param id         评价ID
     * @param reply      回复内容
     * @param replyImage 回复图片
     * @return 操作状态
     */
    boolean reply(String id, String reply, String replyImage);

    /**
     * 获取商品评价数量
     *
     * @param goodsId 商品ID
     * @return 评价数量数据
     */
    EvaluationNumberVO getEvaluationNumber(String goodsId);

    /**
     * 获取今天新增的评价数量
     *
     * @return 今日评价数量
     */
    long todayMemberEvaluation();

    /**
     * 获取等待回复评价数量
     *
     * @return 等待回复评价数量
     */
    long getWaitReplyNum();

    /**
     * 统计商品评价数量
     *
     * @param evaluationQueryParams 查询条件
     * @return 商品评价数量
     */
    long getEvaluationCount(EvaluationQueryParams evaluationQueryParams);

}