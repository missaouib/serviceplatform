package co.yixiang.modules.yiyaobao.service.impl;

import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.modules.yiyaobao.mapper.ProductStoreMappingMapper;
import co.yixiang.modules.yiyaobao.service.ProductStoreMappingService;
import co.yixiang.modules.yiyaobao.web.param.ProductStoreMappingQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.ProductStoreMappingQueryVo;
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
 * 商品-药店-价格配置 服务实现类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductStoreMappingServiceImpl extends BaseServiceImpl<ProductStoreMappingMapper, ProductStoreMapping> implements ProductStoreMappingService {

    @Autowired
    private ProductStoreMappingMapper productStoreMappingMapper;

    @Override
    public ProductStoreMappingQueryVo getProductStoreMappingById(Serializable id) throws Exception{
        return productStoreMappingMapper.getProductStoreMappingById(id);
    }

    @Override
    public Paging<ProductStoreMappingQueryVo> getProductStoreMappingPageList(ProductStoreMappingQueryParam productStoreMappingQueryParam) throws Exception{
        Page page = setPageParam(productStoreMappingQueryParam,OrderItem.desc("create_time"));
        IPage<ProductStoreMappingQueryVo> iPage = productStoreMappingMapper.getProductStoreMappingPageList(page,productStoreMappingQueryParam);
        return new Paging(iPage);
    }

}
