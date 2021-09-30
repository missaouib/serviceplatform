package co.yixiang.modules.xikang.dto;

import lombok.Data;

import java.util.List;

@Data
public class XkOrder {
    private String supplierCode;
    private String supplierName;
    private String prescriptionCode;
    private String payDatetime;
    private String otherPayCode;
    private String paymentWay;
    private String payCost;
    private String deliveryType;
    private String receiverName;
    private String receiverTel;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String receiverAddress;
    private String deliveryPrice;
    private List<XkOrderDetail> drugs;

}
