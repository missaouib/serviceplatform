package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.Hospital;
import co.yixiang.modules.shop.mapper.HospitalMapper;
import co.yixiang.modules.shop.service.HospitalService;
import co.yixiang.modules.shop.web.param.HospitalQueryParam;
import co.yixiang.modules.shop.web.vo.HospitalQueryVo;
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
 * 医院 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-06-11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class HospitalServiceImpl extends BaseServiceImpl<HospitalMapper, Hospital> implements HospitalService {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Override
    public HospitalQueryVo getHospitalById(Serializable id) throws Exception{
        return hospitalMapper.getHospitalById(id);
    }

    @Override
    public Paging<HospitalQueryVo> getHospitalPageList(HospitalQueryParam hospitalQueryParam) throws Exception{
        Page page = setPageParam(hospitalQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(HospitalQueryParam.class, hospitalQueryParam);
        IPage<HospitalQueryVo> iPage = hospitalMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
