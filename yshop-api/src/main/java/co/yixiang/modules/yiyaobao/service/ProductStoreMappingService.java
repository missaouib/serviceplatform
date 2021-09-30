package co.yixiang.modules.yiyaobao.service;

import co.yixiang.modules.yiyaobao.entity.ProductStoreMapping;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yiyaobao.web.param.ProductStoreMappingQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.ProductStoreMappingQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 商品-药店-价格配置 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-18
 */
public interface ProductStoreMappingService extends BaseService<ProductStoreMapping> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    ProductStoreMappingQueryVo getProductStoreMappingById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param productStoreMappingQueryParam
     * @return
     */
    Paging<ProductStoreMappingQueryVo> getProductStoreMappingPageList(ProductStoreMappingQueryParam productStoreMappingQueryParam) throws Exception;

}
