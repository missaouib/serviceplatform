package co.yixiang.modules.zhengdatianqing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.AppFromEnum;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.dto.ConfirmOrderDTO;
import co.yixiang.modules.order.web.dto.OtherDTO;
import co.yixiang.modules.order.web.dto.PriceGroupDTO;
import co.yixiang.modules.order.web.param.OrderExternalParam;
import co.yixiang.modules.order.web.param.OrderParam;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.mapper.YxStoreProductMapper;
import co.yixiang.modules.shop.mapping.YxStoreProductMap;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserAddressQueryVo;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.modules.zhengdatianqing.dto.ExpressMap;
import co.yixiang.modules.zhengdatianqing.dto.OrderDto;
import co.yixiang.modules.zhengdatianqing.dto.SendOrderDetailDto;
import co.yixiang.modules.zhengdatianqing.dto.SendOrderDto;
import co.yixiang.mp.yiyaobao.domain.OrderBatchnoDetail;
import co.yixiang.mp.yiyaobao.service.OrderBatchnoDetailService;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.express.ExpressService;
import co.yixiang.tools.express.dao.Traces;
import co.yixiang.tools.express.domain.RouteResponseInfo;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.codec.binary.Base64;

@Service
@Slf4j
public class ZhengDaTianQingServiceImpl {
    @Autowired
    private  YxStoreCartService storeCartService;

    @Autowired
    private YxStoreOrderService storeOrderService;
    @Autowired
    private YxStoreCouponUserService couponUserService;
    @Autowired
    private YxSystemConfigService systemConfigService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private YxUserService yxUserService;
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

    @Value("${file.localUrl}")
    private String localUrl;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private LocalStorageService localStorageService;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private YxStoreProductMapper yxStoreProductMapper;


    @Autowired
    private OrderBatchnoDetailService orderBatchnoDetailService;


    @Autowired
    private QiNiuService qiNiuService;

    private static Lock lock = new ReentrantLock(false);

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**生成订单*/
    public YxStoreOrder createOrder(OrderDto orderDto){

        /*判断是否已经存在订单，如果存在，则退出*/
        QueryWrapper<YxStoreOrder> queryWrapper_order = new QueryWrapper<>();
        queryWrapper_order.eq("order_id",orderDto.getOrder_sn());
        YxStoreOrder order = storeOrderService.getOne(queryWrapper_order);
        if(order != null) {
            return order;
        }

         String realName = orderDto.getName();
         String phone = orderDto.getMobile();
         String province = orderDto.getProvince();
         String city = orderDto.getCity();
         String district = orderDto.getCounty();
         String detail = orderDto.getAddress();
// 转存处方图片

       // LocalStorageDto localStorageDTO = localStorageService.createByUrl(orderDto.getRecipel(),orderDto.getOrder_sn()+".jpg");

        String recipel = orderDto.getRecipel();
        if(recipel.startsWith("https")) {

        } else {
            recipel = recipel.replace("http","https");
        }
        log.info("处方图片的地址：{}",recipel);
     //   String fileName = UUID.randomUUID().toString().replace("-","");
        String fileName = FileUtil.extName(recipel);
      //  QiniuContent qiniuContent = qiNiuService.uploadByUrl(recipel, qiNiuService.find());
        LocalStorageDto localStorageDTO = localStorageService.createByUrl(recipel,fileName);
       /* StringBuilder url = new StringBuilder();
        if ("".equals(url.toString())) {
            url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
        } else {
            url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
        }*/
        // String url = qiniuContent.getUrl();
       //  String imagePath= qiniuContent.getUrl();
        String imagePath = localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName();
        Integer uid = 0;

        Integer cartNum = orderDto.getDetails().get(0).getQuantity();
        //获取指定药品信息
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del",0);
        queryWrapper.eq("store_name","平适");
        YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper,false);
        if(yxStoreProduct == null) {
            return null;
        }
        Integer productId = yxStoreProduct.getId();

        // 获取广州药房的id
        QueryWrapper<YxSystemStore> queryWrapper2 = new QueryWrapper();

