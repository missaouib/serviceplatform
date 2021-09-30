package co.yixiang.modules.manage.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 采购需求单 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="PurchaseFormQueryParam对象", description="采购需求单查询参数")
public class PurchaseFormQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "用户id")
    private Integer uid;
}
