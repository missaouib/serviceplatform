package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 物流运费模板 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxExpressTemplateQueryParam对象", description="物流运费模板查询参数")
public class YxExpressTemplateQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
