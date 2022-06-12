package cn.lili.modules.user.aop.interceptor;

import cn.lili.modules.user.entity.dos.User;
import cn.lili.modules.user.entity.dos.UserPointsHistory;
import cn.lili.modules.user.entity.enums.PointTypeEnum;
import cn.lili.modules.user.service.UserPointsHistoryService;
import cn.lili.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 积分操作切面
 *
 * @author Chopper
 * @since 2020/11/17 7:22 下午
 */
@Slf4j
@Aspect
@Component
public class PointLogInterceptor {

    @Autowired
    private UserPointsHistoryService userPointsHistoryService;

    @Autowired
    private UserService userService;

    @After("@annotation(cn.lili.modules.user.aop.annotation.PointLogPoint)")
    public void doAfter(JoinPoint pjp) {
        //参数
        Object[] obj = pjp.getArgs();
        try {
            //变动积分
            Long point = 0L;
            if (obj[0] != null) {
                point = Long.valueOf(obj[0].toString());
            }
            //变动类型
            String type = PointTypeEnum.INCREASE.name();
            if (obj[1] != null) {
                type = obj[1].toString();
            }
            // 会员ID
            String userId = "";
            if (obj[2] != null) {
                userId = obj[2].toString();
            }
            // 变动积分为0，则直接返回
            if (point == 0) {
                return;
            }

            //根据会员id查询会员信息
            User user = userService.getById(userId);
            if (user != null) {
                UserPointsHistory memberPointsHistory = new UserPointsHistory();
                memberPointsHistory.setUserId(""+user.getId());
                memberPointsHistory.setUserName(user.getName());
                memberPointsHistory.setPointType(type);

                memberPointsHistory.setVariablePoint(point);
                if (type.equals(PointTypeEnum.INCREASE.name())) {
//                    memberPointsHistory.setBeforePoint(user.getPoint() - point);
                } else {
//                    memberPointsHistory.setBeforePoint(user.getPoint() + point);
                }

//                memberPointsHistory.setPoint(user.getPoint());
                memberPointsHistory.setContent(obj[3] == null ? "" : obj[3].toString());
                memberPointsHistory.setCreateBy("系统");
                userPointsHistoryService.save(memberPointsHistory);
            }
        } catch (Exception e) {
            log.error("积分操作错误", e);
        }


    }

}
