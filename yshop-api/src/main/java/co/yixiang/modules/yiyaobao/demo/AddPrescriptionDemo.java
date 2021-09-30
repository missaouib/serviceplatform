package co.yixiang.modules.yiyaobao.demo;

import java.net.URL;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.yiyaobao.entity.PrescriptionDTO;
import co.yixiang.utils.Base64Util;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static co.yixiang.utils.ImageUtil.encodeImageToBase64;

public class AddPrescriptionDemo {

	private static final String APP_ID = "ZXYDZSXLD";
	private static final String APP_SECRET = "b6c616c4-d8ff-451a-85fd-027e6ea40579";
	private static final String ADD_SINGLE_PRS_URL = "https://www.yiyaogo.com/apitest/prescriptionService/addSingle";


	public static void addSinglePrescription() {
		 String sellerId = "85";
		 String patientName = "zhouhangTest";
		 String patientMobile = "18017890127";
		 String verifyCode ="121030";
		 String checkCode ="";
		 String provinceCode ="5";
		 String cityCode ="258";
		 String districtCode="3203" ;
		 String address ="龙华中路600号";
		 String customerRequirement ="测试接口";
		 String imagePath = "";
		 String projectNo = "202002170001";
		 String items ;
		JSONObject itemsJson = new JSONObject();
		itemsJson.element("sku","010313030");
		itemsJson.element("unitPrice","0.1");
		itemsJson.element("amount","2");

		JSONArray jsonArray = new JSONArray();
		jsonArray.add(itemsJson);

		String imgFilePath="http://d.hiphotos.baidu.com/image/pic/item/a044ad345982b2b713b5ad7d3aadcbef76099b65.jpg";
		String[] strArray = StrUtil.split(imgFilePath,".");
		String suffix = strArray[strArray.length-1];
		String base64_str = "";
		try {
			base64_str =  encodeImageToBase64(new URL(imgFilePath));//将网络图片编码为base64
		} catch (Exception e) {
			e.printStackTrace();
		}

		if("jpg".equals(suffix) || "jpeg".equals(suffix) ) {
			suffix = "jpeg";
		} else {
			suffix = "png";
		}

		imagePath = "data:image/"+ suffix+ ";base64," + base64_str;
 		items = jsonArray.toString();

		PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
		prescriptionDTO.setAddress(address);
		prescriptionDTO.setCityCode(cityCode);
		prescriptionDTO.setCustomerRequirement(customerRequirement);
		prescriptionDTO.setDistrictCode(districtCode);
		prescriptionDTO.setImagePath(imagePath);
		prescriptionDTO.setItems(items);
		prescriptionDTO.setPatientMobile(patientMobile);
		prescriptionDTO.setPatientName(patientName);
		prescriptionDTO.setProjectNo(projectNo);
		prescriptionDTO.setProvinceCode(provinceCode);
		prescriptionDTO.setSellerId(sellerId);
		prescriptionDTO.setVerifyCode(verifyCode);

		String requestBody = JSONObject.fromObject(prescriptionDTO).toString(); // 将处方对象转成json
		System.out.println(requestBody);

		JSONObject jsonObject = new JSONObject();
		jsonObject.element("patientName","zhouhangtest");
		jsonObject.element("patientMobile","18017890127");
		jsonObject.element("codeType","h5");
		System.out.println(jsonObject.toString());

        String image = prescriptionDTO.getImagePath();
		if (image.startsWith("data:image/jpeg;base64,")) {
			image = image.substring(23);
		} else if (image.startsWith("data:image/png;base64,")) {
			image = image.substring(22);

		}
		Base64Util.base64StringToFile(image,"E:/aa.jpg");

		/*try {
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
		}*/
	}



	public static void main(String args[]) {
		addSinglePrescription();
	}


}
