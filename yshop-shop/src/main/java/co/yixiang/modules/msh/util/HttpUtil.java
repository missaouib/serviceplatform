package co.yixiang.modules.msh.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

/**
 * HttpClientUtils, ?????? HttpClient 4.x<br>
 * 
 */
public class HttpUtil {

	private static HttpClient client = null;
	private final static String DEFAULT_CHARSET = "UTF-8";

	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(128);
		client = HttpClients.custom().setConnectionManager(cm).build();
	}

	public static String post(String url, String body)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {

		return post(url, body, "application/json", "utf-8", 30000, 30000);

	}

	/**
	 * ???????????? Post ??????, ??????????????????????????????.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            ?????? application/xml
	 * @param charset
	 *            ??????
	 * @param connTimeout
	 *            ????????????????????????,??????.
	 * @param readTimeout
	 *            ??????????????????,??????.
	 * @return ResponseBody, ??????????????????????????????.
	 * 
	 * @throws ConnectTimeoutException
	 *             ????????????????????????
	 * @throws SocketTimeoutException
	 *             ????????????
	 * @throws Exception
	 */
	public static String post(String url, String body, String mimeType, String charset, Integer connTimeout,
			Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		post.addHeader("Content-Type", mimeType);
		String result = "";
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);

				post.setEntity(entity);
			}
			// ????????????
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());

			HttpResponse res;
			if (url.startsWith("https")) {
				// ?????? Https ??????.
				client = new SSLClient();
				res = client.execute(post);
 			} else {
				// ?????? Http ??????.
				client = HttpUtil.client;
				res = client.execute(post);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * ??????form??????
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> params)
			throws Exception {
		return postForm(url,null , params,null, null, null);
	}

	/**
	 * ??????form??????
	 *
	 * @param url
	 * @param multipartFile
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, MultipartFile[] multipartFile)
			throws Exception {
		return postForm(url, multipartFile, null, null, null,null);
	}
	/**
	 * ??????form??????
	 * 
	 * @param url
	 * @param params
	 * @param connTimeout
	 * @param readTimeout
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static String postForm(String url , MultipartFile[] multipartFile , Map<String, String> params, Map<String, String> headers,
			Integer connTimeout, Integer readTimeout)
					throws Exception {

		HttpClient client = null;

		HttpPost post = new HttpPost(url);
		try {

			if(multipartFile!=null){
				 //
			}else{
				if (params != null && !params.isEmpty()) {
					List<NameValuePair> formParams = new ArrayList<NameValuePair>();
					Set<Entry<String, String>> entrySet = params.entrySet();
					for (Entry<String, String> entry : entrySet) {
						formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					}
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
					post.setEntity(entity);
				}
			}

			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// ????????????
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());
			HttpResponse res = null;
			if (url.startsWith("https")) {
				// ?????? Https ??????.
				client = new SSLClient();
				res = client.execute(post);
			} else {
				// ?????? Http ??????.
				client = HttpUtil.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * ???????????? GET ??????
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String get(String url) throws Exception {
		return get(url, DEFAULT_CHARSET, null, null);
	}

	/**
	 * ???????????? GET ??????
	 * 
	 * @param url
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, String charset) throws Exception {
		return get(url, charset, null, null);
	}

	/**
	 * ???????????? GET ??????
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            ????????????????????????,??????.
	 * @param readTimeout
	 *            ??????????????????,??????.
	 * @return
	 * @throws ConnectTimeoutException
	 *             ??????????????????
	 * @throws SocketTimeoutException
	 *             ????????????
	 * @throws Exception
	 */
	public static String get(String url, String charset, Integer connTimeout, Integer readTimeout)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;

		/* HttpGet get = new HttpGet(url); */
		// String urlR = URLEncoder.encode(url,charset);
		HttpGet get = new HttpGet(url);

		String result = "";
		try {
			// ????????????
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}

			get.setConfig(customReqConf.build());

			HttpResponse res = null;
			if (url.startsWith("https")) {
				// ?????? Https ??????.
				client = createSSLInsecureClient();
				res = client.execute(get);
			} else {
				// ?????? Http ??????.
				client = HttpUtil.client;
				res = client.execute(get);
			}

			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			get.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}

		return result;
	}

	/**
	 * ??? response ????????? charset
	 * 
	 * @param ressponse
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getCharsetFromResponse(HttpResponse ressponse) {
		// Content-Type:text/html; charset=GBK
		if (ressponse.getEntity() != null && ressponse.getEntity().getContentType() != null
				&& ressponse.getEntity().getContentType().getValue() != null) {
			String contentType = ressponse.getEntity().getContentType().getValue();
			if (contentType.contains("charset=")) {
				return contentType.substring(contentType.indexOf("charset=") + 8);
			}
		}
		return null;
	}

	private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

				@Override
                public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
                public void verify(String host, SSLSocket ssl) throws IOException {
					// TODO Auto-generated method stub

				}

				@Override
                public void verify(String host, X509Certificate cert) throws SSLException {
					// TODO Auto-generated method stub

				}

				@Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
					// TODO Auto-generated method stub

				}

			});
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (GeneralSecurityException e) {
			throw e;
		}
	}

	/**
	 * ????????????
	 * 
	 * @param url
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream getFile(String url) throws ClientProtocolException, IOException {
		// ????????????httpclient??????
		HttpClient httpclient = HttpUtil.client;
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		return in;
	}

	public static void main(String[] args) {

	}
}
