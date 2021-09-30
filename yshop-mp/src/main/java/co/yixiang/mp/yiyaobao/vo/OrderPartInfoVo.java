package co.yixiang.mp.yiyaobao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel(value="益药宝订单对象", description="益药宝订单对象")
public class OrderPartInfoVo {
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "订单日期")
    private String orderDate;
    @ApiModelProperty(value = "订单支付状态")
    private String status;
    @ApiModelProperty(value = "物流公司名称")
    private String logisticsName;
    @ApiModelProperty(value = "快递单号")
    private String freightNo;

    /** 支付时间 */
    @ApiModelProperty(value = "支付时间")
    private Timestamp payTime;

}
