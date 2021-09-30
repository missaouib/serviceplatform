
package co.yixiang;

import cn.hutool.json.JSONUtil;
import co.yixiang.modules.api.param.PrescripStatusParam;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.impl.DualImageUrlServiceImpl;
import co.yixiang.modules.taiping.enums.CustomerIdTypeEnum;
import co.yixiang.modules.taiping.enums.TaipingOrderStatusEnum;
import co.yixiang.modules.taiping.service.TaipingCardService;
import co.yixiang.modules.taiping.service.YxStoreCouponCardService;
import co.yixiang.modules.taiping.service.dto.CheckCustomerDto;
import co.yixiang.modules.taiping.service.dto.TaipingOrder;
import co.yixiang.modules.taiping.service.dto.TaipingOrderDetail;
import co.yixiang.modules.taiping.service.dto.OrderStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaiPingTest {


    @Autowired
    private TaipingCardService taipingCardService;

    @Autowired

    private YxStoreProductService yxStoreProductService;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreCouponCardService yxStoreCouponCardService;

    @Autowired
    private DualImageUrlServiceImpl dualImageUrlService;

    @Autowired
    private ProjectService projectService;

    @Test
    public void test1(){
        CheckCustomerDto customer = new CheckCustomerDto();
        customer.setCardNumber("1000012");
        customer.setCustomerIdCardNo("1111111");
        customer.setCustomerIdType(CustomerIdTypeEnum.shenFenZheng.getValue());
        customer.setCustomerName("项羽");

        Boolean result = taipingCardService.checkCustomer(customer);

        log.info("{}",result);

    }


    @Test
    public void test2(){
        OrderStatusDto orderStatus = new OrderStatusDto();
        orderStatus.setOrderNumber("20110216154703990005");
        orderStatus.setStatusCode(TaipingOrderStatusEnum.yiShouLi.getValue());
        orderStatus.setStatusTime("2020-11-03 14:10:03");

        TaipingOrder taipingOrder = new TaipingOrder();
        TaipingOrderDetail detail = new TaipingOrderDetail();
        detail.setTradeName("药品1");
        detail.setCount(2);
        detail.setDrugCode("a001");
        detail.setUnitPrice(new BigDecimal(1));
        detail.setTotalPrice(new BigDecimal(2));

        TaipingOrderDetail detail2 = new TaipingOrderDetail();
        detail2.setTradeName("药品2");
        detail2.setCount(3);
        detail2.setDrugCode("a002");
        detail2.setUnitPrice(new BigDecimal(1));
        detail2.setTotalPrice(new BigDecimal(3));

        ArrayList detailList = new ArrayList();

        detailList.add(detail);
        detailList.add(detail2);
        taipingOrder.setDetails(detailList);
        taipingOrder.setTotalPrice(new BigDecimal(5));



        orderStatus.setProductName(JSONUtil.toJsonStr(taipingOrder));

        Boolean result = taipingCardService.sendOrderStatus(orderStatus);

        log.info("{}",result);

    }
    @Test
    public void test3() {
        PrescripStatusParam statusParam = new PrescripStatusParam();
       // statusParam.setDealDate("2020-11-29");
        statusParam.setPrescripNo("1332251615544999936");
        statusParam.setPrescripStatus("90");
        yxStoreOrderService.prescripStatus(statusParam);
    }

    @Test
    public void test4(){
      //  yxStoreCouponCardService.generateCouponByCardNumber("123456", DateUtil.parse("2020-02-15"));
        yxStoreCouponCardService.updateInvalidStatus();
    }


    @Test
    public void test5(){
        //  yxStoreCouponCardService.generateCouponByCardNumber("123456", DateUtil.parse("2020-02-15"));
        dualImageUrlService.dual();
    }

    @Test
    public void test6(){
        String image = projectService.generateQRCodeH5("yaolian","");
        log.info(image);
    }
}
