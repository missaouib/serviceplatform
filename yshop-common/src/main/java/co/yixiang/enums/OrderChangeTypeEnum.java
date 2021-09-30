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
public enum OrderChangeTypeEnum {

	CACHE_KEY_CREATE_ORDER("cache_key_create_order","订单生成"),
	PAY_SUCCESS("pay_success","用户付款成功"),
	DELIVERY_GOODS("delivery_goods","订单发货"),
	CHECK_PASS("check_pass","审方通过"),
	CHECK_FAIL("check_fail","审方不通过"),
	TO_BE_CONFIRMED_PAY("to_be_confirmed_pay","付款信息待确定"),
	PAID("paid","已付款"),
	APPLY_REFUND("apply_refund","用户申请退款"),
	REFUND_PRICE("refund_price","已退款"),
	ClOSE_ORDER("close_order","订单已完成");


	private String value;
	private String desc;

	public static OrderChangeTypeEnum toType(String value) {
		return Stream.of(OrderChangeTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
