package co.yixiang.modules.zhongan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value="众安普药订单传输对象", description="众安普药订单传输对象")
public class ResultOrder {
    @ApiModelProperty(value = "业务标识")
    private String platformCode;
    @ApiModelProperty(value = "用户标识")
    private String userId;

    @ApiModelProperty(value = "订单号")
    private String thirdOrderNo;

    @ApiModelProperty(value = "订单时间")
    private String orderTime;

    @ApiModelProperty(value = "订单金额（含运费）")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单实际金额")
    private BigDecimal orderRealAmount;

    @ApiModelProperty(value = "订单状态 3：审核中\n" +
            "4：审核失败\n" +
            "5：待发货\n" +
            "6：已发货\n" +
            "7：已完成\n" +
            "8：已退款\n")
    private String orderStatus;

    @ApiModelProperty(value = "订单备注")
    private String orderRemark;

    @ApiModelProperty(value = "订单商品信息")
    private List<OrderGoods> orderGoods;

    @ApiModelProperty(value = "邮费")
    private BigDecimal expressFee;

    @ApiModelProperty(value = "优惠详细信息")
    private List<DiscountDetails>  discountDetails;

    @ApiModelProperty(value = "收获地址")
    private DeliveryAddressDetail deliveryAddressDetail;

    @ApiModelProperty(value = "发票信息")
    private InvoiceDetail invoiceDetail;

    @ApiModelProperty(value = "订单完成时间")
    private String orderFinishTime;

    @ApiModelProperty(value = "订单类型")
    private String orderType;


}
