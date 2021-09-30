package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 项目 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ProjectQueryParam对象", description="项目查询参数")
public class ProjectQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
