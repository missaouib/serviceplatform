package co.yixiang.modules.order.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@ApiModel(value = "OrderDetail4ProjectParam对象", description = "项目下单明细对象")
@Data
public class OrderDetail4ProjectParam implements Serializable {

    @ApiModelProperty(value = "药品id")
    private Integer productId;
    @ApiModelProperty(value = "药品针对药房的唯一码")
    private String productUniqueId;
    @ApiModelProperty(value = "数量")
    private Integer num;

}
