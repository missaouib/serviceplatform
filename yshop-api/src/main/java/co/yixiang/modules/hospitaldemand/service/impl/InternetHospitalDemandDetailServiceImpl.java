package co.yixiang.modules.hospitaldemand.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.mapper.InternetHospitalDemandDetailMapper;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandDetailQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandDetailQueryVo;
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
 * 互联网医院导入的需求单药品明细 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class InternetHospitalDemandDetailServiceImpl extends BaseServiceImpl<InternetHospitalDemandDetailMapper, InternetHospitalDemandDetail> implements InternetHospitalDemandDetailService {

    @Autowired
    private InternetHospitalDemandDetailMapper internetHospitalDemandDetailMapper;

    @Override
    public InternetHospitalDemandDetailQueryVo getInternetHospitalDemandDetailById(Serializable id) throws Exception{
        return internetHospitalDemandDetailMapper.getInternetHospitalDemandDetailById(id);
    }

    @Override
    public Paging<InternetHospitalDemandDetailQueryVo> getInternetHospitalDemandDetailPageList(InternetHospitalDemandDetailQueryParam internetHospitalDemandDetailQueryParam) throws Exception{
        Page page = setPageParam(internetHospitalDemandDetailQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(InternetHospitalDemandDetailQueryParam.class, internetHospitalDemandDetailQueryParam);
        IPage<InternetHospitalDemandDetail> iPage = internetHospitalDemandDetailMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
