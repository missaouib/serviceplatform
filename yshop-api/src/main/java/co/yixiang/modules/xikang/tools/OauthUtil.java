package co.yixiang.modules.xikang.tools;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OauthUtil {
	private static Log logger = LogFactory.getLog(OauthUtil.class);

	private static CloseableHttpClient httpClient = HttpClients.createDefault();

	private Resp testOauthServer(String oauthURL, String clientID, String clientSecret, long count) throws Exception {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("client_id", clientID));
		formparams.add(new BasicNameValuePair("client_secret", clientSecret));
		formparams.add(new BasicNameValuePair("grant_type", "client_credentials"));
		formparams.add(new BasicNameValuePair("scope", "trust"));
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httpPost = new HttpPost(oauthURL);
		httpPost.setEntity(formEntity);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		Resp resp = this.convertResponse(response, count,"oauth");
		httpPost.releaseConnection();
		return resp;
	}

	private Resp convertResponse(CloseableHttpResponse response, long count,String mark) throws Exception {
		HttpEntity entity = null;
		String content = null;
		int status = response.getStatusLine().getStatusCode();
		entity = response.getEntity();
		content = EntityUtils.toString(entity);
		logger.info("*******"+mark+"******第" + count + "次响应   [content-> " + content + " || HTTP " + status + "]");
		return new Resp(content, status);
	}

	private Resp testResource(String resURL, long count) throws Exception {
		HttpGet httpGet = new HttpGet(resURL);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		Resp resp = this.convertResponse(response, count,"hcservicse");
		httpGet.releaseConnection();
		return resp;
	}

	@SuppressWarnings("unchecked")
	public void go(String resURL, String oauthURL, String clientID, String clientSecret) throws Exception {
		long count = 0;
		int exception = 0;
		long mills = 50;
		Resp respOfResource = null;
		Resp respOfOauth = null;
		logger.info("初始化......");
		respOfOauth = this.testOauthServer(oauthURL, clientID, clientSecret, count);
		String accessToken = (String) new Gson().fromJson(respOfOauth.getContent(), Map.class).get("access_token");
		resURL = SignatureUtil.getOauth(resURL, accessToken, clientSecret);
		logger.info("开始循环......");
		while (true) {
			count++;
			logger.info("第 " + count + " 次请求");
			respOfOauth = this.testOauthServer(oauthURL, clientID, clientSecret, count);
			respOfResource = this.testResource(resURL, count);
			if (respOfResource.getStatus() == 401 && respOfOauth.getStatus() == 200) {
				Map<String, String> map = new Gson().fromJson(respOfOauth.getContent(), Map.class);
				accessToken = map.get("access_token");
				resURL = SignatureUtil.getOauth(resURL, accessToken, clientSecret);
			} else if (respOfResource.getStatus() == 403) {
				exception++;
				logger.info("*******hcservice******第 " + count + " 次 出现 HTTP 403");
				if (exception == 2) {
					logger.info("由于出现2次 HTTP 403响应，程序终止!");
					logger.info("本次总结: 请求总数" + count);
					break;
				}
			}
			logger.info("等待 " + mills + "ms ......");
			Thread.sleep(mills);
		}
	}

	private class Resp {
		private String content;
		private int status;

		public Resp(String content, int status) {
			this.content = content;
			this.status = status;
		}

		public String getContent() {
			return this.content;
		}

		public int getStatus() {
			return this.status;
		}
	}
}
