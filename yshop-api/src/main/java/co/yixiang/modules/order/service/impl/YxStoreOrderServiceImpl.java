/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.api.ApiResult;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.*;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.activity.service.*;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.manage.service.YxExpressService;
import co.yixiang.modules.manage.web.dto.ChartDataDTO;
import co.yixiang.modules.manage.web.dto.OrderDataDTO;
import co.yixiang.modules.manage.web.dto.OrderTimeDataDTO;
import co.yixiang.modules.manage.web.param.OrderDeliveryParam;
import co.yixiang.modules.manage.web.param.OrderPriceParam;
import co.yixiang.modules.manage.web.param.OrderRefundParam;
import co.yixiang.modules.manage.web.vo.YxExpressQueryVo;
import co.yixiang.modules.monitor.service.RedisService;
import co.yixiang.modules.notify.NotifyService;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.order.mapping.OrderMap;
import co.yixiang.modules.order.service.UserAgreementService;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.service.YxStoreOrderStatusService;
import co.yixiang.modules.order.web.dto.*;
import co.yixiang.modules.order.web.param.*;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.shop.entity.*;
import co.yixiang.modules.shop.mapper.YxStoreCartMapper;
import co.yixiang.modules.shop.mapper.YxStoreCouponUserMapper;
import co.yixiang.modules.shop.mapper.YxSystemStoreMapper;
import co.yixiang.modules.shop.mapping.CartMap;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.YxStoreCouponUserService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.modules.taibao.entity.TbClaimInfo;
import co.yixiang.modules.taibao.service.TbClaimInfoService;
import co.yixiang.modules.taibao.web.vo.ClaimInfoVo;
import co.yixiang.modules.taiping.enums.TaipingOrderStatusEnum;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxUserBill;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.*;
import co.yixiang.modules.user.service.YxUserAddressService;
import co.yixiang.modules.user.service.YxUserBillService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserAddressQueryVo;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.user.web.vo.YxWechatUserQueryVo;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.modules.yiyaobao.entity.AddressDTO;
import co.yixiang.modules.yiyaobao.entity.PrescriptionDTO;
import co.yixiang.modules.yiyaobao.service.impl.OrderServiceImpl;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.modules.zhongan.ZhongAnParamDto;
import co.yixiang.mp.domain.DictDetail;
import co.yixiang.mp.rest.param.DictDetailQueryParam;
import co.yixiang.mp.service.DictDetailService;
import co.yixiang.mp.service.YxPayService;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.YxMiniPayService;

import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.mp.yiyaobao.service.mapper.OrdOrderMapper;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.domain.AlipayConfig;
import co.yixiang.tools.domain.AlipayConfiguration;
import co.yixiang.tools.domain.WechatConfiguration;
import co.yixiang.tools.domain.vo.TradeVo;
import co.yixiang.tools.service.AlipayConfigurationService;
import co.yixiang.tools.service.WechatConfigurationService;
import co.yixiang.tools.service.impl.SmsServiceImpl;
import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import co.yixiang.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.Product;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.yixiang.tools.service.AlipayConfigService;


import java.io.Serializable;
import java.math.BigDecimal;

import java.net.URL;
import java.net.URLEncoder;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2019-10-27
 */

@Slf4j
@Service
public class YxStoreOrderServiceImpl extends BaseServiceImpl<YxStoreOrderMapper, YxStoreOrder> implements YxStoreOrderService {

    @Autowired
    private YxStoreOrderMapper yxStoreOrderMapper;
    @Autowired
    private YxStoreCartMapper storeCartMapper;
    @Autowired
    private YxStoreCouponUserMapper yxStoreCouponUserMapper;

    @Autowired
    private YxSystemConfigService systemConfigService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private YxUserAddressService userAddressService;
    @Autowired
    private YxStoreOrderCartInfoService orderCartInfoService;
    @Autowired
    private YxStoreOrderStatusService orderStatusService;
    @Autowired
    private YxUserBillService billService;
    @Autowired
    private YxStoreProductReplyService storeProductReplyService;
    @Autowired
    private YxWechatUserService wechatUserService;
    @Autowired
    private YxStoreCouponUserService couponUserService;
    @Autowired
    private YxStoreSeckillService storeSeckillService;
    @Autowired
    private YxUserService userService;
    @Autowired
    private YxStoreProductService productService;
    @Autowired
    private YxStoreCombinationService combinationService;
    @Autowired
    private YxStorePinkService pinkService;
    @Autowired
    private YxStoreBargainUserService storeBargainUserService;
    @Autowired
    private YxStoreBargainService storeBargainService;
    @Autowired
    private YxExpressService expressService;
    @Autowired
    private AlipayConfigService alipayService;
    @Autowired
    private YxSystemStoreService systemStoreService;

    @Autowired
    private OrderMap orderMap;

    @Autowired
    @Lazy
    private OrderServiceImpl yiyaobaoOrderService;
    //@Autowired
    //private MqProducer mqProducer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private YxPayService payService;
    @Autowired
    private YxMiniPayService miniPayService;
    @Autowired
    private YxTemplateService templateService;
    @Autowired
    private YxUserLevelService userLevelService;

    @Autowired
    private YxExpressTemplateService yxExpressTemplateService;

    @Autowired
    private YxExpressTemplateDetailService yxExpressTemplateDetailService;


    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Value("${yiyaobao.apiUrl}")
    private String yiyaobao_apiUrl;

    @Value("${yiyaobao.projectNo}")
    private String yiyaobao_projectNo;

    @Value("${file.localUrl}")
    private String localUrl;


    @Autowired
    private MqProducer mqProducer;

    // 业务队列绑定业务交换机的routeKey
    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    // 业务队列绑定业务交换机的routeKey
    @Value("${zhonganpuyao.delayQueueName}")
    private String bizRoutekeyZhonganpuyao;

    @Value("${yiyaobao.delayQueueName}")
    private String bizRoutekeyYiyaobao;

    @Autowired
    private YxSystemStoreMapper yxSystemStoreMapper;


    @Autowired
    private UserAgreementService userAgreementService;

    @Autowired
    @Lazy
    private InternetHospitalDemandService internetHospitalDemandService;

    @Autowired
    private MdPharmacistServiceService mdPharmacistService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private DictDetailService dictDetailService;


    @Autowired
    @Lazy
    private XkProcessService xkProcessService;


    @Autowired
    private TaipingCardService taipingCardService;

/*    @Autowired
    private SendPrsProducer sendPrsProducer;

    @Autowired
    private SendPrsRefundProducer sendPrsRefundProducer;*/

    @Autowired
    @Lazy
    private YxStoreCartService storeCartService;

    @Autowired
    private CartMap cartMap;

    @Autowired
    private YxStoreProductAttrValueService yxStoreProductAttrValueService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSalesAreaService projectSalesAreaService;

    @Autowired
    private TbClaimInfoService tbClaimInfoService;


    @Value("${yiyao.onlineFlag}")
    private Boolean onlineFlag;

    @Value("${yiyaobao.apiUrlExternal}")
    private String yiyaobao_apiUrl_external;

    @Value("${yiyaobao.cancelOrder}")
    private String cancelOrder;

    @Value("${yiyaobao.appId}")
    private String appId;

    @Value("${yiyaobao.appSecret}")
    private String appSecret;

    @Autowired
    private RocheStoreService rocheStoreService;

    @Autowired
    private AlipayConfigurationService alipayConfigurationService;

