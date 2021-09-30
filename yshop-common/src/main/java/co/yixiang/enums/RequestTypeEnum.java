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
public enum RequestTypeEnum {

	addCoupon("yiyao.yiyaomall.addCoupon","新增优惠券"),
	issueCoupon("yiyao.yiyaomall.issueCoupon","发放优惠券"),
	addDemandList("yiyao.yiyaomall.addDemandList","新增msh需求单"),
	queryLogistics("yiyao.yiyaomall.queryLogistics","msh物流轨迹信息查询"),
	queryOrderDetail("yiyao.yiyaomall.queryOrderDetail","msh订单明细查询"),
	queryMdCountry("yiyao.yiyaomall.queryMdCountry","msh省市区查询"),
	;


	private String value;
	private String desc;

	public static RequestTypeEnum toType(String value) {
		return Stream.of(RequestTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
