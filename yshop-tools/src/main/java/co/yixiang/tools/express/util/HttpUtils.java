package co.yixiang.tools.express.util;


import cn.hutool.json.JSONUtil;
import co.yixiang.tools.express.support.MyX509TrustManager;
import co.yixiang.tools.express.support.NameValuePairHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpUtils {
	private static CloseableHttpClient httpclient = HttpClients.createDefault();
	private static CloseableHttpClient httpsclient;

	static{
		SSLContext sslctxt;
		try {
			sslctxt = SSLContext.getInstance("TLSv1.2");
			sslctxt.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());

		} catch (Exception e){
			throw new RuntimeException("https client初始化错误");
		}
		SSLConnectionSocketFactory sslsf =new SSLConnectionSocketFactory(sslctxt);
		httpsclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	public static String getHttp(String apiUrl, Map<String,String> paramPairs)
			throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpGet httpget = new HttpGet(apiUrl);
			List<NameValuePair> params= NameValuePairHelper.convert(paramPairs);
			String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(
					params));
			httpget.setURI(new URI(apiUrl
					+ (apiUrl.indexOf("?") == -1 ? "?" : "&") + paramsStr));
			httpResponse = httpclient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}

	public static String doGetRequestExc(String urlstr, Map map) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		HttpClient client = new HttpClient(
				new MultiThreadedHttpConnectionManager());
		client.getParams().setIntParameter("http.socket.timeout", 600000);
		client.getParams().setIntParameter("http.connection.timeout", 600000);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");//编码

		HttpMethod httpmethod = new GetMethod(urlstr);
		httpmethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			httpmethod.addRequestHeader(key, val);
		}

		int statusCode = client.executeMethod(httpmethod);
		if (statusCode == HttpStatus.SC_OK) {
			InputStream inputStream = httpmethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			String str= "";
			while((str = br.readLine()) != null){
				stringBuffer.append(str );
			}
		}
		httpmethod.releaseConnection();
		return stringBuffer.toString();
	}

	public static String getHttp(String apiUrl)
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
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}
	public static String getHttps(String apiUrl)
			throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpGet httpget = new HttpGet(apiUrl);
			httpResponse = httpsclient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}

	public static String postHttp(String apiUrl,Map<String,String> paramPairs) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			List<NameValuePair> params=NameValuePairHelper.convert(paramPairs);
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);
			httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String getHttps(String apiUrl,Map<String,String> paramPairs) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpGet httpget = new HttpGet(apiUrl);
			List<NameValuePair> params=NameValuePairHelper.convert(paramPairs);
			String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(
					params));
			httpget.setURI(new URI(apiUrl
					+ (apiUrl.indexOf("?") == -1 ? "?" : "&") + paramsStr));
			httpResponse = httpsclient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                httpResponse.close();
           }
		}
		return body;
	}

	public static String postHttps(String apiUrl,Map<String,String> paramPairs, CloseableHttpClient httpsclientx) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			List<NameValuePair> params=NameValuePairHelper.convert(paramPairs);
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);
			httpResponse = httpsclientx.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postHttps(String apiUrl,Map<String,String> paramPairs) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			List<NameValuePair> params=NameValuePairHelper.convert(paramPairs);
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);
			httpResponse = httpsclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
			if(httpResponse!=null){
				httpResponse.close();
			}
		}
		return body;
	}

	public static String postJsonHttps(String apiUrl,Map<String,Object> paramPairs) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			//String jsonString = JsonUtils.object2Json(paramPairs);
			String jsonString = JSONUtil.parseObj(paramPairs).toString();
			StringEntity paramEntity =new StringEntity(jsonString,"UTF-8");
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(paramEntity);
			httpResponse = httpsclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postJsonHttps(String apiUrl,String requestBody) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			StringEntity paramEntity =new StringEntity(requestBody,"UTF-8");
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(paramEntity);
			httpResponse = httpsclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
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
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postJsonHttps(String apiUrl,String requestBody,Map<String,String> headers, CloseableHttpClient httpsclientx) throws Exception {
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
			httpResponse = httpsclientx.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
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
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}

	public static String postJsonHttpsUtf8(String apiUrl,String requestBody,Map<String,String> headers) throws Exception {
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
			body = EntityUtils.toString(entity,"UTF-8");
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
			if(httpResponse!=null){
				httpResponse.close();
			}
		}
		return body;
	}

	public static String putXmlHttps(String apiUrl,String requestBody) throws Exception {
		return putXmlHttps(apiUrl, requestBody, null);
	}

	public static String putXmlHttps(String apiUrl,String requestBody,Map<String,String> headers) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPut httpPost = new HttpPut(apiUrl);
			StringEntity paramEntity =new StringEntity(requestBody,"UTF-8");
			httpPost.addHeader("content-type", "application/xml");
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
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}


	/**
	 *  TODO HttpURLConnection
	 * @param toURL
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String requestService(String toURL,String data) throws Exception {
		StringBuffer bs = new StringBuffer();
		BufferedReader in = null;
		OutputStream out = null;
		try {
			URL url = new URL(toURL);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("POST");
			urlcon.setUseCaches(false);
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.setDoInput(true);
			urlcon.setDoOutput(true);
			urlcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			out = urlcon.getOutputStream();
			out.write(data.getBytes("UTF-8"));
			out.flush();
			out.close();
			urlcon.connect();
			in= new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

			String l = null;
			while ((l = in.readLine()) != null) {
				bs.append(l);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				throw new Exception(ex);
			}
		}
		return bs.toString();
	}

	public static String postXmlHttps(String apiUrl,String requestBody) throws Exception {
		return postXmlHttps(apiUrl, requestBody, null);
	}

	public static String postXmlHttps(String apiUrl,String requestBody,Map<String,String> headers) throws Exception {
		String body = null;
		CloseableHttpResponse httpResponse=null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			StringEntity paramEntity =new StringEntity(requestBody,"UTF-8");
			httpPost.addHeader("content-type", "application/xml");
			if(headers!=null){
				for(String key:headers.keySet()){
					httpPost.addHeader(key, headers.get(key));
				}
			}
			httpPost.setEntity(paramEntity);
			httpResponse = httpsclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			body = EntityUtils.toString(entity,"UTF-8");
			EntityUtils.consume(entity);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
            if(httpResponse!=null){
                 httpResponse.close();
            }
		}
		return body;
	}
	
	public static void main(String[] args) {
		try {
			String s = postXmlHttps("https://demo.otms.cn/ws/orderImport", "", null);
			System.out.println("s:"+s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
