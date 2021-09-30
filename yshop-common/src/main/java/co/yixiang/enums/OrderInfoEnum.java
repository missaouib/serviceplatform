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
public enum OrderInfoEnum {

	STATUS_0(0,"默认"),
	STATUS_1(1,"待收货"),
	STATUS_2(2,"已收货"),
	STATUS_3(3,"已完成"),

	PAY_STATUS_0(0,"未支付"),
	PAY_STATUS_1(1,"已支付"),

	REFUND_STATUS_0(0,"正常"),
	REFUND_STATUS_1(1,"退款中"),
	REFUND_STATUS_2(2,"已退款"),

	BARGAIN_STATUS_1(1,"参与中"),
	BARGAIN_STATUS_2(2,"参与失败"),
	BARGAIN_STATUS_3(3,"参与成功"),

	PINK_STATUS_1(1,"进行中"),
	PINK_STATUS_2(2,"已完成"),
	PINK_STATUS_3(3,"未完成"),

	CANCEL_STATUS_0(0,"正常"),
	CANCEL_STATUS_1(1,"已取消"),

	CONFIRM_STATUS_0(0,"正常"),
	CONFIRM_STATUS_1(1,"确认"),

	PAY_CHANNEL_0(0,"微信公众号支付渠道"),
	PAY_CHANNEL_1(1,"微信小程序支付渠道"),
	PAY_CHANNEL_2(2,"基金会支付渠道"),
	PAY_CHANNEL_3(3,"系统外支付付款"),
	PAY_CHANNEL_4(4,"微信H5支付渠道"),
	PAY_CHANNEL_5(5,"支付宝H5支付"),
	PAY_CHANNEL_6(6,"支付宝小程序支付"),
	PAY_CHANNEL_7(7,"众安小程序支付"),
	PAY_CHANNEL_8(8,"普通浏览器H5"),

	SHIPPIING_TYPE_1(1,"快递"),
	SHIPPIING_TYPE_2(2,"门店自提");



	private Integer value;
	private String desc;

	public static OrderInfoEnum toType(int value) {
		return Stream.of(OrderInfoEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
