package co.yixiang.modules.ebs.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.mapper.StoreOrderMapper;
import co.yixiang.mp.yiyaobao.domain.SkuSellerPriceStock;
import co.yixiang.utils.Base64Util;
import co.yixiang.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/6/4 8:30
 */
@Slf4j
@Service
public class EbsServiceImpl {

    @Value("${ebs.url}")
    private String url;

    @Autowired
    private StoreOrderMapper storeOrderMapper;

    @Autowired
    private RestTemplate restTemplate;

    public void send(String orderNo,String orderReferee) {

        /*
        * {
    "data": [
       {
         "yybOrderNumber":"3000002110768457",
         "orderReferee":"张三"
       }
    ]
}
        *
        * */
        JSONArray jsonArray = JSONUtil.createArray();
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("yybOrderNumber",orderNo);
        jsonObject.put("orderReferee",orderReferee);
        jsonArray.add(jsonObject);
        JSONObject jsonObject1 = JSONUtil.createObj();
        jsonObject1.put("data",jsonArray);

        log.info("ebs============编码前报文:{}",jsonObject1.toString());
        String encode = Base64.encode(jsonObject1.toString());

        log.info("ebs============编码后报文:{}",encode);
        JSONObject requestBody = JSONUtil.createObj();
        requestBody.put("reqcode","CUXOMYYBDATA");
        requestBody.put("reqcodecate","");
        requestBody.put("reqjsonparam", encode);
        log.info("ebs============请求报文:{}",requestBody.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization","Basic Y3V4d2ViOmN1eHdlYjMyMQ==");
        HttpEntity request = new HttpEntity(requestBody.toString(), headers);
        String body = "";
        try{
            ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("ebs sendRequest 返回结果 {}",body);
        }
    }


    /**
     * 获取ebs商品库存数据
     * @param myDate 起始日期
     * @param startNum 起始条数
     * @param stepNum 每页条数
     * @param itemCode 药品sku
     * @return
     */
    public List<SkuSellerPriceStock> queryYiyaobaoMedStock(String myDate,String organizationId,Integer startNum,Integer stepNum,String itemCode) {
        List<SkuSellerPriceStock> skuSellerPriceStocks=new ArrayList<>();

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("myDate", StringUtils.isEmpty(myDate)?"20150101 00:00:00":myDate);
        jsonObject.put("pageNum",startNum);
        jsonObject.put("stepNum",stepNum);
        jsonObject.put("organizationId",organizationId);
        jsonObject.put("itemCode",itemCode);

        log.info("ebs============编码前报文:{}",jsonObject.toString());
        String encode = Base64.encode(jsonObject.toString());

        log.info("ebs============编码后报文:{}",encode);
        JSONObject requestBody = JSONUtil.createObj();
        requestBody.put("reqcode","CUXINVYYBINVQTYQUERY");
        requestBody.put("reqcodecate","");
        requestBody.put("reqjsonparam", encode);
        log.info("ebs============请求报文:{}",requestBody.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization","Basic Y3V4d2ViOmN1eHdlYjMyMQ==");
        HttpEntity request = new HttpEntity(requestBody.toString(), headers);
        String body = "";
        try{
            ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("ebs sendRequest 返回结果 {}",body);
        }
        if(StringUtils.isNotEmpty(body)){
            JSONObject responseJson=JSONUtil.parseObj(body);
            responseJson= (JSONObject) JSONUtil.parseArray(responseJson.get("data").toString()).get(0);
            if(responseJson.get("success")!=null && responseJson.get("success").toString().equals("true")){
                JSONArray jsonArray=JSONUtil.parseArray(responseJson.get("data"));
                for (Object o : jsonArray) {
                    JSONObject object=(JSONObject)o;
                    SkuSellerPriceStock skuSellerPriceStock=new SkuSellerPriceStock();
                    skuSellerPriceStock.setSku(object.get("itemCode").toString());
                    skuSellerPriceStock.setSellerId(object.get("organizationId").toString());
                    skuSellerPriceStock.setSellerName(object.get("organizationName").toString());
                    skuSellerPriceStock.setStock(Integer.valueOf(object.get("qty").toString()));
                    skuSellerPriceStocks.add(skuSellerPriceStock);
                }
            }
        }
        return skuSellerPriceStocks;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("myDate","20210825 17:35:00");
        jsonObject.put("pageNum",1);
        jsonObject.put("stepNum",100);
        jsonObject.put("organizationId","85");
        jsonObject.put("itemCode","010101191");

        log.info("ebs============编码前报文:{}",jsonObject.toString());
        String encode = Base64.encode(jsonObject.toString());

        log.info("ebs============编码后报文:{}",encode);
        JSONObject requestBody = JSONUtil.createObj();
        requestBody.put("reqcode","CUXINVYYBINVQTYQUERY");
        requestBody.put("reqcodecate","");
        requestBody.put("reqjsonparam", encode);
        log.info("ebs============请求报文:{}",requestBody.toString());
//        List<SkuSellerPriceStock> skuSellerPriceStocks=new ArrayList<>();
//
//        String body="{ \"data\":[{\"success\": true,\"headers\":{\"myDate\":\"20210825 17:35:00\" ,\"pageNum\":1,\"stepNum\":1000,\"organizationId\":\"\" ,\"itemCode\":\"\" },\"data\":[{\"rowNum\":1,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010106026\" ,\"itemDesc\":\"\\u590D\\u65B9\\u8111\\u80BD\\u8282\\u82F7\\u8102\\u6CE8\\u5C04\\u6DB2||2ml|\\u652F|\\u5409\\u6797\\u5929\\u6210\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":377,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":2,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010111074\" ,\"itemDesc\":\"\\u53F8\\u7F8E\\u683C\\u9C81\\u80BD\\u6CE8\\u5C04\\u6DB2|\\u8BFA\\u548C\\u6CF0|1.34mg/ml\\uFF0C1.5ml|\\u76D2|\\u4E39\\u9EA6\\u8BFA\\u548C\\u8BFA\\u5FB7\\u516C\\u53F8\" ,\"qty\":192,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":3,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010201160\" ,\"itemDesc\":\"\\u6CE8\\u5C04\\u7528\\u7D2B\\u6749\\u9187\\uFF08\\u767D\\u86CB\\u767D\\u7ED3\\u5408\\u578B\\uFF09||100mg|\\u74F6|\\u77F3\\u836F\\u96C6\\u56E2\\u6B27\\u610F\\u836F\\u4E1A\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":98,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":4,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010213012\" ,\"itemDesc\":\"\\u6CE8\\u5C04\\u7528\\u80F8\\u817A\\u6CD5\\u65B0|\\u8FC8\\u666E\\u65B0|1.6mg|\\u74F6|\\u6210\\u90FD\\u5730\\u5965\\u4E5D\\u6CD3\\u5236\\u836F\\u5382\" ,\"qty\":848,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":5,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010217057\" ,\"itemDesc\":\"\\u805A\\u4E59\\u4E8C\\u9187\\u5E72\\u6270\\u7D20\\u03B1-2b\\u6CE8\\u5C04\\u6DB2|\\u6D3E\\u683C\\u5BBE\\uFF083%)|180\\u03BCg(60\\u4E07U)/0.5 ml/\\u652F(\\u9884\\u5145\\u5F0F)|\\u652F|\\u53A6\\u95E8\\u7279\\u5B9D\\u751F\\u7269\\u5DE5\\u7A0B\\u80A1\\u4EFD\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":1219,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":6,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010217064\" ,\"itemDesc\":\"\\u805A\\u4E59\\u4E8C\\u9187\\u5E72\\u6270\\u7D20\\u03B1-2b\\u6CE8\\u5C04\\u6DB2|\\u6D3E\\u683C\\u5BBE\\uFF083%)|135\\u03BCg(50\\u4E07U)/0.5ml/\\u652F(\\u9884\\u5145\\u5F0F)|\\u652F|\\u53A6\\u95E8\\u7279\\u5B9D\\u751F\\u7269\\u5DE5\\u7A0B\\u80A1\\u4EFD\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":86,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":7,\"totalNum\":22,\"organizationCode\":\"024\" ,\"organizationName\":\"\\u4E0A\\u836F\\u4E91\\u5065\\u5EB7\\u76CA\\u836F\\u836F\\u623F\\uFF08\\u4E0A\\u6D77\\uFF09\\u6709\\u9650\\u516C\\u53F8\\u4E2D\\u5C71\\u897F\\u8DEF\\u5E97\" ,\"organizationId\":85,\"itemCode\":\"010401068\" ,\"itemDesc\":\"\\u54CC\\u67CF\\u897F\\u5229\\u80F6\\u56CA|\\u7231\\u535A\\u65B0|125mg*21\\u7C92|\\u76D2|Pfizer Manufacturing Deutschland GmbH, Betriebsstatte Freiburg\\uFF08\\u5FB7\\u56FD\\uFF09\" ,\"qty\":25,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":8,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306121\" ,\"itemDesc\":\"\\u785D\\u9178\\u5F02\\u5C71\\u68A8\\u916F\\u7247||5mg*100\\u7247|\\u74F6|\\u4E0A\\u6D77\\u590D\\u65E6\\u590D\\u534E\\u836F\\u4E1A\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":0,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":9,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306168\" ,\"itemDesc\":\"\\u7425\\u73C0\\u9178\\u7F8E\\u6258\\u6D1B\\u5C14\\u7F13\\u91CA\\u7247|\\u500D\\u4ED6\\u4E50\\u514B|47.5mg*7\\u7247|\\u76D2|\\u963F\\u65AF\\u5229\\u5EB7\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":410,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":10,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306173\" ,\"itemDesc\":\"\\u897F\\u6D1B\\u4ED6\\u5511\\u7247|\\u57F9\\u8FBE|50mg*12\\u7247|\\u76D2|\\u6D59\\u6C5F\\u5927\\u51A2\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":15,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":11,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306310\" ,\"itemDesc\":\"\\u963F\\u53F8\\u5339\\u6797\\u80A0\\u6EB6\\u7247||100mg*30\\u7247|\\u76D2|\\u62DC\\u8033\\u533B\\u836F\\u4FDD\\u5065\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":80,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":12,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306320\" ,\"itemDesc\":\"\\u786B\\u9178\\u6C22\\u6C2F\\u5421\\u683C\\u96F7\\u7247|\\u6CE2\\u7ACB\\u7EF4|75mg*7\\u7247|\\u76D2|\\u8D5B\\u8BFA\\u83F2(\\u676D\\u5DDE)\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":149,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":13,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306412\" ,\"itemDesc\":\"\\u76D0\\u9178\\u8D1D\\u5C3C\\u5730\\u5E73\\u7247|\\u53EF\\u529B\\u6D1B|8mg*7\\u7247|\\u76D2|\\u534F\\u548C\\u53D1\\u9175\\u9E92\\u9E9F\\uFF08\\u4E2D\\u56FD\\uFF09\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":105,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":14,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306479\" ,\"itemDesc\":\"\\u666E\\u4F10\\u4ED6\\u6C40\\u94A0\\u7247|\\u7F8E\\u767E\\u4E50\\u9547|40mg*7\\u7247|\\u76D2|\\u7B2C\\u4E00\\u4E09\\u5171\\u5236\\u836F(\\u4E0A\\u6D77)\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":107,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":15,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306522\" ,\"itemDesc\":\"\\u76D0\\u9178\\u4F0A\\u4F10\\u5E03\\u96F7\\u5B9A\\u7247|\\u53EF\\u5170\\u7279|5mg*14\\u7247*1\\u677F|\\u76D2|\\u6CD5\\u56FDLes Laboreloires Servier Industrie\" ,\"qty\":4,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":16,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306715\" ,\"itemDesc\":\"\\u76D0\\u9178\\u8D1D\\u5C3C\\u5730\\u5E73\\u7247||8mg*7\\u7247|\\u76D2|\\u5C71\\u4E1C\\u534E\\u7D20\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":36,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":17,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306723\" ,\"itemDesc\":\"\\u76D0\\u9178\\u83AB\\u96F7\\u897F\\u55EA\\u7247||50mg*40\\u7247|\\u76D2|\\u4E39\\u4E1C\\u533B\\u521B\\u836F\\u4E1A\\u6709\\u9650\\u8D23\\u4EFB\\u516C\\u53F8\" ,\"qty\":25,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":18,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306727\" ,\"itemDesc\":\"\\u5965\\u7F8E\\u6C99\\u5766\\u916F\\u6C22\\u6C2F\\u567B\\u55EA\\u7247||20mg:12.5mg*7\\u7247|\\u76D2|\\u7B2C\\u4E00\\u4E09\\u5171\\u5236\\u836F(\\u4E0A\\u6D77)\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":122,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":19,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306809\" ,\"itemDesc\":\"\\u745E\\u8212\\u4F10\\u4ED6\\u6C40\\u9499\\u7247||10mg*28s\\u8584\\u819C\\u8863|\\u76D2|\\u5357\\u4EAC\\u6B63\\u5927\\u5929\\u6674\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":11,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":20,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306924\" ,\"itemDesc\":\"\\u76D0\\u9178\\u4E50\\u5361\\u5730\\u5E73\\u7247||10mg*10\\u7247|\\u76D2|\\u91CD\\u5E86\\u5723\\u534E\\u66E6\\u836F\\u4E1A\\u80A1\\u4EFD\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":0,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":21,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010306960\" ,\"itemDesc\":\"\\u66FF\\u683C\\u745E\\u6D1B\\u7247||90mg*14\\u7247|\\u76D2|\\u5357\\u4EAC\\u6B63\\u5927\\u5929\\u6674\\u5236\\u836F\\u6709\\u9650\\u516C\\u53F8\" ,\"qty\":0,\"currDate\":\"2021-08-26 08:24:11\" },{\"rowNum\":22,\"totalNum\":22,\"organizationCode\":\"241\" ,\"organizationName\":\"\\u5E7F\\u5DDE\\u4E0A\\u836F\\u76CA\\u836F\\u836F\\u623F\\u6709\\u9650\\u516C\\u53F8\" ,\"organizationId\":187,\"itemCode\":\"010520206\" ,\"itemDesc\":\"\\u73BB\\u7483\\u9178\\u94A0\\u6EF4\\u773C\\u6DB2||0.1%*10ml/\\u652F|\\u76D2|URSAPHARM Arzneimittel GmbH(\\u5FB7\\u56FD\\uFF09\" ,\"qty\":40,\"currDate\":\"2021-08-26 08:24:11\" }]}]}";
//        if(StringUtils.isNotEmpty(body)){
//            JSONObject responseJson=JSONUtil.parseObj(body);
//            responseJson= (JSONObject) JSONUtil.parseArray(responseJson.get("data").toString()).get(0);
//            if(responseJson.get("success")!=null && responseJson.get("success").toString().equals("true")){
//                JSONArray jsonArray=JSONUtil.parseArray(responseJson.get("data"));
//                for (Object o : jsonArray) {
//                    JSONObject object=(JSONObject)o;
//                    SkuSellerPriceStock skuSellerPriceStock=new SkuSellerPriceStock();
//                    skuSellerPriceStock.setSku(object.get("itemCode").toString());
//                    skuSellerPriceStock.setSellerId(object.get("organizationId").toString());
//                    skuSellerPriceStock.setSellerName(object.get("organizationName").toString());
//                    skuSellerPriceStock.setStock(Integer.valueOf(object.get("qty").toString()));
//                    skuSellerPriceStocks.add(skuSellerPriceStock);
//                }
//            }
//        }
//        System.out.println(skuSellerPriceStocks.size());
    }




    public void sendAll() {
        LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(YxStoreOrder::getProjectCode, ProjectNameEnum.DIAO.getValue());
        lambdaQueryWrapper.select(YxStoreOrder::getOrderId, YxStoreOrder::getRefereeCode);
        List<YxStoreOrder> orderList = storeOrderMapper.selectList(lambdaQueryWrapper);
        for(YxStoreOrder yxStoreOrder : orderList) {
            send(yxStoreOrder.getOrderId(),yxStoreOrder.getRefereeCode());
            log.info("{}发送完成",yxStoreOrder.getOrderId());
        }
        log.info("发送ebs订单数：{}",orderList.size());
    }
}
