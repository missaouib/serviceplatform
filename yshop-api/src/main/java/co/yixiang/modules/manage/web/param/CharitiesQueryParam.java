package co.yixiang.modules.manage.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 慈善活动表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="CharitiesQueryParam对象", description="慈善活动表查询参数")
public class CharitiesQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
