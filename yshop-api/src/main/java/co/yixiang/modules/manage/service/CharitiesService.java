package co.yixiang.modules.manage.service;

import co.yixiang.modules.manage.entity.Charities;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.manage.web.param.CharitiesQueryParam;
import co.yixiang.modules.manage.web.vo.CharitiesQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 慈善活动表 服务类
 * </p>
 *
 * @author visa
 * @since 2020-08-20
 */
public interface CharitiesService extends BaseService<Charities> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    CharitiesQueryVo getCharitiesById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param charitiesQueryParam
     * @return
     */
    Paging<Charities> getCharitiesPageList(CharitiesQueryParam charitiesQueryParam) throws Exception;

}
