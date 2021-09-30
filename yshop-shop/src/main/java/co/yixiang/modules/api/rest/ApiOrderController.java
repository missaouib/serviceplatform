package co.yixiang.modules.api.rest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.api.common.ApiResult;
import co.yixiang.modules.api.domain.UserAgreement;
import co.yixiang.modules.api.dto.NotifyReceipt;
import co.yixiang.modules.api.dto.ProductDto;
import co.yixiang.modules.api.param.*;
import co.yixiang.modules.api.service.UserAgreementService;
import co.yixiang.modules.baiji.service.ProductMedStockServiceImpl;
import co.yixiang.modules.shop.domain.Product4project;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.YxStoreDiseaseService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.mapper.StoreProductMapper;
import co.yixiang.mp.utils.JsonUtils;
import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.utils.BASE64DecodedMultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "订单信息接口")
@RestController
@RequestMapping("api")
@Slf4j
public class ApiOrderController extends BaseController{

    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private ZhengDaTianQingServiceImpl zhengDaTianQingService;


    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxStoreDiseaseService yxStoreDiseaseService;

    @Value("${file.localUrl}")
    private String localUrl;
    @Autowired
    private LocalStorageService localStorageService;
    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private UserAgreementService userAgreementService;

    @Autowired
    private  StoreProductMapper storeProductMapper;

    @GetMapping("/callCenter/order")
    @ApiOperation(value = "订单对象",notes = "订单对象",response = OrderVo.class)
    public ApiResult<Paging<OrderVo>> getYiyaobaoOrderPageList(@Validated OrderQueryParam queryParam){
        log.info("queryParam.getMobile()={}",queryParam.getMobile());

        Paging<OrderVo> paging = yiyaobaoOrderService.getYiyaobaoOrderbyMobile(queryParam);

       /* OrderVo orderVo = new OrderVo();
        orderVo.setChannelName("益药公众号");
        orderVo.setDoctorName("王医生");
        orderVo.setHospitalName("上海中山医院");
        orderVo.setMobile("18017890127");
        orderVo.setName("张无忌");
        orderVo.setOrderDate("2020-05-28");
        orderVo.setOrderNo("20222200038188");
        orderVo.setStatus("完成");
        orderVo.setStoreName("上海众协药房中山西路店");
        orderVo.setExpressInfo("");

        orderVo.setDiscountTotalAmount(new BigDecimal("1620"));
        orderVo.setTotalAmount(new BigDecimal("1800"));
          *//*orderVo.setAddress("上海市龙华中路600号");
          orderVo.setReceiveMobile("18017890126");
          orderVo.setReceiveName("张三丰");
          orderVo.setDiagnoseResult("肺癌");*//*
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setProductName("复方脑肽节苷脂注射液");
        orderDetailVo.setDiscountPrice(new BigDecimal("900"));
        orderDetailVo.setUnitPrice(new BigDecimal("1000"));
        orderDetailVo.setDiscountRate(new BigDecimal("0.9"));
        orderDetailVo.setQty(1);
        orderDetailVo.setSpec("20ml");

        OrderDetailVo orderDetailVo2 = new OrderDetailVo();
        orderDetailVo2.setProductName("普宁可");
        orderDetailVo2.setDiscountPrice(new BigDecimal("720"));
        orderDetailVo2.setUnitPrice(new BigDecimal("800"));
        orderDetailVo2.setDiscountRate(new BigDecimal("0.9"));
        orderDetailVo2.setQty(1);
        orderDetailVo2.setSpec("20ml");

        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        orderDetailVoList.add(orderDetailVo2);
        orderDetailVoList.add(orderDetailVo);

        orderVo.setDetails(orderDetailVoList);
        List<OrderVo> orderVoList = new ArrayList<>();
        orderVoList.add(orderVo);*/
        return ApiResult.ok(paging);
    }

    @PostMapping("/yiyaobao/orderStatus")
    @ApiOperation(value = "益药宝订单状态",notes = "益药宝订单状态",response = String.class)
    public ApiResult<String> OrderStatus(@Validated OrderStatusParam queryParam){
        log.info("/yiyaobao/orderStatus OrderStatusParam={}",queryParam);
        String orderStatus = "";
        if(queryParam.getFlag()) {  // 正式环境
            orderStatus = yiyaobaoOrderService.getYiyaobaoOrderStatus(queryParam.getOrderNO());
        } else{  //测试环境
            orderStatus = yiyaobaoOrderService.getYiyaobaoOrderStatus2(queryParam.getOrderNO());
        }


        return ApiResult.ok(orderStatus);
    }

