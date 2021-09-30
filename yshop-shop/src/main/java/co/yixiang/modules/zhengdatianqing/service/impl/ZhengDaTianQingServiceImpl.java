package co.yixiang.modules.zhengdatianqing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.modules.shop.domain.YxStoreCart;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.*;

import co.yixiang.modules.shop.service.mapper.StoreProductMapper;
import co.yixiang.modules.zhengdatianqing.dto.ExpressMap;
import co.yixiang.modules.zhengdatianqing.dto.OrderDto;
import co.yixiang.modules.zhengdatianqing.dto.SendOrderDetailDto;
import co.yixiang.modules.zhengdatianqing.dto.SendOrderDto;
import co.yixiang.mp.yiyaobao.domain.OrderBatchnoDetail;
import co.yixiang.mp.yiyaobao.service.OrderBatchnoDetailService;
import co.yixiang.tools.express.ExpressService;
import co.yixiang.tools.express.domain.RouteResponseInfo;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class ZhengDaTianQingServiceImpl {
    @Autowired
    private  YxStoreCartService storeCartService;

    @Autowired
    private YxStoreOrderService storeOrderService;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private RestTemplate restTemplate;

    @Value("${zhengdatianqing.apiUrl}")
    private String apiUrl;

    @Value("${zhengdatianqing.tokenUrl}")
    private String tokenUrl;

    @Value("${zhengdatianqing.appid}")
    private String appid;

    @Value("${zhengdatianqing.app_secret}")
    private String app_secret;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private StoreProductMapper yxStoreProductMapper;


    @Autowired
    private OrderBatchnoDetailService orderBatchnoDetailService;


    @Value("${yiyao.wechatApiUrl}")
    private String yiyao_wechatApiUrl;

    private String yiyao_whchatOrderApi = "/api/order/external";



    public HttpEntity convert(JSONObject jsonObject,String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if(StrUtil.isNotBlank(token)) {
            String plainCreds = token + ":";
            byte[] plainCredsBytes = plainCreds.getBytes();
            String base64Creds = Base64.encodeBase64String(plainCredsBytes);
           // String base64Creds = new String(base64CredsBytes);
            headers.add("Authorization","Basic " + base64Creds);
        }

        if (jsonObject instanceof JSONObject) {
           // JSONObject jsonObject = (JSONObject) dto;
            long ts = System.currentTimeMillis();
            jsonObject.put("nonce_str", ts);

            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> keys = new ArrayList<>();
            jsonObject.forEach((key, value) -> {
                if (!ObjectUtil.isEmpty(value) && !(value instanceof JSONArray)) {

                    keys.add(key);
                }

            });
            keys.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (o1.compareTo(o2) > 0) {
                        return 1;
                    }
                    if (o1.compareTo(o2) < 0) {
                        return -1;
                    }
                    return 0;
                }
            });

            keys.forEach((key) -> {
                Object object = jsonObject.get(key);
                stringBuilder.append(key).append("=").append(object).append("&");
            });
            String sb = stringBuilder.toString();
            sb = sb.substring(0, sb.lastIndexOf("&"));

            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(sb).append("#").append(app_secret);
            String sign = stringBuilder.toString();
            String s1 = DigestUtil.md5Hex(sign);
            String s = DigestUtils.md5DigestAsHex(sign.getBytes());
            jsonObject.put("sign", s.toUpperCase());
            //jsonObject.put("app_secret",app_secret);
            jsonObject.put("sign_type","md5");
        }
        log.info(jsonObject.toString());
        HttpEntity requestEntity = new HttpEntity(jsonObject.toString(), headers);
        return requestEntity;
    }

    public String sendRequest(HttpEntity request,String url){
        String body = "";
        try{
            ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
             body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("sendRequest 返回结果 {}",body);
        }

        return body;
    }

    public String getToken(){
        String access_token = "";
        Object tokenObject = redisUtils.get("zhengdatianqingToken");
        if(tokenObject != null) {
            log.info("从redis中获取初保基金会token={}",(String)tokenObject);
            return (String)tokenObject;
        } else {
            JSONObject json = JSONUtil.createObj();
            json.put("appid", appid);
            HttpEntity request = convert(json, "");

            String body = sendRequest(request, tokenUrl);
            log.info("获取初保基金会token返回的 body={}",body);
            if(StrUtil.isNotBlank(body) && JSONUtil.isJson(body)) {
                JSONObject jsonObject = JSONUtil.parseObj(body);


                if (jsonObject.getInt("code") == 1) {
                    access_token = jsonObject.getJSONObject("data").getStr("access_token");
                    redisUtils.set("zhengdatianqingToken",access_token,1, TimeUnit.HOURS);
                }

                log.info("从获取初保基金会获取token={}",access_token);

            }
            return access_token;
        }
    }

    public List<OrderDto> getOrderList(){
        List<OrderDto> orderDtoList = new ArrayList<>();
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","orderList");
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        log.info("获取慈善赠药的订单结果：{}",body);
        if(JSONUtil.isJson(body)) {
            JSONObject jsonObject2 = JSONUtil.parseObj(body);
            if(jsonObject2.getBool("success")) {
                orderDtoList = jsonObject2.getJSONArray("data").toList(OrderDto.class);
            }
        }


        return orderDtoList;
    }

    public void sendOrder2Yiyao(){
        List<OrderDto> orderDtoList = getOrderList();

        try {
            if(CollUtil.isNotEmpty(orderDtoList)) {
                log.info("获取慈善赠药订单{}个",orderDtoList.size());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                // headers.set("Authorization",token);
                String data = JSONUtil.parseArray(orderDtoList).toString();
                HttpEntity request = new HttpEntity(data, headers);
                String url = yiyao_wechatApiUrl + yiyao_whchatOrderApi;
                log.info("益药公众号下单url={}",url);
                ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
                String body = resultEntity.getBody();
                log.info("向益药公众号下单，结果：{}",body);
            } else {
                log.info("获取慈善赠药订单0个");
            }
        }catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**订单回执*/
    public Boolean orderSynced(String orderNo){
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","orderSynced");
        jsonObject.put("order_sn",orderNo);
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success") ) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",orderNo);
            updateWrapper.set("order_synced_flag",1);
            log.info("订单[]回执成功",orderNo);
            return storeOrderService.update(updateWrapper);
        }
        return false;
    }

    /**
     *  待发货的慈善订单，发送回执给基金会
     * */
    public Boolean orderSyncedLoop(){
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type","慈善赠药");
        queryWrapper.eq("order_synced_flag",0);
        queryWrapper.eq("status",0);
        queryWrapper.eq("paid",1);
        queryWrapper.eq("is_del",0);
        List<YxStoreOrder> orderList = storeOrderService.list(queryWrapper);

        for(YxStoreOrder yxStoreOrder : orderList) {
            orderSynced(yxStoreOrder.getOrderId());
        }

        return true;
    }

    /**确认收货
     * confirm_time 精确到秒，2018-04-02 15:04:03
     * */
    public Boolean confirmOrder(String orderNo,String confirm_time){
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","confirmOrder");
        jsonObject.put("order_sn",orderNo);
        jsonObject.put("confirm_time",confirm_time);
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success") ) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",orderNo);
            updateWrapper.set("confirm_order_flag",1);
            return storeOrderService.update(updateWrapper);
        }
        return false;

    }



    /** 退货
     * confirm_time 精确到秒，2018-04-02 15:04:03
     * */
    public Boolean orderReturn(String orderNo,String return_time){
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","orderReturn");
        jsonObject.put("order_sn",orderNo);
        jsonObject.put("return_time",return_time);
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success") ) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",orderNo);
            updateWrapper.set("order_return_flag",1);
            updateWrapper.set("paid",0);
            updateWrapper.set("refund_status",2);

            return storeOrderService.update(updateWrapper);
        }
        return false;

    }

    /**
     * 用户确认收货的慈善单
     * 已回执，已发货
     *
     * */
    public Boolean confirmOrderLoop(){
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type","慈善赠药");
        queryWrapper.eq("order_synced_flag",1);
        queryWrapper.ge("status",2);
        queryWrapper.eq("paid",1);
        queryWrapper.eq("is_del",0);
        queryWrapper.eq("confirm_order_flag",0);
        List<YxStoreOrder> orderList = storeOrderService.list(queryWrapper);

        for(YxStoreOrder yxStoreOrder : orderList) {
            confirmOrder(yxStoreOrder.getOrderId(),DateUtil.now());
        }
        return true;
    }

    /**订单发货
     *
     * */
    public Boolean orderSend(SendOrderDto sendOrder){
       // JSONObject jsonObject = JSONUtil.parseObj(sendOrder);
        JSONObject jsonObject =  JSONUtil.createObj();
        jsonObject.put("order_sn",sendOrder.getOrder_sn());
        jsonObject.put("tracking_number",sendOrder.getTracking_number());
        jsonObject.put("receipt_number",sendOrder.getReceipt_number());
        jsonObject.put("receive_date",sendOrder.getReceive_date());
        jsonObject.put("details",JSONUtil.parseArray(sendOrder.getDetails()));
        jsonObject.put("method","orderSend");
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        if(JSONUtil.isJson(body)){
            JSONObject jsonObject2 = JSONUtil.parseObj(body);
            if(jsonObject2.getBool("success") ) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("order_id",sendOrder.getOrder_sn());
                updateWrapper.set("order_send_flag",1);
                return storeOrderService.update(updateWrapper);
            }
        }

        return false;
    }


    public Boolean orderSendLoop(){
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pay_type","慈善赠药");
        queryWrapper.eq("order_send_flag",0);
        //queryWrapper.eq("status",1);
        //queryWrapper.eq("paid",1);
        queryWrapper.eq("is_del",0);
       // queryWrapper.ne("delivery_sn","");
        queryWrapper.eq("order_synced_flag",1);
        List<YxStoreOrder> orderList = storeOrderService.list(queryWrapper);
        log.info("向慈善基金发药{}单",orderList.size());
        // 获取当前的药品默认批号
        String batchNo = yxStoreProductMapper.queryDefaultBatchNo("tianqing");
        for(YxStoreOrder yxStoreOrder:orderList) {
            SendOrderDto sendOrderDto = new SendOrderDto();
            sendOrderDto.setOrder_sn(yxStoreOrder.getOrderId());
            sendOrderDto.setTracking_number(yxStoreOrder.getDeliveryId());
            sendOrderDto.setReceipt_number("");
            sendOrderDto.setReceive_date(DateUtil.today());
            List<SendOrderDetailDto> details = new ArrayList<>();

            // 判断是否有人工配置订单对应的药品明细编号
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("order_id",yxStoreOrder.getOrderId());
            List<OrderBatchnoDetail> orderBatchnoDetailList = orderBatchnoDetailService.list(queryWrapper1);

            if(CollUtil.isNotEmpty(orderBatchnoDetailList)) {
                // 获取原始药品药品id
                Integer cartId = Integer.valueOf(yxStoreOrder.getCartId());
                YxStoreCart yxStoreCart = storeCartService.getById(cartId);
                for(OrderBatchnoDetail orderBatchnoDetail:orderBatchnoDetailList) {
                    SendOrderDetailDto sendOrderDetail = new SendOrderDetailDto();
                    sendOrderDetail.setDrug_id(yxStoreCart.getPartnerCode());
                    sendOrderDetail.setCode_list(Arrays.asList(orderBatchnoDetail.getCodeList().split(",")));
                    sendOrderDetail.setQuantity(orderBatchnoDetail.getNum());
                    sendOrderDetail.setSn(orderBatchnoDetail.getBatchno());
                    details.add(sendOrderDetail);
                }
            } else {
                List<YxStoreCart> storeCartList = (List<YxStoreCart>) storeCartService.listByIds(Arrays.asList(yxStoreOrder.getCartId().split(",")));
                for(YxStoreCart yxStoreCart:storeCartList) {
                    SendOrderDetailDto sendOrderDetail = new SendOrderDetailDto();
                    sendOrderDetail.setDrug_id(yxStoreCart.getPartnerCode());
                    sendOrderDetail.setCode_list(new ArrayList<>());
                    sendOrderDetail.setQuantity(yxStoreCart.getCartNum());
                    sendOrderDetail.setSn(batchNo);
                    details.add(sendOrderDetail);

                }
            }



            sendOrderDto.setDetails(details);

            this.orderSend(sendOrderDto);
        }

        return true;
    }

    /**
     * 快递信息
     * @param order_sn 订单号
     * @tracking_number 快递单号
     * */
    public Boolean setExpressInfo(String order_sn,String tracking_number ){

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","setExpressInfo");
        jsonObject.put("order_sn",order_sn);
        jsonObject.put("tracking_number",tracking_number);
        List<ExpressMap> tracesList= new ArrayList<>();
        // 根据快递单号查询物流信息
        try{
            List<RouteResponseInfo.Body.RouteResponse.Route> routeList = expressService.queryWaybillTrace(tracking_number);

            for(RouteResponseInfo.Body.RouteResponse.Route route: routeList ){
                ExpressMap expressMap = new ExpressMap();
                expressMap.setTime(DateUtil.parse(route.getAcceptTime()).getTime() );
                expressMap.setDescription(route.getRemark());
                expressMap.setOperator("系统");
                tracesList.add(expressMap);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        jsonObject.put("map_list",tracesList);

        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        log.info(body);
        if(JSONUtil.isJson(body) ) {
            JSONObject jsonObject2 = JSONUtil.parseObj(body);
            if(jsonObject2.getBool("success") ) {
                return true;
            }
        }

        return false;
    }

    public Boolean setExpressInfoLoop(){
        try{
            QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type","慈善赠药");
           // queryWrapper.eq("order_send_flag",1);
            queryWrapper.eq("order_express_flag",0);
            queryWrapper.eq("paid",1);
            queryWrapper.eq("is_del",0);
            queryWrapper.eq("order_synced_flag",1);
            queryWrapper.isNotNull("delivery_id");
            List<YxStoreOrder> orderList = storeOrderService.list(queryWrapper);
            for(YxStoreOrder yxStoreOrder:orderList) {
                Boolean flag = setExpressInfo(yxStoreOrder.getOrderId(),yxStoreOrder.getDeliveryId());
                if(flag &&  yxStoreOrder.getStatus() >= 2) {
                    yxStoreOrder.setOrderExpressFlag(1);
                    storeOrderService.updateById(yxStoreOrder);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

         return  true;
    }



    /**订单追回回执*/
    public Boolean orderRecover(String orderNo){
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("method","orderRecover");
        jsonObject.put("order_sn",orderNo);
        jsonObject.put("recovered_time",DateUtil.now());
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success") ) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",orderNo);
            updateWrapper.set("order_recover_flag",1);
            return storeOrderService.update(updateWrapper);
        }
        return false;
    }

    /*订单取消*/
    public Boolean cancelOrder4jjh(String orderId) {

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("is_del", OrderInfoEnum.CANCEL_STATUS_1.getValue());
        updateWrapper.set("status", OrderStatusEnum.STATUS_8.getValue());
        updateWrapper.set("upload_gjp_flag",0);
        updateWrapper.eq("order_id",orderId);

        return storeOrderService.update(updateWrapper);
    }

    /*** 订单需要追回*/

    public Boolean recoverOrder4jjh(String orderId) {

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("order_recover_status",1);
        updateWrapper.set("is_del", OrderInfoEnum.CANCEL_STATUS_1.getValue());
        updateWrapper.set("status", OrderStatusEnum.STATUS_8.getValue());
        updateWrapper.eq("order_id",orderId);
        return storeOrderService.update(updateWrapper);

        //  zhengDaTianQingService.orderRecover(orderId);
        // return true;
    }

public void start(){
     // 拉取订单，向公众号下单
    sendOrder2Yiyao();
    // 回执
    orderSyncedLoop();
    // 发货
    orderSendLoop();
    //确认收货
    confirmOrderLoop();
    //物流信息
    setExpressInfoLoop();

}
}
