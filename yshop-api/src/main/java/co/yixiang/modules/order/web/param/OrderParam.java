package co.yixiang.modules.order.web.param;

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
   // @NotBlank(message="请选择支付方式")
    private String payType;
    // 替他人下单时 真实用户手机号
    private String phone;
    // 替他人下单时 真实用户姓名
    private String realName;
    private Integer pinkId = 0;
    private Integer seckillId;
    private Integer shippingType;
    private Double useIntegral;
    private Integer isChannel = 1;
    private Integer storeId;
    private String imagePath;
    private String verifyCode;
    private String orderNo;
    /*是否替其他人下单 0 否 1 是*/
    private Integer insteadFlag = 0;

    /*是否云配液   值：0 否 1是*/
    private Integer needCloudProduceFlag = 0;
    // 购药人实际姓名
    private String buyerName;
    private Integer age;

    private String factUserPhone;


    private String projectCode="";
    private String cardNumber="";
    private String orderNumber="";
    private String cardType="";

    @ApiModelProperty(value = "收款方开户名")
    private String payeeAccountName;

    @ApiModelProperty(value = "收款方开户银行")
    private String payeeBankName;

    @ApiModelProperty(value = "收款方银行账号")
    private String payeeBankAccount;

    @ApiModelProperty(value = "付款方名称")
    private String payerAccountName;

    @ApiModelProperty(value = "付款凭证照片")
    private String payerVoucherImage;

    @ApiModelProperty(value = "用药人id")
    private String drugUserId;

    @ApiModelProperty(value = "是否开发票 1/否 /是")
    private Integer needInvoiceFlag;
    @ApiModelProperty(value = "开票人姓名")
    private String invoiceName;
    @ApiModelProperty(value = "开票人邮箱")
    private String invoiceMail;

    @ApiModelProperty(value = "是否需要互联网医院处方 值：0 否 1是")
    private Integer needInternetHospitalPrescription;

    @ApiModelProperty(value = "云配液收件地址")
    private String cloudProduceAddress;

    @ApiModelProperty(value = "罗氏医院名称")
    private String rocheHospitalName;

    @ApiModelProperty(value = "支付宝小程序用户id")
    private String userid;

    @ApiModelProperty(value = "收货地址类型 1 医院 2 药房 3 其他")
    private String addressType;

    @ApiModelProperty(value = "运费模板id")
    private Integer expressTemplateId;

}
