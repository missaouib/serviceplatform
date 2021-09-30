/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.domain;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
* @author hupeng
* @date 2020-05-12
*/

@Data
@TableName("yx_store_order")
public class YxStoreOrder implements Serializable {

    /** 订单ID */
    @TableId
    private Integer id;


    /** 订单号 */
    private String orderId;


    /** 额外订单号 */
    private String extendOrderId;


    /** 用户id */
    private Integer uid;


    /** 用户姓名 */
    private String realName;


    /** 用户电话 */
    private String userPhone;


    /** 详细地址 */
    private String userAddress;


    /** 购物车id */
    private String cartId;


    /** 运费金额 */
    private BigDecimal freightPrice;


    /** 订单商品总数 */
    private Integer totalNum;


    /** 订单总价 */
    private BigDecimal totalPrice;


    /** 邮费 */
    private BigDecimal totalPostage;


    /** 实际支付金额 */
    private BigDecimal payPrice;


    /** 支付邮费 */
    private BigDecimal payPostage;


    /** 抵扣金额 */
    private BigDecimal deductionPrice;


    /** 优惠券id */
    private Integer couponId;


    /** 优惠券金额 */
    private BigDecimal couponPrice;


    /** 支付状态 */
    private Integer paid;


    /** 支付时间 */
    private Integer payTime;


    /** 支付方式 */
    private String payType;


    /** 创建时间 */
    private Integer addTime;


    /** 订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款） */
    private Integer status;


    /** 0 未退款 1 申请中 2 已退款 */
    private Integer refundStatus;


    /** 退款图片 */
    private String refundReasonWapImg;


    /** 退款用户说明 */
    private String refundReasonWapExplain;


    /** 退款时间 */
    private Integer refundReasonTime;


    /** 前台退款原因 */
    private String refundReasonWap;


    /** 不退款的理由 */
    private String refundReason;


    /** 退款金额 */
    private BigDecimal refundPrice;


    /** 快递公司编号 */
    private String deliverySn;


    /** 快递名称/送货人姓名 */
    private String deliveryName;


    /** 发货类型 */
    private String deliveryType;


    /** 快递单号/手机号 */
    private String deliveryId;


    /** 消费赚取积分 */
    private BigDecimal gainIntegral;


    /** 使用积分 */
    private BigDecimal useIntegral;


    /** 给用户退了多少积分 */
    private BigDecimal backIntegral;


    /** 备注 */
    private String mark;


    /** 是否删除 */
    private Integer isDel;


    /** 唯一id(md5加密)类似id */
     @TableField(value = "`unique`")
    //@NotBlank
    private String unique;


    /** 管理员备注 */
    private String remark;


    /** 商户ID */
    private Integer merId;


    private Integer isMerCheck;


    /** 拼团产品id0一般产品 */
    private Integer combinationId;


    /** 拼团id 0没有拼团 */
    private Integer pinkId;


    /** 成本价 */
    private BigDecimal cost;


    /** 秒杀产品ID */
    private Integer seckillId;


    /** 砍价id */
    private Integer bargainId;


    /** 核销码 */
    private String verifyCode;


    /** 门店id */
    private Integer storeId;


    /** 配送方式 1=快递 ，2=门店自提 */
    private Integer shippingType;


    /** 支付渠道(0微信公众号1微信小程序) */
    private Integer isChannel;


    private Integer isRemind;


    private Integer isSystemDel;

    // 是否已经上传至管家婆 0/否 1/是
    private Integer uploadGjpFlag;

    public void copy(YxStoreOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    private Integer orderExpressFlag;

    private String imagePath;

    private Integer orderRecoverFlag;

    private Integer orderRecoverStatus;

    @ApiModelProperty(value = "需求单，购物单，慈善赠药")
    private String type;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "推荐人编码")
    private String refereeCode;

    @ApiModelProperty(value = "合作伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "科室编码")
    private String departCode;

    @ApiModelProperty(value = "审核不通过原因")
    private String checkFailReason;

    @ApiModelProperty(value = "审核不通过备注")
    private String checkFailRemark;

    @ApiModelProperty(value = "审核人")
    private String checkUser;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private java.util.Date checkTime;

    @ApiModelProperty(value = "审核状态")
    private String checkStatus;

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


    @ApiModelProperty(value = "是否替其他人下单 0 否 1 是")
    private Integer insteadFlag;

    @ApiModelProperty(value = "是否云配液   值：0 否 1是")
    private Integer needCloudProduceFlag;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "实际用户的姓名")
    private String factUserName;

