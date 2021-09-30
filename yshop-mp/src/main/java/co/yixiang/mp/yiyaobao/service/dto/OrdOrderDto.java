/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.yiyaobao.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2020-06-28
*/
@Data
public class OrdOrderDto implements Serializable {

    /** 标识 */
    private String id;

    /** 订单号 */
    private String orderNo;

    /** EBS订单号 */
    private String ebsOrderNo;

    /** 订单类型(-;10-特药;20-普通;30-寄售) */
    private String orderType;

    /** 药店ID */
    private String sellerId;

    /** 订货用户ID */
    private String userId;

    /** 流通项目ID */
    private String projectId;

    /** 金融项目ID */
    private String finProjectId;

    /** 订单来源（01-APP;02-社区;03-医院;04-网页） */
    private String orderSource;

    /** 患者名称 */
    private String patientName;

    /** 患者身份证号码 */
    private String patientIdCard;

    /** 订单总金额(元) */
    private BigDecimal totalAmount;

    /** 折扣率(如0.92) */
    private BigDecimal discount;

    /** 折扣金额(元) */
    private BigDecimal discountAmount;

    /** 优惠券金额 */
    private BigDecimal couponAmount;

    /** 特殊折扣金额 */
    private BigDecimal specialAmount;

    /** 运费 */
    private BigDecimal freightFee;

    /** 实际运费 */
    private BigDecimal actualFreightFee;

    /** 实名认证费 */
    private BigDecimal realCertificateFee;

    /** 税费 */
    private BigDecimal taxFee;

    /** 实际结算金额 */
    private BigDecimal actualAmount;

    /** 下单时间 */
    private Timestamp orderTime;

    /** 订单状态 */
    private String status;

    /** 收货人 */
    private String receiver;

    /** 国家代码 */
    private String countryCode;

    /** 省市代码 */
    private String provinceCode;

    /** 城市代码 */
    private String cityCode;

    /** 地区代码 */
    private String districtCode;

    /** 联系地址 */
    private String address;

    /** 完整地址 */
    private String fullAddress;

    /** 邮政编码 */
    private String zipcode;

    /** 移动电话 */
    private String mobile;

    /** 电话号码 */
    private String tel;

    /** 联系人电话 */
    private String contactMobile;

    /** 邮件 */
    private String email;

    /** 支付类别(00-货到付款;10-在线支付;20-金融支付) */
    private String payType;

    /** 支付方法(01-现金;02-刷卡;11-银联;12-网上银行;13-微信支付;14-支付宝支付;21-金融支付) */
    private String payMethod;

    /** 支付时间 */
    private Timestamp payTime;

    /** 支付结果 */
    private String payResult;

    /** 预支付交易会话标识 */
    private String prepayId;

    /** 二维码链接 */
    private String qrCodeUrl;

    /** 发票类型(00-不开发票;10-普通发票;20-电子发票;30-增值税发票) */
    private String invoiceType;

    /** 备注 */
    private String remark;

    /** 配送方式(00-自提;10-快递上门) */
    private String freightType;

    /** 预计配送日期 */
    private Timestamp predictFreightDate;

    /** 预计配送时间 */
    private String predictFreightTime;

    /** 时间送达时间 */
    private Timestamp actualFreightTime;

    /** 承运人ID */
    private String freighterId;

    /** 承运人 */
    private String freighter;

    /** 货运单号 */
    private String freightNo;

    /** 物流公司配送站代码 */
    private String siteCode;

    /** 物流公司配送站名称 */
    private String siteName;

    /** 合作伙伴ID(处方来源者在益药宝系统ID) */
    private String partnerId;

    /** JD订单号 */
    private String jdOrderId;

    /** 父订单号 */
    private String parentOrderId;

    /** JD父订单号 */
    private String jdParentOrderId;

    /** JD订单类型(22-SOP；23-LBP ；25-SOPL) */
    private String jdOrderType;

    /** JD用户名 */
    private String jdPin;

    /** 供应商ID */
    private String venderId;

    /** 供应商备注 */
    private String venderRemark;

    /** JD订单来源 */
    private String jdOrderSource;

    /** 余额支付金额 */
    private BigDecimal balanceUsed;

    /** 用户最终支付的金额(订单总金额-优惠+商品运费) */
    private BigDecimal orderPayment;

    /** 订单结束时间 */
    private Timestamp orderEndTime;

    /** 订单状态描述 */
    private String jdOrderStateRemark;

    /** 国家名称 */
    private String countryName;

    /** 省市名称 */
    private String provinceName;

    /** 城市名称 */
    private String cityName;

    /** 区域名称 */
    private String districtName;

    /** 街镇 */
    private String townCode;

