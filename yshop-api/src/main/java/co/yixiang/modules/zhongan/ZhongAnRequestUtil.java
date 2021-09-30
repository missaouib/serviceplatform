package co.yixiang.modules.zhongan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.ProjectNameEnum;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ZhongAnRequestUtil {
    private final static Logger LOG = LoggerFactory.getLogger(ZhongAnRequestUtil.class);

    //获取优惠券
    public static String syncCoupons(ZhongAnParamDto param,String appKey,String version,String privateKey,String url) {
        JSONObject template = new JSONObject();
        template.put("platformCode", "SYY");

        if(StringUtils.isNotEmpty(param.getCardNumber())){
            template.put("userId", param.getCardNumber());
        }
        if(StringUtils.isNotEmpty(param.getCouponNo())){
            template.put("userCouponNo", param.getCouponNo());
        }
        template.put("couponUseScope", "mallDrugs");
        /*1：满减券
        2：折扣券
        3：抵用券*/
        String ids = "1,2";
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        template.put("couponTypeList", idList );

        ZhongAnRequest zhongAnRequest = new ZhongAnRequest();
        zhongAnRequest.setAppKey( appKey);//   ZhongAnProperties.appKey   "test1"
        zhongAnRequest.setCharset("utf-8");
        zhongAnRequest.setVersion(version); //ZhongAnProperties.version  "1.0"
        zhongAnRequest.setFormat("json");
        zhongAnRequest.setServiceName("za.hospital.query.user.coupons");
        zhongAnRequest.setBizContent(template.toString());
        zhongAnRequest.setSignType("RSA2");
        zhongAnRequest.setTimestamp(DateUtil.format(DateUtil.date(),"yyyyMMddHHmmss"));

        String plaintext = SignUtils.getSignContent(JSONUtil.parseObj(zhongAnRequest));
        String sign = "";
        LOG.info("众安报文：{}",plaintext);
        LOG.info("privateKey={}",privateKey);//ZhongAnProperties.privateKey  "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDOB+7d4u08HvfvzynZGZ1FMfKi/4kravQbBd+6O38Yn/b8AfOy+vHHm6CRFOwpAGT503SliRJgSBs6vCwrdAXZctYRzKkU7l4gCFMG8rL0kX1KWzYi3sQlsBylPay7cXVcjZiBNrxI752753L1C+JFP6yfbOSV2xn3xB4KlMRtLDcDYWHa2ffpS9nuFzY9YF5zt+63yxUhhQxe9ISeS1K+1YGrQCemjZG8908e5kuq/ywcL/lhHB4BJ8xEp0u9OuyUTCnJy84Y/ifTsxog8BXuDRPyzEChplssNUjH1RgHjpeCOPImypGuvbXq47KhEEPUW2qho5zIX0+UcdsZJ0XRAgMBAAECggEAXZLCzSnUf1q9VsArDHwSrquZvKf8X6jKxz8qtoVxGvkEDr7ANQi+KN8o1NvAynpwYfrE3q3bl7kIDOwLz4x5X6JFUX43SNdeDoRZWS1/U46EbfHxK3MreMZ8rBvPyK4mFGwG2KDIcQPLCt16m4rTMIpT13B4fQsuxxXeYwXgFIiQpSbVdWadnoZvZRwDeJF3p2kMOx9THnaMNnVakBuSKgpRd18tq4MnVRLreNk5rdtEvUtRtdINUgIAjCkFISw3XgeodRYCYG04EJtO7CEQNAzirZTUXaRjfFa1WXu33xrYyYAJdtYP/Q0fxY60xL8hcAAc2Zhaee6IsJPnuYPcBQKBgQDq549qQ/DzZV7FoIv51VIhGoJOwAI1XJtTvKBtzfjX4oKDmmjOzU4tVgCryxxU53IPwd8oXmvNKJkXVH4q8h4ILwBLBiaSEPtuIjcVSbo7Z99xY2Vt2z/17h1pFLYdJSdP+yDs7iDQKZe7BmdduOys9bRr+OCrVwDerEP/oEziewKBgQDgiJJtsT60z7cM5Jyc7VvF6VnsRZuQQesicJ7AbJ8k7zCjCeCv4/LLr6VUsWs2yW0KzXG5A2nJ70uBNBe+W08vOD7jNFPvyEQEh4+39IfBWVDzq13lcW1X//OU3zUoftXiqgxxtABVB6Mly6skex4KUN1uDt4I5P8oNnvCiPc9IwKBgQDQAgaz8b++uAgI9lac/3H/kErNUydhe0SsDL7/HMH64T/zK1sdrR1J9fsYJP5MjLorC+EBDUNmY0nVJ+OlQcqoMn6O8L5c357VcoTWW/gGPL/W105szhZAPv9aGpX9DvZV06nfRCpYSkxqt4v2qRcjPVvrtHG2J4/EnkSEar1KWwKBgQCxeuKbuD3DuGiN1WsCFBC1uMUuoLrdZW2SVIj3uyR0kmjUhutGvRze6iD6eB8yODdsEYax4sPNLcx1/ZJDEnPd9EypVWR/pcI1/l2Y374rFAmMAkn/IhB3PcbxRxoCv3cbaqTZf5m/nIDWUE4gUP0m1FKjOzdAupoB1EcxNwiPFwKBgQCBYKck0H/Yl3Z7RHAjpxsoV5gZnljxU0WDa7/QW0xr/c2SBcFY1tr7sV3wiz3bEo35Zou7xd++PCetCmd+5TaQ0nNwye1L75q6x9spzb64WHMtuXfZdCB9nWcKtVx1iFNpaqY6EM8poJ2/5kPTyu+qGm7XTEJNg4gpXEtGjhLWOw=="
        try {
            RSA2SignerDemo signerDemo = new RSA2SignerDemo();
            sign = signerDemo.sign(plaintext,"utf-8",privateKey); //ZhongAnProperties.privateKey  "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDOB+7d4u08HvfvzynZGZ1FMfKi/4kravQbBd+6O38Yn/b8AfOy+vHHm6CRFOwpAGT503SliRJgSBs6vCwrdAXZctYRzKkU7l4gCFMG8rL0kX1KWzYi3sQlsBylPay7cXVcjZiBNrxI752753L1C+JFP6yfbOSV2xn3xB4KlMRtLDcDYWHa2ffpS9nuFzY9YF5zt+63yxUhhQxe9ISeS1K+1YGrQCemjZG8908e5kuq/ywcL/lhHB4BJ8xEp0u9OuyUTCnJy84Y/ifTsxog8BXuDRPyzEChplssNUjH1RgHjpeCOPImypGuvbXq47KhEEPUW2qho5zIX0+UcdsZJ0XRAgMBAAECggEAXZLCzSnUf1q9VsArDHwSrquZvKf8X6jKxz8qtoVxGvkEDr7ANQi+KN8o1NvAynpwYfrE3q3bl7kIDOwLz4x5X6JFUX43SNdeDoRZWS1/U46EbfHxK3MreMZ8rBvPyK4mFGwG2KDIcQPLCt16m4rTMIpT13B4fQsuxxXeYwXgFIiQpSbVdWadnoZvZRwDeJF3p2kMOx9THnaMNnVakBuSKgpRd18tq4MnVRLreNk5rdtEvUtRtdINUgIAjCkFISw3XgeodRYCYG04EJtO7CEQNAzirZTUXaRjfFa1WXu33xrYyYAJdtYP/Q0fxY60xL8hcAAc2Zhaee6IsJPnuYPcBQKBgQDq549qQ/DzZV7FoIv51VIhGoJOwAI1XJtTvKBtzfjX4oKDmmjOzU4tVgCryxxU53IPwd8oXmvNKJkXVH4q8h4ILwBLBiaSEPtuIjcVSbo7Z99xY2Vt2z/17h1pFLYdJSdP+yDs7iDQKZe7BmdduOys9bRr+OCrVwDerEP/oEziewKBgQDgiJJtsT60z7cM5Jyc7VvF6VnsRZuQQesicJ7AbJ8k7zCjCeCv4/LLr6VUsWs2yW0KzXG5A2nJ70uBNBe+W08vOD7jNFPvyEQEh4+39IfBWVDzq13lcW1X//OU3zUoftXiqgxxtABVB6Mly6skex4KUN1uDt4I5P8oNnvCiPc9IwKBgQDQAgaz8b++uAgI9lac/3H/kErNUydhe0SsDL7/HMH64T/zK1sdrR1J9fsYJP5MjLorC+EBDUNmY0nVJ+OlQcqoMn6O8L5c357VcoTWW/gGPL/W105szhZAPv9aGpX9DvZV06nfRCpYSkxqt4v2qRcjPVvrtHG2J4/EnkSEar1KWwKBgQCxeuKbuD3DuGiN1WsCFBC1uMUuoLrdZW2SVIj3uyR0kmjUhutGvRze6iD6eB8yODdsEYax4sPNLcx1/ZJDEnPd9EypVWR/pcI1/l2Y374rFAmMAkn/IhB3PcbxRxoCv3cbaqTZf5m/nIDWUE4gUP0m1FKjOzdAupoB1EcxNwiPFwKBgQCBYKck0H/Yl3Z7RHAjpxsoV5gZnljxU0WDa7/QW0xr/c2SBcFY1tr7sV3wiz3bEo35Zou7xd++PCetCmd+5TaQ0nNwye1L75q6x9spzb64WHMtuXfZdCB9nWcKtVx1iFNpaqY6EM8poJ2/5kPTyu+qGm7XTEJNg4gpXEtGjhLWOw=="
        } catch (Exception e) {
            e.printStackTrace();
        }
        zhongAnRequest.setSign(sign);

        String jsonParam= JSONUtil.parseObj(zhongAnRequest).toString();
        LOG.info("发送众安的消息:{}", jsonParam);
        LOG.info("众安url：{}",url);
        String jsonBody = HttpUtil.post( url,jsonParam);  //ZhongAnProperties.url    "https://open-tst.za-doctor.com/open/gateway"
        LOG.info("众安返回的消息:{}",jsonBody);

        return jsonBody;
    }

    public static void main(String[] args) {
        ZhongAnParamDto param=new ZhongAnParamDto();
        param.setCardNumber("1705018");
        param.setProjectCode("zhonganpuyao");
        syncCoupons(param, "test1", "1.0","MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCfgrN/isAPYirFRN+FQKYPydbx0Pl0IadKrNWvK5liaXMn00xbeYjAVE8KxMgIhN6Nu9p6UHOIpwoO3c59yxyn1SBgTpU1Soul9GZF29SAGzQWc019QV3wPj03zf6G0K03S451ki9ikL5DZxWLgTduu/FL1K9DKFxaXi23EFHLwrBQh0Y7AH/gRMmHAMrnVaeDUXrOOcdOfTZIgpKsFCisz+S9N64aTHJ0+/wyYuWV6nF9/LT43gGsIGD3yFaSr1WicSBdAPITHmWDrpYBJy4A2nIemCsnhNVcj77mXsWLwJe9V8jTEKiQrkPSPQnk/qTXbFmAyaTJuq2bn+F6Zsn3AgMBAAECggEBAIHrT84rMeGVwGZqgXAkRt2HNWZCIlvfaUXeXjFN3qGUZ/HhHUlIRQT55mNMAe1GY4qnnfyP+HouK3gOWziELbPZWIR4nPkJ3ZJu7Lorofoxrgw3H79MdXnPB8ejJZOi9eKazM6gIa4zXHTDyn2U4pRzycZM1e9qOUZ1fOClDypY//UszJCgiauu7ADDMbH5GBZUO3ZtqxTnC10SA+8XrbF48rooU3evmg3j6AW0VTZa1uKPirEMPJDpJY0NJ+jLSHSzTJS7mIuQhc/2XiAqcxom5srPf36g1j10I894NCgsf6bVQdVZVOBtA5cqIOLbKhUOSHaT1XJLe85KzMhSmxkCgYEA1De8X9xdY/Z7ADPaNwMl9uZnJVYmRHrjnCNYuDc2hZDHZPOa2paskGaRsNjkfIWXzhcJz3giyDxAYDN623vGdqplOu95axJD+I8PpvzaFnkuu0HIoqzUh4stDwxRaSszdW1EKw8IJrEXHs7Ukx96N+IzN+HQ0x3VfuslwzJuehUCgYEAwGs9bVQw8jd4Ig9YbIzCFswJzPv9zqMmusfaTwK2AnwWSow+fO3VhYRmMIEIw0L6DpakVXDkDvbAVWAAtButBFuYk90q2GrrO8CNC2frLrXenQdIHp5viTuboJabg07zuBKUTlt0BAo3gnSlLzxvnw8+co6hg7Q53icq7ZorctsCgYEAhyqQ0sW6vgapxTFBlbRto9qQa9l5OjjDbBmfUoXoNh5GweffA8bgVoDd3rPmo+E7FUrbNsef78Vgg2WGpPErMT3KiEUrIqVZoaENKSD3j0TUHPUDY4mNPF/K7UC9Qa0Ac5SKzJogaPR2c2rtPl/YKmvqj3dG2JCyAMqpHLCsaRkCgYBPR2AFvZx1D7mlfuEVyGDnd6XKGUEXdE0uw80EpL9NAsdEa8gLPQquekCIV4G+wce5XwPSWDL+n1fbVTS1AHomw3533bYbeOH2unziaCyUEEbqN+fVBb46sp6KacMlNNBh1PXZ2wBP8c2xKFIuxh3PUT6PgIi3dPgqucSMJOsl5QKBgQCLdeghGfAZpe3kzhl2Fk69mANUe1sx9mXYifcWSZPqR4+nsfGQHbyvicLuSgZlWpnL/N6PeRiKiG3psovtL+VNShLts35Jvg2ucn4TkfZ/S344/MM9+HbK5nVMP6/ne8eKYrQKVBOS2Eu2cIyvwUq+/JWS+kJQ1aJuSL68ti5B3g==", "https://open-pre.za-doctor.com/open/gateway");
    }
}
