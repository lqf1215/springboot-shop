package cn.lili.modules.user.serviceimpl;

import cn.lili.modules.user.entity.dos.UserGrade;
import cn.lili.modules.user.mapper.UserGradeMapper;
import cn.lili.modules.user.service.UserGradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会员等级业务层实现
 *
 * @author Bulbasaur
 * @since 2021/5/14 5:58 下午
 */
@Service
public class UserGradeServiceImpl extends ServiceImpl<UserGradeMapper, UserGrade> implements UserGradeService {

}