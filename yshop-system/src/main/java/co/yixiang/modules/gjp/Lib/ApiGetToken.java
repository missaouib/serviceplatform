package co.yixiang.modules.gjp.Lib;


import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class ApiGetToken {
    @Autowired
    private GetToken tokenObj;

    @Autowired
    private Config config;
    public String DoGetAuthCode() throws Exception
    {

        JSONObject pObj = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String time = dateFormat.format(now);
        pObj.put("CompanyName",config.getCompanyName());
        pObj.put("UserId", config.getUserName());
        pObj.put("Password", config.getUserpass());
        pObj.put("TimeStamp", time);

        String jsonParam = pObj.toString();
        AESCoder coder = new AESCoder();
        String p = coder.encrypt(jsonParam, config.getApp_secret());

        JSONObject signObj = new JSONObject();

        signObj.put("appkey", config.getAppkey());
        signObj.put("p", p);
        signObj.put("signkey", config.getSign_key());
        String sign = coder.SHA256(signObj.toString());


        Map<String, String> map = new HashMap<String, String>();
        map.put("appkey",config.getAppkey());
        map.put("p",  URLEncoder.encode(p, "utf-8"));
        map.put("sign",sign);
        String postString = "";
        for (String in : map.keySet()) {
            postString += in + "=" +map.get(in) +"&";
        }
        postString = postString.substring(0, postString.length() - 1);
        String ret =  HttpRequest.sendPost(config.getGet_authcode_url(), postString);
        log.info("get authcode result -> {}",ret);
        JSONObject jsonObject=JSONObject.fromObject(ret);
        String codeResp = jsonObject.getJSONObject("response").get("authcode").toString();
        return codeResp;
    }

    public String DoGetToken()  throws Exception{
      //  GetToken tokenObj = new GetToken();
        String authCode =  DoGetAuthCode();
        String token = tokenObj.DoGetToken(authCode);
    return  token;
    }

    public static void main(String[] args) {
        try {
           // new ApiGetToken().DoGetAuthCode();
           String token = new ApiGetToken().DoGetToken();
           log.info("token={}",token);
        }catch (Exception e) {

        }

    }
}
