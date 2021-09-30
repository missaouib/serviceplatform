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
public enum MSHOrderChangeTypeEnum {

	DSH("wait_for_checked", "待审核"),
	SHTG("check_pass", "审核通过"),
	SHBTG("check_fail", "审核不通过"),
	YFH("delivery_goods", "已发货"),
	YWC("close_order", "已完成"),
	YTH("return_goods", "已退货"),
	BH("reject", "驳回"),;


	private String value;
	private String desc;

	public static MSHOrderChangeTypeEnum toType(String value) {
		return Stream.of(MSHOrderChangeTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
