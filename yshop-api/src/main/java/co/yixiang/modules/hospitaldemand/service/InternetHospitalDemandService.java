package co.yixiang.modules.hospitaldemand.service;

import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandOrderParam;
import co.yixiang.modules.hospitaldemand.web.param.InternetHospitalDemandQueryParam;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalCart;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalDemandQueryVo;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.order.entity.YxStoreOrder;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;

/**
 * <p>
 * 互联网医院导入的需求单 服务类
 * </p>
 *
 * @author visa
 * @since 2020-12-04
 */
public interface InternetHospitalDemandService extends BaseService<InternetHospitalDemand> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    InternetHospitalDemandQueryVo getInternetHospitalDemandById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param internetHospitalDemandQueryParam
     * @return
     */
    Paging<InternetHospitalDemand> getInternetHospitalDemandPageList(InternetHospitalDemandQueryParam internetHospitalDemandQueryParam) throws Exception;

    YxStoreOrder saveDemand(InternetHospitalDemand internetHospitalDemand);

    Boolean noticeDemand(InternetHospitalDemandOrderParam orderParam);

    InternetHospitalCart generateCart(Integer demandId);

    InternetHospitalCart queryInternetHospitalPrescriptionImage(Integer uid,String orderKey);
}
