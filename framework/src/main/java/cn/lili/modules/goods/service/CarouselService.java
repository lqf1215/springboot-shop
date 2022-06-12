package cn.lili.modules.goods.service;


import cn.lili.modules.goods.entity.dos.Carousel;
import cn.lili.modules.goods.entity.dos.Carousel;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.dto.CarouselDTO;
import cn.lili.modules.goods.entity.dto.CarouselSearchParams;
import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.vos.CarouselVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;


public interface CarouselService extends IService<Carousel> {

    /**
     * 轮播查询
     *
     * @param carouselSearchParams 查询参数
     * @return 轮播分页
     */
    IPage<Carousel> queryByParams(CarouselSearchParams carouselSearchParams);

    /**
     * 获取轮播list
     *
     * @return 轮播list
     */
    List<CarouselVO> findByAllBySort();


    /**
     * 轮播状态的更改
     *
     * @param carouselId       轮播ID
     * @param enableOperations 是否启用
     */
    void updateCarouselEnable(String carouselId, Boolean enableOperations);


    void addCarousel(CarouselDTO carouselDTO);

    void editCarousel(CarouselDTO carouselDTO, String id);
}