
package co.yixiang.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.hospitaldemand.web.vo.InternetHospitalCart;
import co.yixiang.modules.manage.service.CheckOneService;
import co.yixiang.modules.manage.service.impl.CASignServiceImpl;
import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.mapper.YxStoreProductMapper;
import co.yixiang.modules.shop.service.MedCalculatorService;
import co.yixiang.modules.shop.service.YxStoreCouponUserService;
import co.yixiang.modules.shop.web.param.YxStoreProductQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.xikang.service.XkProcessService;
import co.yixiang.modules.yaolian.service.impl.YaolianServiceImpl;
import co.yixiang.modules.yaoshitong.service.ChatMsgService;
import co.yixiang.modules.zhongan.ZhongAnPuYaoServiceImpl;
import co.yixiang.mp.service.WxMpTemplateMessageService;
import co.yixiang.mp.service.impl.PdfServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BaseTest {

    @Autowired
    YxStoreCouponUserService yxStoreCouponUserService;

    @Autowired
    CASignServiceImpl caSignService;

    @Autowired
    YxUserService userService;

    @Autowired
    InternetHospitalDemandService demandService;

    @Autowired
    WxMpTemplateMessageService wxMpTemplateMessageService;

    @Autowired
    YxUserService yxUserService;

    @Autowired
    ChatMsgService chatMsgService;

    @Autowired
    private XkProcessService xkProcessService;

    @Autowired
    private MedCalculatorService medCalculatorService;

    @Autowired
    private CheckOneService checkOneService;

    @Autowired
    private YaolianServiceImpl yaolianService;

    @Autowired
    private ZhongAnPuYaoServiceImpl zhongAnPuYaoService;


    @Autowired
    private PdfServiceImpl pdfService;


    @Autowired
    private YxStoreProductMapper yxStoreProductMapper;

    @Test
    public void test(){
      // String html = caSignService.getSignAmgKnowHtml("1234",32);
       //log.info("html={}",html);

     //  String result = wxMpTemplateMessageService.sendDYTemplateMessage("wxfa714c5e734c0511","ostpQ5cJpjl8jOkDY9b8X3RXJRWg","JGzu65ttfmN8VrVsQknjxiZB3yCWDN76A5EnTS9X0PY","/pages/ShoppingCart/submitOrder?prescriptionId=3&type=2",new HashMap<>());
     //   log.info("{}",result);
      //  yxUserService.sendCouponToUser(51,"1234");

   //   Integer unread =  chatMsgService.queryUnreadCountByReviceuser(62);

   //   log.info("unread={}",unread);
        /*AttrDTO attrDTO = new AttrDTO();
        attrDTO.setUid(62);
        attrDTO.setCardNumber("123456");
        attrDTO.setOrderNumber("2222");
        attrDTO.setProjectCode("taipinglexiang");
        String url = xkProcessService.h5Url4doctor(attrDTO);
        log.info("url={}",url);*/

      //  medCalculatorService.getMedCalculatorByUid(15, DateUtil.parseDate("2021-01-07"));
     //   checkOneService.check("","");

       Boolean flag =  checkOneService.check("310227198601271413","周杭1");
       log.info("flag = {}",flag);
    }

    @Test
    public void test3(){
        String rx_id = "340f83e6833e57d2a01dd3adc7720dc8";
         yaolianService.getElecrx(rx_id);
    }


    @Test
    public void test4() {
        String orderNo = "2104291661860546";
        zhongAnPuYaoService.sendOrderInfo(orderNo);
    }


    @Test
    public void pdfService(){
        try{
            pdfService.generatePdf();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testProduct() {
        Page<YxStoreProduct> pageModel = new Page<>(1,
                10);
        YxStoreProductQueryParam productQueryParam = new YxStoreProductQueryParam();
        productQueryParam.setKeyword("希舒美");
      //  productQueryParam.setDiseaseId("11069");
       // productQueryParam.setDiseaseParentId("11068");
      //  productQueryParam.setPinYin("liputuo");
      //  productQueryParam.setManufacturer("辉瑞制药有限公司(中国)");
      //  productQueryParam.setPriceOrder("desc");
      //  productQueryParam.setNews("1");
        //  IPage<YxStoreProduct> pageList = yxStoreProductMapper.selectPage(pageModel,wrapper);
        List<Integer> stores = new ArrayList<>();
        stores.add(123);
        stores.add(222);
       // productQueryParam.setStoreList(stores);
        productQueryParam.setProjectCode("taipinglexiang");
        productQueryParam.setCardType("advanced");
        productQueryParam.setDrugStoreType("85");
        IPage<YxStoreProductQueryVo> yxStoreProductQueryVoIPage = yxStoreProductMapper.getYxStoreProductPageList4Project(pageModel,productQueryParam);
        log.info("{}",JSONUtil.parseArray(yxStoreProductQueryVoIPage.getRecords()));
    }
}
