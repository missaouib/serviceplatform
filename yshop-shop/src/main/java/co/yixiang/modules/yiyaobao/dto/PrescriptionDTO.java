package co.yixiang.modules.yiyaobao.dto;

import lombok.Data;

@Data
public class PrescriptionDTO {
    private String projectNo ;
    private String sellerId ;
    // 用药人名称（患者名称）
    private String patientName ;
    // 用药人电话（患者电话）
    private String patientMobile ;
    private String verifyCode ;
    private String provinceCode ;
    private String cityCode ;
    private String districtCode ;
    private String address ;
    private String customerRequirement ;
    private String imagePath ;
    private String items;

    // 订单号
    private String orderNo;
    //购药人电话
    private String contactMobile;
    //收货人
    private String receiver;
    //收货人电话
    private String receiverMobile;
    // 支付类型
    /*
    *   货到付款 00
        在线支付 10
        客服收款 50
        代收代付 40
        客服现场收款 60
    * */
    private String payType;

    /*发票类型
    *  00 不开发票
    *  10 普通发票
    *  20 电子发票
    * */
    private String invoiceType;

    /*发票抬头*/
    private String invoiceTitle;

    /*备注*/
    private String invoiceRemark;

    /*税号*/
    private String taxNo;

    /*开票金额*/
    private String invoiceAmount;


    private String orderSource;


}
