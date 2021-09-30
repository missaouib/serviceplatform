package co.yixiang.modules.xikang.tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SignatureUtil {

	public static String getOauth(String url, String accessToken, String clientSecret) throws Exception {

		Map<String, String> paramsMap = new HashMap<String, String>();
		// 如果有参数，得到URL地址"?"后面的参数，并将参数与值放到map中
		int existParams = url.indexOf("?");
		if (existParams != -1) {
			String paramsStr = url.split("[?]")[1];
			String[] params = paramsStr.split("&");
			for (String string : params) {
				paramsMap.put(string.split("=")[0], string.split("=")[1]);
			}
		}
		// 加入access_token与时间戳参数
		paramsMap.put("access_token", accessToken);
		paramsMap.put("time", String.valueOf(System.currentTimeMillis()));
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(paramsMap);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();
		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			basestring.append(param.getKey()).append("=").append(param.getValue());
		}
		// 将客户端密码(应用注册时得到)附加到参数最后
		basestring.append(clientSecret);
		// 使用MD5对待签名串求签
		byte[] bytes = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
		} catch (GeneralSecurityException ex) {
			throw new IOException(ex);
		}
		// 将MD5输出的二进制结果转换为小写的十六进制
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		// 返回签名后的URL
		StringBuilder valueUrl = null;
		if (existParams != -1) {
			valueUrl = new StringBuilder(url)
					.append("&access_token=")
					.append(accessToken)
					.append("&time=")
					.append(paramsMap.get("time"))
					.append("&sign=")
					.append(sign);
		} else {
			valueUrl = new StringBuilder(url).append("?access_token=")
					.append(accessToken).append("&time=")
					.append(paramsMap.get("time")).append("&sign=")
					.append(sign);
		}
		return valueUrl.toString();
	}
	
	public static String getOauth(String url, Map<String, String> paramMap, String accessToken, String clientSecret) throws Exception {

		Map<String, String> paramsMap = new HashMap<String, String>();
		// 如果有参数，得到URL地址"?"后面的参数，并将参数与值放到map中
		int existParams = url.indexOf("?");
		if (existParams != -1) {
			String paramsStr = url.split("[?]")[1];
			String[] params = paramsStr.split("&");
			for (String string : params) {
				paramsMap.put(string.split("=")[0], string.split("=")[1]);
			}
		}
		paramsMap.putAll(paramMap);
		// 加入access_token与时间戳参数
		paramsMap.put("access_token", accessToken);
		paramsMap.put("time", String.valueOf(System.currentTimeMillis()));
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(paramsMap);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();
		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			basestring.append(param.getKey()).append("=").append(param.getValue());
		}
		// 将客户端密码(应用注册时得到)附加到参数最后
		basestring.append(clientSecret);
		// 使用MD5对待签名串求签
		byte[] bytes = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
		} catch (GeneralSecurityException ex) {
			throw new IOException(ex);
		}
		// 将MD5输出的二进制结果转换为小写的十六进制
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		// 返回签名后的URL
		StringBuilder valueUrl = null;
		if (existParams != -1) {
			valueUrl = new StringBuilder(url).append("&access_token=")
					.append(accessToken)
					.append("&time=")
					.append(paramsMap.get("time"))
					.append("&sign=")
					.append(sign);
		} else {
			valueUrl = new StringBuilder(url).append("?access_token=")
					.append(accessToken)
					.append("&time=")
					.append(paramsMap.get("time"))
					.append("&sign=")
					.append(sign);
		}
		return valueUrl.toString();
	}
}