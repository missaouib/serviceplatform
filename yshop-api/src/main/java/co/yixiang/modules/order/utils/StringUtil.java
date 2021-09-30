package co.yixiang.modules.order.utils;

import cn.hutool.core.util.RandomUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuyun
 */
public class StringUtil {

    private static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final int count = 6;

    private final static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23)
                + uuid.substring(24);
    }

    public static String getUUID(Integer num) {
        String uuid = UUID.randomUUID().toString();
        uuid =  uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23)
                + uuid.substring(24);
        return uuid.substring(0,num);
    }

    public static String getVerifyCode() {
        return RandomStringUtils.random(count, chars);
    }

    public static String getVerifyCode(int count) {
        return RandomStringUtils.random(count, chars);
    }

    public static String generateShortUrl(String url) {
        HttpClient httpClient = null;
        try {
            httpClient = new DefaultHttpClient();
            String encodeUrl = URLEncoder.encode(url, "UTF-8");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpGet getMethod = new HttpGet(
                    "http://api.t.sina.com.cn/short_url/shorten.json?source=1681459862&url_long=" + encodeUrl);
            String responseStr = httpClient.execute(getMethod, responseHandler);
            if (!StringUtils.isEmpty(responseStr) && responseStr.startsWith("[")) {
                responseStr = responseStr.replace("[", "").replace("]", "");
            }
            JSONObject jsonObj = JSONObject.fromObject(responseStr);
            return jsonObj.getString("url_short");
        } catch (IOException e) {
            logger.info("**********得到短链接错误********url：" + url, e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }

    public static String getValueFromLabel(String content, String startLabel, String endLabel) {
        if (!StringUtils.isEmpty(content)) {
            int startIndex = content.indexOf(startLabel);
            int endIndex = content.indexOf(endLabel);
            if (startIndex != -1 && endIndex != -1) {
                return content.substring(startIndex + startLabel.length(), endIndex);
            }
        }
        return content;
    }

    public static String removeLabel(String content, String startLabel, String endLabel) {
        int startIndex = content.indexOf(startLabel);
        int endIndex = content.indexOf(endLabel);
        if (startIndex != -1 && endIndex != -1) {
            return content.substring(0, startIndex) + content.substring(endIndex + endLabel.length(), content.length());
        }
        return content;
    }

    public static String inputStreamToString(InputStream is) {
        if (is == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        String str = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        return stringBuffer.toString();
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 替换换行符，字符里的换行符替换为html识别的换行符
     *
     * @param newLine
     */
    public static String replaceNewLine(final String newLine) {
        return org.apache.commons.lang.StringUtils.replace(newLine, "\n", "<br>");
    }

    public static List<String> match(String text, String patternStr) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        List<String> matchList = new ArrayList<>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matchList.add(matcher.group());
        }
        return matchList;
    }

    public static String toString(Object o) {
        if (o == null) {
            return "";
        }
        return String.valueOf(o);
    }

    public static void main(String[] args) {

        System.out.println(stripXSS("12321%"));
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static String toHexFormat(String s) {
        int len = s.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i = i + 2) {
            sb.append(" ").append(s.substring(i, i + 2));
        }
        return sb.toString();
    }

    public static String convertDecimalToHex(String vinPart, int bytes) {
        return String.format("%" + bytes * 2 + "s", Integer.toHexString(Integer.valueOf(vinPart))).replace(" ", "0");
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public static boolean startWithIgnoreCase(String src, String obj) {
        if (obj.length() > src.length()) {
            return false;
        }
        return src.substring(0, obj.length()).equalsIgnoreCase(obj);
    }

    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    public static final String EMPTY = "";

    public static List<String> match(String source, String element, String attr) {
        List<String> result = new ArrayList<String>();
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?\\s.*?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        return result;
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String joinRegex(Object[] array, String regex) {
        StringBuffer sb=new StringBuffer();
        for(int i=0,len = array.length;i<len;i++){
            if(i==(len-1)){
                sb.append(array[i]);
            }else{
                sb.append(array[i]).append(regex);
            }
        }
        return sb.toString();
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * 过滤非汉字的utf8的字符
     *
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            } else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            } else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    }

    public static int getNumber(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return Integer.parseInt(sb.toString());
    }

    public static int getContainCount(String[] vals,String[] contailVals){
        int count=0;
        for(String strX : contailVals){
            for(String strY : vals){
                if(strX.equals(strY)){
                    count ++;
                }
            }
        }
        return count;
    }

    public static String sortStringSplit(String sort, String regex) {
        if (StringUtils.isEmpty(sort)) {
            return null;
        } else {
            String[] strings = sort.split(regex);
            Arrays.sort(strings);
            return StringUtil.joinRegex(strings,regex);
        }
    }

    public static String cheanHtml(String html) {
        String reg = "<[^>]+>";
        Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        return m.replaceAll("");
    }

    /**
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     * @param str
     * 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiConvert(String str)
            throws UnsupportedEncodingException {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch(UnsupportedEncodingException e) {
                logger.error("emojiConvert error", e);
                throw e;
            }
        }
        matcher.appendTail(sb);
        logger.info("emojiConvert " + str + " to " + sb.toString()
                + ", len：" + sb.length());
        return sb.toString();
    }

    /**
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     * @param str
     * 转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiRecovery(String str)
            throws UnsupportedEncodingException {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch(UnsupportedEncodingException e) {
                logger.error("emojiRecovery error", e);
                throw e;
            }
        }
        matcher.appendTail(sb);
        logger.info("emojiRecovery " + str + " to " + sb.toString());
        return sb.toString();
    }

    /**
     * 把用户输入的字符串转为可以根据用户地址，姓名，电话 执行模糊查询的 sql 参数
     * @param as 用户输入的字符串
     * @return
     */
    public static String getString(String as){
        String s1 = "[\u4e00-\u9fa5]";
        String st = "%";
        for (int i= 0 ; i<as.length(); i++) {
            if (as.substring(i, i+1).matches(s1)) {
                st = st + as.substring(i , i+1) + "%";
            } else {
                st = st + as.substring(i , i+1);
            }
        }
        st = st + "%";
        System.out.println(st);
        return st;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     * @param inputStr
     * @param length
     * @return
     */
    public static List<String> getStrList(String inputStr,int length){
        int size = inputStr.length() / length;
        if (inputStr.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputStr, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @param size
     *            指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str
     *            原始字符串
     * @param f
     *            开始位置
     * @param t
     *            结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length()) {
            return null;
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    /**
     * 正数、负数、和小数
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
        return pattern.matcher(str).matches();
    }



    public static String getRundom(){
//         48-57 65-90 97-122
        StringBuffer id=new StringBuffer();
      //  Random random = new Random();
        try {
            Random rand = SecureRandom.getInstanceStrong();
            for (int i = 0; i < 8; i++) {
                char s = 0;
                int j= rand.nextInt(3) % 4;
                switch (j) {
                    case 0:
                        //随机生成数字
                        s = (char) (rand.nextInt(57) % (57 - 48 + 1) + 48);
                        break;
                    case 1:
                        //随机生成大写字母
                        s = (char) (rand.nextInt(90) % (90 - 65 + 1) + 65);
                        break;
                    case 2:
                        //随机生成小写字母
                        s = (char) (rand.nextInt(122) % (122 - 97 + 1) + 97);
                        break;
                }
                id.append(s);
            }
            return id.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
       return "";
    }

    public static String deleteAllHTMLTag(String source) {
        if (source == null) {
            return "";
        }

        String s = source;
        /** 删除普通标签  */
        s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
        s = s.replaceAll("<S*?", "");
        s = s.replaceAll("xss","");
        /** 删除转义字符 */
        s = s.replaceAll("&.{2,6}?;", "");
        return s;
    }

    public static String stripXSS(String value) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(value)) {
            value = value.replaceAll("", "");
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            value = value.replaceAll("XSS","");
            value = value.replaceAll("xss", "");
            value = value.replaceAll("alert","");

//            String regEx="[`~$^*()+=|{}''\\[\\]<>/~￥……*——+|{}【】‘”“’]";
//            Pattern p = Pattern.compile(regEx);
//            Matcher m = p.matcher(value);
            return value.trim();
        }
        return value;
    }
}
