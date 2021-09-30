package co.yixiang.modules.gjp.Lib;


public class ConfigBack {
	public static String api_link = "http://apigateway.wsgjp.com.cn/api/";
	public static String companyName = "上海医药众协药业有限公司";
	public static String userName = "hzcxfwzx";
	public static String userpass = "jiang2020@";
	public static String appkey = "15844344740345131832074881228762";
	public static String app_secret = "Fw141jNmtAP8OkGG6awh3T2mtLwYCTsr";
	public static String sign_key = "Yiyao2020#";

/*	public static String appkey = "15844344740345131832074881228762";
	public static String app_secret = "Fw141jNmtAP8OkGG6awh3T2mtLwYCTsr";
	public static String sign_key = "Yiyao2020#";*/

	public static String get_token_url = "http://apigateway.wsgjp.com.cn/api/token";
	public static String redirect_url =  "http://localhost:8080/GetToken/GetToken.jsp";
	public static String auth_code_url = String.format("%sappkey=%s&redirect_url=%s&keyword=test", "http://ca.mygjp.com:666/account/login?",appkey,redirect_url);
	public static String shop_key = "e097783b-ffd2-4297-b0ff-c8227ddb7ed6";
	public static String token = "zK4f4hRxpwehi651mBPM3gkeQEqw2BH96SG32kOr";
	public static String get_authcode_url = " http://apigateway.wsgjp.com.cn/api/login";


	public String GetAuthCodeUrl(String redirect_url)
	{
		  String ret = String.format("%sappkey=%s&redirect_url=%s&keyword=test", "http://ca.mygjp.com:666/account/login?",appkey,redirect_url); ;
		  
		return ret;
	}
}
