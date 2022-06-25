package cn.lili.modules.wallet.mapper;


import cn.lili.modules.wallet.entity.dos.UserWallet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 会员预存款数据处理层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface UserWalletMapper extends BaseMapper<UserWallet> {


    @Select("select * from t_user_wallet where user_id=#{userId} and currency_id=1")
    UserWallet getWalletByUserId(Long userId);

    @Update("UPDATE t_user_wallet SET `using` =#{using} WHERE user_id=#{userId} and currency_id=1")
    boolean updateWalletByUserId(Double using, Long userId);

    @Update("UPDATE t_user_integral SET `integral_mall` =#{integral} WHERE user_id=#{userId}")
    boolean updateIntegralByUserId(Double integral,Long userId);
}