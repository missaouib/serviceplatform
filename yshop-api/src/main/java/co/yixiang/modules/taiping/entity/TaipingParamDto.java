package co.yixiang.modules.taiping.entity;

import lombok.Data;

@Data
public class TaipingParamDto {
    // 项目编码
    private String projectCode="";

    // 卡类型
    private String cardType="";

    // 卡号
    private String cardNumber="";

    // 订单号
    private String orderNumber="";

    private String pageNum="";
}
