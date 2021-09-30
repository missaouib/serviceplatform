package co.yixiang.modules.api.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * @author zhoujinlai
 * @date 2021-09-06
 */
@Data
@ApiModel(value="药品查询入参对象", description="药品查询入参对象")
public class ProductMedStockParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "药房编码")
    private String pharmacyCode;

    @ApiModelProperty(value = "药品code")
    private String goodsCode;
}
