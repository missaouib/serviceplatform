package co.yixiang.modules.hospitaldemand.service;

import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemandDetail;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandDetailQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandDetailQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 互联网医院导入的需求单药品明细 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
public interface InternetHospitalDemandDetailService extends BaseService<InternetHospitalDemandDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    InternetHospitalDemandDetailQueryVo getInternetHospitalDemandDetailById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param internetHospitalDemandDetailQueryParam
     * @return
     */
    Paging<InternetHospitalDemandDetailQueryVo> getInternetHospitalDemandDetailPageList(InternetHospitalDemandDetailQueryParam internetHospitalDemandDetailQueryParam) throws Exception;

}
