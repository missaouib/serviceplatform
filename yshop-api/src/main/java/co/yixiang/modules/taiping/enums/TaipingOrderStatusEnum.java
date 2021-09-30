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
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum TaipingOrderStatusEnum {

	yuYueTiJiao(1,"预约申请已提交"),
	yuYueChengGong(2,"预约成功"),
	yuYueQuXiao(3,"预约取消"),
	yuYueShiBai(4,"预约失败"),
	fuWuWanCheng(5,"服务完成"),
	fuWuShiBai(6,"服务失败"),
	yiShouLi(7,"已受理"),
	jinXingZhong(8,"进行中"),
	yiWanCheng(9,"已完成"),
	STATUS_10(10,"已付款"),
	STATUS_11(11,"审核通过"),
	STATUS_12(12,"审核不通过"),
	STATUS_13(13,"发货"),
	STATUS_14(14,"关闭"),
	STATUS_15(15,"已退货"),
	STATUS_16(16,"已退款")
	;


	private Integer value;
	private String desc;

	public static TaipingOrderStatusEnum toType(Integer value) {
		return Stream.of(TaipingOrderStatusEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
