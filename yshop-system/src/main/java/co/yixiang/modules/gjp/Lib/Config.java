package co.yixiang.modules.gjp.Lib;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gjp")
public class Config {

	private String api_link ;
	private String companyName ;
	private String userName;
	private String userpass;
	private String appkey ;
	private String app_secret ;
	private String sign_key ;

/*	public static String appkey = "15844344740345131832074881228762";
	public static String app_secret = "Fw141jNmtAP8OkGG6awh3T2mtLwYCTsr";
	public static String sign_key = "Yiyao2020#";*/

	private String get_token_url ;
	private String redirect_url ;
	private String auth_code_url ;
	private String shop_key ;
	private String token ;
	private String get_authcode_url ;


	public String GetAuthCodeUrl(String redirect_url)
	{
		  String ret = String.format("%sappkey=%s&redirect_url=%s&keyword=test", "http://ca.mygjp.com:666/account/login?",appkey,redirect_url); ;
		  
		return ret;
	}
}
