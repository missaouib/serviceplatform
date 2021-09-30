package co.yixiang.modules.xikang.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.xikang.dto.*;
import co.yixiang.modules.xikang.entity.XikangMedMapping;
import co.yixiang.modules.xikang.tools.JSONMapper;
import co.yixiang.modules.xikang.tools.SignatureUtil;
import co.yixiang.modules.xikang.tools.TokenUtil;
import co.yixiang.tools.express.support.NameValuePairHelper;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private YxUserService yxUserService;

    @Autowired
    private YxWechatUserService yxWechatUserService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    private XikangMedMappingService xikangMedMappingService;

    @Autowired
    private YxStoreOrderMapper yxStoreOrderMapper;

    @Autowired
    private YxUserAddressService yxUserAddressService;


    @Autowired
    private InternetHospitalDemandService internetHospitalDemandService;
    // 获取医生坐诊平台url
    public String h5Url4doctor(AttrDTO attrDTO) {

        try {

            YxUser yxUser = yxUserService.getById(attrDTO.getUid());
            YxWechatUserQueryVo yxWechatUserQueryVo = yxWechatUserService.getYxWechatUserById(attrDTO.getUid());

            if(yxUser == null || yxWechatUserQueryVo == null) {
                log.info("用户id[{}]没有找到主数据",attrDTO.getUid());

                return "";
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();

          /*  String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
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
            request.setBusinessType("024");
            request.setMethod("XKP090");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);

            UploadPatient uploadPatient = new UploadPatient();
            if( StrUtil.isNotBlank(yxUser.getCardId()) && IdcardUtil.isValidCard(yxUser.getCardId())) {
                int gender = IdcardUtil.getGenderByIdCard(yxUser.getCardId());
                if(gender == 1) {
                    uploadPatient.setPatientGender("1");
                } else {
                    uploadPatient.setPatientGender("2");
                }
            } else {
                uploadPatient.setPatientGender("0");
            }



            if(StrUtil.isNotBlank(yxWechatUserQueryVo.getOpenid())) {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getOpenid());
            } else {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getRoutineOpenid());
            }

            uploadPatient.setPatientIDCard(yxUser.getCardId());
            uploadPatient.setPatientName(yxUser.getRealName());
            uploadPatient.setPatientTel(yxUser.getPhone());
            uploadPatient.setPatientType("PT");
            uploadPatient.setRequestType("024");
            uploadPatient.setAppletsId("PURECHASE");
            uploadPatient.setSupplierCode(supplierCode);
            uploadPatient.setSupplierName(supplierName);
            uploadPatient.setAttrs(JSONUtil.parseObj(attrDTO).toString());
            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String patient = JSONMapper.toJSONString(uploadPatient);

            log.info("patient={}",patient);
            request.setData(patient);

           // String str = JSONUtil.parseObj(request).toString();

            String str= JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            System.out.println(resURL);
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
            if("SUCCESS".equals(jsonObject.getStr("statusMessage"))) {
               String h5Url = jsonObject.getJSONObject("data").getStr("h5Url");

               return  h5Url;
            }

        }catch (Exception e) {

        }

        return "";
    }



    // 获取申请处方的url
    public String h5Url4ApplyPrescription(AttrDTO attrDTO) {

        try {

            YxUser yxUser = yxUserService.getById(attrDTO.getUid());
            YxWechatUserQueryVo yxWechatUserQueryVo = yxWechatUserService.getYxWechatUserById(attrDTO.getUid());

            if(yxUser == null ) {
                log.info("用户id[{}]没有找到主数据",attrDTO.getUid());

                return "";
            }



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
            request.setMethod("XKP090");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);

            UploadPatient uploadPatient = new UploadPatient();

            uploadPatient.setOpenId("12345678");

          /*  if( ObjectUtil.isNotNull(yxWechatUserQueryVo) && StrUtil.isNotBlank(yxWechatUserQueryVo.getOpenid())) {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getOpenid());
            } else if (ObjectUtil.isNotNull(yxWechatUserQueryVo) && StrUtil.isNotBlank(yxWechatUserQueryVo.getRoutineOpenid())) {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getRoutineOpenid());
            }*/

            // 获取用药人信息

            YxDrugUsers yxDrugUsers = yxDrugUsersService.getById(attrDTO.getDrugUserid());
            if( StrUtil.isNotBlank(yxDrugUsers.getIdcard()) && IdcardUtil.isValidCard(yxDrugUsers.getIdcard())) {
                int gender = IdcardUtil.getGenderByIdCard(yxDrugUsers.getIdcard());
                if(gender == 1) {
                    uploadPatient.setPatientGender("1");
                } else {
                    uploadPatient.setPatientGender("2");
                }
            } else {
                uploadPatient.setPatientGender("0");
            }
            uploadPatient.setPatientIDCard(yxDrugUsers.getIdcard());
            uploadPatient.setPatientName(yxDrugUsers.getName());
            uploadPatient.setPatientTel(yxDrugUsers.getPhone());
            uploadPatient.setPatientType("PT");
            uploadPatient.setRequestType("025");
            uploadPatient.setAppletsId("PURECHASE");
            uploadPatient.setSupplierCode(supplierCode);
            uploadPatient.setSupplierName(supplierName);
            uploadPatient.setOrderId(attrDTO.getOrderId());
            uploadPatient.setAttrs(JSONMapper.toJSONString(attrDTO));


            QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("oid",attrDTO.getId());
            List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(wrapper);

            List<Drugs> drugsList = new ArrayList<>();

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

                    Drugs drugs = new Drugs();
                    drugs.setDrugCode(xikangCode);
                    drugs.setDrugName(commonName);
                    drugs.setDrugNum(String.valueOf(cartNum));

                    drugsList.add(drugs);

                }

            }
           if(CollUtil.isEmpty(drugsList)) {
               // 放入 Drugs
              /* Drugs drugs = new Drugs();
               drugs.setDrugCode("86900555000633b");
               drugs.setDrugName("头孢呋辛酯片 ");
               drugs.setDrugNum("2");
               drugsList.add(drugs);*/

               JSONObject jsonObject = JSONUtil.createObj();
               jsonObject.put("statusMessage","无法找到互联网医院药品对照");

               return jsonObject.toString();
           }




            // 放入 Drugs

            uploadPatient.setDrugs(drugsList);



            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String patient = JSONMapper.toJSONString(uploadPatient);
            log.info("patient={}",patient);


            request.setData(patient);

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

            return result;

        }catch (Exception e) {

        }

        return "";
    }


    public String h5Url4ApplyPrescriptionTest() {
        AttrDTO attrDTO = new AttrDTO();


        attrDTO.setUid(62);
        attrDTO.setCardNumber("123456");
        attrDTO.setOrderNumber("2222");
        attrDTO.setProjectCode("taipinglexiang");
        attrDTO.setCardType("advanced");
        attrDTO.setOrderId("2012301574666823");
        try {

            YxUser yxUser = yxUserService.getById(attrDTO.getUid());
            YxWechatUserQueryVo yxWechatUserQueryVo = yxWechatUserService.getYxWechatUserById(attrDTO.getUid());

            if(yxUser == null || yxWechatUserQueryVo == null) {
                log.info("用户id[{}]没有找到主数据",attrDTO.getUid());

                return "";
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();

          /*  String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
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
            request.setMethod("XKP090");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);

            UploadPatient uploadPatient = new UploadPatient();
            uploadPatient.setPatientGender("1");
            if(StrUtil.isNotBlank(yxWechatUserQueryVo.getOpenid())) {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getOpenid());
            } else {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getRoutineOpenid());
            }

            uploadPatient.setPatientIDCard(yxUser.getCardId());
            uploadPatient.setPatientName(yxUser.getRealName());
            uploadPatient.setPatientTel(yxUser.getPhone());
            uploadPatient.setPatientType("PT");
            uploadPatient.setRequestType("025");
            uploadPatient.setAppletsId("PURECHASE");
            uploadPatient.setSupplierCode(supplierCode);
            uploadPatient.setSupplierName(supplierName);
            uploadPatient.setOrderId(attrDTO.getOrderId());
          //  uploadPatient.setAttrs(JSONUtil.parseObj(attrDTO).toString());
            uploadPatient.setAttrs(JSONMapper.toJSONString(attrDTO));
            // 放入 Drugs
            Drugs drugs = new Drugs();
            drugs.setDrugCode("86900555000633b");
            drugs.setDrugName("头孢呋辛酯片 ");
            drugs.setDrugNum("2");
            List<Drugs> drugsList = new ArrayList<>();
            drugsList.add(drugs);
            uploadPatient.setDrugs(drugsList);

            //String patient = JSONUtil.parseObj(uploadPatient).toString();
            String patient = JSONMapper.toJSONString(uploadPatient);
            log.info("patient={}",patient);


            request.setData(patient);

           // String str = JSONUtil.parseObj(request).toString();
            String str = JSONMapper.toJSONString(request);
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
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
            if("SUCCESS".equals(jsonObject.getStr("statusMessage"))) {
                String h5Url = jsonObject.getJSONObject("data").getStr("h5Url");

                return  h5Url;
            }

        }catch (Exception e) {

        }

        return "";
    }



    public String h5Url4doctorTest() {

        AttrDTO attrDTO = new AttrDTO();


        attrDTO.setUid(1706);
        attrDTO.setCardNumber("123456");
        attrDTO.setOrderNumber("2222");
        attrDTO.setProjectCode("taipinglexiang");
        try {

            YxUser yxUser = yxUserService.getById(attrDTO.getUid());
            YxWechatUserQueryVo yxWechatUserQueryVo = yxWechatUserService.getYxWechatUserById(attrDTO.getUid());

            if(yxUser == null || yxWechatUserQueryVo == null) {
                log.info("用户id[{}]没有找到主数据",attrDTO.getUid());

                return "";
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();

        /*    String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
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
            request.setBusinessType("024");
            request.setMethod("XKP090");
            request.setSystemId("sykg");
            request.setSystemKey("sykg");
            request.setPageNum(0);
            request.setPageSize(10);

            UploadPatient uploadPatient = new UploadPatient();
            uploadPatient.setPatientGender("1");
            if(StrUtil.isNotBlank(yxWechatUserQueryVo.getOpenid())) {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getOpenid());
            } else {
                uploadPatient.setOpenId(yxWechatUserQueryVo.getRoutineOpenid());
            }

            uploadPatient.setPatientIDCard(yxUser.getCardId());
            uploadPatient.setPatientName(yxUser.getRealName());
            uploadPatient.setPatientTel(yxUser.getPhone());
            uploadPatient.setPatientType("PT");
            uploadPatient.setRequestType("024");
            uploadPatient.setAppletsId("PURECHASE");
            uploadPatient.setSupplierCode(supplierCode);
            uploadPatient.setSupplierName(supplierName);
            uploadPatient.setAttrs(JSONUtil.parseObj(attrDTO).toString());
            String patient = JSONUtil.parseObj(uploadPatient).toString();
            log.info("patient={}",patient);
            request.setData(patient);

            String str = JSONUtil.parseObj(request).toString();
            log.info("str={}",str);
            //准备上传医生的参数签名***********结束***********
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("request", str);
            //生成带有鉴权的URL
            String resURL = SignatureUtil.getOauth(resURLCall, parameters, accessToken, clientSecret);
            log.info("resURL={}",resURL);
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
            if("SUCCESS".equals(jsonObject.getStr("statusMessage"))) {
                String h5Url = jsonObject.getJSONObject("data").getStr("h5Url");

                return  h5Url;
            }

        }catch (Exception e) {

        }

        return "";
    }


    public String getToken() {
        try {
           // Object token_object = redisUtils.get("xikangToken");
            Object token_object = null;
            if(ObjectUtil.isNull(token_object)) {
                String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
                log.info("获取互联网医院token返回结果{}",content);
                Map<String, String> map = new Gson().fromJson(content, Map.class);
                String accessToken = map.get("access_token");
                redisUtils.set("xikangToken",accessToken, 20,TimeUnit.MINUTES);
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

    // 缴费通知
    @Async
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

    public void refundNotice(String prescriptionCode){
        try {


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


}
