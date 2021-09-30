package co.yixiang.tools.express.support;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NameValuePairHelper {


	public static List<NameValuePair> convert(Map<String,String> pairs){
		List<NameValuePair> result=new ArrayList<NameValuePair>();
           for(String key:pairs.keySet()){
        	   NameValuePair pair = new BasicNameValuePair(key,pairs.get(key));
        	   result.add(pair);
           }
           return result;
	}
}
