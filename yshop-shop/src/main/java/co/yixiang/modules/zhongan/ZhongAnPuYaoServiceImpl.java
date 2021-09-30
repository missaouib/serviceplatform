package co.yixiang.modules.zhongan;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderStatusEnum;

import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.activity.domain.YxStoreCouponUser;
import co.yixiang.modules.activity.service.YxStoreCouponUserService;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import co.yixiang.modules.shop.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryVo;
import co.yixiang.modules.shop.service.mapper.StoreOrderCartInfoMapper;
import co.yixiang.modules.shop.service.mapper.StoreOrderMapper;
import co.yixiang.utils.OrderUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ZhongAnPuYaoServiceImpl {

    @Autowired
    private StoreOrderMapper orderMapper;

    @Autowired
    private StoreOrderCartInfoMapper yxStoreOrderCartInfoMapper;

    @Autowired
    private YxStoreCouponUserService couponUserService;

    @Value("${zhonganpuyao.appKey}")
    private String appKey;

    @Value("${zhonganpuyao.url}")
    private String url;

    @Value("${zhonganpuyao.version}")
    private String version;

    @Value("${zhonganpuyao.privateKey}")
    private String privateKey;


    @Autowired
    private RestTemplate restTemplate;

    // @Async
    public void sendOrderInfo(String orderNo){
        log.info("推送众安订单orderNo：{}",orderNo);
        LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);

        YxStoreOrder yxStoreOrder = orderMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtil.isNotNull(yxStoreOrder)) {
            log.info("推送众安订单：{}",JSONUtil.parseObj(yxStoreOrder).toString());

            // 收货地址信息
            DeliveryAddressDetail addressDetail = new DeliveryAddressDetail();
            addressDetail.setThirdDeliveryId(String.format("%08d", yxStoreOrder.getAddressId()));
            addressDetail.setAddressDetail(yxStoreOrder.getAddress());
            addressDetail.setCityName(yxStoreOrder.getCityName());
            addressDetail.setProvinceName(yxStoreOrder.getProvinceName());
            addressDetail.setCountryName(yxStoreOrder.getDistrictName());
            addressDetail.setContactUserName(yxStoreOrder.getRealName());
            addressDetail.setContactUserPhone(yxStoreOrder.getUserPhone());

            // 优惠信息
            List<DiscountDetails> discountDetails = new ArrayList<>();
            if(yxStoreOrder.getCouponId()!=null && yxStoreOrder.getCouponId()!=0){
                YxStoreCouponUser couponUser= couponUserService.getById(yxStoreOrder.getCouponId());
                if(couponUser!=null){
                    DiscountDetails details=new DiscountDetails();
                    details.setDiscountNo(couponUser.getCouponNo());
                    details.setDiscountName(couponUser.getCouponTitle());
                    details.setDiscountPrice(yxStoreOrder.getCouponPrice());
                    discountDetails.add(details);
                }
            }

            // 发票信息
            InvoiceDetail invoiceDetail = new InvoiceDetail();

            ResultOrder resultOrder = new ResultOrder();
            if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(yxStoreOrder.getProjectCode())) {
                resultOrder.setPlatformCode("SYY");
            } else if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(yxStoreOrder.getProjectCode())){
                resultOrder.setPlatformCode("SYYMB");
            }else{
                resultOrder.setPlatformCode("SYYXB");
            }

            resultOrder.setDeliveryAddressDetail(addressDetail);
            resultOrder.setExpressFee(yxStoreOrder.getTotalPostage().setScale(2, BigDecimal.ROUND_HALF_UP));
            resultOrder.setOrderRemark(yxStoreOrder.getRemark());
            resultOrder.setOrderAmount(yxStoreOrder.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            resultOrder.setDiscountDetails(discountDetails);
            resultOrder.setOrderRealAmount(yxStoreOrder.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            resultOrder.setInvoiceDetail(invoiceDetail);
            String status = "";
            String orderFinishTime = "";
            if(yxStoreOrder.getRefundStatus() == 2 ) { // 8：已退款
                status = "8";
                orderFinishTime = DateUtil.now();
            }else if(OrderStatusEnum.STATUS_5.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_10.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_13.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_15.getValue().equals(yxStoreOrder.getStatus())) { // 3：审核中
                status = "3";
            } else if(OrderStatusEnum.STATUS_6.getValue().equals(yxStoreOrder.getStatus())) {  // 4：审核失败
                status = "4";
            } else if(OrderStatusEnum.STATUS_0.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_9.getValue().equals(yxStoreOrder.getStatus())) { // 5：待发货
                status = "5";
            } else if(OrderStatusEnum.STATUS_1.getValue().equals(yxStoreOrder.getStatus())) {  // 6：已发货
                status = "6";
            } else if(OrderStatusEnum.STATUS_3.getValue().equals(yxStoreOrder.getStatus())) {  // 7：已完成
                status = "7";
                orderFinishTime = DateUtil.now();
            }
            resultOrder.setOrderStatus(status);
            resultOrder.setOrderFinishTime(orderFinishTime);
            // yyyy-MM-dd HH:mm:ss

            resultOrder.setOrderTime(OrderUtil.stampToDate(String.valueOf(yxStoreOrder.getAddTime())));
            resultOrder.setThirdOrderNo(yxStoreOrder.getOrderId());
            // 用户
            resultOrder.setUserId(yxStoreOrder.getCardNumber());
            resultOrder.setOrderType("drugs");
            List<OrderGoods> orderGoods = new ArrayList<>();

            QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("oid",yxStoreOrder.getId());
            List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoMapper.selectList(wrapper);
            for (YxStoreOrderCartInfo info : cartInfos) {
                YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
                int cartNum = cartQueryVo.getCartNum();
                String yiyaobaoSku =  cartQueryVo.getYiyaobaoSku();

                YxStoreProductQueryVo productQueryVo = cartQueryVo.getProductInfo();
                OrderGoods goods = new OrderGoods();
                goods.setGoodsCount(cartNum);
                goods.setGoodsSkuCode(yiyaobaoSku);
                goods.setGoodsPrice( new BigDecimal(cartQueryVo.getTruePrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                goods.setGoodsRealPrice(new BigDecimal(cartQueryVo.getTruePrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                goods.setGoodsApprovalNumber(productQueryVo.getLicenseNumber());
                goods.setGoodsCommonName(productQueryVo.getCommonName());
                goods.setGoodsDosageForm(productQueryVo.getDrugForm());
                goods.setGoodsName(productQueryVo.getStoreName());
                goods.setGoodsSpecification(productQueryVo.getSpec());
                goods.setGoodsPictureUrl(productQueryVo.getImage());
                orderGoods.add(goods);
            }


            resultOrder.setOrderGoods(orderGoods);

            ZhongAnRequest zhongAnRequest = new ZhongAnRequest();
            zhongAnRequest.setAppKey(appKey);
            zhongAnRequest.setCharset("utf-8");
            zhongAnRequest.setVersion(version);
            zhongAnRequest.setFormat("json");
            zhongAnRequest.setServiceName("za.hospital.drugs.order.sync");
            zhongAnRequest.setBizContent(JSONUtil.parseObj(resultOrder).toString());
            zhongAnRequest.setSignType("RSA2");
            zhongAnRequest.setTimestamp(DateUtil.format(DateUtil.date(),"yyyyMMddHHmmss"));

           /* String plaintext = SignUtils.genWithAmple(
                    "appKey="+zhongAnRequest.getAppKey(),"bizContent="+zhongAnRequest.getBizContent(),"charset="+zhongAnRequest.getCharset(),
                    "format="+zhongAnRequest.getFormat(),"serviceName="+zhongAnRequest.getServiceName(),"signType="+zhongAnRequest.getSignType(),
                    "timestamp="+zhongAnRequest.getTimestamp(),"version="+zhongAnRequest.getVersion()
                    );*/
            String plaintext = SignUtils.getSignContent(JSONUtil.parseObj(zhongAnRequest));
            String sign = "";
            log.info("众安普药报文：{}",plaintext);
            log.info("privateKey={}",privateKey);
            try {
                RSA2SignerDemo signerDemo = new RSA2SignerDemo();
                sign = signerDemo.sign(plaintext,"utf-8",privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }

            zhongAnRequest.setSign(sign);


            HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            // MultiValueMap map = new LinkedMultiValueMap();

            HttpEntity request = new HttpEntity(JSONUtil.parseObj(zhongAnRequest).toString(), headers);
            log.info("发送众安普药的消息:{}", JSONUtil.parseObj(zhongAnRequest));

            ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            String body = resultEntity.getBody();


            log.info("众安普药返回的消息:{}",body);


        }
    };
}
