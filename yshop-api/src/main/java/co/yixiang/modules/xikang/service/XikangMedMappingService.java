package co.yixiang.modules.xikang.service;

import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.xikang.web.param.XikangMedMappingQueryParam;
import co.yixiang.modules.xikang.web.vo.XikangMedMappingQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 熙康医院与商城药品的映射 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-30
 */
public interface XikangMedMappingService extends BaseService<XikangMedMapping> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    XikangMedMappingQueryVo getXikangMedMappingById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param xikangMedMappingQueryParam
     * @return
     */
    Paging<XikangMedMappingQueryVo> getXikangMedMappingPageList(XikangMedMappingQueryParam xikangMedMappingQueryParam) throws Exception;

}
