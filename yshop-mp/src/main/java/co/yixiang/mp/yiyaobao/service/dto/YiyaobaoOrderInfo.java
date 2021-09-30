package co.yixiang.mp.yiyaobao.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/6/11 8:41
 */
@Data
public class YiyaobaoOrderInfo implements Serializable {

    // 订单来源
    private String orderSource;

    // 支付时间
    private String payTime;

    // 支付结果
    private String payResult;

    // 支付方式
    private String payMethod;

    // 支付类别   客服现场收款
    private String payType;

    // 处方号
    private String prsNo;

    // 订单号
    private String orderNo;
}
