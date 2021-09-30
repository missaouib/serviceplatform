package co.yixiang.modules.order.service;

import co.yixiang.modules.order.entity.UserAgreement;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.order.web.param.UserAgreementQueryParam;
import co.yixiang.modules.order.web.vo.UserAgreementQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 用户同意书 服务类
 * </p>
 *
 * @author visa
 * @since 2020-11-30
 */
public interface UserAgreementService extends BaseService<UserAgreement> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    UserAgreementQueryVo getUserAgreementById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param userAgreementQueryParam
     * @return
     */
    Paging<UserAgreementQueryVo> getUserAgreementPageList(UserAgreementQueryParam userAgreementQueryParam) throws Exception;

}
