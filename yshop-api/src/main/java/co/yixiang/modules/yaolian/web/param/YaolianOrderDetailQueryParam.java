package co.yixiang.modules.yaolian.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 药联订单明细 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaolianOrderDetailQueryParam对象", description="药联订单明细查询参数")
public class YaolianOrderDetailQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
