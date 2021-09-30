package co.yixiang.modules.manage.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.order.entity.UserAgreement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Slf4j
public class CheckOneService {

    @Autowired
    private RestTemplate restTemplate;


    private String appcode = "967763c00eea4831894996463ba7b1e0";
    private String host = "https://zpc.market.alicloudapi.com";
    private String path = "/efficient/cellphone";
    private String verifyIdcardv2Url = "https://zidv2.market.alicloudapi.com/idcard/VerifyIdcardv2";
    private String appcode2 = "967763c00eea4831894996463ba7b1e0";
    public Boolean check(String idcard,String realName,String mobile) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "APPCODE " + appcode);

        HttpEntity requestEntity = new HttpEntity( headers);

        String url = host + path + "?idCard=%s&mobile=%s&realName=%s";
        url = String.format(url,idcard, mobile,realName);
        log.info("实名认证sendRequest 发送参数 {}",url);
        ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = resultEntity.getBody();
        log.info("实名认证sendRequest 返回结果 {}",body);
        if(JSONUtil.isJson(body) && JSONUtil.parseObj(body).getInt("error_code") == 0 && "1".equals(JSONUtil.parseObj(body).getJSONObject("result").getStr("VerificationResult") )) {
             return true;
        } else {
            return false;
        }
    }


    public Boolean check(String idcard,String realName) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "APPCODE " + appcode2);

        HttpEntity requestEntity = new HttpEntity( headers);

        String url = verifyIdcardv2Url + "?cardNo=%s&realName=%s";
        url = String.format(url,idcard,realName);
        log.info("实名认证sendRequest 发送参数 {}",url);
        ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = resultEntity.getBody();
        log.info("实名认证sendRequest 返回结果 {}",body);
        if(JSONUtil.isJson(body) && JSONUtil.parseObj(body).getInt("error_code") == 0 && JSONUtil.parseObj(body).getJSONObject("result").getBool("isok") ) {
            return true;
        } else {
            return false;
        }
    }
}
