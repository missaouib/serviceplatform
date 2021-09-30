package co.yixiang.modules.xikang.tools;

import cn.hutool.json.JSONUtil;

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

	}

}
