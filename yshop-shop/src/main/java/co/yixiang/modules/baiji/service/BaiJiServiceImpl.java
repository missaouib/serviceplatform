package co.yixiang.modules.baiji.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.baiji.domain.ApiRequestBaiJi;
import co.yixiang.modules.baiji.domain.BaiJiMed;
import co.yixiang.modules.baiji.domain.BaiJiStock;
import co.yixiang.modules.msh.util.HttpUtil;
import co.yixiang.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class BaiJiServiceImpl {

    @Value("${baiji.companyId}")
    private String companyId;

    @Value("${baiji.secureKey}")
    private String secureKey;

    @Value("${baiji.queryStockUrl}")
    private String queryStockUrl;

    @Value("${baiji.queryMedUrl}")
    private String queryMedUrl;

    @Value("${baiji.queryStockRequestType}")
    private String queryStockRequestType;

    @Value("${baiji.queryMedRequestType}")
    private String queryMedRequestType;

    /**
     * 库存查询接口
     * @param pageNo
     * @param pageSize
     * @param pharmacyCode
     * @param goodsCode
     * @return
     */
    public  List<BaiJiStock> queryStock(Integer pageNo,Integer pageSize,String pharmacyCode,String goodsCode){
        List<BaiJiStock> stocks=new ArrayList<>();

        Date date=new Date();
        String requestTime=  DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS,date);
        String nonce = getUUID32();
        String signature= getSignature(date.getTime(), nonce, secureKey);
        String httpUrl=queryStockUrl+"?timestamp="+date.getTime()+"&nonce="+nonce+"&signature="+signature+"&companyId="+companyId+"";
        log.info("发送BaiJi的URL:{}",httpUrl);


            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("pageNo",pageNo);
            jsonObject.put("pageSize",pageSize);
            jsonObject.put("pharmacyCode",pharmacyCode);
            jsonObject.put("goodsCode",goodsCode);

            ApiRequestBaiJi apiRequestJunLing=new ApiRequestBaiJi();
            apiRequestJunLing.setCompanyId(companyId);
            apiRequestJunLing.setRequestId(nonce);
            apiRequestJunLing.setRequestTime(requestTime);
            apiRequestJunLing.setRequestType(queryStockRequestType);
            apiRequestJunLing.setRequestData(jsonObject);

            log.info("发送BaiJi的消息:{}", JSONUtil.parseObj(apiRequestJunLing));
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//            ResponseEntity<String> resultEntity = RestUtil.request(httpUrl,HttpMethod.POST,headers,null,JSONUtil.parseObj(apiRequestJunLing).toString(),String.class);
//            String body = resultEntity.getBody();
            String body="";
            try {
                body = HttpUtil.post(httpUrl, JSONUtil.parseObj(apiRequestJunLing).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("BaiJi返回的消息:{}",body);
            if(StringUtils.isNotEmpty(body)){
                JSONObject object= JSONUtil.parseObj(body);
                if(object.get("success")==null || !object.get("success").equals(true)){
                    throw new RuntimeException("发送失败："+object.get("message"));
                }else{
                    JSONObject resultObject= JSONUtil.parseObj(object.get("result"));
                    if(resultObject!=null){
                         stocks= JSONUtil.toList(JSONUtil.parseArray(resultObject.get("records")),BaiJiStock.class);
                    }
                    return stocks;
                }
            }
        return  stocks;
    }


    /**
     * 药品数据查询接口
     * @param pageNo
     * @param pageSize
     * @param pharmacyCode
     * @param goodsCode
     * @param syncLastTime
     * @return
     */
    public List<BaiJiMed> queryMed(Integer pageNo, Integer pageSize, String pharmacyCode, String goodsCode, String syncLastTime){
        List<BaiJiMed> meds=new ArrayList<>();
        Date date=new Date();
        String requestTime=  DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS,date);
        String nonce = getUUID32();
        String signature= getSignature(date.getTime(), nonce, secureKey);
        String httpUrl=queryMedUrl+"?timestamp="+date.getTime()+"&nonce="+nonce+"&signature="+signature+"&companyId="+companyId+"";
        log.info("发送BaiJi的URL:{}",httpUrl);


        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("pageNo",pageNo);
        jsonObject.put("pageSize",pageSize);
        jsonObject.put("pharmacyCode",pharmacyCode);
        jsonObject.put("goodsCode",goodsCode);
        jsonObject.put("syncLastTime", StringUtils.isEmpty(syncLastTime)?"":syncLastTime);

        ApiRequestBaiJi apiRequestJunLing=new ApiRequestBaiJi();
        apiRequestJunLing.setCompanyId(companyId);
        apiRequestJunLing.setRequestId(nonce);
        apiRequestJunLing.setRequestTime(requestTime);
        apiRequestJunLing.setRequestType(queryMedRequestType);
        apiRequestJunLing.setRequestData(jsonObject);

        log.info("发送BaiJi的消息:{}", JSONUtil.parseObj(apiRequestJunLing));
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//            ResponseEntity<String> resultEntity = RestUtil.request(httpUrl,HttpMethod.POST,headers,null,JSONUtil.parseObj(apiRequestJunLing).toString(),String.class);
//            String body = resultEntity.getBody();
        String body="";
        try {
            body = HttpUtil.post(httpUrl, JSONUtil.parseObj(apiRequestJunLing).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("BaiJi返回的消息:{}",body);
        if(StringUtils.isNotEmpty(body)){
            JSONObject object= JSONUtil.parseObj(body);
            if(object.get("success")==null || !object.get("success").equals(true)){
                throw new RuntimeException("发送失败："+object.get("message"));
            }else{
                JSONObject resultObject= JSONUtil.parseObj(object.get("result"));
                if(resultObject!=null){
                    meds = JSONUtil.toList( JSONUtil.parseArray(resultObject.get("records")), BaiJiMed.class);
                }
                return meds;
            }
        }
        return  meds;
    }
    /**
     * 生成uuid32位
     *
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String getSignature(Long timestamp, String nonce, String secureKey) {
        String str = timestamp.toString() + nonce + secureKey;
        String signature = DigestUtils.sha1Hex(str);
        return signature;
    }

    public static void main(String[] args) {
//        List<BaiJiStock> stocks=  queryStock(1,100,"","11");
//        System.out.println(stocks);
//        BaiJiMed baiJiMed=new BaiJiMed();
//        baiJiMed.setApplyCrowdDesc("1231");
//        baiJiMed.setCode("11111111111");
//        baiJiMed.setIzDel("1");
//        baiJiMed.setTaxRate("0.8");
//        YxStoreProduct storeProduct=new YxStoreProduct();
//        BeanUtils.copyProperties(baiJiMed, storeProduct);
//        System.out.println(JSONUtil.parseObj(storeProduct));
//        storeProduct.setStoreName(baiJiMed.getName());//商品名称
//        storeProduct.setTaxRate(new BigDecimal(baiJiMed.getTaxRate()));//交易税率
//        storeProduct.setYiyaobaoSku(baiJiMed.getCode());//药品sku编码
//        storeProduct.setIsDel(Integer.valueOf(baiJiMed.getIzDel()));//是否删除（0 /否，1/是）
//
//        System.out.println(JSONUtil.parseObj(storeProduct));
//        List<BaiJiMed> stocks=  queryMed(1,100,"","","");
//        System.out.println(stocks);
//


    }

}
