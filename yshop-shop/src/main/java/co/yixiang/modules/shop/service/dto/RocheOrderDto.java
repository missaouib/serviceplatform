package co.yixiang.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RocheOrderDto  implements Serializable {

    //报单日期
    private String orderDate;

    // 需求单提交人
    private String userName;

    //需求单编号
    private String orderNo;

    //患者姓名
    private String patientName;

    //患者年龄
    private Integer patientAge;

    //患者性别
    private String patientSex;

    //患者体重
    private String patientWeight;

    //购买数量
    private Integer purchaseQty;

    //给药剂量
    private String dosage;

    //疾病诊断
    private String diagnosis;

    //处方医院
    private String hospitalName;

    //处方医生
    private String doctorName;

    //处方日期
    private String prescriptionDate;

    // 付款日期
    private String payDate;

    //汇款人姓名
    private String payerAccountName;

    //收件人姓名
    private String receiverName;

    //收件地址
    private String address;

    //联系电话
    private String receiverMobile;

    //发药药房
    private String storeName;

    //服务药房
    private String serviceDrugstoreName;

    //是否委托配液
    private String needCloudProduceFlag;

    //是否赠送冰包
    private String giveIceFlag;

    //冰包赠送方
    private String iceGiver;

    //非首选药房发药原因（如有）
    private String reason;

    //特殊情况
    private String specialSituation;

    //药品配制日期
    private String drugPreparationDate;

    // 药品收货日期
    private String drugReceiptDate;

    //药品用完天数
    private String drugUseUpDay;

    //开始服药日期
    private String startDate;

    //预计用完日期
    private String drugUseUpDate;

    //药品有效期
    private String drugExpiryDate;

    //是否效期内能用完
    private String usedUpFlag;

    //复购提醒日期
    private String repurchaseReminderDate;

    //是否同意复购
    private String repurchaseFlag;

    //最近随访日期
    private String lastFollowUpDate;

    //随访方式
    private String followUpMethod;

    //家属反馈
    private String familyFeedback;

    //服务药师
    private String serviceChemist;

    //订单金额
    private String totalAmount;

    //订单状态
    private String statusName;

    //退款状态
    private String refundStatusName;

    //退款用户说明
    private String refundDesc;

    //申请退款时间
    private String applyRefundDate;

    //实际退款时间
    private String factRefundDate;

    //结单日期
    private String completeDate;

}
