package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxStoreProductGroup;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxStoreProductGroupQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductGroupQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 商品组合 服务类
 * </p>
 *
 * @author visa
 * @since 2021-08-19
 */
public interface YxStoreProductGroupService extends BaseService<YxStoreProductGroup> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreProductGroupQueryVo getYxStoreProductGroupById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxStoreProductGroupQueryParam
     * @return
     */
    Paging<YxStoreProductGroupQueryVo> getYxStoreProductGroupPageList(YxStoreProductGroupQueryParam yxStoreProductGroupQueryParam) throws Exception;

}
