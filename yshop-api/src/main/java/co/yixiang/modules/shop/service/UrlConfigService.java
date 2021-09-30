package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.UrlConfig;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.UrlConfigQueryParam;
import co.yixiang.modules.shop.web.vo.UrlConfigQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-10
 */
public interface UrlConfigService extends BaseService<UrlConfig> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    UrlConfigQueryVo getUrlConfigById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param urlConfigQueryParam
     * @return
     */
    Paging<UrlConfigQueryVo> getUrlConfigPageList(UrlConfigQueryParam urlConfigQueryParam) throws Exception;

}
