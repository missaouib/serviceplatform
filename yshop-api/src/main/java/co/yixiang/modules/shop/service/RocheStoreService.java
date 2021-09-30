package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.RocheStore;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.RocheStoreQueryParam;
import co.yixiang.modules.shop.web.vo.RocheStoreQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-28
 */
public interface RocheStoreService extends BaseService<RocheStore> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RocheStoreQueryVo getRocheStoreById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param rocheStoreQueryParam
     * @return
     */
    Paging<RocheStoreQueryVo> getRocheStorePageList(RocheStoreQueryParam rocheStoreQueryParam) throws Exception;

}
