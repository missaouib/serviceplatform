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
public enum ProjectNameEnum {

	TAIPING_LEXIANG("taipinglexiang","太平乐享健康药品福利项目"),
	ROCHE_SMA("rochesma","利司扑兰口服溶液用散"),
	CSZY("cszy","慈善赠药"),
	YAOLIAN("yaolian","药联健康"),
	MSH("msh","MSH"),
	TAIBAOANLIAN("taibaoanlian","太保安联"),
	ZHONGANPUYAO("zhonganpuyao","众安项目"),
	MEIDEYI("meideyi","美德医"),
	ANT("ant","蚂蚁项目"),
	DIAO("diao","地奥项目"),
	ZHONGANMANBING("zhonganmanbing","众安慢无忧"),
	HEALTHCARE("healthcare","健康生活"),
	LINGYUANZHI("lingyuanzhi","众安0元治"),
	KELUN("kelun","科伦项目"),
	SDFM("sdfm","山东方明项目"),
	BAIJI("baiji","百济项目")

	;




	private String value;
	private String desc;

	public static ProjectNameEnum toType(String value) {
		return Stream.of(ProjectNameEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}
}
