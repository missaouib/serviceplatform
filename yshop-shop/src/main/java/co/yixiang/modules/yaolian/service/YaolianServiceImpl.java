package co.yixiang.modules.yaolian.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.yaolian.domain.YaolianOrder;
import co.yixiang.modules.yaolian.domain.YaolianOrderDetail;
import co.yixiang.modules.yaolian.service.dto.*;
import co.yixiang.modules.yaolian.service.mapper.YaolianOrderMapper;
import co.yixiang.modules.yaolian.utils.Sha1Util;
import co.yixiang.modules.yiyaobao.dto.PrescriptionDTO;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.mp.service.mapper.DictDetailMapper;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.ImageUtil;
import co.yixiang.utils.OrderUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaolianServiceImpl {

    @Autowired
    private YxSystemStoreService yxSystemStoreService;
    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YaolianOrderService yaolianOrderService;

    @Autowired
    private YaolianOrderDetailService yaolianOrderDetailService;

    @Autowired
    private YaolianOrderMapper yaolianOrderMapper;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private DictDetailService dictDetailService;


    @Autowired
    private RestTemplate restTemplate;
    @Value("${yaolian.apiUrl}")
    private String apiUrl;
    @Value("${yaolian.nonce}")
    private String nonce;
    @Value("${yaolian.token}")
    private String token;
    @Value("${yaolian.cooperation}")
    private String cooperation;
    @Value("${yaolian.apiRefundUrl}")
    private String apiRefundUrl;

    @Autowired
    private DictDetailMapper dictDetailMapper;
    /**
     * 门店数据录入接口
     */
    public void pushStores(){
        StoreReqMain storeReqMain = new StoreReqMain();
        ReqHead storeReqHead = new ReqHead();
        List<StoreReqStores> list = new ArrayList<StoreReqStores>();

        try{
            //设置storeReqHead
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            long time = System.currentTimeMillis()/1000;
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            storeReqMain.setRequestHead(storeReqHead);
            LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper();
           // lambdaQueryWrapper.isNotNull(YxSystemStore::getCityName);
           // lambdaQueryWrapper.isNotNull(YxSystemStore::getProvinceName);
           // lambdaQueryWrapper.eq(YxSystemStore::getName,ShopConstants.STORENAME_SHANGHAI_CLOUD);
            lambdaQueryWrapper.apply(" EXISTS (SELECT 1 FROM product4project p  WHERE p.store_id = yx_system_store.id AND p.project_no = {0})",ProjectNameEnum.YAOLIAN.getValue());
            List<YxSystemStore> yxSystemStores = yxSystemStoreService.list(lambdaQueryWrapper);
            if(CollUtil.isEmpty(yxSystemStores)) {
                log.info("药房数据为空");
                return;
            }
            for (YxSystemStore yxSystemStore:yxSystemStores) {
                StoreReqStores storeReqStores = new StoreReqStores();
                storeReqStores.setId(yxSystemStore.getId().toString());
                storeReqStores.setNumber(yxSystemStore.getId().toString());

                if(StrUtil.isNotBlank(yxSystemStore.getShortName())) {
                    storeReqStores.setName(yxSystemStore.getShortName());
                } else {
                    storeReqStores.setName(yxSystemStore.getName());
                }

                storeReqStores.setAddress(yxSystemStore.getDetailedAddress());
                storeReqStores.setPhone(yxSystemStore.getPhone());
                storeReqStores.setGroup_id(yxSystemStore.getId().toString());
                storeReqStores.setIs_dtp(SystemConfigConstants.YAOLIAN_IS_TRUE);
                storeReqStores.setStatus(SystemConfigConstants.YAOLIAN_IS_TRUE);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (yxSystemStore.getAddTime()!=null && StringUtils.isNotBlank(yxSystemStore.getAddTime().toString())){
                    storeReqStores.setCreate_time(OrderUtil.stampToDate(yxSystemStore.getAddTime().toString()));
                    storeReqStores.setUpdate_time(OrderUtil.stampToDate(yxSystemStore.getAddTime().toString()));
                }
                storeReqStores.setLongitude(yxSystemStore.getLongitude());
                storeReqStores.setLatitude(yxSystemStore.getLatitude());
                storeReqStores.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
                storeReqStores.setProvince(yxSystemStore.getProvinceName());
                storeReqStores.setCity(yxSystemStore.getCityName());
               // storeReqStores.setBusiness_time(yxSystemStore.getDayTime());
                storeReqStores.setBusiness_time("08:30-17:00");
                storeReqStores.setArea("");
                list.add(storeReqStores);
            }
            storeReqMain.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
            storeReqMain.setStores(list);
            String response = sendRequest(JSONUtil.parse(storeReqMain).toString(), apiUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("药店信息上传失败：" + (String) map.get("error"));
            }else {
                log.info("药店信息上传成功：" + (String) map.get("errno"));
            }
        }catch (Exception e){
            log.debug("药店信息上传失败："+e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 药品类目接口
     */
    public void pushMedCategory(){
        YaolianCategoryDto yaolianCategoryDto = new YaolianCategoryDto();
        ReqHead storeReqHead = new ReqHead();

        try{
            //设置storeReqHead
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            long time = System.currentTimeMillis()/1000;
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            yaolianCategoryDto.setRequestHead(storeReqHead);
            List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("","productType");
            List<YaolianCategory> categories = new ArrayList<>();
            for(int i=0;i<dictDetailList.size();i++) {
                YaolianCategory yaolianCategory = new YaolianCategory();
                yaolianCategory.setCategory_alias(dictDetailList.get(i).getLabel());
                yaolianCategory.setCategory_name(dictDetailList.get(i).getLabel());
                yaolianCategory.setCategory_id(dictDetailList.get(i).getValue());
                yaolianCategory.setParent_id("0");
                yaolianCategory.setLevels("1");
                yaolianCategory.setStatus("1");
                yaolianCategory.setSort(String.valueOf(i+1));

                categories.add(yaolianCategory);
            }


            yaolianCategoryDto.setCategorys(categories);
            String response = sendRequest(JSONUtil.parse(yaolianCategoryDto).toString(), apiUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("商品类目录上传失败：" + (String) map.get("error"));
            }else {
                log.info("商品类目录上传成功：" + (String) map.get("errno"));
            }
        }catch (Exception e){
            log.debug("商品类目录上传失败："+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 商品数据录入接口
     */
    public void pushMedInfos(){
        DrugReqMain drugReqMain = new DrugReqMain();
        ReqHead storeReqHead = new ReqHead();
        List<DrugReq> list = new ArrayList<DrugReq>();

        // 获取中山西路门店的药店id
       // YxSystemStore yxSystemStore =  yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName, ShopConstants.STORENAME_SHANGHAI_CLOUD));
       // Integer storeId = yxSystemStore.getId();
        try {
            long time = System.currentTimeMillis()/1000;
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            drugReqMain.setRequestHead(storeReqHead);
            List<Drug> drugs = yaolianOrderMapper.pushMedInfos(ProjectNameEnum.YAOLIAN.getValue());
            if(CollUtil.isEmpty(drugs)) {
                log.info("药品数据为空");
                return;
            }
            for (Drug drug:drugs) {
                DrugReq reqDrug = new DrugReq();
                BeanUtils.copyProperties(drug, reqDrug);
                if(StrUtil.isBlank(drug.getName())) {
                    reqDrug.setName(drug.getCommonName());
                }

                reqDrug.setId(drug.getId().toString());
                reqDrug.setCreate_time(drug.getCreateTime()+"");
                reqDrug.setUpdate_time(drug.getUpdateTime()+"");
                reqDrug.setGroup_id(drug.getGroupId()+"");
                reqDrug.setPrice(drug.getPrice()+"");
                reqDrug.setCode(drug.getBarCode());
                reqDrug.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
                reqDrug.setIs_dtp(SystemConfigConstants.YAOLIAN_IS_TRUE);
                if(drug.getStatus() == 0) {
                    reqDrug.setStatus("1");   // 1 表示正常新增
                } else {
                    reqDrug.setStatus("0");    // 0 表示注销
                }

                List<String> categorys = new ArrayList<>();
                if(StrUtil.isBlank(drug.getType())) {
                    categorys.add("01");
                } else {
                    categorys.add(drug.getType());
                }

                reqDrug.setCategory_id(categorys);
                List<String> tags = new ArrayList<>();
                tags.add("1");
                reqDrug.setTags(tags);
                list.add(reqDrug);
            }
            drugReqMain.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
            drugReqMain.setDrugs(list);
            String response = sendRequest(JSONUtil.parse(drugReqMain).toString(), apiUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("药品信息上传失败：" +  (String) map.get("error"));
            }else {
                log.info("药品信息上传成功：" +  (String) map.get("error"));
            }
        }catch (Exception e){
            log.debug("药品信息上传失败："+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 库存数据录入接口
     */
    public void pushStockInfos(){
        StockReqMain stockReqMain = new StockReqMain();
        ReqHead storeReqHead = new ReqHead();
        List<StockReq> list = new ArrayList<StockReq>();
        // 获取中山西路门店的药店id
      //  YxSystemStore yxSystemStore =  yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName, ShopConstants.STORENAME_SHANGHAI_CLOUD));
      //  Integer storeId = yxSystemStore.getId();

        try {
            long time = System.currentTimeMillis()/1000;
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            stockReqMain.setRequestHead(storeReqHead);
            List<Drug> drugs = yaolianOrderMapper.pushMedInfos(ProjectNameEnum.YAOLIAN.getValue());
            if(CollUtil.isEmpty(drugs)) {
                log.info("药品库存数据为空");
                return;
            }
            for (Drug drug:drugs) {
                StockReq stockReq = new StockReq();
                stockReq.setDrug_id(drug.getId()+"");
                stockReq.setStore_id(drug.getGroupId()+"");
                if(drug.getIgnoreStock()!= null && drug.getIgnoreStock() == 1) {
                    stockReq.setQuantity("999");
                } else {
                    stockReq.setQuantity(drug.getQuantity()+"");
                }

               // stockReq.setQuantity("999");
                stockReq.setCreate_time(drug.getCreateTime()+"");
                stockReq.setUpdate_time(drug.getUpdateTime()+"");
                list.add(stockReq);
            }
            stockReqMain.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
            stockReqMain.setStocks(list);
            String response = sendRequest(JSONUtil.parse(stockReqMain).toString(), apiUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("药品库存数据上传失败：" +  (String) map.get("error"));
            }else {
                log.info("药品库存数据上传成功：" +  (String) map.get("error"));
            }
        }catch (Exception e){
            log.debug("药品库存数据上传失败："+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 订单数据录入接口
     * @param orderId
     * @return
     */
    public void pushOrderInfo(String orderId) throws Exception {
        OrderMain orderMain = new OrderMain();
        ReqHead storeReqHead = new ReqHead();
        List<OrderInfoReq> list = new ArrayList<OrderInfoReq>();
        List<OrderInfoDetaiReq> detaiReqList = new ArrayList<OrderInfoDetaiReq>();
        try{
            long time = System.currentTimeMillis()/1000;
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            orderMain.setRequestHead(storeReqHead);
            List<Map> orderOfLots = yaolianOrderMapper.pushOrderIfo(orderId);
            List<Map> yaolianOrder = yaolianOrderMapper.getYaolianOrder(orderId);
            if(yaolianOrder!=null && yaolianOrder.size()>0){
                OrderInfoReq orderInfoReq = new OrderInfoReq();
                orderInfoReq.setId((String) yaolianOrder.get(0).get("orderId"));
                orderInfoReq.setCreate_time((Timestamp) yaolianOrder.get(0).get("createTime")+"");
                orderInfoReq.setUpdate_time((Timestamp) yaolianOrder.get(0).get("createTime")+"");
                orderInfoReq.setMember_id(StringUtils.EMPTY);
                orderInfoReq.setMember_name(StringUtils.EMPTY);
                orderInfoReq.setCard_number(StringUtils.EMPTY);
                orderInfoReq.setStore_id((String) yaolianOrder.get(0).get("storeId"));
                orderInfoReq.setTotal_price((String) yaolianOrder.get(0).get("totalPrice"));
                orderInfoReq.setUd_card(StringUtils.EMPTY);
                orderInfoReq.setOrder_no((String) yaolianOrder.get(0).get("id"));

                for (Map map:orderOfLots) {
                    OrderInfoDetaiReq orderInfoDetaiReq = new OrderInfoDetaiReq();
                    orderInfoDetaiReq.setAmount(String.valueOf(map.get("amount")));
                    orderInfoDetaiReq.setBatch_number((String) map.get("lotNo"));
                    orderInfoDetaiReq.setExpirydate((java.sql.Date) map.get("period")+"");
                    for (Map detail:yaolianOrder) {
                        if(map.get("sku").equals(detail.get("sku"))) {
                            orderInfoDetaiReq.setDrug_id((String) detail.get("drugId"));
                            orderInfoDetaiReq.setCommon_name((String) detail.get("commonName"));
                            orderInfoDetaiReq.setNumber((String) detail.get("number"));
                            orderInfoDetaiReq.setForm((String) detail.get("form"));
                            orderInfoDetaiReq.setPack((String) detail.get("pack"));
                            orderInfoDetaiReq.setPrice(String.valueOf(detail.get("price")));
                            orderInfoDetaiReq.setUnit_price(String.valueOf(detail.get("unitPrice")));
                            orderInfoDetaiReq.setCode(StringUtils.EMPTY);
                            orderInfoDetaiReq.setUnit_price(String.valueOf(detail.get("price")));
                        }
                    }
                    detaiReqList.add(orderInfoDetaiReq);
                }
                orderInfoReq.setRetailDetail(detaiReqList);
                list.add(orderInfoReq);
            }
            orderMain.setChannel(SystemConfigConstants.YAOLIAN_IS_TRUE);
            orderMain.setOrders(list);

            String response = sendRequest(JSONUtil.parse(orderMain).toString(), apiUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("出库单数据上传失败：" +  (String) map.get("error"));
            }else {
                log.info("出库单数据上传成功：" +  (String) map.get("error"));
            }
        }catch (Exception e){
            throw new Exception("出库单数据上传失败："+ e.getMessage());
        }
    }

    public String sendRequest(String requestBody, String url){
        log.info("============请求报文："+requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity request = new HttpEntity(requestBody, headers);
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



    /**
     * 订单作废退单接口
     * @param yaolianOrderRefund
     * @return
     */
    @Async
    public void pushOrderRefundInfo(YaolianOrderRefund yaolianOrderRefund) {
        YaolianOrderRefundDto yaolianOrderRefundDto = new YaolianOrderRefundDto();
        ReqHead storeReqHead = new ReqHead();

            long time = System.currentTimeMillis()/1000;
            storeReqHead.setCooperation(cooperation);
            storeReqHead.setNonce(nonce);
            log.info("===获取时间==="+time);
            storeReqHead.setTimestamp(String.valueOf(time));
            storeReqHead.setSign(getSign(time));
            storeReqHead.setTradeDate(DateUtils.getTime());
            yaolianOrderRefundDto.setRequestHead(storeReqHead);
            yaolianOrderRefundDto.setOrderRefund(yaolianOrderRefund);
            String response = sendRequest(JSONUtil.parse(yaolianOrderRefundDto).toString(), apiRefundUrl);
            log.debug("返回信息：" + response);
            Map map = (Map) JSONUtils.parse(response);
            if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
                log.info("订单作废退单接口上传失败：" +  (String) map.get("error"));
            }else {
                log.info("订单作废退单接口上传成功：" +  (String) map.get("error"));
            }

    }


    private String getSign(long time){
        String sign = StringUtils.EMPTY;
        String[] data = {nonce, String.valueOf(time) ,token};
        Arrays.sort(data);
        String sign_origin = "";
        for(int i=0;i<data.length;i++) {
            sign_origin += data[i];
        }
        try {
            sign = Sha1Util.getSha1(sign_origin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("药联请求签名 nonce={},token={},time={},sign={}",nonce,token,String.valueOf(time),sign);
        return sign;
    }


    @Async
    public Boolean syncData(){
        pushStores();
        pushMedCategory();
        pushMedInfos();
        pushStockInfos();
        return true;
    }
}
