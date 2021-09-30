package co.yixiang.modules.taibao.util;

import co.yixiang.modules.taibao.service.vo.ClaimAccInfoVo;
import co.yixiang.modules.taibao.service.vo.ClaimInfoVo;
import co.yixiang.modules.taibao.service.vo.InsurancePersonVo;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*******************************************************************************
 * xml通用工具类
 *
 */
@SuppressWarnings("unchecked")
public class FileUtils {
    /**
     * 日志
     */
    private final static Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static String xml = "";

    public static void init() {
        xml = "";
    }

    /***************************************************************************
     * 得到指定名称节点下的所有文本内容,包括节点(逆归) <暂不考虑节点属性情况>
     *
     * @param doc
     *            xml文档对象
     * @param e
     *            要获取的节点对象
     *            要排除的节点名称
     * @return
     */

    public static String getChildAllText(Document doc, Element e) {
        if (e != null) {
            if (e.getChildren() != null) {
                List<Element> list = e.getChildren();
                xml += "<" + e.getName() + ">";
                for (Element el : list) {
                    if (el.getChildren().size() > 0) {
                        getChildAllText(doc, el);
                    } else {
                        xml += "<" + el.getName() + ">" + el.getTextTrim() + "</"
                                + el.getName() + ">";
                    }
                }
                xml += "</" + e.getName() + ">";
            } else {
                xml += "<" + e.getName() + ">" + e.getTextTrim() + "</"
                        + e.getName() + ">";
            }
        }
        return xml;
    }


    /**
     * Map key 排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> order(Map<String, String> map) {
        HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> item = infoIds.get(i);
            tempMap.put(item.getKey(), item.getValue());
        }
        return tempMap;
    }

    /**
     * 转换对象为map
     *
     * @param object
     * @param ignore
     * @return
     */
    public static Map<String, String> objectToMap(Object object, String... ignore) {
        Map<String, String> tempMap = new LinkedHashMap<String, String>();
        for (Field f : object.getClass().getDeclaredFields()) {
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            boolean ig = false;
            if (ignore != null && ignore.length > 0) {
                for (String i : ignore) {
                    if (i.equals(f.getName())) {
                        ig = true;
                        break;
                    }
                }
            }
            if (ig) {
                continue;
            } else {
                Object o = null;
                try {
                    o = f.get(object);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LOG.error("objectToMap error!", e);
                }
                String key = null;
                // xml注解key取xml属性名
                XmlElement xmlElement = f.getAnnotation(XmlElement.class);
                if (xmlElement != null) {
                    key = xmlElement.name();
                } else {
                    key = f.getName();
                }

                if ("package_".equals(key)) {
                    key = "package";
                }
                tempMap.put(key, o == null ? "" : o.toString());
            }
        }
        return tempMap;
    }

    /**
     * url 参数串连
     *
     * @param map
     * @param keyLower
     * @param valueUrlencode
     * @return
     */
    public static String mapJoin(Map<String, String> map, boolean keyLower, boolean valueUrlencode) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            if (map.get(key) != null && !"".equals(map.get(key))) {
                try {
                    stringBuilder.append(keyLower ? key.toLowerCase() : key).append("=")
                            .append(valueUrlencode ? URLEncoder.encode(map.get(key), "utf-8") : map.get(key))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    LOG.error("mapJoin error!", e);
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    /**
     * 简单 xml 转换为 Map
     *
     * @param xml
     * @return
     */
    public static Map<String, String> xmlToMap(String xml) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        try {
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            org.w3c.dom.Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            org.w3c.dom.Element element = document.getDocumentElement();
            NodeList nodeList = element.getChildNodes();
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Object nodeItem = nodeList.item(i);
                if (nodeItem instanceof org.w3c.dom.Element) {
                    org.w3c.dom.Element e = (org.w3c.dom.Element) nodeItem;
                    map.put(e.getNodeName(), e.getTextContent());
                }
            }
            return map;
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            LOG.error("xmlToMap error", e);
        }
        return null;
    }

