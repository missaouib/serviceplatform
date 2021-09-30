package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.entity.EnterpriseTopics;
import co.yixiang.modules.shop.mapper.EnterpriseTopicsMapper;
import co.yixiang.modules.shop.service.EnterpriseTopicsService;
import co.yixiang.modules.shop.web.param.EnterpriseTopicsQueryParam;
import co.yixiang.modules.shop.web.vo.EnterpriseTopicsQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class EnterpriseTopicsServiceImpl extends BaseServiceImpl<EnterpriseTopicsMapper, EnterpriseTopics> implements EnterpriseTopicsService {

    @Autowired
    private EnterpriseTopicsMapper enterpriseTopicsMapper;

    @Override
    public EnterpriseTopicsQueryVo getEnterpriseTopicsById(Serializable id) throws Exception{
        return enterpriseTopicsMapper.getEnterpriseTopicsById(id);
    }

    @Override
    public Paging<EnterpriseTopicsQueryVo> getEnterpriseTopicsPageList(EnterpriseTopicsQueryParam enterpriseTopicsQueryParam) throws Exception{
        Page page = setPageParam(enterpriseTopicsQueryParam,OrderItem.desc("add_time"));
        IPage<EnterpriseTopicsQueryVo> iPage = enterpriseTopicsMapper.getEnterpriseTopicsPageList(page,enterpriseTopicsQueryParam);
        return new Paging(iPage);
    }

}
