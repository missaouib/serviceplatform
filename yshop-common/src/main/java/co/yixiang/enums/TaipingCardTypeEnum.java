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
 * 应用来源相关枚举
 */
@Getter
@AllArgsConstructor
public enum TaipingCardTypeEnum {

	card_base("base","家庭药房会员"),
	card_chronic("chronic","家庭药房慢病会员"),
	card_advanced("advanced","家庭药房尊享会员");



	private String value;
	private String desc;

	public static TaipingCardTypeEnum toType(String value) {
		return Stream.of(TaipingCardTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}
}
