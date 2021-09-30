package co.yixiang.modules.manage.service.impl;

import co.yixiang.modules.manage.entity.Partner;
import co.yixiang.modules.manage.mapper.PartnerMapper;
import co.yixiang.modules.manage.service.PartnerService;
import co.yixiang.modules.manage.web.param.PartnerQueryParam;
import co.yixiang.modules.manage.web.vo.PartnerQueryVo;
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
 * @since 2020-05-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PartnerServiceImpl extends BaseServiceImpl<PartnerMapper, Partner> implements PartnerService {

    @Autowired
    private PartnerMapper partnerMapper;

    @Override
    public PartnerQueryVo getPartnerById(Serializable id) throws Exception{
        return partnerMapper.getPartnerById(id);
    }

    @Override
    public Paging<PartnerQueryVo> getPartnerPageList(PartnerQueryParam partnerQueryParam) throws Exception{
        Page page = setPageParam(partnerQueryParam,OrderItem.desc("create_time"));
        IPage<PartnerQueryVo> iPage = partnerMapper.getPartnerPageList(page,partnerQueryParam);
        return new Paging(iPage);
    }

}
