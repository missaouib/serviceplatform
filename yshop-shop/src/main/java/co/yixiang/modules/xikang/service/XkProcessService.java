package co.yixiang.modules.xikang.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import co.yixiang.modules.shop.domain.YxUserAddress;
import co.yixiang.modules.shop.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.shop.service.YxUserAddressService;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.shop.service.mapper.StoreOrderMapper;
import co.yixiang.modules.xikang.domain.XikangMedMapping;
import co.yixiang.modules.xikang.dto.*;
import co.yixiang.modules.xikang.tools.JSONMapper;
import co.yixiang.modules.xikang.tools.SignatureUtil;
import co.yixiang.modules.xikang.tools.TokenUtil;
import co.yixiang.tools.express.support.NameValuePairHelper;
import co.yixiang.utils.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class XkProcessService {

    @Value("${xikang.oauthURL}")
    private String oauthURL;

    @Value("${xikang.clientID}")
    private String clientID;

    @Value("${xikang.clientSecret}")
    private String clientSecret;

    @Value("${xikang.resURL}")
    private String resURLCall;

    @Value("${xikang.supplierCode}")
    private String supplierCode;

    @Value("${xikang.supplierName}")
    private String supplierName;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StoreOrderMapper yxStoreOrderMapper;

    @Autowired
    private InternetHospitalDemandService internetHospitalDemandService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    private XikangMedMappingService xikangMedMappingService;

    public String getToken() {
        try {
            Object token_object = redisUtils.get("xikangToken");
            if(ObjectUtil.isNull(token_object)) {
                String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
                log.info("获取互联网医院token返回结果{}",content);
                Map<String, String> map = new Gson().fromJson(content, Map.class);
                String accessToken = map.get("access_token");
                redisUtils.set("xikangToken",accessToken, 8,TimeUnit.HOURS);
                return  accessToken;
            } else {
                log.info("从redis获取互联网医院token返回结果{}",token_object.toString());
                return token_object.toString();
            }
        }catch (Exception e) {
            log.error("获取互联网医院token错误{}",e.getMessage());
        }
        return "";
    }

    public void payNotice(String prescriptionCode){
        try {

            log.info("payNotice={}",prescriptionCode);
            InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getOne(new QueryWrapper<InternetHospitalDemand>().eq("prescription_code",prescriptionCode).select("order_id"),false);
            YxStoreOrder yxStoreOrder = yxStoreOrderMapper.selectOne(new QueryWrapper<YxStoreOrder>().eq("order_id",internetHospitalDemand.getOrderId()));

            CloseableHttpClient httpClient = HttpClients.createDefault();

           /* String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
            log.info("获取token的结果{}",content);
            Map<String, String> map = new Gson().fromJson(content, Map.class);
            String accessToken = map.get("access_token");*/
            String accessToken = getToken();
            //XK环境 接口地址
//		String resURL = "http://dldoctor.xikang.cn/adapter-sy/openapi/process";
            //正式环境
            //String resURL = "http://dldoctor.xikang.cn/adapter-sy/openapi/process";
//		HisUploadRegRxHN hisUploadRegRxHN = new HisUploadRegRxHN();
//		hisUploadRegRxHN.setDeptCode("ddd");
            //准备上传医生的参数签名***********开始***********具体参数根据实际接口来组装
            Request request = new Request();
            request.setBusinessType("025");
            request.setMethod("XKP091");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);
            YxUserAddress yxUserAddress = yxUserAddressService.getById(yxStoreOrder.getAddressId());
            XkOrder xkOrder = new XkOrder();
            xkOrder.setCityName(yxUserAddress.getCity());
            xkOrder.setDistrictName(yxUserAddress.getDistrict());
            xkOrder.setProvinceName(yxUserAddress.getProvince());
            xkOrder.setDeliveryPrice( String.valueOf(yxStoreOrder.getTotalPostage()));
            xkOrder.setDeliveryType("DELIVERY");


            xkOrder.setPayDatetime(DateUtil.now());

            xkOrder.setSupplierCode(supplierCode);
            xkOrder.setSupplierName(supplierName);


            xkOrder.setPrescriptionCode(prescriptionCode);
            xkOrder.setOtherPayCode(yxStoreOrder.getOrderId());
            xkOrder.setPaymentWay("WECHATPAY");
            xkOrder.setReceiverName(yxUserAddress.getRealName());
            xkOrder.setReceiverTel(yxUserAddress.getPhone());
            xkOrder.setReceiverAddress(yxUserAddress.getDetail());

            QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("oid",yxStoreOrder.getId());
            List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(wrapper);

            List<XkOrderDetail> drugsList = new ArrayList<>();
            BigDecimal totalAmount = new BigDecimal("0");
            for (YxStoreOrderCartInfo info : cartInfos) {
                YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
                int cartNum = cartQueryVo.getCartNum();
                String yiyaobaoSku =  cartQueryVo.getYiyaobaoSku();
                String commonName = cartQueryVo.getProductInfo().getCommonName();
                // 查找互联网医院的药品映射
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("yiyaobao_sku",yiyaobaoSku);
                XikangMedMapping xikangMedMapping = xikangMedMappingService.getOne(queryWrapper,false);
                if(xikangMedMapping != null) {
                    String xikangCode = xikangMedMapping.getXikangCode();
                    XkOrderDetail drugs = new XkOrderDetail();
                    drugs.setDrugCode(xikangCode);
                    drugs.setDrugName(commonName);
                    drugs.setDrugNum(String.valueOf(cartNum));
                    drugs.setDrugCost(String.valueOf(cartQueryVo.getVipTruePrice() * cartNum));
                    drugs.setDrugPrice(String.valueOf(cartQueryVo.getVipTruePrice()));
                    drugsList.add(drugs);
                    totalAmount = NumberUtil.add(totalAmount,cartQueryVo.getVipTruePrice());
                }
            }
            xkOrder.setPayCost(  String.valueOf(yxStoreOrder.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
            // 放入 Drugs
            xkOrder.setDrugs(drugsList);

            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String xkOrderStr = JSONMapper.toJSONString(xkOrder);
            log.info("xkOrder={}",xkOrderStr);


            request.setData(xkOrderStr);

            //String str = JSONUtil.parseObj(request).toString();
            String str = JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String  resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            log.info("resURL={},token={}",resURL,accessToken);
            //接口调用
            //	JSONObject d = HttpUtils.doPostStr(resURL, parameters);
            List<NameValuePair> params= NameValuePairHelper.convert(parameters);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            HttpPost httpPost = new HttpPost(resURL);
            httpPost.setEntity(formEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            httpPost.releaseConnection();
            log.info("result={}",result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if("00000000".equals(jsonObject.getStr("statusCode"))) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("prescription_code",prescriptionCode);
                updateWrapper.set("pay_notice_flag",1);
                updateWrapper.set("pay_notice_date",new Date());
                internetHospitalDemandService.update(updateWrapper);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

  // 退款通知熙康医院
    public void refundNotice(String prescriptionCode){
        try {


            CloseableHttpClient httpClient = HttpClients.createDefault();

            String accessToken = getToken();
            //XK环境 接口地址
            //准备上传医生的参数签名***********开始***********具体参数根据实际接口来组装
            Request request = new Request();
            request.setBusinessType("025");
            request.setMethod("XKP094");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);

            XkRefund xkRefund = new XkRefund();
            xkRefund.setOperator("system");
            xkRefund.setPayDatetime(DateUtil.now());
            xkRefund.setPrescriptionCode(prescriptionCode);
            xkRefund.setSupplierCode(supplierCode);
            xkRefund.setSupplierName(supplierName);
            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String xkRefundStr = JSONMapper.toJSONString(xkRefund);
            log.info("xkRefund={}",xkRefundStr);

            request.setData(xkRefundStr);

            //String str = JSONUtil.parseObj(request).toString();
            String str = JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String  resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            log.info("resURL={},token={}",resURL,accessToken);
            //接口调用
            //	JSONObject d = HttpUtils.doPostStr(resURL, parameters);
            List<NameValuePair> params= NameValuePairHelper.convert(parameters);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            HttpPost httpPost = new HttpPost(resURL);
            httpPost.setEntity(formEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            httpPost.releaseConnection();
            log.info("result={}",result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if("00000000".equals(jsonObject.getStr("statusCode"))) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("prescription_code",prescriptionCode);
                updateWrapper.set("refund_notice_flag",1);
                updateWrapper.set("refund_notice_date",new Date());
                internetHospitalDemandService.update(updateWrapper);
            }

        }catch (Exception e) {

        }

    }

    // 物流通知熙康医院
    public void expressNotice(XkExpress xkExpress){
        try {


            CloseableHttpClient httpClient = HttpClients.createDefault();

            String accessToken = getToken();
            //XK环境 接口地址
            //准备上传医生的参数签名***********开始***********具体参数根据实际接口来组装
            Request request = new Request();
            request.setBusinessType("025");
            request.setMethod("XKP092");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);


            xkExpress.setSupplierCode(supplierCode);
            xkExpress.setSupplierName(supplierName);

            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String xkExpressStr = JSONMapper.toJSONString(xkExpress);
            log.info("xkExpress={}",xkExpressStr);

            request.setData(xkExpressStr);

            //String str = JSONUtil.parseObj(request).toString();
            String str = JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String  resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            log.info("resURL={},token={}",resURL,accessToken);
            //接口调用
            //	JSONObject d = HttpUtils.doPostStr(resURL, parameters);
            List<NameValuePair> params= NameValuePairHelper.convert(parameters);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            HttpPost httpPost = new HttpPost(resURL);
            httpPost.setEntity(formEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            httpPost.releaseConnection();
            log.info("result={}",result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if("00000000".equals(jsonObject.getStr("statusCode"))) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("prescription_code",xkExpress.getPrescriptionCode());
                updateWrapper.set("express_notice_flag",1);
                updateWrapper.set("express_notice_date",new Date());
                internetHospitalDemandService.update(updateWrapper);
            }

        }catch (Exception e) {

        }

    }

    // 签收通知熙康医院
    public void signNotice(XkSign xkSign){
        try {


            CloseableHttpClient httpClient = HttpClients.createDefault();

            String accessToken = getToken();
            //XK环境 接口地址
            //准备上传医生的参数签名***********开始***********具体参数根据实际接口来组装
            Request request = new Request();
            request.setBusinessType("025");
            request.setMethod("XKP093");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);



            xkSign.setSupplierCode(supplierCode);
            xkSign.setSupplierName(supplierName);
            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String xkSignStr = JSONMapper.toJSONString(xkSign);
            log.info("xkSign={}",xkSignStr);

            request.setData(xkSignStr);

            //String str = JSONUtil.parseObj(request).toString();
            String str = JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String  resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            log.info("resURL={},token={}",resURL,accessToken);
            //接口调用
            //	JSONObject d = HttpUtils.doPostStr(resURL, parameters);
            List<NameValuePair> params= NameValuePairHelper.convert(parameters);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            HttpPost httpPost = new HttpPost(resURL);
            httpPost.setEntity(formEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            httpPost.releaseConnection();
            log.info("result={}",result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if("00000000".equals(jsonObject.getStr("statusCode"))) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("prescription_code",xkSign.getPrescriptionCode());
                updateWrapper.set("sign_notice_flag",1);
                updateWrapper.set("sign_notice_date",new Date());
                internetHospitalDemandService.update(updateWrapper);
            }

        }catch (Exception e) {

        }

    }

}
