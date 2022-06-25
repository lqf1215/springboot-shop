package cn.lili.modules.user.service;

import cn.lili.modules.user.entity.dos.UserNoticeSenter;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员消息业务层
 *
 * @author Chopper
 * @since 2020/11/17 3:44 下午
 */
public interface UserNoticeSenterService extends IService<UserNoticeSenter> {

    /**
     * 自定义保存方法
     *
     * @param userNoticeSenter 会员消息
     * @return 操作状态
     */
    boolean customSave(UserNoticeSenter userNoticeSenter);

}