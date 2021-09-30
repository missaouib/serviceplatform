package co.yixiang.modules.taibao.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "Order4ProjectParam对象", description = "项目下单对象")
public class TbOrderProjectParam implements Serializable {
    private Integer id;

    /** 订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；4：完成；5：待审核；6：审核未通过； -1：已退款） */
    private Integer status;

    @ApiModelProperty(value = "订单明细")
    private List<TbOrderDetailProjectParam> details;

    @ApiModelProperty(value = "项目编码")
    private String projectCode = "taibaoanlian";

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "收货省份")
    private String provinceCode;

    @ApiModelProperty(value = "收货城市")
    private String cityCode;

    @ApiModelProperty(value = "收货区县")
    private String districtCode;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "用药人id")
    private Integer drugUserId;

    @ApiModelProperty(value = "用药人姓名")
    private String drugUserName;

    @ApiModelProperty(value = "用药人手机号")
    private String drugUserPhone;

    @ApiModelProperty(value = "订单备注信息")
    private String mark;

    @NotBlank(message = "请选择支付方式")
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

    @ApiModelProperty(value = "免赔余额")
    private BigDecimal deductibleTotal;

    @ApiModelProperty(value = "责任余额")
    private BigDecimal responsibilityTotal;

    @ApiModelProperty(value = "联系人手机号")
    private String phone;

    @ApiModelProperty(value = "是否已经上传至益药宝平台 0/否， 1/是")
    private Integer uploadYiyaobaoFlag;

    @ApiModelProperty(value = "是否开发票 1/否 /是")
    private Integer needInvoiceFlag;
    @ApiModelProperty(value = "开票人姓名")
    private String invoiceName;
    @ApiModelProperty(value = "开票人邮箱")
    private String invoiceMail;

}
