package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxUserAppointment;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.YxUserAppointmentQueryParam;
import co.yixiang.modules.shop.web.vo.YxUserAppointmentQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 预约活动 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
public interface YxUserAppointmentService extends BaseService<YxUserAppointment> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxUserAppointmentQueryVo getYxUserAppointmentById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxUserAppointmentQueryParam
     * @return
     */
    Paging<YxUserAppointmentQueryVo> getYxUserAppointmentPageList(YxUserAppointmentQueryParam yxUserAppointmentQueryParam) throws Exception;

}
