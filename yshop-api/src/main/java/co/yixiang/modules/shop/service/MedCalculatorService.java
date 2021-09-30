package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.MedCalculator;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shop.web.param.MedCalculatorQueryParam;
import co.yixiang.modules.shop.web.vo.MedCalculatorQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用药计算器 服务类
 * </p>
 *
 * @author visa
 * @since 2021-01-03
 */
public interface MedCalculatorService extends BaseService<MedCalculator> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    MedCalculatorQueryVo getMedCalculatorById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param medCalculatorQueryParam
     * @return
     */
    Paging<MedCalculatorQueryVo> getMedCalculatorPageList(MedCalculatorQueryParam medCalculatorQueryParam) throws Exception;

    MedCalculator getMedCalculatorByUid(Integer uid, Date calcuDate);
    void updateMedCalculator(MedCalculator medCalculator);

}
