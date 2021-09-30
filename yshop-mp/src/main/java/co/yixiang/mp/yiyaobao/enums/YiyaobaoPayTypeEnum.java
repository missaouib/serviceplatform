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
public enum YiyaobaoPayTypeEnum {

	payType_00("00","货到付款"),
	payType_10("10","在线支付"),
	payType_50("50","客服收款"),
	payType_40("40","代收代付"),
	payType_60("60","客服现场收款");


	private String value;
	private String desc;

	public static YiyaobaoPayTypeEnum toType(String value) {
		return Stream.of(YiyaobaoPayTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