    /**
     * 功能描述: <br>
     * map
     *
     * @param map
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String mapToXml(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                if (NumberUtils.isNumber(value)) {
                    sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">");
                } else {
                    sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key)
                            .append(">");
                }
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 获取xml格式字符串
     *
     * @param xmlfileUrl xml文件路径
     * @return
     */
    public static String getXmlString(String xmlfileUrl) {
        SAXBuilder sb = new SAXBuilder(); // 新建立构造器
        Document doc = null;
        try {
            doc = sb.build(new FileInputStream(
                    "C:\\Users\\EDZ\\Desktop\\上药对接文件\\D20042103500100.xml")); // 读入6.xml
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement(); // 取得根节点
        String xmlString = getChildAllText(doc, root);
        return xmlString;
    }


    /**
     * xml格式字符串 转json格式数据
     *
     * @param xmlString xml格式字符串
     * @return
     */
    public static JSON xmlStringToJson(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(xmlString);
        return json;
    }

    /**
     * 如果对象中的String类型字段值为字符串"" ,[]，则转为null
     *
     * @param obj
     */
    public static <T> T blankSpaceToNull(T obj) {
        Class cls = obj.getClass();

        Field[] fields = cls.getDeclaredFields();  //得到所有属性
        for (int i = 0; i < fields.length; i++) {//遍历
            try {

                Field field = fields[i];//得到属性

                field.setAccessible(true);  //打开私有访问

                Object value = field.get(obj);  //获取属性值
                if ("class java.lang.String".equals(field.getGenericType().toString())) {
                    if (value == null || "".equals(value.toString().trim()) || "[]".equals(value.toString().trim())) {// 如果为"" 直接设为null
                        field.set(obj, null);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static <T> T jsonStringToObject(T obj, String jsonString) {
        obj = JsonUtil.getJsonToBean(jsonString, (Class<T>) obj.getClass());
        obj = blankSpaceToNull(obj);
        return obj;
    }

    /**
     * 获取txt文件内容并按行放入list中
     */
    public static List<String> getFileContext(String path) throws Exception {
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> list = new ArrayList<String>();
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            if (str.trim().length() > 2) {
                list.add(str);
            }
        }
        fileReader.close();
        return list;
    }

    /**
     * 创建文件
     * @param data
     * @param path
     */
    public static boolean createFile(String data, String path) {
        try {
            File file = new File(path);
            if(file.exists()){
                file.delete();
            }
            boolean b = file.createNewFile();
            if(b) {
                Writer out = new FileWriter(file);
                out.write(data);
                out.close();
                return true;
            }
            return  false;
        }catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

    }

    public static void zipFiles(File[] srcFiles, File zipFile) {
        // 判断压缩后的文件存在不，不存在则创建
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 创建 FileOutputStream 对象
        FileOutputStream fileOutputStream = null;
        // 创建 ZipOutputStream
        ZipOutputStream zipOutputStream = null;
        // 创建 FileInputStream 对象
        FileInputStream fileInputStream = null;

        try {
            // 实例化 FileOutputStream 对象
            fileOutputStream = new FileOutputStream(zipFile);
            // 实例化 ZipOutputStream 对象
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            // 创建 ZipEntry 对象
            ZipEntry zipEntry = null;
            // 遍历源文件数组
            for (int i = 0; i < srcFiles.length; i++) {
                // 将源文件数组中的当前文件读入 FileInputStream 流中
                fileInputStream = new FileInputStream(srcFiles[i]);
                // 实例化 ZipEntry 对象，源文件数组中的当前文件
                zipEntry = new ZipEntry(srcFiles[i].getName());
                zipOutputStream.putNextEntry(zipEntry);
                // 该变量记录每次真正读的字节个数
                int len;
                // 定义每次读取的字节数组
                byte[] buffer = new byte[1024];
                while ((len = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     　　* word转pdf
     * @param wordFilePath word文件路径
     * @param pdfFilePath  pdf文件路径
     * @return 成功或失败
     */
    public static boolean docxToPdf(String wordFilePath, String pdfFilePath) {
        boolean result = false;

        File inputFile = new File(wordFilePath);
        File outputFile = new File(pdfFilePath);
        try {
            InputStream inputStream = new FileInputStream(inputFile);
            OutputStream outputStream = new FileOutputStream(outputFile);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(inputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();

            outputStream.close();
            result = true;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param obj 传入的对象
     * @return 返回对象转换xml字符串
     */
    public static String ObjToXmlString(Object obj){
        System.out.println("开始将对象转换为XML格式字符串");
        String XmlString = "";
        //创建输出 流
        StringWriter writer = new StringWriter();
        try {
            //jdk自带的转换类
            JAXBContext context = JAXBContext.newInstance(obj.getClass());//传入对象的class

            //创建marshaller（指挥）通过它可以将xml与对象互相转换 。对于一些不规范的xml格式它也可以进行规范调试
            Marshaller marshaller = context.createMarshaller();

            //格式化xml输出格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //将对象输出成XML形式
            marshaller.marshal(obj, writer);

            XmlString = writer.toString();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            System.out.println("转换出错!!!");
            e.printStackTrace();
        }
        return XmlString;
    }

    public static boolean fileUrlDown(String urlPath, String filePath){
        URL url = null;
        try {
            url = new URL(urlPath);
            URLConnection con = url.openConnection();
            FileOutputStream out = new FileOutputStream(filePath);
            InputStream ins = con.getInputStream();
            byte[] b = new byte[1024];
            int i=0;
            while((i=ins.read(b))!=-1){
                out.write(b, 0, i);
            }
            ins.close();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws FileNotFoundException,
            JDOMException, IOException {
//        String xmlString = getXmlString("C:\\Users\\EDZ\\Desktop\\上药对接文件\\D20042103500100.xml");
//        System.out.println(xmlString);
//        JSON json = xmlStringToJson(xmlString);
//        System.out.println(json);
//        ClaimInfo claimInfo = jsonStringToObject(new ClaimInfo(), json.toString());
//        InsurancePerson insurancePerson = jsonStringToObject(new InsurancePerson(), JSONObject.fromObject((json.toString())).get("insurance_person").toString());
//        System.out.println(claimInfo);
//        System.out.println(insurancePerson);


//       boolean b=  createFile("10001|PA10001|02|我不想赔","D:\\","result.txt");
//        System.out.println(b);

//        boolean b= docxToPdf("D:\\附件23 理赔结案通知书.docx","D:\\理赔结案通知书.pdf");
//        System.out.println(b);


        ClaimInfoVo claimInfoVo = new ClaimInfoVo();
        claimInfoVo.setBatchno("D200421035");
        claimInfoVo.setClaimno("D20042103500100");
        claimInfoVo.setCustmco("201801034891");
        claimInfoVo.setExptime("2020-04-21");
        claimInfoVo.setEmailAccept("Y");
        claimInfoVo.setVisitDate("2020-04-21 18:39:12");
        claimInfoVo.setReauditdate("2020-04-21 18:07:44");
        claimInfoVo.setClaimrescode("08");
        claimInfoVo.setAuditoption("本次申请在合同保障范围内，有住院事实，诊治合理");
//        claimInfoVo.setHangupsign(Arrays.asList("12312,123".split(",")));

        InsurancePersonVo insurancePersonVo=new InsurancePersonVo();
        insurancePersonVo.setName("许灏辰");
        insurancePersonVo.setSex("1");
        insurancePersonVo.setIdtype("a");
        insurancePersonVo.setIdno("320102200101150817");
        insurancePersonVo.setIdBegdate("9999-12-31");
        insurancePersonVo.setIdEnddate("9999-12-31");
        insurancePersonVo.setOrganization("豪洛捷医疗科技(北京)有限公司");
        insurancePersonVo.setBirthdate("2001-01-15");

//        claimInfoVo.setInsurancePersonVo(insurancePersonVo);
        List<String> s=new ArrayList<>();
        s.add("s");
        s.add("01");
        ClaimAccInfoVo claimAccInfoVo=new ClaimAccInfoVo();
        claimAccInfoVo.setClaimacc(s);
        claimInfoVo.setClaimAccInfoVo(claimAccInfoVo);



        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(ClaimInfoVo.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(claimInfoVo, baos);
            String xmlObj = new String(baos.toByteArray());

            // 生成XML字符串
            System.out.println(xmlObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
