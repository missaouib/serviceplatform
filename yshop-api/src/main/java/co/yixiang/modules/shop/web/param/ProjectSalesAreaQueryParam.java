package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 项目配置销售省份 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ProjectSalesAreaQueryParam对象", description="项目配置销售省份查询参数")
public class ProjectSalesAreaQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
