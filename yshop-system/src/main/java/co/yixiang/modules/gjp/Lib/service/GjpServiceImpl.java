package co.yixiang.modules.gjp.Lib.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.enums.OrderTypeEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.gjp.Lib.*;

import co.yixiang.modules.gjp.Lib.HttpRequest;
import co.yixiang.modules.gjp.Lib.vo.GjpNotifyDTO;
import co.yixiang.modules.gjp.Lib.vo.GjpResultVo;
import co.yixiang.modules.gjp.Lib.vo.SelfbuiltmallproductVo;
import co.yixiang.modules.gjp.Lib.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GjpServiceImpl {

    @Autowired
    private YxStoreOrderStatusService yxStoreOrderStatusService;
    @Autowired
    private YxWechatUserService wechatUserService;

    @Autowired
    private YxTemplateService templateService;

    @Autowired
    private  RedisTemplate<String, String> redisTemplate;

    @Autowired
    private YxExpressService yxExpressService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    private YxStoreProductService yxStoreProductService;
    @Autowired
    private Config config;

    @Autowired
    private ApiGetToken apiGetToken;

    @Autowired
    private ZhengDaTianQingServiceImpl zhengDaTianQingService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;

    @Value("${yiyaoShop.apiUrl}")
    private String yiyaoShopApiUrl;

    @Autowired
    private YxStoreOrderService storeOrderService;

     public UploadSaleOrdersRequest transforOrder() {

         QueryWrapper queryWrapper = new QueryWrapper();
         queryWrapper.eq("paid",OrderInfoEnum.PAY_STATUS_1.getValue());
         queryWrapper.eq("upload_gjp_flag",0);
         queryWrapper.eq("is_del",0);
         List<YxStoreOrder> stordOrderList = yxStoreOrderService.list(queryWrapper);



         UploadSaleOrdersRequest gjpOrder = new UploadSaleOrdersRequest();
         gjpOrder.ShopKey = config.getShop_key();
         List<SaleOrderEntity> orderList = new ArrayList<SaleOrderEntity>();

         for(YxStoreOrder orderInfo : stordOrderList) {

           //  orderInfo = yxStoreOrderService.handleOrder(orderInfo);

             QueryWrapper queryWrapper1 = new QueryWrapper();
             queryWrapper1.eq("oid",orderInfo.getId());
             List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(queryWrapper1);

            /* List<StoreOrderCartInfoDTO> cartInfoDTOS = new ArrayList<>();
             for (StoreOrderCartInfo cartInfo : cartInfos) {
                 StoreOrderCartInfoDTO cartInfoDTO = new StoreOrderCartInfoDTO();
                 cartInfoDTO.setCartInfoMap(JSON.parseObject(cartInfo.getCartInfo()));

                 cartInfoDTOS.add(cartInfoDTO);
             }*/
           //  orderInfo.setCartInfoList(cartInfoDTOS);
             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
             Date now = new Date();
             String time = dateFormat.format(now);


             SaleOrderEntity saleorder = new SaleOrderEntity();
             saleorder.setEShopBuyer(new BuyerEntity()) ;
             // ???????????????
             saleorder.EShopBuyer.setCustomerReceiver(orderInfo.getRealName());

             //???????????????
             saleorder.EShopBuyer.setCustomerReceiverMobile(orderInfo.getUserPhone());
             saleorder.EShopBuyer.setCustomerReceiverPhone(orderInfo.getUserPhone());

             //????????????????????? ,?????????
             List<String> addresslist = StrUtil.split(orderInfo.getUserAddress(), " ", 4 ,true, true);
             saleorder.EShopBuyer.setCustomerReceiverCountry("china");
             saleorder.EShopBuyer.setCustomerReceiverProvince(addresslist.get(0));
             saleorder.EShopBuyer.setCustomerReceiverCity(addresslist.get(1));
             saleorder.EShopBuyer.setCustomerReceiverDistrict(addresslist.get(2));
             saleorder.EShopBuyer.setCustomerReceiverAddress(addresslist.get(3));


             //??????????????????
             saleorder.setPreferentialTotal(orderInfo.getDeductionPrice().floatValue());

             // ????????????  ????????????0 ems???1 ????????? 2 ????????? 3 ?????????4  ?????????5 ??????????????? :6 ?????????????????? :7 ????????????
             saleorder.setShippingType(1);

             // ??????????????????
             String tradeCreateTime = dateFormat.format(new Date(orderInfo.getAddTime() * 1000L));
             saleorder.setTradeCreateTime(tradeCreateTime);

             //??????????????????
             String tradePaiedTime = dateFormat.format(new Date(orderInfo.getPayTime() * 1000L));
             saleorder.setTradePaiedTime(tradePaiedTime);

             //??????????????????????????? -1=??????,1= ??????????????????2=??????????????????3=??????????????????4=?????????????????????5=??????????????????6=???????????????
             saleorder.setTradeStatus(2);
             //???????????????
             saleorder.setTradeTotal(orderInfo.getTotalPrice().floatValue());
             //???????????? ???0=?????????1=?????????2=?????????3=???????????????
             saleorder.setTradeType(0);

             //?????????
             saleorder.setTradeId(orderInfo.getOrderId());

             //??????????????????
             saleorder.setTotal(orderInfo.getPayPrice().floatValue());

             //??????????????????
             saleorder.setPaiedTotal(orderInfo.getPayPrice().floatValue());

             saleorder.setOrderDetails(new ArrayList<SaleOrderDetailEntity>());


             cartInfos.forEach(cart->{

                 String cartInfo_str = cart.getCartInfo();
                 YxStoreCartQueryVo cartInfo = JSONUtil.toBean(cartInfo_str,YxStoreCartQueryVo.class);

                 SaleOrderDetailEntity detail = new SaleOrderDetailEntity();
                 //????????????
                 String productName = cartInfo.getProductInfo().getStoreName();
                 detail.setProductName(productName);
                 //????????????ID
                 detail.setPtypeId(String.valueOf(cartInfo.getProductId()));
                 //??????
                 detail.setQty(cartInfo.getCartNum());
                 //?????????????????????
                 detail.setTradeOriginalPrice( cartInfo.getProductInfo().getPrice().floatValue());
                 //????????????
                 detail.setpreferentialtotal(0);
                 //Sku????????????
                 detail.setPlatformPropertiesName(cartInfo.getProductAttrUnique());

                 //????????????????????????????????????????????????????????????????????????
                 detail.setOid(String.valueOf(cartInfo.getId()));

                 saleorder.OrderDetails.add(detail);

             });

             orderList.add(saleorder);
         }


         gjpOrder.orders=orderList;

        return gjpOrder;

     };

    public Boolean DoUploadSaleOrders()  throws Exception {
        String ret = "";

        // ?????????????????? ??????????????????????????????????????????
        QueryWrapper queryWrapper = new QueryWrapper();
      //  queryWrapper.eq("paid",OrderInfoEnum.PAY_STATUS_1.getValue());
        queryWrapper.eq("upload_gjp_flag",0);
        // queryWrapper.eq("is_del",0);
        queryWrapper.eq("pay_type", "????????????");
       // queryWrapper.eq("status",0);
        List<YxStoreOrder> stordOrderList = yxStoreOrderService.list(queryWrapper);
        if(CollectionUtil.isEmpty(stordOrderList)) {
            return false;
        }

        // ????????????????????????????????????????????????
        UploadSaleOrdersRequest gjpOrder = new UploadSaleOrdersRequest();

        gjpOrder.ShopKey = config.getShop_key();
        List<SaleOrderEntity> orderList = new ArrayList<SaleOrderEntity>();

        for(YxStoreOrder orderInfo : stordOrderList) {
            //  orderInfo = yxStoreOrderService.handleOrder(orderInfo);
            // ????????????id????????????????????????????????????

            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("oid",orderInfo.getId());
            List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(queryWrapper1);
            /* List<StoreOrderCartInfoDTO> cartInfoDTOS = new ArrayList<>();
             for (StoreOrderCartInfo cartInfo : cartInfos) {
                 StoreOrderCartInfoDTO cartInfoDTO = new StoreOrderCartInfoDTO();
                 cartInfoDTO.setCartInfoMap(JSON.parseObject(cartInfo.getCartInfo()));

                 cartInfoDTOS.add(cartInfoDTO);
             }*/
            //  orderInfo.setCartInfoList(cartInfoDTOS);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
            Date now = new Date();
            String time = dateFormat.format(now);


            SaleOrderEntity saleorder = new SaleOrderEntity();
            saleorder.setEShopBuyer(new BuyerEntity()) ;
            // ???????????????
            saleorder.EShopBuyer.setCustomerReceiver(orderInfo.getRealName());

            //???????????????
            saleorder.EShopBuyer.setCustomerReceiverMobile(orderInfo.getUserPhone());
            saleorder.EShopBuyer.setCustomerReceiverPhone(orderInfo.getUserPhone());

            //????????????????????? ,?????????
            List<String> addresslist = StrUtil.split(orderInfo.getUserAddress(), " ", 4 ,true, true);
            saleorder.EShopBuyer.setCustomerReceiverCountry("china");
            saleorder.EShopBuyer.setCustomerReceiverProvince(addresslist.get(0));
            saleorder.EShopBuyer.setCustomerReceiverCity(addresslist.get(1));
            saleorder.EShopBuyer.setCustomerReceiverDistrict(addresslist.get(2));
            saleorder.EShopBuyer.setCustomerReceiverAddress(addresslist.get(3));


            //??????????????????
            saleorder.setPreferentialTotal(orderInfo.getDeductionPrice().floatValue());

            // ????????????  ????????????0 ems???1 ????????? 2 ????????? 3 ?????????4  ?????????5 ??????????????? :6 ?????????????????? :7 ????????????
            saleorder.setShippingType(1);

            // ??????????????????
            String tradeCreateTime = dateFormat.format(new Date(orderInfo.getAddTime() * 1000L));
            saleorder.setTradeCreateTime(tradeCreateTime);

            //??????????????????
            String tradePaiedTime = dateFormat.format(new Date(orderInfo.getAddTime() * 1000L));
            saleorder.setTradePaiedTime(tradePaiedTime);

            //??????????????????????????? -1=??????,1= ??????????????????2=??????????????????3=??????????????????4=?????????????????????5=??????????????????6=???????????????
            if(orderInfo.getIsDel() == 1 ) {
                saleorder.setTradeStatus(5);
            } else {
                saleorder.setTradeStatus(2);
            }

            //???????????????
            saleorder.setTradeTotal(orderInfo.getTotalPrice().floatValue());
            //???????????? ???0=?????????1=?????????2=?????????3=???????????????
            saleorder.setTradeType(0);

            //?????????
            saleorder.setTradeId(orderInfo.getOrderId());

            //??????????????????
            saleorder.setTotal(orderInfo.getPayPrice().floatValue());

            //??????????????????
            saleorder.setPaiedTotal(orderInfo.getPayPrice().floatValue());

            saleorder.setOrderDetails(new ArrayList<SaleOrderDetailEntity>());


            cartInfos.forEach(cart->{

                String cartInfo_str = cart.getCartInfo();
                YxStoreCartQueryVo cartInfo = JSONUtil.toBean(cartInfo_str,YxStoreCartQueryVo.class);

                SaleOrderDetailEntity detail = new SaleOrderDetailEntity();
                //????????????
                String productName = cartInfo.getProductInfo().getStoreName();
                detail.setProductName(productName);
                //????????????ID
                detail.setPtypeId(String.valueOf(cartInfo.getProductId()));
                //??????
                detail.setQty(cartInfo.getCartNum());
                //?????????????????????
                detail.setTradeOriginalPrice( cartInfo.getProductInfo().getPrice().floatValue());
                //????????????
                detail.setpreferentialtotal(0);
                //Sku????????????
               // detail.setPlatformPropertiesName(cartInfo.getProductAttrUnique());

                //????????????????????????????????????????????????????????????????????????
                detail.setOid(String.valueOf(cartInfo.getId()));

                saleorder.OrderDetails.add(detail);

            });

            orderList.add(saleorder);
        }


        gjpOrder.orders=orderList;

        log.info("gjpOrder={}",gjpOrder);

        if(gjpOrder == null) {
            return false;
        }

        //??????token
        String token = getToken();

        //??????
        Map<String, String> param;
        try {
            param = GetPostParams(gjpOrder, token, config.getAppkey(), config.getSign_key());
            //post??????
            String postString = "";
            for (String in : param.keySet()) {
                postString += in + "=" + URLEncoder.encode(param.get(in),"utf-8")  +"&";
            }
            postString = postString.substring(0, postString.length() - 1);
            ret =  HttpRequest.sendPost(config.getApi_link(), postString);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(StrUtil.isNotBlank(ret)) {

            log.info("upload order to gjp {}",ret);

            GjpResultVo gjpResultVo =  JSONUtil.toBean(ret,GjpResultVo.class);
            Boolean result = gjpResultVo.getResponse().getIssuccess();

            if(result) {
                stordOrderList.forEach(order -> {
                    order.setUploadGjpFlag(1);
                    yxStoreOrderService.updateById(order);
                });
                return true;
            } else {
                return false;
            }


        } else {
            return  false;
        }


    }

    public  Map<String, String> GetPostParams(UploadSaleOrdersRequest order, String token, String appKey, String signKey) throws NoSuchAlgorithmException {
        Map<String, String> txtParams = new HashMap<String, String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//???????????????????????????????????????????????
        Date now = new Date();
        String time = dateFormat.format(now);


        BuildOrderParams(order, txtParams);

        txtParams.put("method", order.GetApiName());
        txtParams.put("appkey", appKey);
        txtParams.put("timestamp", time);
        txtParams.put("token", token);
        //
        AESCoder coder = new AESCoder();
        txtParams.put("sign", coder.SignRequest(txtParams, signKey));
        return txtParams;
    }

    public  void BuildOrderParams(UploadSaleOrdersRequest order, Map<String, String> parameters) {
        parameters.put("shopkey", config.getShop_key());
        parameters.put("orders",  JSONArray.fromObject(order.orders).toString());
    }

    public  void BuildOrderParams(UploadProductRequest productRequest, Map<String, String> parameters) {
        parameters.put("shopkey", config.getShop_key());
        parameters.put("products",  JSONArray.fromObject(productRequest.products).toString());
    }

    public  Map<String, String> GetPostParams(UploadProductRequest productRequest, String token, String appKey, String signKey) throws NoSuchAlgorithmException {
        Map<String, String> txtParams = new HashMap<String, String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//???????????????????????????????????????????????
        Date now = new Date();
        String time = dateFormat.format(now);


        BuildOrderParams(productRequest, txtParams);

        txtParams.put("method", productRequest.GetApiName());
        txtParams.put("appkey", appKey);
        txtParams.put("timestamp", time);
        txtParams.put("token", token);
        //
        AESCoder coder = new AESCoder();
        txtParams.put("sign", coder.SignRequest(txtParams, signKey));
        return txtParams;
    }

    public String getToken(){
        String token = "";
        Object redis_token = redisUtils.get("gjpToken");
        if(redis_token == null) {
            try {
               // ApiGetToken apiGetToken = new ApiGetToken();
                String result = apiGetToken.DoGetToken();
                log.info("DoGetToken result ={}",result);
                JSONObject jsonObject = JSONObject.fromObject(result);
                token = jsonObject.getString("auth_token");
                redisUtils.set("gjpToken", token,
                        ShopConstants.ORDER_OUTTIME_UNCONFIRM, TimeUnit.DAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            token = String.valueOf(redis_token);
        }

       /* HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity requestEntity = new HttpEntity("", headers);
        ResponseEntity<String> resultEntity = restTemplate.exchange(yiyaoShopApiUrl, HttpMethod.GET, requestEntity, String.class);
        token = resultEntity.getBody();
        log.info("??????????????????????????????token[{}]",token);*/

        return token;
    }

    public Boolean DoUploadProduct() throws Exception {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("upload_gjp_flag",0);
        List<YxStoreProduct> list = yxStoreProductService.list(queryWrapper);

        UploadProductRequest uploadProductRequest = new UploadProductRequest();
        List<SelfbuiltmallproductVo> products = new ArrayList<>();
        for(YxStoreProduct productDTO : list) {
            SelfbuiltmallproductVo selfbuiltmallproductVo = new SelfbuiltmallproductVo();
            //????????????
            selfbuiltmallproductVo.setProductname(productDTO.getStoreName());
            //????????????ID????????????????????????ptypeid???????????????
            selfbuiltmallproductVo.setNumid(String.valueOf(productDTO.getId()));
            //??????????????????
            selfbuiltmallproductVo.setOuterid(String.valueOf(productDTO.getId()));
            //?????????????????????
            selfbuiltmallproductVo.setPicurl(productDTO.getImage());
            //????????????
            selfbuiltmallproductVo.setPrice(productDTO.getPrice().floatValue());
            //??????????????????(1-??????;2-??????)
            Integer stockStatus = 1;
            if(productDTO.getIsShow() == 0) {
                stockStatus = 2;
            }
            selfbuiltmallproductVo.setStockstatus(stockStatus);
            selfbuiltmallproductVo.setSkus(new ArrayList<>());

            products.add(selfbuiltmallproductVo);
        }

        uploadProductRequest.setProducts(products);
        uploadProductRequest.setShopKey(config.getShop_key());
        String ret = "";


        if( CollectionUtil.isEmpty(products)) {
            return false;
        }

        //??????token
        String token = getToken();

        //??????
        Map<String, String> param;
        try {
            param = GetPostParams(uploadProductRequest, token, config.getAppkey(), config.getSign_key());
            //post??????
            String postString = "";
            for (String in : param.keySet()) {
                postString += in + "=" + URLEncoder.encode(param.get(in),"utf-8")  +"&";
            }
            postString = postString.substring(0, postString.length() - 1);
            ret =  HttpRequest.sendPost(config.getApi_link(), postString);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(StrUtil.isNotBlank(ret)) {

            log.info("upload product to gjp {}",ret);

            GjpResultVo gjpResultVo =  JSONUtil.toBean(ret,GjpResultVo.class);
            Boolean result = gjpResultVo.getResponse().getIssuccess();
            if(result) {
                list.forEach(product -> {
                    product.setUploadGjpFlag(1);
                    yxStoreProductService.updateById(product);
                });
                return true;
            } else {
                return false;
            }


        } else {
            return  false;
        }



    }

    @Async
 public Boolean express(GjpNotifyDTO gjpNotifyDTO){
        // ????????????
     String orderId = gjpNotifyDTO.getTradeid();

     QueryWrapper queryWrapper = new QueryWrapper();
     queryWrapper.eq("order_id",orderId);
     List<YxStoreOrder> stordOrderList = yxStoreOrderService.list(queryWrapper);

     if(CollectionUtil.isEmpty(stordOrderList)) {
         throw new BadRequestException("?????????{"+ orderId +"} ?????????");
     }


     YxExpressQueryCriteria criteria = new YxExpressQueryCriteria();
     criteria.setName(gjpNotifyDTO.getFreightName());
     List<YxExpress> yxExpressDTOList = yxExpressService.queryAll(criteria);
     if (CollectionUtil.isEmpty(yxExpressDTOList)) {
         throw new BadRequestException("?????????{"+ orderId +"} ?????????????????????{"+ gjpNotifyDTO.getFreightName() +"}?????????");
     }
     YxExpress yxExpressDTO = yxExpressDTOList.get(0);

     // ??????????????????
     String deliveryName = yxExpressDTO.getName();
     // ??????????????????
     String deliverySn = yxExpressDTO.getCode();
     // ????????????
     String deliveryId = gjpNotifyDTO.getFreightNo();

     YxStoreOrder resources = stordOrderList.get(0);
     /*if( StrUtil.isNotBlank(resources.getDeliveryId())) {
            return true;
     }*/
     resources.setStatus(1);
     resources.setDeliveryType("express");
     resources.setDeliveryName(deliveryName);
     resources.setDeliverySn(deliverySn);
     resources.setDeliveryId(deliveryId);
   //  yxStoreOrderService.update(resources);
     yxStoreOrderService.updateById(resources);

     YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
     storeOrderStatus.setOid(resources.getId());
     storeOrderStatus.setChangeType("delivery_goods");
     storeOrderStatus.setChangeMessage("????????? ???????????????" + resources.getDeliveryName()
             + " ???????????????" + resources.getDeliveryId());
     storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());

     yxStoreOrderStatusService.save(storeOrderStatus);




     // ???????????????????????????????????????????????????
     yiyaobaoOrderService.updateYiyaobaoExpress(deliveryId,orderId);


     // ??????????????????????????????
     if(orderId.startsWith("JJH")) {
         log.info("?????????????????????????????????,{}",orderId);
         zhengDaTianQingService.orderSendLoop();
     } else {
         log.info("??????????????????{}",orderId);
     }

     //??????????????????
     try {
         YxWechatUser wechatUser = wechatUserService.getById(resources.getUid());
         if (ObjectUtil.isNotNull(wechatUser)) {
             if (StrUtil.isNotBlank(wechatUser.getOpenid())) {
                 templateService.deliverySuccessNotice(resources.getOrderId(),
                         resources.getDeliveryName(),resources.getDeliveryId(),wechatUser.getOpenid());
             } else if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                 //todo ???????????????
             }
         }
     } catch (Exception e) {
         log.info("?????????????????????????????????????????????????????????!");
     }

     //??????redis???7????????????????????????
     String redisKey = String.valueOf(StrUtil.format("{}{}",
             ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM,resources.getId()));
     redisTemplate.opsForValue().set(redisKey, resources.getOrderId(),
             ShopConstants.ORDER_OUTTIME_UNCONFIRM, TimeUnit.DAYS);

        return true;
 }
}
