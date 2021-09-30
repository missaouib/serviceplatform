package co.yixiang.tools.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppSiganatureUtils {
	private static String SAPARATOR="@$@";
	public static String createSiganature(String data, String appid,String appSecret,long timestamp) {
        String plain=appid+SAPARATOR+appSecret+SAPARATOR+data+SAPARATOR+timestamp+SAPARATOR+appSecret+SAPARATOR+appid;
        String siganature=encrypt(plain,"SHA-512");
        return siganature;
	}


	private static String encrypt(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt=null;
		try {
			bt = strSrc.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        try {
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static void main(String args[]){
    	String data="test";
        System.out.println(createSiganature(data,"test001","2770bcb4-7e35-498d-b83e-fe2d5377cf74",1446738417  ));
    }

}
