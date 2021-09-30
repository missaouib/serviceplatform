package co.yixiang.modules.user.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 内部员工表 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="InnerEmployeeQueryParam对象", description="内部员工表查询参数")
public class InnerEmployeeQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
