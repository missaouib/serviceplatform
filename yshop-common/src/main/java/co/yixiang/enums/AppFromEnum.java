/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hupeng
 * 应用来源相关枚举
 */
@Getter
@AllArgsConstructor
public enum AppFromEnum {

	WEIXIN_H5("weixinh5","weixinh5"),
	H5("h5","H5"),
	WECHAT("wechat","公众号"),
	APP("app","APP"),
	ROUNTINE("routine","小程序"),
	CSZY("cszy","慈善赠药"),
	PC("pc","PC端下单"),
	PATNER("partner","第三方合作伙伴"),
	ali_h5("alipayh5","支付宝H5支付"),
	ali_miniapp("aliminiapp","支付宝小程序"),
    zhongan_miniapp("zhongan","众安小程序")
	;



	private String value;
	private String desc;


}
