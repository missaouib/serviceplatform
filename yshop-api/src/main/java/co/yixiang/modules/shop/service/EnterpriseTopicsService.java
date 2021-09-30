package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.EnterpriseTopics;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.EnterpriseTopicsQueryParam;
import co.yixiang.modules.shop.web.vo.EnterpriseTopicsQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
public interface EnterpriseTopicsService extends BaseService<EnterpriseTopics> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    EnterpriseTopicsQueryVo getEnterpriseTopicsById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param enterpriseTopicsQueryParam
     * @return
     */
    Paging<EnterpriseTopicsQueryVo> getEnterpriseTopicsPageList(EnterpriseTopicsQueryParam enterpriseTopicsQueryParam) throws Exception;

}
