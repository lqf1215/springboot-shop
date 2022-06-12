package cn.lili.modules.goods.serviceimpl;

import cn.lili.modules.goods.entity.dos.Carousel;
import cn.lili.modules.goods.entity.dto.*;
import cn.lili.modules.goods.entity.vos.CarouselVO;
import cn.lili.modules.goods.mapper.CarouselMapper;
import cn.lili.modules.goods.service.*;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 轮播业务层实现
 *
 * @author pikachu
 * @since 2020-02-23 15:18:56
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Override
    public IPage<Carousel> queryByParams(CarouselSearchParams carouselSearchParams) {
        return this.page(PageUtil.initPage(carouselSearchParams), carouselSearchParams.queryWrapper());
    }

    @Override
    public List<CarouselVO> findByAllBySort() {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", false)
        .eq("enable",true)
        .orderByAsc("sort");
        List<Carousel> list = this.baseMapper.selectList(queryWrapper);
        //构造分类树
       List<CarouselVO> carouselVOList = new ArrayList<>();
        for (Carousel carousel : list) {

            CarouselVO carouselVO = new CarouselVO(carousel);

            carouselVOList.add(carouselVO);
        }
        return carouselVOList;
    }




    @Override
    public void updateCarouselEnable(String carouselId, Boolean enableOperations) {
        UpdateWrapper<Carousel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", carouselId)
                .set("enable",enableOperations);
        this.update(updateWrapper);
    }
   // 添加轮播
    @Override
    public void addCarousel(CarouselDTO carouselDTO) {
        Carousel carousel=new Carousel(carouselDTO);
        //添加轮播
        this.save(carousel);
    }

    @Override
    public void editCarousel(CarouselDTO carouselDTO, String id) {
        Carousel carousel=new Carousel(carouselDTO);
        carousel.setId(id);
        //修改轮播
        this.updateById(carousel);
    }
}