    /** 街镇名称 */
    private String townName;

    /** JD支付方式（10货到付款, 20邮局汇款, 30自提, 40在线支付, 50公司转账, 60银行卡转账,70商保支付） */
    private String jdPayType;

    /** 发票信息("不需要开具发票"下无需开具发票；其它返回值请正常开具发票) */
    private String invoiceInfo;

    /** 是否已开发票(0-否;1-是) */
    private Integer isInvoiceIssued;

    /** 是否已核销(0-否;1-是) */
    private Integer isRepaid;

    /** 保税区信息 */
    private String customs;

    /** 保税模型 */
    private String customsModel;

    /** 送货时间类型(10-只工作日送货(双休日、假日不用送);20-只双休日、假日送货(工作日不用送);30-工作日、双休日与假日均可送货;其他值-返回“任意时间”) */
    private String deliveryType;

    /** 物流公司ID */
    private String logisticsId;

    /** JD物流公司ID */
    private String jdLogisticsId;

    /** JD仓单 */
    private String jdStoreOrder;

    /** JD修改时间 */
    private Timestamp jdModified;

    /** 同步状态位 */
    private String synStatus;

    /** 售后订单标记(0:不是换货订单 1返修发货,直接赔偿,客服补件 2售后调货) */
    private String returnOrder;

    /** KJT系统订单号 */
    private String kjtSosysNo;

    /** KJT计算的运费金额 */
    private BigDecimal kjtShippingAmount;

    /** 是否删除(0-否;1-是) */
    private Integer isDelete;

    /** 特殊要求 */
    private String customerRequirement;

    /** 获得的积分 */
    private Long obtainPoint;

    /** 消耗积分 */
    private Long consumePoint;

    /** 兑换的金额 */
    private Long consumeAmount;

    /** 订单是否回写EBS(0-否;1-是) */
    private Integer isReturnEbs;

    /** 最后打印时间 */
    private Timestamp printTime;

    /** 结算批次号 */
    private String settleBatchNo;

    /** 金融消费金额 */
    private BigDecimal financeConsumeAmount;

    /** 是否多次配送（0. 否  1.是） */
    private Integer isMultipleDelivery;

    /** 取消原因 */
    private String cancelRemark;

    /** 合并配送单ID */
    private String deliveryBillId;

    /** 门店备注 */
    private String sellerRemark;

    /** 是否首单(0-否；1-是) */
    private Integer isFirst;

    /** 是否拒收(0-否；1-是) */
    private Integer rejectFlag;

    /** 拣货单需打印次数 */
    private Integer pickingbillRequireNum;

    /** 拣货单已打印次数 */
    private Integer pickingbillPrintNum;

    /** 门店三联单需打印次数 */
    private Integer trigeminybillRequireNum;

    /** 门店三联单已打印次数 */
    private Integer trigeminybillPrintNum;

    /** 发货单需打印次数 */
    private Integer sendgoodsbillRequireNum;

    /** 发货单已打印次数 */
    private Integer sendgoodsbillPrintNum;

    /** 运单需打印次数 */
    private Integer waybillRequireNum;

    /** 运单已打印次数 */
    private Integer waybillPrintNum;

    /** 需打印次数扩展字段1 */
    private Integer requireNumExt1;

    /** 已打印次数扩展字段1 */
    private Integer printNumExt1;

    /** 需打印次数扩展字段2 */
    private Integer requireNumExt2;

    /** 已打印次数扩展字段2 */
    private Integer printNumExt2;

    /** 需打印次数扩展字段3 */
    private Integer requireNumExt3;

    /** 已打印次数扩展字段3 */
    private Integer printNumExt3;

    /** 需打印次数扩展字段4 */
    private Integer requireNumExt4;

    /** 已打印次数扩展字段4 */
    private Integer printNumExt4;

    /** 需打印次数扩展字段5 */
    private Integer requireNumExt5;

    /** 已打印次数扩展字段5 */
    private Integer printNumExt5;

    /** 产线 */
    private String pipelineCode;

    /** 是否自动发药 */
    private Integer isAutoDispensing;

    /** 发药机标识 */
    private String dispensingId;

    /** 发货仓库ID */
    private String warehouseId;

    /** 订单重量(KG) */
    private BigDecimal ordWeight;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建人(记录帐号） */
    private String createUser;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 更新人(记录帐号） */
    private String updateUser;

    /** 处方导入时间 */
    private Timestamp prsImportTime;

    /** 自动发送消息(00-不发送；10-发送) */
    private String automationMessage;

    /** 用药医院id */
    private String hospitalId;

    /** 用药医院名称 */
    private String hospitalName;
}
