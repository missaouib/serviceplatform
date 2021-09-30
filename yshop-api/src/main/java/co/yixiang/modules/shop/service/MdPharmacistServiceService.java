package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.MdPharmacistServiceQueryParam;
import co.yixiang.modules.shop.web.vo.MdPharmacistServiceQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 药师在线配置表 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-09
 */
public interface MdPharmacistServiceService extends BaseService<MdPharmacistService> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MdPharmacistServiceQueryVo getMdPharmacistServiceById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param mdPharmacistServiceQueryParam
     * @return
     */
    Paging<MdPharmacistServiceQueryVo> getMdPharmacistServicePageList(MdPharmacistServiceQueryParam mdPharmacistServiceQueryParam) throws Exception;

    MdPharmacistService getMdPharmacistByUid(Integer id);

    List<MdPharmacistService> getPharmacistByPatientId(Integer patientId);
}
