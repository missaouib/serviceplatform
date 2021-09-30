package co.yixiang.modules.gjp.Lib;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AESCoder {
	
	 final String algorithmStr = "AES/CBC/PKCS5Padding";  
	 final String KEY_ALGORITHM = "AES";  

    //private static 
    public  AESCoder(){

    }

    // ����
    public  String encrypt(String sSrc, String sKey) throws Exception {
    	String ivParameter = sKey.substring(5, 5+16);
        Cipher cipher = Cipher.getInstance(algorithmStr);
        byte[] raw = sKey.getBytes("US-ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("US-ASCII"));//ʹ��CBCģʽ����Ҫһ������iv�������Ӽ����㷨��ǿ��
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
        return new BASE64Encoder().encode(encrypted).replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");//�˴�ʹ��BASE64��ת�롣
}

    // ����
    public  String decrypt(String sSrc, String sKey) throws Exception {
        try {
        	String ivParameter = sKey.substring(5, 5+16);
            byte[] raw = sKey.getBytes("US-ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(algorithmStr);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("US-ASCII"));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//����base64����
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original,"UTF-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }   
    }
    
    public  String SHA256(String str)
    {
    	MessageDigest messageDigest;
    	String encodeStr = "";
    	try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		 	messageDigest.update(str.getBytes("UTF-8"));
	    	encodeStr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
    	return encodeStr;
    }
    
    
    private  String byte2Hex(byte[] bytes){
    	StringBuffer stringBuffer = new StringBuffer();
    	String temp = null;
    	for (int i=0;i<bytes.length;i++){
    		temp = Integer.toHexString(bytes[i] & 0xFF);
    		if (temp.length()==1){  
    			stringBuffer.append("0"); 
    			}
    		stringBuffer.append(temp);
    		}
    	return stringBuffer.toString();
    }
    
    
    public  String SignRequest(Map<String, String> parameters, String signKey) throws NoSuchAlgorithmException
{
String result = "";
// ��һ�������ֵ䰴Key����ĸ˳������
Collection<String> keyset= parameters.keySet();   
List list=new ArrayList<String>(keyset);  
Collections.sort(list);  
// �ڶ����������в������Ͳ���ֵ����һ��
String query = "";


for(int i=0;i<list.size();i++){  
    if		(list.get(i) != null && list.get(i).toString().length() > 0 && parameters.get(list.get(i)) != null && parameters.get(list.get(i)).length() > 0) {
        query = query + list.get(i) + parameters.get(list.get(i));
    }
}  

query =query + signKey;
//��������ָ���㷨���Ƶ���ϢժҪ 
MessageDigest md5 = MessageDigest.getInstance("MD5"); 
//ʹ��ָ�����ֽ������ժҪ���������£�Ȼ�����ժҪ���� 
byte[] results;
try {
	results = md5.digest(query.getBytes("UTF-8"));
	result = byte2Hex(results); 
} catch (UnsupportedEncodingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} 
//���õ����ֽ��������ַ�������  
return result; 
}
    
    
    
}
