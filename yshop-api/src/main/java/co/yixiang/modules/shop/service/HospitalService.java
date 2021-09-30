package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.Hospital;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.HospitalQueryParam;
import co.yixiang.modules.shop.web.vo.HospitalQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 医院 服务类
 * </p>
 *
 * @author visa
 * @since 2021-06-11
 */
public interface HospitalService extends BaseService<Hospital> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    HospitalQueryVo getHospitalById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param hospitalQueryParam
     * @return
     */
    Paging<HospitalQueryVo> getHospitalPageList(HospitalQueryParam hospitalQueryParam) throws Exception;

}
