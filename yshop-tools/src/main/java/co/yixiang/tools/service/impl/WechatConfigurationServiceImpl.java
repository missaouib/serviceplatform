/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.mp.config.ShopKeyUtils;
import co.yixiang.mp.config.WxPayConfiguration;
import co.yixiang.tools.domain.AlipayConfiguration;
import co.yixiang.tools.domain.WechatConfiguration;
import co.yixiang.tools.service.WechatConfigurationService;
import co.yixiang.tools.service.dto.WechatConfigurationDto;
import co.yixiang.tools.service.dto.WechatConfigurationQueryCriteria;
import co.yixiang.tools.service.mapper.WechatConfigurationMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author zhoujinlai
* @date 2021-09-24
*/
@Slf4j
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "wechatConfiguration")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class WechatConfigurationServiceImpl extends BaseServiceImpl<WechatConfigurationMapper, WechatConfiguration> implements WechatConfigurationService {

    @Autowired
    private IGenerator generator;

    @Value("${server.ip}")
    private String ip;

    @Autowired
    private WechatConfigurationMapper wechatConfigurationMapper;


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(WechatConfigurationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<WechatConfiguration> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), WechatConfigurationDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<WechatConfiguration> queryAll(WechatConfigurationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(WechatConfiguration.class, criteria));
    }


    @Override
    public void download(List<WechatConfigurationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WechatConfigurationDto wechatConfiguration : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("1：小程序 2：H5  3: 公众号 4：app 5：众安", wechatConfiguration.getType());
            map.put("1:默认", wechatConfiguration.getIsDefault());
            map.put("应用名称", wechatConfiguration.getName());
            map.put("商户Id", wechatConfiguration.getMchId());
            map.put("商户key", wechatConfiguration.getMchKey());
            map.put("微信appid", wechatConfiguration.getAppId());
            map.put("支付证书地址", wechatConfiguration.getKeyPath());
            map.put("应用网关 ，回调接口", wechatConfiguration.getNotifyUrl());
            map.put("创建时间", wechatConfiguration.getCreateTime());
            map.put("创建人", wechatConfiguration.getCreateUser());
            map.put("更新时间", wechatConfiguration.getUpdateTime());
            map.put("更新人", wechatConfiguration.getUpdateUser());
            map.put("删除表示  1：已删除  0 未删除", wechatConfiguration.getDeleteFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean saveWechatConfiguration(WechatConfiguration resources) {
        Integer a= wechatConfigurationMapper.checkedMchid(resources);
        if(a>0){
            throw new BadRequestException("同环境类型下商户Id不能重复");
        }
        String redsKey="";
        if(resources.getType().equals("1")){
            redsKey="wechat_routine_"+resources.getMchId();
        }
        if(resources.getType().equals("2")){
            redsKey="wechat_h5_"+resources.getMchId();
        }
        if(resources.getType().equals("3")){
            redsKey="wechat_app_"+resources.getMchId();
        }
        if(resources.getType().equals("4")){
            redsKey="wechat_zhongan_"+resources.getMchId();
        }

        QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
        wrapper.eq("type",resources.getType());
        wrapper.eq("delete_flag",0);
        Integer typeCount = count(wrapper);
        if(typeCount==0){
            resources.setIsDefault(1);
        }
        if(resources.getIsDefault()==1){
            LambdaUpdateWrapper<WechatConfiguration> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(WechatConfiguration::getType,resources.getType());
            lambdaUpdateWrapper.eq(WechatConfiguration::getDeleteFlag,0);
            lambdaUpdateWrapper.set(WechatConfiguration::getIsDefault,0);
            update(lambdaUpdateWrapper);
        }
        String username = SecurityUtils.getUsername();
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setCreateUser(username);
        resources.setDeleteFlag(0);

        Boolean b=save(resources);
        if(b){
            RedisUtil.set(redsKey,resources);
        }
        return b;
    }

    @Override
    public Boolean updateWechatConfiguration(WechatConfiguration resources) {
        Integer a= wechatConfigurationMapper.checkedMchid(resources);
        if(a>0){
            throw new BadRequestException("同环境类型下商户Id不能重复");
        }
        WechatConfiguration wechatConfiguration= wechatConfigurationMapper.selectById(resources.getId());
        String redsKey="";
        if(wechatConfiguration.getType().equals("1")){
            redsKey="wechat_routine_"+wechatConfiguration.getMchId();
        }
        if(wechatConfiguration.getType().equals("2")){
            redsKey="wechat_h5_"+wechatConfiguration.getMchId();
        }
        if(wechatConfiguration.getType().equals("3")){
            redsKey="wechat_app_"+wechatConfiguration.getMchId();
        }
        if(wechatConfiguration.getType().equals("4")){
            redsKey="wechat_zhongan_"+wechatConfiguration.getMchId();
        }
        RedisUtil.del(redsKey);


        QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
        wrapper.eq("type",resources.getType());
        wrapper.eq("delete_flag",0);
        wrapper.ne("id",resources.getId());
        Integer typeCount = count(wrapper);

        if(typeCount==0 && resources.getIsDefault()==0){
            throw new BadRequestException("一个类型应用必须有一个默认配置。");
        }
        if(resources.getType().equals("1")){
            redsKey="wechat_routine_"+wechatConfiguration.getMchId();
        }
        if(resources.getType().equals("2")){
            redsKey="wechat_h5_"+wechatConfiguration.getMchId();
        }
        if(resources.getType().equals("3")){
            redsKey="wechat_app_"+wechatConfiguration.getMchId();
        }
        if(resources.getType().equals("4")){
            redsKey="wechat_zhongan_"+wechatConfiguration.getMchId();
        }

        if(resources.getIsDefault()==1){
            LambdaUpdateWrapper<WechatConfiguration> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(WechatConfiguration::getType,resources.getType());
            lambdaUpdateWrapper.eq(WechatConfiguration::getDeleteFlag,0);
            lambdaUpdateWrapper.set(WechatConfiguration::getIsDefault,0);
            update(lambdaUpdateWrapper);
        }
        String username = SecurityUtils.getUsername();
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        resources.setUpdateUser(username);
        Boolean b=updateById(resources);

        if(b){
            wechatConfiguration= wechatConfigurationMapper.selectById(resources.getId());
            RedisUtil.set(redsKey,wechatConfiguration);
        }
        return b;
    }

    @Override
    public void deleteById(Integer id) {
        String username = SecurityUtils.getUsername();
        WechatConfiguration resources= wechatConfigurationMapper.selectById(id);

        String wechatHfiveMchid="";
        String wechatAppletMchid="";
        String wechatAppMchid="";
        String wechatZhonganMchid="";

        String redsKey="";
        if(resources.getType().equals("1")){
            wechatHfiveMchid=resources.getMchId();
            redsKey="wechat_routine_"+resources.getMchId();
        }
        if(resources.getType().equals("2")){
            wechatAppletMchid=resources.getMchId();
            redsKey="wechat_h5_"+resources.getMchId();
        }
        if(resources.getType().equals("3")){
            wechatAppMchid=resources.getMchId();
            redsKey="wechat_app_"+resources.getMchId();
        }
        if(resources.getType().equals("4")){
            wechatZhonganMchid=resources.getMchId();
            redsKey="wechat_zhongan_"+resources.getMchId();
        }
        Integer projectCount= wechatConfigurationMapper.getProjectCountByMchId(wechatHfiveMchid,wechatAppletMchid,wechatAppMchid,wechatZhonganMchid);
        Integer storeCount= wechatConfigurationMapper.getStoreCountByMchId(wechatHfiveMchid,wechatAppletMchid,wechatAppMchid,wechatZhonganMchid);
        if(projectCount!=0){
            throw new BadRequestException("该配置已在项目中使用，无法删除");
        }
        if(storeCount!=0){
            throw new BadRequestException("该配置已在门店中使用，无法删除");
        }

        resources.setDeleteFlag(1);
        resources.setUpdateUser(username);
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        Boolean b= updateById(resources);
        if(b){
            RedisUtil.del(redsKey);
        }
    }

    @Override
    public WxPayMpOrderResult wxRoutinePay(String payOutTradeNo, String openId, String body, int total, String attach, String mchId) throws WxPayException{
        WechatConfiguration resources =RedisUtil.get("wechat_routine_"+mchId);
        if(resources==null){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("mch_id",mchId);
            wrapper.eq("type",1);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        WxPayService wxPayService = getWxPayService(resources);

        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();

        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(payOutTradeNo);
        orderRequest.setTotalFee(total);
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(resources.getNotifyUrl());
        orderRequest.setAttach(attach+"#"+resources.getMchId());

        WxPayMpOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;
    }

    @Override
    public WxPayMwebOrderResult wxH5Pay(String payOutTradeNo, String body, int total, String attach, String mchId) throws WxPayException {
        log.info("h5 mchId:{}",mchId);

        WechatConfiguration resources =RedisUtil.get("wechat_h5_"+mchId);
        if(resources==null){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("mch_id",mchId);
            wrapper.eq("type",2);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        log.info("h5 resources:{}",resources);

        WxPayService wxPayService = getWxPayService(resources);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setDeviceInfo("WEB");
        orderRequest.setTradeType("MWEB");
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(payOutTradeNo);
        orderRequest.setTotalFee(total);
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setNotifyUrl(resources.getNotifyUrl());
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());
        log.info("微信h5 配置："+ JSONObject.toJSONString(wxPayService.getConfig()) +",WxPayUnifiedOrderRequest:"+JSONObject.toJSONString(orderRequest));
        WxPayMwebOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;
    }

    @Override
    public void refundRoutineOrder(String orderId, Integer totalFee,String mchId) throws WxPayException {
        WechatConfiguration resources =RedisUtil.get("wechat_routine_"+mchId);
        if(resources==null){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("mch_id",mchId);
            wrapper.eq("type",1);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }

        WxPayService wxPayService = getWxPayService(resources);
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();

        wxPayRefundRequest.setTotalFee(totalFee);//订单总金额
        wxPayRefundRequest.setOutTradeNo(orderId);
        wxPayRefundRequest.setOutRefundNo(orderId);
        wxPayRefundRequest.setRefundFee(totalFee);//退款金额
        wxPayRefundRequest.setNotifyUrl(resources.getReturnNotifyUrl());

        wxPayService.refund(wxPayRefundRequest);

    }

    @Override
    public void refundH5Order(String orderId, Integer totalFee, String mchId) throws WxPayException {
        WechatConfiguration resources =RedisUtil.get("wechat_h5_"+mchId);
        if(resources==null){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("mch_id",mchId);
            wrapper.eq("type",2);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        WxPayService wxPayService = getWxPayService(resources);
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();

        wxPayRefundRequest.setTotalFee(totalFee);//订单总金额
        wxPayRefundRequest.setOutTradeNo(orderId);
        wxPayRefundRequest.setOutRefundNo(orderId);
        wxPayRefundRequest.setRefundFee(totalFee);//退款金额
        wxPayRefundRequest.setNotifyUrl(resources.getReturnNotifyUrl());

        wxPayService.refund(wxPayRefundRequest);
    }


    /**
     *  getWxPayService
     * @return
     */
    public static WxPayService getWxPayService(WechatConfiguration resources) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(resources.getAppId());
        payConfig.setMchId(resources.getMchId());
        payConfig.setMchKey(resources.getMchKey());
        payConfig.setKeyPath(resources.getKeyPath());
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
