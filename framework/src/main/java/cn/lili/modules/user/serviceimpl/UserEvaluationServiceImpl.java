package cn.lili.modules.user.serviceimpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.sensitive.SensitiveWordsFilter;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserEvaluation;
import cn.lili.modules.user.entity.dto.EvaluationQueryParams;
import cn.lili.modules.user.entity.dto.UserEvaluationDTO;
import cn.lili.modules.user.entity.enums.EvaluationGradeEnum;
import cn.lili.modules.user.entity.vo.EvaluationNumberVO;
import cn.lili.modules.user.entity.vo.UserEvaluationListVO;
import cn.lili.modules.user.entity.vo.UserEvaluationVO;
import cn.lili.modules.user.mapper.UserEvaluationMapper;
import cn.lili.modules.user.service.UserEvaluationService;
import cn.lili.modules.user.service.UserService;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.entity.enums.CommentStatusEnum;
import cn.lili.modules.order.order.service.OrderItemService;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.GoodsTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ?????????????????????????????????
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Service
public class UserEvaluationServiceImpl extends ServiceImpl<UserEvaluationMapper, UserEvaluation> implements UserEvaluationService {

    /**
     * ?????????????????????
     */
    @Resource
    private UserEvaluationMapper userEvaluationMapper;
    /**
     * ??????
     */
    @Autowired
    private OrderService orderService;
    /**
     * ?????????
     */
    @Autowired
    private OrderItemService orderItemService;
    /**
     * ??????
     */
    @Autowired
    private UserService userService;
    /**
     * ??????
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * rocketMq
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * rocketMq??????
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Override
    public IPage<UserEvaluation> managerQuery(EvaluationQueryParams queryParams) {
        //??????????????????
        return this.page(PageUtil.initPage(queryParams), queryParams.queryWrapper());
    }

    @Override
    public IPage<UserEvaluationListVO> queryPage(EvaluationQueryParams evaluationQueryParams) {
        return userEvaluationMapper.getMemberEvaluationList(PageUtil.initPage(evaluationQueryParams), evaluationQueryParams.queryWrapper());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEvaluationDTO addMemberEvaluation(UserEvaluationDTO userEvaluationDTO, Boolean isSelf) {
        //?????????????????????
        OrderItem orderItem = orderItemService.getBySn(userEvaluationDTO.getOrderItemSn());
        //??????????????????
        Order order = orderService.getBySn(orderItem.getOrderSn());
        //????????????????????????????????????
        User user;

        checkMemberEvaluation(orderItem, order);

        if (Boolean.TRUE.equals(isSelf)) {
            //??????????????????????????????????????????????????????
            user = userService.getUserInfo();
        } else {
            //?????????????????? ????????????????????????????????????
            user = userService.getById(order.getUserId());
        }
        //??????????????????
        GoodsSku goodsSku = goodsSkuService.getGoodsSkuByIdFromCache(userEvaluationDTO.getSkuId());
        //??????????????????
        UserEvaluation userEvaluation = new UserEvaluation(userEvaluationDTO, goodsSku, user, order);
        //???????????????????????????
        userEvaluation.setContent(SensitiveWordsFilter.filter(userEvaluation.getContent()));
        //????????????
        this.save(userEvaluation);

        //??????????????????????????????????????????
        orderItemService.updateCommentStatus(orderItem.getSn(), CommentStatusEnum.FINISHED);
        //????????????????????????
        String destination = rocketmqCustomProperties.getGoodsTopic() + ":" + GoodsTagsEnum.GOODS_COMMENT_COMPLETE.name();
        rocketMQTemplate.asyncSend(destination, JSONUtil.toJsonStr(userEvaluation), RocketmqSendCallbackBuilder.commonCallback());
        return userEvaluationDTO;
    }

    @Override
    public UserEvaluationVO queryById(String id) {
        return new UserEvaluationVO(this.getById(id));
    }

    @Override
    public boolean updateStatus(String id, String status) {
        UpdateWrapper updateWrapper = Wrappers.update();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status.equals(SwitchEnum.OPEN.name()) ? SwitchEnum.OPEN.name() : SwitchEnum.CLOSE.name());
        return this.update(updateWrapper);
    }

    @Override
    public boolean delete(String id) {
        LambdaUpdateWrapper<UserEvaluation> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(UserEvaluation::getDeleteFlag, true);
        updateWrapper.eq(UserEvaluation::getId, id);
        return this.update(updateWrapper);
    }

    @Override
    public boolean reply(String id, String reply, String replyImage) {
        UpdateWrapper<UserEvaluation> updateWrapper = Wrappers.update();
        updateWrapper.set("reply_status", true);
        updateWrapper.set("reply", reply);
        if (CharSequenceUtil.isNotEmpty(replyImage)) {
            updateWrapper.set("have_reply_image", true);
            updateWrapper.set("reply_image", replyImage);
        }
        updateWrapper.eq("id", id);
        return this.update(updateWrapper);
    }

    @Override
    public EvaluationNumberVO getEvaluationNumber(String goodsId) {
        EvaluationNumberVO evaluationNumberVO = new EvaluationNumberVO();
        List<Map<String, Object>> list = this.baseMapper.getEvaluationNumber(goodsId);


        Integer good = 0;
        Integer moderate = 0;
        Integer worse = 0;
        for (Map<String, Object> map : list) {
            if (map.get("grade").equals(EvaluationGradeEnum.GOOD.name())) {
                good = Integer.valueOf(map.get("num").toString());
            } else if (map.get("grade").equals(EvaluationGradeEnum.MODERATE.name())) {
                moderate = Integer.valueOf(map.get("num").toString());
            } else if (map.get("grade").equals(EvaluationGradeEnum.WORSE.name())) {
                worse = Integer.valueOf(map.get("num").toString());
            }
        }
        evaluationNumberVO.setAll(good + moderate + worse);
        evaluationNumberVO.setGood(good);
        evaluationNumberVO.setModerate(moderate);
        evaluationNumberVO.setWorse(worse);
        evaluationNumberVO.setHaveImage(this.count(new QueryWrapper<UserEvaluation>()
                .eq("have_image", 1)
                .eq("goods_id", goodsId)));

        return evaluationNumberVO;
    }

    @Override
    public long todayMemberEvaluation() {
        return this.count(new LambdaQueryWrapper<UserEvaluation>().ge(UserEvaluation::getCreateTime, DateUtil.beginOfDay(new DateTime())));
    }

    @Override
    public long getWaitReplyNum() {
        QueryWrapper<UserEvaluation> queryWrapper = Wrappers.query();
        queryWrapper.eq(CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name()),
                "store_id", UserContext.getCurrentUser().getStoreId());
        queryWrapper.eq("reply_status", false);
        return this.count(queryWrapper);
    }

    /**
     * ????????????????????????
     *
     * @param evaluationQueryParams ????????????
     * @return ??????????????????
     */
    @Override
    public long getEvaluationCount(EvaluationQueryParams evaluationQueryParams) {
        return this.count(evaluationQueryParams.queryWrapper());
    }

    /**
     * ??????????????????
     *
     * @param orderItem ?????????
     * @param order     ??????
     */
    public void checkMemberEvaluation(OrderItem orderItem, Order order) {

        //??????????????????????????????????????????
        if (orderItem.getCommentStatus().equals(CommentStatusEnum.FINISHED.name())) {
            throw new ServiceException(ResultCode.EVALUATION_DOUBLE_ERROR);
        }

        //????????????????????????????????????
        if (!order.getUserId().equals(UserContext.getCurrentUser().getId())) {
            throw new ServiceException(ResultCode.ORDER_NOT_USER);
        }
    }

}