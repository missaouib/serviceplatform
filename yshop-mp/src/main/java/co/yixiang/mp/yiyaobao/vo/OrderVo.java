package co.yixiang.mp.yiyaobao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value="益药宝订单对象", description="益药宝订单对象")
public class OrderVo {
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "订单日期")
    private String orderDate;
    @ApiModelProperty(value = "购药人")
    private String name;
    @ApiModelProperty(value = "购药手机号")
    private String mobile;
    @ApiModelProperty(value = "订单支付状态")
    private String status;
    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "订单折后总金额")
    private BigDecimal discountTotalAmount;
    @ApiModelProperty(value = "处方医院")
    private String hospitalName;
    @ApiModelProperty(value = "处方医生")
    private String doctorName;
    @ApiModelProperty(value = "商品购买渠道")
    private String channelName;
    @ApiModelProperty(value = "物流信息")
    private String expressInfo;
    @ApiModelProperty(value = "药店名称")
    private String storeName;
/*    @ApiModelProperty(value = "诊断结果")
    private String diagnoseResult;*/
    @ApiModelProperty(value = "收货地址")
    private String address;
    @ApiModelProperty(value = "收货人")
    private String receiveName;
    @ApiModelProperty(value = "收货人电话")
    private String receiveMobile;
    List<OrderDetailVo> details;

    @ApiModelProperty(value = "物流公司名称")
    private String logisticsName;
    @ApiModelProperty(value = "快递单号")

    private String freightNo;
    @ApiModelProperty(value = "处方id")
    private String prescriptionTempId;

    @ApiModelProperty(value = "处方图片id")
    private String imageId;

    @ApiModelProperty(value = "处方图片地址")
    private String imagePath;
    @ApiModelProperty(value = "订单id")
    private String id;
    @ApiModelProperty(value = "合作伙伴Id")
    private String partnerId;
    @ApiModelProperty(value = "处方编号")
    private String prescripNo;
    @ApiModelProperty(value = "合作伙伴Code")
    private String partnerCode;
    @ApiModelProperty(value = "合作伙伴Key")
    private String privateKey;

    private String statusCode;

    private String factUserPhone;

    private Date payTime;

    private String payResult;
}
