/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.service.impl;

import cn.hutool.json.JSONObject;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.tools.domain.AlipayConfiguration;
import co.yixiang.tools.service.AlipayConfigurationService;
import co.yixiang.tools.service.dto.AlipayConfigurationDto;
import co.yixiang.tools.service.dto.AlipayConfigurationQueryCriteria;
import co.yixiang.tools.service.mapper.AlipayConfigurationMapper;
import co.yixiang.tools.utils.AlipayProperties;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2021-09-01
*/
@Slf4j
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "alipayConfiguration")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AlipayConfigurationServiceImpl extends BaseServiceImpl<AlipayConfigurationMapper, AlipayConfiguration> implements AlipayConfigurationService {

    private final IGenerator generator;

    @Autowired
    private AlipayConfigurationMapper alipayConfigurationMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(AlipayConfigurationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<AlipayConfiguration> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), AlipayConfigurationDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<AlipayConfiguration> queryAll(AlipayConfigurationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(AlipayConfiguration.class, criteria));
    }


    @Override
    public void download(List<AlipayConfigurationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AlipayConfigurationDto alipayConfiguration : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("1：小程序 2：H5", alipayConfiguration.getType());
            map.put("应用名称", alipayConfiguration.getName());
            map.put("支付宝appid", alipayConfiguration.getAppId());
            map.put(" format",  alipayConfiguration.getFormat());
            map.put(" charset",  alipayConfiguration.getCharset());
            map.put(" signType",  alipayConfiguration.getSignType());
            map.put("支付宝网关", alipayConfiguration.getServerUrl());
            map.put("应用网关 ，回调接口", alipayConfiguration.getNotifyUrl());
            map.put("授权回调地址", alipayConfiguration.getReturnUrl());
            map.put("私钥", alipayConfiguration.getPrivateKey());
            map.put("支付宝应用公钥", alipayConfiguration.getPublicKey());
            map.put("创建时间", alipayConfiguration.getCreateTime());
            map.put("创建人", alipayConfiguration.getCreateUser());
            map.put("更新时间", alipayConfiguration.getUpdateTime());
            map.put("更新人", alipayConfiguration.getUpdateUser());
            map.put("删除表示  1：已删除  0 未删除", alipayConfiguration.getDeleteFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean saveAlipayConfiguration(AlipayConfiguration resources) {
        Integer a= alipayConfigurationMapper.checkedAppId(resources);
        if(a>0){
            throw new BadRequestException("appId不能重复");
        }

        QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
        wrapper.eq("type",resources.getType());
        wrapper.eq("delete_flag",0);
        Integer typeCount = count(wrapper);
        if(typeCount==0){
            resources.setIsDefault(1);
        }

        if(resources.getIsDefault()==1){
            LambdaUpdateWrapper<AlipayConfiguration> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(AlipayConfiguration::getType,resources.getType());
            lambdaUpdateWrapper.eq(AlipayConfiguration::getDeleteFlag,0);
            lambdaUpdateWrapper.set(AlipayConfiguration::getIsDefault,0);
            update(lambdaUpdateWrapper);
        }
        String username = SecurityUtils.getUsername();
        resources.setFormat("json");
        resources.setCharset("UTF-8");
        resources.setSignType("RSA2");
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setCreateUser(username);
        resources.setDeleteFlag(0);

        Boolean b=save(resources);
        if(b){

            RedisUtil.set("alipay_"+resources.getAppId(),resources);
        }
        return b;
    }

    @Override
    public Boolean updateAlipayConfiguration(AlipayConfiguration resources) {
        Integer a= alipayConfigurationMapper.checkedAppId(resources);
        if(a>0){
            throw new BadRequestException("appId不能重复");
        }

        QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
        wrapper.eq("type",resources.getType());
        wrapper.eq("delete_flag",0);
        wrapper.ne("id",resources.getId());
        Integer typeCount = count(wrapper);

        if(typeCount==0 && resources.getIsDefault()==0){
            throw new BadRequestException("一个类型应用必须有一个默认配置。");
        }

        if(resources.getIsDefault()==1){
            LambdaUpdateWrapper<AlipayConfiguration> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(AlipayConfiguration::getType,resources.getType());
            lambdaUpdateWrapper.eq(AlipayConfiguration::getDeleteFlag,0);
            lambdaUpdateWrapper.set(AlipayConfiguration::getIsDefault,0);
            update(lambdaUpdateWrapper);
        }
        String username = SecurityUtils.getUsername();
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        resources.setUpdateUser(username);
        Boolean b=updateById(resources);
        if(b){
            resources= alipayConfigurationMapper.selectById(resources.getId());
            RedisUtil.set("alipay_"+resources.getAppId(),resources);
        }
        return b;
    }

    @Override
    public void deleteById(Integer id) {
        String username = SecurityUtils.getUsername();
        AlipayConfiguration resources= alipayConfigurationMapper.selectById(id);
        resources.setDeleteFlag(1);
        resources.setUpdateUser(username);
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        Boolean b= updateById(resources);
        if(b){
            RedisUtil.del("alipay_"+resources.getAppId());
        }
    }


    /**
     * 支付宝 h5 支付
     * @param body
     * @param subject
     * @param orderId
     * @param timeoutExpress
     * @param totalAmount
     * @param appId
     * @return
     */
    @Override
    public String alipayH5Pay(String body, String subject, String outTradeNo , String timeoutExpress, String totalAmount,String appId,String orderId) {
        AlipayConfiguration resources =RedisUtil.get("alipay_"+appId);
        if(resources==null){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id",appId);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        log.info("金额totalAmount:" + totalAmount);
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(resources.getServerUrl(),appId, resources.getPrivateKey(), resources.getFormat(), resources.getCharset(), resources.getPublicKey(), resources.getSignType());
        log.info("AlipayClient :" + com.alibaba.fastjson.JSONObject.toJSONString(alipayClient));
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeWapPayRequest alipayRequest=new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setSubject(resources.getName());
        model.setTotalAmount(totalAmount);
        model.setBody(body);
        model.setProductCode("QUICK_WAP_PAY");
        model.setPassbackParams("pay_product#"+appId);
        model.setTimeoutExpress(timeoutExpress);
        alipayRequest.setBizModel(model);
        // 设置异步通知地址
        alipayRequest.setNotifyUrl(resources.getNotifyUrl());
        alipayRequest.setReturnUrl(resources.getReturnUrl()+"?orderId=" + orderId+"&price="+totalAmount);
        log.info("AlipayTradeWapPayRequest  :" + com.alibaba.fastjson.JSONObject.toJSONString(alipayRequest));

        try {
            String form = alipayClient.pageExecute(alipayRequest).getBody();
            log.info("alipayClient.pageExecute.getBody() from :" + form);
            return form;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 支付宝小程序支付
     * @param subject
     * @param userid
     * @param outTradeNo
     * @param timeoutExpress
     * @param totalAmount
     * @param appId
     * @return
     */
    @Override
    public String alipayTradeAppPay(String subject, String userid, String outTradeNo, String timeoutExpress, String totalAmount,String appId) {
        AlipayConfiguration resources =RedisUtil.get("alipay_"+appId);
        if(resources==null){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id",appId);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }

        AlipayClient alipayClient = new DefaultAlipayClient(resources.getServerUrl(),appId, resources.getPrivateKey(), resources.getFormat(), resources.getCharset(), resources.getPublicKey(), resources.getSignType());
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。

        com.alibaba.fastjson.JSONObject json=new com.alibaba.fastjson.JSONObject();
        //订单号
        json.put("out_trade_no",outTradeNo);
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",totalAmount);
        //描述
        json.put("subject",resources.getName());
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",userid);
        //回传参数
        json.put("passback_params","pay_product#"+appId);
        //对象转化为json字符串
        String jsonStr=json.toString();

        request.setBizContent(jsonStr);
        request.setNotifyUrl(AlipayProperties.notifyUrl);

        try {
            AlipayTradeCreateResponse response = alipayClient.execute(request);
            String trade_no = response.getTradeNo();// 获取返回的tradeNO。
            log.info("trade_no:"+trade_no);
            return trade_no;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 支付宝退款
     * @param tradeNo
     * @param refundAmount
     * @param appId
     * @return
     */
    @Override
    public String refundOrder(String tradeNo, String refundAmount, String appId) {
        AlipayConfiguration resources =RedisUtil.get("alipay_"+appId);
        if(resources==null){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id",appId);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        log.info("开始调用支付宝退款接口******************************************************");
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(resources.getServerUrl(),appId, resources.getPrivateKey(), resources.getFormat(), resources.getCharset(), resources.getPublicKey(), resources.getSignType());
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setTradeNo(tradeNo);
        model.setRefundAmount(refundAmount);
        alipay_request.setBizModel(model);
        alipay_request.setNotifyUrl(resources.getNotifyUrl());
        log.info("支付宝退款 refundOrder  :" + com.alibaba.fastjson.JSONObject.toJSONString(alipay_request));
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(alipay_request);
            log.info("支付宝退款msg"+response.getMsg() + "\n");
            log.info("支付宝退款body"+response.getBody());
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("支付宝退款错误！", e.getMessage());
            return null;
        }
    }

    public String getUserId(String auth_code,String appId) {
        AlipayConfiguration resources =RedisUtil.get("alipay_"+appId);
        if(resources==null){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id",appId);
            wrapper.eq("delete_flag",0);
            resources = getOne(wrapper);
        }
        AlipayClient alipayClient = new DefaultAlipayClient(resources.getServerUrl(),appId, resources.getPrivateKey(), resources.getFormat(), resources.getCharset(), resources.getPublicKey(), resources.getSignType());
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        // 值为authorization_code时，代表用code换取
        request.setGrantType("authorization_code");
        //授权码，用户对应用授权后得到的
        request.setCode(auth_code);
        //这里使用execute方法
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //刷新令牌，上次换取访问令牌时得到。见出参的refresh_token字段
            request.setRefreshToken(response.getAccessToken());
            if (response.isSuccess()) {
                return response.getUserId();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
