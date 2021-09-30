package co.yixiang.modules.zhongan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value="众安普药优惠详细信息", description="众安普药优惠详细信息")
public class DiscountDetails {
    @ApiModelProperty(value = "优惠唯一标识")
    private String discountNo;
    @ApiModelProperty(value = "优惠名称")
    private String discountName;
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountPrice;
}
