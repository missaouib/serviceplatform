package co.yixiang.modules.manage.service;

import co.yixiang.modules.manage.entity.PurchaseForm;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.manage.web.param.PurchaseFormQueryParam;
import co.yixiang.modules.manage.web.vo.PurchaseFormQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 采购需求单 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
public interface PurchaseFormService extends BaseService<PurchaseForm> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    PurchaseFormQueryVo getPurchaseFormById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param purchaseFormQueryParam
     * @return
     */
    Paging<PurchaseFormQueryVo> getPurchaseFormPageList(PurchaseFormQueryParam purchaseFormQueryParam) throws Exception;

}
