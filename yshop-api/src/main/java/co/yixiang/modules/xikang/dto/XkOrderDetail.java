package co.yixiang.modules.xikang.dto;

import lombok.Data;

@Data
public class XkOrderDetail {
    private String drugCode;
    private String drugName;
    private String drugNum;
    //药品单价
    private String drugPrice;
    //药品金额
    private String drugCost;
}
