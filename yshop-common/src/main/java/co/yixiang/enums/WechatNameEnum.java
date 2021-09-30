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
public enum WechatNameEnum {

	WECHAT("wechat","公众号"),
	GUANGZHOU("guangzhou","广州"),
	YIAOSHITONG("yaoshitong","药师通");

	private String value;
	private String desc;

	public static WechatNameEnum toType(String value) {
		return Stream.of(WechatNameEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
