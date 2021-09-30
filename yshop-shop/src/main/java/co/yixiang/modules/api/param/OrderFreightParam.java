package co.yixiang.modules.api.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 益药宝订单状态
 * </p>
 *
 * @author zhoujinlai
 * @date 2021-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="订单运费信息对象", description="处方状态信息对象")
public class OrderFreightParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "订单来源")
    private String orderSource;

    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @ApiModelProperty(value = "运费")
    private String freight;

    @ApiModelProperty(value = "物流公司名称")
    private String deliveryName;

}
