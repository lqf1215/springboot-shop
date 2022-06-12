package cn.lili.modules.goods.entity.vos;

import cn.lili.modules.goods.entity.dos.Goods;
import lombok.Data;

import java.util.List;

@Data
public class HomeGoodsVO {

    List<Goods> HomePageGoods;

    List<Goods> SelectGoods;
}
