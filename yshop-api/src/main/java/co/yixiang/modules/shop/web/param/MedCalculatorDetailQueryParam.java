package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 用药计算器用药量变更表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MedCalculatorDetailQueryParam对象", description="用药计算器用药量变更表查询参数")
public class MedCalculatorDetailQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
