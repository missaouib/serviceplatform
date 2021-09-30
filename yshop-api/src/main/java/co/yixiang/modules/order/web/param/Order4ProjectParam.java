package co.yixiang.modules.order.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "Order4ProjectParam对象", description = "项目下单对象")
public class Order4ProjectParam implements Serializable {

    @ApiModelProperty(value = "订单明细")
    private List<OrderDetail4ProjectParam> details;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "收货地址")
    private String addressId;

    @ApiModelProperty(value = "用药人id")
    private String drugUserid;

    @ApiModelProperty(value = "订单备注信息")
    private String mark;

    @NotBlank(message="请选择支付方式")
    private String payType;

    @ApiModelProperty(value = "药房id")
    private Integer storeId;

    @ApiModelProperty(value = "药房名称")
    private String storeName;
    @ApiModelProperty(value = "处方照片")
    private String imagePath;

    @ApiModelProperty(value = "推荐人")
    private String refereeCode;

    private Integer uid;

    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;

}
