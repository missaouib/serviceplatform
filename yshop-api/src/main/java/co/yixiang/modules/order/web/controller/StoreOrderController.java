/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.order.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.*;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.manage.service.impl.CASignServiceImpl;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.taiping.util.EncryptionToolUtilAes;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.modules.zhengdatianqing.dto.OrderDto;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.modules.order.web.param.ThirdPartyPayParam;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.tools.express.ExpressService;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.modules.activity.entity.YxStoreBargainUser;
import co.yixiang.modules.activity.service.YxStoreBargainUserService;
import co.yixiang.modules.activity.service.YxStorePinkService;
import co.yixiang.modules.activity.web.vo.YxStorePinkQueryVo;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.service.YxStoreOrderStatusService;
import co.yixiang.modules.order.web.dto.*;
import co.yixiang.modules.order.web.param.*;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.user.service.YxSystemAttachmentService;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.yiyaobao.dto.UserInfoDTO;
import co.yixiang.modules.yiyaobao.service.impl.OrderServiceImpl;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.tools.utils.AlipayProperties;
import co.yixiang.tools.utils.AlipayUtils;
import co.yixiang.tools.utils.mpai.MapiPayUtils;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 订单控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-27
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "订单模块", tags = "商城:订单模块", description = "订单模块")
public class StoreOrderController extends BaseController {

    private final YxStoreOrderService storeOrderService;
    private final YxStoreCartService cartService;
    private final YxUserService userService;
    private final YxUserAddressService addressService;
    private final YxStoreOrderCartInfoService orderCartInfoService;
    private final YxStoreProductReplyService productReplyService;
    private final YxStoreOrderStatusService orderStatusService;
    private final YxStoreCouponUserService couponUserService;
    private final YxSystemConfigService systemConfigService;
    private final YxStorePinkService storePinkService;
    private final YxExpressTemplateService yxExpressTemplateService;
    private final YxStoreBargainUserService storeBargainUserService;
    private final YxSystemStoreService systemStoreService;
    private final YxSystemAttachmentService systemAttachmentService;
    private final YxSystemStoreStaffService systemStoreStaffService;


    private static Lock lock = new ReentrantLock(false);

    @Value("${file.path}")
    private String path;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private ZhengDaTianQingServiceImpl zhengDaTianQingService;

    @Autowired
    private DictDetailService dictDetailService;

    @Autowired
    private CASignServiceImpl caSignService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Autowired
    private XkProcessService xkProcessService;

    @Autowired
    private InternetHospitalDemandService internetHospitalDemandService;

    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Value(("${yiyao.url}"))
    private String yiyao_url;

