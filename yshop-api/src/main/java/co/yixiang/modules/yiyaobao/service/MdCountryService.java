package co.yixiang.modules.yiyaobao.service;

import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.yiyaobao.web.param.MdCountryQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.MdCountryQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 国家地区信息表 服务类
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
public interface MdCountryService extends BaseService<MdCountry> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MdCountryQueryVo getMdCountryById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param mdCountryQueryParam
     * @return
     */
    Paging<MdCountryQueryVo> getMdCountryPageList(MdCountryQueryParam mdCountryQueryParam) throws Exception;

}