    /**
     * 订单取消   未支付的订单回退积分,回退优惠券,回退库存
     *
     */
    @Log(value = "基金会取消订单",type = 1)
    @PostMapping("/order/jjh/cancel")
    @ApiOperation(value = "订单取消",notes = "订单取消")
    public String cancelOrder4jjh(@RequestBody String jsonStr){
        log.info("基金会取消订单 {}",jsonStr);
        cn.hutool.json.JSONObject result = JSONUtil.createObj();
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        String orderId = jsonObject.getStr("order_sn");
        if(StrUtil.isBlank(orderId)) {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }

        /*YxStoreOrder order = orderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",orderId).select("extend_order_id"));
        if(order == null) {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }*/
        Boolean flag = zhengDaTianQingService.cancelOrder4jjh(orderId);
        if(flag) {
            result.put("success",true);
            result.put("message","ok");
            return result.toString();
        } else {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }



    }

    /**
     * 订单追回
     *
     */
    @Log(value = "基金会订单追回",type = 1)
    @PostMapping("/order/jjh/recover")
    @ApiOperation(value = "基金会订单追回",notes = "基金会订单追回")
    public String recoverOrder4jjh(@RequestBody String jsonStr){
        log.info("基金会订单追回 {}",jsonStr);
        cn.hutool.json.JSONObject result = JSONUtil.createObj();
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        String orderId = jsonObject.getStr("order_sn");
        if(StrUtil.isBlank(orderId)) {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }
      /*  YxStoreOrder order = orderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",orderId).select("extend_order_id"));
        if(order == null) {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }*/
        Boolean flag = zhengDaTianQingService.recoverOrder4jjh(orderId);
        if(flag) {
            result.put("success",true);
            result.put("message","ok");
            return result.toString();
        } else {
            result.put("success",false);
            result.put("message","参数错误");
            return result.toString();
        }
    }

    @PostMapping("/order/sync")
    @AnonymousAccess
    public ApiResult<String> SyncOrder(){

        yxStoreOrderService.syncOrderStatus();

        return ApiResult.ok();
    }

    @PostMapping("/product/list")
    @AnonymousAccess
    @Log("查询项目-药品配置")
    @ApiOperation("查询项目-药品配置")
    public ApiResult<String> productList(@RequestBody String jsonStr){
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        String projectNo = jsonObject.getStr("projectNo");

        if(StrUtil.isNotBlank(projectNo)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("project_no",projectNo);
            List<Product4project> product4projectList = product4projectService.list(queryWrapper);

            for(Product4project product4project : product4projectList) {
                product4project.getMaxPrice();
                product4project.getMinPrice();
                Integer productId = product4project.getProductId();
                product4project.getProductUniqueId();

                if(productId != null) {
                    YxStoreProduct product = yxStoreProductService.getById(productId);
                    if(product != null) {
                        ProductDto productDto = new ProductDto();
                        BeanUtils.copyProperties(product,productDto);

                        // 病种
                        if (StrUtil.isNotBlank(product.getDiseaseId())){
                            List<String> diseaseList = Arrays.asList(product.getDiseaseId().split(","));
                            QueryWrapper queryWrapper1 = new QueryWrapper();
                            queryWrapper1.in("id",diseaseList);
                            queryWrapper1.select("cate_name");
                            List<YxStoreDisease> yxStoreDiseaseList = yxStoreDiseaseService.list(queryWrapper1);
                            List<String> diseaseNameList = yxStoreDiseaseList.stream().map(disease->{ return disease.getCateName();}).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(diseaseNameList)) {
                               String diseaseName = CollUtil.join(diseaseNameList,",");
                                productDto.setDiseaseName(diseaseName);
                            }
                        }

                        if(StrUtil.isNotBlank(product.getType())) {
                            String typeName = storeProductMapper.queryProductTypeName(product.getType());
                            productDto.setTypeName(typeName);
                        }



                    }
                }

            }
            return ApiResult.ok(product4projectList);
        }

