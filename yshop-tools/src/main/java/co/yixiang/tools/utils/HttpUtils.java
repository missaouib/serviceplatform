package co.yixiang.tools.utils;

import java.math.BigInteger;
import java.net.URI;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.yixiang.tools.express.support.MyX509TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpUtils {
	private static CloseableHttpClient httpclient = HttpClients.createDefault();
	private static CloseableHttpClient httpsclient;

	static{
		SSLContext sslctxt;
		try {
			sslctxt = SSLContext.getInstance("TLSv1.2");
			sslctxt.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());

		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("https client初始化错误");
		}
		SSLConnectionSocketFactory sslsf =new SSLConnectionSocketFactory(sslctxt);
		httpsclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}



	public static String get(String apiUrl, Map<String,String> paramPairs)
			throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpGet httpget = new HttpGet(apiUrl);
			List<NameValuePair> params=convert(paramPairs);
			String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(
					params));
			httpget.setURI(new URI(apiUrl
					+ (apiUrl.indexOf("?") == -1 ? "?" : "&") + paramsStr));
			httpResponse = httpclient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}

	public static String get(String apiUrl)
			throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpGet httpget = new HttpGet(apiUrl);
			httpResponse = httpclient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}


	public static String postForm(String apiUrl,Map<String,String> paramPairs,Map<String,String> headers) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			List<NameValuePair> params=convert(paramPairs);
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(params, "UTF-8");
			if(headers!=null){
				for(String key:headers.keySet()){
					httpPost.addHeader(key, headers.get(key));
				}
			}
			httpPost.setEntity(uefEntity);
			httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postJson(String apiUrl,String requestBody,Map<String,String> headers) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			StringEntity paramEntity =new StringEntity(requestBody,"UTF-8");
			httpPost.addHeader("content-type", "application/json");
			if(headers!=null){
				for(String key:headers.keySet()){
					httpPost.addHeader(key, headers.get(key));
				}
			}
			httpPost.setEntity(paramEntity);
			httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postJsonHttps(String apiUrl,String requestBody,Map<String,String> headers) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			StringEntity paramEntity =new StringEntity(requestBody,"UTF-8");
			httpPost.addHeader("content-type", "application/json");
			if(headers!=null){
				for(String key:headers.keySet()){
					httpPost.addHeader(key, headers.get(key));
				}
			}
			httpPost.setEntity(paramEntity);
			httpResponse = httpsclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static List<NameValuePair> convert(Map<String,String> pairs){
		List<NameValuePair> result=new ArrayList<NameValuePair>();
           for(String key:pairs.keySet()){
        	   NameValuePair pair = new BasicNameValuePair(key,pairs.get(key));
        	   result.add(pair);
           }
           return result;
	}


}
