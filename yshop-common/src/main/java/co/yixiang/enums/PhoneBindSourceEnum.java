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
public enum PhoneBindSourceEnum {

	SOOURCE_1(1,"微信小程序"),
	SOOURCE_2(2,"H5"),
	SOOURCE_3(3,"支付宝小程序"),
	SOOURCE_4(4,"IOS"),
	SOOURCE_5(5,"Android")
	;
	;



	private Integer value;
	private String desc;

	public static PhoneBindSourceEnum toType(int value) {
		return Stream.of(PhoneBindSourceEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
