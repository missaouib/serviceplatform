package co.yixiang.modules.gjp.Lib;


public class ConfigTest {
	public static String api_link = "http://ca.mygjp.com:8002/api";
	public static String companyName = "TestMall";
	public static String userName = "zhouhang";
	public static String userpass = "Yiyao2020#";
	public static String appkey = "68943923115886070418838901844741";
	public static String app_secret = "ONxYDyNaCoyTzsp83JoQ3YYuMPHxk3j7";
	public static String sign_key = "lezitiancheng";

/*	public static String appkey = "15844344740345131832074881228762";
	public static String app_secret = "Fw141jNmtAP8OkGG6awh3T2mtLwYCTsr";
	public static String sign_key = "Yiyao2020#";*/

	public static String get_token_url = "http://ca.mygjp.com:8002/api/token";
	public static String redirect_url =  "http://localhost:8080/GetToken/GetToken.jsp";
	public static String auth_code_url = String.format("%sappkey=%s&redirect_url=%s&keyword=test", "http://ca.mygjp.com:666/account/login?",appkey,redirect_url);
	public static String shop_key = "f7f352a1-992e-4ab4-950a-de374af6335b";
	public static String token = "zK4f4hRxpwehi651mBPM3gkeQEqw2BH96SG32kOr";
	public static String get_authcode_url = "http://ca.mygjp.com:8002/api/login";


	public String GetAuthCodeUrl(String redirect_url)
	{
		  String ret = String.format("%sappkey=%s&redirect_url=%s&keyword=test", "http://ca.mygjp.com:666/account/login?",appkey,redirect_url); ;
		  
		return ret;
	}
}
