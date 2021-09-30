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
public enum PayTypeEnum {

	WEIXIN("weixin","微信支付"),
	YUE("yue","余额支付"),
	OFFLINE("offline","线下支付"),
	SMS("sms","短信支付"),
	ALIPAY("alipay","支付宝支付"),
	ThirdParty("third-party-payment","第三方支付"),
	ONLINE("online","线上支付"),
	MAPI("mapi","翼支付"),
	ZhongAnPay("zhongan","众安支付"),
	;


	private String value;
	private String desc;

	public static PayTypeEnum toType(String value) {
		return Stream.of(PayTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
