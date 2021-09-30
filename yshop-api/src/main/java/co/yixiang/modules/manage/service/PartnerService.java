package co.yixiang.modules.manage.service;

import co.yixiang.modules.manage.entity.Partner;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.manage.web.param.PartnerQueryParam;
import co.yixiang.modules.manage.web.vo.PartnerQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
public interface PartnerService extends BaseService<Partner> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    PartnerQueryVo getPartnerById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param partnerQueryParam
     * @return
     */
    Paging<PartnerQueryVo> getPartnerPageList(PartnerQueryParam partnerQueryParam) throws Exception;

}
