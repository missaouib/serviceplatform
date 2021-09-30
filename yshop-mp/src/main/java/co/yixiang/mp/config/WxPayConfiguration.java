/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.config;

import co.yixiang.mp.handler.RedisHandler;
import co.yixiang.utils.RedisUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 支付配置
 * @author hupeng
 * @date 2020/03/01
 */
@Slf4j
@Configuration
public class WxPayConfiguration {

	private static Map<String, WxPayService> payServices = Maps.newHashMap();

	private static RedisHandler redisHandler;

	@Autowired
	public WxPayConfiguration(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	/**
	 *  获取WxPayService
	 * @return
	 */
	public static WxPayService getPayService(String wechatName) {
		wechatName=StringUtils.isEmpty(wechatName)?"":wechatName;
		WxPayService wxPayService = payServices.get(ShopKeyUtils.getYshopWeiXinPayService(wechatName));
		if(wxPayService == null || RedisUtil.get(ShopKeyUtils.getYshopWeiXinPayService(wechatName)) == null) {
			WxPayConfig payConfig = new WxPayConfig();
			payConfig.setAppId(RedisUtil.get(ShopKeyUtils.getWechatAppId(wechatName)));
			payConfig.setMchId(RedisUtil.get(ShopKeyUtils.getWxPayMchId(wechatName)));
			payConfig.setMchKey(RedisUtil.get(ShopKeyUtils.getWxPayMchKey(wechatName)));
			payConfig.setKeyPath(RedisUtil.get(ShopKeyUtils.getWxPayKeyPath(wechatName)));
			// 可以指定是否使用沙箱环境
			payConfig.setUseSandboxEnv(false);
			wxPayService = new WxPayServiceImpl();
			wxPayService.setConfig(payConfig);
			payServices.put(ShopKeyUtils.getYshopWeiXinPayService(wechatName), wxPayService);

			//增加标识
			RedisUtil.set(ShopKeyUtils.getYshopWeiXinPayService(wechatName),"yshop");
		}
		return wxPayService;
	}

	/**
	 *  获取小程序WxAppPayService
	 * @return
	 */
	public static WxPayService getWxAppPayService(String wechatName) {
		wechatName=StringUtils.isEmpty(wechatName)?"":wechatName;
		log.info("wechatName:{}",wechatName);
		WxPayService wxPayService = payServices.get(ShopKeyUtils.getYshopWeiXinMiniPayService()+wechatName);
		if(wxPayService == null || RedisUtil.get(ShopKeyUtils.getYshopWeiXinPayService(wechatName)) == null) {
			WxPayConfig payConfig = new WxPayConfig();
			payConfig.setAppId(RedisUtil.get(ShopKeyUtils.getWxAppAppId()));
			payConfig.setMchId(RedisUtil.get(ShopKeyUtils.getWxPayMchId(wechatName)));
			payConfig.setMchKey(RedisUtil.get(ShopKeyUtils.getWxPayMchKey(wechatName)));
			payConfig.setKeyPath(RedisUtil.get(ShopKeyUtils.getWxPayKeyPath(wechatName)));
			// 可以指定是否使用沙箱环境
			payConfig.setUseSandboxEnv(false);
			wxPayService = new WxPayServiceImpl();
			wxPayService.setConfig(payConfig);
			payServices.put(ShopKeyUtils.getYshopWeiXinMiniPayService()+wechatName, wxPayService);

			//增加标识
			RedisUtil.set(ShopKeyUtils.getYshopWeiXinPayService(wechatName),"yshop");
		}
		return wxPayService;
	}

	/**
	 *  获取众安小程序WxAppPayService
	 * @return
	 */
	public static WxPayService getWxAppPayService4zhongan(String wechatName) {
		wechatName=StringUtils.isEmpty(wechatName)?"":wechatName;
		WxPayService wxPayService = payServices.get("zhongan");
		if(wxPayService == null || RedisUtil.get(("zhongan")) == null) {
			WxPayConfig payConfig = new WxPayConfig();
			payConfig.setAppId("wx3c564538ea8e3905");
			payConfig.setMchId(RedisUtil.get(ShopKeyUtils.getWxPayMchId(wechatName)));
			payConfig.setMchKey(RedisUtil.get(ShopKeyUtils.getWxPayMchKey(wechatName)));
			payConfig.setKeyPath(RedisUtil.get(ShopKeyUtils.getWxPayKeyPath(wechatName)));
			// 可以指定是否使用沙箱环境
			payConfig.setUseSandboxEnv(false);
			wxPayService = new WxPayServiceImpl();
			wxPayService.setConfig(payConfig);
			payServices.put("zhongan", wxPayService);

			//增加标识
			RedisUtil.set("zhongan","yshop");
		}
		return wxPayService;
	}

	/**
	 *  获取APPPayService
	 * @return
	 */
	public static WxPayService getAppPayService(String wechatName) {
		wechatName=StringUtils.isEmpty(wechatName)?"":wechatName;
		WxPayService wxPayService = payServices.get(ShopKeyUtils.getYshopWeiXinAppPayService());
		if(wxPayService == null || RedisUtil.get(ShopKeyUtils.getYshopWeiXinPayService(wechatName)) == null) {
			WxPayConfig payConfig = new WxPayConfig();
			payConfig.setAppId(RedisUtil.get(ShopKeyUtils.getWxNativeAppAppId()));
			payConfig.setMchId(RedisUtil.get(ShopKeyUtils.getWxPayMchId(wechatName)));
			payConfig.setMchKey(RedisUtil.get(ShopKeyUtils.getWxPayMchKey(wechatName)));
			payConfig.setKeyPath(RedisUtil.get(ShopKeyUtils.getWxPayKeyPath(wechatName)));
			// 可以指定是否使用沙箱环境
			payConfig.setUseSandboxEnv(false);
			wxPayService = new WxPayServiceImpl();
			wxPayService.setConfig(payConfig);
			payServices.put(ShopKeyUtils.getYshopWeiXinAppPayService(), wxPayService);

			//增加标识
			RedisUtil.set(ShopKeyUtils.getYshopWeiXinPayService(wechatName),"yshop");
		}
		return wxPayService;
	}

	/**
	 * 移除WxPayService
	 */
	public static void removeWxPayService(String wechatName){
		wechatName=StringUtils.isEmpty(wechatName)?"":wechatName;
		RedisUtil.del(ShopKeyUtils.getYshopWeiXinPayService(wechatName));
		payServices.remove(ShopKeyUtils.getYshopWeiXinPayService(wechatName));
		payServices.remove(ShopKeyUtils.getYshopWeiXinMiniPayService());
		payServices.remove(ShopKeyUtils.getYshopWeiXinAppPayService());
	}

}
