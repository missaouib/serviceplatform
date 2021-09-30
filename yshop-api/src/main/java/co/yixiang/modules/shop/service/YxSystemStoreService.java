package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 门店自提 服务类
 * </p>
 *
 * @author hupeng
 * @since 2020-03-04
 */
public interface YxSystemStoreService extends BaseService<YxSystemStore> {

    List<YxSystemStoreQueryVo> getStoreList(String latitude,String longitude,int page, int limit,List<String> countryList,String keyword,String provinceName);
    List<YxSystemStoreQueryVo> getStoreListByProductId(String latitude,String longitude,int page, int limit,Integer productId,String projectCode);

    List<YxSystemStoreQueryVo> getStoreListByProductIdNoGPS(int page, int limit,Integer productId,String drugstoreName);

    YxSystemStoreQueryVo getStoreInfo(String latitude,String longitude);

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxSystemStoreQueryVo getYxSystemStoreById(Serializable id);

    /**
     * 获取分页对象
     * @param yxSystemStoreQueryParam
     * @return
     */
    Paging<YxSystemStoreQueryVo> getYxSystemStorePageList(YxSystemStoreQueryParam yxSystemStoreQueryParam);

    public List<YxSystemStoreQueryVo> getStoreListByProductIdStoreIds(String latitude, String longitude, int page, int limit,Integer productId,List<Integer> storeIds);


    List<YxSystemStoreQueryVo>  getStoreListByProductIdStoreIdsNoGPS(int page, int limit,Integer productId,List<Integer> storeIds);
}
