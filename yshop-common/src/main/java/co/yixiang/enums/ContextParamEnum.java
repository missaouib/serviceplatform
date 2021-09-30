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
 * 支付相关枚举
 */
@Getter
@AllArgsConstructor
public enum ContextParamEnum {

	context_1("weixin","微信支付"),
	context_2("yue","余额支付"),
	context_3("offline","线下支付"),
	context_4("sms","短信支付"),
	context_5("alipay","支付宝支付"),
	context_6("third-party-payment","第三方支付"),
	context_7("online","线上支付"),
	context_8("zhongan","众安支付")
	;


	private String value;
	private String desc;

	public static ContextParamEnum toType(String value) {
		return Stream.of(ContextParamEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
