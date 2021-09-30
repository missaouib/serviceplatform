package co.yixiang.modules.yiyaobao.demo;

import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QueryOrderStatusDemo {

	private static final String APP_ID = "ZSLXYF";
	private static final String APP_SECRET = "82b099cf-3d9b-45bb-a76c-4942d7c00c0c";
	private static final String ADD_SINGLE_PRS_URL = "https://www.yiyaogo.com/api/externalOrderService/queryOrderStatus";


	public static void queryOrderStatus() {


		JSONObject jsonObject = new JSONObject();
		jsonObject.element("prsNo","");
		jsonObject.element("hospitalName","");
		jsonObject.element("jdOrderId","");
		jsonObject.element("orderNo","2002191623606672");

		String requestBody = jsonObject.toString(); //
		System.out.println(requestBody);
		try {
			long timestamp = System.currentTimeMillis(); // 生成签名时间戳
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("ACCESS_APPID", APP_ID); // 设置APP
			headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
			headers.put("ACCESS_SIGANATURE", AppSiganatureUtils
					.createSiganature(requestBody, APP_ID, APP_SECRET,
							timestamp)); // 生成并设置签名
			String result = HttpUtils.postJsonHttps(ADD_SINGLE_PRS_URL, requestBody,
					headers); // 发起调用
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void main(String args[]) {
		queryOrderStatus();
	}


}
