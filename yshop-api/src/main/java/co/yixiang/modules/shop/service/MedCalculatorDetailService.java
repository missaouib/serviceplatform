package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.MedCalculatorDetail;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.MedCalculatorDetailQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorDetailQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 用药计算器用药量变更表 服务类
 * </p>
 *
 * @author visa
 * @since 2021-01-12
 */
public interface MedCalculatorDetailService extends BaseService<MedCalculatorDetail> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MedCalculatorDetailQueryVo getMedCalculatorDetailById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param medCalculatorDetailQueryParam
     * @return
     */
    Paging<MedCalculatorDetailQueryVo> getMedCalculatorDetailPageList(MedCalculatorDetailQueryParam medCalculatorDetailQueryParam) throws Exception;

}
