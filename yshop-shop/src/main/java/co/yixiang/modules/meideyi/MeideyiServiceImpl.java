package co.yixiang.modules.meideyi;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryVo;
import co.yixiang.modules.shop.service.mapper.StoreOrderCartInfoMapper;
import co.yixiang.modules.shop.service.mapper.StoreOrderMapper;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.utils.OrderUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:30
 */
@Service
@Slf4j
public class MeideyiServiceImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StoreOrderMapper orderMapper;

    @Autowired
    private StoreOrderCartInfoMapper yxStoreOrderCartInfoMapper;

    private String submitOrderUrl = "/api/provider/v2/submitOrder?code=yy01";

    private String refundOrderUrl = "/api/provider/v2/refundOrder?code=yy01";

    @Value("${meideyi.url}")
    private String url ;

    @Autowired
    private MqProducer mqProducer;

    // 死信队列名称
    @Value("${spring.rabbitmq.deadQueueName}")
    private String deadQueueName;

    public void sendOrderInfo(String msg){

        String plainText = String.valueOf(System.currentTimeMillis());
        String sign = ProviderAES.encrypt(plainText,ProviderAES.SEED);
        String postUrl = "";
        JSONArray array = JSONUtil.createArray();
        JSONObject jsonObject = JSONUtil.parseObj(msg);
        String orderNo = jsonObject.getStr("orderNo");
        String status = jsonObject.getStr("status");
        String requestBody = "";
        if("11".equals(status)) { // 已支付，待审核状态
            postUrl = url + submitOrderUrl + "&time="+ sign;
            MeideyiPaidOrder meideyiPaidOrder = new MeideyiPaidOrder();

            LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);

            YxStoreOrder yxStoreOrder = orderMapper.selectOne(lambdaQueryWrapper);
            if(ObjectUtil.isNotNull(yxStoreOrder)) {
                meideyiPaidOrder.setMobile(yxStoreOrder.getFactUserPhone());
                meideyiPaidOrder.setOrderCode(yxStoreOrder.getOrderId());
                meideyiPaidOrder.setOrderId(yxStoreOrder.getOrderId());
                meideyiPaidOrder.setOrderTime(OrderUtil.stampToDate(String.valueOf(yxStoreOrder.getAddTime())));
                meideyiPaidOrder.setProviderCode("yy01");
                meideyiPaidOrder.setProviderName("益药商城");
                meideyiPaidOrder.setStatus("11");
                meideyiPaidOrder.setTotalPrice(yxStoreOrder.getPayPrice().toString());
                meideyiPaidOrder.setUserId(yxStoreOrder.getCardNumber());
                List<MeideyiDrug> meideyiDrugList= new ArrayList<>();
                Integer totalNum = 0;
                // 获取商品明细
                QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
                wrapper.eq("oid",yxStoreOrder.getId());
                List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoMapper.selectList(wrapper);
                for (YxStoreOrderCartInfo info : cartInfos) {
                    YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
                    int cartNum = cartQueryVo.getCartNum();
                    String yiyaobaoSku =  cartQueryVo.getYiyaobaoSku();

                    YxStoreProductQueryVo productQueryVo = cartQueryVo.getProductInfo();
                    MeideyiDrug goods = new MeideyiDrug();
                    goods.setDrugCode(cartQueryVo.getProductId().toString());
                    goods.setDrugName(productQueryVo.getCommonName());
                    goods.setDrugSpec(productQueryVo.getSpec());
                    goods.setPrice(new BigDecimal(cartQueryVo.getTruePrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    goods.setQuantity(cartNum);
                    goods.setTotalPrice( new BigDecimal(cartQueryVo.getTruePrice() * cartNum).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    goods.setUnit(productQueryVo.getUnit());

                    totalNum  = totalNum + cartNum;
                    meideyiDrugList.add(goods);
                }
                meideyiPaidOrder.setTotalNum(totalNum.toString());
                meideyiPaidOrder.setItemNum( String.valueOf(cartInfos.size()));
                meideyiPaidOrder.setDrugs(meideyiDrugList);

                MeideyiExpress meideyiExpress = new MeideyiExpress();
                meideyiExpress.setAddress(yxStoreOrder.getUserAddress());
                meideyiExpress.setMobile(yxStoreOrder.getUserPhone());
                meideyiExpress.setName(yxStoreOrder.getRealName());
                meideyiPaidOrder.setExpress(meideyiExpress);
                MeideyiLogistics meideyiLogistics = new MeideyiLogistics();
                meideyiPaidOrder.setLogistics(meideyiLogistics);
                MeideyiPrescription meideyiPrescription = new MeideyiPrescription();
                MeideyiImage meideyiImage = new MeideyiImage();
                meideyiImage.setImageUrl(yxStoreOrder.getImagePath());
                List<MeideyiImage> meideyiImageList = new ArrayList<>();
                meideyiImageList.add(meideyiImage);
                meideyiPrescription.setImages(meideyiImageList);

                List<MeideyiPrescription> meideyiPrescriptionList = new ArrayList<>();
                meideyiPrescriptionList.add(meideyiPrescription);
                meideyiPaidOrder.setPrescriptions(meideyiPrescriptionList);

               // requestBody = JSONUtil.parseObj(meideyiPaidOrder).toString();

                array.add(meideyiPaidOrder);
            }
        } else if("-2".equals(status)) {  // 已退款
            postUrl = url + refundOrderUrl + "&time="+ sign;

            MeideyiRefundOrder meideyiRefundOrder = new MeideyiRefundOrder();
            LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);

            YxStoreOrder yxStoreOrder = orderMapper.selectOne(lambdaQueryWrapper);
            if(ObjectUtil.isNotNull(yxStoreOrder)) {
                meideyiRefundOrder.setMobile(yxStoreOrder.getFactUserPhone());
                meideyiRefundOrder.setOrderCode(yxStoreOrder.getOrderId());
                meideyiRefundOrder.setProviderCode("yy01");
                meideyiRefundOrder.setUserId(yxStoreOrder.getCardNumber());
                meideyiRefundOrder.setRefundTime(DateUtil.now());
              //  requestBody = JSONUtil.parseObj(meideyiRefundOrder).toString();
                array.add(meideyiRefundOrder);
            }


        } else if ("12".equals(status)) {  // 已完成
            postUrl = url + submitOrderUrl + "&time="+ sign;
            MeideyiCompleteOrder meideyiCompleteOrder = new MeideyiCompleteOrder();

            LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);

            YxStoreOrder yxStoreOrder = orderMapper.selectOne(lambdaQueryWrapper);
            if(ObjectUtil.isNotNull(yxStoreOrder)) {
                meideyiCompleteOrder.setMobile(yxStoreOrder.getFactUserPhone());
                meideyiCompleteOrder.setOrderCode(yxStoreOrder.getOrderId());
                meideyiCompleteOrder.setProviderCode("yy01");
                meideyiCompleteOrder.setProviderName("益药商城");
                meideyiCompleteOrder.setStatus("12");
                meideyiCompleteOrder.setUserId(yxStoreOrder.getCardNumber());
                MeideyiExpress meideyiExpress = new MeideyiExpress();
                meideyiExpress.setAddress(yxStoreOrder.getUserAddress());
                meideyiExpress.setMobile(yxStoreOrder.getUserPhone());
                meideyiExpress.setName(yxStoreOrder.getRealName());
                meideyiCompleteOrder.setExpress(meideyiExpress);
                MeideyiLogistics meideyiLogistics = new MeideyiLogistics();
                meideyiCompleteOrder.setLogistics(meideyiLogistics);
                requestBody = JSONUtil.parseObj(meideyiCompleteOrder).toString();

                array.add(meideyiCompleteOrder);
            }

        }


        if(array.size() >0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity request = new HttpEntity(array.toString(), headers);
            log.info("美德医ur:{}",postUrl);
            log.info("发送美德医的消息:{}", array.toString());

            ResponseEntity<String> resultEntity = restTemplate.exchange(postUrl, HttpMethod.POST, request, String.class);

            String body = resultEntity.getBody();

            log.info("美德医返回的消息:{}",body);

            if(JSONUtil.isJson(body) && "0001".equals(JSONUtil.parseObj(body).getStr("code"))) {
                log.error("美德医返回的错误消息:{}",body);
                mqProducer.sendToDeadQueue(deadQueueName,msg);
            }
        }



    }
}
