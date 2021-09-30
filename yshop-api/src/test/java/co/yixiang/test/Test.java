package co.yixiang.test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.order.web.dto.Order4ProjectDto;
import co.yixiang.modules.order.web.dto.OrderDetail4Project;
import co.yixiang.mp.yiyaobao.vo.OrderDetailVo;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Slf4j
public class Test {
    public static void main(String[] args) {
        /*  OrderVo orderVo = new OrderVo();
          orderVo.setChannelName("益药公众号");
          orderVo.setDoctorName("王医生");
          orderVo.setHospitalName("上海中山医院");
          orderVo.setMobile("18017890127");
          orderVo.setName("张无忌");
          orderVo.setOrderDate("2020-05-28");
          orderVo.setOrderNo("20222200038188");
          orderVo.setStatus("完成");
          orderVo.setStoreName("上海众协药房中山西路店");
          orderVo.setExpressInfo("");

          orderVo.setDiscountTotalAmount(new BigDecimal("1620"));
          orderVo.setTotalAmount(new BigDecimal("1800"));
          *//*orderVo.setAddress("上海市龙华中路600号");
          orderVo.setReceiveMobile("18017890126");
          orderVo.setReceiveName("张三丰");
          orderVo.setDiagnoseResult("肺癌");*//*
          OrderDetailVo orderDetailVo = new OrderDetailVo();
          orderDetailVo.setProductName("复方脑肽节苷脂注射液");
          orderDetailVo.setDiscountPrice(new BigDecimal("900"));
          orderDetailVo.setUnitPrice(new BigDecimal("1000"));
          orderDetailVo.setDiscountRate(new BigDecimal("0.9"));
          orderDetailVo.setQty(1);
          orderDetailVo.setSpec("20ml");

            OrderDetailVo orderDetailVo2 = new OrderDetailVo();
            orderDetailVo2.setProductName("普宁可");
            orderDetailVo2.setDiscountPrice(new BigDecimal("720"));
            orderDetailVo2.setUnitPrice(new BigDecimal("800"));
            orderDetailVo2.setDiscountRate(new BigDecimal("0.9"));
            orderDetailVo2.setQty(1);
            orderDetailVo2.setSpec("20ml");

            List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
            orderDetailVoList.add(orderDetailVo2);
            orderDetailVoList.add(orderDetailVo);

        orderVo.setDetails(orderDetailVoList);

        log.info(JSONUtil.parseObj(orderVo).toString());*/

        Date d = DateUtil.parse("20090101");

        Date d2 = DateUtil.beginOfMonth(DateUtil.date());

        log.info("d={},d2={}",d,d2);

        log.info("相差=={}",DateUtil.between(d2,d, DateUnit.DAY));

        log.info("年龄{}",DateUtil.ageOfNow("20190101"));


        double a = NumberUtil.mul(NumberUtil.div(88,100),100);
        log.info("a==={}",a);


        Order4ProjectDto dto = new Order4ProjectDto();
        dto.setAddressId(111);
        dto.setProjectCode("test2");
        List<OrderDetail4Project> list = new ArrayList<>();
        OrderDetail4Project orderDetail4Project = new OrderDetail4Project();
        orderDetail4Project.setQty(2);
        orderDetail4Project.setDrugStoreId(11);
        orderDetail4Project.setProductId(12);

        OrderDetail4Project orderDetail4Project2 = new OrderDetail4Project();
        orderDetail4Project2.setQty(3);
        orderDetail4Project2.setDrugStoreId(11);
        orderDetail4Project2.setProductId(14);
        list.add(orderDetail4Project);
        list.add(orderDetail4Project2);
        dto.setDrugList(list);
        log.info("{}",JSONUtil.parseObj(dto));


        log.info(" FileUtil.extName(url_parm)={}" , FileUtil.extName("https://wechat-api.yiyaogo.com/api/file/static/defaultMed.jpg"));

    }

    public void get(){
        try {
            InputStream stream =  getClass().getClassLoader().getResourceAsStream("otc.jpg");

            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = stream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            stream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
            log.info(base64);
        }catch (Exception e) {

        }

    }
}
