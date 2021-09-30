/**
 *  www.meditrusthealth.com Copyright © MediTrust Health 2017
 */
package co.yixiang.modules.zhongan;

import cn.hutool.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author
 * @date 2018年1月26日 上午9:51:57
 * @version 1.0.0
 */
public class SignUtils {
	
	public static String genWithAmple(String... arr) {
		if (StringUtils.isAnyEmpty(arr)) {
			throw new IllegalArgumentException("非法请求参数，有部分参数为空 : " + Arrays.toString(arr));
		}

		Arrays.sort(arr);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			String a = arr[i];
			sb.append(a);
			if (i != arr.length - 1) {
				sb.append('&');
			}
		}



		return sb.toString();
	}


	public static String getSignContent(JSONObject jo) {
		StringBuilder content = new StringBuilder();
		List<String> keys = new ArrayList<String>(jo.keySet());
		Collections.sort(keys);
		int index = 0;
		for (String key : keys) {
			String value = jo.getStr(key);

			if (!StringUtils.isBlank(value) && (",sign," + null + ",").indexOf("," + key + ",") < 0) {

				content.append(index == 0 ? "" : "&").append(key).append("=").append(value.trim());

			}
			index++;
		}
		return content.toString();
	}
}