        return ApiResult.ok();
    }



    @Log("益药宝更新订单物流信息")
    @PostMapping("/order/orderInfo")
    @ApiOperation(value = "更新订单物流信息",notes = "更新订单物流信息",response = String.class)
    public ApiResult<String> orderInfo(@Validated @RequestBody OrderInfoParam orderInfoParam){
        log.info("/order/orderInfo orderInfoParam={}",orderInfoParam);

        yxStoreOrderService.updateOrderInfo(orderInfoParam);

        return ApiResult.ok();
    }


    @Log("益药宝更新订单/处方状态")
    @PostMapping("/order/prescripStatus")
    @ApiOperation(value = "更新订单状态",notes = "更新订单状态",response = String.class)
    @AnonymousAccess
    public JSONObject prescripStatus(@Validated @RequestBody PrescripStatusParam prescripStatusParam){
        log.info("/order/orderStatus prescripStatusParam={}",JSONUtil.parseObj(prescripStatusParam));
        try{
            yxStoreOrderService.prescripStatus(prescripStatusParam);
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",true);
            jsonObject.put("msg","回写处方单状态成功");
            jsonObject.put("code",0);
            return jsonObject;
        }catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",false);
            jsonObject.put("msg","回写处方单状态失败");
            jsonObject.put("code",90);
            return jsonObject;
        }

    }

    @Log("益药宝更新订单运费")
    @PostMapping("/order/orderFreight")
    @ApiOperation(value = "更新订单运费",notes = "更新订单运费",response = String.class)
    @AnonymousAccess
    public JSONObject orderFreight(@Validated @RequestBody List<OrderFreightParam> orderFreightParams){
        log.info("/order/orderFreight orderFreightParams={}",JSONUtil.parseArray(orderFreightParams));
        try{
            yxStoreOrderService.orderFreight(orderFreightParams);
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",true);
            jsonObject.put("msg","回传成功");
            jsonObject.put("code",0);
            return jsonObject;
        }catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",false);
            jsonObject.put("msg","回传失败");
            jsonObject.put("code",90);
            return jsonObject;
        }

    }

    @Log("订单物流运单号信息同步")
    @PostMapping("/order/waybill")
    @ApiOperation(value = "订单物流运单号信息同步",notes = "订单物流运单号信息同步",response = String.class)
    @AnonymousAccess
    public JSONObject waybill(@Validated @RequestBody  OrderFreightParam orderFreightParam){
        log.info("/order/waybill orderFreightParams={}",JSONUtil.parse(orderFreightParam));
        try{
            List<OrderFreightParam>  orderFreightParams=new ArrayList<>();
            orderFreightParams.add(orderFreightParam);
            yxStoreOrderService.orderFreight(orderFreightParams);
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",true);
            jsonObject.put("msg","回传成功");
            jsonObject.put("code",0);
            return jsonObject;
        }catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("success",false);
            jsonObject.put("msg","回传失败");
            jsonObject.put("code",90);
            return jsonObject;
        }

    }


    @Log("北京CA签名回调")
    @PostMapping("/order/caNotify")
    @ApiOperation(value = "更新订单状态",notes = "更新订单状态",response = String.class)
    @AnonymousAccess
    public String caNotify(@RequestBody NotifyReceipt notifyReceipt, HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            String requestId = notifyReceipt.getBody().getRequestId();
            log.info("CA签名回单 requestid=" + requestId);
            String pdfSign = notifyReceipt.getBody().getPdfSign();
            String signFlowId = notifyReceipt.getBody().getSignFlowId();

            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipart(pdfSign);

            StringBuilder url = new StringBuilder();
            if (StrUtil.isNotEmpty(localUrl)) { //存在走本地
                String fileName = requestId + ".pdf";
                LocalStorageDto localStorageDTO = localStorageService.create2signPdf(fileName, file);
                if ("".equals(url.toString())) {
                    url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                } else {
                    url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                }

            } else {//走七牛云
                String fileName = requestId + ".pdf";
                QiniuContent qiniuContent = qiNiuService.upload(file, qiNiuService.find(),fileName);
                if ("".equals(url.toString())) {
                    url = url.append(qiniuContent.getUrl());
                }else{
                    url = url.append(","+qiniuContent.getUrl());
                }

            }


            log.info("CA签名回单PDF webpath={}", url);

            // 更新用户同意书表
            UserAgreement userAgreement = userAgreementService.getOne(new QueryWrapper<UserAgreement>().eq("request_id",requestId),false);
            if(ObjectUtil.isEmpty(userAgreement)) {
                userAgreement = new UserAgreement();
                userAgreement.setSignFilePath(url.toString());
                userAgreement.setSignFlowId(signFlowId);
                userAgreement.setStatus(1);
                userAgreement.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                userAgreementService.save(userAgreement);
            } else {
                userAgreement.setSignFilePath(url.toString());
                userAgreement.setSignFlowId(signFlowId);
                userAgreement.setStatus(1);
                userAgreementService.updateById(userAgreement);
            }


            HashMap map = new HashMap();
            map.put("status", "0");
            map.put("message", "sucess");
            return  JSONUtil.parseObj(map).toString();
        }catch (Exception e){
            e.printStackTrace();
            log.error("电子签名回单回调失败:" + JSONUtil.parseObj(notifyReceipt));

            HashMap map = new HashMap();
            map.put("status", "1");
            map.put("message", "fail");
            return JSONUtil.parseObj(map).toString();

        }
    }
}
