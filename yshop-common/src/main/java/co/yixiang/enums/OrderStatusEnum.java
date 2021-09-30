/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author hupeng
 * 订单相关枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

	STATUS_0(0,"未支付"),
	STATUS_1(1,"待发货"),
	STATUS_2(2,"待收货"),
	STATUS_3(3,"待评价"),
	STATUS_4(4,"已完成"),
	STATUS_5(5,"待审核"),
	STATUS_6(6,"审核未通过"),
	STATUS_7(7,"药店取消订单"),
	STATUS_8(8,"用户取消订单"),
	STATUS_9(9,"已备货"),
	STATUS_10(10,"未申请处方"),
	STATUS_11(11,"付款信息待确定"),
	STATUS_12(12,"取消待确认"),  // 用户取消订单，待确认
	STATUS_13(13,"待开方"),  // 未开具处方
	STATUS_14(14,"待上传处方"),  //
	STATUS_MINUS_1(-1,"退款中"),
	STATUS_MINUS_2(-2,"已退款"),
	STATUS_MINUS_3(-3,"退款"),
	STATUS_99(99,"全部订单"),
	STATUS_15(15,"待下发益药宝");
	;



	private Integer value;
	private String desc;

	public static OrderStatusEnum toType(int value) {
		return Stream.of(OrderStatusEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
