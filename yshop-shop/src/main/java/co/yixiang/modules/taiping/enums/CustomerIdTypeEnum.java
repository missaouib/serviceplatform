/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.taiping.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author hupeng
 * 证件类型
 */
@Getter
@AllArgsConstructor
public enum CustomerIdTypeEnum {

	shenFenZheng("身份证","身份证"),
	junRenZheng("军人证","军人证"),
	huZhao("护照","护照"),
	chuShengZheng("出生证","出生证"),
	yiChangShenFenZheng("异常身份证","异常身份证"),
	gangAoTaiTongXingZheng("港澳台通行证","港澳台通行证"),
	shiBingZheng("士兵证","士兵证"),
	jingGuanZheng("警官证","警官证"),
	juMinHuKouBu("居民户口簿","居民户口簿"),
	qiTa("其它","其它");

	private String value;
	private String desc;

	public static CustomerIdTypeEnum toType(String value) {
		return Stream.of(CustomerIdTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
