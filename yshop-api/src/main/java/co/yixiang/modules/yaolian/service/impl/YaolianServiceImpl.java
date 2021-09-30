package co.yixiang.modules.yaolian.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;

import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.AppFromEnum;
import co.yixiang.enums.PayTypeEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.param.Order4ProjectParam;
import co.yixiang.modules.order.web.param.OrderDetail4ProjectParam;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.entity.YxStoreProductAttrValue;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.yaolian.dto.*;
import co.yixiang.modules.yaolian.entity.YaolianOrder;
import co.yixiang.modules.yaolian.entity.YaolianOrderDetail;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.utils.Sha1Util;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.OrderUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaolianServiceImpl {

    @Autowired
    private YaolianOrderService yaolianOrderService;

    @Autowired
    private YaolianOrderDetailService yaolianOrderDetailService;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxUserAddressService yxUserAddressService;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LocalStorageService localStorageService;

/*    @Autowired
    private SendPrsProducer sendPrsProducer;*/

    @Autowired
    private YxSystemStoreService yxSystemStoreService;
    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${yaolian.apiUrl}")
    private String apiUrl;
    @Value("${yaolian.nonce}")
    private String nonce;
    @Value("${yaolian.token}")
    private String token;
    @Value("${yaolian.cooperation}")
    private String cooperation;

    private String rxUrlSuffix = "/api/order/rx";

    private String refundUrlSuffix = "/api/order/refund";

    private YaolianOrder convertOrder(Order order){
        YaolianOrder yaolianOrder = new YaolianOrder();
        yaolianOrder.setAssistantMobile(order.getAssistant_mobile());
        yaolianOrder.setAssistantNumber(order.getAssistant_number());
        DateTime dateTime = DateUtil.parse(order.getCreate_time());
        yaolianOrder.setCreateTime(dateTime.toTimestamp());
        yaolianOrder.setFreePrice(order.getFree_price());
        yaolianOrder.setId(order.getId());
        yaolianOrder.setIsPrescription(order.getIs_prescription());
        yaolianOrder.setIsSuper(order.getIsSuper());
        yaolianOrder.setMemberId(order.getMember_id());
        yaolianOrder.setRxId(order.getRx_id());
        yaolianOrder.setSalePrice(order.getSale_price());
        yaolianOrder.setStoreId(order.getStore_id());
        yaolianOrder.setTotalPrice(order.getTotal_price());

        return yaolianOrder;
    };

    private YaolianOrderDetail convertOrderDetail(OrderDetail orderDetail){
        YaolianOrderDetail yaolianOrderDetail = new YaolianOrderDetail();
        yaolianOrderDetail.setActivityType(orderDetail.getActivity_type());
        yaolianOrderDetail.setAmount(orderDetail.getAmount());
        yaolianOrderDetail.setCode(orderDetail.getCode());
        yaolianOrderDetail.setCommonName(orderDetail.getCommon_name());
        yaolianOrderDetail.setDrugId(orderDetail.getDrug_id());
        yaolianOrderDetail.setPrice(orderDetail.getPrice());
        yaolianOrderDetail.setSettleDiscountRate(orderDetail.getSettle_discount_rate());

        return  yaolianOrderDetail;
    };

    public String addOrder(OrdersDTO resource){
        Order order = resource.getOrders().get(0);
        Integer storeId = Integer.valueOf(order.getStore_id());
        List<OrderDetail> orderDetailList = order.getRetailDetail();
        String addressDetail = "中山西路1440号5幢";  // 收货地址
        String imagePath = "";  // 处方图片地址
        String patientPhone = ""; // 患者电话
        String patientName = "";  // 患者名称
        String contactPhone = patientPhone; // 联系人电话
        String receiver = "";// 收货人
        String receiverMobile = ""; // 收货人电话
        String provinceName = "上海";
        String cityName = "长宁区";
        String districtName = "城区";
        YxSystemStore yxSystemStore = yxSystemStoreService.getById(storeId);
        if(yxSystemStore != null) {
             addressDetail = yxSystemStore.getAddress();  // 收货地址
             provinceName = yxSystemStore.getProvinceName();
             cityName = yxSystemStore.getCityName();
             districtName = "";
        }

 // 获取处方
     //   Elecrx elecrx = getElecrx(order.getRx_id());
        Elecrx elecrx = order.getElecrx();
        if(elecrx == null || StrUtil.isBlank(elecrx.getPic())) {
            // 获取处方失败
            YaolianOrderRefund yaolianOrderRefund = new YaolianOrderRefund();
            yaolianOrderRefund.setOrderNo(order.getId());
            yaolianOrderRefund.setReason("处方获取失败");
            pushOrderRefundInfo(yaolianOrderRefund);
            log.error("药联订单号[{}],处方号[{}],获取处方信息失败",order.getId(),order.getRx_id());
            return "";
        }
        String imagePathOriginal =  elecrx.getPic();
        String fileName = elecrx.getRx_id()+".png";
        LocalStorageDto localStorageDTO = localStorageService.createByUrl(imagePathOriginal,fileName);
        imagePath = localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName();

        if(StrUtil.isBlank(elecrx.getName())) {
            patientName = "药联用户";
        } else {
            patientName = elecrx.getName();
        }

        if(StrUtil.isBlank(elecrx.getPhone())) {
            patientPhone = "18000000000";
        } else {
            patientPhone = elecrx.getPhone();
        }

        contactPhone = patientPhone;
        receiver = patientName;
        receiverMobile = patientPhone;
        // 保存到数据库

        // 商城下单
        YxUser yxUser = yxUserService.getYxUserByPhone(contactPhone);
        int uid = yxUser.getUid();

        // 用药人信息

        YxDrugUsers yxDrugUsers = yxDrugUsersService.getDrugUserByInfo(uid,patientName,patientPhone);
        Integer userId = yxDrugUsers.getId();
        // 收货地址
        YxUserAddress yxUserAddress = yxUserAddressService.getYxUserAddressByInfo(uid,addressDetail,provinceName,cityName,districtName,0,receiver,receiverMobile);

        Order4ProjectParam order4ProjectParam = new Order4ProjectParam();
        order4ProjectParam.setUid(uid);
        order4ProjectParam.setPayType(PayTypeEnum.ThirdParty.getValue());
        order4ProjectParam.setAddressId(yxUserAddress.getId().toString());
        order4ProjectParam.setStoreId(Integer.valueOf(order.getStore_id()));
        order4ProjectParam.setImagePath(imagePath);
        order4ProjectParam.setProjectCode(ProjectNameEnum.YAOLIAN.getValue());
        order4ProjectParam.setDrugUserid(userId.toString());
        List<OrderDetail4ProjectParam> details = new ArrayList<>();
        for( OrderDetail orderDetail: orderDetailList) {
            OrderDetail4ProjectParam detail = new OrderDetail4ProjectParam();
            detail.setProductId( new Integer(orderDetail.getDrug_id()));
            detail.setNum( new Integer(orderDetail.getAmount()));

            // 获取unquieid
            Integer productId = new Integer(orderDetail.getDrug_id());
            LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(YxStoreProductAttrValue::getProductId,productId);
            lambdaQueryWrapper.eq(YxStoreProductAttrValue::getStoreId,storeId);
            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(lambdaQueryWrapper);
            detail.setProductUniqueId(yxStoreProductAttrValue.getUnique());

            details.add(detail);
        }
        order4ProjectParam.setDetails(details);
        order4ProjectParam.setMark("");
        order4ProjectParam.setOriginalOrderNo(order.getId());
        YxStoreOrder yxStoreOrder = yxStoreOrderService.addOrder4Project(order4ProjectParam);


        // dto 转换 entity
        YaolianOrder yaolianOrder = convertOrder(order);
        yaolianOrder.setOrderId(yxStoreOrder.getOrderId());
        yaolianOrder.setImage(imagePath);
        yaolianOrderService.save(yaolianOrder);

        for (OrderDetail orderDetail : orderDetailList) {
            // 保存
            YaolianOrderDetail yaolianOrderDetail = convertOrderDetail(orderDetail);
            yaolianOrderDetail.setOrderId(order.getId());
            yaolianOrderDetailService.save(yaolianOrderDetail);
        }

        return  yxStoreOrder.getOrderId();
    }


    public Elecrx getElecrx(String rx_id){
        RequestHead requestHead = new RequestHead();
        requestHead.setCooperation(cooperation);
        requestHead.setNonce(nonce);
        long time = System.currentTimeMillis()/1000;
        String time_str = String.valueOf(time);
        String sign = Sha1Util.getSign(nonce,time_str,token);
        requestHead.setTimestamp(time_str);
        requestHead.setSign(sign);
        requestHead.setTradeDate(DateUtil.now());

        RxDTO rxDTO = new RxDTO();
        rxDTO.setCooperation(cooperation);
        rxDTO.setRequestHead(requestHead);
        rxDTO.setRx_id(rx_id);
        String rxUrl = apiUrl + rxUrlSuffix;
        log.info("处方查询url:{}",rxUrl);
        try {
            String body = sendRequest(JSONUtil.parseObj(rxDTO).toString(),rxUrl);

            ElecrxResultDTO resultDTO = JSONUtil.toBean(body,ElecrxResultDTO.class);

            Elecrx elecrx = resultDTO.getData().getElecrx().get(0);
            log.info("药联处方:{}",elecrx);
            return elecrx;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
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


    public void pushOrderRefundInfo(YaolianOrderRefund yaolianOrderRefund) {
        YaolianOrderRefundDto yaolianOrderRefundDto = new YaolianOrderRefundDto();
        RequestHead requestHead = new RequestHead();
        requestHead.setCooperation(cooperation);
        requestHead.setNonce(nonce);
        long time = System.currentTimeMillis()/1000;
        String time_str = String.valueOf(time);
        String sign = Sha1Util.getSign(nonce,time_str,token);
        requestHead.setTimestamp(time_str);
        requestHead.setSign(sign);
        requestHead.setTradeDate(DateUtil.now());
        yaolianOrderRefundDto.setRequestHead(requestHead);
        yaolianOrderRefundDto.setOrderRefund(yaolianOrderRefund);
        String apiRefundUrl = apiUrl + refundUrlSuffix;
        String response = sendRequest(JSONUtil.parse(yaolianOrderRefundDto).toString(), apiRefundUrl);
        log.debug("返回信息：" + response);
        Map map = (Map) JSONUtils.parse(response);
        if(SystemConfigConstants.YAOLIAN_IS_TRUE.compareTo((String) map.get("errno"))==0){
            log.info("订单作废退单接口上传失败：" +  (String) map.get("error"));
        }else {
            log.info("订单作废退单接口上传成功：" +  (String) map.get("error"));
        }

    }
}
