package co.yixiang.modules.xikang.tools;

import cn.hutool.json.JSONUtil;
import co.yixiang.modules.hospitaldemand.entity.AttrDTO;
import co.yixiang.modules.xikang.dto.Drugs;
import co.yixiang.modules.xikang.dto.Request;
import co.yixiang.modules.xikang.dto.UploadPatient;
import co.yixiang.tools.express.support.NameValuePairHelper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TokenUtil {

	private static CloseableHttpClient httpClient = HttpClients.createDefault();

	public String getTokenByForm(String oauthURL, String clientID, String clientSecret) throws Exception {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("client_id", clientID));
		formparams.add(new BasicNameValuePair("client_secret", clientSecret));
		formparams.add(new BasicNameValuePair("grant_type", "client_credentials"));
		formparams.add(new BasicNameValuePair("scope", "trust"));
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httpPost = new HttpPost(oauthURL);
		httpPost.setEntity(formEntity);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		String content = EntityUtils.toString(response.getEntity());
		httpPost.releaseConnection();
		return content;
	}

	public String getTokenByHeader(String oauthURL, String clientID, String clientSecret) throws Exception {
		HttpPost httpPost = new HttpPost(oauthURL + "?grant_type=client_credentials&scope=trust");
		httpPost.addHeader("Authorization", "Basic " + Base64.encodeBase64String((clientID + ":" + clientSecret).getBytes()));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		String content = EntityUtils.toString(response.getEntity());
		httpPost.releaseConnection();
		return content;
	}

	public static void main(String[] args) throws Exception {
//		集成环境oauthURL
		String oauthURL = "https://dlpassport.xikang.com/oauth/token"; //"http://dlpassport.xikang.com/oauth/token";
//		XK环境oauthURL
		//String oauthURL = "http://dlpassport.xikang.com/oauth/token";
//		生产环境oauthURL
		//String oauthURL = "http://passport.xikang.com/oauth/token";
		//下面 clientID、clientSecret 为处方流转内部测试使用，其他方使用需要重新申请
		String clientID = "cflzCSZY2019";
		String clientSecret = "54f9a3ea949ab96bd7d87f56b9153c7b";
		String content = new TokenUtil().getTokenByHeader(oauthURL, clientID, clientSecret);
		System.out.println(content);
		Map<String, String> map = new Gson().fromJson(content, Map.class);
		String accessToken = map.get("access_token");
		//集成环境 接口地址
		String resURL = "https://dldoctor.xikang.cn/adapter/openapi/process";
		//XK环境 接口地址
//		String resURL = "http://dldoctor.xikang.cn/adapter-sy/openapi/process";
		//正式环境
		//String resURL = "http://dldoctor.xikang.cn/adapter-sy/openapi/process";
//		HisUploadRegRxHN hisUploadRegRxHN = new HisUploadRegRxHN();
//		hisUploadRegRxHN.setDeptCode("ddd");
		//准备上传医生的参数签名***********开始***********具体参数根据实际接口来组装
		Request request = new Request();
		request.setBusinessType("025");
		request.setMethod("XKP090");
		request.setSystemId("sykg");
		request.setSystemKey("sykg");
		request.setPageNum(0);
		request.setPageSize(10);

		UploadPatient uploadPatient = new UploadPatient();
		uploadPatient.setPatientGender("1");
		uploadPatient.setOpenId("o5KQn0SBILm8vF3gWEgrvfpXbyQg");
		uploadPatient.setPatientIDCard("310227198601271413");
		uploadPatient.setPatientName("周杭");
		uploadPatient.setPatientTel("18017890127");
		uploadPatient.setPatientType("PT");
		uploadPatient.setRequestType("025");
		uploadPatient.setSupplierCode("710172c177db468fa431736aff6b2a90");
		uploadPatient.setSupplierName("广州上药益药药房");
		AttrDTO attrDTO = new AttrDTO();
		attrDTO.setOrderNumber("1233");
		attrDTO.setCardNumber("1111");
		attrDTO.setUid(62);
		String attrs = JSONUtil.parseObj(attrDTO).toString();
		uploadPatient.setAttrs(attrs);

		// 放入 Drugs
		Drugs drugs = new Drugs();
        drugs.setDrugCode("86900555000633b");
        drugs.setDrugName("头孢呋辛酯片 ");
        drugs.setDrugNum("2");
        List<Drugs> drugsList = new ArrayList<>();
		drugsList.add(drugs);
		uploadPatient.setDrugs(drugsList);
	//	String patient = JSONUtil.parseObj(uploadPatient).toString();
		String patient =  JSONMapper.toJSONString(uploadPatient);
		log.info("patient={}",patient);
		request.setData(patient);
		String str = JSONMapper.toJSONString(request);
		// String str = JSONUtil.parseObj(request).toString();
		log.info("str={}",str);
		//准备上传医生的参数签名***********结束***********
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("request", str);
		//生成带有鉴权的URL
		resURL = SignatureUtil.getOauth(resURL, parameters, accessToken, clientSecret);
		System.out.println(resURL);
		//接口调用
	//	JSONObject d = HttpUtils.doPostStr(resURL, parameters);
		List<NameValuePair> params= NameValuePairHelper.convert(parameters);
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
		HttpPost httpPost = new HttpPost(resURL);
		httpPost.setEntity(formEntity);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		String result = EntityUtils.toString(response.getEntity());
		httpPost.releaseConnection();
		log.info("result={}",result);
	}

}
