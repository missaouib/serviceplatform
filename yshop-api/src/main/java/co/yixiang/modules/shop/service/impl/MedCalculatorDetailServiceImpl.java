package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.MedCalculatorDetail;
import co.yixiang.modules.shop.mapper.MedCalculatorDetailMapper;
import co.yixiang.modules.shop.service.MedCalculatorDetailService;
import co.yixiang.modules.shop.web.param.MedCalculatorDetailQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorDetailQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 * 用药计算器用药量变更表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-01-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MedCalculatorDetailServiceImpl extends BaseServiceImpl<MedCalculatorDetailMapper, MedCalculatorDetail> implements MedCalculatorDetailService {

    @Autowired
    private MedCalculatorDetailMapper medCalculatorDetailMapper;

    @Override
    public MedCalculatorDetailQueryVo getMedCalculatorDetailById(Serializable id) throws Exception{
        return medCalculatorDetailMapper.getMedCalculatorDetailById(id);
    }

    @Override
    public Paging<MedCalculatorDetailQueryVo> getMedCalculatorDetailPageList(MedCalculatorDetailQueryParam medCalculatorDetailQueryParam) throws Exception{
        Page page = setPageParam(medCalculatorDetailQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(MedCalculatorDetailQueryParam.class, medCalculatorDetailQueryParam);
        IPage<MedCalculatorDetailQueryVo> iPage = medCalculatorDetailMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
