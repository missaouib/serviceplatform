package co.yixiang.modules.yiyaobao.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.param.ExpressParam;
import co.yixiang.modules.order.web.param.OrderParam;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserAddressQueryVo;
import co.yixiang.modules.yiyaobao.dto.ImageModel;
import co.yixiang.modules.yiyaobao.dto.Prescription;
import co.yixiang.modules.yiyaobao.dto.PrescriptionDetail;
import co.yixiang.modules.yiyaobao.entity.PrescriptionDTO;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.tools.express.dao.Traces;
import co.yixiang.tools.express.domain.RouteResponseInfo;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import co.yixiang.modules.yiyaobao.dto.OrderResultDTO;
import co.yixiang.modules.yiyaobao.entity.AddressDTO;
import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.service.MdCountryService;
import co.yixiang.modules.yiyaobao.utils.CryptUtils;
import co.yixiang.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class OrderServiceImpl {
    @Value("${yiyaobao.apiUrl}")
    private String apiUrl;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.queryOrderStatusUrl}")
    private String queryOrderStatusUrl ;

    @Value("${yiyaobao.needEncrypt}")
    private Boolean needEncrypt ;

    @Value("${yiyaobao.yiyaoWechatApiUrl}")
    private String yiyaoWechatApiUrl;

    @Value("${yiyaobao.yiyaoWechatApiFlag}")
    private Boolean yiyaoWechatApiFlag;

    @Value("${yiyaobao.orderLogisticsByOrderIdUrl}")
    private String orderLogisticsByOrderIdUrl ;

    @Value("${yiyaobao.addSingleUrl}")
    private String addSingleUrl ;

    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${file.path}")
    private String path;

    @Value("${yiyaobao.getMedPartnerMedicineUrl}")
    private String yiyaobao_getMedPartnerMedicineUrl;

    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    TransactionDefinition transactionDefinition;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Value("${yiyaobao.projectNo}")
    private String yiyaobao_projectNo;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private YxWechatUserService wechatUserService;


    @Autowired
    private YxTemplateService templateService;

    private String yiyaoWechatAuthUrl = "/auth/login";

    private String yiyaoWechatGetOrderStatus = "/api/yiyaobao/orderStatus";

    private static Map<String,Integer> statusCoventMap = new HashedMap();

    static {
        statusCoventMap.put("01",5);
        statusCoventMap.put("90",6);
        statusCoventMap.put("14",0);
        statusCoventMap.put("20",1);
        statusCoventMap.put("30",1);
        statusCoventMap.put("19",2);
        statusCoventMap.put("45",4);
        statusCoventMap.put("94",7);

    }

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MdCountryService mdCountryService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrdOrderMapper yiyaobaoOrdOrderMapper;

    public Boolean generateVerifyCode(String name,String mobile){
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("patientName",name);
        jsonObject.put("patientMobile",mobile);
        jsonObject.put("codeType","h5");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap map = new LinkedMultiValueMap();
        log.info("jsonObject.toString()={}",jsonObject.toString());
        String data = "";

        if(needEncrypt) {
            data = CryptUtils.encryptString(jsonObject.toString(), "b2ctestkey");
        } else {
            data = jsonObject.toString();
        }

        log.info("CryptUtils.encryptString={}",data);
        map.add("data",data);
        map.add("token","22");
        map.add("action","YM22");
        map.add("method","getVerifyCode");
        HttpEntity request = new HttpEntity(map, headers);

        ResponseEntity<String> resultEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
        String body = resultEntity.getBody();
        log.info("getVerifyCode ???????????? {}",body);
        // ??????

        log.info(jsonObject.toString());
        JSONObject result = JSONUtil.parseObj(body);
        log.info(body);

        if ("ok".equals(result.getStr("status"))) {
            return true;
        } else {
            return false;
        }

    }

    public AddressDTO getAddressDTO(String provinceName, String cityName, String districtName){
         // ????????????code
        QueryWrapper queryWrapper_provice = new QueryWrapper();
      //  queryWrapper_provice.eq("NAME",provinceName);
        queryWrapper_provice.apply(" {0} LIKE CONCAT('%',NAME,'%') ",provinceName);
        queryWrapper_provice.eq("TREE_ID","1");
        MdCountry province = mdCountryService.getOne(queryWrapper_provice,false);

        String provinceCode = "";
        String cityCode = "";
        String districtCode = "";
        if(province != null) {
            provinceCode = province.getCode();
            // ????????????
            QueryWrapper queryWrapper_city = new QueryWrapper();
           // queryWrapper_city.eq("NAME",cityName);
            queryWrapper_city.apply(" {0} LIKE CONCAT('%',NAME,'%') ",cityName);
            queryWrapper_city.eq("TREE_ID","2");
            queryWrapper_city.eq("PARENT_ID",province.getId());
            MdCountry city = mdCountryService.getOne(queryWrapper_city,false);

            if(city != null) {
                cityCode = city.getCode();
                // ?????????
                QueryWrapper queryWrapper_district = new QueryWrapper();
                queryWrapper_district.eq("NAME",districtName);
                queryWrapper_district.eq("TREE_ID","3");
                queryWrapper_district.eq("PARENT_ID",city.getId());
                MdCountry district = mdCountryService.getOne(queryWrapper_district,false);

                if(district != null){
                    districtCode = district.getCode();
                }
            }
        }

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setProvinceCode(provinceCode);
        addressDTO.setProvinceName(provinceName);
        addressDTO.setCityCode(cityCode);
        addressDTO.setCityName(cityName);
        addressDTO.setDistrictCode(districtCode);
        addressDTO.setDistrictName(districtName);

        return  addressDTO;

    }

    public String  uploadOrder(PrescriptionDTO prescriptionDTO){
        try {
            JSONObject jsonObject = JSONUtil.parseObj(prescriptionDTO);
            PrescriptionDTO prescriptionDTO1 = new PrescriptionDTO();
            BeanUtils.copyProperties(prescriptionDTO,prescriptionDTO1);
            prescriptionDTO1.setImagePath("");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap map = new LinkedMultiValueMap();
            log.info("??????????????????={}",prescriptionDTO);
            String data = "";

            if(needEncrypt) {
                data = CryptUtils.encryptString(jsonObject.toString(), "b2ctestkey");
            }else {
                data = jsonObject.toString();
            }

            //   log.info("CryptUtils.encryptString={}",data);
            map.add("data",data);
            map.add("token","22");
            map.add("action","YM22");
            map.add("method","saveYiyaoMallPrs");
            // map.add("method","savePrs");
            HttpEntity request = new HttpEntity(map, headers);
            ResponseEntity<String> resultEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            String body = resultEntity.getBody();
            log.info(jsonObject.toString());
            log.info(body);
            OrderResultDTO orderResultDTO = JSONUtil.toBean(body, OrderResultDTO.class);
            String orderNo = "";
            if("ok".equals(orderResultDTO.getStatus())) {
                log.info("{}",orderResultDTO);

                log.info("??????={}", CryptUtils.decryptString(orderResultDTO.getData(), "b2ctestkey", "GBK"));

                String ret = URLDecoder.decode(URLEncoder.encode(orderResultDTO.getData()));
                String jsonStr = CryptUtils.decryptString(ret, "b2ctestkey", "GBK");
                JSONObject obj = JSONUtil.parseObj(jsonStr);
                orderNo = obj.getStr("orderNo");


            } else {

                throw new ErrorRequestException(orderResultDTO.getMsg());
            }
            return orderNo;
        }catch (Exception e) {
            e.printStackTrace();
            throw new ErrorRequestException("?????????????????????????????????");
        }


    }

    public Integer queryOrderStatus(String orderNo) {
        Integer orderStatus = 5;
        String url = yiyaobao_apiUrl_external + queryOrderStatusUrl;
        net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        jsonObject.element("prsNo","");
        jsonObject.element("hospitalName","");
        jsonObject.element("jdOrderId","");
        jsonObject.element("orderNo",orderNo);

        String requestBody = jsonObject.toString(); //

        try {
            long timestamp = System.currentTimeMillis(); // ?????????????????????
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // ??????APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // ????????????
            log.info("???????????????????????????????????????{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);

            if(object.getBool("success")) {
                 JSONObject result1 = object.getJSONObject("result");
                 String orderStatus_yiyaobao = result1.getStr("orderStatus");
                if(statusCoventMap.containsKey(orderStatus_yiyaobao)){
                    orderStatus = statusCoventMap.get(orderStatus_yiyaobao);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderStatus;
    }

    public String queryToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("username","admin");
        jsonObject.put("password","O7yM6QqcWW1cMdI4zlgcM01HW/rsy1IFpkQQpUmOgG/W0k6j9VwTVROT32e8jRwBTWMQFrXZRaip9CeMckjXeg==");
        jsonObject.put("code","callcenter");
        HttpEntity request = new HttpEntity(jsonObject.toString(), headers);
        String authUrl = yiyaoWechatApiUrl + yiyaoWechatAuthUrl;
        ResponseEntity<String> resultEntity = restTemplate.exchange(authUrl, HttpMethod.POST, request, String.class);
        String body = resultEntity.getBody();
        log.info("authUrl ???????????? {}",body);
        String token = JSONUtil.parseObj(body).getStr("token");
        return  token;
    }

    public String getYiyaoToken(){
        String yiyaoToken = (String)redisUtils.get("yiyaoToken");
        if(StrUtil.isBlank(yiyaoToken)) {
            yiyaoToken = queryToken();
            redisUtils.set("yiyaoToken",yiyaoToken,2, TimeUnit.HOURS);
        }
        return yiyaoToken;
    }

    public Map<String ,Integer> queryYiyaobaoOrderStatus(String orderNo) {
        String token = getYiyaoToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization",token);
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("orderNO",orderNo);
        map.add("flag",yiyaoWechatApiFlag);

        HttpEntity request = new HttpEntity(map, headers);
        String authUrl = yiyaoWechatApiUrl + yiyaoWechatGetOrderStatus;
        ResponseEntity<String> resultEntity = restTemplate.exchange(authUrl, HttpMethod.POST, request, String.class);
        String body = resultEntity.getBody();
        log.info("authUrl ???????????? {}",body);
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String yiyaobaoStatus = jsonObject.getStr("data");
        Integer status=5;
        Integer paid=0;
        if(yiyaobaoStatus.equals("01")) {  //?????????
            status=5;
            paid=0;
        }else if(yiyaobaoStatus.equals("14") || yiyaobaoStatus.equals("15")) {  //?????????
            status=0;
            paid=0;
        }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                yiyaobaoStatus.equals("42") || yiyaobaoStatus.equals("50")
        ) { //?????????
            status=0;
            paid=1;
        } else if(yiyaobaoStatus.equals("43")){ //?????????
            status=1;
            paid=1;
        }else if(yiyaobaoStatus.equals("90")){  //???????????????
            status=6;
            paid=0;
        }
        Map result = new HashMap<>();
        result.put("status",status);
        result.put("paid",paid);
        return result;
    }

    @DS("multi-datasource1")
    public void cancelYiyaobaoOrder(String orderNo){
        String orderId = ordOrderMapper.getOrderIdByNo(orderNo);
        if(StrUtil.isNotBlank(orderId)) {
            ordOrderMapper.updatePrescriptionApp(orderId);
            ordOrderMapper.updatePrescription(orderId);
            ordOrderMapper.updateOrderStatus(orderId);
        }
    }


    @DS("multi-datasource1")
    public Paging<OrderVo> getYiyaobaoOrderbyMobile(OrderQueryParam orderQueryParam) {

        Page page = new Page();
        // ??????????????????
        page.setCurrent(orderQueryParam.getPage());
        // ???????????????
        page.setSize(orderQueryParam.getLimit());
        page.setOrders( Arrays.asList(OrderItem.desc("t.orderDate")));

        IPage<OrderVo> iPage = ordOrderMapper.getOrderPageList_2(page,orderQueryParam);

        return new Paging(iPage);
    }


    @DS("multi-datasource1")
    public OrderVo getYiyaobaoOrderbyOrderId(String orderId) {



        OrderVo orderVo = ordOrderMapper.getYiyaobaoOrderbyOrderId(orderId);

        return orderVo;
    }


    @DS("multi-datasource1")
    public List<OrderDetailVo> getOrderDetail( String orderId){
        return ordOrderMapper.getOrderDetail(orderId);
    }


    public ExpressInfo queryOrderLogisticsProcess(ExpressParam expressInfoDo) {
        ExpressInfo expressInfo = new ExpressInfo();
        expressInfo.setSuccess(false);
        String url = yiyaobao_apiUrl_external + orderLogisticsByOrderIdUrl;
        //net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderId",expressInfoDo.getYiyaobaoOrderId());

        String requestBody = jsonObject.toString(); //
        String express = "";
        try {
            long timestamp = System.currentTimeMillis(); // ?????????????????????
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // ??????APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // ????????????
            log.info("?????????????????????????????????????????????{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {
                JSONArray jsonArray = object.getJSONArray("result");
                List<Traces> tracesList= new ArrayList<>();

                expressInfo.setLogisticCode(expressInfoDo.getLogisticCode());
                expressInfo.setOrderCode(expressInfoDo.getOrderCode());
                expressInfo.setShipperCode(expressInfoDo.getShipperCode());
                expressInfo.setShipperName("");
                expressInfo.setSuccess(true);
                for(int i=0;i< jsonArray.size();i++) {
                   JSONObject js = jsonArray.getJSONObject(i);
                   Integer processNo = js.getInt("processNo");
                   String processTime = js.getStr("processTime");
                   String processRemark = js.getStr("processRemark");

                    Traces trace = new Traces();
                    trace.setAcceptStation(processRemark);
                    trace.setAcceptTime(processTime);
                    tracesList.add(trace);
                }
                expressInfo.setTraces(tracesList);
            }


           /* ExpressInfo ei = new ExpressInfo();
            ei.setLogisticCode(LogisticCode);
            ei.setOrderCode(OrderCode);
            ei.setShipperCode(ShipperCode);
            ei.setShipperName(shipperName);
            ei.setSuccess(true);
            List<Traces> tracesList= new ArrayList<>();
            for(RouteResponseInfo.Body.RouteResponse.Route route: list ){
                Traces trace = new Traces();
                trace.setAcceptStation(route.getRemark());
                trace.setAcceptTime(route.getAcceptTime());
                tracesList.add(trace);
            }
            ei.setTraces(tracesList);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressInfo;
    }

    @DS("multi-datasource1")
    public String getMedicineImageBySku(String yiyaobaoSku) {
        return ordOrderMapper.getMedicineImageBySku(yiyaobaoSku);
    }
    @DS("multi-datasource1")
    public void takeOrder(String orderId) {
        ordOrderMapper.takeOrder(orderId);
    }

    @DS("multi-datasource1")
    @Async
    public Future<String> generateVerifyCode(String phone){
        String verifyCode = "";
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
       try {
           verifyCode =  RandomUtil.randomNumbers(6);
           ordOrderMapper.updateVerifyCodeInvalid(phone);
           ordOrderMapper.insertVerifyCode(phone,verifyCode);
           dataSourceTransactionManager.commit(transactionStatus);//??????
       }catch (Exception e) {
           dataSourceTransactionManager.rollback(transactionStatus);
       }
      return new AsyncResult<String>(verifyCode);
    };


    @DS("multi-datasource1")
    public String generateVerifyCode2(String phone){
        String verifyCode = "";

        verifyCode =  RandomUtil.randomNumbers(6);
        ordOrderMapper.updateVerifyCodeInvalid(phone);
        ordOrderMapper.insertVerifyCode(phone,verifyCode);


        return verifyCode;
    };


    @DS("multi-datasource1")
    public Boolean changeOrderNo(String sourceOrderNo,String targetOrderNo) {
         return  ordOrderMapper.changeOrderNo(sourceOrderNo,targetOrderNo);
    }

    @DS("multi-datasource1")
    public String queryYiyaobaoOrderId(String orderNo) {
        return  ordOrderMapper.queryYiyaobaoOrderId(orderNo);
    }

    @DS("multi-datasource1")
    public Boolean checkPassPrescription(String orderNo) {
        return  ordOrderMapper.checkPassPrescription(orderNo);
    }


    @DS("multi-datasource1")
    public Boolean changeOrderReceiver(String OrderNo,String receiverName,String receiverPhone,String factUserPhone) {
        return  ordOrderMapper.changeOrderReceiver(OrderNo,receiverName,receiverPhone,factUserPhone);
    }


    public Boolean sendOrder2YiyaobaoCloud(String orderId){

        // ??????????????????

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("?????????["+ orderId + "]????????????");
        }

        if(  yxStoreOrder.getUploadYiyaobaoFlag() == 1) {
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????????????????");
        }

        // ??????????????????
        // List<YxStoreCart> storeCartList = yxStoreCartService.list(new QueryWrapper<YxStoreCart>().in("id",Arrays.asList(yxStoreOrder.getCartId().split(","))));
        List<YxStoreOrderCartInfo> yxStoreOrderCartInfoList = yxStoreOrderCartInfoService.list(new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        if(CollUtil.isEmpty(yxStoreOrderCartInfoList)){
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????");
        }

        // ????????????
        YxUserAddress yxUserAddress = yxUserAddressService.getById(yxStoreOrder.getAddressId());

        String province = "";
        String city = "";
        String district = "";
        String receiver = "";
        String receiverMobile = "";
        String addressDetail = "";
        if(yxUserAddress != null) {
            province = yxUserAddress.getProvince();
            city = yxUserAddress.getCity();
            district = yxUserAddress.getDistrict();
            receiver = yxUserAddress.getRealName();
            receiverMobile = yxUserAddress.getPhone();
            addressDetail = yxUserAddress.getDetail();
        }

        /** ??????????????????
         */
        Prescription pres = new Prescription();

        // ??????
        if(StrUtil.isNotBlank(yxStoreOrder.getImagePath()) && yxStoreOrder.getImagePath().contains(localUrl)){
            String urlpath = yxStoreOrder.getImagePath().replace(localUrl+"/file","");

            //   path.replace("\\",File.)

            String localFilePath = path + urlpath;
            String extensionName = FileUtil.getExtensionName(localFilePath);
            String base64 = Base64Util.getImageBinary(localFilePath,extensionName);

            ImageModel imageModel = new ImageModel();
            imageModel.setImageBase64(base64);

            List<ImageModel> imageModelList = new ArrayList<>();
            imageModelList.add(imageModel);
            pres.setImages(imageModelList);
        }
        // ???????????? ??????
        pres.setName(yxStoreOrder.getFactUserName());
        // ???????????????
        pres.setMobile(yxStoreOrder.getFactUserPhone());

        // ????????????
        pres.setDoctorName("?????????");
        // ????????????
        pres.setDepartment("????????????");
        pres.setDeptCode("200301");
        // ???????????? ??????
        pres.setHospitalName("????????????");

        // ????????????
        pres.setAddress(addressDetail);
        pres.setProvinceName(province);
        pres.setCityName(city);
        pres.setDistrictName(district);
        pres.setReceiver(receiver);
        pres.setReceiverMobile(receiverMobile);

        // ????????????
        pres.setPrescribeDate(yxStoreOrder.getAddTime()*1000L);

        // ????????? ??????
        pres.setPrescripNo(yxStoreOrder.getOrderId() );
        // ???????????? ??????
        pres.setRegisterType(0L);
        //??????(0:??????;1:??????)
        pres.setFeeType("0");



        // ????????????(01-??????;02-??????;70-???????????????)
        pres.setPayMethod("70");

        //????????????(00-?????????10-???????????????99-????????????)
        pres.setDeliverType("10");
        pres.setRemark(yxStoreOrder.getRemark());
        pres.setRegisterDate(yxStoreOrder.getAddTime()*1000L);

        pres.setRegisterType(1L);

        // pres.setDiscount();
        /*
         * ????????????????????????????????????????????????).
         */
        List<PrescriptionDetail> details = new ArrayList<PrescriptionDetail>();
        // ??????????????????
        Double totalTruePrice = 0d;

        // ????????????????????????
        Double totalVipDiscountAmount = 0d;
        String sku = "*010";
        String medName = "????????????3%???";
        for(YxStoreOrderCartInfo yxStoreOrderCartInfo: yxStoreOrderCartInfoList) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(yxStoreOrderCartInfo.getCartInfo(),YxStoreCartQueryVo.class);
            YxStoreProductQueryVo yxStoreProduct = cartQueryVo.getProductInfo();

            if(yxStoreProduct.getTaxRate() != null &&  yxStoreProduct.getTaxRate().doubleValue()== new Double("13").doubleValue() ) {
                sku = "*009";
                medName = "????????????13%???";
            }

            // ???????????????
            /*YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",cartQueryVo.getProductAttrUnique()));
            if(yxStoreProductAttrValue == null) {
                throw new BadRequestException("????????????????????????????????????");
            }
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("id",yxStoreProductAttrValue.getProductId());
            queryWrapper1.select("id","store_name","common_name","yiyaobao_sku");
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper1);*/
            if(yxStoreProduct == null) {
                throw new BadRequestException("??????????????????????????????");
            }
            PrescriptionDetail detail = new PrescriptionDetail();
            // ???????????? ??????
            detail.setMedCode(yxStoreProduct.getYiyaobaoSku());
            // ???????????? ??????
            detail.setMedName(yxStoreProduct.getStoreName());
            // ???????????? ??????
            detail.setAmount(new BigDecimal(cartQueryVo.getCartNum()));
            detail.setUnitPrice( new BigDecimal(cartQueryVo.getTruePrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
            // ???????????? = (?????? - ?????????)*????????????
            BigDecimal vipDiscountAmount = new BigDecimal( NumberUtil.mul( cartQueryVo.getCartNum().intValue(),  NumberUtil.sub(cartQueryVo.getTruePrice(),cartQueryVo.getVipTruePrice()))).setScale(2,BigDecimal.ROUND_HALF_UP);
            detail.setDiscountAmount(vipDiscountAmount);
            // ?????????
            detail.setDiscount(cartQueryVo.getDiscount());
            details.add(detail);

            totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(cartQueryVo.getCartNum().doubleValue() , cartQueryVo.getTruePrice().doubleValue() ));
            totalVipDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue(), vipDiscountAmount.doubleValue() );
        }
        // ?????????>0 ???
        if(yxStoreOrder.getTotalPostage() != null && yxStoreOrder.getTotalPostage().doubleValue() > 0) {
            PrescriptionDetail detail = new PrescriptionDetail();
            // ???????????? ??????
            detail.setMedCode(sku);
            // ???????????? ??????
            detail.setMedName(medName);
            // ???????????? ??????
            detail.setAmount(new BigDecimal(1));
            detail.setUnitPrice(yxStoreOrder.getTotalPostage() );
            // ???????????? = (?????? - ?????????)*????????????
            //  BigDecimal vipDiscountAmount = new BigDecimal( NumberUtil.mul( cartQueryVo.getCartNum().intValue(),  NumberUtil.sub(cartQueryVo.getTruePrice(),cartQueryVo.getVipTruePrice()))).setScale(2,BigDecimal.ROUND_HALF_UP);
            detail.setDiscountAmount(new BigDecimal(0));
            // ?????????
            //   detail.setDiscount(cartQueryVo.getDiscount());
            details.add(detail);
        }
        // ??????????????????????????????????????????????????????????????????setDetail??????setDetails;
        pres.setDetails(details);

        //?????????
        pres.setTotalAmount(yxStoreOrder.getPayPrice().setScale(2,BigDecimal.ROUND_HALF_UP));

        //???????????????
        pres.setPaidAmount(yxStoreOrder.getPayPrice());

        // ????????????
        // ???????????? = ??????????????????  + ????????????????????? + ??????????????????
        Double totalDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue() , yxStoreOrder.getCouponPrice().doubleValue() , yxStoreOrder.getDeductionPrice().doubleValue() ).doubleValue();
        pres.setDiscountAmount(new BigDecimal(totalDiscountAmount).setScale(2,BigDecimal.ROUND_HALF_UP));

        //?????????(??????????????????)
        // ????????? = (??????-??????)/??????
        if(totalTruePrice == 0) {
            pres.setDiscount( new BigDecimal( 0).setScale(2,BigDecimal.ROUND_HALF_UP));
        } else {
            pres.setDiscount( new BigDecimal( 1- NumberUtil.div(totalDiscountAmount,totalTruePrice)).setScale(2,BigDecimal.ROUND_HALF_UP));
        }


        String url = yiyaobao_apiUrl_external + addSingleUrl;

        String requestBody = JSONUtil.parseObj(pres).toString(); //

        try {
            long timestamp = System.currentTimeMillis(); // ?????????????????????
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // ??????APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // ????????????
            log.info("?????????????????????????????????{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {

                yxStoreOrder.setUploadYiyaobaoFlag(1);
                yxStoreOrder.setUploadYiyaobaoTime(new Date());

                yxStoreOrderService.updateById(yxStoreOrder);

                //?????????????????????????????????????????????
                if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(yxStoreOrder.getProjectCode())) {
                    //??????
                    yiyaobaoOrdOrderMapper.updateYiyaobaoOrderSourceByPrescripNo(pres.getPrescripNo(), "28");
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean sendOrder2YiyaobaoCloudCancel(String orderId){

        // ??????????????????

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("?????????["+ orderId + "]????????????");
        }

        if(   yxStoreOrder.getUploadYiyaobaoRefundFlag() == 1) {
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????????????????");
        }

        // ??????????????????
        // List<YxStoreCart> storeCartList = yxStoreCartService.list(new QueryWrapper<YxStoreCart>().in("id",Arrays.asList(yxStoreOrder.getCartId().split(","))));
        List<YxStoreOrderCartInfo> yxStoreOrderCartInfoList = yxStoreOrderCartInfoService.list(new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        if(CollUtil.isEmpty(yxStoreOrderCartInfoList)){
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????");
        }

        // ????????????
        YxUserAddress yxUserAddress = yxUserAddressService.getById(yxStoreOrder.getAddressId());

        String province = "";
        String city = "";
        String district = "";
        String receiver = "";
        String receiverMobile = "";
        String addressDetail = "";
        if(yxUserAddress != null) {
            province = yxUserAddress.getProvince();
            city = yxUserAddress.getCity();
            district = yxUserAddress.getDistrict();
            receiver = yxUserAddress.getRealName();
            receiverMobile = yxUserAddress.getPhone();
            addressDetail = yxUserAddress.getDetail();
        }

        /** ??????????????????
         */
        Prescription pres = new Prescription();


        // ??????
        if(StrUtil.isNotBlank(yxStoreOrder.getImagePath()) && yxStoreOrder.getImagePath().contains(localUrl)){
            String urlpath = yxStoreOrder.getImagePath().replace(localUrl+"/file","");

            //   path.replace("\\",File.)

            String localFilePath = path + urlpath;
            String extensionName = FileUtil.getExtensionName(localFilePath);
            String base64 = Base64Util.getImageBinary(localFilePath,extensionName);

            ImageModel imageModel = new ImageModel();
            imageModel.setImageBase64(base64);

            List<ImageModel> imageModelList = new ArrayList<>();
            imageModelList.add(imageModel);
            pres.setImages(imageModelList);
        }

        // ???????????? ??????
        pres.setName(yxStoreOrder.getFactUserName());
        // ???????????????
        pres.setMobile(yxStoreOrder.getFactUserPhone());

        // ????????????
        pres.setDoctorName("?????????");
        // ????????????
        pres.setDepartment("????????????");
        pres.setDeptCode("200301");
        // ???????????? ??????
        pres.setHospitalName("????????????");

        // ????????????
        pres.setAddress(addressDetail);
        pres.setProvinceName(province);
        pres.setCityName(city);
        pres.setDistrictName(district);
        pres.setReceiver(receiver);
        pres.setReceiverMobile(receiverMobile);

        // ????????????
        pres.setPrescribeDate(yxStoreOrder.getAddTime()*1000L);

        // ????????? ??????
        pres.setPrescripNo(yxStoreOrder.getOrderId() + "_1" );
        pres.setOriginalPrescripNo(yxStoreOrder.getOrderId());
        // ???????????? ??????
        pres.setRegisterType(0L);
        //??????(0:??????;1:??????)
        pres.setFeeType("0");



        // ????????????(01-??????;02-??????;70-???????????????)
        pres.setPayMethod("70");

        //????????????(00-?????????10-???????????????99-????????????)
        pres.setDeliverType("10");
        pres.setRemark(yxStoreOrder.getRemark());
        pres.setRegisterDate(yxStoreOrder.getAddTime()*1000L);
        pres.setRegisterType(1L);

        // pres.setDiscount();
        /*
         * ????????????????????????????????????????????????).
         */
        List<PrescriptionDetail> details = new ArrayList<PrescriptionDetail>();
        // ??????????????????
        Double totalTruePrice = 0d;

        // ????????????????????????
        Double totalVipDiscountAmount = 0d;

        for(YxStoreOrderCartInfo yxStoreOrderCartInfo: yxStoreOrderCartInfoList) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(yxStoreOrderCartInfo.getCartInfo(),YxStoreCartQueryVo.class);
            YxStoreProductQueryVo yxStoreProduct = cartQueryVo.getProductInfo();
            // ???????????????
            /*YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",cartQueryVo.getProductAttrUnique()));
            if(yxStoreProductAttrValue == null) {
                throw new BadRequestException("????????????????????????????????????");
            }
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("id",yxStoreProductAttrValue.getProductId());
            queryWrapper1.select("id","store_name","common_name","yiyaobao_sku");
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper1);*/
            if(yxStoreProduct == null) {
                throw new BadRequestException("??????????????????????????????");
            }
            PrescriptionDetail detail = new PrescriptionDetail();
            // ???????????? ??????
            detail.setMedCode(yxStoreProduct.getYiyaobaoSku());
            // ???????????? ??????
            detail.setMedName(yxStoreProduct.getStoreName());
            // ???????????? ??????
            // ???????????????????????????
            detail.setAmount(new BigDecimal(cartQueryVo.getCartNum() * -1));
            detail.setUnitPrice( new BigDecimal(cartQueryVo.getTruePrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
            // ???????????? = (?????? - ?????????)*????????????
            // BigDecimal vipDiscountAmount = new BigDecimal( NumberUtil.mul( cartQueryVo.getCartNum().intValue(),  NumberUtil.sub(cartQueryVo.getTruePrice(),cartQueryVo.getVipTruePrice()))).setScale(2,BigDecimal.ROUND_HALF_UP);
            //?????????????????????????????????
            // detail.setDiscountAmount(cartQueryVo.getDiscountAmount());
            // ?????????
            //??????????????????????????????
            //  detail.setDiscount(cartQueryVo.getDiscount());
            details.add(detail);

            totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(cartQueryVo.getCartNum().doubleValue() , cartQueryVo.getTruePrice().doubleValue() ));
            totalVipDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue(), cartQueryVo.getDiscountAmount().doubleValue() );
        }


        // ??????????????????????????????????????????????????????????????????setDetail??????setDetails;
        pres.setDetails(details);
        // ???????????????????????????
        totalTruePrice = NumberUtil.mul(totalTruePrice , new Double(-1));
        //?????????
        pres.setTotalAmount(new BigDecimal(totalTruePrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        //???????????????
        pres.setPaidAmount(yxStoreOrder.getPayPrice());

        // ????????????
        // ???????????? = ??????????????????  + ????????????????????? + ??????????????????
        Double totalDiscountAmount = totalVipDiscountAmount.doubleValue() ;
        //?????????????????????????????????
        // pres.setDiscountAmount(new BigDecimal(totalDiscountAmount).setScale(2,BigDecimal.ROUND_HALF_UP));

        //?????????(??????????????????)
        // ????????? = (??????-??????)/??????
        //??????????????????????????????
        // pres.setDiscount( new BigDecimal( 1- NumberUtil.div(totalDiscountAmount,totalTruePrice)).setScale(2,BigDecimal.ROUND_HALF_UP));

        String url = yiyaobao_apiUrl_external + addSingleUrl;

        String requestBody = JSONUtil.parseObj(pres).toString(); //

        try {
            long timestamp = System.currentTimeMillis(); // ?????????????????????
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // ??????APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // ????????????
            log.info("???????????????????????????????????????{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {

                yxStoreOrder.setUploadYiyaobaoRefundFlag(1);
                yxStoreOrder.setUploadYiyaobaoRefundTime(new Date());

                yxStoreOrderService.updateById(yxStoreOrder);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public Boolean sendOrder2YiyaobaoStore(String orderId){

        // ??????????????????

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("?????????["+ orderId + "]????????????");
        }

        if(  yxStoreOrder.getUploadYiyaobaoFlag() == 1) {
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????????????????");
        }

        // ??????????????????
        // List<YxStoreCart> storeCartList = yxStoreCartService.list(new QueryWrapper<YxStoreCart>().in("id",Arrays.asList(yxStoreOrder.getCartId().split(","))));
        List<YxStoreOrderCartInfo> yxStoreOrderCartInfoList = yxStoreOrderCartInfoService.list(new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        if(CollUtil.isEmpty(yxStoreOrderCartInfoList)){
            throw new BadRequestException("?????????["+ orderId + "]????????????????????????");
        }

        // ????????????
        //  YxUserAddress yxUserAddress = yxUserAddressService.getById(yxStoreOrder.getAddressId());

        String province = yxStoreOrder.getProvinceName();
        String city = yxStoreOrder.getCityName();
        String district = yxStoreOrder.getDistrictName();
        String receiver = yxStoreOrder.getRealName();
        String receiverMobile = yxStoreOrder.getUserPhone();
        String addressDetail = yxStoreOrder.getAddress();
        String provinceCode = "";
        String cityCode = "";
        String districtCode = "";

        AddressDTO addressDTO = getAddressDTO(province,city,district);
        provinceCode = addressDTO.getProvinceCode();
        cityCode = addressDTO.getCityCode();
        districtCode = addressDTO.getDistrictCode();


        // ??????base64
        //  String imgFilePath="http://d.hiphotos.baidu.com/image/pic/item/a044ad345982b2b713b5ad7d3aadcbef76099b65.jpg";

        String base64_str =  "";
        String imagePath = yxStoreOrder.getImagePath();
        try {
            if(StrUtil.isBlank(imagePath)) {
                //              String defaultImage = path + "static" + File.separator  + "defaultMed.jpg";
//log.info(" defaultImage imagePath = {}",defaultImage);
                base64_str = new ImageUtil().localImageToBase64("otc.jpg");
            } /*else if( imagePath.contains(localUrl)  ){
                // ????????????????????????
                String str = localUrl + "/file/";
                imagePath =  imagePath.replace(str,path);
                log.info("?????????????????????????????????????????????{}",imagePath);

                base64_str = new ImageUtil().localImageToBase64_2(imagePath);
                log.info("base64_str.length==={}",base64_str.length());
            }*/ else {
                //String imagePathConvert = imagePath.replace(localUrl,imageUrl);
                String imagePathConvert = imagePath;
                log.info("imagePathConvert === {}",imagePathConvert);
                base64_str = ImageUtil.encodeImageToBase64(new URL(imagePathConvert));
                log.info("base64_str.length==={}",base64_str.length());
            }
            base64_str = "data:image/jpeg;base64,"+ base64_str;
            base64_str = URLEncoder.encode(base64_str,"UTF-8")  ;
        } catch (Exception e) {
            e.printStackTrace();
        }


        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
        prescriptionDTO.setAddress(addressDetail);
        prescriptionDTO.setCityCode(cityCode);
        prescriptionDTO.setProvinceCode(provinceCode);
        prescriptionDTO.setDistrictCode(districtCode);

        prescriptionDTO.setCustomerRequirement(yxStoreOrder.getRemark());
        if(StrUtil.isNotBlank(yxStoreOrder.getDrugUserPhone())) {
            prescriptionDTO.setPatientMobile(yxStoreOrder.getDrugUserPhone()); // ???????????????
        } else {
            prescriptionDTO.setPatientMobile(yxStoreOrder.getFactUserPhone()); // ??????????????????
        }

        prescriptionDTO.setPatientName(yxStoreOrder.getDrugUserName()); // ????????????
        prescriptionDTO.setContactMobile(yxStoreOrder.getFactUserPhone());  // ??????????????????
        prescriptionDTO.setReceiver(receiver);  // ?????????
        prescriptionDTO.setReceiverMobile(receiverMobile);  // ???????????????
        prescriptionDTO.setPayType("10");  // 10 ??????????????????
        if(ProjectNameEnum.YAOLIAN.getValue().equals(yxStoreOrder.getProjectCode())){
            prescriptionDTO.setPayType("60");
        }else if (yxStoreOrder.getPaid() == 1) {  // ?????????
            prescriptionDTO.setPayType("40");
        }

        prescriptionDTO.setOrderNo(yxStoreOrder.getOrderId()); // ?????????
        if(yxStoreOrder.getNeedInvoiceFlag() != null && yxStoreOrder.getNeedInvoiceFlag() == 1) {
            prescriptionDTO.setInvoiceType("10");
            prescriptionDTO.setInvoiceTitle(yxStoreOrder.getInvoiceName());
            prescriptionDTO.setInvoiceRemark(yxStoreOrder.getInvoiceMail());
            prescriptionDTO.setInvoiceAmount(yxStoreOrder.getPayPrice().toString());
        } else {
            prescriptionDTO.setInvoiceType("00");
        }


        String projectCode = yxStoreOrder.getProjectCode();
        if(StrUtil.isNotBlank(projectCode)) {
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,yxStoreOrder.getProjectCode()));
           // List<Product4project> product4projectList = product4projectService.list(new QueryWrapper<Product4project>().eq("project_no",projectCode).last("limit 1"));
            if(project != null) {
                yiyaobao_projectNo = project.getYiyaobaoProjectCode();
            }
        }
        prescriptionDTO.setProjectNo(yiyaobao_projectNo);

        YxSystemStore yxSystemStore = yxSystemStoreService.getById(yxStoreOrder.getStoreId());

        prescriptionDTO.setSellerId(yxSystemStore.getYiyaobaoId());

        // ?????????????????????
        String verifyCode = generateVerifyCode2(yxStoreOrder.getFactUserPhone());

     /*   String verifyCode = null;
        try {
            verifyCode = r1.get(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/
        prescriptionDTO.setVerifyCode(verifyCode);
        JSONArray jsonArray = JSONUtil.createArray();
        String sku_postage = "*010";
        String medName_postage = "????????????3%???";
        for (YxStoreOrderCartInfo yxStoreOrderCartInfo : yxStoreOrderCartInfoList) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(yxStoreOrderCartInfo.getCartInfo(),YxStoreCartQueryVo.class);

            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",cartQueryVo.getYiyaobaoSku());
            jsonObject.put("unitPrice",cartQueryVo.getVipTruePrice());
            jsonObject.put("amount",cartQueryVo.getCartNum());

            jsonArray.add(jsonObject);

            if(cartQueryVo.getProductInfo().getTaxRate() != null &&  cartQueryVo.getProductInfo().getTaxRate().doubleValue()== new Double("13").doubleValue() ) {
                sku_postage = "*009";
                medName_postage = "????????????13%???";
            }

        }

        // ?????????>0 ???
        if(yxStoreOrder.getTotalPostage() != null && yxStoreOrder.getTotalPostage().doubleValue() > 0) {

            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",sku_postage);
            jsonObject.put("unitPrice",yxStoreOrder.getTotalPostage().doubleValue());
            jsonObject.put("amount",1);
            jsonArray.add(jsonObject);
        }

        prescriptionDTO.setItems(jsonArray.toString());
        //  log.info(JSONUtil.parseObj(prescriptionDTO).toString());
        prescriptionDTO.setImagePath(base64_str);

        // ImageUtil.toFileByBase64(prescriptionDTO.getImagePath());

        // ????????????

        String orderSn = uploadOrder(prescriptionDTO);

        if(StrUtil.isNotBlank(orderSn)) {  // ??????????????????
            // ?????????????????????/???????????????
            // changeOrderReceiver(orderSn,receiver,receiverMobile,yxStoreOrder.getFactUserPhone());

            // ???????????????
            //  changeOrderNo(orderSn,yxStoreOrder.getOrderId());

            // ????????????
            if( OrderInfoEnum.PAY_CHANNEL_2.getValue() == yxStoreOrder.getIsChannel()) {

                checkPassPrescription(yxStoreOrder.getOrderId());

            }

            // ?????????????????????id
            String yiyaobaoOrderId = queryYiyaobaoOrderId(yxStoreOrder.getOrderId());
            if( StrUtil.isNotBlank(yiyaobaoOrderId)) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.set("yiyaobao_order_id",yiyaobaoOrderId);
                updateWrapper.eq("id",yxStoreOrder.getId());
                updateWrapper.set("upload_yiyaobao_flag",1);
                updateWrapper.set("upload_yiyaobao_time",new Date());
                yxStoreOrderService.update(updateWrapper);

                if(ProjectNameEnum.YAOLIAN.getValue().equals(projectCode)) {
                    //?????????????????????????????????????????????(??????)
                    yiyaobaoOrdOrderMapper.updateOrderSourceByOrderno(yiyaobaoOrderId, "25");
                }
            }


            // ??????????????????????????????????????????
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(yxStoreOrder.getProjectCode())) {
                // ???????????? ?????????
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("id",yxStoreOrder.getId());
                updateWrapper.set("status", 0);
                yxStoreOrderService.update(updateWrapper);

                //??????????????????
                try {
                    YxWechatUser wechatUser = wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",yxStoreOrder.getUid()));
                    if (ObjectUtil.isNotNull(wechatUser)) {
                        //??????????????????????????????????????????????????????
                        if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                            String page = "pages/wode/orderDetail?orderId="+yxStoreOrder.getOrderId();
                            OrderTemplateMessage message = new OrderTemplateMessage();
                            message.setOrderDate( OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
                            message.setOrderId(yxStoreOrder.getOrderId());
                            String orderStatus = "?????????";
                            String remark = "??????????????????????????????????????????";

                            message.setOrderStatus(orderStatus);
                            message.setRemark(remark);
                            templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                        }
                    }
                } catch (Exception e) {
                    log.info("????????????????????????????????????:{}",yxStoreOrder.getOrderId());
                    e.printStackTrace();

                }
            }

           /* String phone = "";
            // ????????????????????????????????????????????????????????????????????????
            if(ShopConstants.STORENAME_SHANGHAI_CLOUD.equals(yxSystemStore.getName()) ) {
                phone = "13816035865";
            } else {
               // phone = "13816274773";
            }
            String remindmessage = "???????????????%s???????????????????????????????????????????????????????????????";
            remindmessage = String.format(remindmessage, yxSystemStore.getName());
            smsService.sendTeddy("",remindmessage,phone);*/
        }


        return true;
    }


    public void getMedPartnerMedicine() {

        String url = yiyaobao_apiUrl_external + yiyaobao_getMedPartnerMedicineUrl;

        // String requestBody = JSONUtil.parseObj(pres).toString(); //
        String requestBody = "";
        try {
            long timestamp = System.currentTimeMillis(); // ?????????????????????
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // ??????APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // ?????????????????????
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // ?????????????????????
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // ????????????
            log.info("?????????????????????????????????{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {

              //  yxStoreOrder.setUpload_yiyaobao_flag(1);
             //   yxStoreOrder.setUpload_yiyaobao_time(new Date());

             //   yxStoreOrderService.updateById(yxStoreOrder);

             //   return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
