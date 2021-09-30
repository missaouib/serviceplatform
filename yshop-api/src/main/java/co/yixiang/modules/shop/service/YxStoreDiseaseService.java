package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxStoreDisease;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxStoreDiseaseQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreDiseaseQueryVo;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.DiseaseDTO;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 病种 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-03
 */
public interface YxStoreDiseaseService extends BaseService<YxStoreDisease> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxStoreDiseaseQueryVo getYxStoreDiseaseById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxStoreDiseaseQueryParam
     * @return
     */
    Paging<YxStoreDiseaseQueryVo> getYxStoreDiseasePageList(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam) throws Exception;

    List<DiseaseDTO> getList(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam);
    List<DiseaseDTO> getListFirstLevel(YxStoreDiseaseQueryParam yxStoreDiseaseQueryParam);

    List<YxStoreDisease> getList4patient();
}
