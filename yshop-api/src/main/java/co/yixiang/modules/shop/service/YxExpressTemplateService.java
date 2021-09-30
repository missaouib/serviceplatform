package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxExpressTemplate;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 物流运费模板 服务类
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
public interface YxExpressTemplateService extends BaseService<YxExpressTemplate> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxExpressTemplateQueryVo getYxExpressTemplateById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxExpressTemplateQueryParam
     * @return
     */
    Paging<YxExpressTemplateQueryVo> getYxExpressTemplatePageList(YxExpressTemplateQueryParam yxExpressTemplateQueryParam) throws Exception;

    Boolean saveTemplate(YxExpressTemplate yxExpressTemplat);
}
