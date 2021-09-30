package co.yixiang.modules.api.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 益药宝订单状态
 * </p>
 *
 * @author visa
 * @date 2019-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="订单信息对象", description="订单信息对象")
public class OrderInfoParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订单号")
    @NotBlank(message="请订单号")
    private String orderNo;

    @ApiModelProperty(value = "物流公司名称")
    private String deliveryName;

    @ApiModelProperty(value = "运单号")
    private String deliveryId;


}
