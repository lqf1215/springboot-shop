package cn.lili.modules.promotion.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.promotion.entity.dos.MemberCoupon;
import cn.lili.modules.promotion.entity.dto.search.MemberCouponSearchParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 会员优惠券业务层
 *
 * @author Chopper
 * @since 2020/11/18 9:45 上午
 */
public interface MemberCouponService extends IService<MemberCoupon> {

    /**
     * 检查该会员领取优惠券的可领取数量
     *
     * @param couponId 优惠券编号
     * @param userId 会员
     */
    void checkCouponLimit(String couponId, String userId);

    /**
     * 领取优惠券
     *
     * @param couponId   优惠券编号
     * @param userId   会员
     * @param userName 会员名称
     */
    void receiveBuyerCoupon(String couponId, String userId, String userName);

    /**
     * 领取优惠券
     *
     * @param couponId   优惠券编号
     * @param userId   会员
     * @param userName 会员名称
     */
    void receiveCoupon(String couponId, String userId, String userName);

    /**
     * 获取会员优惠券列表
     *
     * @param param  查询参数
     * @param pageVo 分页参数
     * @return 会员优惠券列表
     */
    IPage<MemberCoupon> getMemberCoupons(MemberCouponSearchParams param, PageVO pageVo);

    /**
     * 获取会员优惠券列表
     *
     * @param param  查询参数
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getMemberCoupons(MemberCouponSearchParams param);

    /**
     * 获取当前用户的优惠券列表（优先读取缓存）
     *
     * @param userId 会员id
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getMemberCoupons(String userId);

    /**
     * 获取会员优惠券列表
     *
     * @param param      查询参数
     * @param totalPrice 当前商品总价
     * @param pageVo     分页参数
     * @return 会员优惠券列表
     */
    IPage<MemberCoupon> getMemberCouponsByCanUse(MemberCouponSearchParams param, Double totalPrice, PageVO pageVo);

    /**
     * 获取当前会员当前商品可用的会员优惠券
     *
     * @param userId   会员Id
     * @param couponIds  优惠券id列表
     * @param totalPrice 当前商品总价
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getCurrentGoodsCanUse(String userId, List<String> couponIds, Double totalPrice);

    /**
     * 获取当前会员全品类优惠券
     *
     * @param userId 会员Id
     * @param storeId  店铺id
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getAllScopeMemberCoupon(String userId, List<String> storeId);

    /**
     * 获取会员优惠券
     *
     * @param param  查询参数
     * @return 会员优惠券列表
     */
    MemberCoupon getMemberCoupon(MemberCouponSearchParams param);

    /**
     * 获取会员优惠券数量
     *
     * @return 会员优惠券数量
     */
    long getMemberCouponsNum();

    /**
     * 使用优惠券
     *
     * @param ids 会员优惠券id
     */
    void used(String userId, List<String> ids);

    /**
     * 作废当前会员优惠券
     *
     * @param id id
     */
    void cancellation(String userId, String id);

    /**
     * 作废无效的会员优惠券
     *
     * @return 是否操作成功
     */
    boolean expireInvalidMemberCoupon(String userId);

    /**
     * 关闭会员优惠券
     *
     * @param couponIds 优惠券id集合
     */
    void closeMemberCoupon(List<String> couponIds);

}