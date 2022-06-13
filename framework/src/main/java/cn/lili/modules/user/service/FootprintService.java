package cn.lili.modules.user.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.user.entity.dos.FootPrint;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 会员浏览历史业务层
 *
 * @author Chopper
 * @since 2020/11/18 10:46 上午
 */
public interface FootprintService extends IService<FootPrint> {


    /**
     * 清空当前会员的足迹
     *
     * @return 处理结果
     */
    boolean clean();

    /**
     * 根据ID进行清除会员的历史足迹
     *
     * @param ids 商品ID列表
     * @return 处理结果
     */
    boolean deleteByIds(List<String> ids);



    /**
     * 获取当前会员的浏览记录数量
     *
     * @return 当前会员的浏览记录数量
     */
    long getFootprintNum();
}