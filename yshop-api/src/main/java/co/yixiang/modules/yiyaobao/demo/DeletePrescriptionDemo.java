package co.yixiang.modules.yiyaobao.demo;

import java.util.HashMap;
import java.util.Map;

import co.yixiang.tools.utils.AppSiganatureUtils;
import co.yixiang.tools.utils.HttpUtils;
import net.sf.json.JSONObject;

public class DeletePrescriptionDemo {

	private static final String APP_ID = "test002";
	private static final String APP_SECRET = "2770bcb4-7e35-498d-b83e-fe2d5377cf76";
	private static final String DELETE_SINGLE_PRS_URL = "https://www.yiyaogo.com/apitest/prescriptionService/deleteSingle";




	public static void deleteSinglePrescription(){
		Map<String,String> param=new HashMap<String,String>();
		param.put("prsNo", "1234567890123456"); //处方号
		param.put("hospitalName", "测试医院"); //医院名称
		String requestBody = JSONObject.fromObject(param).toString(); // 将处方对象转成json
		System.out.println(requestBody);
		try {
			long timestamp = System.currentTimeMillis(); // 生成签名时间戳
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("ACCESS_APPID", APP_ID); // 设置APP
			headers.put("ACCESS_TIMESTAMP", String.valueOf(timestamp)); // 设置签名时间戳
			headers.put("ACCESS_SIGANATURE", AppSiganatureUtils
					.createSiganature(requestBody, APP_ID, APP_SECRET,
							timestamp)); // 生成并设置签名
			String result = HttpUtils.postJsonHttps(DELETE_SINGLE_PRS_URL, requestBody,
					headers); // 发起调用
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		deleteSinglePrescription();
	}



}