        queryWrapper2.and(wrapper -> wrapper.eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));

        YxSystemStore systemStore = yxSystemStoreService.getOne(queryWrapper2);
        QueryWrapper<YxStoreProductAttrValue> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("product_id",productId);
        queryWrapper1.eq("store_id",systemStore.getId());
        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(queryWrapper1,false);
        if(yxStoreProductAttrValue == null) {
            return null;
        }
        String uniqueId = yxStoreProductAttrValue.getUnique();
        Integer isNew = 1;
        Integer combinationId = 0;
        Integer seckillId=0;
        Integer bargainId=0;
        Integer storeId=yxStoreProductAttrValue.getStoreId();
        String departmentCode="";
        String partnerCode=orderDto.getDetails().get(0).getDrug_id();
        String refereeCode="";

        YxUser yxUser = yxUserService.getOne(new QueryWrapper<YxUser>().eq("phone",phone),false);
        if(yxUser == null) {
            uid = 10000000 + Long.valueOf(redisUtils.incr("patient",1)).intValue();

            //用户保存
            YxUser user = new YxUser();
            user.setAccount(uid.toString());


            user.setUsername(uid.toString());

            user.setPassword(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPwd(passwordEncoder.encode(ShopConstants.YSHOP_DEFAULT_PWD));
            user.setPhone("");
            user.setUserType(AppFromEnum.WECHAT.getValue());
            user.setLoginType(AppFromEnum.WECHAT.getValue());
            user.setAddTime(OrderUtil.getSecondTimestampTwo());
            user.setLastTime(OrderUtil.getSecondTimestampTwo());
            user.setNickname(uid.toString());
            user.setAvatar("");
            user.setNowMoney(BigDecimal.ZERO);
            user.setBrokeragePrice(BigDecimal.ZERO);
            user.setIntegral(BigDecimal.ZERO);

            yxUserService.save(user);

            uid = user.getUid();
        } else {
            uid = yxUser.getUid();
        }

        // 添加购物车
       Integer storeCartId = storeCartService.addCart(uid,productId,cartNum,uniqueId
                ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,"");

        // 订单确认
        String cartId = String.valueOf(storeCartId);

        String cardNumber = "";
        String cardNo = "";
        List<StoreCartVo> storeCartVoList = storeCartService.getUserProductCartList4Store(uid,cartId,1,"",cardNumber,cardNo,null);

        Map<String, Object> cartGroup = storeCartVoList.get(0).getInfo();

        if(ObjectUtil.isNotEmpty(cartGroup.get("invalid"))){
            log.error("有失效的商品请重新提交");
            return order;
        }
        if(ObjectUtil.isEmpty(cartGroup.get("valid"))){
            log.error("请提交购买的商品");
            return order;
        }
        List<YxStoreCartQueryVo> cartInfo = (List<YxStoreCartQueryVo>)cartGroup.get("valid");
        PriceGroupDTO priceGroup = storeOrderService.getOrderPriceGroup(cartInfo);

        ConfirmOrderDTO confirmOrderDTO = new ConfirmOrderDTO();

        confirmOrderDTO.setUsableCoupon(couponUserService
                .beUsableCoupon(uid,priceGroup.getTotalPrice()));
        //积分抵扣
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));
        other.setStoreId(storeId);
        other.setProjectCode(ProjectNameEnum.CSZY.getValue());
        String orderKey = storeOrderService.cacheOrderInfo(uid,cartInfo,
                priceGroup,other);


        // 收货地址
        YxUserAddress userAddress = new YxUserAddress();
        userAddress.setRealName(realName);
        userAddress.setPhone(phone);
        userAddress.setProvince(province);
        userAddress.setCity(city);
        userAddress.setDistrict(district);
        userAddress.setDetail(detail);
        userAddress.setUid(uid);
        yxUserAddressService.save(userAddress);


        OrderParam param = new OrderParam();
        param.setAddressId(userAddress.getId().toString());
        param.setIsChannel(OrderInfoEnum.PAY_CHANNEL_2.getValue());
        param.setBargainId(0);
        param.setCombinationId(0);
        param.setCouponId(0);
        param.setFrom(AppFromEnum.CSZY.getValue());
        param.setImagePath(imagePath);
        param.setMark("");
        param.setPayType("慈善赠药");
        param.setPhone(phone);
        param.setPinkId(0);
        param.setRealName(realName);
        param.setSeckillId(0);
        param.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
        param.setStoreId(systemStore.getId());
        param.setOrderNo(orderDto.getOrder_sn());
        param.setUseIntegral(0d);
        param.setProjectCode(ProjectNameEnum.CSZY.getValue());
       /* OrderExternalParam param = new OrderExternalParam();
        param.setImagePath(imagePath);
        param.setPayType("慈善赠药");
        param.setAddress(userAddress);
        param.setMark("正大天晴");
        param.setType("慈善赠药");
        param.setOrderNo(orderDto.getOrder_sn());*/
        //创建订单

        try{
            lock.lock();
            order = storeOrderService.createOrder4Store(uid,orderKey,param);
        }finally {
            lock.unlock();
        }

        // 订单回执
        if(order != null) {
            orderSynced(order.getOrderId());
        }

        return order;
    }


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
                if (Objects.nonNull(value) && !(value instanceof JSONArray)) {

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

        ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String body = resultEntity.getBody();
        log.info("sendRequest 返回结果 {}",body);
        return body;
    }

    public String getToken(){

        Object tokenObject = redisUtils.get("zhengdatianqingToken");
        if(tokenObject != null) {
            return (String)tokenObject;
        } else {
            JSONObject json = JSONUtil.createObj();
            json.put("appid", appid);
            HttpEntity request = convert(json, "");

            String body = sendRequest(request, tokenUrl);

            JSONObject jsonObject = JSONUtil.parseObj(body);
            String access_token = "";

            if (jsonObject.getInt("code") == 1) {
                access_token = jsonObject.getJSONObject("data").getStr("access_token");
                redisUtils.set("zhengdatianqingToken",access_token,1, TimeUnit.HOURS);
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
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success")) {
            orderDtoList = jsonObject2.getJSONArray("data").toList(OrderDto.class);
        }

        return orderDtoList;
    }

    public void createOrderLoop(List<OrderDto> orderDtoList) {
        for(OrderDto orderDto:orderDtoList) {
            YxStoreOrder yxStoreOrder = createOrder(orderDto);
            if(yxStoreOrder != null) {
                log.info("原始订单号{}已经生成订单{}",orderDto.getOrder_sn(),yxStoreOrder.getOrderId());
            }else{
                log.info("原始订单号{}生成订单失败",orderDto.getOrder_sn());
            }
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
            log.info("订单[{}]回执成功",orderNo);
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
        if(JSONUtil.isJson(body)) {
            JSONObject jsonObject2 = JSONUtil.parseObj(body);
            if(jsonObject2.getBool("success") ) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("order_id",orderNo);
                updateWrapper.set("confirm_order_flag",1);
                return storeOrderService.update(updateWrapper);
            }
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
        JSONObject jsonObject = JSONUtil.parseObj(sendOrder);
        jsonObject.put("method","orderSend");
        String token = getToken();
        HttpEntity request = convert(jsonObject,token);
        String body = sendRequest(request,apiUrl);
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        if(jsonObject2.getBool("success") ) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("order_id",sendOrder.getOrder_sn());
            updateWrapper.set("order_send_flag",1);
            return storeOrderService.update(updateWrapper);
        }
        return false;
    }


    public Boolean orderSendLoop(){
        QueryWrapper<YxStoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type","慈善赠药");
        queryWrapper.eq("order_send_flag",0);
        queryWrapper.eq("status",1);
        queryWrapper.eq("paid",1);
        queryWrapper.eq("is_del",0);
        queryWrapper.eq("order_synced_flag",1);
        List<YxStoreOrder> orderList = storeOrderService.list(queryWrapper);
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
                for(OrderBatchnoDetail orderBatchnoDetail:orderBatchnoDetailList) {
                    SendOrderDetailDto sendOrderDetail = new SendOrderDetailDto();
                    sendOrderDetail.setDrug_id(orderBatchnoDetail.getProductId());
                    sendOrderDetail.setCode_list(Arrays.asList(orderBatchnoDetail.getCodeList().split(",")));
                    sendOrderDetail.setQuantity(orderBatchnoDetail.getNum());
                    sendOrderDetail.setSn(orderBatchnoDetail.getBatchno());
                    details.add(sendOrderDetail);
                }
            } else {
                List<YxStoreCart> storeCartList = (List<YxStoreCart>) storeCartService.listByIds(Arrays.asList(yxStoreOrder.getCartId().split(",")));
                for(YxStoreCart yxStoreCart:storeCartList) {
                    SendOrderDetailDto sendOrderDetail = new SendOrderDetailDto();
                    sendOrderDetail.setDrug_id(yxStoreCart.getPartnerId());
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

        return false;
    }

    public Boolean setExpressInfoLoop(){
return  true;
    }


}
