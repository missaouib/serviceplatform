package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.entity.UrlConfig;
import co.yixiang.modules.shop.mapper.UrlConfigMapper;
import co.yixiang.modules.shop.service.UrlConfigService;
import co.yixiang.modules.shop.web.param.UrlConfigQueryParam;
import co.yixiang.modules.shop.web.vo.UrlConfigQueryVo;
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
 * @since 2020-06-10
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UrlConfigServiceImpl extends BaseServiceImpl<UrlConfigMapper, UrlConfig> implements UrlConfigService {

    @Autowired
    private UrlConfigMapper urlConfigMapper;

    @Override
    public UrlConfigQueryVo getUrlConfigById(Serializable id) throws Exception{
        return urlConfigMapper.getUrlConfigById(id);
    }

    @Override
    public Paging<UrlConfigQueryVo> getUrlConfigPageList(UrlConfigQueryParam urlConfigQueryParam) throws Exception{
        Page page = setPageParam(urlConfigQueryParam,OrderItem.desc("create_time"));
        IPage<UrlConfigQueryVo> iPage = urlConfigMapper.getUrlConfigPageList(page,urlConfigQueryParam);
        return new Paging(iPage);
    }

}
