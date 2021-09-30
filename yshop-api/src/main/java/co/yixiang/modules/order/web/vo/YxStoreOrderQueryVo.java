package co.yixiang.modules.order.web.vo;

import co.yixiang.modules.order.web.dto.StatusDTO;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.modules.taibao.web.vo.ClaimInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单表 查询结果对象
 * </p>
 *
 * @author hupeng
 * @date 2019-10-27
 */
@Data
@ApiModel(value = "YxStoreOrderQueryVo对象", description = "订单表查询参数")
public class YxStoreOrderQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;


    private String extendOrderId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户电话")
    private String userPhone;

    @ApiModelProperty(value = "详细地址")
    private String userAddress;

    @ApiModelProperty(value = "购物车id")
    private String cartId;

    private List<YxStoreCartQueryVo> cartInfo;

    private StatusDTO _status;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightPrice;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionPrice;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "支付状态")
    private Integer paid;

    @ApiModelProperty(value = "支付时间")
    private Integer payTime;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "创建时间")
    private Integer addTime;

    @ApiModelProperty(value = "订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款）")
    private Integer status;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "退款时间")
    private Integer refundReasonTime;

    @ApiModelProperty(value = "前台退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "不退款的理由")
    private String refundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    private String deliverySn;

    @ApiModelProperty(value = "发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递单号/手机号")
    private String deliveryId;

    @ApiModelProperty(value = "消费赚取积分")
    private BigDecimal gainIntegral;

    @ApiModelProperty(value = "使用积分")
    private BigDecimal useIntegral;

    @ApiModelProperty(value = "给用户退了多少积分")
    private BigDecimal backIntegral;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "唯一id(md5加密)类似id")
    private String unique;

    @ApiModelProperty(value = "管理员备注")
    private String remark;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    private Integer isMerCheck;

    @ApiModelProperty(value = "拼团产品id0一般产品")
    private Integer combinationId;

    @ApiModelProperty(value = "拼团id 0没有拼团")
    private Integer pinkId;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "秒杀产品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "门店id")
    private Integer storeId;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private Integer shippingType;

    @ApiModelProperty(value = "支付渠道(0微信公众号1微信小程序)")
    private Integer isChannel;

    private Integer isRemind;

    private Integer isSystemDel;

    private String code;

    private String mapKey;

    private YxSystemStoreQueryVo systemStore;

    private String imagePath;

    private String type;

    private String statusName;

    private String yiyaobaoOrderId;

    private Boolean isSelfOrder;

    @ApiModelProperty(value = "是否替其他人下单 0 否 1 是")
    private Integer insteadFlag;

    @ApiModelProperty(value = "实际用户的姓名")
    private String factUserName;

    @ApiModelProperty(value = "实际用户的电话")
    private String factUserPhone;


    private String projectCode;

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

    @ApiModelProperty(value = "药师列表")
    private List<MdPharmacistService> pharmacists;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

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

    @ApiModelProperty(value = "是否需要互联网医院处方 值：0 否 1是 2 已收到处方")
    private Integer needInternetHospitalPrescription;

    @ApiModelProperty(value = "太平订单号")
    private String taipingOrderNumber;

    @ApiModelProperty(value = "太平卡类型")
    private String cardType;
    @ApiModelProperty(value = "太平卡号")
    private String cardNumber;

    /**
     * 0 未审核
     * 1 处方申请成功
     * 2 处方申请失败
     * 3 处方审核驳回
     * */
    @ApiModelProperty(value = "互联网医院处方申请状态")
    private Integer internetHospitalNoticeFlag = 0 ;

    @ApiModelProperty(value = "是否需要退款")
    private Integer needRefund = 0;


    @ApiModelProperty(value = "云配液收件地址")
    private String cloudProduceAddress;

    @ApiModelProperty(value = "罗氏医院名称")
    private String rocheHospitalName;

    @ApiModelProperty(value = "预计收货日期")
    private Date expectedReceivingDate;

    @ApiModelProperty(value = "七鱼客服组id")
    private String serviceGroupId;

    @ApiModelProperty(value = "服务电话")
    private String servicePhone;

    private String projectName;

    @ApiModelProperty(value = "太保安联附件信息")
    private  ClaimInfoVo claimInfoVo;
    //
    private String mchName;

    @ApiModelProperty(value = "是否处方药订单")
    private Boolean prescriptionFlag;

    @ApiModelProperty(value = "物流单号")
    private String logisticsOrderNo;

    @ApiModelProperty(value = "服务药师")
    private String serviceChemist;

    @ApiModelProperty(value = "服务药店Id")
    private Integer serviceDrugstoreId;

    @ApiModelProperty(value = "服务药店")
    private String serviceDrugstore;

    @ApiModelProperty(value = "服务药师、客服联系电话")
    private String chemistPhone;

    @ApiModelProperty(value = "是否云配液   值：0 否 1是")
    private Integer needCloudProduceFlag;

    @ApiModelProperty(value = "药师提示")
    private String pharmacistTips;

    @ApiModelProperty(value = "审核不通过原因")
    private String checkFailReason;

    @ApiModelProperty(value = "审核不通过备注")
    private String checkFailRemark;

    @ApiModelProperty(value = "支付商户订单号")
    private String payOutTradeNo;
}