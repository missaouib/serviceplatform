package co.yixiang.test;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.hospitaldemand.entity.InternetHospitalDemand;
import co.yixiang.modules.zhongan.RSA2SignerDemo;
import co.yixiang.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;

@Slf4j
public class MainTest {


    public static void main(String[] args) {
     //   String filePath = "D:\\bbb.pdf";
     //   pdf2Pic(filePath, "D:\\bb");
     //   String orderSn =  UUID.randomUUID().toString();
       // log.info("orderSn = {}",orderSn);
      //  File file = new File("D:\\bbb.pdf");
       // String base = Base64Util.getFileBinary(file);
      //  log.info("base=={}",base);

        //
     /*   try {
           String base64 = FileUtil.fileToBase64(file);
           log.info("base==={}",base64);
        }catch (Exception e) {

        }*/

        /*Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
       Integer id_int = Integer.valueOf(new Long(id).intValue());
        log.info("long id={}",id);
        log.info("int id={}",id_int);

        DateUtil.format(OrderUtil.stampToDateObj(String.valueOf(1619659062)), DatePattern.NORM_DATETIME_FORMAT );


        log.info("{}",OrderUtil.stampToDate("1619659062"));


        RSA2SignerDemo signerDemo = new RSA2SignerDemo();
        try {
            String sign = signerDemo.sign("abcj","utf-8","MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN+eIfzJEsxwJ8Jq+YGRiclWvqe/JZcS1OE+2nyvo/u4WALw4rGerxGRPoG7DH+cr4ZWtIr4azG3guqlAqYj/frhmBBDqIbacm4ongvkqHL9zmOC+nJpsyl9NcGuTQjMuuGrqQ6FxJbUNPSRgjXH44xy46/ipXkanaR8zbWmLsbNAgMBAAECgYAFeRVT3of7QPN4Kq4YobtBlkHsUR1WaTuUl7k5PANDceuVhtfFiSC3yVpCSpWvueIB47VfgVz+RoOJwqgh8NeSdKGMyHEm0SGMGVZr/DGlOWJ8WeK++EqsSNPxgOFIPu9eQ35ofartzDM5kKB0meM5gsYMh0oBC8MBYgLOAIVygQJBAPqi5Zp8CNJACuxyIkWwO91EDZCOqVh1ElvXwHoRcBUIEDJZVB3+wZU9CSGwcLYs5Z/aVPHbGBRJ9yRS8S6i26kCQQDkZzccaokosoKx37s9CJZncxN+YChX19qPxlS0G38lkMqiD+PjtzwrA8SEkHTlDZITlepeMRvLleNyn8r1ZmiFAkEAzuenD88RITBJVEQsUsdXxCOn5ww+dI3A6BzGAn9evZ34cPgZXuGuQVsvrKYRhfKuZTZCJZ6u1CDzawMlo1BCcQJBAJdDkCkemUW9q3a30F4kcM+EU9WvQyiiCNqNJA2/5B3x3XElCu0FrjBip2SP8wq7SRH1iCjnZxwWYXLJRvXs460CQQDCroGyv0H9gpXaBFWEVNVZz0jm0BAKJIRADv/3S+Z2BfDQEkqt3FiD6PQCyCH3WY166/+KmXzoNKTS4xd6lRF9");
            log.info("sign={}",sign);
            Boolean flag =  signerDemo.verify("abcj","utf-8","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfniH8yRLMcCfCavmBkYnJVr6nvyWXEtThPtp8r6P7uFgC8OKxnq8RkT6Buwx/nK+GVrSK+Gsxt4LqpQKmI/364ZgQQ6iG2nJuKJ4L5Khy/c5jgvpyabMpfTXBrk0IzLrhq6kOhcSW1DT0kYI1x+OMcuOv4qV5Gp2kfM21pi7GzQIDAQAB",sign);
            log.info("flag = {}",flag);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean flag = StringUtils.isPhone("19921600416");*/
        Integer time = 1631858127;
        String time_str= DateUtil.date(time).toString();
        log.info("{}",time_str);
        String time_str2 = OrderUtil.stampToDate(time.toString());
        Date date = DateUtil.parse(time_str2);
        log.info("{}",date);
    }




}