    @Autowired
    private WechatConfigurationService wechatConfigurationService;
    /**
     * 订单退款
     * @param param
     */
    @Override
    public void orderRefund(OrderRefundParam param) {

        YxStoreOrderQueryVo orderQueryVo = getOrderInfo(param.getOrderId(),0);
        if(ObjectUtil.isNull(orderQueryVo)) {
            throw new ErrorRequestException("订单不存在");
        }

        YxUserQueryVo userQueryVo = userService.getYxUserById(orderQueryVo.getUid());
        if(ObjectUtil.isNull(userQueryVo)) {
            throw new ErrorRequestException("用户不存在");
        }

        if(param.getPrice() > orderQueryVo.getPayPrice().doubleValue()) {
            throw new ErrorRequestException("退款金额不正确");
        }

        YxStoreOrder storeOrder = new YxStoreOrder();
        //修改状态
        storeOrder.setId(orderQueryVo.getId());

        if(param.getType() == 2){
            storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
            yxStoreOrderMapper.updateById(storeOrder);
            return;
        }

        //根据支付类型不同退款不同
        if(orderQueryVo.getPayType().equals("yue")){
            storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_2.getValue());
            storeOrder.setRefundPrice(BigDecimal.valueOf(param.getPrice()));
            yxStoreOrderMapper.updateById(storeOrder);
            //退款到余额
            userService.incMoney(orderQueryVo.getUid(),param.getPrice());

            //增加流水
            YxUserBill userBill = new YxUserBill();
            userBill.setUid(orderQueryVo.getUid());
            userBill.setLinkId(orderQueryVo.getId().toString());
            userBill.setPm(BillEnum.PM_1.getValue());
            userBill.setTitle("商品退款");
            userBill.setCategory("now_money");
            userBill.setType("pay_product_refund");
            userBill.setNumber(BigDecimal.valueOf(param.getPrice()));
            userBill.setBalance(NumberUtil.add(param.getPrice(),userQueryVo.getNowMoney()));
            userBill.setMark("订单退款到余额");
            userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
            userBill.setStatus(BillEnum.STATUS_1.getValue());
            billService.save(userBill);


            orderStatusService.create(orderQueryVo.getId(),"order_edit","退款给用户："+param.getPrice() +"元");
        }else{
            BigDecimal bigDecimal = new BigDecimal("100");
            String mchName="";
            Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderQueryVo.getProjectCode()),false);
            if(product!=null && StringUtils.isNotEmpty(product.getMchName())){
                mchName=product.getMchName();
            }else{
                YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderQueryVo.getStoreId());
                if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getMchName())){
                    mchName=yxSystemStoreQueryVo.getMchName();
                }
            }
            try {
                if(OrderInfoEnum.PAY_CHANNEL_1.getValue().equals(orderQueryVo.getIsChannel())){
                    miniPayService.refundOrder(param.getOrderId(),
                            bigDecimal.multiply(orderQueryVo.getPayPrice()).intValue(),mchName);
                }else{
                    payService.refundOrder(param.getOrderId(),
                            bigDecimal.multiply(orderQueryVo.getPayPrice()).intValue());
                }

            } catch (WxPayException e) {
                log.info("refund-error:{}",e.getMessage());
            }
        }

        //模板消息通知
        YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(orderQueryVo.getUid());
        if(ObjectUtil.isNotNull(wechatUser)){
            //公众号与小程序打通统一公众号模板通知
            if(StrUtil.isNotBlank(wechatUser.getOpenid())){
                templateService.refundSuccessNotice(orderQueryVo.getOrderId(),
                        orderQueryVo.getPayPrice().toString(),wechatUser.getOpenid(),
                        OrderUtil.stampToDate(orderQueryVo.getAddTime().toString()));
            }

        }


    }

    /**
     * 订单发货
     * @param param
     */
    @Override
    public void orderDelivery(OrderDeliveryParam param) {
        YxStoreOrderQueryVo orderQueryVo = getOrderInfo(param.getOrderId(),0);
        if(ObjectUtil.isNull(orderQueryVo)) {
            throw new ErrorRequestException("订单不存在");
        }

        if(!orderQueryVo.getStatus().equals(OrderInfoEnum.STATUS_0.getValue()) ||
                orderQueryVo.getPaid().equals(OrderInfoEnum.PAY_STATUS_0.getValue())){
            throw new ErrorRequestException("订单状态错误");
        }

        YxExpressQueryVo expressQueryVo = expressService
                .getYxExpressById(Integer.valueOf(param.getDeliveryName()));
        if(ObjectUtil.isNull(expressQueryVo)) {
            throw new ErrorRequestException("请后台先添加快递公司");
        }

        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setId(orderQueryVo.getId());
        storeOrder.setStatus(OrderInfoEnum.STATUS_1.getValue());
        storeOrder.setDeliveryId(param.getDeliveryId());
        storeOrder.setDeliveryName(expressQueryVo.getName());
        storeOrder.setDeliveryType(param.getDeliveryType());
        storeOrder.setDeliverySn(expressQueryVo.getCode());

        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(orderQueryVo.getId(),"delivery_goods",
                "已发货 快递公司："+expressQueryVo.getName()+"快递单号：" +param.getDeliveryId());

        //模板消息通知
        YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(orderQueryVo.getUid());
        if(ObjectUtil.isNotNull(wechatUser)){
            ////公众号与小程序打通统一公众号模板通知
            if(StrUtil.isNotBlank(wechatUser.getOpenid())){
                templateService.deliverySuccessNotice(storeOrder.getOrderId(),
                        expressQueryVo.getName(),param.getDeliveryId(),wechatUser.getOpenid());
            }

        }

        //加入redis，7天后自动确认收货
        String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM,orderQueryVo.getId()));
        redisTemplate.opsForValue().set(redisKey, orderQueryVo.getOrderId(),
                ShopConstants.ORDER_OUTTIME_UNCONFIRM, TimeUnit.DAYS);

    }

    /**
     * 修改订单价格
     * @param param
     */
    @Override
    public void editOrderPrice(OrderPriceParam param) {
        YxStoreOrderQueryVo orderQueryVo = getOrderInfo(param.getOrderId(),0);
        if(ObjectUtil.isNull(orderQueryVo)){
            throw new ErrorRequestException("订单不存在");
        }

        if(orderQueryVo.getPayPrice().doubleValue() == param.getPrice()){
            return;
        }

        if(orderQueryVo.getPaid() > 0) {
            throw new ErrorRequestException("订单状态错误");
        }


        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setId(orderQueryVo.getId());
        storeOrder.setPayPrice(BigDecimal.valueOf(param.getPrice()));

        //判断金额是否有变动,生成一个额外订单号去支付

        int res = NumberUtil.compare(orderQueryVo.getPayPrice().doubleValue(),param.getPrice());
        if(res != 0){
            String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
            storeOrder.setExtendOrderId(orderSn);
        }


        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"order_edit","修改实际支付金额");


    }

    /**
     * 获取拼团订单
     * @param pid
     * @param uid
     * @param type
     * @return
     */
    @Override
    public YxStoreOrder getOrderPink(int pid, int uid,int type) {
        QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).eq("uid",uid).eq("pink_id",pid);
        if(type == 0) {
            wrapper.eq("refund_status",0);
        }
        return yxStoreOrderMapper.selectOne(wrapper);
    }

    /**
     * 退回优惠券
     * @param order
     */
    @Override
    public void regressionCoupon(YxStoreOrderQueryVo order) {
        if(order.getPaid() > 0 || order.getStatus() == -2 || order.getIsDel() ==1){
            return;
        }

        QueryWrapper<YxStoreCouponUser> wrapper= new QueryWrapper<>();
        if(order.getCouponId() > 0){
            wrapper.eq("id",order.getCouponId()).eq("status",1).eq("uid",order.getUid());
            YxStoreCouponUser couponUser = yxStoreCouponUserMapper.selectOne(wrapper);

            if(ObjectUtil.isNotNull(couponUser)){
                YxStoreCouponUser storeCouponUser = new YxStoreCouponUser();
                QueryWrapper<YxStoreCouponUser> wrapperT= new QueryWrapper<>();
                wrapperT.eq("id",order.getCouponId()).eq("uid",order.getUid());
                storeCouponUser.setStatus(0);
                storeCouponUser.setUseTime(0);
                yxStoreCouponUserMapper.update(storeCouponUser,wrapperT);
            }
        }

    }

    /**
     * 退回库存
     * @param order
     */
    @Override
    public void regressionStock(YxStoreOrderQueryVo order) {
        if(order.getPaid() > 0 || order.getStatus() == -2 || order.getIsDel() ==1){
            return;
        }
        QueryWrapper<YxStoreOrderCartInfo> wrapper= new QueryWrapper<>();
        wrapper.in("cart_id", Arrays.asList(order.getCartId().split(",")));

        List<YxStoreOrderCartInfo> cartInfoList =  orderCartInfoService.list(wrapper);
        for (YxStoreOrderCartInfo cartInfo : cartInfoList) {
            YxStoreCartQueryVo cart = JSONObject.parseObject(cartInfo.getCartInfo()
                    ,YxStoreCartQueryVo.class);
            if(order.getCombinationId() > 0){//拼团
                combinationService.incStockDecSales(cart.getCartNum(),order.getCombinationId());
            }else if(order.getSeckillId() > 0){//秒杀
                storeSeckillService.incStockDecSales(cart.getCartNum(),order.getSeckillId());
            }else if(order.getBargainId() > 0){//砍价
                storeBargainService.incStockDecSales(cart.getCartNum(),order.getBargainId());
            }else{
                productService.incProductStock(cart.getCartNum(),cart.getProductId()
                        ,cart.getProductAttrUnique());
            }

        }
    }

    /**
     * 退回积分
     * @param order
     */
    @Override
    public void regressionIntegral(YxStoreOrderQueryVo order) {
        if(order.getPaid() > 0 || order.getStatus() == -2 || order.getIsDel() ==1){
            return;
        }
        if(order.getUseIntegral().doubleValue() <= 0) {
            return;
        }

        if(order.getStatus() != -2 && order.getRefundStatus() != 2
                && ObjectUtil.isNotNull(order.getBackIntegral())
                && order.getBackIntegral().doubleValue() >= order.getUseIntegral().doubleValue()){
            return;
        }

        YxUserQueryVo userQueryVo = userService
                .getYxUserById(order.getUid());

        //增加积分
        userService.incIntegral(order.getUid(),order.getUseIntegral().doubleValue());

        //增加流水
        YxUserBill userBill = new YxUserBill();
        userBill.setUid(order.getUid());
        userBill.setTitle("积分回退");
        userBill.setLinkId(order.getId().toString());
        userBill.setCategory("integral");
        userBill.setType("deduction");
        userBill.setNumber(order.getUseIntegral());
        userBill.setBalance(userQueryVo.getIntegral());
        userBill.setMark("购买商品失败,回退积分");
        userBill.setStatus(BillEnum.STATUS_1.getValue());
        userBill.setPm(BillEnum.PM_1.getValue());
        userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
        billService.save(userBill);

        //更新回退积分
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setBackIntegral(order.getUseIntegral());
        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);
    }

    /**
     * 未付款取消订单
     * @param orderId
     * @param uid
     */
    @Override
    public void cancelOrder(String orderId, int uid) {
        YxStoreOrderQueryVo order = getOrderInfo(orderId,uid);
        if(ObjectUtil.isNull(order)) {
            throw new ErrorRequestException("订单不存在");
        }
      //  if(order.getIsDel().equals(OrderInfoEnum.CANCEL_STATUS_1.getValue()))throw new ErrorRequestException("订单已取消");
        if(order.getStatus().equals(OrderStatusEnum.STATUS_8.getValue())  || order.getStatus().equals(OrderStatusEnum.STATUS_12.getValue())  ) {
            throw new ErrorRequestException("订单已取消");
        }
        regressionIntegral(order);

        regressionStock(order);

        regressionCoupon(order);

        YxStoreOrder storeOrder = new YxStoreOrder();
        //storeOrder.setIsDel(OrderInfoEnum.CANCEL_STATUS_1.getValue());
        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(order.getProjectCode())) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_12.getValue());
        } else {
            storeOrder.setStatus(OrderStatusEnum.STATUS_8.getValue());
        }

        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);


        YxSystemStoreQueryVo storeQueryVo = systemStoreService.getYxSystemStoreById(order.getStoreId());
        // 更新益药宝订单/处方状态
        if(storeQueryVo!=null){
//            yiyaobaoOrderService.cancelYiyaobaoOrder(orderId);
            OrderVo orderVo= yiyaobaoOrderService.getYiyaobaoOrderbyOrderId(orderId);
            if(orderVo==null){
                return;
            }
            String type="0";
            if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(storeQueryVo.getName())){
                type="1";
            }

            String url = yiyaobao_apiUrl_external+cancelOrder;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("voucher", orderId);
            jsonObject.put("type", type);
            String requestBody = jsonObject.toString(); //

            try {
                long timestamp = System.currentTimeMillis(); // 生成签名时间戳
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("ACCESS_APPID", appId); // 设置APP
                headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
                String ACCESS_SIGANATURE = AppSiganatureUtils
                        .createSiganature(requestBody, appId, appSecret,
                                timestamp);
                headers.put("ACCESS_SIGANATURE", ACCESS_SIGANATURE); // 生成并设置签名
                log.info("ACCESS_APPID={}", appId);
                log.info("ACCESS_TIMESTAMP={}", String.valueOf(timestamp));
                log.info("ACCESS_SIGANATURE={}", ACCESS_SIGANATURE);
                log.info("url={}", url);
                log.info("requestBody={}", requestBody);
                String result = null; // 发起调用
                try {
                    result = HttpUtils.postJsonHttps(url, requestBody, headers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("取消订单下发益药宝，结果：{}", result);
                if(StringUtils.isEmpty(result)){
                    throw new BadRequestException("取消订单下发失败。");
                }
                cn.hutool.json.JSONObject object=JSONUtil.parseObj(result);
                if(object.get("code")==null){
                    throw new BadRequestException("取消订单下发失败。");
                }
                if(object.get("code").equals("1")){
                    throw new BadRequestException("取消订单下发失败,订单号为空");
                }
                if(object.get("code").equals("2")){
                    throw new BadRequestException("取消订单下发失败,未查询到订单");
                }
                if(object.get("code").equals("3")){
                    throw new BadRequestException("取消订单下发失败,订单不可取消");
                }
                if(object.get("code").equals("5")){
                    throw new BadRequestException("取消订单下发失败,EBS订单取消失败");
                }
                if(object.get("code").equals("90")){
                    throw new BadRequestException("取消订单下发失败,其他未知错误");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 系统自动主动取消未付款取消订单
     * @param orderId
     */
    @Override
    public void cancelOrderByTask(int orderId) {
        YxStoreOrderQueryVo order = null;
        try {
            order = getYxStoreOrderById(orderId);

            if(ObjectUtil.isNull(order)) {
                throw new ErrorRequestException("订单不存在");
            }

            if(order.getIsDel().equals(OrderInfoEnum.CANCEL_STATUS_1.getValue())) {
                throw new ErrorRequestException("订单已取消");
            }

            regressionIntegral(order);

            regressionStock(order);

            regressionCoupon(order);

            YxStoreOrder storeOrder = new YxStoreOrder();
            storeOrder.setIsDel(OrderInfoEnum.CANCEL_STATUS_1.getValue());
            storeOrder.setId(order.getId());
            yxStoreOrderMapper.updateById(storeOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 奖励积分
     * @param order
     */
    @Override
    public void gainUserIntegral(YxStoreOrderQueryVo order) {
        if(order.getGainIntegral().intValue() > 0){
            YxUserQueryVo userQueryVo = userService
                    .getYxUserById(order.getUid());

            YxUser user = new YxUser();

            user.setIntegral(NumberUtil.add(userQueryVo.getIntegral(),
                    order.getGainIntegral()));
            user.setUid(order.getUid());
            userService.updateById(user);

            YxUserBill userBill = new YxUserBill();
            userBill.setUid(order.getUid());
            userBill.setTitle("购买商品赠送积分");
            userBill.setLinkId(order.getId().toString());
            userBill.setCategory("integral");
            userBill.setType("gain");
            userBill.setNumber(order.getGainIntegral());
            userBill.setBalance(userQueryVo.getIntegral());
            userBill.setMark("购买商品赠送");
            userBill.setStatus(BillEnum.STATUS_1.getValue());
            userBill.setPm(BillEnum.PM_1.getValue());
            userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
            billService.save(userBill);

        }
    }


    /**
     * 删除订单
     * @param orderId
     * @param uid
     */
    @Override
    public void removeOrder(String orderId, int uid) {
        YxStoreOrderQueryVo order = getOrderInfo(orderId,uid);
        if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单不存在");
        order = handleOrder(order);
        if(!order.get_status().get_type().equals("0") &&
                !order.get_status().get_type().equals("-2") &&
                !order.get_status().get_type().equals("4")) {
            throw new ErrorRequestException("该订单无法删除");
        }

        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setIsDel(OrderInfoEnum.CANCEL_STATUS_1.getValue());
        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(),"remove_order","删除订单");
    }

    /**
     * 订单确认收货
     * @param orderId
     * @param uid
     */
    @Override
    public void takeOrder(String orderId, int uid) {
        YxStoreOrderQueryVo order = getOrderInfo(orderId,uid);
        if(ObjectUtil.isNull(order)){
            throw new ErrorRequestException("订单不存在");
        }
        order = handleOrder4Store(order);
       // if(!order.get_status().get_type().equals("2")) throw new ErrorRequestException("订单状态错误");

        YxStoreOrder storeOrder = new YxStoreOrder();
       // storeOrder.setStatus(OrderInfoEnum.STATUS_2.getValue());
        storeOrder.setStatus(OrderInfoEnum.STATUS_3.getValue());
        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(),"user_take_delivery","用户已收货");

        //奖励积分
        gainUserIntegral(order);

        //分销计算
        userService.backOrderBrokerage(order);

        //检查是否符合会员升级条件
        userLevelService.setLevelComplete(uid);

        //更新益药宝订单的状态为 关闭 50
        yiyaobaoOrderService.takeOrder(orderId);

    }

    /**
     * 核销订单
     * @param orderId
     */
    @Override
    public void verificOrder(String orderId) {
        YxStoreOrderQueryVo order = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(order)) throw new ErrorRequestException("订单不存在");

        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setStatus(OrderInfoEnum.STATUS_2.getValue());
        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(),"user_take_delivery","已核销");

        //奖励积分
        gainUserIntegral(order);

        //分销计算
        userService.backOrderBrokerage(order);

        //检查是否符合会员升级条件
        userLevelService.setLevelComplete(order.getUid());
    }

    /**
     * 申请退款
     * @param param
     * @param uid
     */
    @Override
    public void orderApplyRefund(RefundParam param, int uid) {
        YxStoreOrderQueryVo order = getOrderInfo(param.getUni(),uid);
        if(ObjectUtil.isNull(order)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(order.getRefundStatus() == 2){
            throw new ErrorRequestException("订单已退款");
        }
        if(order.getRefundStatus() == 1){
            throw new ErrorRequestException("正在申请退款中");
        }
        if(order.getStatus() == 1 ) {
            throw new ErrorRequestException("订单当前无法退款");
        }

        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_1.getValue());
        storeOrder.setRefundReasonTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setRefundReasonWapExplain(param.getRefund_reason_wap_explain());
        storeOrder.setRefundReasonWapImg(param.getRefund_reason_wap_img());
        storeOrder.setRefundReasonWap(param.getText());
        storeOrder.setId(order.getId());
        yxStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(),"apply_refund","用户申请退款，原因："+param.getText());

        YxSystemStoreQueryVo storeQueryVo = systemStoreService.getYxSystemStoreById(order.getStoreId());
        if(StringUtils.isNotEmpty(storeQueryVo.getLinkPhone())){
           List<String> phoneList= Arrays.asList(storeQueryVo.getLinkPhone().split(",")) ;
            //发送短信
            for(String phone : phoneList) {
                String remindmessage = "【益药】您有订单待处理，订单状态：%s。订单编号：%s";
                remindmessage = String.format(remindmessage, "申请退款", order.getOrderId());
                smsService.sendTeddy("",remindmessage,phone);
            }
        }
    }

    /**
     * 订单列表
     * @param uid
     * @param type
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<YxStoreOrderQueryVo> orderList(int uid, int type, int page, int limit,YxStoreOrderQueryParam queryParam) {
        QueryWrapper<YxStoreOrder> wrapper= new QueryWrapper<>();
        if(uid > 0) {
            wrapper.eq("uid",uid);
        }
        wrapper.eq("is_del",0).orderByDesc("add_time");
       // wrapper.orderByDesc("add_time");
        switch (OrderStatusEnum.toType(type)){
            case STATUS_5://待审核
                wrapper.eq("refund_status",0).eq("status",5);
                break;
            case STATUS_0://待付款
                wrapper.eq("paid",0).eq("refund_status",0).eq("status",0);
                break;
            case STATUS_1://待发货
                wrapper.eq("paid",1).eq("refund_status",0).eq("status",0);
                break;
            case STATUS_2://待收货
                wrapper.eq("paid",1).eq("refund_status",0).eq("status",1);
                break;
            case STATUS_3://待评价
                wrapper.eq("paid",1).eq("refund_status",0).eq("status",2);
                break;
            case STATUS_4://已完成
                wrapper.eq("paid",1).eq("refund_status",0).eq("status",3);
                break;
            case STATUS_6://审核未通过/药店取消订单
                wrapper.eq("paid",0).eq("refund_status",0).in("status",6,7);
                break;
            case STATUS_MINUS_1://退款中
                wrapper.eq("paid",1).eq("refund_status",1);
                break;
            case STATUS_MINUS_2://已退款
                wrapper.eq("paid",0).eq("refund_status",2);
                break;
            case STATUS_MINUS_3://退款
                String[] strs = {"1","2"};
                wrapper.eq("paid",1).in("refund_status",Arrays.asList(strs));
                break;
            case STATUS_99://全部订单
                break;
        }
        if(StrUtil.isNotBlank(queryParam.getKeyword())) {
            wrapper.apply("EXISTS(SELECT 1 FROM yx_store_cart ysc,yx_store_product ysp WHERE ysc.product_id = ysp.id and FIND_IN_SET(ysc.id,yx_store_order.cart_id) AND (ysp.store_name LIKE  concat( '%',{0},'%') OR ysp.common_name LIKE concat( '%',{1},'%') ) )",queryParam.getKeyword(),queryParam.getKeyword());
        }

        Page<YxStoreOrder> pageModel = new Page<>(page, limit);

        IPage<YxStoreOrder> pageList = yxStoreOrderMapper.selectPage(pageModel,wrapper);
        List<YxStoreOrderQueryVo> list = orderMap.toDto(pageList.getRecords());
        List<YxStoreOrderQueryVo> newList = new ArrayList<>();
        for (YxStoreOrderQueryVo order : list) {
            YxStoreOrderQueryVo orderQueryVo = handleOrder4Store(order);

            newList.add(orderQueryVo);
        }

        return newList;
    }

    /**
     * chart图标统计
     * @param cate
     * @param type
     * @return
     */
    @Override
    public Map<String,Object> chartCount(int cate,int type) {
        int today = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(new Date()));
        int yesterday = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.
                yesterday()));
        int lastWeek = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.lastWeek()));
        int nowMonth = OrderUtil.dateToTimestampT(DateUtil
                .beginOfMonth(new Date()));
        double price = 0d;
        List<ChartDataDTO> list = null;
        QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("paid",1).eq("refund_status",0).eq("is_del",0);

        switch (OrderCountEnum.toType(cate)){
            case TODAY: //今天
                wrapper.ge("pay_time",today);
                break;
            case YESTERDAY: //昨天
                wrapper.lt("pay_time",today).ge("pay_time",yesterday);
                break;
            case WEEK: //上周
                wrapper.ge("pay_time",lastWeek);
                break;
            case MONTH: //本月
                wrapper.ge("pay_time",nowMonth);
                break;
        }
        if(type == 1){
            list = yxStoreOrderMapper.chartList(wrapper);
            price = yxStoreOrderMapper.todayPrice(wrapper);
        }else{
            list = yxStoreOrderMapper.chartListT(wrapper);
            price = yxStoreOrderMapper.selectCount(wrapper).doubleValue();
        }

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("chart",list);
        map.put("time",price);
        return map;
    }

    /**
     * 获取 今日 昨日 本月 订单金额
     * @return
     */
    @Override
    public OrderTimeDataDTO getOrderTimeData() {

        int today = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(new Date()));
        int yesterday = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.
                yesterday()));
        int nowMonth = OrderUtil.dateToTimestampT(DateUtil
                .beginOfMonth(new Date()));
        OrderTimeDataDTO orderTimeDataDTO = new OrderTimeDataDTO();

        //今日成交额
        QueryWrapper<YxStoreOrder> wrapperOne = new QueryWrapper<>();
        wrapperOne.ge("pay_time",today).eq("paid",1)
                .eq("refund_status",0).eq("is_del",0);
        orderTimeDataDTO.setTodayPrice(yxStoreOrderMapper.todayPrice(wrapperOne));
        //今日订单数
        orderTimeDataDTO.setTodayCount(yxStoreOrderMapper.selectCount(wrapperOne));

        //昨日成交额
        QueryWrapper<YxStoreOrder> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.lt("pay_time",today).ge("pay_time",yesterday).eq("paid",1)
                .eq("refund_status",0).eq("is_del",0);
        orderTimeDataDTO.setProPrice(yxStoreOrderMapper.todayPrice(wrapperTwo));
        //昨日订单数
        orderTimeDataDTO.setProCount(yxStoreOrderMapper.selectCount(wrapperTwo));

        //本月成交额
        QueryWrapper<YxStoreOrder> wrapperThree = new QueryWrapper<>();
        wrapperThree.ge("pay_time",nowMonth).eq("paid",1)
                .eq("refund_status",0).eq("is_del",0);
        orderTimeDataDTO.setMonthPrice(yxStoreOrderMapper.todayPrice(wrapperThree));
        //本月订单数
        orderTimeDataDTO.setMonthCount(yxStoreOrderMapper.selectCount(wrapperThree));


        return orderTimeDataDTO;
    }

    /**
     * 订单每月统计数据
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<OrderDataDTO> getOrderDataPriceCount(int page, int limit) {
        Page<YxStoreOrder> pageModel = new Page<>(page, limit);
        return yxStoreOrderMapper.getOrderDataPriceList(pageModel);
    }

    /**
     * 获取某个用户的订单统计数据
     * @param uid uid>0 取用户 否则取所有
     * @return
     */
    @Override
    public OrderCountDTO orderData(int uid) {

        OrderCountDTO countDTO = new OrderCountDTO();
        //订单支付没有退款 数量
        QueryWrapper<YxStoreOrder> wrapperOne = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperOne.eq("uid",uid);
        }
        wrapperOne.eq("is_del",0).eq("paid",1).eq("refund_status",0);
        countDTO.setOrderCount(yxStoreOrderMapper.selectCount(wrapperOne));

        //订单支付没有退款 支付总金额
        countDTO.setSumPrice(yxStoreOrderMapper.sumPrice(uid));

        //订单待支付 数量
        QueryWrapper<YxStoreOrder> wrapperTwo = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperTwo.eq("uid",uid);
        }
        wrapperTwo.eq("is_del",0).eq("paid",0)
                .eq("refund_status",0).eq("status",0);
        countDTO.setUnpaidCount(yxStoreOrderMapper.selectCount(wrapperTwo));

        //订单待审核 数量
        QueryWrapper<YxStoreOrder> wrapperCheck = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperCheck.eq("uid",uid);
        }
        wrapperCheck.eq("is_del",0).eq("paid",0)
                .eq("refund_status",0).eq("status",5);
        countDTO.setCheckCount(yxStoreOrderMapper.selectCount(wrapperCheck));

        //订单审核不通过 药店取消订单 数量
        QueryWrapper<YxStoreOrder> wrapperCheckFail = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperCheckFail.eq("uid",uid);
        }
        wrapperCheckFail.eq("is_del",0).eq("paid",0)
                .eq("refund_status",0).in("status",6,7);
        countDTO.setCheckFailCount(yxStoreOrderMapper.selectCount(wrapperCheckFail));

        //订单待发货 数量
        QueryWrapper<YxStoreOrder> wrapperThree = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperThree.eq("uid",uid);
        }
        wrapperThree.eq("is_del",0).eq("paid",1)
                .eq("refund_status",0).eq("status",0);
        countDTO.setUnshippedCount(yxStoreOrderMapper.selectCount(wrapperThree));

        //订单待收货 数量
        QueryWrapper<YxStoreOrder> wrapperFour = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperFour.eq("uid",uid);
        }
        wrapperFour.eq("is_del",0).eq("paid",1)
                .eq("refund_status",0).eq("status",1);
        countDTO.setReceivedCount(yxStoreOrderMapper.selectCount(wrapperFour));

        //订单待评价 数量
        QueryWrapper<YxStoreOrder> wrapperFive = new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperFive.eq("uid",uid);
        }
        wrapperFive.eq("is_del",0).eq("paid",1)
                .eq("refund_status",0).eq("status",2);
        countDTO.setEvaluatedCount(yxStoreOrderMapper.selectCount(wrapperFive));

        //订单已完成 数量
        QueryWrapper<YxStoreOrder> wrapperSix= new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperSix.eq("uid",uid);
        }
        wrapperSix.eq("is_del",0).eq("paid",1)
                .eq("refund_status",0).eq("status",3);
        countDTO.setCompleteCount(yxStoreOrderMapper.selectCount(wrapperSix));

        //订单退款
        QueryWrapper<YxStoreOrder> wrapperSeven= new QueryWrapper<>();
        if(uid > 0 ) {
            wrapperSeven.eq("uid",uid);
        }
        String[] strArr = {"1","2"};
        wrapperSeven.eq("is_del",0).eq("paid",1)
                .in("refund_status",Arrays.asList(strArr));
        countDTO.setRefundCount(yxStoreOrderMapper.selectCount(wrapperSeven));


        return countDTO;
    }

    /**
     * 处理订单返回的状态
     * @param order order
     * @return
     */
    @Override
    public YxStoreOrderQueryVo handleOrder(YxStoreOrderQueryVo order) {
        QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("oid",order.getId());
        List<YxStoreOrderCartInfo> cartInfos = orderCartInfoService.list(wrapper);

        List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
        for (YxStoreOrderCartInfo info : cartInfos) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
            cartQueryVo.setUnique(info.getUnique());
            //新增是否评价字段
            cartQueryVo.setIsReply(storeProductReplyService.replyCount(info.getUnique()));
            cartInfo.add(cartQueryVo);
        }
        order.setCartInfo(cartInfo);
        StatusDTO statusDTO = new StatusDTO();
        if(order.getPaid() == 0){
            //计算未支付到自动取消订 时间
            long time = ShopConstants.ORDER_OUTTIME_UNPAY *60 + Long.valueOf(order.getAddTime());
            statusDTO.set_class("nobuy");
            statusDTO.set_msg(StrUtil.format("请在{}前完成支付",OrderUtil.stampToDate(String.valueOf(time))));
            statusDTO.set_type("0");
            statusDTO.set_title("未支付");
        }else if(order.getRefundStatus() == 1){
            statusDTO.set_class("state-sqtk");
            statusDTO.set_msg("审核中,请耐心等待");
            statusDTO.set_type("-1");
            statusDTO.set_title("申请退款中");
        }else if(order.getRefundStatus() == 2){
            statusDTO.set_class("state-sqtk");
            statusDTO.set_msg("已为您退款,感谢您的支持");
            statusDTO.set_type("-2");
            statusDTO.set_title("已退款");
        }else if(order.getStatus() == 0){
            // 拼团
            if(order.getPinkId() > 0){
                if(pinkService.pinkIngCount(order.getPinkId()) > 0){
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待其他人参加拼团");
                    statusDTO.set_type("1");
                    statusDTO.set_title("拼团中");
                }else{
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待发货,请耐心等待");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待发货");
                }
            }else{
                if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(order.getShippingType())){
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待发货,请耐心等待");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待发货");
                }else{
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待核销,请到核销点进行核销");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待核销");
                }
            }

        }else if(order.getStatus() == 1){
            statusDTO.set_class("state-ysh");
            statusDTO.set_msg("已发货");
            statusDTO.set_type("2");
            statusDTO.set_title("待收货");
        }else if(order.getStatus() == 2){
            statusDTO.set_class("state-ypj");
            statusDTO.set_msg("已收货,快去评价一下吧");
            statusDTO.set_type("3");
            statusDTO.set_title("待评价");
        }else if(order.getStatus() == 3){
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("交易完成,感谢您的支持");
            statusDTO.set_type("4");
            statusDTO.set_title("交易完成");
        }

        if(order.getPayType().equals("weixin")){
            statusDTO.set_payType("微信支付");
        }else{
            statusDTO.set_payType("余额支付");
        }

        order.set_status(statusDTO);


        return order;
    }

    /**
     * 处理订单返回的状态-多门店 -益药宝
     * @param order order
     * @return
     */
    @Override
    public YxStoreOrderQueryVo handleOrder4Store(YxStoreOrderQueryVo order) {

        QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("oid",order.getId());
        List<YxStoreOrderCartInfo> cartInfos = orderCartInfoService.list(wrapper);

        List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
        for (YxStoreOrderCartInfo info : cartInfos) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
            cartQueryVo.setUnique(info.getUnique());
            //新增是否评价字段
            cartQueryVo.setIsReply(storeProductReplyService.replyCount(info.getUnique()));
            cartInfo.add(cartQueryVo);
        }
        order.setCartInfo(cartInfo);

        YxSystemStoreQueryVo yxSystemStoreQueryVo = systemStoreService.getYxSystemStoreById(order.getStoreId());
        order.setSystemStore(yxSystemStoreQueryVo);
        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(order.getProjectCode())){
            RocheStore rocheStore= rocheStoreService.getById(order.getStoreId());
            if(rocheStore!=null){
                yxSystemStoreQueryVo=new YxSystemStoreQueryVo();
                yxSystemStoreQueryVo.setId(order.getStoreId());
                yxSystemStoreQueryVo.setName(rocheStore.getName());
                order.setSystemStore(yxSystemStoreQueryVo);
            }
        }
        StatusDTO statusDTO = new StatusDTO();

        if(order.getRefundStatus() == 1){
            statusDTO.set_class("state-sqtk");
            statusDTO.set_msg("退款审核中,请耐心等待");
            statusDTO.set_type("-1");
            statusDTO.set_title("申请退款中");
        }else if(order.getRefundStatus() == 2){
            statusDTO.set_class("state-sqtk");
            statusDTO.set_msg("已为您退款,感谢您的支持");
            statusDTO.set_type("-2");
            statusDTO.set_title("已退款");
        }else if(OrderStatusEnum.STATUS_8.getValue().equals(order.getStatus() ) ) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("已取消");
            statusDTO.set_type("6");
            statusDTO.set_title("需求单已取消");
        }else if( OrderStatusEnum.STATUS_9.getValue().equals(order.getStatus()) ) {  // 已备货
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("需求单已备货");
            statusDTO.set_type("6");
            statusDTO.set_title("已备货");
        }else if( OrderStatusEnum.STATUS_5.getValue().equals( order.getStatus()) || OrderStatusEnum.STATUS_15.getValue().equals(order.getStatus())) {  // 审核中
            if( ObjectUtil.isNotNull(order.getPrescriptionFlag() ) && order.getPrescriptionFlag()) {
                statusDTO.set_class("state-nfh");
                statusDTO.set_msg("处方正在审核中,请耐心等待");
                statusDTO.set_type("5");
                statusDTO.set_title("审核中");
            }else{
                statusDTO.set_class("state-nfh");
                statusDTO.set_msg("待发货,请耐心等待");
                statusDTO.set_type("5");
                statusDTO.set_title("待发货");
            }

        }else if(OrderStatusEnum.STATUS_11.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-nfh");
            statusDTO.set_msg("付款信息待确定,请耐心等待");
            statusDTO.set_type("1");
            statusDTO.set_title("付款信息待确定");
        }else if(OrderStatusEnum.STATUS_6.getValue().equals(order.getStatus())  ||  order.getStatus() == OrderStatusEnum.STATUS_7.getValue()) {
            statusDTO.set_class("state-ytk");
            if(order.getNeedRefund() == 1) {
                statusDTO.set_msg("审核未通过,正在退款中,请耐心等待");
            } else {
                statusDTO.set_msg("审核未通过/已取消");
            }
            statusDTO.set_type("6");
            statusDTO.set_title("审核未通过");
        }else if( OrderStatusEnum.STATUS_10.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("未申请处方，请点击申请处方");
            statusDTO.set_type("7");
            statusDTO.set_title("未申请处方");
        }else if( OrderStatusEnum.STATUS_12.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("已申请取消，待确认");
            statusDTO.set_type("6");
            statusDTO.set_title("申请待确认");
        }else if( OrderStatusEnum.STATUS_13.getValue().equals(order.getStatus()) ) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("已申请处方，待医生开方");
            statusDTO.set_type("6");
            statusDTO.set_title("待开方");
        }else if( OrderStatusEnum.STATUS_14.getValue().equals(order.getStatus())  ) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("请尽快上传处方笺图片");
            statusDTO.set_type("6");
            statusDTO.set_title("待上传处方");
        }else if(order.getPaid() == 0 ){  // 待支付
            //计算未支付到自动取消订 时间
            long time = ShopConstants.ORDER_OUTTIME_UNPAY *60 + Long.valueOf(order.getAddTime());
            if( ObjectUtil.isNotNull(order.getPrescriptionFlag() ) && order.getPrescriptionFlag()) {
                statusDTO.set_class("nobuy");
                statusDTO.set_msg("请尽快完成预付");
                statusDTO.set_type("0");
                statusDTO.set_title("未预付");
            }else{
                statusDTO.set_class("nobuy");
                statusDTO.set_msg("请尽快完成支付");
                statusDTO.set_type("0");
                statusDTO.set_title("未支付");
            }


        }else if( OrderStatusEnum.STATUS_0.getValue().equals(order.getStatus()) ){  // 待发货
            // 拼团
            if(order.getPinkId() > 0){
                if(pinkService.pinkIngCount(order.getPinkId()) > 0){
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待其他人参加拼团");
                    statusDTO.set_type("1");
                    statusDTO.set_title("拼团中");
                }else{
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待发货,请耐心等待");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待发货");
                }
            }else{
                if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(order.getShippingType())){
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待发货,请耐心等待");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待发货");
                }else{
                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("待核销,请到核销点进行核销");
                    statusDTO.set_type("1");
                    statusDTO.set_title("待核销");
                }
            }

        }else if(order.getStatus() == 1){
            statusDTO.set_class("state-ysh");
            statusDTO.set_msg("已发货");
            statusDTO.set_type("2");
            statusDTO.set_title("待收货");
        }else if(order.getStatus() == 2){
            statusDTO.set_class("state-ypj");
            statusDTO.set_msg("已收货,快去评价一下吧");
            statusDTO.set_type("3");
            statusDTO.set_title("待评价");
        }else if(order.getStatus() == 3){
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("交易完成,感谢您的支持");
            statusDTO.set_type("4");
            statusDTO.set_title("交易完成");
        }

        if(order.getPayType().equals("weixin")){
            statusDTO.set_payType("微信支付");
        }else if(order.getPayType().equals("offline")){
            statusDTO.set_payType("线下收款");
        } else if(order.getPayType().equals("慈善赠药")){
            statusDTO.set_payType("慈善赠药");
        } else if(PayTypeEnum.YUE.getValue().equals(order.getPayType())){
            statusDTO.set_payType("余额支付");
        }else{
            statusDTO.set_payType("其他支付");
        }

        ClaimInfoVo claimInfo=tbClaimInfoService.getByOrderId(order.getStoreId().longValue());

        List<String> showType = new ArrayList<>();
        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(order.getProjectCode())) {
            order.setPharmacistTips("扫码添加“艾满欣服务药师”\n开展线上咨询");
            if(order.getRefundStatus() == 2) {  // 已退款

            }else if( OrderStatusEnum.STATUS_5.getValue().equals(order.getStatus())) {  // 审核中
                showType.add(ButtonEnum.Button_18.getValue());  // 申请取消
            } else if (order.getStatus() == 0 && order.getPaid() == 0) {  // 待支付
                showType.add(ButtonEnum.Button_18.getValue());  // 申请取消
                showType.add(ButtonEnum.Button_19.getValue());  // 提交付款
            } else if ( OrderStatusEnum.STATUS_11.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 付款信息待确定
                showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
            } else if (order.getStatus() == 0 && order.getPaid() == 1) {   // 待发货
                showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
            } else if ( OrderStatusEnum.STATUS_9.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 已备货
                showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
            } else if(order.getStatus() == 1 && order.getPaid() == 1) {  // 待收货
                showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
            } else if(order.getStatus() == 3 && order.getPaid() == 1) {  //  完成关闭

            }
        } else  {

            if(PayTypeEnum.WEIXIN.getValue().equals(order.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(order.getPayType()) || PayTypeEnum.YUE.getValue().equals(order.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(order.getPayType()) || PayTypeEnum.MAPI.getValue().equals(order.getPayType())) {  // 微信在线支付
                if(order.getRefundStatus() == 1 || order.getRefundStatus() == 2) { // 申请退款中，不需要显示其他按钮

                }else if (order.getStatus() == 0 && order.getPaid() == 0) {  // 待支付
                    if(!ProjectNameEnum.LINGYUANZHI.getValue().equals(order.getProjectCode())) {
                        showType.add(ButtonEnum.Button_11.getValue());  // 取消
                    }

                    if(claimInfo==null || (claimInfo!=null && claimInfo.getStatus()==1)){
                        showType.add(ButtonEnum.Button_12.getValue());  // 预付
                    }

                }else if ( OrderStatusEnum.STATUS_10.getValue().equals(order.getStatus())  && order.getPaid() == 0) {  // 未申请处方
                    showType.add(ButtonEnum.Button_11.getValue());  // 取消
                    if(claimInfo==null || (claimInfo!=null && claimInfo.getStatus()==1)){
                        showType.add(ButtonEnum.Button_12.getValue());  // 预付
                    }
                    showType.add(ButtonEnum.Button_16.getValue());  // 申请处方
                } else if ( OrderStatusEnum.STATUS_10.getValue().equals(order.getStatus())  && order.getPaid() == 1) {  // 未申请处方
                    showType.add(ButtonEnum.Button_15.getValue());  // 申请退款
                    showType.add(ButtonEnum.Button_16.getValue());  // 申请处方
                }else if ( OrderStatusEnum.STATUS_13.getValue().equals(order.getStatus()) && order.getPaid() == 0) {  // 未开具处方,未支付
                    if(claimInfo==null || (claimInfo!=null && claimInfo.getStatus()==1)){
                        showType.add(ButtonEnum.Button_12.getValue());  // 预付
                    }
                } else if ( OrderStatusEnum.STATUS_13.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 未开具处方，已支付
                    showType.add(ButtonEnum.Button_15.getValue());  // 申请退款
                } else if ( OrderStatusEnum.STATUS_5.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 审核中
                    if(!ProjectNameEnum.LINGYUANZHI.getValue().equals(order.getProjectCode())) {
                        showType.add(ButtonEnum.Button_15.getValue());  // 申请退款
                    }

                }else if ( OrderStatusEnum.STATUS_15.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 审核中
                    if(!ProjectNameEnum.LINGYUANZHI.getValue().equals(order.getProjectCode())) {
                        showType.add(ButtonEnum.Button_15.getValue());  // 申请退款
                    }
                } else if (OrderStatusEnum.STATUS_0.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 待发货
                    if(!ProjectNameEnum.LINGYUANZHI.getValue().equals(order.getProjectCode())) {
                        showType.add(ButtonEnum.Button_15.getValue());  // 申请退款
                    }
                } else if (OrderStatusEnum.STATUS_9.getValue().equals(order.getStatus()) && order.getPaid() == 1) {  // 已备货
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                    showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
                } else if (order.getStatus() == 1 && order.getPaid() == 1) {  // 待收货
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                    showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
                  //  showType.add(ButtonEnum.Button_17.getValue()); // 收货
                } else if(order.getStatus() == 3) {  //  完成关闭
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                }
            } else  {
                if( OrderStatusEnum.STATUS_5.getValue().equals(order.getStatus())) {  // 审核中
                    showType.add(ButtonEnum.Button_11.getValue());  // 取消
                } else if (order.getStatus() == 0 && order.getPaid() == 0 ) {  // 待支付
                    showType.add(ButtonEnum.Button_11.getValue());  // 取消
                } else if (order.getStatus() == 0 && order.getPaid() == 1) {   // 待发货
                    showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
                } else if (OrderStatusEnum.STATUS_9.getValue().equals(order.getStatus()) ) {  // 已备货
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                    showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
                } else if(order.getStatus() == 1) {  // 待收货
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                    showType.add(ButtonEnum.Button_13.getValue());  // 退款电话
                } else if(order.getStatus() == 3) {  //  完成关闭
                    showType.add(ButtonEnum.Button_14.getValue()); // 物流查询
                }
            }
        }

        statusDTO.set_showType(showType);
        order.set_status(statusDTO);
        // 获得订单所属的药师列表
        if(order.getStoreId() != null) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("FOREIGN_ID",order.getStoreId());

            order.setPharmacists(mdPharmacistService.list(queryWrapper));
        }

        String serviceGroupId = "";
        String servicePhone = "";
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getProjectCode,order.getProjectCode());
        Project project =  projectService.getOne(lambdaQueryWrapper,false);
        // 特定项目，获取网易七鱼的客服组号和咨询电话
        if(StrUtil.isNotBlank( order.getProjectCode())) {

            if(project != null) {
                serviceGroupId = project.getServiceGroupId();
                servicePhone = project.getPhone();
                order.setProjectName(project.getProjectName());
            }
        }

        // 获取益药商城的默认网易七鱼客服组号
        if(StrUtil.isBlank(serviceGroupId)) {
            DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
            dictDetailQueryParam.setName("serviceGroupId");
            String label = "yiyao";
            dictDetailQueryParam.setLabel(label);
            List<DictDetail> dictDetailList = dictDetailService.queryAll(dictDetailQueryParam);
            if(CollUtil.isNotEmpty(dictDetailList)) {
                serviceGroupId = dictDetailList.get(0).getValue();
            }
        }

        // 获取订单对应门店的电话
        if(StrUtil.isBlank(servicePhone)) {
            if(ObjectUtil.isNotNull(yxSystemStoreQueryVo)) {
                servicePhone = yxSystemStoreQueryVo.getPhone();
            }
        }

        order.setServiceGroupId(serviceGroupId);
        order.setServicePhone(servicePhone);

        // 门店的支付方式
        if(ObjectUtil.isNotNull(order.getSystemStore()) && StrUtil.isNotBlank(order.getSystemStore().getPayType()) ) {
            order.setPayType(order.getSystemStore().getPayType());
        }else{  // 如果门店没有指定支付方式，默认用短信链接的支付
            order.setPayType(PayTypeEnum.SMS.getValue());
        }

        // 项目的支付方式
        if(ObjectUtil.isNotNull(project) && StrUtil.isNotBlank(project.getPayType()) && StrUtil.isNotBlank(order.getProjectCode()) ) {
            order.setPayType(project.getPayType());
        }


        return order;
    }



    /**
     * 支付成功后操作
     * @param orderId 订单号
     * @param payType 支付方式
     */
    @Override
    public void paySuccess(String orderId, String payType) {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        String statusName = "";
        //更新订单状态
        QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setPayType(payType);
        storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
        // 更新 订单的状态。如果是从互联网获取处方，则更新状态 未申请处方
        if(orderInfo.getNeedInternetHospitalPrescription()!= null && orderInfo.getNeedInternetHospitalPrescription() == 1 && orderInfo.getInternetHospitalNoticeFlag() == 0) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_10.getValue());
            statusName = "已支付，" + OrderStatusEnum.STATUS_10.getDesc();
        } else {
            storeOrder.setStatus(OrderStatusEnum.STATUS_15.getValue());
            statusName = "已支付，" + OrderStatusEnum.STATUS_15.getDesc();
        }


     //   storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
        yxStoreOrderMapper.update(storeOrder,wrapper);

        if(orderInfo.getCouponId()!=null && orderInfo.getCouponId() > 0){//使用优惠券
            couponUserService.useCoupon(orderInfo.getCouponId(),0,1);//更新优惠券状态
        }

        if(orderInfo.getNeedInternetHospitalPrescription() == null || orderInfo.getNeedInternetHospitalPrescription() == 0 ||  orderInfo.getNeedInternetHospitalPrescription() == 2) {
          //  sendPrsProducer.sendMsg("prsShop-topic", orderInfo.getOrderId());
            log.info("投递延时订单id： [{}]：", orderInfo.getOrderId());


            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",orderInfo.getOrderId());

            jsonObject.put("desc", PayTypeEnum.toType(payType).getValue() + "成功，下发订单" );

            jsonObject.put("projectCode",orderInfo.getProjectCode());
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyYiyaobao,jsonObject.toString(),2000);
        }

        //增加用户购买次数
        userService.incPayCount(orderInfo.getUid());
        //增加状态
        orderStatusService.create(orderInfo.getId(),"pay_success","用户付款成功");
        //拼团
        if(orderInfo.getCombinationId() > 0) {
            pinkService.createPink(orderInfo);
        }

        //砍价
        if(orderInfo.getBargainId() > 0) {
            storeBargainUserService.setBargainUserStatus(orderInfo.getBargainId(),
                    orderInfo.getUid());
        }
        //模板消息推送
        try {
            YxWechatUserQueryVo wechatUser =  wechatUserService.getYxWechatUserById(orderInfo.getUid());
            if(ObjectUtil.isNotNull(wechatUser)){
                ////公众号与小程序打通统一公众号模板通知
                if(StrUtil.isNotBlank(wechatUser.getOpenid())){
                    templateService.paySuccessNotice(orderInfo.getOrderId(),
                            orderInfo.getPayPrice().toString(),wechatUser.getOpenid());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        // 订单状态发送至太平


     /*   if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(orderInfo.getProjectCode())) {
            zhongAnPuYaoService.sendOrderInfo(orderId);
        }*/

        if(ProjectNameEnum.MEIDEYI.getValue().equals(orderInfo.getProjectCode())) {
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",orderInfo.getOrderId());
            jsonObject.put("status","11");
            jsonObject.put("desc","美德医已支付订单");
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
        } else if (ProjectNameEnum.ZHONGANPUYAO.getValue().equals(orderInfo.getProjectCode())) {
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",orderInfo.getOrderId());
            jsonObject.put("status",orderInfo.getStatus().toString());
            jsonObject.put("desc","众安普药已支付订单");
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
        } else if (ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(orderInfo.getProjectCode()) && StrUtil.isNotBlank(orderInfo.getTaipingOrderNumber())) {
            taipingCardService.sendOrderStatus(orderId,TaipingOrderStatusEnum.STATUS_10.getValue());
        }else if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(orderInfo.getProjectCode())){
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",orderInfo.getOrderId());
            jsonObject.put("status",orderInfo.getStatus().toString());
            jsonObject.put("desc","众安慢病已支付订单");
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
        }else if(ProjectNameEnum.LINGYUANZHI.getValue().equals(orderInfo.getProjectCode())){
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",orderInfo.getOrderId());
            jsonObject.put("status",orderInfo.getStatus().toString());
            jsonObject.put("desc","众安0元支付订单");
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);
        }



    }

    public static void main(String[] args) {
        double a=0;
        System.out.println(a==0);
    }


    /**
     * 支付宝支付
     * @param orderId,支付宝支付 本系统已经集成，请自行根据下面找到代码整合下即可
     * @return
     */
    @Override
    public String aliPay(String orderId) throws Exception {
        AlipayConfig alipay = alipayService.find();
        if(ObjectUtil.isNull(alipay)) throw new ErrorRequestException("请先配置支付宝");
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) throw new ErrorRequestException("订单不存在");
        if(orderInfo.getPaid() == 1) throw new ErrorRequestException("该订单已支付");

        if(orderInfo.getPayPrice().doubleValue() <= 0) throw new ErrorRequestException("该支付无需支付");
        TradeVo trade = new TradeVo();
        trade.setOutTradeNo(orderId);
        String payUrl = alipayService.toPayAsWeb(alipay,trade);
        return payUrl;
    }
    /**
     * 微信APP支付
     * @param orderId
     * @return
     * @throws WxPayException
     */
    @Override
    public WxPayAppOrderResult appPay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        YxUser wechatUser = userService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }

        if(StrUtil.isNotEmpty(orderInfo.getExtendOrderId())){
            orderId = orderInfo.getExtendOrderId();
        }

        BigDecimal bigDecimal = new BigDecimal(100);

        return payService.appPay(orderInfo.getPayOutTradeNo(),"app商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue());
    }


    /**
     * 微信H5支付
     * @param orderId
     * @return
     * @throws WxPayException
     */
    @Override
    public WxPayMwebOrderResult wxH5Pay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        YxUser wechatUser = userService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }

        if(StrUtil.isNotEmpty(orderInfo.getExtendOrderId())){
            orderId = orderInfo.getExtendOrderId();
        }

        BigDecimal bigDecimal = new BigDecimal(100);

        return payService.wxH5Pay(orderInfo.getPayOutTradeNo(),"H5商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue());
    }

    /**
     * 小程序支付
     * @param orderId
     * @return
     * @throws WxPayException
     */
    @Override
    public WxPayMpOrderResult wxAppPay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        YxWechatUser wechatUser = wechatUserService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }

        if(StrUtil.isNotEmpty(orderInfo.getExtendOrderId())){
            orderId = orderInfo.getExtendOrderId();
        }

        BigDecimal bigDecimal = new BigDecimal(100);
        String mchName="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getMchName())){
            mchName=product.getMchName();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getMchName())){
                mchName=yxSystemStoreQueryVo.getMchName();
            }
        }
        return miniPayService.wxPay(orderInfo.getPayOutTradeNo(),wechatUser.getRoutineOpenid(),"小程序商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue(),mchName);
    }

    @Override
    public WxPayMpOrderResult newWxAppPay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        YxWechatUser wechatUser = wechatUserService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }

        BigDecimal bigDecimal = new BigDecimal(100);

        String mchId="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getWechatAppletMchid())){
            mchId=product.getWechatAppletMchid();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getWechatAppletMchid())){
                mchId=yxSystemStoreQueryVo.getWechatAppletMchid();
            }
        }
        if(StringUtils.isEmpty(mchId)){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("type","1");
            wrapper.eq("is_default",1);
            wrapper.eq("delete_flag",0);
            WechatConfiguration wechatConfiguration = wechatConfigurationService.getOne(wrapper);
            mchId=wechatConfiguration.getMchId();
        }
        return wechatConfigurationService.wxRoutinePay(orderInfo.getPayOutTradeNo(),wechatUser.getRoutineOpenid(),"小程序商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue(),mchId);
    }

    @Override
    public WxPayMwebOrderResult newWxH5Pay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        YxUser wechatUser = userService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }

        BigDecimal bigDecimal = new BigDecimal(100);

        String mchId="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getWechatHfiveMchid())){
            mchId=product.getWechatHfiveMchid();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getWechatHfiveMchid())){
                mchId=yxSystemStoreQueryVo.getWechatHfiveMchid();
            }
        }
        if(StringUtils.isEmpty(mchId)){
            QueryWrapper<WechatConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("type","2");
            wrapper.eq("is_default",1);
            wrapper.eq("delete_flag",0);
            WechatConfiguration wechatConfiguration = wechatConfigurationService.getOne(wrapper);
            mchId=wechatConfiguration.getMchId();
        }

        return wechatConfigurationService.wxH5Pay(orderInfo.getPayOutTradeNo(),"H5商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue(),mchId);
    }


    /**
     * 小程序支付，用第三方的openid 发起支付
     * @param orderId
     * @return
     * @throws WxPayException
     */
    @Override
    public WxPayMpOrderResult wxAppPay4ThirdPartyOpenid(String orderId,String openid) throws WxPayException {

        if(ObjectUtil.isNull(openid)) {
            throw new ErrorRequestException("openId不能为空");
        }

        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }

        /*YxWechatUser wechatUser = wechatUserService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) {
            throw new ErrorRequestException("用户错误");
        }*/

        if(StrUtil.isNotEmpty(orderInfo.getExtendOrderId())){
            orderId = orderInfo.getExtendOrderId();
        }

        BigDecimal bigDecimal = new BigDecimal(100);
        String mchName="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getMchName())){
            mchName=product.getMchName();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getMchName())){
                mchName=yxSystemStoreQueryVo.getMchName();
            }
        }


        return miniPayService.wxPay4zhongan(orderId,openid,"小程序商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue(),mchName);
    }

    /**
     * 微信支付
     * @param orderId
     */
    @Override
    public WxPayMpOrderResult wxPay(String orderId) throws WxPayException {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) throw new ErrorRequestException("订单不存在");
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) throw new ErrorRequestException("该订单已支付");

        if(orderInfo.getPayPrice().doubleValue() <= 0) throw new ErrorRequestException("该支付无需支付");

        YxWechatUser wechatUser = wechatUserService.getById(orderInfo.getUid());
        if(ObjectUtil.isNull(wechatUser)) throw new ErrorRequestException("用户错误");
        if(StrUtil.isNotEmpty(orderInfo.getExtendOrderId())){
            orderId = orderInfo.getExtendOrderId();
        }
        BigDecimal bigDecimal = new BigDecimal(100);

        return payService.wxPay(orderInfo.getPayOutTradeNo(),wechatUser.getOpenid(),"公众号商品购买",
                bigDecimal.multiply(orderInfo.getPayPrice()).intValue(),
                BillDetailEnum.TYPE_3.getValue());

    }



    /**
     * 余额支付
     * @param orderId 订单号
     * @param uid 用户id
     */
    @Override
    public void yuePay(String orderId, int uid) {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,uid);
        if(ObjectUtil.isNull(orderInfo)) throw new ErrorRequestException("订单不存在");

        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) throw new ErrorRequestException("该订单已支付");

        YxUserQueryVo userInfo = userService.getYxUserById(uid);

        if(userInfo.getNowMoney().doubleValue() < orderInfo.getPayPrice().doubleValue()){
            throw new ErrorRequestException("余额不足");
        }

        userService.decPrice(uid,orderInfo.getPayPrice().doubleValue());

        YxUserBill userBill = new YxUserBill();
        userBill.setUid(uid);
        userBill.setTitle("购买商品");
        userBill.setLinkId(orderInfo.getId().toString());
        userBill.setCategory("now_money");
        userBill.setType("pay_product");
        userBill.setNumber(orderInfo.getPayPrice());
        userBill.setBalance(userInfo.getNowMoney());
        userBill.setMark("余额支付");
        userBill.setStatus(BillEnum.STATUS_1.getValue());
        userBill.setPm(BillEnum.PM_0.getValue());
        userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
        billService.save(userBill);

        //支付成功后处理
        paySuccess(orderInfo.getOrderId(),"yue");

    }

    /**
     * 创建订单-多门店
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */
    @Override
    public YxStoreOrder createOrder4Store(int uid, String key, OrderParam param) {
        JSONArray jsonArray = JSONUtil.createArray();

        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
        Double payPrice = cacheDTO.getPriceGroup().getTotalPrice();
        Double payPostage = cacheDTO.getPriceGroup().getStorePostage();
        OtherDTO other = cacheDTO.getOther();
        YxUserAddressQueryVo userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("请选择收货地址");
            userAddress = userAddressService.getYxUserAddressById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("地址选择有误");
        }else{ //门店
            if(StrUtil.isBlank(param.getRealName()) || StrUtil.isBlank(param.getPhone())) {
                throw new ErrorRequestException("请填写姓名和电话");
            }
            userAddress = new YxUserAddressQueryVo();
            userAddress.setRealName(param.getRealName());
            userAddress.setPhone(param.getPhone());
            userAddress.setProvince("");
            userAddress.setCity("");
            userAddress.setDistrict("");
            userAddress.setDetail("");
        }

        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;

        for (YxStoreCartQueryVo cart : cartInfo) {
            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku",cart.getYiyaobaoSku());
            jsonObject.put("unitPrice",cart.getTruePrice());
            jsonObject.put("amount",cart.getCartNum());

            jsonArray.add(jsonObject);

        }


        //门店

        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            payPrice = NumberUtil.add(payPrice,payPostage);
        }else{
            payPostage = 0d;
        }

        //优惠券
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }

        int useIntegral = param.getUseIntegral().intValue();

        boolean deduction = false;//拼团等
        //拼团等不参与抵扣
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) deduction = true;
        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
        double couponPrice = 0; //优惠券金额
        if(couponId > 0){//使用优惠券
            YxStoreCouponUser couponUser = couponUserService.getCoupon(couponId,uid);
            if(ObjectUtil.isNull(couponUser)) throw new ErrorRequestException("使用优惠劵失败");

            if(couponUser.getUseMinPrice().doubleValue() > payPrice){
                throw new ErrorRequestException("不满足优惠劵的使用条件");
            }
            payPrice = NumberUtil.sub(payPrice,couponUser.getCouponPrice()).doubleValue();

            couponUserService.useCoupon(couponId);//更新优惠券状态

            couponPrice = couponUser.getCouponPrice().doubleValue();

        }
        // 积分抵扣
        double deductionPrice = 0; //抵扣金额
        double usedIntegral = 0; //使用的积分

        //积分抵扣开始
        if(useIntegral > 0 && userInfo.getIntegral().doubleValue() > 0){
            Double integralMax = Double.valueOf(cacheDTO.getOther().getIntegralMax());
            Double integralFull = Double.valueOf(cacheDTO.getOther().getIntegralFull());
            Double integralRatio = Double.valueOf(cacheDTO.getOther().getIntegralRatio());
            if(totalPrice >= integralFull){
                Double userIntegral = userInfo.getIntegral().doubleValue();
                if(integralMax > 0 && userIntegral >= integralMax) userIntegral = integralMax;
                deductionPrice = NumberUtil.mul(userIntegral, integralRatio);
                if(deductionPrice < payPrice){
                    payPrice = NumberUtil.sub(payPrice.doubleValue(),deductionPrice);
                    usedIntegral = userIntegral;
                }else{
                    deductionPrice = payPrice;
                    usedIntegral = NumberUtil.div(payPrice,
                            Double.valueOf(cacheDTO.getOther().getIntegralRatio()));
                    payPrice = 0d;
                }
                userService.decIntegral(uid,usedIntegral);
                //积分流水
                YxUserBill userBill = new YxUserBill();
                userBill.setUid(uid);
                userBill.setTitle("积分抵扣");
                userBill.setLinkId(key);
                userBill.setCategory("integral");
                userBill.setType("deduction");
                userBill.setNumber(BigDecimal.valueOf(usedIntegral));
                userBill.setBalance(userInfo.getIntegral());
                userBill.setMark("购买商品使用");
                userBill.setStatus(1);
                userBill.setPm(0);
                userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
                billService.save(userBill);
            }
        }

        if(payPrice <= 0) payPrice = 0d;

        // 整理数据，发送至益药宝

        // 获取药店信息
        Integer storeId = other.getStoreId();

        YxSystemStore yxSystemStore = yxSystemStoreMapper.selectById(storeId);

        String yiyaobao_store_id = yxSystemStore.getYiyaobaoId();

        String projectCode = other.getProjectCode();
        if(StrUtil.isNotBlank(projectCode)) {
            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,projectCode));
           // List<Product4project> product4projectList = product4projectService.list(new QueryWrapper<Product4project>().eq("project_no",projectCode));
            if(project != null) {
                yiyaobao_projectNo = project.getYiyaobaoProjectCode();
            }
        }

        // 发送到益药宝，获得订单号码
        String phone = "";
        if(StrUtil.isNotBlank(userInfo.getPhone())) {
            phone = userInfo.getPhone();
        }else {
            phone = userInfo.getYaoshiPhone();
        }
        param.setFactUserPhone(phone);
        String orderSn = uploadOrder2Yiyaobao(param,userAddress,yiyaobao_projectNo,yiyaobao_store_id,jsonArray.toString());

        //生成分布式唯一值
       // String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);
        storeOrder.setInsteadFlag(param.getInsteadFlag());


        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        if(OrderInfoEnum.PAY_CHANNEL_2.getValue() == param.getIsChannel()) {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
            storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
            storeOrder.setStatus(OrderStatusEnum.STATUS_0.getValue());
            storeOrder.setType("慈善赠药");
        } else {
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
            storeOrder.setType("需求单");
        }


        storeOrder.setPayType(param.getPayType());
        storeOrder.setUseIntegral(BigDecimal.valueOf(usedIntegral));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(param.getPinkId());
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
        storeOrder.setImagePath(param.getImagePath());
        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
        }else if (AppFromEnum.CSZY.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_2.getValue());
        } else{
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_0.getValue());
        }
        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(param.getShippingType());

        storeOrder.setPartnerCode(other.getPartnerCode());
        storeOrder.setProjectCode(other.getProjectCode());
        storeOrder.setRefereeCode(other.getRefereeCode());
        storeOrder.setDepartCode(other.getDepartCode());


        //处理门店
        if(OrderInfoEnum.SHIPPIING_TYPE_2.getValue().equals(param.getShippingType())){
            YxSystemStoreQueryVo systemStoreQueryVo = systemStoreService.getYxSystemStoreById(param.getStoreId());
            if(systemStoreQueryVo == null ) throw new ErrorRequestException("暂无门店无法选择门店自提");
            storeOrder.setVerifyCode(StrUtil.sub(orderSn,orderSn.length(),-12));
            storeOrder.setStoreId(systemStoreQueryVo.getId());
        }


        // 处理是否代下订单
        if(param.getInsteadFlag() == 1) {
            storeOrder.setFactUserName(param.getRealName());
            storeOrder.setFactUserPhone(param.getPhone());
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("phone",param.getPhone());
            YxUser user = userService.getOne(queryWrapper,false);
            if(user != null) {
                storeOrder.setFactUserId(user.getUid());
            }

        }

        boolean res = save(storeOrder);
        if(!res) throw new ErrorRequestException("订单生成失败");

        //减库存加销量
        for (YxStoreCartQueryVo cart : cartInfo) {
            if(combinationId > 0){
                combinationService.decStockIncSales(cart.getCartNum(),combinationId);
            }else if(seckillId > 0){
                storeSeckillService.decStockIncSales(cart.getCartNum(),seckillId);
            }else if(bargainId > 0){
                storeBargainService.decStockIncSales(cart.getCartNum(),bargainId);
            } else {
                productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                        cart.getProductAttrUnique());
            }

        }

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");


        //使用MQ延时消息
        //mqProducer.sendMsg("yshop-topic",storeOrder.getId().toString());
        //log.info("投递延时订单id： [{}]：", storeOrder.getId());

        //加入redis，30分钟自动取消
      /*  String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNPAY, storeOrder.getId()));
        redisTemplate.opsForValue().set(redisKey, storeOrder.getOrderId() ,
                ShopConstants.ORDER_OUTTIME_UNPAY, TimeUnit.MINUTES);*/

        return storeOrder;
    }


    /**
     * 创建订单
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public YxStoreOrder createOrder(int uid, String key, OrderParam param) {
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
        Double payPrice = cacheDTO.getPriceGroup().getTotalPrice();
        Double payPostage = cacheDTO.getPriceGroup().getStorePostage();

        OtherDTO other = cacheDTO.getOther();
        YxUserAddressQueryVo userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(StrUtil.isEmpty(param.getAddressId())) throw new ErrorRequestException("请选择收货地址");
            userAddress = userAddressService.getYxUserAddressById(param.getAddressId());
            if(ObjectUtil.isNull(userAddress)) throw new ErrorRequestException("地址选择有误");
        }else{ //门店
            if(StrUtil.isBlank(param.getRealName()) || StrUtil.isBlank(param.getPhone())) {
                throw new ErrorRequestException("请填写姓名和电话");
            }
            userAddress = new YxUserAddressQueryVo();
            userAddress.setRealName(param.getRealName());
            userAddress.setPhone(param.getPhone());
            userAddress.setProvince("");
            userAddress.setCity("");
            userAddress.setDistrict("");
            userAddress.setDetail("");
        }

        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;

        for (YxStoreCartQueryVo cart : cartInfo) {
            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }

        }


        //门店

        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            payPrice = NumberUtil.add(payPrice,payPostage);
        }else{
            payPostage = 0d;
        }

        //优惠券
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }

        int useIntegral = param.getUseIntegral().intValue();

        boolean deduction = false;//拼团等
        //拼团等不参与抵扣
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) deduction = true;
        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
        double couponPrice = 0; //优惠券金额
        if(couponId > 0){//使用优惠券
            YxStoreCouponUser couponUser = couponUserService.getCoupon(couponId,uid);
            if(ObjectUtil.isNull(couponUser)) throw new ErrorRequestException("使用优惠劵失败");

            if(couponUser.getUseMinPrice().doubleValue() > payPrice){
                throw new ErrorRequestException("不满足优惠劵的使用条件");
            }
            payPrice = NumberUtil.sub(payPrice,couponUser.getCouponPrice()).doubleValue();

            couponUserService.useCoupon(couponId);//更新优惠券状态

            couponPrice = couponUser.getCouponPrice().doubleValue();

        }
        // 积分抵扣
        double deductionPrice = 0; //抵扣金额
        double usedIntegral = 0; //使用的积分

        //积分抵扣开始
        if(useIntegral > 0 && userInfo.getIntegral().doubleValue() > 0){
            Double integralMax = Double.valueOf(cacheDTO.getOther().getIntegralMax());
            Double integralFull = Double.valueOf(cacheDTO.getOther().getIntegralFull());
            Double integralRatio = Double.valueOf(cacheDTO.getOther().getIntegralRatio());
            if(totalPrice >= integralFull){
                Double userIntegral = userInfo.getIntegral().doubleValue();
                if(integralMax > 0 && userIntegral >= integralMax) userIntegral = integralMax;
                deductionPrice = NumberUtil.mul(userIntegral, integralRatio);
                if(deductionPrice < payPrice){
                    payPrice = NumberUtil.sub(payPrice.doubleValue(),deductionPrice);
                    usedIntegral = userIntegral;
                }else{
                    deductionPrice = payPrice;
                    usedIntegral = NumberUtil.div(payPrice,
                            Double.valueOf(cacheDTO.getOther().getIntegralRatio()));
                    payPrice = 0d;
                }
                userService.decIntegral(uid,usedIntegral);
                //积分流水
                YxUserBill userBill = new YxUserBill();
                userBill.setUid(uid);
                userBill.setTitle("积分抵扣");
                userBill.setLinkId(key);
                userBill.setCategory("integral");
                userBill.setType("deduction");
                userBill.setNumber(BigDecimal.valueOf(usedIntegral));
                userBill.setBalance(userInfo.getIntegral());
                userBill.setMark("购买商品使用");
                userBill.setStatus(1);
                userBill.setPm(0);
                userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
                billService.save(userBill);
            }


        }

        if(payPrice <= 0) payPrice = 0d;

        //生成分布式唯一值
        String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);
        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
        storeOrder.setPayType(param.getPayType());
        storeOrder.setUseIntegral(BigDecimal.valueOf(usedIntegral));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(param.getPinkId());
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
        }else{
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_0.getValue());
        }
        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(param.getShippingType());
        //处理门店
        if(OrderInfoEnum.SHIPPIING_TYPE_2.getValue().equals(param.getShippingType())){
            YxSystemStoreQueryVo systemStoreQueryVo = systemStoreService.getYxSystemStoreById(param.getStoreId());
            if(systemStoreQueryVo == null ) throw new ErrorRequestException("暂无门店无法选择门店自提");
            storeOrder.setVerifyCode(StrUtil.sub(orderSn,orderSn.length(),-12));
            storeOrder.setStoreId(systemStoreQueryVo.getId());
        }

        storeOrder.setImagePath(param.getImagePath());
        boolean res = save(storeOrder);
        if(!res) {
            throw new ErrorRequestException("订单生成失败");
        }

        //减库存加销量
        for (YxStoreCartQueryVo cart : cartInfo) {
            if(combinationId > 0){
                combinationService.decStockIncSales(cart.getCartNum(),combinationId);
            }else if(seckillId > 0){
                storeSeckillService.decStockIncSales(cart.getCartNum(),seckillId);
            }else if(bargainId > 0){
                storeBargainService.decStockIncSales(cart.getCartNum(),bargainId);
            } else {
                productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                        cart.getProductAttrUnique());
            }

        }

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");


        //使用MQ延时消息
        //mqProducer.sendMsg("yshop-topic",storeOrder.getId().toString());
        //log.info("投递延时订单id： [{}]：", storeOrder.getId());

        //加入redis，30分钟自动取消
        String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNPAY, storeOrder.getId()));
        redisTemplate.opsForValue().set(redisKey, storeOrder.getOrderId() ,
                ShopConstants.ORDER_OUTTIME_UNPAY, TimeUnit.MINUTES);

        return storeOrder;
    }



    /**
     * 创建订单
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public YxStoreOrder createOrder4External(int uid, String key, OrderExternalParam param) {
       // YxUserQueryVo userInfo = userService.getYxUserById(uid);
        //if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
        Double payPrice = cacheDTO.getPriceGroup().getTotalPrice();
        Double payPostage = cacheDTO.getPriceGroup().getStorePostage();

        OtherDTO other = cacheDTO.getOther();
        YxUserAddressQueryVo userAddress = param.getAddress();


        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;

        for (YxStoreCartQueryVo cart : cartInfo) {
            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }

        }



        payPostage = 0d;


        //优惠券
        int couponId = 0;

        int useIntegral = 0;

        boolean deduction = false;//拼团等

        double couponPrice = 0; //优惠券金额

        // 积分抵扣
        double deductionPrice = 0; //抵扣金额
        double usedIntegral = 0; //使用的积分

        //积分抵扣开始


        if(payPrice <= 0) payPrice = 0d;

        //生成分布式唯一值
       // String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        String orderSn = param.getOrderNo();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);
        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setPayTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setPayType(param.getPayType());
        storeOrder.setUseIntegral(BigDecimal.valueOf(usedIntegral));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(0);
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
        storeOrder.setStatus(0);

        storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_2.getValue());
        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(1);
        storeOrder.setImagePath(param.getImagePath());
        storeOrder.setType(param.getType());
        boolean res = save(storeOrder);
        if(!res) throw new ErrorRequestException("订单生成失败");

        //减库存加销量
        for (YxStoreCartQueryVo cart : cartInfo) {
            if(combinationId > 0){
                combinationService.decStockIncSales(cart.getCartNum(),combinationId);
            }else if(seckillId > 0){
                storeSeckillService.decStockIncSales(cart.getCartNum(),seckillId);
            }else if(bargainId > 0){
                storeBargainService.decStockIncSales(cart.getCartNum(),bargainId);
            } else {
                productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                        cart.getProductAttrUnique());
            }

        }

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");


        return storeOrder;
    }

    /**
     * 计算价格
     * @param key
     * @param couponId
     * @param useIntegral
     * @param shippingType
     * @return
     */
    @Override
    public ComputeDTO computedOrder(int uid, String key, int couponId,
                                    int useIntegral, int shippingType,String addressId,String projectCode,String cardNumber,Integer expressTemplateId) {
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");
        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }
        ComputeDTO computeDTO = new ComputeDTO();

        Double totalPrice = cacheDTO.getPriceGroup().getVipPrice();
       // computeDTO.setTotalPrice(cacheDTO.getPriceGroup().getVipPrice());
        Double payPrice = cacheDTO.getPriceGroup().getVipPrice();
        Double payPostage = 0d;

        // 太平项目，邮费根据收货地址计算
        /*if (ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode) && StrUtil.isNotBlank(addressId) ){

            YxUserAddressQueryVo  userAddress = userAddressService.getYxUserAddressById(addressId);
            if(ObjectUtil.isNotEmpty(userAddress)) {
                YxExpressTemplate yxExpressTemplate = yxExpressTemplateService.getOne(new QueryWrapper<YxExpressTemplate>().eq("is_default",1),false);
                if(ObjectUtil.isNotEmpty(yxExpressTemplate)) {
                    YxExpressTemplateDetail yxExpressTemplateDetail =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", userAddress.getProvince()),false);
                    if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail)){
                        payPostage = yxExpressTemplateDetail.getPrice().doubleValue();
                    }else{
                        payPostage = 0d;
                    }
                }
            }
        }*/

        ComputeDTO computeDTO1 = computedOrder4Project(uid,projectCode,BigDecimal.valueOf(totalPrice),addressId,expressTemplateId);
        payPostage = computeDTO1.getPayPostage();

        boolean deduction = false;//拼团秒杀砍价等
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;
        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
        for (YxStoreCartQueryVo cart : cartInfo) {
            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
        }
        //拼团等不参与抵扣
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) deduction = true;


        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
        double couponPrice = 0;

        if(couponId > 0){//使用优惠券
            YxStoreCouponUser couponUser = couponUserService.getCoupon(couponId,uid);
            if(ObjectUtil.isNull(couponUser)) throw new ErrorRequestException("使用优惠劵失败");

            /*if(couponUser.getUseMinPrice().doubleValue() > payPrice){
                throw new ErrorRequestException("不满足优惠劵的使用条件");
            }
            payPrice = NumberUtil.sub(payPrice,couponUser.getCouponPrice()).doubleValue();

            couponPrice = couponUser.getCouponPrice().doubleValue();*/
            // 按项目区分优惠券的使用逻辑
            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
                // 1. 只针对5折类商品，使用折扣优惠券
                // 2. 其他商品使用会员价
                BigDecimal sumPrice_label3 = BigDecimal.ZERO;
                BigDecimal sumPrice_label_other = BigDecimal.ZERO;
                List<YxStoreCartQueryVo> cartLsit = cacheDTO.getCartInfo();
                for(YxStoreCartQueryVo storeCart:cartLsit) {
                    if( "Y".equals(storeCart.getLabel3())) {
                        sumPrice_label3 = NumberUtil.add(sumPrice_label3,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
                        // 5折类商品，用了优惠券，就是原价
                        storeCart.setVipTruePrice(storeCart.getTruePrice());
                    } else if("Y".equals(storeCart.getLabel1())) {
                        sumPrice_label_other = NumberUtil.add(sumPrice_label_other,NumberUtil.mul(storeCart.getCartNum(),storeCart.getVipTruePrice()));
                    } else {
                        sumPrice_label_other = NumberUtil.add(sumPrice_label_other,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
                    }
                }

                // 每单抵扣50%，每次最高抵扣250元
                couponPrice = NumberUtil.mul(sumPrice_label3.doubleValue(),couponUser.getDeductionRate().doubleValue());
                //每次最高抵扣250元
                if(couponPrice >= couponUser.getMaxDeductionAmount().doubleValue()) {
                    couponPrice = couponUser.getMaxDeductionAmount().doubleValue();
                }

                // 商品总计价格= 5折商品的支付价格 + 其他品类的会员价
                payPrice = NumberUtil.add(sumPrice_label3.doubleValue(),sumPrice_label_other.doubleValue());

                // 商品支付价格 = 商品总计价格 - 优惠金额
                payPrice = NumberUtil.sub(payPrice.doubleValue(),couponPrice);



                totalPrice = NumberUtil.add(sumPrice_label3.doubleValue(),sumPrice_label_other.doubleValue());
            }else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(projectCode) && couponUser != null){
                ZhongAnParamDto param=new ZhongAnParamDto();
                param.setCardNumber(cardNumber);
                param.setProjectCode(projectCode);
                param.setCouponNo(couponUser.getCouponNo());
                couponUserService.updateCoupon(param,uid);
                couponUser = couponUserService.getCoupon(couponId,uid);
                if(couponUser.getCouponEffectiveTime()!=null && couponUser.getCouponEffectiveTime().after(new Date())){
                    throw new ErrorRequestException("使用优惠劵失败，优惠券未到使用时间段。");
                }
                if(couponUser.getCouponExpiryTime()!=null && couponUser.getCouponExpiryTime().before(new Date())){
                    throw new ErrorRequestException("使用优惠劵失败，优惠券已过期。");
                }
                if(couponUser.getStatus()!=0){
                    throw new ErrorRequestException("使用优惠劵失败，优惠券已使用或已过期。");
                }
                List<YxStoreCartQueryVo> cartLsit = cacheDTO.getCartInfo();

                BigDecimal total= BigDecimal.ZERO;
                // 满减
                if(couponUser.getCouponDetailType()==1){
                    couponPrice=couponUser.getCouponPrice().doubleValue();
                    for(YxStoreCartQueryVo storeCart:cartLsit) {
                        total = NumberUtil.add(total,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
                    }

                    payPrice = NumberUtil.sub(total.doubleValue(),couponPrice);
                    totalPrice=total.doubleValue();
                }
                //折扣
                if(couponUser.getCouponDetailType()==2){

                    for(YxStoreCartQueryVo storeCart:cartLsit) {
                        total = NumberUtil.add(total,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
                    }
                    couponPrice=total.subtract(total.multiply(couponUser.getCouponPrice().divide(new BigDecimal("10")))).doubleValue();

                    payPrice = NumberUtil.sub(total.doubleValue(),couponPrice);
                    totalPrice=total.doubleValue();
                }
                //免费
                if(couponUser.getCouponDetailType()==3){

//                    for(YxStoreCartQueryVo storeCart:cartLsit) {
//                        total = NumberUtil.add(total,NumberUtil.mul(storeCart.getCartNum(),storeCart.getVipTruePrice()));
//                    }
//                    couponPrice=total.doubleValue();
                }
//                payPrice = NumberUtil.sub(total.doubleValue(),couponPrice);
//                totalPrice=total.doubleValue();
            }


        }


        //1-配送 2-到店
        if(shippingType == 1){
            payPrice = NumberUtil.add(payPrice,payPostage);
        }else{
            payPostage = 0d;
        }

        // 积分抵扣
        double deductionPrice = 0;
        System.out.println("a:"+userInfo.getIntegral().doubleValue());
        if(useIntegral > 0 && userInfo.getIntegral().doubleValue() > 0){
            Double integralMax = Double.valueOf(cacheDTO.getOther().getIntegralMax());
            Double integralFull = Double.valueOf(cacheDTO.getOther().getIntegralFull());
            Double integralRatio = Double.valueOf(cacheDTO.getOther().getIntegralRatio());
            if(computeDTO.getTotalPrice() >= integralFull){
                Double userIntegral = userInfo.getIntegral().doubleValue();
                if(integralMax > 0 && userIntegral >= integralMax) userIntegral = integralMax;
                deductionPrice = NumberUtil.mul(userIntegral, integralRatio);
                if(deductionPrice < payPrice){
                    payPrice = NumberUtil.sub(payPrice.doubleValue(),deductionPrice);
                }else{
                    deductionPrice = payPrice;
                    payPrice = 0d;
                }
            }
        }

        if(payPrice <= 0) payPrice = 0d;

        computeDTO.setTotalPrice(totalPrice);
        computeDTO.setPayPrice(payPrice);
        computeDTO.setPayPostage(payPostage);
        computeDTO.setCouponPrice(couponPrice);
        computeDTO.setDeductionPrice(deductionPrice);

        return computeDTO;
    }

    /**
     * 订单信息
     * @param unique 订单id
     * @param uid
     * @return
     */
    @Override
    public YxStoreOrderQueryVo getOrderInfo(String unique,int uid) {
        QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del",0).and(
                i->i.eq("order_id",unique).or().eq("`unique`",unique).or()
                        .eq("extend_order_id",unique));
        if(uid > 0) wrapper.eq("uid",uid);

        return orderMap.toDto(yxStoreOrderMapper.selectOne(wrapper));
    }

    @Override
    public CacheDTO getCacheOrderInfo(int uid, String key) {

        return (CacheDTO)redisService.getObj("user_order_"+uid+key);
    }

    @Override
    public void delCacheOrderInfo(int uid, String key) {
        redisService.delete("user_order_"+uid+key);
    }

    /**
     * 缓存订单
     * @param uid uid
     * @param cartInfo cartInfo
     * @param priceGroup priceGroup
     * @param other other
     * @return
     */
    @Override
    public String cacheOrderInfo(int uid, List<YxStoreCartQueryVo> cartInfo, PriceGroupDTO priceGroup, OtherDTO other) {
        String key = IdUtil.simpleUUID();
        CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setCartInfo(cartInfo);
        cacheDTO.setPriceGroup(priceGroup);
        cacheDTO.setOther(other);
        redisService.saveCode("user_order_"+uid+key,cacheDTO,1800L);
        return key;
    }

    /**
     * 获取订单价格
     * @param cartInfo
     * @return
     */
    @Override
    public PriceGroupDTO getOrderPriceGroup(List<YxStoreCartQueryVo> cartInfo) {

        String storePostageStr = systemConfigService.getData("store_postage");//邮费基础价
        Double storePostage = 0d;
        if(StrUtil.isNotEmpty(storePostageStr)) storePostage = Double.valueOf(storePostageStr);

        String storeFreePostageStr = systemConfigService.getData("store_free_postage");//满额包邮
        Double storeFreePostage = 0d;
        if(StrUtil.isNotEmpty(storeFreePostageStr)) storeFreePostage = Double.valueOf(storeFreePostageStr);

        Double totalPrice = getOrderSumPrice(cartInfo, "truePrice");//获取订单总金额 （商品原价）
        Double costPrice = getOrderSumPrice(cartInfo, "costPrice");//获取订单成本价
        Double vipPrice = getOrderSumPrice(cartInfo, "vipTruePrice");//获取订单会员优惠金额 （会员价）
        Double innerPrice = getOrderSumPrice(cartInfo, "innerPrice");//获取订单内购金额

        if(storeFreePostage == 0){//包邮
            storePostage = 0d;
        }else{
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                if(storeCart.getProductInfo().getIsPostage() != 0){//不包邮
                    storePostage = NumberUtil.add(storePostage
                            ,storeCart.getProductInfo().getPostage()).doubleValue();
                }
            }
            //如果总价大于等于满额包邮 邮费等于0
            if (storeFreePostage <= totalPrice) storePostage = 0d;
        }

        PriceGroupDTO priceGroupDTO = new PriceGroupDTO();
        priceGroupDTO.setStorePostage(storePostage);
        priceGroupDTO.setStoreFreePostage(storeFreePostage);
        priceGroupDTO.setTotalPrice(totalPrice);
        priceGroupDTO.setCostPrice(costPrice);
        priceGroupDTO.setVipPrice(vipPrice);
        priceGroupDTO.setInnerPrice(innerPrice);
        return priceGroupDTO;
    }

    /**
     * 获取某字段价格
     * @param cartInfo
     * @param key
     * @return
     */
    @Override
    public Double getOrderSumPrice(List<YxStoreCartQueryVo> cartInfo, String key) {
        BigDecimal sumPrice = BigDecimal.ZERO;
        // 商品原价
        if(key.equals("truePrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
            }
        }else if(key.equals("costPrice")){
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getCostPrice()));
            }
        }else if(key.equals("vipTruePrice")){  // vip价
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getVipTruePrice()));
            }
        }else if(key.equals("innerPrice")){  // 内购价
            for (YxStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(),storeCart.getInnerPrice()));
            }
        }

        //System.out.println("sumPrice:"+sumPrice);
        return sumPrice.doubleValue();
    }

    @Override
    public YxStoreOrderQueryVo getYxStoreOrderById(Serializable id) throws Exception{
        return yxStoreOrderMapper.getYxStoreOrderById(id);
    }

    @Override
    public Paging<YxStoreOrderQueryVo> getYxStoreOrderPageList(YxStoreOrderQueryParam yxStoreOrderQueryParam) throws Exception{
        Page page = setPageParam(yxStoreOrderQueryParam,OrderItem.desc("create_time"));
        IPage<YxStoreOrderQueryVo> iPage = yxStoreOrderMapper.getYxStoreOrderPageList(page,yxStoreOrderQueryParam);
        return new Paging(iPage);
    }


    @Override
    public List<YxStoreOrderQueryVo> yiyaobaoOrderList(int uid, YxStoreOrderQueryParam queryParam) {

        int type = queryParam.getType().intValue();
        int page = queryParam.getPage().intValue();
        int limit = queryParam.getLimit().intValue();
        // 获取用户的手机号
        YxUser yxUser = userService.getById(uid);

        if(yxUser == null) {
            return  null;
        }
        List<String> otherStatusList = Arrays.asList("90","80","94","10","33","37","60","98");
        // 如果没有绑定手机号，则返回益药公众号中的订单数据
        if(StrUtil.isBlank(yxUser.getPhone()) && StrUtil.isBlank(yxUser.getYaoshiPhone())){
            return orderList(uid,type,page,limit,new YxStoreOrderQueryParam());
        } else {
            List<YxStoreOrderQueryVo> list = new ArrayList<>();
            OrderQueryParam orderQueryParam = new OrderQueryParam();
            String phone = "";
            if(StrUtil.isNotBlank(yxUser.getPhone())){
                phone = yxUser.getPhone();
            } else {
                phone = yxUser.getYaoshiPhone();
            }
            orderQueryParam.setMobile(phone);
            orderQueryParam.setPage(page);
            orderQueryParam.setLimit(limit);
            List<String> statusList = new ArrayList<>();
            List<String> orderNoList = new ArrayList<>();
            List<String> orderNoListNotExists = new ArrayList<>();
            switch (OrderStatusEnum.toType(type)){
                case STATUS_5://待审核
                    statusList = Arrays.asList("01");
                    break;
                case STATUS_0://未支付
                    statusList = Arrays.asList("14","15");
                    break;
                case STATUS_1://待发货
                    statusList = Arrays.asList("20","25","30","31","35","36","38","40","41","42");
                    break;
                case STATUS_2://待收货
                    statusList = Arrays.asList("43");
                    break;
                case STATUS_3://待评价
                    statusList = Arrays.asList("50","45");

                    QueryWrapper queryWrapper1 = new QueryWrapper();
                    queryWrapper1.eq("uid",uid);
                    queryWrapper1.select("oid");
                    List<YxStoreProductReply> replyList1 = storeProductReplyService.list(queryWrapper1);
                    for(YxStoreProductReply reply: replyList1) {
                        if(!orderNoListNotExists.contains(reply.getOid())) {
                            orderNoListNotExists.add(reply.getOid());
                        }
                    }

                    break;
                case STATUS_4://已完成
                    statusList = Arrays.asList("50","45");

                    // 已评价的订单号列表
                 /*   QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("uid",uid);
                    queryWrapper.select("oid");
                    List<YxStoreProductReply> replyList = storeProductReplyService.list(queryWrapper);
                    for(YxStoreProductReply reply: replyList) {
                         if(!orderNoList.contains(reply.getOid())) {
                             orderNoList.add(reply.getOid());
                         }
                    }
                    if(CollUtil.isEmpty(orderNoList)) {
                        orderNoList.add("-");
                    }*/
                    break;
                case STATUS_6://其他订单
                  //  statusList = Arrays.asList("90","80","94","10","33","37","60","98");
                    break;
            }
            orderQueryParam.setStatusList(statusList);
            orderQueryParam.setOrderNoList(orderNoList);
            orderQueryParam.setOrderNoListNotExists(orderNoListNotExists);
            orderQueryParam.setKeyword(queryParam.getKeyword());
            orderQueryParam.setStartDate(queryParam.getStartDate());
            orderQueryParam.setEndDate(queryParam.getEndDate());
            Paging<OrderVo> yiyaobaoOrderPage = yiyaobaoOrderService.getYiyaobaoOrderbyMobile(orderQueryParam);
            if(yiyaobaoOrderPage != null) {
                List<OrderVo> yiyaobaoOrderList = yiyaobaoOrderPage.getRecords();
// addTime   status  orderId  cartInfo  payPrice  _status
                for(OrderVo orderVo : yiyaobaoOrderList) {
                    YxStoreOrderQueryVo yxStoreOrderQueryVo = new YxStoreOrderQueryVo();

                    List<OrderDetailVo> orderDetailVoList = yiyaobaoOrderService.getOrderDetail(orderVo.getId());
                    boolean isReplyFlag = false;
                    List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
                    String yiyaobaoStatus = orderVo.getStatusCode();
                    // 获取订单明细
                    // id  productInfo productId productAttrUnique cartNum
                    for(OrderDetailVo detailVo:orderDetailVoList) {
                        YxStoreCartQueryVo cartQueryVo = new YxStoreCartQueryVo();
                        // cartQueryVo.setId(detailVo.getId());
                        cartQueryVo.setBargainId(0);
                        cartQueryVo.setSeckillId(0);
                        cartQueryVo.setCombinationId(0);
                        cartQueryVo.setUnique(detailVo.getSku());
                        cartQueryVo.setCartNum(detailVo.getQty());
                        cartQueryVo.setProductAttrUnique(detailVo.getSku());
                        // 判断是否已经评价过
                        QueryWrapper queryWrapper2 = new QueryWrapper();
                        queryWrapper2.eq("oid",orderVo.getOrderNo());
                        queryWrapper2.eq("`unique`",detailVo.getSku());
                        Integer count =  storeProductReplyService.count(queryWrapper2);
                        if(count == 0 && !otherStatusList.contains(yiyaobaoStatus)) {
                            cartQueryVo.setIsReplyFlag(true);

                            isReplyFlag = true;

                        } else {
                            cartQueryVo.setIsReplyFlag(false);
                        }

                        YxStoreProductQueryVo productInfo = new YxStoreProductQueryVo();
                        // 查找药品图片
                        String image = "http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg";
                        YxStoreProduct yxStoreProduct = productService.getOne(new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",detailVo.getSku()).select("image"),false);
                        if(yxStoreProduct != null  && StrUtil.isNotBlank(yxStoreProduct.getImage())) {
                            image = yxStoreProduct.getImage();
                        }else{
                            // 到益药宝系统查找药品的图片
                            String image_temp = yiyaobaoOrderService.getMedicineImageBySku(detailVo.getSku());
                            if(StrUtil.isNotBlank(image_temp)) {
                                image = image_temp;
                            }
                        }

                        productInfo.setImage(image);
                        productInfo.setStoreName(detailVo.getProductName());
                        productInfo.setPrice(detailVo.getUnitPrice());
                        cartQueryVo.setProductInfo(productInfo);
                        cartQueryVo.setTruePrice(detailVo.getUnitPrice().doubleValue());
                        cartInfo.add(cartQueryVo);
                    }

                    yxStoreOrderQueryVo.setCartInfo(cartInfo);



                    String orderDate = orderVo.getOrderDate();
                    DateTime dateTime = DateUtil.parseDateTime(orderDate);
                    Integer time = OrderUtil.dateToTimestampT(dateTime);
                    yxStoreOrderQueryVo.setAddTime( time);
                    yxStoreOrderQueryVo.setOrderId(orderVo.getOrderNo());
                    yxStoreOrderQueryVo.setPayPrice(orderVo.getTotalAmount());
                    StatusDTO statusDTO = new StatusDTO();
                    Integer status = null;
                    Integer paid=0;

                    if("10".equals(orderVo.getPayResult())) {
                        paid = 1;
                    }else {
                        paid = 0;
                    }

                    String statusName = "";
                    if(yiyaobaoStatus.equals("01")) {  //待审核
                        status=5;

                        statusName = "待审核";

                        statusDTO.set_class("state-nfh");
                        statusDTO.set_msg("处方未审核,请耐心等待");
                        statusDTO.set_type("5");
                        statusDTO.set_title("待审核");
                    }else if(yiyaobaoStatus.equals("14") || yiyaobaoStatus.equals("15")) {  //待支付
                        status=0;

                        statusName = "待付款";
                        statusDTO.set_class("nobuy");
                        statusDTO.set_msg("请尽快完成支付");
                        statusDTO.set_type("0");
                        statusDTO.set_title("未支付");
                    }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                            yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                            yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                            yiyaobaoStatus.equals("42")
                    ) { //待发货
                        status=0;

                        statusName = "待发货";

                        statusDTO.set_class("state-nfh");
                        statusDTO.set_msg("待发货,请耐心等待");
                        statusDTO.set_type("1");
                        statusDTO.set_title("待发货");
                      /*  if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*/
                    } else if(yiyaobaoStatus.equals("43")){ //待收货
                        status=1;

                        statusName = "待收货";

                        statusDTO.set_class("state-ysh");
                        statusDTO.set_msg("服务商已发货");
                        statusDTO.set_type("2");
                        statusDTO.set_title("待收货");
                        /*paid=1;
                        if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*/
                    } else if (yiyaobaoStatus.equals("50") || yiyaobaoStatus.equals("45")){  // 已收货，待评价
                        status=4;


                        // 判断是否已经评价过

                        if(isReplyFlag ) { // 待评价
                            statusName = "已完成";
                            statusDTO.set_class("state-ypj");
                            statusDTO.set_msg("已收货,快去评价一下吧");
                            statusDTO.set_type("3");
                            statusDTO.set_title("待评价");
                        } else {  // 已完成
                            statusName = "已完成";
                            statusDTO.set_class("state-ytk");
                            statusDTO.set_msg("交易完成,感谢您的支持");
                            statusDTO.set_type("4");
                            statusDTO.set_title("交易完成");
                        }

                        /*paid=1;
                        if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*/
                    }else {  // 其他状态
                        status=6;

                        statusName = orderVo.getStatus();
                        statusDTO.set_class("state-ytk");
                        statusDTO.set_msg(orderVo.getStatus());
                        statusDTO.set_type("4");
                        statusDTO.set_title("交易完成");
                    }

                    yxStoreOrderQueryVo.setStatus(status);
                    yxStoreOrderQueryVo.setPaid(paid);
                    yxStoreOrderQueryVo.setStatusName(statusName);
                    yxStoreOrderQueryVo.setYiyaobaoOrderId(orderVo.getId());
                    yxStoreOrderQueryVo.setDeliveryType("express");
                    yxStoreOrderQueryVo.setDeliveryName(orderVo.getLogisticsName());
                    yxStoreOrderQueryVo.setDeliveryId(orderVo.getFreightNo());
                    yxStoreOrderQueryVo.setDeliverySn("");
                    /*if(order.getPayType().equals("weixin")){
                        statusDTO.set_payType("微信支付");
                    }else if(order.getPayType().equals("慈善赠药")){
                        statusDTO.set_payType("慈善赠药");
                    }else{
                        statusDTO.set_payType("余额支付");
                    }*/

                    yxStoreOrderQueryVo.set_status(statusDTO);

                    // 判断订单是否是益药公众号的订单
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("order_id",orderVo.getOrderNo());
                    int v_count = this.count(queryWrapper);
                    if(v_count >= 0) {
                        yxStoreOrderQueryVo.setIsSelfOrder(true);
                    } else {
                        yxStoreOrderQueryVo.setIsSelfOrder(false);
                    }





                    list.add(yxStoreOrderQueryVo);
                }

            }

            return  list;
        }
    }

    @Override
    public YxStoreOrderQueryVo queryOrderDetail(String orderId) {
        int uid = SecurityUtils.getUserId().intValue();
// 获取用户的手机号
        /*YxUser yxUser = userService.getById(uid);

        if(yxUser == null) {
            return  null;
        }*/
        YxStoreOrderQueryVo storeOrder = this.getOrderInfo(orderId,uid);
        if(ObjectUtil.isNull(storeOrder)){
            return null;
        }
        YxStoreOrderQueryVo yxStoreOrderQueryVo = this.handleOrder4Store(storeOrder);

        ClaimInfoVo claimInfoVo = tbClaimInfoService.getByOrderId(storeOrder.getId().longValue());
        if(claimInfoVo!=null){
            yxStoreOrderQueryVo.setClaimInfoVo(claimInfoVo);
        }
        return yxStoreOrderQueryVo;

        // 如果没有绑定手机号，则返回益药公众号中的订单数据
       /* if(StrUtil.isBlank(yxUser.getPhone()) && StrUtil.isBlank(yxUser.getYaoshiPhone())) {
            YxStoreOrderQueryVo storeOrder = this.getOrderInfo(orderId,uid);
            if(ObjectUtil.isNull(storeOrder)){
                return null;
            }
            YxStoreOrderQueryVo yxStoreOrderQueryVo = this.handleOrder4Store(storeOrder);

            return yxStoreOrderQueryVo;
        }
        else {
            OrderVo orderVo = yiyaobaoOrderService.getYiyaobaoOrderbyOrderId(orderId);

            YxStoreOrderQueryVo yxStoreOrderQueryVo = new YxStoreOrderQueryVo();

            boolean isReplyFlag = false;
            List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
            List<String> otherStatusList = Arrays.asList("90","80","94","10","33","37","60","98");
            // 获取订单明细

// 订单明细
            List<OrderDetailVo> orderDetailVoList = yiyaobaoOrderService.getOrderDetail(orderVo.getId());
            String yiyaobaoStatus = orderVo.getStatusCode();
            // id  productInfo productId productAttrUnique cartNum
            for(OrderDetailVo detailVo:orderDetailVoList) {
                YxStoreCartQueryVo cartQueryVo = new YxStoreCartQueryVo();
                // cartQueryVo.setId(detailVo.getId());
                cartQueryVo.setBargainId(0);
                cartQueryVo.setSeckillId(0);
                cartQueryVo.setCombinationId(0);
                cartQueryVo.setUnique(detailVo.getSku());
                cartQueryVo.setCartNum(detailVo.getQty());
                cartQueryVo.setProductAttrUnique(detailVo.getSku());

                // 判断是否已经评价过
                QueryWrapper queryWrapper2 = new QueryWrapper();
                queryWrapper2.eq("oid",orderVo.getOrderNo());
                queryWrapper2.eq("`unique`",detailVo.getSku());
                Integer count =  storeProductReplyService.count(queryWrapper2);
                if(count == 0  &&  !otherStatusList.contains(yiyaobaoStatus)) {
                    cartQueryVo.setIsReplyFlag(true);

                    isReplyFlag = true;

                } else {
                    cartQueryVo.setIsReplyFlag(false);
                }

                YxStoreProductQueryVo productInfo = new YxStoreProductQueryVo();
                // image storeName attrInfo price
                productInfo.setImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
                productInfo.setStoreName(detailVo.getProductName());
                productInfo.setPrice(detailVo.getUnitPrice());
                cartQueryVo.setTruePrice(detailVo.getUnitPrice().doubleValue());
                cartQueryVo.setProductInfo(productInfo);
                cartInfo.add(cartQueryVo);
            }


            String orderDate = orderVo.getOrderDate();
            DateTime dateTime = DateUtil.parseDateTime(orderDate);
            Integer time = OrderUtil.dateToTimestampT(dateTime);
            yxStoreOrderQueryVo.setAddTime( time);
            yxStoreOrderQueryVo.setOrderId(orderVo.getOrderNo());
            yxStoreOrderQueryVo.setPayPrice(orderVo.getDiscountTotalAmount());
            yxStoreOrderQueryVo.setTotalPrice(orderVo.getTotalAmount());
            yxStoreOrderQueryVo.setShippingType(1);
            Integer status = null;
            Integer paid = 0;
           if("10".equals(orderVo.getPayResult())) {
               paid = 1;
           } else {
               paid = 0;
           }
            String statusName = "";
            StatusDTO statusDTO = new StatusDTO();
            if(yiyaobaoStatus.equals("01")) {  //待审核
                status=5;

                statusName = "待审核";

                statusDTO.set_class("state-nfh");
                statusDTO.set_msg("处方未审核,请耐心等待");
                statusDTO.set_type("5");
                statusDTO.set_title("待审核");
            }else if(yiyaobaoStatus.equals("14") || yiyaobaoStatus.equals("15")) {  //待支付
                status=0;

                statusName = "待付款";
                statusDTO.set_class("nobuy");
                statusDTO.set_msg("请尽快完成支付");
                statusDTO.set_type("0");
                statusDTO.set_title("未支付");
            }else if(yiyaobaoStatus.equals("20") || yiyaobaoStatus.equals("25")  || yiyaobaoStatus.equals("30") ||
                    yiyaobaoStatus.equals("31") || yiyaobaoStatus.equals("35")  || yiyaobaoStatus.equals("36") ||
                    yiyaobaoStatus.equals("38") || yiyaobaoStatus.equals("40")  || yiyaobaoStatus.equals("41") ||
                    yiyaobaoStatus.equals("42")
            ) { //待发货
                status=0;

                statusName = "待发货";

                statusDTO.set_class("state-nfh");
                statusDTO.set_msg("商家待发货,请耐心等待");
                statusDTO.set_type("1");
                statusDTO.set_title("待发货");
                      *//*  if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*//*
            } else if(yiyaobaoStatus.equals("43")){ //待收货
                status=1;

                statusName = "待收货";

                statusDTO.set_class("state-ysh");
                statusDTO.set_msg("服务商已发货");
                statusDTO.set_type("2");
                statusDTO.set_title("待收货");
                        *//*paid=1;
                        if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*//*
            } else if (yiyaobaoStatus.equals("50") || yiyaobaoStatus.equals("45")){  // 已收货，待评价
                status=4;


                // 判断是否已经评价过

                if(isReplyFlag) { // 待评价
                    statusName = "已完成";
                    statusDTO.set_class("state-ypj");
                    statusDTO.set_msg("已收货,快去评价一下吧");
                    statusDTO.set_type("3");
                    statusDTO.set_title("待评价");
                } else {  // 已完成
                    statusName = "已完成";
                    statusDTO.set_class("state-ytk");
                    statusDTO.set_msg("交易完成,感谢您的支持");
                    statusDTO.set_type("4");
                    statusDTO.set_title("交易完成");
                }

                        *//*paid=1;
                        if(orderPartInfoVo.getPayTime() != null) {
                            Timestamp timestamp = orderPartInfoVo.getPayTime();
                            payTime = OrderUtil.dateToTimestamp(timestamp);
                        }*//*
            }else {  // 其他状态
                status=6;

                statusName = orderVo.getStatus();
                statusDTO.set_class("state-ytk");
                statusDTO.set_msg(orderVo.getStatus());
                statusDTO.set_type("3");
                statusDTO.set_title("交易完成");
            }
            yxStoreOrderQueryVo.setStatusName(statusName);
            yxStoreOrderQueryVo.setStatus(status);
            yxStoreOrderQueryVo.setPaid(paid);
            yxStoreOrderQueryVo.setRealName(orderVo.getReceiveName());
            yxStoreOrderQueryVo.setUserPhone(orderVo.getReceiveMobile());
            yxStoreOrderQueryVo.setUserAddress(orderVo.getAddress());
            yxStoreOrderQueryVo.setYiyaobaoOrderId(orderVo.getId());
            yxStoreOrderQueryVo.setDeliveryName(orderVo.getLogisticsName());
            yxStoreOrderQueryVo.setDeliveryId(orderVo.getFreightNo());
            yxStoreOrderQueryVo.setDeliveryType("express");



            *//*if(order.getPayType().equals("weixin")){
                statusDTO.set_payType("微信支付");
            }else if(order.getPayType().equals("慈善赠药")){
                statusDTO.set_payType("慈善赠药");
            }else{
                statusDTO.set_payType("余额支付");
            }*//*

            yxStoreOrderQueryVo.set_status(statusDTO);

            Integer insteadFlag = 0;
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("order_id",orderVo.getOrderNo());
            YxStoreOrder yxStoreOrder = this.getOne(queryWrapper,false);
            if(yxStoreOrder != null) {
                if(yxStoreOrder.getInsteadFlag() != null) {
                    insteadFlag = yxStoreOrder.getInsteadFlag();
                }
                yxStoreOrderQueryVo.setInsteadFlag(insteadFlag);
                yxStoreOrderQueryVo.setFactUserName(yxStoreOrder.getFactUserName());
                yxStoreOrderQueryVo.setFactUserPhone(yxStoreOrder.getFactUserPhone());
            }


            yxStoreOrderQueryVo.setCartInfo(cartInfo);
            return  yxStoreOrderQueryVo;
        }*/

    }



    private String uploadOrder2Yiyaobao(OrderParam param,YxUserAddressQueryVo userAddress,String yiyaobao_projectNo,String yiyaobao_store_id,String items){

        // 整理数据，发送至益药宝

        AddressDTO addressDTO = yiyaobaoOrderService.getAddressDTO(userAddress.getProvince(),userAddress.getCity(),userAddress.getDistrict());
        log.info(addressDTO.toString());

        // 图片base64
        //  String imgFilePath="http://d.hiphotos.baidu.com/image/pic/item/a044ad345982b2b713b5ad7d3aadcbef76099b65.jpg";

        String base64_str =  "";
        String imagePath = param.getImagePath();
        try {
            if(StrUtil.isBlank(imagePath)) {
                base64_str = new ImageUtil().localImageToBase64("otc.jpg");
            } else {
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
        prescriptionDTO.setAddress(userAddress.getDetail());
        prescriptionDTO.setCityCode(addressDTO.getCityCode());
        prescriptionDTO.setProvinceCode(addressDTO.getProvinceCode());
        prescriptionDTO.setDistrictCode(addressDTO.getDistrictCode());

        prescriptionDTO.setCustomerRequirement(param.getMark());
        prescriptionDTO.setPatientMobile(param.getPhone());
        prescriptionDTO.setPatientName(param.getRealName());

        prescriptionDTO.setProjectNo(yiyaobao_projectNo);
        prescriptionDTO.setSellerId(yiyaobao_store_id);

        // 生成短信验证码
       /* Future<String> r1 = yiyaobaoOrderService.generateVerifyCode(param.getPhone());

        String verifyCode = null;
        try {
            verifyCode = r1.get(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        prescriptionDTO.setVerifyCode(verifyCode);*/
        prescriptionDTO.setItems(items);
        log.info(JSONUtil.parseObj(prescriptionDTO).toString());
        prescriptionDTO.setImagePath(base64_str);

        // ImageUtil.toFileByBase64(prescriptionDTO.getImagePath());

        // 发送处方

        String orderSn = yiyaobaoOrderService.uploadOrder(prescriptionDTO);

        // 更新收货人姓名/收货人电话
        yiyaobaoOrderService.changeOrderReceiver(orderSn,userAddress.getRealName(),userAddress.getPhone(),param.getFactUserPhone());

        // 更新订单号
        if(StrUtil.isNotBlank(param.getOrderNo())) {
            yiyaobaoOrderService.changeOrderNo(orderSn,param.getOrderNo());
        }


        // 慈善赠药
        if( OrderInfoEnum.PAY_CHANNEL_2.getValue() == param.getIsChannel()) {

            yiyaobaoOrderService.checkPassPrescription(param.getOrderNo());
            orderSn = param.getOrderNo();
        }

        return orderSn;
    }

    @Override
    public void dualCSOrder(List<String> orderNoList){
        // 获取慈善订单
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("pay_type","慈善赠药");
        queryWrapper.in("order_id",
                orderNoList);
        List<YxStoreOrder> orderList = list(queryWrapper);
        for(YxStoreOrder order: orderList) {
            // 生成OrderParam param
            OrderParam param = new OrderParam();
           // param.setAddressId(userAddress.getId().toString());
            param.setIsChannel(OrderInfoEnum.PAY_CHANNEL_2.getValue());
            param.setBargainId(0);
            param.setCombinationId(0);
            param.setCouponId(0);
            param.setFrom(AppFromEnum.CSZY.getValue());
            param.setImagePath(order.getImagePath());
            param.setMark("");
            param.setPayType("慈善赠药");
            param.setPhone(order.getUserPhone());
            param.setPinkId(0);
            param.setRealName(order.getRealName());
            param.setSeckillId(0);
            param.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
            param.setStoreId(136);
            param.setOrderNo(order.getOrderId());
            param.setUseIntegral(0d);

            YxUserAddressQueryVo userAddress = new YxUserAddressQueryVo();
            List<String> addressList = Arrays.asList(order.getUserAddress().split(" "));
            String provinceName = addressList.get(0);
            String cityName = addressList.get(1);
            String districtName = addressList.get(2);
            String address = addressList.get(3);
            userAddress.setProvince(provinceName);
            userAddress.setCity(cityName);
            userAddress.setDistrict(districtName);
            userAddress.setDetail(address);

            // item
            JSONArray jsonArray = new JSONArray();
            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("sku","010507067");
            jsonObject.put("unitPrice",200);
            jsonObject.put("amount",2);

            jsonArray.add(jsonObject);
            String item = jsonArray.toString();

            String yiyaobao_projectNo = "202008170001";
            String yiyaobao_store_id = "187";

            String orderNo = uploadOrder2Yiyaobao(param,userAddress,yiyaobao_projectNo,yiyaobao_store_id,item);
            if(StrUtil.isNotBlank(orderNo)) {
                log.info("订单号{}导入益药宝成功",orderNo);
            } else {
                log.info("订单号{}导入益药宝错误",orderNo);
            }
        }

    }


    /**
     * 创建订单-项目
     * @param uid uid
     * @param key key
     * @param param param
     * @return
     */
    @Override
    public YxStoreOrder createOrder4Project(int uid, String key, OrderParam param) {
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        if(ObjectUtil.isNull(userInfo)) throw new ErrorRequestException("用户不存在");

        CacheDTO cacheDTO = getCacheOrderInfo(uid,key);
        if(ObjectUtil.isNull(cacheDTO)){
            throw new ErrorRequestException("订单已过期,请刷新当前页面");
        }

        List<YxStoreCartQueryVo> cartInfo = cacheDTO.getCartInfo();
       // Double totalPrice =  cacheDTO.getPriceGroup().getTotalPrice();
      //  Double payPrice = cacheDTO.getPriceGroup().getVipPrice();
       // Double payPostage = cacheDTO.getPriceGroup().getStorePostage();

        OtherDTO other = cacheDTO.getOther();


        log.info("OtherDTO={}",JSONUtil.parseObj(other));
        YxUserAddressQueryVo userAddress = null;
        if(OrderInfoEnum.SHIPPIING_TYPE_1.getValue().equals(param.getShippingType())){
            if(ProjectNameEnum.ROCHE_SMA.getValue().equals(other.getProjectCode()) && "2".equals(param.getAddressType()) ) {
                userAddress = new YxUserAddressQueryVo();
                userAddress.setRealName(param.getRealName());
                userAddress.setPhone(param.getPhone());
                userAddress.setProvince("");
                userAddress.setCity("");
                userAddress.setDistrict("");
                userAddress.setDetail("");
            } else {
                if(StrUtil.isEmpty(param.getAddressId())) {
                    throw new ErrorRequestException("请选择收货地址");
                }
                userAddress = userAddressService.getYxUserAddressById(param.getAddressId());
                if(ObjectUtil.isNull(userAddress)) {
                    throw new ErrorRequestException("地址选择有误");
                }
            }

        }else{ //门店
            if(StrUtil.isBlank(param.getRealName()) || StrUtil.isBlank(param.getPhone())) {
                throw new ErrorRequestException("请填写姓名和电话");
            }
            userAddress = new YxUserAddressQueryVo();
            userAddress.setRealName(param.getRealName());
            userAddress.setPhone(param.getPhone());
            userAddress.setProvince("");
            userAddress.setCity("");
            userAddress.setDistrict("");
            userAddress.setDetail("");
        }

        Integer totalNum = 0;
        Integer gainIntegral = 0;
        List<String> cartIds = new ArrayList<>();
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;
        //优惠券
        int couponId = 0;
        if(ObjectUtil.isNotEmpty(param.getCouponId())){
            couponId = param.getCouponId().intValue();
        }


        ComputeDTO computeDTO = new ComputeDTO();
        int useIntegral = param.getUseIntegral().intValue();
        if( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(other.getProjectCode()) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(other.getProjectCode()) || ProjectNameEnum.ZHONGANPUYAO.getValue().equals(other.getProjectCode()) || ProjectNameEnum.LINGYUANZHI.getValue().equals(other.getProjectCode())) {
            computeDTO = this.computedOrder(uid,key,
                    couponId,
                    useIntegral,
                    param.getShippingType(),param.getAddressId(),other.getProjectCode(),param.getCardNumber(),param.getExpressTemplateId());
        } else {


            BigDecimal totalPrice = new BigDecimal(cacheDTO.getPriceGroup().getTotalPrice());
            computeDTO = this.computedOrder4Project(uid,other.getProjectCode(),totalPrice,param.getAddressId(),param.getExpressTemplateId());
        }


        Double totalPrice = computeDTO.getTotalPrice();
        Double payPrice = computeDTO.getPayPrice();
        Double payPostage = computeDTO.getPayPostage();
        Double couponPrice = computeDTO.getCouponPrice();
        Double deductionPrice = computeDTO.getDeductionPrice();

        Double couponPrice_temp = couponPrice;

     //   JSONArray jsonArray = JSONUtil.createArray();
        for (YxStoreCartQueryVo cart : cartInfo) {
            int stock = productService.getProductStock(cart.getProductId(),cart.getProductAttrUnique());
            if(stock < cart.getCartNum()){
                throw new BadRequestException("["+ cart.getProductInfo().getStoreName() +"]"+"该产品库存不足"+cart.getCartNum());
            }

            combinationId = cart.getCombinationId();
            seckillId = cart.getSeckillId();
            bargainId = cart.getBargainId();
            cartIds.add(cart.getId().toString());
            totalNum += cart.getCartNum();
            //计算积分
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            if(combinationId == 0 && seckillId == 0 && bargainId == 0){//拼团等活动不参与积分
                if(cart.getProductInfo().getGiveIntegral().intValue() > 0){
                    cartInfoGainIntegral = NumberUtil.mul(cart.getCartNum(),cart.
                            getProductInfo().getGiveIntegral());
                }
                gainIntegral = NumberUtil.add(gainIntegral,cartInfoGainIntegral).intValue();
            }

          //  cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
          //  jsonObject.put("sku",cart.getYiyaobaoSku());
          //  jsonObject.put("unitPrice",cart.getTruePrice());
          //  jsonObject.put("amount",cart.getCartNum());

           // jsonArray.add(jsonObject);

            // 如果使用优惠券，5折类商品 用原价
            double discountAmount = 0;
            if("Y".equals(cart.getLabel3()) && param.getCouponId() >0 && ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(other.getProjectCode())){
                cart.setVipTruePrice(cart.getTruePrice());
                // 折扣金额=
                // 如果 商品原单价*数量 <= 整单折扣价 ,那么明细的折扣价 =  商品原单价*数量
                // 如果整单折扣价
                double amount = NumberUtil.mul(cart.getTruePrice().doubleValue(),cart.getCartNum().doubleValue());

                if( amount <= couponPrice_temp) {
                    discountAmount = amount;
                    couponPrice_temp = couponPrice_temp - amount;
                } else {
                    discountAmount =  couponPrice_temp;
                    couponPrice_temp=0d;
                }

            } else {
                // 没有用优惠券的情况，（折扣价= 原单价-会员价）* 数量
                discountAmount = (cart.getTruePrice() - cart.getVipTruePrice()) * cart.getCartNum();
            }

            cart.setDiscountAmount( BigDecimal.valueOf(discountAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
            cart.setDiscount(new BigDecimal(1));
        }







        boolean deduction = false;//拼团等
        //拼团等不参与抵扣
        if(combinationId > 0 || seckillId > 0 || bargainId > 0) {
            deduction = true;
        }
        if(deduction){
            couponId = 0;
            useIntegral = 0;
        }
       // double couponPrice = 0; //优惠券金额
        if(couponId > 0){//锁定优惠券
            couponUserService.useCoupon(couponId,couponPrice,5);//更新优惠券状态
        }

        // 积分抵扣
      //  double deductionPrice = 0; //抵扣金额
        double usedIntegral = 0; //使用的积分

        //积分抵扣开始
        if(useIntegral > 0 && userInfo.getIntegral().doubleValue() > 0){
            Double integralMax = Double.valueOf(cacheDTO.getOther().getIntegralMax());
            Double integralFull = Double.valueOf(cacheDTO.getOther().getIntegralFull());
            Double integralRatio = Double.valueOf(cacheDTO.getOther().getIntegralRatio());
            if(totalPrice >= integralFull){
                Double userIntegral = userInfo.getIntegral().doubleValue();
                if(integralMax > 0 && userIntegral >= integralMax) {
                    userIntegral = integralMax;
                }
               // deductionPrice = NumberUtil.mul(userIntegral, integralRatio);
                if(deductionPrice < payPrice){
                  //  payPrice = NumberUtil.sub(payPrice.doubleValue(),deductionPrice);
                    usedIntegral = userIntegral;
                }else{
                  //  deductionPrice = payPrice;
                    usedIntegral = NumberUtil.div(payPrice,
                            Double.valueOf(cacheDTO.getOther().getIntegralRatio()));
                  //  payPrice = 0d;
                }
                userService.decIntegral(uid,usedIntegral);
                //积分流水
                YxUserBill userBill = new YxUserBill();
                userBill.setUid(uid);
                userBill.setTitle("积分抵扣");
                userBill.setLinkId(key);
                userBill.setCategory("integral");
                userBill.setType("deduction");
                userBill.setNumber(BigDecimal.valueOf(usedIntegral));
                userBill.setBalance(userInfo.getIntegral());
                userBill.setMark("购买商品使用");
                userBill.setStatus(1);
                userBill.setPm(0);
                userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
                billService.save(userBill);
            }


        }

       // if(payPrice <= 0) payPrice = 0d;

/*

        if( PayTypeEnum.YUE.getValue().equals(param.getPayType()) && userInfo.getNowMoney().doubleValue() < payPrice.doubleValue()){
            throw new ErrorRequestException("余额不足");
        }
*/


        //生成分布式唯一值
        String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        if(!onlineFlag) {  // 测试环境的情况，单号拼上_TS
            orderSn = orderSn + "_TS";
        }
       // String orderSn = OrderUtil.generateOrderNoByUUId16();
        //组合数据
        YxStoreOrder storeOrder = new YxStoreOrder();
        storeOrder.setProjectCode(other.getProjectCode());
        storeOrder.setStoreId(other.getStoreId());
        storeOrder.setOriginalOrderNo(other.getOriginalOrderNo());
        storeOrder.setUid(uid);
        storeOrder.setOrderId(orderSn);
        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince()+" "+userAddress.getCity()+
                " "+userAddress.getDistrict()+" "+userAddress.getDetail());
        storeOrder.setCartId(StrUtil.join(",",cartIds));
        storeOrder.setTotalNum(totalNum);
        storeOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));
        storeOrder.setTotalPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(BigDecimal.valueOf(couponPrice));
        storeOrder.setPayPrice(BigDecimal.valueOf(payPrice));
        storeOrder.setPayPostage(BigDecimal.valueOf(payPostage));
        storeOrder.setDeductionPrice(BigDecimal.valueOf(deductionPrice));
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
        storeOrder.setPrescriptionFlag(other.getNeedImageFlag());
        storeOrder.setPayType(param.getPayType());
        storeOrder.setUseIntegral(BigDecimal.valueOf(usedIntegral));
        storeOrder.setGainIntegral(BigDecimal.valueOf(gainIntegral));
        storeOrder.setMark(param.getMark());
        storeOrder.setCombinationId(combinationId);
        storeOrder.setPinkId(param.getPinkId());
        storeOrder.setSeckillId(seckillId);
        storeOrder.setBargainId(bargainId);
        storeOrder.setCost(BigDecimal.valueOf(cacheDTO.getPriceGroup().getCostPrice()));
        if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_1.getValue());
        }else if(AppFromEnum.WEIXIN_H5.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_4.getValue());
        }else if(AppFromEnum.WECHAT.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_0.getValue());
        }else if(AppFromEnum.ali_h5.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_5.getValue());
        }else if(AppFromEnum.ali_miniapp.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_6.getValue());
        }else if(AppFromEnum.zhongan_miniapp.getValue().equals(param.getFrom())){
            storeOrder.setIsChannel(OrderInfoEnum.PAY_CHANNEL_7.getValue());
        }


        storeOrder.setAddTime(OrderUtil.getSecondTimestampTwo());
        storeOrder.setUnique(key);
        storeOrder.setShippingType(param.getShippingType());
        //处理门店
        if(OrderInfoEnum.SHIPPIING_TYPE_2.getValue().equals(param.getShippingType())){
            YxSystemStoreQueryVo systemStoreQueryVo = systemStoreService.getYxSystemStoreById(param.getStoreId());
            if(systemStoreQueryVo == null ) {
                throw new ErrorRequestException("暂无门店无法选择门店自提");
            }
            storeOrder.setVerifyCode(StrUtil.sub(orderSn,orderSn.length(),-12));
            storeOrder.setStoreId(systemStoreQueryVo.getId());
        }

        storeOrder.setImagePath(param.getImagePath());

        storeOrder.setInsteadFlag(param.getInsteadFlag());
        storeOrder.setNeedCloudProduceFlag(param.getNeedCloudProduceFlag());
        storeOrder.setNeedInternetHospitalPrescription(param.getNeedInternetHospitalPrescription());
        storeOrder.setCardNumber(other.getCardNumber());
        storeOrder.setCardType(other.getCardType());
        storeOrder.setRefereeCode(other.getRefereeCode());
        storeOrder.setPartnerCode(other.getPartnerCode());
        if(StrUtil.isNotBlank(param.getAddressId())) {
            storeOrder.setAddressId( Integer.valueOf( param.getAddressId()));
        }

        // 更新用药人信息
        if(StrUtil.isNotBlank(param.getDrugUserId()) && other.getNeedImageFlag()) {
            YxDrugUsers yxDrugUsers = yxDrugUsersService.getById( Integer.valueOf(param.getDrugUserId()));
            if(yxDrugUsers != null) {
                storeOrder.setDrugUserId(yxDrugUsers.getId());
                storeOrder.setDrugUserName(yxDrugUsers.getName());
                storeOrder.setDrugUserPhone(yxDrugUsers.getPhone());
                storeOrder.setDrugUserBirth(yxDrugUsers.getBirth());
                storeOrder.setDrugUserIdcard(yxDrugUsers.getIdcard());
                storeOrder.setDrugUserSex(yxDrugUsers.getSex());
                storeOrder.setDrugUserWeight(yxDrugUsers.getWeight());
                storeOrder.setDrugUserType(yxDrugUsers.getUserType());
            }

        }
       // 是否开票
        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(other.getProjectCode())) {
            storeOrder.setNeedInvoiceFlag(1);
            storeOrder.setInvoiceName(storeOrder.getDrugUserName());
            storeOrder.setInvoiceMail("");
        } else {
            storeOrder.setNeedInvoiceFlag(param.getNeedInvoiceFlag());
            storeOrder.setInvoiceName(param.getInvoiceName());
            storeOrder.setInvoiceMail(param.getInvoiceMail());
        }


        // 更新订单的收货省市区地址
        storeOrder.setProvinceName(userAddress.getProvince());
        storeOrder.setCityName(userAddress.getCity());
        storeOrder.setDistrictName(userAddress.getDistrict());
        storeOrder.setAddress(userAddress.getDetail());

        // 益药宝下发标记
        storeOrder.setUploadYiyaobaoFlag(0);
        storeOrder.setDemandId(other.getDemandId());

        // 云配液收货地址
        storeOrder.setCloudProduceAddress(param.getCloudProduceAddress());
        storeOrder.setRocheHospitalName(param.getRocheHospitalName());
        storeOrder.setPayeeAccountName("");
        storeOrder.setPayeeBankName("");
        storeOrder.setPayeeBankAccount("");
        storeOrder.setPayerAccountName("");
        storeOrder.setPayerVoucherImage("");

        if(param.getInsteadFlag() !=null && param.getInsteadFlag() == 1) { // 替别人下单
            storeOrder.setFactUserPhone(param.getPhone());
            storeOrder.setFactUserName(param.getRealName());
            YxUser yxUser = userService.getOne(new QueryWrapper<YxUser>().eq("phone",param.getPhone()).last("limit 1"),false);
            if(yxUser != null){
                storeOrder.setFactUserId(yxUser.getUid());
            }
        } else {

            if(StrUtil.isNotBlank(userInfo.getRealName()) ) {
                storeOrder.setFactUserName(userInfo.getRealName());
            } else {
                storeOrder.setFactUserName(userInfo.getNickname());
            }


            if(StrUtil.isNotBlank(userInfo.getPhone()) ) {
                storeOrder.setFactUserPhone(userInfo.getPhone());
            }else {
                storeOrder.setFactUserPhone(userInfo.getYaoshiPhone());
            }
            storeOrder.setFactUserId(uid);

        }



  /*      if( PayTypeEnum.WEIXIN.getValue().equals(storeOrder.getPayType()) || PayTypeEnum.ALIPAY.getValue().equals(storeOrder.getPayType()) || PayTypeEnum.OFFLINE.getValue().equals(storeOrder.getPayType()) || PayTypeEnum.YUE.getValue().equals(storeOrder.getPayType()) || PayTypeEnum.ZhongAnPay.getValue().equals(storeOrder.getPayType()) ) {
            // 先付款后审方，待支付
            storeOrder.setStatus(OrderStatusEnum.STATUS_0.getValue());
        } else {
            // 待下发erp
            storeOrder.setStatus(OrderStatusEnum.STATUS_15.getValue());
        }*/

        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(storeOrder.getProjectCode()) ) {
            storeOrder.setStoreId(null); // 罗氏罕见病项目下单时，药房信息不保存，后期药师手动选择药房
            storeOrder.setStatus(OrderStatusEnum.STATUS_5.getValue());
        }else if(PayTypeEnum.SMS.getValue().equals(storeOrder.getPayType())) {
            // 待下发erp
            storeOrder.setStatus(OrderStatusEnum.STATUS_15.getValue());
        }else{
            // 先付款后审方，待支付
            storeOrder.setStatus(OrderStatusEnum.STATUS_0.getValue());
        }

        // 没有处方照片的，状态改为 处方待上传
        if( StrUtil.isBlank(storeOrder.getImagePath()) && other.getNeedImageFlag() && (storeOrder.getNeedInternetHospitalPrescription() == null || storeOrder.getNeedInternetHospitalPrescription() == 0)) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_14.getValue());
        }
     /*   else if( storeOrder.getNeedInternetHospitalPrescription() != null && storeOrder.getNeedInternetHospitalPrescription() == 1) {
            storeOrder.setStatus(OrderStatusEnum.STATUS_10.getValue());
        }*/

        if ( ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(other.getProjectCode()))  {
             // 更新太平订单号
             storeOrder.setTaipingOrderNumber(other.getOriginalOrderNo());
        }

        if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(other.getProjectCode()) || ProjectNameEnum.ZHONGANPUYAO.getValue().equals(other.getProjectCode()) || ProjectNameEnum.LINGYUANZHI.getValue().equals(other.getProjectCode())) {
            storeOrder.setCardNumber(userInfo.getZhonganCardNumber());
        }



        storeOrder.setAddressType(param.getAddressType());


        storeOrder.setPayOutTradeNo(orderSn+storeOrder.getAddTime());
        log.info("storeOrder==={}",JSONUtil.parseObj(storeOrder));
        boolean res = save(storeOrder);
        if(!res) {
            throw new ErrorRequestException("订单生成失败");
        }

        //减库存加销量
        for (YxStoreCartQueryVo cart : cartInfo) {
            if(combinationId > 0){
                combinationService.decStockIncSales(cart.getCartNum(),combinationId);
            }else if(seckillId > 0){
                storeSeckillService.decStockIncSales(cart.getCartNum(),seckillId);
            }else if(bargainId > 0){
                storeBargainService.decStockIncSales(cart.getCartNum(),bargainId);
            } else {
                productService.decProductStock(cart.getCartNum(),cart.getProductId(),
                        cart.getProductAttrUnique());
            }

        }

        //保存购物车商品信息
        orderCartInfoService.saveCartInfo(storeOrder.getId(),cartInfo);

        //购物车状态修改
        QueryWrapper<YxStoreCart> wrapper = new QueryWrapper<>();
        wrapper.in("id",cartIds);
        YxStoreCart cartObj = new YxStoreCart();
        cartObj.setIsPay(1);
        storeCartMapper.update(cartObj,wrapper);

        //删除缓存
        delCacheOrderInfo(uid,key);

        //增加状态
        orderStatusService.create(storeOrder.getId(),"cache_key_create_order","订单生成");

       // 如果是互联网医院的需求单转换的订单，更新互联网需求单上的订单编号

        if(other.getDemandId() != null) {
            InternetHospitalDemand demand = internetHospitalDemandService.getById( other.getDemandId());
            demand.setIsUse(1);
            demand.setOrderId(storeOrder.getOrderId());
            internetHospitalDemandService.updateById(demand);
        }
        //使用MQ延时消息
        //mqProducer.sendMsg("yshop-topic",storeOrder.getId().toString());
        //log.info("投递延时订单id： [{}]：", storeOrder.getId());

        //加入redis，30分钟自动取消
        /*String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNPAY, storeOrder.getId()));
        redisTemplate.opsForValue().set(redisKey, storeOrder.getOrderId() ,
                ShopConstants.ORDER_OUTTIME_UNPAY, TimeUnit.MINUTES);*/


        // 将订单发送至益药宝


        if (ProjectNameEnum.ROCHE_SMA.getValue().equals(storeOrder.getProjectCode())  ) {// 罗氏罕见病的订单，等待处理，暂不发益药宝
               // 更新订单号到电子签名表
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("order_no",storeOrder.getOrderId());
            updateWrapper.eq("order_key",storeOrder.getUnique());
            userAgreementService.update(updateWrapper);

            YxSystemStore yxSystemStore = systemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getId,other.getStoreId()).select(YxSystemStore::getLinkPhone),false);
            if( yxSystemStore != null && StringUtils.isNotBlank(yxSystemStore.getLinkPhone())){
                List<String> phoneList= Arrays.asList(yxSystemStore.getLinkPhone().split(",")) ;
                //发送短信
                for(String phone : phoneList) {
                    String remindmessage = "【益药】您有新的[艾满欣]订单待处理，订单状态：%s。订单编号：%s";
                    remindmessage = String.format(remindmessage, "待审核", storeOrder.getOrderId());
                    smsService.sendTeddy("",remindmessage,phone);
                }
            }

        } else if(PayTypeEnum.SMS.getValue().equals(storeOrder.getPayType())) { // 短信支付的订单，发送至益药宝门店


            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.put("orderNo",storeOrder.getOrderId());
            jsonObject.put("desc","生成订单" );
            jsonObject.put("projectCode",storeOrder.getProjectCode());
            jsonObject.put("time", DateUtil.now());
            mqProducer.sendDelayQueue(bizRoutekeyYiyaobao,jsonObject.toString(),2000);

            log.info("投递延时订单编号：[{}],队列名称：{},msg:{}", storeOrder.getOrderId(),storeOrder.getProjectCode(),bizRoutekeyYiyaobao,jsonObject.toString());

        }
        return storeOrder;
    }

    public void sendOrder2YiyaobaoCloud(String orderId){
        yiyaobaoOrderService.sendOrder2YiyaobaoCloud(orderId);
    }

    public void sendOrder2YiyaobaoStore(String orderId) {
        yiyaobaoOrderService.sendOrder2YiyaobaoStore(orderId);
    }

    public void sendOrder2YiyaobaoCloudCancel(String orderId){
        yiyaobaoOrderService.sendOrder2YiyaobaoCloudCancel(orderId);
    }

    @Override
    public void updatePayVoucher(OrderParam param) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("payer_account_name",param.getPayerAccountName());
        updateWrapper.set("payer_voucher_image",param.getPayerVoucherImage());
        updateWrapper.set("status",OrderStatusEnum.STATUS_11.getValue());
        updateWrapper.set("paid",OrderInfoEnum.PAY_STATUS_1.getValue());
        updateWrapper.set("pay_type","offline");
        updateWrapper.set("pay_time",OrderUtil.getSecondTimestampTwo());
        updateWrapper.eq("order_id",param.getOrderNo());
        this.update(updateWrapper);

        //todo 推送

        DictDetailQueryParam dictDetailQueryParam = new DictDetailQueryParam();
        dictDetailQueryParam.setName("rocheManger");
        List<DictDetail> phoneList =  dictDetailService.getDictDetailList(dictDetailQueryParam);

        //发送短信
        for(DictDetail detail :phoneList) {
            if(StrUtil.isNotBlank(detail.getValue())) {
                String remindmessage = "【益药】您有订单待处理，订单状态：%s。订单编号：%s";
                remindmessage = String.format(remindmessage, "付款信息待确定", param.getOrderNo());
                smsService.sendTeddy("",remindmessage,detail.getValue());
            }
        }

        //增加状态
        YxStoreOrder yxStoreOrder = this.getOne(new LambdaQueryWrapper<YxStoreOrder>().eq(YxStoreOrder::getOrderId,param.getOrderNo()).select(YxStoreOrder::getId),false);
        if(yxStoreOrder != null) {
            orderStatusService.create(yxStoreOrder.getId(),OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getValue(),OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getDesc());
        }


    }

    @Override
    public String getHpUrl(String orderId) {


        AttrDTO attrDTO = new AttrDTO();

        attrDTO.setUid(SecurityUtils.getUserId().intValue());
        attrDTO.setOrderId(orderId);

        LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,attrDTO.getOrderId());
        lambdaQueryWrapper.select(YxStoreOrder::getCardNumber,YxStoreOrder::getCardType,YxStoreOrder::getProjectCode,YxStoreOrder::getDrugUserId,YxStoreOrder::getId);
        YxStoreOrder yxStoreOrder = yxStoreOrderMapper.selectOne(lambdaQueryWrapper);

        attrDTO.setCardType(yxStoreOrder.getCardType());
        attrDTO.setCardNumber(yxStoreOrder.getCardNumber());
        attrDTO.setProjectCode(yxStoreOrder.getProjectCode());
        attrDTO.setDrugUserid(yxStoreOrder.getDrugUserId());
        attrDTO.setId(yxStoreOrder.getId());
        log.info(" 获取 互联网医院开始=====");
        String result = xkProcessService.h5Url4ApplyPrescription(attrDTO);

        return result;
    }

    @Override
    public YxStoreOrder addOrder4Project(Order4ProjectParam order4ProjectParam) {
        // 1.添加购物车
        int isNew = 0;
        int combinationId = 0;
        int seckillId = 0;
        int bargainId = 0;
        String departmentCode = "";
        String partnerCode = "";
        String refereeCode = order4ProjectParam.getRefereeCode();
        String projectCode = order4ProjectParam.getProjectCode();
        int uid = order4ProjectParam.getUid();
        List<Integer> cartIdList = new ArrayList<>();
        List<YxStoreCartQueryVo> cartInfo = new ArrayList<>();
        for( OrderDetail4ProjectParam detail : order4ProjectParam.getDetails()) {
           int cartId = storeCartService.addCart(uid,detail.getProductId(),detail.getNum(),detail.getProductUniqueId()
                    ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode);
           cartIdList.add(cartId);

           YxStoreCart storeCart = storeCartMapper.selectById(cartId);
           YxStoreCartQueryVo storeCartQueryVo = cartMap.toDto(storeCart);

            YxStoreProductAttrValue productAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,detail.getProductUniqueId()));
            BigDecimal price = new BigDecimal(0);
           YxStoreProductQueryVo storeProduct = productService.getNewStoreProductById(detail.getProductId());
            if(StrUtil.isNotBlank(projectCode)) {
                LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Product4project::getProductUniqueId,detail.getProductUniqueId());
                lambdaQueryWrapper.eq(Product4project::getIsDel,0);
                lambdaQueryWrapper.eq(Product4project::getProjectNo,projectCode);
                Product4project product4project = product4projectService.getOne(lambdaQueryWrapper,false);
                 price = product4project.getUnitPrice();
                 if(price == null) {
                     price = productAttrValue.getPrice();
                 }
            } else {
                price = productAttrValue.getPrice();
            }

            storeProduct.setPrice(price);
            storeProduct.setStoreNameReal(productAttrValue.getSuk());
            storeProduct.setAttrInfo(productAttrValue);
            storeCartQueryVo.setProductInfo(storeProduct);
            // 设置商品价格（会员价）
            storeCartQueryVo.setVipTruePrice(price.doubleValue());
            //设置商品价格（原价）
            storeCartQueryVo.setTruePrice(price.doubleValue());
            storeCartQueryVo.setCostPrice(price.doubleValue());
            storeCartQueryVo.setTrueStock(productAttrValue.getStock());
            storeCartQueryVo.setYiyaobaoSku(storeProduct.getYiyaobaoSku());
            storeCartQueryVo.setProductAttrUnique(productAttrValue.getUnique());


            cartInfo.add(storeCartQueryVo);

        }


        // 2.confirm订单

        PriceGroupDTO priceGroup = this.getOrderPriceGroup(cartInfo);
        OtherDTO other = new OtherDTO();
        other.setIntegralRatio(systemConfigService.getData("integral_ratio"));
        other.setIntegralFull(systemConfigService.getData("integral_full"));
        other.setIntegralMax(systemConfigService.getData("integral_max"));

        other.setStoreId(order4ProjectParam.getStoreId());
        other.setStoreName(order4ProjectParam.getStoreName());
        other.setProjectCode(projectCode);
        other.setPartnerCode(partnerCode);
        other.setRefereeCode(refereeCode);
        other.setDepartCode("");
        other.setOriginalOrderNo(order4ProjectParam.getOriginalOrderNo());
        other.setNeedImageFlag(true);
        String orderKey = this.cacheOrderInfo(uid,cartInfo,
                priceGroup,other);

        // 3.创建订单

        OrderParam param = new OrderParam();
        param.setProjectCode(projectCode);
        param.setStoreId(order4ProjectParam.getStoreId());
        param.setShippingType(1);
        param.setMark(order4ProjectParam.getMark());
        param.setAddressId(order4ProjectParam.getAddressId());
        param.setDrugUserId(order4ProjectParam.getDrugUserid());
        param.setImagePath(order4ProjectParam.getImagePath());
        param.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
        param.setUseIntegral(0d);
        param.setPinkId(0);
        param.setBargainId(0);
        param.setSeckillId(0);
        param.setCouponId(0);
        param.setFrom(AppFromEnum.ROUNTINE.getValue());
        param.setPayType(order4ProjectParam.getPayType());
        YxStoreOrder order =  this.createOrder4Project(uid,orderKey,param);


        return order;
    }

    @Override
    public ComputeDTO computedOrder4Project(int uid, String projectCode,BigDecimal totalPrice,String addressId,Integer expressTemplateId) {
        ComputeDTO computeDTO = new ComputeDTO();
        BigDecimal  payPostage = new BigDecimal(0);
        computeDTO.setCouponPrice(0d);
        computeDTO.setDeductionPrice(0d);
        computeDTO.setTotalPrice(totalPrice.doubleValue());
        computeDTO.setPayPostage(payPostage.doubleValue());
        computeDTO.setPayPrice(NumberUtil.add(totalPrice,payPostage).doubleValue());

        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getProjectCode,projectCode);
        Project project = projectService.getOne(lambdaQueryWrapper);
        if(project == null) {
            return computeDTO;
        }
        // 满多少金额免邮
        YxUserAddressQueryVo  userAddress = userAddressService.getYxUserAddressById(addressId);
        if(userAddress == null) {
            return computeDTO;
        }
        String province = userAddress.getProvince();
        String city = userAddress.getCity();
        Boolean isFreePostage = false;

        LambdaQueryWrapper<ProjectSalesArea> lambdaQueryWrapper1 = new LambdaQueryWrapper();
        lambdaQueryWrapper1.eq(ProjectSalesArea::getProjectCode,projectCode);
        lambdaQueryWrapper1.eq(ProjectSalesArea::getAreaName,province);
        ProjectSalesArea projectSalesArea = projectSalesAreaService.getOne(lambdaQueryWrapper1,false);
        if(projectSalesArea == null) {
            return computeDTO;
        }
        if(projectSalesArea.getIsFree()!=null && projectSalesArea.getIsFree() == 1 ) {
            if (NumberUtil.isGreaterOrEqual(totalPrice, new BigDecimal(projectSalesArea.getFreePostage()))) {  // 商品总金额 >= 免邮金额
                isFreePostage = true;
            }
        }

        if(isFreePostage) {
            payPostage = new BigDecimal(0);
        } else { // 按照地区计算邮费
            Integer expressId = null;
            if(ObjectUtil.isNull(expressTemplateId) && StrUtil.isNotBlank(project.getExpressTemplateId())) {
                expressId = Integer.valueOf(Arrays.asList(project.getExpressTemplateId().split(",")).get(0));

            }else {
                expressId = expressTemplateId;
            }
            YxExpressTemplate yxExpressTemplate = yxExpressTemplateService.getById(expressId);
            if(ObjectUtil.isNotEmpty(yxExpressTemplate)) {
                // 先找 城市邮费
                YxExpressTemplateDetail yxExpressTemplateDetail =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", city).eq("level",2),false);
                if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail)) {
                    payPostage = yxExpressTemplateDetail.getPrice();
                } else {
                    // 再找 省份邮费
                    YxExpressTemplateDetail yxExpressTemplateDetail_province =  yxExpressTemplateDetailService.getOne(new QueryWrapper<YxExpressTemplateDetail>().eq("template_id",yxExpressTemplate.getId()).eq("area_name", province).eq("level",1),false);
                    if(ObjectUtil.isNotEmpty(yxExpressTemplateDetail_province)) {
                        payPostage = yxExpressTemplateDetail_province.getPrice();
                    } else {
                        payPostage = new BigDecimal(0);
                    }

                }
            }
        }

        computeDTO.setPayPostage(payPostage.doubleValue());
        computeDTO.setPayPrice(NumberUtil.add(totalPrice,payPostage).doubleValue());

        return computeDTO;
    }

    @Override
    public String getRocheHospitalName(int uid) {
        return yxStoreOrderMapper.getRocheHospitalName(uid);
    }

    @Override
    public YxStoreOrder findByUidAndOriginalOrderNo(int uid, String orderNumber) {
        return yxStoreOrderMapper.findByUidAndOriginalOrderNo(uid,orderNumber);
    }

    @Override
    public String aliPayH5Pay(String orderId) {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }
        String appid="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getAlipayHfiveAppid())){
            appid=product.getAlipayHfiveAppid();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getAlipayHfiveAppid())){
                appid=yxSystemStoreQueryVo.getAlipayHfiveAppid();
            }
        }
        if(StringUtils.isEmpty(appid)){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("type","2");
            wrapper.eq("is_default",1);
            wrapper.eq("delete_flag",0);
            AlipayConfiguration alipayConfiguration = alipayConfigurationService.getOne(wrapper);
            appid=alipayConfiguration.getAppId();
        }
        return alipayConfigurationService.alipayH5Pay("支付宝支付", "上海益药药业", orderInfo.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(orderInfo.getPayPrice()),appid,orderId+"");
    }

    @Override
    public String alipayTradeAppPay(String orderId,String userid) {
        YxStoreOrderQueryVo orderInfo = getOrderInfo(orderId,0);
        if(ObjectUtil.isNull(orderInfo)) {
            throw new ErrorRequestException("订单不存在");
        }
        if(orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new ErrorRequestException("该订单已支付");
        }

        if(orderInfo.getPayPrice().doubleValue() <= 0) {
            throw new ErrorRequestException("该支付无需支付");
        }
        String appid="";
        Project product=projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,orderInfo.getProjectCode()),false);
        if(product!=null && StringUtils.isNotEmpty(product.getAlipayAppletAppid())){
            appid=product.getAlipayAppletAppid();
        }else{
            YxSystemStoreQueryVo yxSystemStoreQueryVo= systemStoreService.getYxSystemStoreById(orderInfo.getStoreId());
            if(yxSystemStoreQueryVo!=null && StringUtils.isNotEmpty(yxSystemStoreQueryVo.getAlipayAppletAppid())){
                appid=yxSystemStoreQueryVo.getAlipayAppletAppid();
            }
        }
        if(StringUtils.isEmpty(appid)){
            QueryWrapper<AlipayConfiguration> wrapper = new QueryWrapper<>();
            wrapper.eq("type","1");
            wrapper.eq("is_default",1);
            wrapper.eq("delete_flag",0);
            AlipayConfiguration alipayConfiguration = alipayConfigurationService.getOne(wrapper);
            appid=alipayConfiguration.getAppId();
        }
        return alipayConfigurationService.alipayTradeAppPay("支付宝支付", userid, orderInfo.getPayOutTradeNo(), "30m", new DecimalFormat("0.00").format(orderInfo.getPayPrice()),appid);
    }

    @Override
    @Transactional
    public void updateOrderById(YxStoreOrder storeOrder) {
        updateById(storeOrder);
    }

    @Override
    public List<PayDTO> queryPayType(String projectCode, String contextParam) {
        List<PayDTO> payDTOS = new ArrayList<>();
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(Project::getProjectCode,projectCode);
        projectLambdaQueryWrapper.select(Project::getPayType);
        Project project = projectService.getOne(projectLambdaQueryWrapper,false);
        if(ContextParamEnum.context_1.getValue().equals(contextParam)) {

        }
        return null;
    }
}
