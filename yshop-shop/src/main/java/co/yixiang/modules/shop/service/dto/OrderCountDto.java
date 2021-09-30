/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderCountDto {

    private List<String> column;

    private List<OrderCountData> orderCountDatas;

    @Data
    public static class OrderCountData{
        private String name;

        private Integer value;
    }

    //订单支付没有退款 数量
    private Integer orderCount;
    //订单支付没有退款 支付总金额
    private Double sumPrice;
    //订单待支付 数量
    private Integer unpaidCount;
    private Integer unshippedCount; //订单待发货 数量
    private Integer receivedCount;  //订单待收货 数量
    private Integer evaluatedCount;  //订单待评价 数量
    private Integer completeCount;  //订单已完成 数量
    private Integer refundCount;   //订单退款
    private Integer checkCount;   //订单审核
    private Integer checkFailCount;   //订单审核
}
