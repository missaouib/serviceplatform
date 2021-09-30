package co.yixiang.modules.zhongan;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.mapper.YxStoreOrderCartInfoMapper;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
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
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ZhongAnPuYaoServiceImpl {

    @Autowired
    private YxStoreOrderMapper orderMapper;

    @Autowired
    private YxStoreOrderCartInfoMapper yxStoreOrderCartInfoMapper;

    @Value("${zhonganpuyao.appKey}")
    private String appKey;

    @Value("${zhonganpuyao.url}")
    private String url;

    @Value("${zhonganpuyao.version}")
    private String version;

    @Value("${zhonganpuyao.publicKey}")
    private String publicKey;

    @Value("${zhonganpuyao.privateKey}")
    private String privateKey;


    @Autowired
    private RestTemplate restTemplate;

    // @Async
    public void sendOrderInfo(String orderNo){
        log.info("推送众安普药订单：{}",orderNo);
        LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);

        YxStoreOrder yxStoreOrder = orderMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtil.isNotNull(yxStoreOrder)) {
            // 收货地址信息
            DeliveryAddressDetail addressDetail = new DeliveryAddressDetail();
            addressDetail.setAddressDetail(yxStoreOrder.getAddress());
            addressDetail.setCityName(yxStoreOrder.getCityName());
            addressDetail.setProvinceName(yxStoreOrder.getProvinceName());
            addressDetail.setCountryName(yxStoreOrder.getDistrictName());
            addressDetail.setContactUserName(yxStoreOrder.getRealName());
            addressDetail.setContactUserPhone(yxStoreOrder.getUserPhone());

            // 优惠信息
            DiscountDetails discountDetails = new DiscountDetails();

            // 发票信息
            InvoiceDetail invoiceDetail = new InvoiceDetail();

            ResultOrder resultOrder = new ResultOrder();
            resultOrder.setPlatformCode("SYY");
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
            }else if(OrderStatusEnum.STATUS_5.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_10.getValue().equals(yxStoreOrder.getStatus()) || OrderStatusEnum.STATUS_13.getValue().equals(yxStoreOrder.getStatus())) { // 3：审核中
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
