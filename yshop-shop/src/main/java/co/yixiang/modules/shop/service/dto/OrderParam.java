package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName OrderParam
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/28
 **/
@Data
public class OrderParam implements Serializable {
    private String addressId;
    private Integer bargainId;
    private Integer combinationId;
    private Integer couponId;
    private String from;
    private String mark;
    @NotBlank(message="请选择支付方式")
    private String payType;
    private String phone;
    private Integer pinkId = 0;
    private String realName;
    private Integer seckillId;
    private Integer shippingType;
    private Double useIntegral;
    private Integer isChannel = 1;
    private Integer storeId;
    private String imagePath;
    private String verifyCode;
    private String orderNo;
    private String projectCode="";
    private Integer insteadFlag = 0;
    @ApiModelProperty(value = "用药人id")
    private String drugUserId;
    private Integer needCloudProduceFlag = 0;
    private Integer needInternetHospitalPrescription;
    @ApiModelProperty(value = "是否开发票 1/否 /是")
    private Integer needInvoiceFlag;
    @ApiModelProperty(value = "开票人姓名")
    private String invoiceName;
    @ApiModelProperty(value = "开票人邮箱")
    private String invoiceMail;
    @ApiModelProperty(value = "云配液收件地址")
    private String cloudProduceAddress;
    @ApiModelProperty(value = "罗氏医院名称")
    private String rocheHospitalName;


}