    @Value("${zhonganpuyao.cipherKey}")
    private String cipherKey;
    /**
     * 订单确认
     */
    @PostMapping("/order/confirm")
    @ApiOperation(value = "订单确认",notes = "订单确认")
    public ApiResult<ConfirmOrderDTO> confirm(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String cartId = jsonObject.getString("cartId");
        if(StrUtil.isEmpty(cartId)){
            return ApiResult.fail("请提交购买的商品");
        }
        int uid = SecurityUtils.getUserId().intValue();
        String projectCode = "";

        Map<String, Object> cartGroup = cartService.getUserProductCartList(uid,cartId,1,projectCode);
        if(ObjectUtil.isNotEmpty(cartGroup.get("invalid"))){
            return ApiResult.fail("有失效的商品请重新提交");
        }
        if(ObjectUtil.isEmpty(cartGroup.get("valid"))){
            return ApiResult.fail("请提交购买的商品");
        }
        List<YxStoreCartQueryVo> cartInfo = (List<YxStoreCartQueryVo>)cartGroup.get("valid");
        PriceGroupDTO priceGroup = storeOrderService.getOrderPriceGroup(cartInfo);

        ConfirmOrderDTO confirmOrderDTO = new ConfirmOrderDTO();

        confirmOrderDTO.setUsableCoupon(couponUserService
                .beUsableCoupon(uid,priceGroup.getTotalPrice()));
        //积分抵扣
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));

        //拼团 砍价 秒杀
        int combinationId = 0;
        int secKillId = 0;
        int bargainId = 0;
        if(cartId.split(",").length == 1){
            YxStoreCartQueryVo cartQueryVo = cartService.getYxStoreCartById(Integer
                    .valueOf(cartId));
            combinationId = cartQueryVo.getCombinationId();
            secKillId = cartQueryVo.getSeckillId();
            bargainId = cartQueryVo.getBargainId();
        }


        //拼团砍价秒杀类产品不参与抵扣
        if(combinationId > 0 || secKillId > 0 || bargainId > 0) confirmOrderDTO.setDeduction(true);

        //判断积分是否满足订单额度
        if(priceGroup.getTotalPrice() < Double.valueOf(other.getIntegralFull())) confirmOrderDTO.setEnableIntegral(false);

        confirmOrderDTO.setEnableIntegralNum(Double.valueOf(other.getIntegralMax()));


        confirmOrderDTO.setAddressInfo(addressService.getUserDefaultAddress(uid));

        confirmOrderDTO.setCartInfo(cartInfo);
        confirmOrderDTO.setPriceGroup(priceGroup);
        confirmOrderDTO.setOrderKey(storeOrderService.cacheOrderInfo(uid,cartInfo,
                                    priceGroup,other));


        confirmOrderDTO.setUserInfo(userService.getYxUserById(uid));

        //门店
      //  confirmOrderDTO.setSystemStore(systemStoreService.getStoreInfo("",""));

        return ApiResult.ok(confirmOrderDTO);
    }

    /**
     * 订单确认
     */
    @PostMapping("/order/confirm4Store")
    @ApiOperation(value = "订单确认-多门店",notes = "订单确认-多门店")
    public ApiResult<ConfirmOrderDTO> confirm4Store(@RequestBody String jsonStr){
        log.info("confirm4Store入参{}",jsonStr);
        JSONObject jsonObject = JSON.parseObject(jsonStr);

        String cartId = jsonObject.getString("cartId");
        if(StrUtil.isEmpty(cartId)){
            return ApiResult.fail("请提交购买的商品");
        }




        int uid = SecurityUtils.getUserId().intValue();

  /*      List<YxSystemStore> storeIdList = cartService.getStoreInfo(uid,"product",0,Arrays.asList(cartId.split(",")));

        if(CollUtil.isEmpty(storeIdList)) {
            return ApiResult.fail("商品对应的药店维护错误");
        } else if(storeIdList.size() != 1) {
            return ApiResult.fail("每次只能选择一家药房提交");
        }*/
        YxStoreCart yxStoreCart = cartService.getById(Arrays.asList(cartId.split(",")).get(0));

      //  String projectCode = yxStoreCart.getProjectCode();
       // String partnerCode= yxStoreCart.getPartnerCode();
        String refereeCode = yxStoreCart.getRefereeCode();
        String departCode = yxStoreCart.getDepartCode();
        Integer isNew = yxStoreCart.getIsNew();
        String projectCode = yxStoreCart.getProjectCode();
        if(StrUtil.isNotBlank(jsonObject.getString("projectCode"))) {
            projectCode = jsonObject.getString("projectCode");
        }

        String cardNumber = "";

        if(StrUtil.isNotBlank(jsonObject.getString("cardNumber"))) {
            cardNumber = jsonObject.getString("cardNumber");
        }

        String cardType = "";
        if(StrUtil.isNotBlank(jsonObject.getString("cardType"))) {
            cardType = jsonObject.getString("cardType");
        }


        String originalOrderNo = "";
        if(StrUtil.isNotBlank(jsonObject.getString("orderNumber"))) {
            originalOrderNo = jsonObject.getString("orderNumber");
        }
        Integer demandId = null;
        String image = "";
        if(StrUtil.isNotBlank(jsonObject.getString("demandId"))) {
            demandId = Integer.valueOf(jsonObject.getString("demandId"));

            // demandId 不为空 projectcode cardType cardNumber orderNumber 从处方单中获取
            InternetHospitalDemand internetHospitalDemand = internetHospitalDemandService.getById(demandId);
            projectCode = internetHospitalDemand.getProjectCode();
            cardType = internetHospitalDemand.getCardType();
            cardNumber = internetHospitalDemand.getCardNumber();
            originalOrderNo = internetHospitalDemand.getOrderNumber();
            image = internetHospitalDemand.getImage();
        }
        Integer storeId = yxStoreCart.getStoreId();
        YxSystemStore yxSystemStore = systemStoreService.getById(storeId);
       // String storeName = yxSystemStore.getName();
       // String storePhone = yxSystemStore.getPhone();



        List<StoreCartVo> cartGroupList = cartService.getUserProductCartList4Store(uid,cartId,isNew,projectCode,cardNumber,cardType,demandId);
        List<YxStoreCartQueryVo> cartInfo = new ArrayList();
        for(StoreCartVo storeCartVo : cartGroupList) {
            Map<String, Object> cartGroup = storeCartVo.getInfo();
            if(ObjectUtil.isNotEmpty(cartGroup.get("invalid"))){
                return ApiResult.fail("有失效的商品请重新提交");
            }
            if(ObjectUtil.isEmpty(cartGroup.get("valid"))){
                return ApiResult.fail("请提交购买的商品");
            }

            cartInfo.addAll((List<YxStoreCartQueryVo>)cartGroup.get("valid")) ;
        }
        Boolean needImageFlag = false;
        Boolean needCloudProduceFlag = false;

        String partnerCode = "";
        for(YxStoreCartQueryVo cartQueryVo:cartInfo) {

            if( cartQueryVo.getProductInfo().getType()!=null &&
                    ( "00".equals(cartQueryVo.getProductInfo().getType()) ||
                            "01".equals(cartQueryVo.getProductInfo().getType()) ||
                            "07".equals(cartQueryVo.getProductInfo().getType()) ||
                            "2".equals(cartQueryVo.getProductInfo().getType()) ||
                            "3".equals(cartQueryVo.getProductInfo().getType()) )  ) {
                needImageFlag = true;

            }

            if(cartQueryVo.getProductInfo().getIsNeedCloudProduce()!=null && cartQueryVo.getProductInfo().getIsNeedCloudProduce() == 1) {
                needCloudProduceFlag = true;
            }

            int exists = product4projectService.count(new QueryWrapper<Product4project>().eq("project_no",ProjectNameEnum.ROCHE_SMA.getValue()).eq("product_id",cartQueryVo.getProductId()) );
            if(exists >0 ) {
                projectCode = ProjectNameEnum.ROCHE_SMA.getValue();
            }

            if( StrUtil.isNotBlank(cartQueryVo.getPartnerCode())) {
                partnerCode = cartQueryVo.getPartnerCode();
            }
        }

        if(needImageFlag) {
            List carts =  Arrays.asList(cartId.split(","));
            if(carts.size() >= 6) {
                return ApiResult.fail("一张处方订单不能超过5种药品");
            }
        }

        PriceGroupDTO priceGroup = storeOrderService.getOrderPriceGroup(cartInfo);

        ConfirmOrderDTO confirmOrderDTO = new ConfirmOrderDTO();
        confirmOrderDTO.setNeedImageFlag(needImageFlag);
        confirmOrderDTO.setNeedCloudProduceFlag(needCloudProduceFlag);

        // 优惠券
       /* confirmOrderDTO.setUsableCoupon(couponUserService
                .beUsableCoupon(uid,priceGroup.getTotalPrice()));*/
        String orderSource = "";
        if(demandId != null) {
            orderSource = OrderSourceEnum.internetHospital.getValue();
        }
        // 默认不选优惠券
       // confirmOrderDTO.setUsableCoupon(couponUserService.beUsableCoupon4project(uid,priceGroup.getTotalPrice(),projectCode, cardType,orderSource));
        // 项目编码为空，药店是广州店
        if(StrUtil.isBlank(projectCode) && ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(yxSystemStore.getName())) {
            projectCode = ProjectNameEnum.HEALTHCARE.getValue();
        }

        Project project =  projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,projectCode));

        if(project == null) {
            return ApiResult.fail("项目代码["+projectCode+"]不存在");
        }

        //积分抵扣
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));

        other.setStoreId(storeId);
        other.setStoreName(yxSystemStore.getName());
        other.setProjectCode(projectCode);
        other.setPartnerCode(partnerCode);
        other.setRefereeCode(refereeCode);
        other.setDepartCode(departCode);
        // 太平项目的3个参数
        other.setOriginalOrderNo(originalOrderNo);
        other.setCardNumber(cardNumber);
        other.setCardType(cardType);
        other.setOrderSource(orderSource);
        other.setDemandId(demandId);

        other.setNeedImageFlag(needImageFlag);
        //拼团 砍价 秒杀
        int combinationId = 0;
        int secKillId = 0;
        int bargainId = 0;
        if(cartId.split(",").length == 1){
            YxStoreCartQueryVo cartQueryVo = cartService.getYxStoreCartById(Integer
                    .valueOf(cartId));
            combinationId = cartQueryVo.getCombinationId();
            secKillId = cartQueryVo.getSeckillId();
            bargainId = cartQueryVo.getBargainId();
        }


        //拼团砍价秒杀类产品不参与抵扣
        if(combinationId > 0 || secKillId > 0 || bargainId > 0) confirmOrderDTO.setDeduction(true);

        //判断积分是否满足订单额度
        if(priceGroup.getTotalPrice() < Double.valueOf(other.getIntegralFull())) confirmOrderDTO.setEnableIntegral(false);

        confirmOrderDTO.setEnableIntegralNum(Double.valueOf(other.getIntegralMax()));

        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(projectCode)) {

            yxSystemStore.setPhone(project.getPhone());
            // 最近一笔订单的医院名称
            confirmOrderDTO.setRocheHospitalName(storeOrderService.getRocheHospitalName(uid));
        }
        confirmOrderDTO.setAddressInfo(addressService.getUserDefaultAddressType(uid,0));
        confirmOrderDTO.setDrugUsersInfo(yxDrugUsersService.getUserDefaultDrugUser(uid));
        confirmOrderDTO.setCartInfo(cartInfo);
        confirmOrderDTO.setPriceGroup(priceGroup);
        confirmOrderDTO.setOrderKey(storeOrderService.cacheOrderInfo(uid,cartInfo,
                priceGroup,other));

        confirmOrderDTO.setProjectCode(projectCode);
        confirmOrderDTO.setUserInfo(userService.getYxUserById(uid));

        // 是否开启互联网医院获取处方
        confirmOrderDTO.setNeedInternetHospital(project.getNeedInternetHospital());
        confirmOrderDTO.setOrderSource(orderSource);
        confirmOrderDTO.setImage(image);
        //门店
        confirmOrderDTO.setSystemStore(yxSystemStore);
        //站点信息
        confirmOrderDTO.setSiteInfo(project.getSiteInfo());

        // 门店的支付方式
        if(StrUtil.isNotBlank(yxSystemStore.getPayType())) {
            confirmOrderDTO.setPayType(yxSystemStore.getPayType());
        }else{  // 如果门店没有指定支付方式，默认用短信链接的支付
            confirmOrderDTO.setPayType(PayTypeEnum.SMS.getValue());
        }

        // 项目的支付方式
        if(StrUtil.isNotBlank(projectCode) && ObjectUtil.isNotNull(project)) {
            confirmOrderDTO.setPayType(project.getPayType());
        }

        // 项目的物流模板
        String expressTemplateIds =  project.getExpressTemplateId();
        List<YxExpressTemplate> expressTemplateList = new ArrayList<>();
        if( StrUtil.isNotBlank(expressTemplateIds)) {
            List<String> ids_str =  Arrays.asList(expressTemplateIds.split(","));
            List<Integer> ids_int = new ArrayList<>();
            for(String id:ids_str) {
                ids_int.add(Integer.valueOf(id));
            }
            LambdaQueryWrapper<YxExpressTemplate> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.in(YxExpressTemplate::getId,ids_int);
            expressTemplateList = yxExpressTemplateService.list(queryWrapper);
        }
        confirmOrderDTO.setExpressTemplateList(expressTemplateList);

        return ApiResult.ok(confirmOrderDTO);
    }

    /**
     * 订单创建
     */
    @PostMapping("/order/create/{key}")
    @ApiOperation(value = "订单创建",notes = "订单创建")
    public ApiResult<ConfirmOrderDTO> create(@Valid @RequestBody OrderParam param,
                                             @PathVariable String key){

        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(StrUtil.isEmpty(key)) return ApiResult.fail("参数错误");

        YxStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key,uid);
        if(ObjectUtil.isNotNull(storeOrder)){
            map.put("status","EXTEND_ORDER");
            OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
            orderExtendDTO.setKey(key);
            orderExtendDTO.setOrderId(storeOrder.getOrderId());
            map.put("result",orderExtendDTO);
            return ApiResult.ok(map,"订单已生成");
        }

        // 砍价
        if(ObjectUtil.isNotNull(param.getBargainId())){
            YxStoreBargainUser storeBargainUser = storeBargainUserService.
                    getBargainUserInfo(param.getBargainId(),uid);
            if(ObjectUtil.isNull(storeBargainUser)) return ApiResult.fail("砍价失败");
            if(storeBargainUser.getStatus().equals(OrderInfoEnum.BARGAIN_STATUS_3.getValue())) return ApiResult.fail("砍价已支付");

            storeBargainUserService.setBargainUserStatus(param.getBargainId(),uid);

        }
        // 拼团
        if(ObjectUtil.isNotNull(param.getPinkId())){
            int pinkId = param.getPinkId();
            if(pinkId > 0){
                YxStoreOrder yxStoreOrder = storeOrderService.getOrderPink(pinkId,uid,1);
                if(ObjectUtil.isNotNull(yxStoreOrder)){
                    if(storePinkService.getIsPinkUid(pinkId,uid) > 0){
                        map.put("status","ORDER_EXIST");
                        OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                        orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                        map.put("result",orderExtendDTO);
                        return ApiResult.ok(map,"订单生成失败，你已经在该团内不能再参加了");
                    }
                }

                YxStoreOrder yxStoreOrderT = storeOrderService.getOrderPink(pinkId,uid,0);
                if(ObjectUtil.isNotNull(yxStoreOrderT)){
                    map.put("status","ORDER_EXIST");
                    OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                    orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                    map.put("result",orderExtendDTO);
                    return ApiResult.ok(map,"订单生成失败，你已经参加该团了，请先支付订单");
                }
            }

        }


        if(param.getFrom().equals("weixin"))  param.setIsChannel(0);
        //创建订单
        YxStoreOrder order = null;
        try{
             lock.lock();
             order = storeOrderService.createOrder(uid,key,param);
        }finally {
             lock.unlock();
        }


        if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单生成失败");

        String orderId = order.getOrderId();

        OrderExtendDTO orderDTO = new OrderExtendDTO();
        orderDTO.setKey(key);
        orderDTO.setOrderId(orderId);
        map.put("status","SUCCESS");
        map.put("result",orderDTO);
        //开始处理支付
        if(StrUtil.isNotEmpty(orderId)){
            //处理金额为0的情况
            if(order.getPayPrice().doubleValue() <= 0){
                storeOrderService.yuePay(orderId,uid);
                return ApiResult.ok(map,"支付成功");
            }

            switch (PayTypeEnum.toType(param.getPayType())){
                case WEIXIN:
                     try {
                         Map<String,String> jsConfig = new HashMap<>();
                         if(param.getFrom().equals("weixinh5")){
                             WxPayMwebOrderResult wxPayMwebOrderResult = storeOrderService
                                     .wxH5Pay(orderId);
                             log.info("wxPayMwebOrderResult:{}",wxPayMwebOrderResult);
                             jsConfig.put("mweb_url",wxPayMwebOrderResult.getMwebUrl());
                             orderDTO.setJsConfig(jsConfig);
                             map.put("result",orderDTO);
                             map.put("status","WECHAT_H5_PAY");
                             return ApiResult.ok(map);
                         }else if(param.getFrom().equals("routine")){
                             map.put("status","WECHAT_PAY");
                             WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                                     .wxAppPay(orderId);
                             jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                             jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
                             jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                             jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                             jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                             jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                             orderDTO.setJsConfig(jsConfig);
                             map.put("result",orderDTO);
                             return ApiResult.ok(map,"订单创建成功");
                         }else if(param.getFrom().equals("app")){//app支付
                             map.put("status","WECHAT_APP_PAY");
                             WxPayAppOrderResult wxPayAppOrderResult = storeOrderService
                                     .appPay(orderId);
                             jsConfig.put("appid",wxPayAppOrderResult.getAppId());
                             jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
                             jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
                             jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
                             jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
                             jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
                             jsConfig.put("sign",wxPayAppOrderResult.getSign());
                             orderDTO.setJsConfig(jsConfig);
                             map.put("result",orderDTO);
                             return ApiResult.ok(map,"订单创建成功");
                         } else{//公众号
                             map.put("status","WECHAT_PAY");
                             WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                                     .wxPay(orderId);
                             jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                             jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
                             jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                             jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                             jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                             jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                             orderDTO.setJsConfig(jsConfig);
                             map.put("result",orderDTO);
                             return ApiResult.ok(map,"订单创建成功");
                         }

                     } catch (WxPayException e) {
                        return ApiResult.fail(e.getMessage());
                    }
                case YUE:
                    storeOrderService.yuePay(orderId,uid);
                    return ApiResult.ok(map,"余额支付成功");
            }
        }


        return ApiResult.fail("订单生成失败");
    }


    /**
     * 订单创建
     */
    @PostMapping("/order/create4Store/{key}")
    @ApiOperation(value = "订单创建-多门店",notes = "订单创建-多门店")
    public ApiResult<ConfirmOrderDTO> create4Store(@Valid @RequestBody OrderParam param,
                                             @PathVariable String key){
       long start = System.currentTimeMillis();
       log.info("key={},开始时间：{}" ,key, start);
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(StrUtil.isEmpty(key)) return ApiResult.fail("参数错误");

        YxStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key,uid);
        if(ObjectUtil.isNotNull(storeOrder)){
            map.put("status","EXTEND_ORDER");
            OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
            orderExtendDTO.setKey(key);
            orderExtendDTO.setOrderId(storeOrder.getOrderId());
            map.put("result",orderExtendDTO);
            return ApiResult.ok(map,"订单已生成");
        }

        // 砍价
        if(ObjectUtil.isNotNull(param.getBargainId())){
            YxStoreBargainUser storeBargainUser = storeBargainUserService.
                    getBargainUserInfo(param.getBargainId(),uid);
            if(ObjectUtil.isNull(storeBargainUser)) return ApiResult.fail("砍价失败");
            if(storeBargainUser.getStatus().equals(OrderInfoEnum.BARGAIN_STATUS_3.getValue())) return ApiResult.fail("砍价已支付");

            storeBargainUserService.setBargainUserStatus(param.getBargainId(),uid);

        }
        // 拼团
        if(ObjectUtil.isNotNull(param.getPinkId())){
            int pinkId = param.getPinkId();
            if(pinkId > 0){
                YxStoreOrder yxStoreOrder = storeOrderService.getOrderPink(pinkId,uid,1);
                if(ObjectUtil.isNotNull(yxStoreOrder)){
                    if(storePinkService.getIsPinkUid(pinkId,uid) > 0){
                        map.put("status","ORDER_EXIST");
                        OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                        orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                        map.put("result",orderExtendDTO);
                        return ApiResult.ok(map,"订单生成失败，你已经在该团内不能再参加了");
                    }
                }

                YxStoreOrder yxStoreOrderT = storeOrderService.getOrderPink(pinkId,uid,0);
                if(ObjectUtil.isNotNull(yxStoreOrderT)){
                    map.put("status","ORDER_EXIST");
                    OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                    orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                    map.put("result",orderExtendDTO);
                    return ApiResult.ok(map,"订单生成失败，你已经参加该团了，请先支付订单");
                }
            }

        }


        if(param.getFrom().equals("weixin"))  param.setIsChannel(0);

        // 是否替其他人下单
        if(param.getInsteadFlag() == 0) {  // 否
            YxUser yxUser = userService.getById(uid);
            param.setRealName(yxUser.getRealName());
            param.setPhone(yxUser.getPhone());
        }

        //创建订单
        YxStoreOrder order = null;
        try{
            lock.lock();
            order = storeOrderService.createOrder4Store(uid,key,param);
        }finally {
            lock.unlock();
        }


        if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单生成失败");

        String orderId = order.getOrderId();

        OrderExtendDTO orderDTO = new OrderExtendDTO();
        orderDTO.setKey(key);
        orderDTO.setOrderId(orderId);
        map.put("status","SUCCESS");
        map.put("result",orderDTO);

        long end = System.currentTimeMillis();
        log.info("key={},结束，耗时{}毫秒" ,key, end -start);

        return ApiResult.ok(map,"订单创建成功");

       // return ApiResult.fail("订单生成失败");
    }

    public static void main(String[] args) {
        System.out.println(OrderUtil.getSecondTimestampTwo());
    }

    /**
     *  订单支付
     */
    @Log(value = "订单支付",type = 1)
    @PostMapping("/order/pay")
    @ApiOperation(value = "订单支付",notes = "订单支付")
    public ApiResult<ConfirmOrderDTO> pay(@Valid @RequestBody PayParam param){
        log.info("订单支付param={}",JSONUtil.parseObj(param).toString());
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(PayTypeEnum.YUE.getValue().equals(param.getPaytype()) && StrUtil.isBlank(param.getFrom())) {
            param.setFrom("routine");
        }


        YxStoreOrderQueryVo storeOrder = storeOrderService
                .getOrderInfo(param.getUni(),uid);
        if(ObjectUtil.isNull(storeOrder)) {
            return ApiResult.fail("订单不存在");
        }

        if( ObjectUtil.isNotNull(storeOrder.getPaid()) && storeOrder.getPaid() == 1) {
            return ApiResult.fail("订单已支付");
        }

        if( StrUtil.isBlank(storeOrder.getImagePath()) && storeOrder.getPrescriptionFlag() && storeOrder.getNeedInternetHospitalPrescription() !=null &&  storeOrder.getNeedInternetHospitalPrescription() == 0 ){
            return ApiResult.fail("请尽快上传处方图片");
        }

        if(StrUtil.isBlank(param.getUni()) || StrUtil.isBlank(param.getFrom()) || StrUtil.isBlank(param.getPaytype()) ){
            return ApiResult.fail("参数错误");
        }

        String orderId = storeOrder.getOrderId();

        OrderExtendDTO orderDTO = new OrderExtendDTO();
        orderDTO.setOrderId(orderId);
        orderDTO.setPrice(String.valueOf(storeOrder.getPayPrice()));
        String h5Url = "";
        String statusMessage ="";
        /*if( storeOrder.getNeedInternetHospitalPrescription() !=null &&  storeOrder.getNeedInternetHospitalPrescription() == 1) {
            AttrDTO attrDTO = new AttrDTO();
            attrDTO.setProjectCode(storeOrder.getProjectCode());
            attrDTO.setOrderNumber(storeOrder.getTaipingOrderNumber());
            attrDTO.setCardNumber(storeOrder.getCardNumber());
            attrDTO.setCardType(storeOrder.getCardType());
            attrDTO.setUid(storeOrder.getUid());
            attrDTO.setOrderId(storeOrder.getOrderId());
            attrDTO.setDrugUserid(storeOrder.getDrugUserId());
            attrDTO.setId(storeOrder.getId());
            log.info(" 获取 互联网医院开始=====");
            String result = xkProcessService.h5Url4ApplyPrescription(attrDTO);

            cn.hutool.json.JSONObject jsonObject_result = JSONUtil.parseObj(result);
            if("SUCCESS".equals(jsonObject_result.getStr("statusMessage"))) {
                h5Url = jsonObject_result.getJSONObject("data").getStr("h5Url");
                statusMessage = jsonObject_result.getStr("statusMessage");
            }else {
                statusMessage = jsonObject_result.getStr("statusMessage");
            }
            log.info(" 获取 互联网医院结束 url = {}",h5Url);
        }*/
        map.put("url",h5Url);
        map.put("statusMessage",statusMessage);
        map.put("status","SUCCESS");
        map.put("result",orderDTO);

        YxStoreOrder yxStoreOrder=new YxStoreOrder();
        yxStoreOrder.setId(storeOrder.getId());
        yxStoreOrder.setPayOutTradeNo(orderId+OrderUtil.getSecondTimestampTwo());
        yxStoreOrder.setPayType(param.getPaytype());
        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
        }else if(AppFromEnum.WEIXIN_H5.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_4.getValue());
        }else if(AppFromEnum.WECHAT.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_0.getValue());
        }else if(AppFromEnum.ali_h5.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_5.getValue());
        }else if(AppFromEnum.ali_miniapp.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_6.getValue());
        }else if(AppFromEnum.zhongan_miniapp.getValue().equals(param.getFrom())){
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_7.getValue());
            yxStoreOrder.setPayOutTradeNo(orderId);
        }else if(AppFromEnum.H5.getValue().equals(param.getFrom())) {
            yxStoreOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_8.getValue());
        }
        storeOrderService.updateById(yxStoreOrder);
        //开始处理支付
        if(StrUtil.isNotEmpty(orderId)){

            if(storeOrder.getPayPrice().doubleValue() <= 0) {  // 支付金额为0，直接调用付款成功
                //支付成功后处理
                storeOrderService.paySuccess(storeOrder.getOrderId(),param.getPaytype());
                return ApiResult.ok(map);
            } else {
                switch (PayTypeEnum.toType(param.getPaytype())){
                    case WEIXIN:
                        try {
                            Map<String,String> jsConfig = new HashMap<>();
                            if(param.getFrom().equals("weixinh5")){
//                                WxPayMwebOrderResult wxPayMwebOrderResult = storeOrderService
//                                        .wxH5Pay(orderId);

                                WxPayMwebOrderResult wxPayMwebOrderResult = storeOrderService
                                        .newWxH5Pay(orderId);
                                log.info("wxPayMwebOrderResult:{}",wxPayMwebOrderResult);

                                String url = yiyao_url + "#/mypackage/pages/ShoppingCart/paySuccess?orderId=" + orderId+"&price="+storeOrder.getPayPrice();
                                url = URLEncoder.encode(url);
                                String domain = yiyao_url.replace("https://","").replace("/","");
                                String h5AppReferer = URLEncoder.encode(domain) ;
                                String mweb_url = wxPayMwebOrderResult.getMwebUrl() + "&redirect_url=" + url +"&h5AppReferer="+h5AppReferer;

                                log.info("mweb_url={}",mweb_url);

                                jsConfig.put("mweb_url",mweb_url);
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                map.put("status","WECHAT_H5_PAY");
                                return ApiResult.ok(map,"");
                            }else if(param.getFrom().equals("routine")){
                                map.put("status","WECHAT_PAY");
//                                WxPayMpOrderResult wxPayMpOrderResult = storeOrderService.wxAppPay(orderId);
                                WxPayMpOrderResult wxPayMpOrderResult=storeOrderService.newWxAppPay(orderId);
                                jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                                jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
                                jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                                jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                                jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                                jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"");
                            }else if(param.getFrom().equals("app")){//app支付
                                map.put("status","WECHAT_APP_PAY");
                                WxPayAppOrderResult wxPayAppOrderResult = storeOrderService
                                        .appPay(orderId);
                                jsConfig.put("appid",wxPayAppOrderResult.getAppId());
                                jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
                                jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
                                jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
                                jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
                                jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
                                jsConfig.put("sign",wxPayAppOrderResult.getSign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"");
                            }else if(param.getFrom().equals("zhongan")){


                                orderDTO.setOrderTime(OrderUtil.stampToDate(storeOrder.getAddTime().toString()));
                                if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(storeOrder.getProjectCode())) {
                                    orderDTO.setPlatformCode("SYYMB");
                                } else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(storeOrder.getProjectCode())) {
                                    orderDTO.setPlatformCode("SYY");
                                }

                                orderDTO.setPrice( String.valueOf(storeOrder.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
                                Date addTime = OrderUtil.stampToDateObj(storeOrder.getAddTime().toString());
                                String expireTime =  DateUtil.offsetMinute(addTime,5).toString();
                                orderDTO.setExpireTime(expireTime);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"");
                            }else{
                                map.put("status","WECHAT_PAY");
                                WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                                        .wxPay(orderId);
                                //重新组装
                                jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                                jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
                                jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                                jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                                jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                                jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map);
                            }
                        } catch (WxPayException e) {
                            return ApiResult.fail(e.getMessage());
                        }
                    case YUE:
                        storeOrderService.yuePay(orderId,uid);
                        return ApiResult.ok(map,"余额支付成功");
                    case ALIPAY:
                        Map<String,String> jsConfig = new HashMap<>();
                        if(param.getFrom().equals("alipayh5")){

//                            String jsApiParameters = AlipayUtils.alipayH5Pay("支付宝支付", "上海益药药业", yxStoreOrder.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(storeOrder.getPayPrice()),AlipayProperties.appIdH5,orderId);
                        String jsApiParameters = storeOrderService.aliPayH5Pay(orderId);
                            jsConfig.put("jsApiParameters",jsApiParameters);
                            orderDTO.setJsConfig(jsConfig);
                            map.put("result",orderDTO);
                            return ApiResult.ok(map,"");
                        }else {
//                            String jsApiParameters = AlipayUtils.alipayTradeAppPay("支付宝支付", param.getUserid(), yxStoreOrder.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(storeOrder.getPayPrice()),AlipayProperties.appId);
                        String jsApiParameters = storeOrderService.alipayTradeAppPay(orderId,param.getUserid());
                            jsConfig.put("jsApiParameters", jsApiParameters);
                            orderDTO.setJsConfig(jsConfig);
                            map.put("result", orderDTO);
                            return ApiResult.ok(map,"");
                        }
                    case MAPI:
                        String url = yiyao_url + "#/mypackage/pages/ShoppingCart/paySuccess";
                        String parms="orderId=" + orderId+"&price="+storeOrder.getPayPrice();

                        Map<String,String> jsConfigMap = new HashMap<>();
                        if(param.getFrom().equals("h5")){
                            String jsApiParameters = MapiPayUtils.mapiH5Pay("翼支付", "益药商城商品购买", yxStoreOrder.getPayOutTradeNo(), new DecimalFormat("0.00").format(storeOrder.getPayPrice()),url,parms);
                            jsConfigMap.put("jsApiParameters",jsApiParameters);
                            orderDTO.setJsConfig(jsConfigMap);
                            map.put("result",orderDTO);
                            return ApiResult.ok(map,"");
                        }else {
                            return ApiResult.ok(map,"");
                        }
                    case ZhongAnPay:
                        yxStoreOrder.setPayOutTradeNo(orderId);
                        storeOrderService.updateById(yxStoreOrder);

                        orderDTO.setOrderTime(OrderUtil.stampToDate(storeOrder.getAddTime().toString()));
                        if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(storeOrder.getProjectCode())) {
                            orderDTO.setPlatformCode("SYYMB");
                        } else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(storeOrder.getProjectCode())) {
                            orderDTO.setPlatformCode("SYY");
                        }

                        orderDTO.setPrice( String.valueOf(storeOrder.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
                        Date addTime = OrderUtil.stampToDateObj(storeOrder.getAddTime().toString());
                        String expireTime =  DateUtil.offsetMinute(addTime,5).toString();
                        orderDTO.setExpireTime(expireTime);
                        map.put("result",orderDTO);
                        return ApiResult.ok(map,"");
                }
            }


        }


        return ApiResult.fail("订单生成失败");
    }

    /**
     * 订单列表
     */
    @Log(value = "查看订单列表",type = 1)
    @GetMapping("/order/list")
    @ApiOperation(value = "订单列表",notes = "订单列表")
    public ApiResult<List<YxStoreOrderQueryVo>> orderList(YxStoreOrderQueryParam queryParam){
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(storeOrderService.orderList(uid,queryParam.getType().intValue(),
                queryParam.getPage().intValue(),queryParam.getLimit().intValue(),queryParam));
      //  return ApiResult.ok(storeOrderService.yiyaobaoOrderList(uid,queryParam));

    }


    /**
     * 订单详情
     */
    @Log(value = "查看订单详情",type = 1)
    @GetMapping("/order/detail/{key}")
    @ApiOperation(value = "订单详情",notes = "订单详情")
    public ApiResult<YxStoreOrderQueryVo> detail(@PathVariable String key){



        if(StrUtil.isEmpty(key)) return ApiResult.fail("参数错误");

        return ApiResult.ok(storeOrderService.queryOrderDetail(key));
    }

    /**
     * 计算订单金额
     */
    @PostMapping("/order/computed/{key}")
    @ApiOperation(value = "计算订单金额",notes = "计算订单金额")
    public ApiResult<Map<String,Object>> computedOrder(@RequestBody String jsonStr,
                                                    @PathVariable String key){

        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(StrUtil.isEmpty(key)) return ApiResult.fail("参数错误");
        YxStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key,uid);
        if(ObjectUtil.isNotNull(storeOrder)){
            map.put("status","EXTEND_ORDER");
            OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
            orderExtendDTO.setKey(key);
            orderExtendDTO.setOrderId(storeOrder.getOrderId());
            map.put("result",orderExtendDTO);
            return ApiResult.ok(map,"订单已生成");
        }

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String addressId = jsonObject.getString("addressId");
        String couponId = jsonObject.getString("couponId");
        String shippingType = jsonObject.getString("shipping_type");
        String useIntegral = jsonObject.getString("useIntegral");

        String projectCode = jsonObject.getString("projectCode");
        String cardNumber = jsonObject.get("cardNumber")==null?null:jsonObject.get("cardNumber").toString();
        Integer expressTemplateId =  jsonObject.get("expressTemplateId")==null?null:jsonObject.getInteger("expressTemplateId");
        // 砍价
        if(ObjectUtil.isNotNull(jsonObject.getInteger("bargainId"))){
            YxStoreBargainUser storeBargainUser = storeBargainUserService.getBargainUserInfo(jsonObject.getInteger("bargainId")
                    ,uid);
            if(ObjectUtil.isNull(storeBargainUser)) return ApiResult.fail("砍价失败");
            if(storeBargainUser.getStatus() == 3) return ApiResult.fail("砍价已支付");
        }
        // 拼团
        if(ObjectUtil.isNotNull(jsonObject.getInteger("pinkId"))){
            int pinkId = jsonObject.getInteger("pinkId");
            YxStoreOrder yxStoreOrder = storeOrderService.getOrderPink(pinkId,uid,1);
            if(storePinkService.getIsPinkUid(pinkId,uid) > 0){
                map.put("status","ORDER_EXIST");
                OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                map.put("result",orderExtendDTO);
                return ApiResult.ok(map,"订单生成失败，你已经在该团内不能再参加了");
            }
            YxStoreOrder yxStoreOrderT = storeOrderService.getOrderPink(pinkId,uid,0);
            if(ObjectUtil.isNotNull(yxStoreOrderT)){
                map.put("status","ORDER_EXIST");
                OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                map.put("result",orderExtendDTO);
                return ApiResult.ok(map,"订单生成失败，你已经参加该团了，请先支付订单");
            }

        }

        ComputeDTO computeDTO = storeOrderService.computedOrder(uid,key,
                Integer.valueOf(couponId),
                Integer.valueOf(useIntegral),
                Integer.valueOf(shippingType),addressId,projectCode,cardNumber,expressTemplateId);


        map.put("result",computeDTO);
        map.put("status","NONE");
        return ApiResult.ok(map);
    }


    /**
     * 订单收货
     */
    @Log(value = "订单收货",type = 1)
    @PostMapping("/order/take")
    @ApiOperation(value = "订单收货",notes = "订单收货")
    @AnonymousAccess
    public ApiResult<Object> orderTake(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String orderId = jsonObject.getString("uni");
        if(StrUtil.isEmpty(orderId)) {
            return ApiResult.fail("参数错误");
        }
        int uid = SecurityUtils.getUserId().intValue();
        storeOrderService.takeOrder(orderId,uid);
        if(orderId.startsWith("JJH")) {
            zhengDaTianQingService.confirmOrder(orderId,DateUtil.now());
        }

        return ApiResult.ok("ok");

    }

    /**
     * 订单产品信息
     */
    @PostMapping("/order/product")
    @ApiOperation(value = "订单产品信息",notes = "订单产品信息")
    public ApiResult<Object> product(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String unique = jsonObject.getString("unique");
        if(StrUtil.isEmpty(unique)) return ApiResult.fail("参数错误");

        YxStoreOrderCartInfo orderCartInfo = orderCartInfoService.findByUni(unique);

        YxStoreCartQueryVo cartInfo = JSONObject.parseObject(orderCartInfo.getCartInfo(),
                YxStoreCartQueryVo.class);


        OrderCartInfoDTO orderCartInfoDTO = new OrderCartInfoDTO();
        orderCartInfoDTO.setBargainId(cartInfo.getBargainId());
        orderCartInfoDTO.setCartNum(cartInfo.getCartNum());
        orderCartInfoDTO.setCombinationId(cartInfo.getCombinationId());
        orderCartInfoDTO.setOrderId(storeOrderService
                .getById(orderCartInfo.getOid()).getOrderId());
        orderCartInfoDTO.setSeckillId(cartInfo.getSeckillId());

        ProductDTO productDTO = new ProductDTO();
        productDTO.setImage(cartInfo.getProductInfo().getImage());
        productDTO.setPrice(cartInfo.getProductInfo().getPrice().doubleValue());
        productDTO.setStoreName(cartInfo.getProductInfo().getStoreName());
        if(ObjectUtil.isNotEmpty(cartInfo.getProductInfo().getAttrInfo())){
            ProductAttrDTO productAttrDTO = new ProductAttrDTO();
            productAttrDTO.setImage(cartInfo.getProductInfo().getAttrInfo().getImage());
            productAttrDTO.setPrice(cartInfo.getProductInfo().getAttrInfo().getPrice().doubleValue());
            productAttrDTO.setProductId(cartInfo.getProductInfo().getAttrInfo().getProductId());
            productAttrDTO.setSuk(cartInfo.getProductInfo().getAttrInfo().getSuk());
            productDTO.setAttrInfo(productAttrDTO);
        }

        orderCartInfoDTO.setProductInfo(productDTO);


        return ApiResult.ok(orderCartInfoDTO);

    }

    /**
     * 订单评价
     */
    @Log(value = "评价商品",type = 1)
    @PostMapping("/order/comment")
    @ApiOperation(value = "订单评价",notes = "订单评价")
    public ApiResult<Object> comment(@Valid @RequestBody YxStoreProductReply productReply){
        int uid = SecurityUtils.getUserId().intValue();
        /*YxStoreOrderCartInfo orderCartInfo = orderCartInfoService
                .findByUni(productReply.getUnique());
        if(ObjectUtil.isEmpty(orderCartInfo)) return ApiResult.fail("评价产品不存在");
*/

        if(StrUtil.isBlank(productReply.getOid())) {
            return ApiResult.fail("订单id未传入");
        }

        if(StrUtil.isBlank(productReply.getUnique())) {
            return ApiResult.fail("药品id未传入");
        }
        int count = productReplyService.getInfoCount(productReply.getOid()
                ,productReply.getUnique());
        if(count > 0) return ApiResult.fail("该产品已评价");

        if(productReply.getProductScore() < 1) return ApiResult.fail("请为产品评分");
        if(productReply.getServiceScore() < 1) return ApiResult.fail("请为商家服务评分");

        productReply.setUid(uid);
        productReply.setOid(productReply.getOid());

        productReply.setAddTime(OrderUtil.getSecondTimestampTwo());
        productReply.setReplyType("product");
        productReply.setProductId(0);
        productReplyService.save(productReply);

/*        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setStatus(3);
        storeOrder.setOrderId(productReply.getOid());
        */
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("status",3);
        updateWrapper.eq("order_id",productReply.getOid());

        storeOrderService.update(updateWrapper);


        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id",productReply.getOid());

        YxStoreOrder yxStoreOrder = storeOrderService.getOne(queryWrapper,false);
        if(yxStoreOrder != null) {
            orderStatusService.create(yxStoreOrder.getId(),"check_order_over","用户评价");
        }

        return ApiResult.ok("ok");

    }


    /**
     * 订单删除
     */
    @PostMapping("/order/del")
    @ApiOperation(value = "订单删除",notes = "订单删除")
    public ApiResult<Object> orderDel(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String orderId = jsonObject.getString("uni");
        if(StrUtil.isEmpty(orderId)) return ApiResult.fail("参数错误");
        int uid = SecurityUtils.getUserId().intValue();
        storeOrderService.removeOrder(orderId,uid);

        return ApiResult.ok("ok");

    }

    /**
     * 订单退款理由
     */
    @GetMapping("/order/refund/reason")
    @ApiOperation(value = "订单退款理由",notes = "订单退款理由")
    public ApiResult<Object> refundReason(){
        ArrayList<String> list = new ArrayList<>();
        list.add("收货地址填错了");
        list.add("与描述不符");
        list.add("信息填错了，重新拍");
        list.add("收到商品损坏了");
        list.add("未按预定时间发货");
        list.add("其它原因");

        return ApiResult.ok(list);
    }

    /**
     * 订单退款审核
     */
    @Log(value = "提交订单退款",type = 1)
    @PostMapping("/order/refund/verify")
    @ApiOperation(value = "订单退款审核",notes = "订单退款审核")
    public ApiResult<Object> refundVerify(@RequestBody RefundParam param){
        int uid = SecurityUtils.getUserId().intValue();
        storeOrderService.orderApplyRefund(param,uid);
        return ApiResult.ok("ok");
    }

    /**
     * 订单取消   未支付的订单回退积分,回退优惠券,回退库存
     */
    @Log(value = "取消订单",type = 1)
    @PostMapping("/order/cancel")
    @ApiOperation(value = "订单取消",notes = "订单取消")
    public ApiResult<Object> cancelOrder(@RequestBody String jsonStr){
        int uid = SecurityUtils.getUserId().intValue();
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String orderId = jsonObject.getString("id");
        if(StrUtil.isEmpty(orderId)) return ApiResult.fail("参数错误");

        storeOrderService.cancelOrder(orderId,uid);

        return ApiResult.ok("ok");
    }


    /**@Valid
     * 获取物流信息,根据传的订单编号 ShipperCode快递公司编号 和物流单号,
     */
    @PostMapping("/order/express")
    @ApiOperation(value = "获取物流信息",notes = "获取物流信息",response = ExpressParam.class)
    public ApiResult<Object> express( @RequestBody ExpressParam expressInfoDo){
       /* ExpressInfo expressInfo = expressService.getExpressInfo(expressInfoDo.getOrderCode(),
                expressInfoDo.getShipperCode(), expressInfoDo.getLogisticCode());*/

        YxStoreOrder yxStoreOrder = storeOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",expressInfoDo.getOrderCode()));
        expressInfoDo.setYiyaobaoOrderId(yxStoreOrder.getYiyaobaoOrderId());
        ExpressInfo expressInfo = orderService.queryOrderLogisticsProcess(expressInfoDo);

        if(!expressInfo.isSuccess()) return ApiResult.fail(expressInfo.getReason());
        return ApiResult.ok(expressInfo);
    }

    /**
     * 订单核销
     */
    @PostMapping("/order/order_verific")
    @ApiOperation(value = "订单核销",notes = "订单核销")
    public ApiResult<Object> orderVerify( @RequestBody OrderVerifyParam param){
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setVerifyCode(param.getVerifyCode());
        storeOrder.setIsDel(OrderInfoEnum.CANCEL_STATUS_0.getValue());
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());

        YxStoreOrder order = storeOrderService.getOne(Wrappers.query(storeOrder));
        if(order == null) return ApiResult.fail("核销的订单不存在或未支付或已退款");

        int uid = SecurityUtils.getUserId().intValue();
        boolean checkStatus = systemStoreStaffService.checkStatus(uid,order.getStoreId());
        if(!checkStatus) return ApiResult.fail("您没有当前店铺核销权限！");

        if(order.getStatus() > 0)  return ApiResult.fail("订单已经核销");

        if(order.getCombinationId() > 0 && order.getPinkId() > 0){
            YxStorePinkQueryVo storePink = storePinkService.getYxStorePinkById(order.getPinkId());
            if(!OrderInfoEnum.PINK_STATUS_2.getValue().equals(storePink.getStatus())){
                return ApiResult.fail("拼团订单暂未成功无法核销");
            }
        }

        if(OrderInfoEnum.CONFIRM_STATUS_0.getValue().equals(param.getIsConfirm())){
            return ApiResult.ok(order);
        }

        storeOrderService.verificOrder(order.getOrderId());

        return ApiResult.ok("核销成功");
    }

    /**
     * 后台订单核销用于远程调用
     */
    @AnonymousAccess
    @GetMapping("/order/admin/order_verific/{code}")
    public ApiResult<Object> orderAminVerify(@PathVariable String code){
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setVerifyCode(code);
        storeOrder.setIsDel(OrderInfoEnum.CANCEL_STATUS_0.getValue());
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());

        YxStoreOrder order = storeOrderService.getOne(Wrappers.query(storeOrder));
        if(order == null) return ApiResult.fail("核销的订单不存在或未支付或已退款");


        if(order.getStatus() > 0)  return ApiResult.fail("订单已经核销");

        if(order.getCombinationId() > 0 && order.getPinkId() > 0){
            YxStorePinkQueryVo storePink = storePinkService.getYxStorePinkById(order.getPinkId());
            if(!OrderInfoEnum.PINK_STATUS_2.getValue().equals(storePink.getStatus())){
                return ApiResult.fail("拼团订单暂未成功无法核销");
            }
        }

        storeOrderService.verificOrder(order.getOrderId());

        return ApiResult.ok("核销成功");
    }


    /**
     * 订单列表
     */
    @Log(value = "下单验证码",type = 1)
    @GetMapping("/order/generateVerifyCode")
    @ApiOperation(value = "下单验证码",notes = "下单验证码")
    public ApiResult<Object> generateVerifyCode(UserInfoDTO queryParam){
        Boolean flag = orderService.generateVerifyCode(queryParam.getName(),queryParam.getMobile());
        if(flag) {
            return ApiResult.ok("验证码发送成功");
        } else {
            return ApiResult.fail("验证码发送失败");
        }

    }

    /**
     * 判断需求单提交时是否勾选了多家药房
     */
    @PostMapping("/order/checkStoreNum")
    @ApiOperation(value = "检查订单是否包含多家药房",notes = "检查订单是否包含多家药房")
    public ApiResult<Boolean> checkStoreNum(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String cartId = jsonObject.getString("cartId");
        if(StrUtil.isEmpty(cartId)){
            return ApiResult.fail("请提交购买的商品");
        }
        int uid = SecurityUtils.getUserId().intValue();
        YxStoreCart yxStoreCart = cartService.getById(Arrays.asList(cartId.split(",")).get(0));
        cn.hutool.json.JSONObject jsonObject1 = JSONUtil.createObj();
        jsonObject1.put("flag",true);
        String projectCode = yxStoreCart.getProjectCode();
        List<String> projectCodes = new ArrayList<>();
        projectCodes.add(projectCode);
        if(StrUtil.isBlank(projectCode)) {
            projectCodes.add(projectCode);
        }
        List<YxSystemStore> storeIdList = cartService.getStoreInfo(uid,"product",0,Arrays.asList(cartId.split(",")),projectCodes);
        Boolean flag = true;
        if(CollUtil.isEmpty(storeIdList)) {
            jsonObject1.put("flag",false);
            jsonObject1.put("msg","商品对应的药店维护错误");

            return ApiResult.ok(jsonObject1);

        } else if(storeIdList.size() != 1) {
            jsonObject1.put("flag",false);
            jsonObject1.put("msg","请选择同一家药房的药品");

            return ApiResult.ok(jsonObject1);
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",Arrays.asList(cartId.split(",")));
        List<YxStoreCart> cartList = cartService.list(queryWrapper);
        Boolean needImageFlag = false;
        for(YxStoreCart cart : cartList) {
            YxStoreProduct yxStoreProduct = yxStoreProductService.getById(cart.getProductId());
            if(yxStoreProduct!= null && yxStoreProduct.getType() !=null &&
                    ( "00".equals(yxStoreProduct.getType()) ||
                            "01".equals(yxStoreProduct.getType()) ||
                            "07".equals(yxStoreProduct.getType()) ||
                            "2".equals(yxStoreProduct.getType()) ||
                            "3".equals(yxStoreProduct.getType()) )
            ) {
                needImageFlag = true;
            }
        }


        if(needImageFlag) {
            List carts =  Arrays.asList(cartId.split(","));
            if(carts.size() >= 6) {
                jsonObject1.put("flag",false);
                jsonObject1.put("msg","一张处方订单不能超过5种药品");
                return ApiResult.ok(jsonObject1);
            }
        }

        return ApiResult.ok(jsonObject1);
    }


    /**
     * 慈善赠药的订单
     */
    @PostMapping("/order/external")
    @ApiOperation(value = "慈善赠药的订单",notes = "慈善赠药的订单")
    @AnonymousAccess
    public ApiResult<Boolean> externalOrder(@RequestBody List<OrderDto> orderDtoList){

        if(CollUtil.isNotEmpty(orderDtoList)) {
            log.info("收到慈善赠药订单：{} 条" ,orderDtoList.size());
            zhengDaTianQingService.createOrderLoop(orderDtoList);
        }

        return ApiResult.ok(true);
    }


    /**
     * 慈善赠药的订单
     */
    @PostMapping("/order/sendYiyaobao")
    @ApiOperation(value = "发送订单到益药宝",notes = "发送订单到益药宝")
    @AnonymousAccess
    public ApiResult<Boolean> sendYiyaobao(@RequestBody String jsonStr){
        List<String> list = Arrays.asList(JSONUtil.parseObj(jsonStr).getStr("data").split(","));
        log.info("{}", list.size());
        storeOrderService.dualCSOrder(list);
        return ApiResult.ok(true);
    }


    /**
     * 订单创建
     */
    @Log
    @PostMapping("/order/create4project/{key}")
    @ApiOperation(value = "订单创建-项目",notes = "订单创建-项目")
    public ApiResult<ConfirmOrderDTO> create4project(@Valid @RequestBody OrderParam param,
                                             @PathVariable String key){
        log.info("OrderParam()={}",JSONObject.toJSONString(param));

        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(StrUtil.isEmpty(key)) {
            return ApiResult.fail("参数错误");
        }
        if(StringUtils.isNotEmpty(param.getOrderNumber()) && PayTypeEnum.ZhongAnPay.getValue().equals(param.getPayType()) && ProjectNameEnum.LINGYUANZHI.getValue().equals(param.getProjectCode())){
            YxStoreOrder yxStoreOrder= storeOrderService.findByUidAndOriginalOrderNo(uid,param.getOrderNumber());
            if(yxStoreOrder!=null){
                return ApiResult.fail("不能重复领取。");
            }
        }
        YxStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key,uid);
        if(ObjectUtil.isNotNull(storeOrder)){
            map.put("status","EXTEND_ORDER");
            OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
            orderExtendDTO.setKey(key);
            orderExtendDTO.setOrderId(storeOrder.getOrderId());
            map.put("result",orderExtendDTO);
            return ApiResult.ok(map,"订单已生成");
        }
        param.setBargainId(null);
        param.setPinkId(null);

        // 砍价
        if(ObjectUtil.isNotNull(param.getBargainId())){
            YxStoreBargainUser storeBargainUser = storeBargainUserService.
                    getBargainUserInfo(param.getBargainId(),uid);
            if(ObjectUtil.isNull(storeBargainUser)) return ApiResult.fail("砍价失败");
            if(storeBargainUser.getStatus().equals(OrderInfoEnum.BARGAIN_STATUS_3.getValue())) return ApiResult.fail("砍价已支付");

            storeBargainUserService.setBargainUserStatus(param.getBargainId(),uid);

        }
        // 拼团
        if(ObjectUtil.isNotNull(param.getPinkId())){
            int pinkId = param.getPinkId();
            if(pinkId > 0){
                YxStoreOrder yxStoreOrder = storeOrderService.getOrderPink(pinkId,uid,1);
                if(ObjectUtil.isNotNull(yxStoreOrder)){
                    if(storePinkService.getIsPinkUid(pinkId,uid) > 0){
                        map.put("status","ORDER_EXIST");
                        OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                        orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                        map.put("result",orderExtendDTO);
                        return ApiResult.ok(map,"订单生成失败，你已经在该团内不能再参加了");
                    }
                }

                YxStoreOrder yxStoreOrderT = storeOrderService.getOrderPink(pinkId,uid,0);
                if(ObjectUtil.isNotNull(yxStoreOrderT)){
                    map.put("status","ORDER_EXIST");
                    OrderExtendDTO orderExtendDTO = new OrderExtendDTO();
                    orderExtendDTO.setOrderId(yxStoreOrder.getOrderId());
                    map.put("result",orderExtendDTO);
                    return ApiResult.ok(map,"订单生成失败，你已经参加该团了，请先支付订单");
                }
            }

        }

        if(param.getFrom().equals("weixin")) {
            param.setIsChannel(0);
        }
        //创建订单
        YxStoreOrder order = null;
        try{
            lock.lock();
            order = storeOrderService.createOrder4Project(uid,key,param);
        }finally {
            lock.unlock();
        }


        if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单生成失败");

        String orderId = order.getOrderId();

        OrderExtendDTO orderDTO = new OrderExtendDTO();
        orderDTO.setKey(key);
        orderDTO.setOrderId(orderId);
        orderDTO.setPrice(String.valueOf(order.getPayPrice()));
        map.put("status","SUCCESS");
        map.put("result",orderDTO);
        map.put("url","");


        String h5Url = "";
        String statusMessage = "";
        log.info("param.getNeedInternetHospitalPrescription()={}",param.getNeedInternetHospitalPrescription());
        /*if( param.getNeedInternetHospitalPrescription() !=null &&  param.getNeedInternetHospitalPrescription() == 1) {
            AttrDTO attrDTO = new AttrDTO();
            attrDTO.setProjectCode(param.getProjectCode());
            attrDTO.setOrderNumber(param.getOrderNumber());
            attrDTO.setCardNumber(param.getCardNumber());
            attrDTO.setCardType(param.getCardType());
            attrDTO.setUid(SecurityUtils.getUserId().intValue());
            attrDTO.setOrderId(order.getOrderId());
            attrDTO.setId(order.getId());
            attrDTO.setDrugUserid(order.getDrugUserId());
            log.info(" 获取 互联网医院开始=====");
            String result = xkProcessService.h5Url4ApplyPrescription(attrDTO);


            cn.hutool.json.JSONObject jsonObject_result = JSONUtil.parseObj(result);
            if("SUCCESS".equals(jsonObject_result.getStr("statusMessage"))) {
                h5Url = jsonObject_result.getJSONObject("data").getStr("h5Url");
                statusMessage = jsonObject_result.getStr("statusMessage");
            }else {
                statusMessage = jsonObject_result.getStr("statusMessage");
            }
            log.info(" 获取 互联网医院结束 url = {}",h5Url);


        }*/
        map.put("url",h5Url);
        map.put("statusMessage",statusMessage);
        log.info("订单创建成功");
        return ApiResult.ok(map,"订单创建成功");

// 是否需要直接在线支付,这里需要优化
       /* if(PayTypeEnum.WEIXIN.getValue().equals(order.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(order.getPayType()) || PayTypeEnum.YUE.getValue().equals(order.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(order.getPayType()) ) {
            //开始处理支付
            if(StrUtil.isNotEmpty(orderId)){
                //处理金额为0的情况
                if(order.getPayPrice().doubleValue() <= 0){
                    storeOrderService.yuePay(orderId,uid);
                    return ApiResult.ok(map,"支付成功");
                }

                switch (PayTypeEnum.toType(param.getPayType())){
                    case WEIXIN:
                        try {
                            Map<String,String> jsConfig = new HashMap<>();
                            if(param.getFrom().equals("weixinh5")){
                                WxPayMwebOrderResult wxPayMwebOrderResult = storeOrderService
                                        .wxH5Pay(orderId);
                                log.info("wxPayMwebOrderResult:{}",wxPayMwebOrderResult);
                                String url = yiyao_url + "#/mypackage/pages/ShoppingCart/paySuccess?orderId=" + order.getOrderId()+"&price="+order.getPayPrice();
                                url = URLEncoder.encode(url);
                                String domain = yiyao_url.replace("https://","").replace("/","");
                                String h5AppReferer = URLEncoder.encode(domain) ;
                                String mweb_url = wxPayMwebOrderResult.getMwebUrl() + "&redirect_url=" + url +"&h5AppReferer="+h5AppReferer;

                                log.info("mweb_url={}",mweb_url);
                                jsConfig.put("mweb_url",mweb_url);
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                map.put("status","WECHAT_H5_PAY");
                                return ApiResult.ok(map);
                            }else if(param.getFrom().equals("routine")){
                                map.put("status","WECHAT_PAY");
                                WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                                        .wxAppPay(orderId);
                                jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                                jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
                                jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                                jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                                jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                                jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"订单创建成功");
                            }else if(param.getFrom().equals("app")){//app支付
                                map.put("status","WECHAT_APP_PAY");
                                WxPayAppOrderResult wxPayAppOrderResult = storeOrderService
                                        .appPay(orderId);
                                jsConfig.put("appid",wxPayAppOrderResult.getAppId());
                                jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
                                jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
                                jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
                                jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
                                jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
                                jsConfig.put("sign",wxPayAppOrderResult.getSign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"订单创建成功");
                            }else if(param.getFrom().equals("zhongan")){
                                UpdateWrapper<YxStoreOrder> updateWrapper = new UpdateWrapper<>();
                                updateWrapper.eq("order_id",orderId);
                                updateWrapper.set("pay_out_trade_no",orderId);
                                storeOrderService.update(updateWrapper);

                                orderDTO.setOrderTime(OrderUtil.stampToDate(order.getAddTime().toString()));
                                if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(order.getProjectCode())) {
                                    orderDTO.setPlatformCode("SYYMB");
                                } else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(order.getProjectCode())) {
                                    orderDTO.setPlatformCode("SYY");
                                }

                                orderDTO.setPrice( String.valueOf(order.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
                                Date addTime = OrderUtil.stampToDateObj(order.getAddTime().toString());
                                String expireTime =  DateUtil.offsetMinute(addTime,5).toString();
                                orderDTO.setExpireTime(expireTime);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map);
                            } else{//公众号
                                map.put("status","WECHAT_PAY");
                                WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                                        .wxPay(orderId);
                                jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                                jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
                                jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                                jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                                jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                                jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                                orderDTO.setJsConfig(jsConfig);
                                map.put("result",orderDTO);
                                return ApiResult.ok(map,"订单创建成功");
                            }

                        } catch (WxPayException e) {
                            return ApiResult.fail(e.getMessage());
                        }
                    case YUE:
                        storeOrderService.yuePay(orderId,uid);
                        return ApiResult.ok(map,"余额支付成功");
                    case ALIPAY:
                        Map<String,String> jsConfig = new HashMap<>();
                        log.info("param.getFrom : "+ param.getFrom());
                        log.info("orderId : "+ orderId);
                        log.info("storeOrder : "+ order==null?null:JSONObject.toJSONString(order));
                        if(param.getFrom().equals("alipayh5")){
                            String jsApiParameters = AlipayUtils.alipayH5Pay("支付宝支付", "上海益药药业", order.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(order.getPayPrice()), AlipayProperties.appIdH5,orderId);
                            jsConfig.put("jsApiParameters",jsApiParameters);
                            orderDTO.setJsConfig(jsConfig);
                            map.put("result",orderDTO);
                            return ApiResult.ok(map);
                        }else{
                            String jsApiParameters = AlipayUtils.alipayTradeAppPay("支付宝支付", param.getUserid(), order.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(order.getPayPrice()), AlipayProperties.appId);
                            jsConfig.put("jsApiParameters",jsApiParameters);
                            orderDTO.setJsConfig(jsConfig);
                            map.put("result",orderDTO);
                            return ApiResult.ok(map);
                        }
                    case MAPI:
                        String url = yiyao_url + "#/mypackage/pages/ShoppingCart/paySuccess";
                        String parms="orderId=" + orderId+"&price="+storeOrder.getPayPrice();

                        Map<String,String> jsConfigMap = new HashMap<>();
                        if(param.getFrom().equals("h5")){
                            String jsApiParameters = MapiPayUtils.mapiH5Pay("翼支付", "上海益药药业", order.getPayOutTradeNo(), new DecimalFormat("0.00").format(storeOrder.getPayPrice()),url,parms);
                            jsConfigMap.put("jsApiParameters",jsApiParameters);
                            orderDTO.setJsConfig(jsConfigMap);
                            map.put("result",orderDTO);
                            return ApiResult.ok(map);
                        }else {
                            return ApiResult.ok(map);
                        }
                    case ZhongAnPay:
                        UpdateWrapper<YxStoreOrder> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("order_id",orderId);
                        updateWrapper.set("pay_out_trade_no",orderId);
                        storeOrderService.update(updateWrapper);

                        orderDTO.setOrderTime(OrderUtil.stampToDate(order.getAddTime().toString()));
                        if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(order.getProjectCode())) {
                            orderDTO.setPlatformCode("SYYMB");
                        } else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(order.getProjectCode())) {
                            orderDTO.setPlatformCode("SYY");
                        }

                        orderDTO.setPrice( String.valueOf(order.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
                        Date addTime = OrderUtil.stampToDateObj(order.getAddTime().toString());
                        String expireTime =  DateUtil.offsetMinute(addTime,5).toString();
                        orderDTO.setExpireTime(expireTime);
                        map.put("result",orderDTO);
                        return ApiResult.ok(map);
                }
            }
        } else {
            return ApiResult.ok(map,"订单创建成功");
        }

        return ApiResult.fail("订单生成失败");*/
    }



    /**
     * 获取签名url
     */
    @GetMapping("/order/caSignUrl")
    @ApiOperation(value = "获取签名url",notes = "获取签名url")
    @AnonymousAccess
    public ApiResult<Object> caSignUrl(UserInfoDTO queryParam){

        if(StrUtil.isBlank(queryParam.getOrderKey())) {
            return ApiResult.fail("缺少参数：订单key");
        }
       // Integer uid = SecurityUtils.getUserId().intValue();
        Integer uid = null;
        String signUrl = caSignService.getSignAmgKnowHtml(queryParam.getOrderKey(),uid);
        if(StrUtil.isNotBlank(signUrl)) {
            return ApiResult.ok(signUrl);
        } else {
            return ApiResult.fail("获取签名url失败");
        }
    }


    /**
     * 获取签名url
     */
    @GetMapping("/order/caSignStatus")
    @ApiOperation(value = "获取签名状态",notes = "获取签名状态")
    @AnonymousAccess
    public ApiResult<Object> caSignStatus(UserInfoDTO queryParam){

        if(StrUtil.isBlank(queryParam.getOrderKey())) {
            return ApiResult.fail("缺少参数：订单key");
        }
        String signUrl = caSignService.querySignResult(queryParam.getOrderKey(),queryParam.getOrderNo());
        if(StrUtil.isNotBlank(signUrl)) {
            return ApiResult.ok(signUrl);
        } else {
            return ApiResult.fail("获取签名结果失败");
        }
    }


    /**
     * 订单更新线下付款凭证
     */
    @PostMapping("/order/payVoucher")
    @ApiOperation(value = "订单更新线下付款凭证",notes = "订单更新线下付款凭证")
    public ApiResult<Object> payVoucher( @RequestBody OrderParam param){
        storeOrderService.updatePayVoucher(param);
        return ApiResult.ok("更新付款信息成功");
    }

    /**
     * 根据orderId获取互联网医院的页面url
     */
    @PostMapping("/order/getHpUrl")
    @ApiOperation(value = "根据orderId获取互联网医院的页面url",notes = "根据orderId获取互联网医院的页面url")
    public ApiResult<Object> getHpUrl( @RequestBody String jsonStr){

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String orderId = jsonObject.getString("orderId");

        String result = storeOrderService.getHpUrl(orderId);
        String h5Url = "";
        String statusMessage ="";
        cn.hutool.json.JSONObject jsonObject_result = JSONUtil.parseObj(result);
        if("SUCCESS".equals(jsonObject_result.getStr("statusMessage"))) {
            h5Url = jsonObject_result.getJSONObject("data").getStr("h5Url");
            statusMessage = jsonObject_result.getStr("statusMessage");
        }else {
            statusMessage = jsonObject_result.getStr("statusMessage");
        }

        log.info(" 获取 互联网医院结束 url = {}",h5Url);

        Map map = new HashMap();
        map.put("url",h5Url);
        map.put("statusMessage",statusMessage);
        return ApiResult.ok(map,"ok");
    }



    /**
     * 项目下单
     */
    @PostMapping("/order/add4Project")
    @ApiOperation(value = "项目下单",notes = "项目下单")
    public ApiResult<Object> add4Project( @RequestBody Order4ProjectParam order4ProjectParam){
       int uid = SecurityUtils.getUserId().intValue();
       order4ProjectParam.setUid(uid);
        Map<String,Object> map = new LinkedHashMap<>();

       try{

           // 校验销售区域
           String projectCode = order4ProjectParam.getProjectCode();
           if(StrUtil.isBlank(projectCode)) {
               return ApiResult.fail("项目代码不能为空");
           }

           LambdaQueryWrapper<ProjectSalesArea> queryWrapper = new LambdaQueryWrapper();
           queryWrapper.eq(ProjectSalesArea::getProjectCode,projectCode);

           List<ProjectSalesArea> areaList = projectSalesAreaService.list(queryWrapper);
           if(CollUtil.isNotEmpty(areaList)) {
               String addressId = order4ProjectParam.getAddressId();
               YxUserAddress userAddress = addressService.getById(addressId);
               String province = userAddress.getProvince();
               Boolean flag = false;
               String areaName = "";
               for(ProjectSalesArea area:areaList) {
                   areaName = areaName + "" + area.getAreaName();
                   if(province.equals(area.getAreaName())) {
                       flag = true;
                   }
               }

               if(!flag) {
                   return ApiResult.fail("仅支持（"+ areaName +"）配送，如需送至其他地区请选择与电话客服进行联系");
               }

           }

           YxStoreOrder order = storeOrderService.addOrder4Project(order4ProjectParam);



            if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单生成失败");
            String orderId = order.getOrderId();
            OrderExtendDTO orderDTO = new OrderExtendDTO();
            orderDTO.setOrderId(orderId);
            orderDTO.setPrice(order.getPayPrice().toString());

            map.put("status","SUCCESS");
            map.put("result",orderDTO);

            // 微信在线支付
           /* if(PayTypeEnum.WEIXIN.getValue().equals(order4ProjectParam.getPayType()) && StrUtil.isNotBlank(order4ProjectParam.getImagePath())) {
                    map.put("status","WECHAT_PAY");
                    Map<String,String> jsConfig = new HashMap<>();
                    WxPayMpOrderResult wxPayMpOrderResult = storeOrderService
                            .wxAppPay(orderId);
                    jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                    jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
                    jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                    jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                    jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                    jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("result",orderDTO);
            }*/

       }catch (ErrorRequestException e) {
           e.printStackTrace();
           return ApiResult.fail(e.getMessage());
       }catch (Exception e) {
           e.printStackTrace();
           return ApiResult.fail(e.getMessage());
       }

        return ApiResult.ok(map,"订单创建成功");
    }


    @PostMapping("/order/computed4Project")
    @ApiOperation(value = "计算订单金额",notes = "计算订单金额")
    public ApiResult<ComputeDTO> computedOrder4Project(@RequestBody Order4ProjectDto order4ProjectDto){

       int uid = SecurityUtils.getUserId().intValue();

        BigDecimal totalPrice = new BigDecimal(0);
        for(OrderDetail4Project detail : order4ProjectDto.getDrugList()) {
            LambdaQueryWrapper<Product4project> lambdaQueryWrapper1 = new LambdaQueryWrapper();
            lambdaQueryWrapper1.eq(Product4project::getProjectNo,order4ProjectDto.getProjectCode());
            lambdaQueryWrapper1.eq(Product4project::getStoreId,detail.getDrugStoreId());
            lambdaQueryWrapper1.eq(Product4project::getProductId,detail.getProductId());
            lambdaQueryWrapper1.eq(Product4project::getIsDel,0);
            Product4project product4project = product4projectService.getOne(lambdaQueryWrapper1,false);
            if(product4project!= null) {

                BigDecimal price = product4project.getUnitPrice();
                if(price == null) {
                    YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                    price = yxStoreProductAttrValue.getPrice();
                }

                totalPrice = NumberUtil.add(totalPrice , NumberUtil.mul(price , detail.getQty()));
            }
        }

        String projectCode = order4ProjectDto.getProjectCode();
        Integer addressId = order4ProjectDto.getAddressId();
        ComputeDTO computeDTO = storeOrderService.computedOrder4Project(uid,projectCode,totalPrice,String.valueOf(addressId),order4ProjectDto.getExpressTemplateId());

        return ApiResult.ok(computeDTO);
    }

    @PostMapping("/order/updateImage")
    @ApiOperation(value = "更新订单中的处方图片",notes = "更新订单中的处方图片")
    public ApiResult<Object> updateImage(@RequestBody OrderExternalParam orderExternalParam) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("image_path",orderExternalParam.getImagePath());
        updateWrapper.eq("order_id",orderExternalParam.getOrderNo());
        updateWrapper.set("status",0);
        Boolean flag = storeOrderService.update(updateWrapper);
        if(flag) {
            return ApiResult.ok();
        } else {
            return ApiResult.fail("更新处方图片出错");
        }

    }

    @AnonymousAccess
    @Log(value = "获取支付参数")
    @PostMapping("/payParam/query")
    @ApiOperation(value = "获取支付参数",notes = "获取支付参数")
    public ApiResult<Object> queryPayParam(@Valid @RequestBody ThirdPartyPayParam thirdPartyPayParam, HttpServletRequest request) throws Exception{
         log.info("获取支付参数:{}",JSONUtil.parseObj(thirdPartyPayParam));
        String encrypt = thirdPartyPayParam.getEncrypt();
        String orderNumber = "";

            try{
                orderNumber = EncryptionToolUtilAes.decrypt(encrypt, cipherKey);
            } catch (Exception e) {
                return ApiResult.fail("encrypt参数解析出错");
            }

            long systemTime = System.currentTimeMillis();
            long requestTime = Long.valueOf(orderNumber).longValue();
            long gap = systemTime - requestTime;
            if(gap >= 60000) {
                //      throw new BadRequestException("跳转链接已过期，请重新获取");
            }

        String openId = "";
        try{
            openId = EncryptionToolUtilAes.decrypt(thirdPartyPayParam.getOpenId(), cipherKey);
        } catch (Exception e) {
            return ApiResult.fail("openId参数解析出错");
        }

        String orderId = "";
        try{
            orderId = EncryptionToolUtilAes.decrypt(thirdPartyPayParam.getOrderId(), cipherKey);
        } catch (Exception e) {

            return ApiResult.fail("orderId参数解析出错");
        }

        log.info("解码结果：orderId:{},openId:{},encrypt:{}",orderId,openId,orderNumber);

        LambdaQueryWrapper<YxStoreOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YxStoreOrder::getOrderId,orderId);
        queryWrapper.select(YxStoreOrder::getOrderId,YxStoreOrder::getProjectCode, YxStoreOrder::getCardNumber, YxStoreOrder::getCardType);
        YxStoreOrder yxStoreOrder = storeOrderService.getOne(queryWrapper);

        if(yxStoreOrder == null) {
            return ApiResult.fail("订单没有找到");
        }

        WxPayMpOrderResult wxPayMpOrderResult = storeOrderService.wxAppPay4ThirdPartyOpenid(orderId, openId);
        String prepayId = "";
        String packageValue = wxPayMpOrderResult.getPackageValue();
        if(StrUtil.isNotBlank(packageValue)) {
            prepayId = packageValue.replace("prepay_id=","");
        }


        String redirectUrl = yiyao_url + "#/pages/wode/orderDetail?orderId="+orderId+"&type=home&projectCode="+yxStoreOrder.getProjectCode()+"&cardNumber="+yxStoreOrder.getCardNumber()+"&cardType="+yxStoreOrder.getCardType()+"&orderNumber="+ System.currentTimeMillis();
        log.info("获取支付参数返回回调url:{}",redirectUrl);
        HashMap map = new HashMap();
        map.put("prepay_id",prepayId);
        map.put("redirectUrl",redirectUrl);
        map.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
        map.put("nonceStr",wxPayMpOrderResult.getNonceStr());
        map.put("paySign",wxPayMpOrderResult.getPaySign());
        return ApiResult.ok(map);
    }

}

