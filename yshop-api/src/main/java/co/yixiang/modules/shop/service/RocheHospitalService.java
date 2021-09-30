package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.RocheHospital;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.RocheHospitalQueryParam;
import co.yixiang.modules.shop.web.vo.RocheHospitalQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 罗氏罕见病sma医院列表 服务类
 * </p>
 *
 * @author visa
 * @since 2021-02-05
 */
public interface RocheHospitalService extends BaseService<RocheHospital> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    RocheHospitalQueryVo getRocheHospitalById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param rocheHospitalQueryParam
     * @return
     */
    Paging<RocheHospitalQueryVo> getRocheHospitalPageList(RocheHospitalQueryParam rocheHospitalQueryParam) throws Exception;

}
