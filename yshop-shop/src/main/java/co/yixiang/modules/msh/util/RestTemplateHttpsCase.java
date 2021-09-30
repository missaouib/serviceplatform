package co.yixiang.modules.msh.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 通过RestTemplate调用Https接口
 */
public class RestTemplateHttpsCase {

	public static void main(String[] args) {
		try {
            /*
             * 请求有权威证书的地址
             */
			String requestPath = "https://www.baidu.com/";
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			ResponseEntity<String> response = template.getForEntity(requestPath, String.class);
			System.out.println("get1返回结果：" + response.getBody());

            /*
             * 请求自定义证书的地址
             */
			//获取信任证书库
			KeyStore trustStore = getkeyStore("jks", "d:/temp/cacerts", "123456");

			//不需要客户端证书
			requestPath = "https://10.40.x.x:9010/zsywservice";
			template = new RestTemplate(new HttpsClientRequestFactory(trustStore));
			template.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			response = template.getForEntity(requestPath, String.class);
			System.out.println("get2返回结果：" + response.getBody());

			//需要客户端证书
			requestPath = "https://10.40.x.x:9016/zsywservice";
			KeyStore keyStore = getkeyStore("pkcs12", "d:/client.p12", "123456");
			template = new RestTemplate(new HttpsClientRequestFactory(keyStore, "123456", trustStore));
			template.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			response = template.getForEntity(requestPath, String.class);
			System.out.println("get3返回结果：" + response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取证书
	 * @return
	 */
	private static KeyStore getkeyStore(String type, String filePath, String password) {
		KeyStore keySotre = null;
		FileInputStream in = null;
		try {
			keySotre = KeyStore.getInstance(type);
			in = new FileInputStream(new File(filePath));
			keySotre.load(in, password.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return keySotre;
	}

	/**
	 * 扩展SimpleClientHttpRequestFactory以支持Https
	 */
	private static class HttpsClientRequestFactory extends SimpleClientHttpRequestFactory {
		private KeyStore keyStore;
		private String keyStorePassword;
		private KeyStore trustStore;

		public HttpsClientRequestFactory(KeyStore keyStore, String keyStorePassword, KeyStore trustStore) {
			this.keyStore = keyStore;
			this.keyStorePassword = keyStorePassword;
			this.trustStore = trustStore;
		}

		public HttpsClientRequestFactory(KeyStore trustStore) {
			this.trustStore = trustStore;
		}

		@Override
		protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
			try {
				if (!(connection instanceof HttpsURLConnection)) {
					throw new RuntimeException("An instance of HttpsURLConnection is expected");
				}
				HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;

				KeyManager[] keyManagers = null;
				TrustManager[] trustManagers = null;
				if (this.keyStore != null) {
					KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
					keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
					keyManagers = keyManagerFactory.getKeyManagers();
				}
				if (this.trustStore != null) {
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
					trustManagerFactory.init(trustStore);
					trustManagers = trustManagerFactory.getTrustManagers();
				} else {
					trustManagers = new TrustManager[] { new DefaultTrustManager()};
				}

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyManagers, trustManagers, null);
				httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
				//验证URL的主机名和服务器的标识主机名是否匹配
				httpsConnection.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String s, SSLSession sslSession) {
						//if ("xxx".equals(hostname)) {
						//    return true;
						//} else {
						//    return false;
						//}
						return true;
					}
				});

				super.prepareConnection(httpsConnection, httpMethod);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
