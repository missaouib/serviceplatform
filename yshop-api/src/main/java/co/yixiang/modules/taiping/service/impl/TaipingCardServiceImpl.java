package co.yixiang.modules.taiping.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.entity.YxStoreOrderCartInfo;
import co.yixiang.modules.order.service.YxStoreOrderCartInfoService;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.shop.web.vo.YxStoreCartQueryVo;
import co.yixiang.modules.taiping.entity.*;
import co.yixiang.modules.taiping.enums.TaipingOrderStatusEnum;
import co.yixiang.modules.taiping.mapper.TaipingCardMapper;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.util.EncryptionToolUtilAes;
import co.yixiang.modules.taiping.web.param.TaipingCardQueryParam;
import co.yixiang.modules.taiping.web.vo.TaipingCardQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 太平乐享虚拟卡 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TaipingCardServiceImpl extends BaseServiceImpl<TaipingCardMapper, TaipingCard> implements TaipingCardService {

    @Autowired
    private TaipingCardMapper taipingCardMapper;

    @Value("${taiping.CipherKey}")
    private String CipherKey;

    @Value("${taiping.taipingUrlPrefix}")
    private String taipingUrlfix;

    @Autowired
    private RestTemplate restTemplate;

    private String sendOrderStatusUrlSuffix = "/lxjk-wechat-backend/taxPreference/orderStatus";

    @Autowired
    private YxStoreOrderCartInfoService yxStoreOrderCartInfoService;

    @Autowired
    @Lazy
    private YxStoreOrderService yxStoreOrderService;

    @Override
    public TaipingCardQueryVo getTaipingCardById(Serializable id) throws Exception{
        return taipingCardMapper.getTaipingCardById(id);
    }

    @Override
    public Paging<TaipingCardQueryVo> getTaipingCardPageList(TaipingCardQueryParam taipingCardQueryParam) throws Exception{
        Page page = setPageParam(taipingCardQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(TaipingCardQueryParam.class, taipingCardQueryParam);
        IPage<TaipingCard> iPage = taipingCardMapper.selectPage(page,queryWrapper);
        return new Paging(iPage);
    }

    @Override
    public TaipingCard getTaipingCardByNumber(String cardNumber)  {
        QueryWrapper<TaipingCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_number",cardNumber);
        TaipingCard taipingCard = this.getOne(queryWrapper,false);
        return taipingCard;
    }

    @Override
    public TaipingParamDto analysisParam(TaipingParamDto taipingParamDto) {
        // 太平项目
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(taipingParamDto.getProjectCode())) {
            String cardNumber_encrypt = taipingParamDto.getCardNumber();
            String cardNumber = "";
            if(StrUtil.isNotBlank(cardNumber_encrypt)) {
                try {
                    cardNumber = EncryptionToolUtilAes.decrypt(cardNumber_encrypt, CipherKey);
                }catch (Exception e) {
                    throw new BadRequestException("太平卡号解密出错");
                }
            }


            String orderNumber_encrypt = taipingParamDto.getOrderNumber();
            String orderNumber = "";
            if(StrUtil.isNotBlank(orderNumber_encrypt)) {
                try{
                    orderNumber = EncryptionToolUtilAes.decrypt(orderNumber_encrypt, CipherKey);
                } catch (Exception e) {
                    throw new BadRequestException("太平订单号解密出错");
                }

            }


            taipingParamDto.setCardNumber(cardNumber);

            taipingParamDto.setOrderNumber(orderNumber);
        }

        return taipingParamDto;
    }


    @Override
    public Boolean sendOrderStatus(OrderStatusDto orderStatus) {

        // 转json，然后加密
        String orderStatus_data_original = JSONUtil.toJsonStr(orderStatus);
        String orderStatus_data_enc = EncryptionToolUtilAes.encrypt(orderStatus_data_original, CipherKey);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("data",orderStatus_data_enc);
        jsonObject.put("callerName","XSYF");

        HttpEntity requestEntity = new HttpEntity(jsonObject.toString(), headers);
        String sendOrderStatusUrl = taipingUrlfix + sendOrderStatusUrlSuffix;
        log.info("太平更新订单状态的url={}",sendOrderStatusUrl);
        String body = "";
        try{
            log.info("太平订单状态更新：发送数据 {}",jsonObject.toString());
            ResponseEntity<String> resultEntity = restTemplate.exchange(sendOrderStatusUrl, HttpMethod.POST, requestEntity, String.class);
            body = resultEntity.getBody();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("太平订单状态更新：返回结果 {}",body);
        }

        if(JSONUtil.isJson(body)) {
            JSONObject result = JSONUtil.parseObj(body);
            int status = result.getInt("status");
            if(status == 1) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Async
    public Boolean sendOrderStatus(String orderId,Integer status) {
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",orderId));
        QueryWrapper<YxStoreOrderCartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("oid",yxStoreOrder.getId());
        List<YxStoreOrderCartInfo> cartInfos = yxStoreOrderCartInfoService.list(wrapper);

        OrderStatusDto orderStatus = new OrderStatusDto();
        orderStatus.setOrderNumber(yxStoreOrder.getTaipingOrderNumber());
        orderStatus.setStatusCode(status);
        orderStatus.setStatusTime(DateUtil.now());

        TaipingOrder order = new TaipingOrder();

        ArrayList detailList = new ArrayList();
        for (YxStoreOrderCartInfo info : cartInfos) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(info.getCartInfo(),YxStoreCartQueryVo.class);
            int cartNum = cartQueryVo.getCartNum();
            String yiyaobaoSku =  cartQueryVo.getYiyaobaoSku();
            String commonName = cartQueryVo.getProductInfo().getCommonName();
            BigDecimal unitPrice =  new BigDecimal(cartQueryVo.getVipTruePrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
            TaipingOrderDetail detail = new TaipingOrderDetail();
            detail.setTradeName(commonName);
            detail.setCount(cartNum);
            detail.setDrugCode(yiyaobaoSku);
            detail.setUnitPrice(unitPrice);
            detail.setTotalPrice(NumberUtil.mul(unitPrice , cartNum).setScale(2,BigDecimal.ROUND_HALF_UP));
            detailList.add(detail);
        }
        order.setDetails(detailList);
        order.setTotalPrice(yxStoreOrder.getPayPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
        orderStatus.setProductName(JSONUtil.toJsonStr(order));
        this.sendOrderStatus(orderStatus);

        return true;
    }
}
