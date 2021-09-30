package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 用药计算器 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-01-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MedCalculatorQueryParam对象", description="用药计算器查询参数")
public class MedCalculatorQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
