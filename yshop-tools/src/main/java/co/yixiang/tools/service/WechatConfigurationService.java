/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.tools.domain.WechatConfiguration;
import co.yixiang.tools.service.dto.WechatConfigurationDto;
import co.yixiang.tools.service.dto.WechatConfigurationQueryCriteria;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author zhoujinlai
* @date 2021-09-24
*/
public interface WechatConfigurationService extends BaseService<WechatConfiguration> {

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(WechatConfigurationQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<WechatConfigurationDto>
    */
    List<WechatConfiguration> queryAll(WechatConfigurationQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<WechatConfigurationDto> all, HttpServletResponse response) throws IOException;

    Boolean saveWechatConfiguration(WechatConfiguration resources);

    Boolean updateWechatConfiguration(WechatConfiguration resources);

    void deleteById(Integer id);

     WxPayMpOrderResult wxRoutinePay(String payOutTradeNo, String routineOpenid, String body, int total, String attach, String mchId) throws WxPayException;

    WxPayMwebOrderResult wxH5Pay(String payOutTradeNo, String body, int total, String attach, String appid) throws WxPayException;

    void refundRoutineOrder(String orderId, Integer totalFee,String mchId) throws WxPayException;

    void refundH5Order(String orderId, Integer totalFee,String mchId) throws WxPayException;
}
