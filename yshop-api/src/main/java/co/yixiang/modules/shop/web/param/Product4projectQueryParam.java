package co.yixiang.modules.shop.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 项目对应的药品 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Product4projectQueryParam对象", description="项目对应的药品查询参数")
public class Product4projectQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目编号")
    private String projectCode;
}
