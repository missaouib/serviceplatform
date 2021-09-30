package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 储值记录表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RechargeLogQueryParam对象", description="储值记录表查询参数")
public class RechargeLogQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
