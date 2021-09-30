
package co.yixiang.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.monitor.service.RedisService;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.param.ExpressParam;
import co.yixiang.modules.yiyaobao.entity.AddressDTO;
import co.yixiang.modules.yiyaobao.entity.PrescriptionDTO;
import co.yixiang.modules.zhengdatianqing.dto.OrderDto;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.mp.yiyaobao.service.CmdStockDetailEbsService;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.utils.ImageUtil;
import co.yixiang.modules.yiyaobao.service.impl.OrderServiceImpl;
import co.yixiang.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class YiyaobaoTest {
@Autowired
private OrderServiceImpl orderService;

@Autowired
private RedisService redisService;

@Autowired
private CmdStockDetailEbsService cmdStockDetailEbsService;

@Autowired
private ZhengDaTianQingServiceImpl zhengDaTianQingService;

@Autowired
private RedisUtils redisUtils;

@Autowired
private LocalStorageService localStorageService;

@Autowired
private QiNiuService qiNiuService;

@Autowired
private YxStoreOrderService yxStoreOrderService;

@Test
    public void test(){
    orderService.generateVerifyCode("zhouhang","18017890127");
}


@Test
    public void test2() {
    // 生成省市区code
    String provinceName = "河北";
    String cityName = "衡水市";
    String districtName = "枣强县";
    AddressDTO addressDTO = orderService.getAddressDTO(provinceName,cityName,districtName);
    log.info(addressDTO.toString());

    // 图片base64
    String imgFilePath="http://d.hiphotos.baidu.com/image/pic/item/a044ad345982b2b713b5ad7d3aadcbef76099b65.jpg";

    String base64_str =  "";
    try{
        base64_str = ImageUtil.encodeImageToBase64(new URL(imgFilePath));
        base64_str = "data:image/jpeg;base64,"+ base64_str;
        base64_str = URLEncoder.encode(base64_str,"UTF-8")  ;
    }catch (Exception e) {
        e.printStackTrace();
    }


    PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
    prescriptionDTO.setAddress("测试地址");
    prescriptionDTO.setCityCode(addressDTO.getCityCode());
    prescriptionDTO.setProvinceCode(addressDTO.getProvinceCode());
    prescriptionDTO.setDistrictCode(addressDTO.getDistrictCode());
    prescriptionDTO.setImagePath(base64_str);
    prescriptionDTO.setCustomerRequirement("测试");
    prescriptionDTO.setPatientMobile("18017890127");
    prescriptionDTO.setPatientName("zhouhang");
    prescriptionDTO.setProjectNo("202002170001");
    prescriptionDTO.setSellerId("85");
    prescriptionDTO.setVerifyCode("559391");
    JSONObject jsonObject = JSONUtil.createObj();
    jsonObject.put("sku","010313030");
    jsonObject.put("unitPrice","0.3");
    jsonObject.put("amount","3");
    JSONArray jsonArray = JSONUtil.createArray();
    jsonArray.add(jsonObject);

    prescriptionDTO.setItems(jsonArray.toString());
    log.info(JSONUtil.parseObj(prescriptionDTO).toString());

    ImageUtil.toFileByBase64(prescriptionDTO.getImagePath());

    orderService.uploadOrder(prescriptionDTO);
}

@Test
    public void test3() {
    redisService.addGeo("store",121.24426,31.01475,"荣都");
    redisService.addGeo("store",121.220718,31.007544,"松江中心医院");
    redisService.addGeo("store",121.242584,31.05769,"松江万达");
    redisService.addGeo("store",121.422638,31.178101,"上海第六人民医院");
    redisService.addGeo("store",121.46772,31.23284,"上海长征医院");

    redisService.geoRadiusByCoordinate("store",100,20,121.24426,31.01475);

  //  redisService.addGeo("store",31.192247,121.42062,"店3");
   // redisService.saveCode("abc","nnini",22222l);
  //  redisService.saveCode("you","nizai",6000l);
/*    Object object = redisService.getObj("abc");
    log.info( "======{}", String.valueOf(object));
    List<Point> pointList = redisService.geoops("store","store1");
    if(CollUtil.isEmpty(pointList)) {
        log.info("pointList is empty");
    } else {
        log.info("pointList.size()=",pointList.size());
    }
    for(Point point:pointList) {
        log.info("point.getX()={}",point.getX());
        log.info("point.getY()={}",point.getY());
    }*/
}

@Test
public void test4(){
   Integer orderNo = orderService.queryOrderStatus("2005210745569458");
   log.info("orderNo={}",orderNo);
}

    @Test
    public void test5(){
       // YxStoreOrder order = zhengDaTianQingService.createOrder();
       // log.info("order={}",order);
String recipel = "https://papssrc.ilvzhou.com/statics/upload/attachment/202010/20/a40f463ddc4d7f1c8c681ffbeb1f5004.jpg";
        QiniuContent qiniuContent = qiNiuService.uploadByUrl(recipel, qiNiuService.find());
        log.info("地址：{}",qiniuContent.getUrl());
    }


    @Test
    public void test6(){
        // YxStoreOrder order = zhengDaTianQingService.createOrder();
        // log.info("order={}",order);

      LocalStorageDto dto = localStorageService.createByUrl("http://papv2beta1.ilvzhou.com/statics/upload/attachment/202007/01/2a8b65e88ff255db1a90bb69428cfb6c.jpg","JJH0001.jpg");
      log.info(dto.getPath() + dto.getName());
    }

    @Test
    public void test7(){
        QiniuContent content = qiNiuService.uploadByUrl("https://papssrc.ilvzhou.com/statics/upload/attachment/202007/27/4816d313f8c3d431c52644264218af64.jpg",qiNiuService.find());

        log.info(content.getUrl());
       // qiNiuService.downloadPicture("https://papssrc.ilvzhou.com/statics/upload/attachment/202007/27/4816d313f8c3d431c52644264218af64.jpg","e:\\a.jpg");
    }

    @Test
    public void test8(){
       // ExpressInfo str = orderService.queryOrderLogisticsProcess("7a40f92a-9b25-4349-b3d9-1293d1c4c356","","","","");

      //  log.info("{}",str);

        AddressDTO addressDTO = orderService.getAddressDTO("江西省","抚州市","崇仁县");
        log.info(addressDTO.toString());
   }

   @Test
    public void test9(){
       ExpressParam expressInfoDo = new ExpressParam();
       expressInfoDo.setYiyaobaoOrderId("ae5640c1-82d4-4709-97f4-4255a36ea1c1");
       orderService.queryOrderLogisticsProcess(expressInfoDo);
   }


   @Test
    public void test10(){
            orderService.sendOrder2YiyaobaoCloud("2012220155406899");
   }

   @Test
    public void test11() {
       orderService.getMedPartnerMedicine();
   }
}
