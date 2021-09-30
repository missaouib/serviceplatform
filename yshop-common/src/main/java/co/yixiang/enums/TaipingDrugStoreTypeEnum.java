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
public enum TaipingDrugStoreTypeEnum {

	store_88("88","88折药房"),
	store_85("85","85折药房"),
	store_50("50","5折药房");

	private String value;
	private String desc;

	public static TaipingDrugStoreTypeEnum toType(String value) {
		return Stream.of(TaipingDrugStoreTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}
}
