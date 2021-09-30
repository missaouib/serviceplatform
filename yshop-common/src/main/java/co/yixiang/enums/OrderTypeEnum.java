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
public enum OrderTypeEnum {

	STATUS_PRODUCT("product","购物单"),
	STATUS_DEMAND("demand","需求单"),
	STATUS_CS("cs","慈善赠药"),
	STATUS_ThirdPartner("thirdPartner","第三方合作伙伴");


	private String value;
	private String desc;

	public static OrderTypeEnum toType(String value) {
		return Stream.of(OrderTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
