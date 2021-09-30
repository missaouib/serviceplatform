package co.yixiang.modules.yiyaobao.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.ebs.service.EbsServiceImpl;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupQueryVo;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryVo;
import co.yixiang.modules.shop.service.mapper.StoreOrderMapper;
import co.yixiang.modules.shop.service.mapping.YxStoreProductGroupMap;
import co.yixiang.modules.shop.service.param.ExpressParam;
import co.yixiang.modules.yiyaobao.dto.*;
import co.yixiang.modules.yiyaobao.utils.CryptUtils;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.mp.service.mapper.DictDetailMapper;
import co.yixiang.mp.yiyaobao.domain.*;
import co.yixiang.mp.yiyaobao.mapper.CmdStockDetailEbsMapper;
import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderPartInfoVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.tools.express.dao.Traces;
import co.yixiang.tools.service.impl.SmsServiceImpl;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import co.yixiang.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
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
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Future;

@Service
@Slf4j
public class OrderServiceImpl extends BaseServiceImpl<OrdOrderMapper, OrdOrder> {
    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.orderLogisticsUrl}")
    private String orderLogisticsUrl ;

    @Value("${yiyaobao.orderLogisticsByOrderIdUrl}")
    private String orderLogisticsByOrderIdUrl ;

    @Autowired
    @Lazy
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Value("${yiyaobao.addSingleUrl}")
    private String addSingleUrl ;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;

    @Autowired
    private MdCountryService mdCountryService;

    @Autowired
    private Product4projectService product4projectService;

    @Value("${yiyaobao.projectNo}")
    private String yiyaobao_projectNo;

    @Value("${yiyaobao.apiUrl}")
    private String apiUrl;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Value("${yiyaobao.needEncrypt}")
    private Boolean needEncrypt ;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    TransactionDefinition transactionDefinition;

    @Value("${yiyaobao.getMedPartnerMedicineUrl}")
    private String yiyaobao_getMedPartnerMedicineUrl;


    @Value("${yiyaobao.yiyaobaoImageUrlPrefix}")
    private String yiyaobaoImageUrlPrefix;

    @Autowired
    private StoreOrderMapper yxStoreOrderMapper;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;


    @Autowired
    private CmdStockDetailEbsMapper cmdStockDetailEbsMapper;

    @Autowired
    private  YxWechatUserService wechatUserService;

    @Autowired
    private DictDetailMapper dictDetailMapper;

    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${file.path}")
    private String path;

    @Autowired
    private YxTemplateService templateService;

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EbsServiceImpl ebsService;

    @Autowired
    private YxStoreProductGroupService yxStoreProductGroupService;

    @Value("${yiyaobao.partnerId}")
    private String partnerId;

    /***正式环境*/
    @DS("multi-datasource1")
    public String getYiyaobaoOrderStatus(String orderNo){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ORDER_NO",orderNo);
        OrdOrder ordOrder = ordOrderMapper.selectOne(queryWrapper);
        if(ordOrder != null) {
            return ordOrder.getStatus();
        }
        return "";
    }


    /***正式环境*/
    @DS("multi-datasource1")
    public OrderPartInfoVo getYiyaobaoOrder(String orderNo){

        OrderPartInfoVo ordOrder = ordOrderMapper.getOrderPartInfoByOrderNo(orderNo);

        return ordOrder;
    }

    /**
     *  测试环境
     * */
    @DS("multi-datasource2")
    public String getYiyaobaoOrderStatus2(String orderNo){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ORDER_NO",orderNo);
        OrdOrder ordOrder = ordOrderMapper.selectOne(queryWrapper);
        if(ordOrder != null) {
            return ordOrder.getStatus();
        }
        return "";
    }

    @DS("multi-datasource1")
    public Paging<OrderVo> getYiyaobaoOrderbyMobile(OrderQueryParam orderQueryParam) {
        Page page = setPageParam(orderQueryParam, OrderItem.desc("o.ORDER_TIME"));
        IPage<OrderVo> iPage = ordOrderMapper.getOrderPageList(page,orderQueryParam);

        for(OrderVo order:iPage.getRecords()) {
            // 处方图片
            if(StrUtil.isNotBlank(order.getImageId())) {
                String imagePath = ordOrderMapper.getImagePath(order.getImageId());
                if(StrUtil.isNotBlank(imagePath)) {
                    imagePath = yiyaobaoImageUrlPrefix + imagePath;
                }
                order.setImagePath(imagePath);
                // 订单明细
                List<OrderDetailVo> orderDetailVoList = ordOrderMapper.getOrderDetail(order.getId());
                order.setDetails(orderDetailVoList);
                // 物流轨迹信息
                // 物流轨迹信息
                String logisticsProcess = queryOrderLogisticsProcess(order.getPrescripNo(),order.getPartnerCode(),order.getPrivateKey());
                order.setExpressInfo(logisticsProcess);
            }

            log.info("{}",order);
        }

        return new Paging(iPage);
    }

    @DS("multi-datasource1")
    public List<OrderVo> getYiyaobaoOrderbyMobile(String mobile,Pageable pageable){

        getPage(pageable);
        PageInfo<OrderVo> page = new PageInfo<>(ordOrderMapper.getYiyaobaoOrderByMobile(mobile));
        //List<OrderVo> orderVoList = ordOrderMapper.getYiyaobaoOrderByMobile(mobile);

        for(OrderVo order:page.getList()) {
            // 处方图片
            if(StrUtil.isNotBlank(order.getImageId())) {
                String imagePath = ordOrderMapper.getImagePath(order.getImageId());
                if(StrUtil.isNotBlank(imagePath)) {
                    imagePath = yiyaobaoImageUrlPrefix + imagePath;
                }
                order.setImagePath(imagePath);
            // 订单明细
                List<OrderDetailVo> orderDetailVoList = ordOrderMapper.getOrderDetail(order.getId());
                order.setDetails(orderDetailVoList);
              // 物流轨迹信息
               String logisticsProcess = queryOrderLogisticsProcess(order.getPrescripNo(),order.getPartnerCode(),order.getPrivateKey());
               order.setExpressInfo(logisticsProcess);
            }

            log.info("{}",order);
        }
        return page.getList();
    }

    public String queryOrderLogisticsProcess(String prescripNo,String appId,String appSecret) {

        String url = yiyaobao_apiUrl_external + orderLogisticsUrl;
        //net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("prescripNo",prescripNo);
        jsonObject.put("hospitalName","--");
        jsonObject.put("prescribeDate","");


        String requestBody = jsonObject.toString(); //
        String express = "";
        try {
            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("查询益药宝订单物流信息，结果：{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);


            if(object.getBool("success")) {
                express = object.getJSONArray("result").toString();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return express;
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
            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("查询益药宝订单物流信息，结果：{}" ,result);
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
    public void updateYiyaobaoExpress(String deliveryId,String orderNo) {
        ordOrderMapper.updateYiyaobaoExpress(deliveryId,orderNo);
    }


    public Boolean sendOrder2YiyaobaoCloud(String orderId,String projectName){

        // 获取订单信息

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("订单号["+ orderId + "]没有找到");
        }

        if(  yxStoreOrder.getUploadYiyaobaoFlag() == 1) {
            throw new BadRequestException("订单号["+ orderId + "]已经下发过，无法再次下发");
        }

        Prescription prescription = convertPrescription(yxStoreOrder);
        String orderSource = queryOrderSourceCode(projectName);
        if(StrUtil.isBlank(orderSource)) {
            orderSource = "23";
        }
        prescription.setOrderSource(orderSource);
        prescription.setPayType("90");
        Prescription prescription_tmp = new Prescription();
        BeanUtil.copyProperties(prescription,prescription_tmp);
        prescription_tmp.setImages(null);
        log.info("自费药业务下发益药宝订单：{}",JSONUtil.parseObj(prescription_tmp).toString());

        String url = yiyaobao_apiUrl_external + addSingleUrl;

        String requestBody = JSONUtil.parseObj(prescription).toString(); //

        try {
            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
           /* log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);*/
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("下发益药宝订单，结果：{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);

            if(object.getBool("success")) {

                yxStoreOrder.setUploadYiyaobaoFlag(1);
                yxStoreOrder.setUploadYiyaobaoTime(new Date());
                if( StrUtil.isNotBlank(yxStoreOrder.getImagePath())) {
                    yxStoreOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
                }else {
                    yxStoreOrder.setStatus(0);
                }

                yxStoreOrderService.updateById(yxStoreOrder);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    public Boolean sendOrder2YiyaobaoCloudCancel(String orderId){

        // 获取订单信息

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("订单号["+ orderId + "]没有找到");
        }

        if(   yxStoreOrder.getUploadYiyaobaoRefundFlag() == 1) {
            throw new BadRequestException("订单号["+ orderId + "]已经下发过，无法再次下发");
        }

        Prescription prescription = convertPrescription(yxStoreOrder);

        // 退费处方：原处方号，订单总金额*-1

        prescription.setOriginalPrescripNo(prescription.getPrescripNo());
        prescription.setPrescripNo(prescription.getPrescripNo() + "_1");
        prescription.setTotalAmount(prescription.getTotalAmount().multiply(new BigDecimal(-1)));

        Prescription prescription_tmp = new Prescription();
        BeanUtil.copyProperties(prescription,prescription_tmp);
        prescription_tmp.setImages(null);
        log.info("自费药业务下发益药宝订单：{}",JSONUtil.parseObj(prescription_tmp).toString());

        String url = yiyaobao_apiUrl_external + addSingleUrl;

        String requestBody = JSONUtil.parseObj(prescription).toString(); //

        try {
            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
       /*     log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);*/
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("自费药业务下发益药宝订单，结果：{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);

            if(object.getBool("success")) {

                yxStoreOrder.setUploadYiyaobaoFlag(1);
                yxStoreOrder.setUploadYiyaobaoTime(new Date());
                yxStoreOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
                yxStoreOrderService.updateById(yxStoreOrder);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Prescription convertPrescription(YxStoreOrder yxStoreOrder){


        // 获取订单明细
        // List<YxStoreCart> storeCartList = yxStoreCartService.list(new QueryWrapper<YxStoreCart>().in("id",Arrays.asList(yxStoreOrder.getCartId().split(","))));
        List<YxStoreOrderCartInfo> yxStoreOrderCartInfoList = yxStoreOrderCartInfoService.list(new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        if(CollUtil.isEmpty(yxStoreOrderCartInfoList)){
            throw new BadRequestException("订单号["+ yxStoreOrder.getOrderId() + "]无法找到药品明细");
        }

        // 获取地址
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

        /** 设置处方字段
         */
        Prescription pres = new Prescription();

        // 图片
        /*if(StrUtil.isNotBlank(yxStoreOrder.getImagePath()) && yxStoreOrder.getImagePath().contains(localUrl)){
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
        }*/
        try {
            if(StrUtil.isNotBlank(yxStoreOrder.getImagePath())){

                URL url = new URL(yxStoreOrder.getImagePath());
                String base64 = ImageUtil.encodeImageToBase64(url);

                ImageModel imageModel = new ImageModel();
                imageModel.setImageBase64(base64);

                List<ImageModel> imageModelList = new ArrayList<>();
                imageModelList.add(imageModel);
                pres.setImages(imageModelList);
            }
        }catch (Exception e) {
            log.error("图片转base64异常，imagePath={}",yxStoreOrder.getImagePath());
        }


        // 病人名称 必填
        // pres.setName(yxStoreOrder.getFactUserName());
        if(StrUtil.isNotBlank(yxStoreOrder.getDrugUserName())) {
            pres.setName(yxStoreOrder.getDrugUserName());
        } else {
            pres.setName(yxStoreOrder.getFactUserName());
        }


        // 患者手机号
        // pres.setMobile(yxStoreOrder.getFactUserPhone());

        if(StrUtil.isNotBlank(yxStoreOrder.getDrugUserPhone())) {
            pres.setMobile(yxStoreOrder.getDrugUserPhone());
        } else {
            pres.setMobile(yxStoreOrder.getFactUserPhone());
        }

        // 医生名称
        pres.setDoctorName("汪志方");
        // 科室名称
        pres.setDepartment("普通全科");
        pres.setDeptCode("200301");
        // 医院名称 必填
        pres.setHospitalName("益药商城");

        // 收货信息
        pres.setAddress(addressDetail);
        pres.setProvinceName(province);
        pres.setCityName(city);
        pres.setDistrictName(district);
        pres.setReceiver(receiver);
        pres.setReceiverMobile(receiverMobile);

        // 处方日期
        pres.setPrescribeDate(yxStoreOrder.getAddTime()*1000L);

        // 处方号 必填
        pres.setPrescripNo(yxStoreOrder.getOrderId() );
        // 挂号类别 必填
        pres.setRegisterType(0L);
        //费别(0:自费;1:医保)
        pres.setFeeType("0");



        // 付款方式(01-现金;02-刷卡;70-门店已收款)
        pres.setPayMethod("70");

        //配送类型(00-自提；10-送货上门；99-无需配送)
        pres.setDeliverType("10");
        pres.setRemark(yxStoreOrder.getMark());
        pres.setRegisterDate(yxStoreOrder.getAddTime()*1000L);

        pres.setRegisterType(1L);

        // pres.setDiscount();
        /*
         * 设置处方明细列表（以不同药品区分).
         */
        List<PrescriptionDetail> details = new ArrayList<PrescriptionDetail>();
        // 商品原价总和
        Double totalTruePrice = 0d;

        // 商品会员折价总和
       // Double totalVipDiscountAmount = 0d;
        String sku = "*010";
        String medName = "服务费（3%）";
        for(YxStoreOrderCartInfo yxStoreOrderCartInfo: yxStoreOrderCartInfoList) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(yxStoreOrderCartInfo.getCartInfo(),YxStoreCartQueryVo.class);
            YxStoreProductQueryVo yxStoreProduct = cartQueryVo.getProductInfo();

            if(yxStoreProduct.getTaxRate() != null &&  yxStoreProduct.getTaxRate().doubleValue()== new Double("13").doubleValue() ) {
                sku = "*009";
                medName = "服务费（13%）";
            }

            // 药品主数据
            /*YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",cartQueryVo.getProductAttrUnique()));
            if(yxStoreProductAttrValue == null) {
                throw new BadRequestException("无法找到药品属性详情信息");
            }
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("id",yxStoreProductAttrValue.getProductId());
            queryWrapper1.select("id","store_name","common_name","yiyaobao_sku");
            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper1);*/
            if(yxStoreProduct == null) {
                throw new BadRequestException("无法找到药品详情信息");
            }
            // 组合商品，展示子商品信息
            if(yxStoreProduct.getIsGroup() != null && yxStoreProduct.getIsGroup() == 1) {
                LambdaQueryWrapper<YxStoreProductGroup> yxStoreProductGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
                yxStoreProductGroupLambdaQueryWrapper.eq(YxStoreProductGroup::getParentProductId, yxStoreProduct.getId());
                List<YxStoreProductGroupQueryVo> voList = YxStoreProductGroupMap.toDto(yxStoreProductGroupService.list(yxStoreProductGroupLambdaQueryWrapper));

                for (YxStoreProductGroupQueryVo vo : voList) {
                    // 获取子商品的详细信息
                    QueryWrapper<YxStoreProduct> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("is_del", 0).eq("id", vo.getProductId());
                    queryWrapper1.select("id", "image", "slider_image", "store_name", "store_info", "common_name", "license_number", "drug_form", "spec", "manufacturer", "storage_condition", "unit", "indication", "quality_period", "contraindication", "label1", "label2", "label3", "yiyaobao_sku", "pregnancy_lactation_directions", "children_directions", "elderly_patient_directions", "type", "description", "is_group", "is_need_cloud_produce");
                    YxStoreProduct product = yxStoreProductService.getOne(queryWrapper1);
                    if (product != null) {
                        PrescriptionDetail detail = new PrescriptionDetail();
                        // 药品编码 必填
                        detail.setMedCode(product.getYiyaobaoSku());
                        // 药品名称 必填
                        detail.setMedName(product.getStoreName());
                        // 药品数量 必填
                        detail.setAmount(new BigDecimal(vo.getNum()));
                        if(vo.getUnitPrice()==null){

                            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",vo.getProductUnique()));
                            if(yxStoreProductAttrValue != null) {
                                detail.setUnitPrice(yxStoreProductAttrValue.getPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
                            }else{
                                throw new BadRequestException("子商品["+ yxStoreProduct.getYiyaobaoSku() + "],项目["+ yxStoreOrder.getProjectCode() +"]在对应门店无法找到价格配置信息");
                            }


                        }else{
                            detail.setUnitPrice(vo.getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
                        }
                        detail.setDiscountAmount(BigDecimal.ZERO);
                        // 折扣率
                        detail.setDiscount(BigDecimal.ZERO);
                        details.add(detail);

                        totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(vo.getNum().doubleValue() , detail.getUnitPrice().doubleValue() ));

                    }
                }
            }else{
                PrescriptionDetail detail = new PrescriptionDetail();
                // 药品编码 必填
                detail.setMedCode(yxStoreProduct.getYiyaobaoSku());
                // 药品名称 必填
                detail.setMedName(yxStoreProduct.getStoreName());
                // 药品数量 必填
                detail.setAmount(new BigDecimal(cartQueryVo.getCartNum()));

                BigDecimal price = yxStoreProductService.queryProductPrice(yxStoreProduct.getYiyaobaoSku(),yxStoreOrder.getProjectCode());
                if(ObjectUtil.isNull(price)) {
                    throw new BadRequestException("商品["+ yxStoreProduct.getYiyaobaoSku() + "],项目["+ yxStoreOrder.getProjectCode() +"]无法找到价格配置信息");
                }
                detail.setUnitPrice( price);
                // 折扣金额 = (原价 - 会员价)*药品数量
                BigDecimal vipDiscountAmount = new BigDecimal( 0);
                detail.setDiscountAmount(vipDiscountAmount);
                // 折扣率
                detail.setDiscount(new BigDecimal(0));
                details.add(detail);

                totalTruePrice = NumberUtil.add(totalTruePrice.doubleValue(), NumberUtil.mul(cartQueryVo.getCartNum().doubleValue() , price.doubleValue() ));

            }
        }
        // 当运费>0 时
        if(yxStoreOrder.getTotalPostage() != null && yxStoreOrder.getTotalPostage().doubleValue() > 0) {
            PrescriptionDetail detail = new PrescriptionDetail();
            // 药品编码 必填
            detail.setMedCode(sku);
            // 药品名称 必填
            detail.setMedName(medName);
            // 药品数量 必填
            detail.setAmount(new BigDecimal(1));
            detail.setUnitPrice(yxStoreOrder.getTotalPostage() );
            // 折扣金额 = (原价 - 会员价)*药品数量
            //  BigDecimal vipDiscountAmount = new BigDecimal( NumberUtil.mul( cartQueryVo.getCartNum().intValue(),  NumberUtil.sub(cartQueryVo.getTruePrice(),cartQueryVo.getVipTruePrice()))).setScale(2,BigDecimal.ROUND_HALF_UP);
            detail.setDiscountAmount(new BigDecimal(0));
            // 折扣率
            //   detail.setDiscount(cartQueryVo.getDiscount());
            details.add(detail);
        }

        // 优惠券金额
      /*  if(yxStoreOrder.getCouponPrice() != null && yxStoreOrder.getCouponPrice().doubleValue() >0) {
            PrescriptionDetail detail = new PrescriptionDetail();
            // 药品编码 必填
            detail.setMedCode("*001");
            // 药品名称 必填
            detail.setMedName("13%税单品种折扣");
            // 药品数量 必填
            detail.setAmount(new BigDecimal(1));
            detail.setUnitPrice(yxStoreOrder.getCouponPrice().multiply(new BigDecimal(-1) ));
            // 折扣金额 = (原价 - 会员价)*药品数量
            //  BigDecimal vipDiscountAmount = new BigDecimal( NumberUtil.mul( cartQueryVo.getCartNum().intValue(),  NumberUtil.sub(cartQueryVo.getTruePrice(),cartQueryVo.getVipTruePrice()))).setScale(2,BigDecimal.ROUND_HALF_UP);
            detail.setDiscountAmount(new BigDecimal(0));
            // 折扣率
            //   detail.setDiscount(cartQueryVo.getDiscount());
            details.add(detail);
        }*/

        // 设置处方明细。当一次只能上传一条明细时，调用setDetail而非setDetails;
        pres.setDetails(details);



        //已收费金额
        pres.setPaidAmount(yxStoreOrder.getPayPrice());

        // 折扣金额
        // 折扣金额 = 会员折扣金额  + 优惠券折扣金额 + 积分折扣金额
       // Double totalDiscountAmount = NumberUtil.add(totalVipDiscountAmount.doubleValue() , yxStoreOrder.getCouponPrice().doubleValue() , yxStoreOrder.getDeductionPrice().doubleValue() ).doubleValue();
        pres.setDiscountAmount(new BigDecimal(0));

        //折扣率(采用小数表示)
        pres.setDiscount( new BigDecimal( 0).setScale(2,BigDecimal.ROUND_HALF_UP));
        // 折扣率 = (原价-现价)/原价
        /*if(totalTruePrice == 0) {
            pres.setDiscount( new BigDecimal( 0).setScale(2,BigDecimal.ROUND_HALF_UP));
        } else {
            pres.setDiscount( new BigDecimal( 1- NumberUtil.div(totalDiscountAmount,totalTruePrice)).setScale(2,BigDecimal.ROUND_HALF_UP));
        }*/

//订单总金额 = 商品原价金额 + 快递金额
        pres.setTotalAmount( new BigDecimal( totalTruePrice + yxStoreOrder.getTotalPostage().setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()));

        return pres;

    }

    public AddressDTO getAddressDTO(String provinceName, String cityName, String districtName){
        // 获取省份code
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
            // 获取城市
            QueryWrapper queryWrapper_city = new QueryWrapper();
            // queryWrapper_city.eq("NAME",cityName);
            queryWrapper_city.apply(" {0} LIKE CONCAT('%',NAME,'%') ",cityName);
            queryWrapper_city.eq("TREE_ID","2");
            queryWrapper_city.eq("PARENT_ID",province.getId());
            MdCountry city = mdCountryService.getOne(queryWrapper_city,false);

            if(city != null) {
                cityCode = city.getCode();
                // 获取区
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

    public Boolean sendOrder2YiyaobaoStore(String orderId){

        // 获取订单信息

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",orderId);
        // queryWrapper.eq("upload_yiyaobao_flag",0);
        // queryWrapper.select("order_id","fact_user_name","id","cart_id","upload_yiyaobao_flag","add_time","id","pay_price","total_price");
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            throw new BadRequestException("订单号["+ orderId + "]没有找到");
        }

        if(  yxStoreOrder.getUploadYiyaobaoFlag() == 1) {
            throw new BadRequestException("订单号["+ orderId + "]已经下发过，无法再次下发");
        }

        // 获取订单明细
        // List<YxStoreCart> storeCartList = yxStoreCartService.list(new QueryWrapper<YxStoreCart>().in("id",Arrays.asList(yxStoreOrder.getCartId().split(","))));
        List<YxStoreOrderCartInfo> yxStoreOrderCartInfoList = yxStoreOrderCartInfoService.list(new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",yxStoreOrder.getId()));
        if(CollUtil.isEmpty(yxStoreOrderCartInfoList)){
            throw new BadRequestException("订单号["+ orderId + "]无法找到药品明细");
        }

        // 获取地址
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


        // 图片base64
        //  String imgFilePath="http://d.hiphotos.baidu.com/image/pic/item/a044ad345982b2b713b5ad7d3aadcbef76099b65.jpg";

        String base64_str =  "";
        String imagePath = yxStoreOrder.getImagePath();
        try {
            if(StrUtil.isBlank(imagePath)) {
                //              String defaultImage = path + "static" + File.separator  + "defaultMed.jpg";
//log.info(" defaultImage imagePath = {}",defaultImage);
                base64_str = new ImageUtil().localImageToBase64("otc.jpg");
            } /*else if( imagePath.contains(localUrl)  ){
                // 文件来源于本域名
                String str = localUrl + "/file/";
                imagePath =  imagePath.replace(str,path);
                log.info("下发益药宝订单中照片转换地址：{}",imagePath);

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

        prescriptionDTO.setCustomerRequirement(yxStoreOrder.getMark());
        if(StrUtil.isNotBlank(yxStoreOrder.getDrugUserPhone())) {
            prescriptionDTO.setPatientMobile(yxStoreOrder.getDrugUserPhone()); // 患者手机号
        } else {
            prescriptionDTO.setPatientMobile(yxStoreOrder.getFactUserPhone()); // 购药人手机号
        }

        prescriptionDTO.setPatientName(yxStoreOrder.getDrugUserName()); // 患者姓名
        prescriptionDTO.setContactMobile(yxStoreOrder.getFactUserPhone());  // 购药人手机号
        prescriptionDTO.setReceiver(receiver);  // 收货人
        prescriptionDTO.setReceiverMobile(receiverMobile);  // 收货人电话
        prescriptionDTO.setPayType("10");  // 10 代表在线支付
        if(ProjectNameEnum.YAOLIAN.getValue().equals(yxStoreOrder.getProjectCode())){
            prescriptionDTO.setPayType("60");
        }else if (yxStoreOrder.getPaid() == 1) {  // 已付款,90 代表商城支付
            prescriptionDTO.setPayType("90");
        }

        prescriptionDTO.setOrderNo(yxStoreOrder.getOrderId()); // 订单号
        if(yxStoreOrder.getNeedInvoiceFlag() != null && yxStoreOrder.getNeedInvoiceFlag() == 1) {
            prescriptionDTO.setInvoiceType("10");
            prescriptionDTO.setInvoiceTitle(yxStoreOrder.getInvoiceName());
            prescriptionDTO.setInvoiceRemark(yxStoreOrder.getInvoiceMail());
            prescriptionDTO.setInvoiceAmount(yxStoreOrder.getPayPrice().toString());
        } else {
            prescriptionDTO.setInvoiceType("00");
        }

        String orderSource = "23";
        String projectCode = yxStoreOrder.getProjectCode();
        if(StrUtil.isNotBlank(projectCode)) {
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,yxStoreOrder.getProjectCode()));
            // List<Product4project> product4projectList = product4projectService.list(new QueryWrapper<Product4project>().eq("project_no",projectCode).last("limit 1"));
            if(project != null) {
                yiyaobao_projectNo = project.getYiyaobaoProjectCode();
            }

            orderSource = this.queryOrderSourceCode(project.getProjectName());
            if(StrUtil.isBlank(orderSource)) {
                orderSource = "23";
            }
        }
        prescriptionDTO.setProjectNo(yiyaobao_projectNo);
        prescriptionDTO.setOrderSource(orderSource);
        YxSystemStore yxSystemStore = yxSystemStoreService.getById(yxStoreOrder.getStoreId());

        prescriptionDTO.setSellerId(yxSystemStore.getYiyaobaoId());

        // 生成短信验证码
      //  String verifyCode = generateVerifyCode2(yxStoreOrder.getFactUserPhone());

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
        //prescriptionDTO.setVerifyCode(verifyCode);
        JSONArray jsonArray = JSONUtil.createArray();
        String sku_postage = "*010";
        String medName_postage = "服务费（3%）";
        for (YxStoreOrderCartInfo yxStoreOrderCartInfo : yxStoreOrderCartInfoList) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(yxStoreOrderCartInfo.getCartInfo(),YxStoreCartQueryVo.class);

            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",cartQueryVo.getYiyaobaoSku());
            jsonObject.put("unitPrice",cartQueryVo.getVipTruePrice());
            jsonObject.put("amount",cartQueryVo.getCartNum());

            jsonArray.add(jsonObject);

            if(cartQueryVo.getProductInfo().getTaxRate() != null &&  cartQueryVo.getProductInfo().getTaxRate().doubleValue()== new Double("13").doubleValue() ) {
                sku_postage = "*009";
                medName_postage = "服务费（13%）";
            }

        }

        // 当运费>0 时
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

        // 发送处方

        String orderSn = uploadOrder(prescriptionDTO);

        if(StrUtil.isNotBlank(orderSn)) {  // 订单号不为空
            // 更新收货人姓名/收货人电话
            // changeOrderReceiver(orderSn,receiver,receiverMobile,yxStoreOrder.getFactUserPhone());

            // 更新订单号
            //  changeOrderNo(orderSn,yxStoreOrder.getOrderId());

            // 慈善赠药
          /*  if( OrderInfoEnum.PAY_CHANNEL_2.getValue() == yxStoreOrder.getIsChannel()) {

                checkPassPrescription(yxStoreOrder.getOrderId());

            }
*/
            // 获取益药宝订单id
            String yiyaobaoOrderId = queryYiyaobaoOrderId(yxStoreOrder.getOrderId());
            if( StrUtil.isNotBlank(yiyaobaoOrderId)) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.set("yiyaobao_order_id",yiyaobaoOrderId);
                updateWrapper.eq("id",yxStoreOrder.getId());
                updateWrapper.set("upload_yiyaobao_flag",1);
                updateWrapper.set("upload_yiyaobao_time",new Date());
                if(StrUtil.isNotBlank(yxStoreOrder.getImagePath())) {
                    updateWrapper.set("status",OrderStatusEnum.STATUS_5.getValue());
                }else {
                    updateWrapper.set("status",0);
                }

                yxStoreOrderService.update(updateWrapper);

              /*  if(ProjectNameEnum.YAOLIAN.getValue().equals(projectCode)) {
                    //根据项目名称更新益药宝订单来源(药联)
                    yiyaobaoOrdOrderMapper.updateOrderSourceByOrderno(yiyaobaoOrderId, "25");
                }*/
            }


            // 订单下发完成，发送通知给用户
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(yxStoreOrder.getProjectCode())) {
                // 状态改成 待发货
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("id",yxStoreOrder.getId());
                updateWrapper.set("status", 0);
                yxStoreOrderService.update(updateWrapper);

                //模板消息通知
                try {
                    YxWechatUser wechatUser = wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",yxStoreOrder.getUid()));
                    if (ObjectUtil.isNotNull(wechatUser)) {
                        //公众号与小程序打通统一公众号模板通知
                        if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {
                            String page = "pages/wode/orderDetail?orderId="+yxStoreOrder.getOrderId();
                            OrderTemplateMessage message = new OrderTemplateMessage();
                            message.setOrderDate( OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()));
                            message.setOrderId(yxStoreOrder.getOrderId());
                            String orderStatus = "待发货";
                            String remark = "钱款已经确认收到，药品待发货";

                            message.setOrderStatus(orderStatus);
                            message.setRemark(remark);
                            templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                        }
                    }
                } catch (Exception e) {
                    log.info("订单状态通知异常，订单号:{}",yxStoreOrder.getOrderId());
                    e.printStackTrace();

                }
            }

           /* String phone = "";
            // 下发普通门店后，通知门店管理员，有新订单需要处理
            if(ShopConstants.STORENAME_SHANGHAI_CLOUD.equals(yxSystemStore.getName()) ) {
                phone = "13816035865";
            } else {
               // phone = "13816274773";
            }
            String remindmessage = "【益药】【%s】有一笔待处理的益药商城订单，请尽快处理。";
            remindmessage = String.format(remindmessage, yxSystemStore.getName());
            smsService.sendTeddy("",remindmessage,phone);*/
        }


        return true;
    }

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
        log.info("getVerifyCode 返回结果 {}",body);
        // 解密

        log.info(jsonObject.toString());
        JSONObject result = JSONUtil.parseObj(body);
        log.info(body);

        if ("ok".equals(result.getStr("status"))) {
            return true;
        } else {
            return false;
        }

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
            log.info("发送至益药宝={}",prescriptionDTO);
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

                log.info("解密={}", CryptUtils.decryptString(orderResultDTO.getData(), "b2ctestkey", "GBK"));

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
            throw new ErrorRequestException("调用益药宝生成订单异常");
        }


    }

    @DS("multi-datasource1")
    public Boolean changeOrderReceiver(String OrderNo,String receiverName,String receiverPhone,String factUserPhone) {
        return  ordOrderMapper.changeOrderReceiver(OrderNo,receiverName,receiverPhone,factUserPhone);
    }

    @DS("multi-datasource1")
    public Boolean changeOrderNo(String sourceOrderNo,String targetOrderNo) {
        return  ordOrderMapper.changeOrderNo(sourceOrderNo,targetOrderNo);
    }

    @DS("multi-datasource1")
    public Boolean checkPassPrescription(String orderNo) {
        return  ordOrderMapper.checkPassPrescription(orderNo);
    }

    @DS("multi-datasource1")
    @Async
    public Future<String> generateVerifyCode(String phone){
        String verifyCode =  RandomUtil.randomNumbers(6);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {

            ordOrderMapper.updateVerifyCodeInvalid(phone);
            ordOrderMapper.insertVerifyCode(phone,verifyCode);
            dataSourceTransactionManager.commit(transactionStatus);//提交
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
    public String queryYiyaobaoOrderId(String orderNo) {
        return  ordOrderMapper.queryYiyaobaoOrderId(orderNo);
    }
    @DS("multi-datasource1")
    public String queryYiyaobaoOrderIdByPrescription(String prescriptionNO) {
        return  ordOrderMapper.queryYiyaobaoOrderIdByPrescription(prescriptionNO);
    }

    @DS("multi-datasource1")
    public String queryYiyaobaoOrderIdByOrderNo(String orderNo) {
        return  ordOrderMapper.queryYiyaobaoOrderIdByOrderNo(orderNo);
    }

   /* public int getMedPartnerMedicine() {
        int count =0;
        String url = yiyaobao_apiUrl_external + yiyaobao_getMedPartnerMedicineUrl;
        JSONObject jsonObject = JSONUtil.createObj();

        // 获取最后同步时间
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2020-12-01"), DatePattern.NORM_DATETIME_MS_FORMAT);
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("last_time","partner_data_sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }

        jsonObject.put("lastUpdateTime",lastUpdateTime);
        // String requestBody = JSONUtil.parseObj(pres).toString(); //
        String requestBody = jsonObject.toString();

        try {





            long timestamp = System.currentTimeMillis(); // 生成签名时间戳
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ACCESS_APPID", appId); // 设置APP
            headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
            String ACCESS_SIGANATURE = AppSiganatureUtils
                    .createSiganature(requestBody, appId, appSecret,
                            timestamp);
            headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
            log.info("ACCESS_APPID={}",appId);
            log.info("ACCESS_TIMESTAMP={}",String.valueOf(timestamp));
            log.info("ACCESS_SIGANATURE={}",ACCESS_SIGANATURE);
            log.info("url={}",url);
            log.info("requestBody={}",requestBody);
            String result = HttpUtils.postJsonHttps(url, requestBody,
                    headers); // 发起调用
            log.info("获取益药宝合作伙伴药品主数据，结果：{}" ,result);
            JSONObject object = JSONUtil.parseObj(result);

            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
            if(object.getBool("success")) {
                JSONArray jsonArray = object.getJSONObject("result").getJSONArray("med");
                for(int i=0; i< jsonArray.size();i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    YiyaobaoMed yiyaobaoMed = JSONUtil.toBean(item,YiyaobaoMed.class);

                     // 保存药品主数据
                     saveYiyaobaoMed(yiyaobaoMed);

                    QueryWrapper<YxStoreProduct> queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobaoMed.getSku());
                     queryWrapper.select("id");
                    YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper,false);
                    // 保存药品-药房-价格-库存
                    SkuSellerPriceStock skuSellerPriceStock = new SkuSellerPriceStock();
                    skuSellerPriceStock.setPrice(yiyaobaoMed.getPrice());
                    skuSellerPriceStock.setSellerId(yxSystemStore.getYiyaobaoId());
                    skuSellerPriceStock.setSellerName(yxSystemStore.getName());
                    skuSellerPriceStock.setSku(yiyaobaoMed.getSku());
                    skuSellerPriceStock.setStock(9999);

                    skuSellerPriceStock.setStoreId(yxSystemStore.getId());
                    skuSellerPriceStock.setProductid(yxStoreProduct.getId());
                    skuSellerPriceStock.setStatus(yiyaobaoMed.getStatus());
                    YxStoreProductAttrValue yxStoreProductAttrValue = saveSkuSellerPriceStock(skuSellerPriceStock);

                    // 保存药品-项目 -- 太平
                    saveProduct4project(yxStoreProductAttrValue.getUnique(),ProjectNameEnum.TAIPING_LEXIANG.getValue(),ProjectNameEnum.TAIPING_LEXIANG.getDesc());
                    // 保存药品-项目 -- msh
                    saveProduct4project(yxStoreProductAttrValue.getUnique(),ProjectNameEnum.MSH.getValue(),ProjectNameEnum.MSH.getDesc());

                    // 保存药品-项目 -- 众安普药
                    saveProduct4project(yxStoreProductAttrValue.getUnique(),ProjectNameEnum.ZHONGANPUYAO.getValue(),ProjectNameEnum.ZHONGANPUYAO.getDesc());

                    // 保存药品-项目 -- 蚂蚁
                    saveProduct4project(yxStoreProductAttrValue.getUnique(),ProjectNameEnum.ANT.getValue(),ProjectNameEnum.ANT.getDesc());
                }
                count = jsonArray.size();
                log.info("获取益药宝合作伙伴药品主数据，结果条数：{}" ,count);

            *//*    if(count > 0) {  // 更新药品中的价格（最低价），库存（合计）
                    yxStoreProductService.updatePriceStock();
                }
*//*
                // 更新最后同步时间
                if(dictDetail!=null) {
                    lastUpdateTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_FORMAT);
                    dictDetail.setValue(lastUpdateTime);
                    dictDetailMapper.updateById(dictDetail);
                }

                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
}
*/

    public int syncYiyaobaoPartnerMed(Date date) {
        // 获取最后同步时间
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2020-12-01"), DatePattern.NORM_DATETIME_FORMAT);
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("partner_data_sync_last_time","sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }

       Date lastUpdate =  DateUtil.parse(lastUpdateTime,DatePattern.NORM_DATETIME_FORMAT);
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前3分钟的数据
        lastUpdate = DateUtil.offsetMinute(lastUpdate,-3);
        // 根据益药宝的更新时间获取最新修改过的数据

        YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
        Page page = new Page();
        // 设置当前页码
        page.setCurrent(1);
        long size= 1000;
        // 设置页大小
        page.setSize(size);
       IPage<YiyaobaoMed> yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoPartnerMed(page,partnerId,lastUpdate);
       List<YiyaobaoMed> yiyaomedLst = yiyaobaoMedPage.getRecords();
       long total = yiyaobaoMedPage.getTotal();
       log.info("同步广州店药品数据，记录数{}",total);
       Integer totalPage = new Double(Math.ceil( NumberUtil.div( total,size))).intValue();
       Integer currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
       Integer count =0;
       while(currentPage <= totalPage) {
           log.info("同步广州店药品数据，第{}页",currentPage);
           for(YiyaobaoMed yiyaobaoMed:yiyaomedLst) {
               count = count + 1;

               YiyaobaoMed yiyaobaoMed_image = ordOrderMapper.queryYiyaobaoMedImagesBySku(yiyaobaoMed.getSku());

               if(yiyaobaoMed_image != null && StrUtil.isNotBlank(yiyaobaoMed_image.getFilePath())) {
                   yiyaobaoMed.setFilePath(yiyaobaoMed_image.getFilePath());
               }

               // 保存药品主数据
                saveYiyaobaoMed(yiyaobaoMed);
               QueryWrapper<YxStoreProduct> queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobaoMed.getSku());
               queryWrapper.select("id","image");
               YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper,false);
               // 保存药品-药房-价格-库存
               SkuSellerPriceStock skuSellerPriceStock = new SkuSellerPriceStock();
               skuSellerPriceStock.setPrice(yiyaobaoMed.getPrice());
               skuSellerPriceStock.setSellerId(yxSystemStore.getYiyaobaoId());
               skuSellerPriceStock.setSellerName(yxSystemStore.getName());
               skuSellerPriceStock.setSku(yiyaobaoMed.getSku());
               skuSellerPriceStock.setStock(9999);
               skuSellerPriceStock.setImage(yxStoreProduct.getImage());
               skuSellerPriceStock.setStoreId(yxSystemStore.getId());
               skuSellerPriceStock.setProductid(yxStoreProduct.getId());
               skuSellerPriceStock.setStatus(yiyaobaoMed.getStatus());
               skuSellerPriceStock.setMedPartnerMedicineId(yiyaobaoMed.getMedPartnerMedicineId());
               if(yiyaobaoMed.getIsDelete() == 1) {
                   skuSellerPriceStock.setStatus(1);
               }

               YxStoreProductAttrValue yxStoreProductAttrValue = saveSkuSellerPriceStock4GuangZhou(skuSellerPriceStock);

               LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
               lambdaQueryWrapper.eq(Project::getFlag,"1");

               List<Project> projectList = projectService.list(lambdaQueryWrapper);
               for(Project project:projectList) {
                   saveProduct4project(yxStoreProductAttrValue.getUnique(),project.getProjectCode(),project.getProjectName());
               }



           }

           if( currentPage < totalPage) {
               page = new Page();
               // 设置当前页码
               page.setCurrent(currentPage + 1);

               // 设置页大小
               page.setSize(size);
               yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoPartnerMed(page,partnerId,lastUpdate);
               yiyaomedLst = yiyaobaoMedPage.getRecords();
               currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
           } else {
               currentPage = currentPage +1;
           }

       }



        // 更新最后同步时间
        if(dictDetail!=null) {
            lastUpdateTime = DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT);
            dictDetail.setValue(lastUpdateTime);
            dictDetailMapper.updateById(dictDetail);
        }
        log.info("count={}",count);
        return new Long(total).intValue();
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.format(new Date(), "yyyyMMdd HH:mm:ss"));
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2015-01-01 01:01:01"), "yyyyMMdd HH:mm:ss");
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前3分钟的数据
        System.out.println(lastUpdateTime);
    }

    public int syncYiyaobaoMedImage(Date date){

        // 获取最后同步时间
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2017-12-01"), DatePattern.NORM_DATETIME_FORMAT);
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("image_data_sync_last_time","sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }

        Date lastUpdate =  DateUtil.parse(lastUpdateTime,DatePattern.NORM_DATETIME_FORMAT);
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前3分钟的数据
        lastUpdate = DateUtil.offsetMinute(lastUpdate,-3);

        Page page = new Page();
        // 设置当前页码
        page.setCurrent(1);
        long size= 1000;
        // 设置页大小
        page.setSize(size);
        IPage<YiyaobaoMed> yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoMedImages(page,lastUpdate);
        List<YiyaobaoMed> yiyaomedLst = yiyaobaoMedPage.getRecords();
        long total = yiyaobaoMedPage.getTotal();
        log.info("同步药品照片，记录数{}",total);
        Integer totalPage = new Double(Math.ceil( NumberUtil.div( total,size))).intValue();
        Integer currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
        Integer count =0;
        while(currentPage <= totalPage) {
            log.info("同步药品照片，第{}页",currentPage);
            for(YiyaobaoMed yiyaobaoMed:yiyaomedLst) {
                count = count + 1;

                // 保存药品主数据
                String yiyaobaosku = yiyaobaoMed.getSku();

                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper();
                lambdaQueryWrapper.eq(YxStoreProduct::getYiyaobaoSku,yiyaobaosku);
                int existsCount = yxStoreProductService.count(lambdaQueryWrapper);
                if(existsCount >0) {
                    List<String> imagetListTemp = Arrays.asList(yiyaobaoMed.getFilePath().split(","));
                    List<String> imagetListTemp2 =  CollUtil.sub(imagetListTemp,0,6);
                    List<String> imageList= new ArrayList<>();
                    for(String image_temp:imagetListTemp2) {
                        String str = yiyaobaoImageUrlPrefix + image_temp;
                        imageList.add(str);
                    }

                    String image = "";
                    String sliderImage = "";
                    if(CollUtil.isNotEmpty(imageList)) {
                        image = imageList.get(0);
                        sliderImage = CollUtil.join(imageList,",");
                    } else {
                        image = localUrl + "/file/static/defaultMed.jpg";
                        sliderImage = image;
                    }



                    LambdaUpdateWrapper<YxStoreProduct> updateWrapper= new LambdaUpdateWrapper<>();
                    updateWrapper.eq(YxStoreProduct::getYiyaobaoSku,yiyaobaosku);
                    updateWrapper.set(YxStoreProduct::getImage,image);
                    updateWrapper.set(YxStoreProduct::getSliderImage,sliderImage);
                    updateWrapper.set(YxStoreProduct::getUpdateTime,new Timestamp(System.currentTimeMillis()));
                    yxStoreProductService.update(updateWrapper);
                }



            }

            if( currentPage < totalPage) {
                page = new Page();
                // 设置当前页码
                page.setCurrent(currentPage + 1);

                // 设置页大小
                page.setSize(size);
                yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoMedImages(page,lastUpdate);
                yiyaomedLst = yiyaobaoMedPage.getRecords();
                currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
            } else {
                currentPage = currentPage +1;
            }

        }






        // 更新最后同步时间
        if(dictDetail!=null) {
            lastUpdateTime = DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT);
            dictDetail.setValue(lastUpdateTime);
            dictDetailMapper.updateById(dictDetail);
        }
        log.info("count={}",count);
        return new Long(total).intValue();
    }

    // 同步普通门店的商品
    public int syncYiyaobaoStoreMed(Date date) {

        // 获取最后同步时间
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2015-01-01 01:01:01"), DatePattern.NORM_DATETIME_FORMAT);
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("store_data_sync_last_time","sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }

        Date lastUpdate =  DateUtil.parse(lastUpdateTime,DatePattern.NORM_DATETIME_FORMAT);
        // 根据益药宝的更新时间获取最新修改过的数据
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前3分钟的数据
        lastUpdate = DateUtil.offsetMinute(lastUpdate,-3);

        Page page = new Page();
        // 设置当前页码
        page.setCurrent(1);
        long size= 1000;
        // 设置页大小
        page.setSize(size);
        IPage<YiyaobaoMed> yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoStoreMed(page,lastUpdate);
        List<YiyaobaoMed> yiyaomedLst = yiyaobaoMedPage.getRecords();
        long total = yiyaobaoMedPage.getTotal();
        log.info("同步益药药品数据，记录数{}",total);
        Integer totalPage = new Double(Math.ceil( NumberUtil.div( total,size))).intValue();
        Integer currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
        Integer count =0;
        while(currentPage <= totalPage) {
            log.info("同步益药药品数据，第{}页",currentPage);
            for(YiyaobaoMed yiyaobaoMed:yiyaomedLst) {
                count = count + 1;

                YiyaobaoMed yiyaobaoMed_image = ordOrderMapper.queryYiyaobaoMedImagesBySku(yiyaobaoMed.getSku());

                if(yiyaobaoMed_image != null && StrUtil.isNotBlank(yiyaobaoMed_image.getFilePath())) {
                    yiyaobaoMed.setFilePath(yiyaobaoMed_image.getFilePath());
                }

                // 保存药品主数据
                saveYiyaobaoMed(yiyaobaoMed);
            }

            if( currentPage < totalPage) {
                page = new Page();
                // 设置当前页码
                page.setCurrent(currentPage + 1);

                // 设置页大小
                page.setSize(size);
                yiyaobaoMedPage = ordOrderMapper.queryYiyaobaoStoreMed(page,lastUpdate);
                yiyaomedLst = yiyaobaoMedPage.getRecords();
                currentPage = new Long(yiyaobaoMedPage.getCurrent()).intValue();
            } else {
                currentPage = currentPage +1;
            }

        }

        // 更新最后同步时间
        if(dictDetail!=null) {
            lastUpdateTime = DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT);
            dictDetail.setValue(lastUpdateTime);
            dictDetailMapper.updateById(dictDetail);
        }
        log.info("count={}",count);

        return new Long(total).intValue();
    }

    // 同步普通门店
    public int syncYiyaobaoStore() {

        List<Seller> sellerList = cmdStockDetailEbsMapper.getSeller();
        for(Seller seller:sellerList) {
             int existsCount =  yxSystemStoreService.count(new QueryWrapper<YxSystemStore>().eq("yiyaobao_id", seller.getYiyaobaoId()));
            if(existsCount == 0) {
                // throw new BadRequestException("益药宝药房id["+ skuSellerPriceStock.getSellerId() +"]在商城主数据中找不到");
                // 商城中药房主数据中找不到，新增处理
                YxSystemStore yxSystemStore = new YxSystemStore();
                BeanUtils.copyProperties(seller, yxSystemStore);
                yxSystemStore.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxSystemStoreService.save(yxSystemStore);
            }
        }


        log.info("count={}",sellerList.size());

        return new Long(sellerList.size()).intValue();
    }

    // 同步普通门店的商品价格
    public int syncYiyaobaoStoreMedPrice(Date date) {

        // 获取最后同步时间
        String lastUpdateTime = DateUtil.format(DateUtil.parseDate("2015-01-01 01:01:01"), DatePattern.NORM_DATETIME_FORMAT);
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("store_price_data_sync_last_time","sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }

        Date lastUpdate =  DateUtil.parse(lastUpdateTime,DatePattern.NORM_DATETIME_FORMAT);
        // 根据益药宝的更新时间获取最新修改过的数据
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前3分钟的数据
        lastUpdate = DateUtil.offsetMinute(lastUpdate,-3);


        List<SkuSellerPriceStock> yiyaomedLst = ordOrderMapper.queryYiyaobaoMedPriceByCity(lastUpdate);

        Integer count =0;

        for(SkuSellerPriceStock yiyaobaoMed:yiyaomedLst) {
            count = count + 1;
            // 保存药品-门店-价格数据
            saveSkuSellerPrice(yiyaobaoMed);
        }
        List<SkuSellerPriceStock> yiyaomedLst2 = ordOrderMapper.queryYiyaobaoMedPriceBySeller(lastUpdate);
        for(SkuSellerPriceStock yiyaobaoMed:yiyaomedLst2) {
            count = count + 1;
            // 保存药品-门店-价格数据
            saveSkuSellerPrice(yiyaobaoMed);
        }

        // 更新最后同步时间
        if(dictDetail!=null) {
            lastUpdateTime = DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT);
            dictDetail.setValue(lastUpdateTime);
            dictDetailMapper.updateById(dictDetail);
        }

        log.info("同步益药药品价格数据，记录数{}",count);
        return  count;
    }

    // 同步普通门店的商品库存
    public int syncYiyaobaoStoreMedStock() {
        Date date=new Date();
        // 获取最后同步时间
        String lastUpdateTime ="20150101 00:00:00";
        List<DictDetail> dictDetailList = dictDetailMapper.selectDictDetailList("esb_med_stock_sync_last_time","sync_last_time");
        DictDetail dictDetail = null;
        if(CollUtil.isNotEmpty(dictDetailList)) {
            dictDetail = dictDetailList.get(0);
            if(StrUtil.isNotBlank(dictDetail.getValue())) {
                lastUpdateTime = dictDetail.getValue();
            }
        }
        Date lastUpdate =  DateUtil.parse(lastUpdateTime,"yyyyMMdd HH:mm:ss");
        // 为了解决益药宝录入数据与商城抽数时间差的问题，每次多抽取前1天的数据
        lastUpdate = DateUtil.offsetDay(lastUpdate,-1);
        lastUpdateTime = DateUtil.format(lastUpdate,"yyyyMMdd HH:mm:ss");

        LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ne(YxStoreProductAttrValue::getSuk,ShopConstants.STORENAME_GUANGZHOU_CLOUD);
        lambdaQueryWrapper.ge(YxStoreProductAttrValue::getCreateTime,lastUpdate);
        lambdaQueryWrapper.eq(YxStoreProductAttrValue::getIsDel,0);
        List<YxStoreProductAttrValue> yxStoreProductAttrValueList = yxStoreProductAttrValueService.list(lambdaQueryWrapper);
        List<YxStoreProductAttrValue> list = new ArrayList<>();
        for(YxStoreProductAttrValue yxStoreProductAttrValue:yxStoreProductAttrValueList) {
            List<SkuSellerPriceStock> yiyaomedLst = ebsService.queryYiyaobaoMedStock("20150101 00:00:00",yxStoreProductAttrValue.getYiyaobaoSellerId(),1,10,yxStoreProductAttrValue.getYiyaobaoSku());
            if(CollUtil.isNotEmpty(yiyaomedLst)) {
                SkuSellerPriceStock yiyaobaoMed = yiyaomedLst.get(0);
                // 保存药品-门店-库存数据
                saveSkuSellerStock(yiyaobaoMed,list);
            }
        }
        if(list.size()>0){
            yxStoreProductAttrValueService.updateBatchById(list);
        }
        // 更新商城端药房的库存
        // 页码数
        Integer current=1;
        // 每页条数
        Integer size= 1000;
        boolean f=true;
        int batchNo = OrderUtil.getSecondTimestampTwo();
        list.clear();
        do{
            List<SkuSellerPriceStock> yiyaomedLst = ebsService.queryYiyaobaoMedStock(lastUpdateTime,"",current,size,"");
            for(SkuSellerPriceStock yiyaobaoMed:yiyaomedLst) {
                yiyaobaoMed.setBatchNo(batchNo);
                // 保存药品-门店-库存数据
                saveSkuSellerStock(yiyaobaoMed,list);
            }
            if(yiyaomedLst.size()==size){
                current=current+1;
            }else{
                f=false;
            }
        }while(f);

        log.info("益药宝门店药品库存同步条数 count={}",list.size());
        if(list.size()>0){
            yxStoreProductAttrValueService.updateBatchById(list);
        }

        // 事务原因，导致上一段代码没真实提交，就执行下段代码，导致更新异常数据
        // 解决方案，先sleep一段时间
        // 将本次未更新到的库存数据清零
//        yxStoreOrderMapper.updateStock(ShopConstants.STORENAME_GUANGZHOU_CLOUD,batchNo);
        // 更新最后同步时间
        if(dictDetail!=null) {
            lastUpdateTime = DateUtil.format(date, "yyyyMMdd HH:mm:ss");
            dictDetail.setValue(lastUpdateTime);
            dictDetailMapper.updateById(dictDetail);
        }
        return new Long(list.size()).intValue();
    }


    public void syncYiyaobaoMedInfo(){
        Date date=DateUtil.date();
        // 同步药店信息
        syncYiyaobaoStore();

        // 广州店药品信息
        syncYiyaobaoPartnerMed(date);
        // 普通门店药品信息
        syncYiyaobaoStoreMed(date);

        // 所有图片信息
        syncYiyaobaoMedImage(date);

        // 普通门店价格信息
        syncYiyaobaoStoreMedPrice(date);
    }

    // 更新药品主数据
    private void saveYiyaobaoMed(YiyaobaoMed yiyaobaoMed) {
        log.info("{}",yiyaobaoMed);


        QueryWrapper<YxStoreProduct> queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobaoMed.getSku());
       // queryWrapper.select("id");
        int count = yxStoreProductService.count(queryWrapper);
        if( count == 0) {
            // throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
            YxStoreProduct product = new YxStoreProduct();
            product.setIsShow(1);
            product.setUnit(StrUtil.emptyIfNull(yiyaobaoMed.getUnit()));
            product.setManufacturer(StrUtil.emptyIfNull(yiyaobaoMed.getManufacturer()));
            product.setPrice(ObjectUtil.defaultIfNull(yiyaobaoMed.getPrice(),new BigDecimal(0)));
            product.setSpec(StrUtil.emptyIfNull(yiyaobaoMed.getSpec()));
            product.setIsDel(0);
            String store_name = yiyaobaoMed.getMedName();
            String common_name = yiyaobaoMed.getCommonName();
        /*if(StrUtil.isBlank(store_name)) {
            store_name = common_name;
        }*/
            product.setStoreName(store_name);
            product.setCommonName(common_name);
            product.setYiyaobaoSku(yiyaobaoMed.getSku());
            product.setLicenseNumber(yiyaobaoMed.getLicenseNumber());
            product.setStorageCondition(yiyaobaoMed.getStorageCondition());

            String commonPinYin = PinYinUtils.getHanziPinYin(yiyaobaoMed.getCommonName()) ;
            String namePinYin = PinYinUtils.getHanziPinYin(yiyaobaoMed.getMedName()) ;
            if(commonPinYin == null) {
                commonPinYin = "";
            }
            if(namePinYin == null) {
                namePinYin = "";
            }
            String pinYin = "";
            if(commonPinYin.equals(namePinYin)) {
                pinYin = commonPinYin;
            } else {
                pinYin = commonPinYin + "(" + namePinYin + ")";
            }

            String commonShortPinYin = PinYinUtils.getHanziInitials(yiyaobaoMed.getCommonName());
            String nameShortPinYin = PinYinUtils.getHanziInitials(yiyaobaoMed.getMedName()) ;
            if(commonShortPinYin == null) {
                commonShortPinYin = "";
            }
            if(nameShortPinYin == null) {
                nameShortPinYin = "";
            }
            String shortPinYin = "";
            if(commonShortPinYin.equals(nameShortPinYin)) {
                shortPinYin = commonShortPinYin;
            } else {
                shortPinYin = commonShortPinYin + "(" + nameShortPinYin + ")";
            }


            product.setPinyinName(pinYin);
            product.setPinyinShortName(shortPinYin);
            product.setTaxRate(ObjectUtil.defaultIfNull(yiyaobaoMed.getTaxRate(),new BigDecimal(0)));
            product.setIndication(StrUtil.emptyIfNull(yiyaobaoMed.getIndication()));
            product.setApplyCrowdDesc(StrUtil.emptyIfNull(yiyaobaoMed.getApplyCrowdDesc()));
            product.setDirections(StrUtil.emptyIfNull(yiyaobaoMed.getDirections()));
            product.setContraindication(StrUtil.emptyIfNull(yiyaobaoMed.getContraindication()));
            product.setDrugForm(StrUtil.emptyIfNull(yiyaobaoMed.getDrugForm()));
            product.setType(StrUtil.emptyIfNull(yiyaobaoMed.getCategory()));
            product.setQualityPeriod(StrUtil.emptyIfNull(yiyaobaoMed.getQualityPeriod()));
            product.setAttention(StrUtil.emptyIfNull(yiyaobaoMed.getAttention()));
            product.setMedicationCycle(StrUtil.emptyIfNull(yiyaobaoMed.getMedicationCycle()));
            product.setStorage(StrUtil.emptyIfNull(yiyaobaoMed.getStorage()));
            product.setStock(9999);
            product.setPrice(yiyaobaoMed.getPrice());

            String image = localUrl + "/file/static/defaultMed.jpg";
            String sliderImage = image;
            if( StrUtil.isNotBlank(yiyaobaoMed.getFilePath())) {
                List<String> imagetListTemp = Arrays.asList(yiyaobaoMed.getFilePath().split(","));
                List<String> imagetListTemp2 =  CollUtil.sub(imagetListTemp,0,6);
                List<String> imageList= new ArrayList<>();
                for(String image_temp:imagetListTemp2) {
                    String str = yiyaobaoImageUrlPrefix + image_temp;
                    imageList.add(str);
                }
                if(CollUtil.isNotEmpty(imageList)) {
                    image = imageList.get(0);
                    sliderImage = CollUtil.join(imageList,",");
                } else {
                    image = localUrl + "/file/static/defaultMed.jpg";
                    sliderImage = image;
                }

            }


            product.setImage(image);
            product.setSliderImage(sliderImage);

            product.setUntowardEffect(StrUtil.emptyIfNull(yiyaobaoMed.getUntowardEffect()));
            product.setDrugInteraction(StrUtil.emptyIfNull(yiyaobaoMed.getDrugInteraction()));
            product.setFunctionIndication(StrUtil.emptyIfNull(yiyaobaoMed.getFunctionIndication()));
            product.setBasis(StrUtil.emptyIfNull(yiyaobaoMed.getBasis()));
            product.setCharacters(StrUtil.emptyIfNull(yiyaobaoMed.getCharacters()));
            product.setPregnancyLactationDirections(StrUtil.emptyIfNull(yiyaobaoMed.getPregnancyLactationDirections()));
            product.setChildrenDirections(StrUtil.emptyIfNull(yiyaobaoMed.getChildrenDirections()));
            product.setElderlyPatientDirections(StrUtil.emptyIfNull(yiyaobaoMed.getElderlyPatientDirections()));

            yxStoreProductService.save(product);
        } else {

            LambdaUpdateWrapper<YxStoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.set(YxStoreProduct::getIsShow,1);
            lambdaUpdateWrapper.set(YxStoreProduct::getUnit,StrUtil.emptyIfNull(yiyaobaoMed.getUnit()));
            lambdaUpdateWrapper.set(YxStoreProduct::getManufacturer,StrUtil.emptyIfNull(yiyaobaoMed.getManufacturer()));
            lambdaUpdateWrapper.set(YxStoreProduct::getPrice,ObjectUtil.defaultIfNull(yiyaobaoMed.getPrice(),new BigDecimal(0)));
            lambdaUpdateWrapper.set(YxStoreProduct::getSpec,StrUtil.emptyIfNull(yiyaobaoMed.getSpec()));
            lambdaUpdateWrapper.set(YxStoreProduct::getIsDel,0);
            String store_name = yiyaobaoMed.getMedName();
            String common_name = yiyaobaoMed.getCommonName();
        /*if(StrUtil.isBlank(store_name)) {
            store_name = common_name;
        }*/

            lambdaUpdateWrapper.set(YxStoreProduct::getStoreName,StrUtil.emptyIfNull(store_name));
            lambdaUpdateWrapper.set(YxStoreProduct::getCommonName,StrUtil.emptyIfNull(common_name));
            lambdaUpdateWrapper.set(YxStoreProduct::getLicenseNumber,StrUtil.emptyIfNull(yiyaobaoMed.getLicenseNumber()));
            lambdaUpdateWrapper.set(YxStoreProduct::getStorageCondition,StrUtil.emptyIfNull(yiyaobaoMed.getStorageCondition()));
            String commonPinYin = PinYinUtils.getHanziPinYin(yiyaobaoMed.getCommonName()) ;
            String namePinYin = PinYinUtils.getHanziPinYin(yiyaobaoMed.getMedName()) ;
            if(commonPinYin == null) {
                commonPinYin = "";
            }
            if(namePinYin == null) {
                namePinYin = "";
            }
            String pinYin = "";
            if(commonPinYin.equals(namePinYin)) {
                pinYin = commonPinYin;
            } else {
                pinYin = commonPinYin + "(" + namePinYin + ")";
            }

            String commonShortPinYin = PinYinUtils.getHanziInitials(yiyaobaoMed.getCommonName());
            String nameShortPinYin = PinYinUtils.getHanziInitials(yiyaobaoMed.getMedName()) ;
            if(commonShortPinYin == null) {
                commonShortPinYin = "";
            }
            if(nameShortPinYin == null) {
                nameShortPinYin = "";
            }
            String shortPinYin = "";
            if(commonShortPinYin.equals(nameShortPinYin)) {
                shortPinYin = commonShortPinYin;
            } else {
                shortPinYin = commonShortPinYin + "(" + nameShortPinYin + ")";
            }

            lambdaUpdateWrapper.set(YxStoreProduct::getPinyinName,pinYin);
            lambdaUpdateWrapper.set(YxStoreProduct::getPinyinShortName,shortPinYin);
            lambdaUpdateWrapper.set(YxStoreProduct::getTaxRate,ObjectUtil.defaultIfNull(yiyaobaoMed.getTaxRate(),new BigDecimal(0)));
            lambdaUpdateWrapper.set(YxStoreProduct::getIndication,StrUtil.emptyIfNull(yiyaobaoMed.getIndication()));
            lambdaUpdateWrapper.set(YxStoreProduct::getApplyCrowdDesc,StrUtil.emptyIfNull(yiyaobaoMed.getApplyCrowdDesc()));
            lambdaUpdateWrapper.set(YxStoreProduct::getDirections,StrUtil.emptyIfNull(yiyaobaoMed.getDirections()));
            lambdaUpdateWrapper.set(YxStoreProduct::getContraindication,StrUtil.emptyIfNull(yiyaobaoMed.getContraindication()));
            lambdaUpdateWrapper.set(YxStoreProduct::getDrugForm,StrUtil.emptyIfNull(yiyaobaoMed.getDrugForm()));
            lambdaUpdateWrapper.set(YxStoreProduct::getType,StrUtil.emptyIfNull(yiyaobaoMed.getCategory()));
            lambdaUpdateWrapper.set(YxStoreProduct::getQualityPeriod,StrUtil.emptyIfNull(yiyaobaoMed.getQualityPeriod()));
            lambdaUpdateWrapper.set(YxStoreProduct::getAttention,StrUtil.emptyIfNull(yiyaobaoMed.getAttention()));
            lambdaUpdateWrapper.set(YxStoreProduct::getMedicationCycle,StrUtil.emptyIfNull(yiyaobaoMed.getMedicationCycle()));
            lambdaUpdateWrapper.set(YxStoreProduct::getStorage,StrUtil.emptyIfNull(yiyaobaoMed.getStorage()));
            lambdaUpdateWrapper.set(YxStoreProduct::getStock,9999);
            lambdaUpdateWrapper.eq(YxStoreProduct::getYiyaobaoSku,StrUtil.emptyIfNull(yiyaobaoMed.getSku()));
            lambdaUpdateWrapper.set(YxStoreProduct::getUpdateTime,new Timestamp(System.currentTimeMillis()));

            lambdaUpdateWrapper.set(YxStoreProduct::getUntowardEffect,StrUtil.emptyIfNull(yiyaobaoMed.getUntowardEffect()));
            lambdaUpdateWrapper.set(YxStoreProduct::getDrugInteraction,StrUtil.emptyIfNull(yiyaobaoMed.getDrugInteraction()));
            lambdaUpdateWrapper.set(YxStoreProduct::getFunctionIndication,StrUtil.emptyIfNull(yiyaobaoMed.getFunctionIndication()));
            lambdaUpdateWrapper.set(YxStoreProduct::getBasis,StrUtil.emptyIfNull(yiyaobaoMed.getBasis()));
            lambdaUpdateWrapper.set(YxStoreProduct::getCharacters,StrUtil.emptyIfNull(yiyaobaoMed.getCharacters()));

            lambdaUpdateWrapper.set(YxStoreProduct::getPregnancyLactationDirections,StrUtil.emptyIfNull(yiyaobaoMed.getPregnancyLactationDirections()));
            lambdaUpdateWrapper.set(YxStoreProduct::getChildrenDirections,StrUtil.emptyIfNull(yiyaobaoMed.getChildrenDirections()));
            lambdaUpdateWrapper.set(YxStoreProduct::getElderlyPatientDirections,StrUtil.emptyIfNull(yiyaobaoMed.getElderlyPatientDirections()));

            yxStoreProductService.update(lambdaUpdateWrapper);
        }

    }

   // 插入药品-药房-库存-价格
    private YxStoreProductAttrValue saveSkuSellerPriceStock(SkuSellerPriceStock skuSellerPriceStock){
        Integer storeId ;
        String storeName = "";
        if(skuSellerPriceStock.getStoreId()== null){
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("yiyaobao_id", skuSellerPriceStock.getSellerId()),false);
            if(yxSystemStore == null) {
                // throw new BadRequestException("益药宝药房id["+ skuSellerPriceStock.getSellerId() +"]在商城主数据中找不到");
                // 商城中药房主数据中找不到，新增处理
                Seller seller = cmdStockDetailEbsMapper.getSellerById(skuSellerPriceStock.getSellerId());
                yxSystemStore = new YxSystemStore();
                BeanUtils.copyProperties(seller,yxSystemStore);
                yxSystemStore.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxSystemStoreService.save(yxSystemStore);
            }
            storeId = yxSystemStore.getId();
            storeName = yxSystemStore.getName();
        } else {
            storeId = skuSellerPriceStock.getStoreId();
            storeName = skuSellerPriceStock.getSellerName();
        }
        Integer productid = 0;
        String image = "";
        if(skuSellerPriceStock.getProductid() == null) {
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().select("id","image").eq("yiyaobao_sku",skuSellerPriceStock.getSku()),false);
            if(yxStoreProduct == null) {
                throw new BadRequestException("益药宝药品sku["+ skuSellerPriceStock.getSku() +"]在商城主数据中找不到");
            }

            productid = yxStoreProduct.getId();
            image = yxStoreProduct.getImage();
        }else {
            productid = skuSellerPriceStock.getProductid();
            image = skuSellerPriceStock.getImage();
        }

        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("product_id",productid);
        queryWrapper_store.eq("store_id",storeId);
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            productAttrValue = new YxStoreProductAttrValue();
            productAttrValue.setUnique(UUID.randomUUID().toString());
            productAttrValue.setSales(0);
        }

        productAttrValue.setYiyaobaoSku(skuSellerPriceStock.getSku());
        productAttrValue.setStoreId(storeId);
        productAttrValue.setProductId(productid);
        productAttrValue.setSuk(storeName);
        productAttrValue.setStock(skuSellerPriceStock.getStock().intValue());
        productAttrValue.setPrice(skuSellerPriceStock.getPrice());
        productAttrValue.setYiyaobaoSellerId(skuSellerPriceStock.getSellerId());
        productAttrValue.setImage(image);
        productAttrValue.setCost(skuSellerPriceStock.getPrice());
        productAttrValue.setIsDel(skuSellerPriceStock.getStatus());

        yxStoreProductAttrValueService.saveOrUpdate(productAttrValue);

        return productAttrValue;
    };

    // 插入药品-药房-库存-价格 广州店定制
    private YxStoreProductAttrValue saveSkuSellerPriceStock4GuangZhou(SkuSellerPriceStock skuSellerPriceStock){
        Integer storeId ;
        String storeName = "";
        if(skuSellerPriceStock.getStoreId()== null){
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("yiyaobao_id", skuSellerPriceStock.getSellerId()),false);
            if(yxSystemStore == null) {
                // throw new BadRequestException("益药宝药房id["+ skuSellerPriceStock.getSellerId() +"]在商城主数据中找不到");
                // 商城中药房主数据中找不到，新增处理
                Seller seller = cmdStockDetailEbsMapper.getSellerById(skuSellerPriceStock.getSellerId());
                yxSystemStore = new YxSystemStore();
                BeanUtils.copyProperties(seller,yxSystemStore);
                yxSystemStore.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxSystemStoreService.save(yxSystemStore);
            }
            storeId = yxSystemStore.getId();
            storeName = yxSystemStore.getName();
        } else {
            storeId = skuSellerPriceStock.getStoreId();
            storeName = skuSellerPriceStock.getSellerName();
        }
        Integer productid = 0;
        String image = "";
        if(skuSellerPriceStock.getProductid() == null) {
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().select("id","image").eq("yiyaobao_sku",skuSellerPriceStock.getSku()),false);
            if(yxStoreProduct == null) {
                throw new BadRequestException("益药宝药品sku["+ skuSellerPriceStock.getSku() +"]在商城主数据中找不到");
            }

            productid = yxStoreProduct.getId();
            image = yxStoreProduct.getImage();
        }else {
            productid = skuSellerPriceStock.getProductid();
            image = skuSellerPriceStock.getImage();
        }

        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("med_partner_medicine_id",skuSellerPriceStock.getMedPartnerMedicineId());
        queryWrapper_store.eq("store_id",storeId);
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            productAttrValue = new YxStoreProductAttrValue();
            productAttrValue.setUnique(UUID.randomUUID().toString());
            productAttrValue.setSales(0);
        }

        productAttrValue.setYiyaobaoSku(skuSellerPriceStock.getSku());
        productAttrValue.setStoreId(storeId);
        productAttrValue.setProductId(productid);
        productAttrValue.setSuk(storeName);
        productAttrValue.setStock(skuSellerPriceStock.getStock().intValue());
        productAttrValue.setPrice(skuSellerPriceStock.getPrice());
        productAttrValue.setYiyaobaoSellerId(skuSellerPriceStock.getSellerId());
        productAttrValue.setImage(image);
        productAttrValue.setCost(skuSellerPriceStock.getPrice());
        productAttrValue.setIsDel(skuSellerPriceStock.getStatus());
        productAttrValue.setMedPartnerMedicineId(skuSellerPriceStock.getMedPartnerMedicineId());
        yxStoreProductAttrValueService.saveOrUpdate(productAttrValue);

        return productAttrValue;
    };

    private void saveSkuSellerPrice(SkuSellerPriceStock skuSellerPriceStock){
        Integer storeId ;
        String storeName = "";
        if(skuSellerPriceStock.getStoreId()== null){
            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("yiyaobao_id", skuSellerPriceStock.getSellerId()),false);
            if(yxSystemStore == null) {
                // throw new BadRequestException("益药宝药房id["+ skuSellerPriceStock.getSellerId() +"]在商城主数据中找不到");
                // 商城中药房主数据中找不到，新增处理
                Seller seller = cmdStockDetailEbsMapper.getSellerById(skuSellerPriceStock.getSellerId());
                yxSystemStore = new YxSystemStore();
                BeanUtils.copyProperties(seller,yxSystemStore);
                yxSystemStore.setAddTime(OrderUtil.getSecondTimestampTwo());
                yxSystemStoreService.save(yxSystemStore);
            }
            storeId = yxSystemStore.getId();
            storeName = yxSystemStore.getName();
        } else {
            storeId = skuSellerPriceStock.getStoreId();
            storeName = skuSellerPriceStock.getSellerName();
        }
        Integer productid = 0;
        String image = "";
        if(skuSellerPriceStock.getProductid() == null) {
            YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(new QueryWrapper<YxStoreProduct>().select("id","image").eq("yiyaobao_sku",skuSellerPriceStock.getSku()),false);
            if(yxStoreProduct == null) {
                log.error("益药宝药品sku["+ skuSellerPriceStock.getSku() +"]在商城主数据中找不到");
               // throw new BadRequestException("益药宝药品sku["+ skuSellerPriceStock.getSku() +"]在商城主数据中找不到");
                return;
            }

            productid = yxStoreProduct.getId();
            image = yxStoreProduct.getImage();
        }else {
            productid = skuSellerPriceStock.getProductid();
            image = skuSellerPriceStock.getImage();
        }

        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("product_id",productid);
        queryWrapper_store.eq("store_id",storeId);
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            productAttrValue = new YxStoreProductAttrValue();
            productAttrValue.setUnique(UUID.randomUUID().toString());
            productAttrValue.setSales(0);
            productAttrValue.setYiyaobaoSku(skuSellerPriceStock.getSku());
            productAttrValue.setStoreId(storeId);
            productAttrValue.setProductId(productid);
            productAttrValue.setSuk(storeName);
            productAttrValue.setStock(0);
            productAttrValue.setPrice(skuSellerPriceStock.getPrice());
            productAttrValue.setYiyaobaoSellerId(skuSellerPriceStock.getSellerId());
            productAttrValue.setImage(image);
            productAttrValue.setCost(skuSellerPriceStock.getPrice());
            productAttrValue.setIsDel(0);
            productAttrValue.setCreateTime(new Timestamp(System.currentTimeMillis()));
            productAttrValue.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            yxStoreProductAttrValueService.save(productAttrValue);
        } else {
            LambdaUpdateWrapper<YxStoreProductAttrValue> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.eq(YxStoreProductAttrValue::getId,productAttrValue.getId());
            updateWrapper.set(YxStoreProductAttrValue::getPrice,skuSellerPriceStock.getPrice());
            updateWrapper.set(YxStoreProductAttrValue::getUpdateTime,new Timestamp(System.currentTimeMillis()));
            yxStoreProductAttrValueService.update(updateWrapper);
        }
        // 页码数
        Integer current=1;
        // 每页条数
        Integer size= 1000;
        // 同步ebs库存
        List<SkuSellerPriceStock> yiyaomedLst = ebsService.queryYiyaobaoMedStock("",skuSellerPriceStock.getSellerId(),current,size,skuSellerPriceStock.getSku());
        for(SkuSellerPriceStock yiyaobaoMed:yiyaomedLst) {
            LambdaUpdateWrapper<YxStoreProductAttrValue> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.eq(YxStoreProductAttrValue::getId,productAttrValue.getId());
            updateWrapper.set(YxStoreProductAttrValue::getStock,yiyaobaoMed.getStock());
            updateWrapper.set(YxStoreProductAttrValue::getUpdateTime,new Timestamp(System.currentTimeMillis()));
            yxStoreProductAttrValueService.update(updateWrapper);
        }


    };
    private void saveSkuSellerStock(SkuSellerPriceStock skuSellerPriceStock,List<YxStoreProductAttrValue> list){
        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("yiyaobao_sku",skuSellerPriceStock.getSku());
        queryWrapper_store.eq("yiyaobao_seller_id",skuSellerPriceStock.getSellerId());
        queryWrapper_store.ne("suk",ShopConstants.STORENAME_GUANGZHOU_CLOUD);
        queryWrapper_store.select("id","stock");
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            return;
        } else {
                YxStoreProductAttrValue yxStoreProductAttrValue = new YxStoreProductAttrValue();
                yxStoreProductAttrValue.setId(productAttrValue.getId());
                yxStoreProductAttrValue.setStock(skuSellerPriceStock.getStock());
               // yxStoreProductAttrValue.setAttrId(skuSellerPriceStock.getBatchNo());
                list.add(yxStoreProductAttrValue);

           // }
        }
    };

    // 插入项目药品表
    public void saveProduct4project(String uniqueId,String projectCode,String projectName){
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("project_no",projectCode);
        queryWrapper1.eq("product_unique_id",uniqueId);
        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("`unique`",uniqueId));
        int exists = product4projectService.count(queryWrapper1);
        if(exists == 0) {
            Product4project product4project = new Product4project();

            product4project.setProductId(yxStoreProductAttrValue.getProductId());
            product4project.setStoreId(yxStoreProductAttrValue.getStoreId());
            product4project.setStoreName(yxStoreProductAttrValue.getSuk());
            product4project.setNum(1);
            product4project.setProductUniqueId(uniqueId);
            product4project.setProjectName(projectName);
            product4project.setProjectNo(projectCode);
            product4project.setIsDel(yxStoreProductAttrValue.getIsDel());
            if(yxStoreProductAttrValue.getIsDel() == 1) {
                product4project.setIsShow(0);
            } else {
                product4project.setIsShow(1);
            }

            product4project.setCreateTime(new Timestamp(System.currentTimeMillis()));
            product4project.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            product4projectService.save(product4project);
        }else {
            LambdaUpdateWrapper<Product4project> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(Product4project::getProjectNo,projectCode);
            lambdaUpdateWrapper.eq(Product4project::getProductUniqueId,uniqueId);
            lambdaUpdateWrapper.set(Product4project::getIsDel,yxStoreProductAttrValue.getIsDel());

            lambdaUpdateWrapper.set(Product4project::getUpdateTime,new Timestamp(System.currentTimeMillis()));
            product4projectService.update(lambdaUpdateWrapper);


        }

    };

    // 获取普通门店的药品主数据和库存
    public Map<String,HashMap> getMedStoreMedicine(){
        // 获取药品-药房的价格和库存 按城市
        List<SkuSellerPriceStock> skuSellerPriceStockByCityList = cmdStockDetailEbsMapper.getSellerPriceStockByCity();
        // 获取药品-药房的价格和库存 按药房
        List<SkuSellerPriceStock> skuSellerPriceStockBySellerList = cmdStockDetailEbsMapper.getSellerPriceStockBySeller();
        // 药品主数据
        //   HashMap<String, Medicine> skuMap = new HashMap<>();

        HashMap<String,SkuSellerPriceStock> skuSellerPriceStockMap = new HashMap<>();

        Map<String,String> skuMap = new HashMap<>();

        HashMap<String,YiyaobaoMed> yiyaobaoMedMap = new HashMap<>();

        // 按城市获取的药品-药店-库存
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockByCityList){
            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
            skuMap.put(skuSellerPriceStock.getSku(),skuSellerPriceStock.getSku());
        }

        //优先 按药店获取的药品-药店-库存；
        for(SkuSellerPriceStock skuSellerPriceStock:skuSellerPriceStockBySellerList){
            skuSellerPriceStockMap.put(skuSellerPriceStock.getSku()+"_"+skuSellerPriceStock.getSellerId(),skuSellerPriceStock);
            skuMap.put(skuSellerPriceStock.getSku(),skuSellerPriceStock.getSku());
        }
        log.info("同步门店药品数据条数={},库存-价格数据条数={}",skuMap.size(),skuSellerPriceStockMap.size());
        for (Map.Entry<String, String> entry : skuMap.entrySet()) {
            // System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            String sku = entry.getKey();
            // 获取药品其他属性字段
            YiyaobaoMed yiyaobaoMed = cmdStockDetailEbsMapper.getMedicineBySkuSample(sku);

            // 图片
            List<String> imageList = cmdStockDetailEbsMapper.getMedicineImageBySku(sku);
            if(CollUtil.isNotEmpty(imageList)) {
                yiyaobaoMed.setMainFilePath(imageList.get(0));
                yiyaobaoMed.setFilePath(CollUtil.join(imageList,","));
            }

            yiyaobaoMedMap.put(sku,yiyaobaoMed);

        }

        // 保存药品主数据
        for (Map.Entry<String, YiyaobaoMed> entry : yiyaobaoMedMap.entrySet()) {
            YiyaobaoMed yiyaobaoMed = entry.getValue();
            YiyaobaoMed yiyaobaoMed_image = ordOrderMapper.queryYiyaobaoMedImagesBySku(yiyaobaoMed.getSku());

            if(yiyaobaoMed_image != null && StrUtil.isNotBlank(yiyaobaoMed_image.getFilePath())) {
                yiyaobaoMed.setFilePath(yiyaobaoMed_image.getFilePath());
            }
            saveYiyaobaoMed(yiyaobaoMed);
        }

        // 保存药品-药房-价格-库存

        for (Map.Entry<String,SkuSellerPriceStock> entry : skuSellerPriceStockMap.entrySet()) {
            SkuSellerPriceStock skuSellerPriceStock = entry.getValue();
            saveSkuSellerPriceStock(skuSellerPriceStock);
        }

          // 更新药品中的价格（最低价），库存（合计）
        yxStoreProductService.updatePriceStock();


        Map<String,HashMap> map = new HashMap<>();
        // map.put("skuMap",skuMap);
        map.put("skuSellerPriceStockMap",skuSellerPriceStockMap);
        map.put("yiyaobaoMedMap",yiyaobaoMedMap);
        return map;
    }


    public OrderVo getYiyaobaoOrderbyOrderIdSample(String orderNo) {
        return ordOrderMapper.getYiyaobaoOrderbyOrderIdSample(orderNo);
    }



    @DS("multi-datasource1")
    public void saveProject(Project project){
        if(StrUtil.isNotBlank(project.getProjectCode())) {
            int existsCount = ordOrderMapper.countProject(project.getProjectCode());
            if(existsCount == 0) {
                String project_id = UUID.randomUUID().toString();
                ordOrderMapper.saveProject(project_id,"",project.getProjectCode(),project.getProjectName(),project.getProjectDesc());
                ordOrderMapper.saveProjectAttr(project_id);
            }
        }


    }

    @DS("multi-datasource1")
    public void updateYiyaobaoOrderSourceByPrescripNo(String prescripNo,String orderSource){
        ordOrderMapper.updateYiyaobaoOrderSourceByPrescripNo(prescripNo,orderSource);
    }

// 更新益药宝中订单支付状态 支付时间 支付方式
    @DS("multi-datasource1")
    public void updateYiyaobaoOrderInoByPrescripNo(YiyaobaoOrderInfo orderInfo){
        ordOrderMapper.updateYiyaobaoOrderInfoByPrescripNo(orderInfo);
    }


    @DS("multi-datasource1")
    public void updateYiyaobaoOrderSourceByOrderNo(String orderNo,String orderSource){
        ordOrderMapper.updateYiyaobaoOrderSourceByOrderNo(orderNo,orderSource);
    }

    @DS("multi-datasource1")
    public void updateYiyaobaoOrderInfoByOrderNo(YiyaobaoOrderInfo orderInfo){
        ordOrderMapper.updateYiyaobaoOrderInfoByOrderNo(orderInfo);
    }


    @DS("multi-datasource1")
    public String queryOrderSourceCode(String projectName) {
       return   ordOrderMapper.queryOrderSourceCode(projectName);
    }

}
