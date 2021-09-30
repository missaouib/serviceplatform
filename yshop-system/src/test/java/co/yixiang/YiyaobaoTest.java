
package co.yixiang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.ebs.service.EbsServiceImpl;
import co.yixiang.modules.gjp.Lib.service.GjpServiceImpl;
import co.yixiang.modules.monitor.service.RedisService;

import co.yixiang.modules.shop.domain.Product4project;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.mapper.Product4projectMapper;
import co.yixiang.modules.yaolian.service.YaolianServiceImpl;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.modules.yiyaobao.service.StockServiceImpl;
import co.yixiang.modules.zhengdatianqing.dto.OrderDetailDto;
import co.yixiang.modules.zhengdatianqing.dto.OrderDto;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayMethodEnum;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayTypeEnum;
import co.yixiang.mp.yiyaobao.param.OrderQueryParam;
import co.yixiang.mp.yiyaobao.service.CmdStockDetailEbsService;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.PinYinUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class YiyaobaoTest {


@Autowired
private RedisService redisService;

@Autowired
private CmdStockDetailEbsService cmdStockDetailEbsService;

@Autowired
private GjpServiceImpl gjpService;


@Autowired
private ZhengDaTianQingServiceImpl zhengDaTianQingService;

@Autowired
private YxStoreOrderService orderService;


@Autowired
private StockServiceImpl stockService;

@Autowired
private YxStoreProductService productService;

@Autowired
private EbsServiceImpl ebsService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${yiyao.wechatApiUrl}")
    private String yiyao_wechatApiUrl;

    private String yiyao_whchatOrderApi = "/api/order/external";


    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YaolianServiceImpl yaolianService;

    @Autowired
    private Product4projectMapper product4projectMapper;

    @Autowired
    private Product4projectService product4projectService;

@Test
    public void test(){
try{
    OrderQueryParam queryParam = new OrderQueryParam();
    queryParam.setMobile("13386098088");
    queryParam.setPage(0);
    queryParam.setSize(2);
  //  Paging<OrderVo> paging = orderService.getYiyaobaoOrderbyMobile(queryParam);
   // log.info("{}",paging);
  //  orderService.queryOrderLogisticsProcess("4250483683975265","YYZFRC","54e2e08c-af81-4c96-85bb-3a16512bdd0f");
    String token = gjpService.getToken();
    log.info("token={}",token);
}catch (Exception e) {
    e.printStackTrace();
}

}
@Test
public void test2(){
    orderService.syncOrderStatus();
}

    @Test
public void pinyin(){
    QueryWrapper<YxStoreProduct> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("id","store_name","common_name");
    List<YxStoreProduct> list = productService.list(queryWrapper);
    for(YxStoreProduct yxStoreProduct:list) {
        String commonPinYin = PinYinUtils.getHanziPinYin(yxStoreProduct.getCommonName()) ;
        String namePinYin = PinYinUtils.getHanziPinYin(yxStoreProduct.getStoreName()) ;
        if(commonPinYin == null) {
            commonPinYin = "";
        }
        if(namePinYin == null) {
            namePinYin = "";
        }
        String pinYin = commonPinYin + "(" + namePinYin + ")";
        String commonShortPinYin = PinYinUtils.getHanziInitials(yxStoreProduct.getCommonName());
        String nameShortPinYin = PinYinUtils.getHanziInitials(yxStoreProduct.getStoreName()) ;
        if(commonShortPinYin == null) {
            commonShortPinYin = "";
        }
        if(nameShortPinYin == null) {
            nameShortPinYin = "";
        }
        String shortPinYin = commonShortPinYin + "(" + nameShortPinYin + ")";

        UpdateWrapper<YxStoreProduct> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",yxStoreProduct.getId());
        updateWrapper.set("pinyin_name",pinYin);
        updateWrapper.set("pinyin_short_name",shortPinYin);
        productService.update(updateWrapper);
    }
}
    @Test
public void convertImage(){
    productService.convertImage();
}



 @Test
 public void sendOrder2Yiyao(){
     List<OrderDto> orderDtoList = new ArrayList<>();
     OrderDto orderDto = new OrderDto();
     orderDto.setAddress("上海市松江222弄");
     orderDto.setCity("松江");
     orderDto.setProvince("上海");
     orderDto.setCounty("松江");
     orderDto.setMobile("18017890127");
     orderDto.setMode_name("");
     orderDto.setName("周杭");
     orderDto.setOrder_sn("JJH00010");
     orderDto.setRecipel("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=205441424,1768829584&fm=26&gp=0.jpg");
     orderDto.setPre_receive_date("2019-09-22");
     List<OrderDetailDto> details= new ArrayList<>();
     OrderDetailDto detailDto = new OrderDetailDto();
     detailDto.setDrug_id("1");
     detailDto.setQuantity(5);
     details.add(detailDto);
     orderDto.setDetails(details);

     orderDtoList.add(orderDto);
     try {
         if(CollUtil.isNotEmpty(orderDtoList)) {
             log.info("获取慈善赠药订单{}个",orderDtoList.size());
             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
             // headers.set("Authorization",token);
             String data = JSONUtil.parseArray(orderDtoList).toString();
             HttpEntity request = new HttpEntity(data, headers);
              yiyao_wechatApiUrl = "http://wechat-api.yiyaogo.com";
             String url = yiyao_wechatApiUrl + yiyao_whchatOrderApi;
             log.info("益药公众号下单url={}",url);
             ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
             String body = resultEntity.getBody();
             log.info("向益药公众号下单，结果：{}",body);
         } else {
             log.info("获取慈善赠药订单0个");
         }
     }catch (Exception e) {

         e.printStackTrace();
     }

 }


 @Test
    public void test1(){
     yiyaobaoOrderService.saveProduct4project("21612f34-3c63-4a93-b790-49898e8d6d31","meideyi","meideyi");


     LambdaUpdateWrapper<Product4project> lambdaUpdateWrapper = new LambdaUpdateWrapper();
     lambdaUpdateWrapper.eq(Product4project::getProjectNo,"meideyi");
     lambdaUpdateWrapper.eq(Product4project::getProductUniqueId,"21612f34-3c63-4a93-b790-49898e8d6d31");
     lambdaUpdateWrapper.set(Product4project::getIsDel,"0");

     //  lambdaUpdateWrapper.set(Product4project::getUpdateTime,new Timestamp(System.currentTimeMillis()));
   //  product4projectMapper.update(new Product4project(), lambdaUpdateWrapper);


 }

 @Test
    public void test3(){
     orderService.sendOrder2yiyaobao();
 }

 @Test
    public void test4() {
     YxStoreProduct yxStoreProduct = yxStoreProductService.getById(101679);
     if(yxStoreProduct.getTaxRate().doubleValue() == new Double("13").doubleValue()) {
         log.info("=====================");
     }
 }

    @Test
    public void test5() {
       // yiyaobaoOrderService.getMedStoreMedicine();
        YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
        yiyaobaoOrderInfo.setPrsNo("1402904863968329728");
        yiyaobaoOrderInfo.setOrderSource("37");
        yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_13.getValue());  // 微信支付
        yiyaobaoOrderInfo.setPayResult("10"); // 已支付
        yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
        String payTimeStr = OrderUtil.stampToDate(String.valueOf(1592393405));
        yiyaobaoOrderInfo.setPayTime(payTimeStr); // 支付时间
    //    yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
        yiyaobaoOrderService.updateYiyaobaoOrderInoByPrescripNo(yiyaobaoOrderInfo);


    }
    @Test
    public void test6() {
       //  yiyaobaoOrderService.syncYiyaobaoStoreMed();
       // yaolianService.pushStores();
    }

}
