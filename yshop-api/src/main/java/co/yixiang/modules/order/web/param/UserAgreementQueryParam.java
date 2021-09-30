package co.yixiang.modules.order.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 用户同意书 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="UserAgreementQueryParam对象", description="用户同意书查询参数")
public class UserAgreementQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
