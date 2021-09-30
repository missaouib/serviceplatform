package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.RocheHospital;
import co.yixiang.modules.shop.mapper.RocheHospitalMapper;
import co.yixiang.modules.shop.service.RocheHospitalService;
import co.yixiang.modules.shop.web.param.RocheHospitalQueryParam;
import co.yixiang.modules.shop.web.vo.RocheHospitalQueryVo;
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
 * 罗氏罕见病sma医院列表 服务实现类
 * </p>
 *
 * @author visa
 * @since 2021-02-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RocheHospitalServiceImpl extends BaseServiceImpl<RocheHospitalMapper, RocheHospital> implements RocheHospitalService {

    @Autowired
    private RocheHospitalMapper rocheHospitalMapper;

    @Override
    public RocheHospitalQueryVo getRocheHospitalById(Serializable id) throws Exception{
        return rocheHospitalMapper.getRocheHospitalById(id);
    }

    @Override
    public Paging<RocheHospitalQueryVo> getRocheHospitalPageList(RocheHospitalQueryParam rocheHospitalQueryParam) throws Exception{
        Page page = setPageParam(rocheHospitalQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(RocheHospitalQueryParam.class, rocheHospitalQueryParam);
        IPage<RocheHospitalQueryVo> iPage = rocheHospitalMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
