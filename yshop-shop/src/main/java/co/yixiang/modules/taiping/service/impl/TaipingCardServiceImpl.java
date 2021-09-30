/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import co.yixiang.modules.shop.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.taiping.domain.TaipingCard;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.taiping.service.YxStoreCouponCardService;
import co.yixiang.modules.taiping.service.dto.*;
import co.yixiang.modules.taiping.util.EncryptionToolUtilAes;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.service.mapper.TaipingCardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-11-02
*/
@Slf4j
@Service
//@AllArgsConstructor
//@CacheConfig(cacheNames = "taipingCard")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TaipingCardServiceImpl extends BaseServiceImpl<TaipingCardMapper, TaipingCard> implements TaipingCardService {

    @Autowired
    private  IGenerator generator;

    @Value("${taiping.CipherKey}")
    private String CipherKey;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${taiping.taipingUrlPrefix}")
    private String taipingUrlfix;

    private String checkCustomerUrlSuffix = "/lxjk-wechat-backend/taxPreference/checkCustomer";

    private String sendOrderStatusUrlSuffix = "/lxjk-wechat-backend/taxPreference/orderStatus";

    @Autowired
    private YxStoreCouponCardService couponCardService;

    @Autowired
    @Lazy
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TaipingCardQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TaipingCard> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TaipingCardDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TaipingCard> queryAll(TaipingCardQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TaipingCard.class, criteria));
    }


    @Override
    public void download(List<TaipingCardDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TaipingCardDto taipingCard : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("卡号", taipingCard.getCardNumber());
            map.put("卡的具体种类", taipingCard.getCardType());
            map.put("卡渠道", taipingCard.getSellChannel());
            map.put("代理", taipingCard.getAgentCate());
            map.put("组织ID", taipingCard.getOrganID());
            map.put("乐享同步记录时间", taipingCard.getInsertTime());
            map.put(" createTime",  taipingCard.getCreateTime());
            map.put(" updateTime",  taipingCard.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public String saveCard(TaipingDataDto resource) {
        String dataEncrypt = resource.getData();

        String dataDEC = EncryptionToolUtilAes.decrypt(dataEncrypt, CipherKey);
        JSONObject jsonObject = JSONUtil.createObj();
        TaipingCard card = JSONUtil.toBean(dataDEC,TaipingCard.class);
        log.info("{}",card);
        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("card_number",card.getCardNumber());
            int existsFalg = this.count(queryWrapper);
            if(existsFalg == 0) {
                this.save(card);
            }

            jsonObject.put("status",1);
        }catch (DuplicateKeyException e) {
           // e.printStackTrace();
            jsonObject.put("status",1);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("status",0);
        }

        couponCardService.generateCouponByCardNumber(card.getCardNumber(),new Date());

        return JSONUtil.toJsonStr(jsonObject);
     //   return false;
    }

    @Override
    public Boolean checkCustomer(CheckCustomerDto customer) {

        // 转json，然后加密
        String customer_data_original = JSONUtil.toJsonStr(customer);
        String customer_data_enc = EncryptionToolUtilAes.encrypt(customer_data_original, CipherKey);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("data",customer_data_enc);
        jsonObject.put("callerName","XSYF");

        HttpEntity requestEntity = new HttpEntity(jsonObject.toString(), headers);
        String checkCustomerUrl = taipingUrlfix + checkCustomerUrlSuffix;
        log.info("太平校验客户的url={}",checkCustomerUrl);
        String body = "";
        try{
            log.info("太平校验客户身份：发送数据 {}",jsonObject.toString());
            ResponseEntity<String> resultEntity = restTemplate.exchange(checkCustomerUrl, HttpMethod.POST, requestEntity, String.class);
            body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("太平校验客户身份：返回结果 {}",body);
        }

        if(JSONUtil.isJson(body)) {
           JSONObject result = JSONUtil.parseObj(body);
           int status = result.getInt("status");
           if(status == 1) {
               return true;
           }
        }

        return false;
    }

    @Override
    public Boolean sendOrderStatus(OrderStatusDto orderStatus) {

        // 转json，然后加密
        String orderStatus_data_original = JSONUtil.toJsonStr(orderStatus);
        String orderStatus_data_enc = EncryptionToolUtilAes.encrypt(orderStatus_data_original, CipherKey);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("data",orderStatus_data_enc);
        jsonObject.put("callerName","XSYF");

        HttpEntity requestEntity = new HttpEntity(jsonObject.toString(), headers);
        String sendOrderStatusUrl = taipingUrlfix + sendOrderStatusUrlSuffix;
        log.info("太平更新订单状态的url={}",sendOrderStatusUrl);
        String body = "";
        try{
            log.info("太平订单状态更新：发送数据 {}",jsonObject.toString());
            ResponseEntity<String> resultEntity = restTemplate.exchange(sendOrderStatusUrl, HttpMethod.POST, requestEntity, String.class);
            body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("太平订单状态更新：返回结果 {}",body);
        }

        if(JSONUtil.isJson(body)) {
            JSONObject result = JSONUtil.parseObj(body);
            int status = result.getInt("status");
            if(status == 1) {
                return true;
            }
        }

        return false;
    }


    @Override
    @Async
    public Boolean sendOrderStatus(String orderId,Integer status) {
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",orderId));
        QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("oid",yxStoreOrder.getId());
        List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(wrapper);

        OrderStatusDto orderStatus = new OrderStatusDto();
        orderStatus.setOrderNumber(yxStoreOrder.getTaipingOrderNumber());
        orderStatus.setStatusCode(status);
        orderStatus.setStatusTime(DateUtil.now());

        TaipingOrder order = new TaipingOrder();

        ArrayList detailList = new ArrayList();
        for (YxStoreOrderCartInfo info : cartInfos) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
            int cartNum = cartQueryVo.getCartNum();
            String yiyaobaoSku =  cartQueryVo.getYiyaobaoSku();
            String commonName = cartQueryVo.getProductInfo().getCommonName();
            BigDecimal unitPrice =  new BigDecimal(cartQueryVo.getVipTruePrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
            TaipingOrderDetail detail = new TaipingOrderDetail();
            detail.setTradeName(commonName);
            detail.setCount(cartNum);
            detail.setDrugCode(yiyaobaoSku);
            detail.setUnitPrice(unitPrice);
            detail.setTotalPrice(NumberUtil.mul(unitPrice , cartNum).setScale(2,BigDecimal.ROUND_HALF_UP));
            detailList.add(detail);
        }
        order.setDetails(detailList);
        order.setTotalPrice(yxStoreOrder.getPayPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
        orderStatus.setProductName(JSONUtil.toJsonStr(order));
        this.sendOrderStatus(orderStatus);

        return true;
    }
}
