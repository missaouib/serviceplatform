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
public enum ButtonEnum {

	Button_11("11","取消"),
	Button_12("12","预付"),
	Button_13("13","退款电话"),
	Button_14("14","查询物流"),
	Button_15("15","申请退款"),
	Button_16("16","申请处方"),
	Button_17("17","收货"),
	Button_18("18","申请取消"),
	Button_19("19","提交付款");


	private String value;
	private String desc;

	public static ButtonEnum toType(String value) {
		return Stream.of(ButtonEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
