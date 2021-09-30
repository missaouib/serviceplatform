package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxExpressTemplateDetail;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxExpressTemplateDetailQueryParam;
import co.yixiang.modules.shop.web.vo.YxExpressTemplateDetailQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 物流运费模板明细 服务类
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
public interface YxExpressTemplateDetailService extends BaseService<YxExpressTemplateDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxExpressTemplateDetailQueryVo getYxExpressTemplateDetailById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxExpressTemplateDetailQueryParam
     * @return
     */
    Paging<YxExpressTemplateDetailQueryVo> getYxExpressTemplateDetailPageList(YxExpressTemplateDetailQueryParam yxExpressTemplateDetailQueryParam) throws Exception;


}
