package co.yixiang.modules.xikang.service.impl;

import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.modules.xikang.mapper.XikangMedMappingMapper;
import co.yixiang.modules.xikang.service.XikangMedMappingService;
import co.yixiang.modules.xikang.web.param.XikangMedMappingQueryParam;
import co.yixiang.modules.xikang.web.vo.XikangMedMappingQueryVo;
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
 * 熙康医院与商城药品的映射 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-12-30
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class XikangMedMappingServiceImpl extends BaseServiceImpl<XikangMedMappingMapper, XikangMedMapping> implements XikangMedMappingService {

    @Autowired
    private XikangMedMappingMapper xikangMedMappingMapper;

    @Override
    public XikangMedMappingQueryVo getXikangMedMappingById(Serializable id) throws Exception{
        return xikangMedMappingMapper.getXikangMedMappingById(id);
    }

    @Override
    public Paging<XikangMedMappingQueryVo> getXikangMedMappingPageList(XikangMedMappingQueryParam xikangMedMappingQueryParam) throws Exception{
        Page page = setPageParam(xikangMedMappingQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(XikangMedMappingQueryParam.class, xikangMedMappingQueryParam);
        IPage<XikangMedMappingQueryVo> iPage = xikangMedMappingMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

}
