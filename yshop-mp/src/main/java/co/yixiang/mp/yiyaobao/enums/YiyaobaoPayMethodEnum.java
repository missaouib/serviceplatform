/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.yiyaobao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author hupeng
 * 支付相关枚举
 */
@Getter
@AllArgsConstructor
public enum YiyaobaoPayMethodEnum {

	payMethod_01("01","现金支付"),
	payMethod_02("02","刷卡支付"),
	payMethod_11("11","银联"),
	payMethod_12("12","网上银行"),
	payMethod_13("13","微信支付"),
	payMethod_14("14","支付宝支付"),
	payMethod_21("21","金融支付"),
	payMethod_22("22","账户余额");


	private String value;
	private String desc;

	public static YiyaobaoPayMethodEnum toType(String value) {
		return Stream.of(YiyaobaoPayMethodEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