    @ApiModelProperty(value = "实际用户的电话")
    private String factUserPhone;

    @ApiModelProperty(value = "实际用户的id")
    private Integer factUserId;

    @ApiModelProperty(value = "太平订单号")
    private String taipingOrderNumber;

    @ApiModelProperty(value = "益药宝订单id（主键id）")
    private String yiyaobaoOrderId;

    @ApiModelProperty(value = "益药宝订单No")
    private String yiyaobaoOrderNo;

    @ApiModelProperty(value = "是否已经上传至益药宝平台 0/否， 1/是")
    private Integer uploadYiyaobaoFlag;
    @ApiModelProperty(value = "上传至益药宝平台时间")
    private Date uploadYiyaobaoTime;

    @ApiModelProperty(value = "退款订单是否已经上传至益药宝平台 0/否， 1/是")
    private Integer uploadYiyaobaoRefundFlag;
    @ApiModelProperty(value = "退款订单上传至益药宝平台时间")
    private Date uploadYiyaobaoRefundTime;
    @ApiModelProperty(value = "是否需要互联网医院处方 值：0 否 1是 2 已收到处方")
    private Integer needInternetHospitalPrescription;
    @ApiModelProperty(value = "是否需要退款")
    private Integer needRefund = 0;

    @ApiModelProperty(value = "收货省份")
    private String provinceName;
    @ApiModelProperty(value = "收货城市")
    private String cityName;
    @ApiModelProperty(value = "收货区县")
    private String districtName;
    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "太平会员卡类型")
    private String cardType;

    @ApiModelProperty(value = "太平会员卡类型")
    private String cardNumber;

    @ApiModelProperty(value = "处方id")
    private Integer demandId;

    @ApiModelProperty(value = "用药人id")
    private Integer drugUserId;

    @ApiModelProperty(value = "用药人姓名")
    private String drugUserName;

    @ApiModelProperty(value = "用药人手机号")
    private String drugUserPhone;

    @ApiModelProperty(value = "是否开发票 0/否 1/是")
    private Integer needInvoiceFlag;
    @ApiModelProperty(value = "开票人姓名")

    private String invoiceName;
    @ApiModelProperty(value = "开票人邮箱")
    private String invoiceMail;

    @ApiModelProperty(value = "云配液收件地址")
    private String cloudProduceAddress;

    @ApiModelProperty(value = "罗氏医院名称")
    private String rocheHospitalName;

    @ApiModelProperty(value = "预计收货日期")
    private Date expectedReceivingDate;

    @ApiModelProperty(value = "用药人身份证号")
    private String drugUserIdcard;

    @ApiModelProperty(value = "用药人出身年月")
    private String drugUserBirth;

    @ApiModelProperty(value = "用药人体重")
    private String drugUserWeight;

    @ApiModelProperty(value = "用药人性别")
    private String drugUserSex;

    @ApiModelProperty(value = "用药人类型 1/成人 2/儿童")
    private Integer drugUserType;

    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;

    @ApiModelProperty(value = "支付来源")
    private String payFrom;

    @ApiModelProperty(value = "支付单号（支付宝）")
    private String tradeNo;

    @ApiModelProperty(value = "商户配置名称")
    @TableField(exist = false)
    private String mchName;

    @ApiModelProperty(value = "模板消息提示说明")
    private String templateMessage;

    @ApiModelProperty(value = "物流单号")
    private String logisticsOrderNo;

    @ApiModelProperty(value = "服务药师")
    private String serviceChemist;

    @ApiModelProperty(value = "服务药店")
    private String serviceDrugstore;

    @ApiModelProperty(value = "服务药店Id")
    private Integer serviceDrugstoreId;

    @ApiModelProperty(value = "服务联系电话")
    private String chemistPhone;

    @ApiModelProperty(value = "实际退款时间")
    private Date refundFactTime;

    @ApiModelProperty(value = "收货地址类型 1 医院 2 药房 3 其他")
    private String addressType;

    @ApiModelProperty(value = "商户号(微信)或APPID(支付宝)")
    private String merchantNumber;

    @ApiModelProperty(value = "退款类型（1：财务退款，2：门店退款）")
    @TableField(exist = false)
    private String returnType;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "支付商户订单号")
    private String payOutTradeNo;
}
