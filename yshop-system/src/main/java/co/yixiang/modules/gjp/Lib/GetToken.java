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
public class GetToken {
	@Autowired
	private Config config;

	public String DoGetToken(String authCode) throws Exception
	{
	//	String code = GetAuthCode(param);

            //???p????		
		    JSONObject obj = new JSONObject();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//?????????????????
			Date now = new Date(); 
			String time = dateFormat.format(now);
	        obj.put("TimeStamp", time);
		    obj.put("GrantType", "auth_token");
	        obj.put("AuthParam", authCode);
			String jsonParam = obj.toString();
			AESCoder coder = new AESCoder();
			String p = coder.encrypt(jsonParam, config.getApp_secret());
			
			//???sign
		    JSONObject signObj = new JSONObject();
		    signObj.put("appkey", config.getAppkey());
		    signObj.put("p", p);
		    signObj.put("signkey", config.getSign_key());
			String sign = coder.SHA256(signObj.toString());
			
		    //??????
			Map<String, String> map = new HashMap<String, String>();
	        map.put("appkey",config.getAppkey());
	        map.put("p",  URLEncoder.encode(p, "utf-8"));
	        map.put("sign",sign);
	        String postString = ""; 
	        for (String in : map.keySet()) {
	            postString += in + "=" +map.get(in) +"&";
	        }
	        postString = postString.substring(0, postString.length() - 1);
	       String ret =  HttpRequest.sendPost(config.getGet_token_url(), postString);

	       log.info("get token result -> {}",ret);
	       //??????????
	       JSONObject jsonObject=JSONObject.fromObject(ret);
	       String resp = jsonObject.getJSONObject("response").get("response").toString();
	       String token = coder.decrypt(resp,  config.getApp_secret());
		return token;		
	}
	
	public String GetAuthCode(String param) throws Exception
	{
		String AuthCode = "";
		String[] params = param.trim().split("&");
		  for(int i = 0; i < params.length; i++){  
              if(params[i].toLowerCase().indexOf("auth_code") >= 0 && params[i].toLowerCase().indexOf("=") >= 0)
              {
            	  AuthCode = params[i].substring(params[i].toLowerCase().indexOf("="));
            	  break;
              }
          }  
		  if(AuthCode != "" && AuthCode.length() > 1)
		  {
			  AuthCode = AuthCode.substring(1);
		  }
		  else
		  {
			  throw new Exception("???????code???");
		  }
		return AuthCode;
	}
	
}


