package co.yixiang.modules.manage.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.order.entity.UserAgreement;
import co.yixiang.modules.order.service.UserAgreementService;
import co.yixiang.modules.yiyaobao.demo.JsonUtils;
import co.yixiang.mp.service.impl.PdfServiceImpl;
import co.yixiang.tools.express.util.HttpUtils;
import co.yixiang.utils.ImageUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CASignServiceImpl {


    @Value("${beijingCA.clientId}")
    private String clientId;		//厂商标识

    @Value("${beijingCA.appSecret}")
    private String appSecret;		//应用secret

    @Value("${beijingCA.signPdfUrl}")
    private String signPdfUrl;		//pdf签名同步url

    @Value("${beijingCA.getTokenUrl}")
    private String getTokenUrl;		//获取token url

    @Value("${beijingCA.scopeUrl}")
    private String scopeUrl;  // url前缀

    @Value("${beijingCA.signVerifyUrl}")
    private String signVerifyUrl;	//pdf签名验证url

    @Value("${beijingCA.serviceId}")
    private String serviceId;		//serviceId

    @Value("${beijingCA.templateId}")
    private String templateId;		//templateId

    @Value("${beijingCA.signDataPageUrl}")
    private String signDataPageUrl; //公众签名获取待签数据对应的h5 页面url

    @Value("${beijingCA.signOrderStatusUrl}")
    private String signOrderStatusUrl; //获取签名结果

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserAgreementService userAgreementService;

    @Value("${file.localUrl}")
    private String localUrl;


    @Value("${file.path}")
    private String path;

    @Autowired
    private PdfServiceImpl pdfService;

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        String token = "";

        if(redisUtils.get("CA-TOKEN") != null) {
           return String.valueOf(redisUtils.get("CA-TOKEN"));
        }

        try {
            Map<String, String> map = new HashMap();
            map.put("clientId", clientId);
            map.put("appSecret", appSecret);


            /*HttpHeaders headers = new HttpHeaders();
            HttpEntity requestEntity = new HttpEntity(map, headers);
            String url = scopeUrl + getTokenUrl + "?clientId="+ clientId + "&appSecret=" + appSecret;

            log.info("请求参数getToken：" + JSONUtil.parseObj(map) + "，请求地址：" + url);
            ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class,map);
            String body = resultEntity.getBody();*/

            String url = scopeUrl + getTokenUrl;
            log.info("请求参数getToken：" + JsonUtils.object2Json(map) + "，请求地址：" + url);
            String body = HttpUtils.getHttp(url, map);

            log.info("获取token返回结果 {}",body);

            JSONObject jsonObject = JSONUtil.parseObj(body);
            if( "success".equals(jsonObject.getStr("message"))) {
                token = jsonObject.getJSONObject("data").getStr("accessToken");
                String expiresIn = jsonObject.getJSONObject("data").getStr("expiresIn");
               // redisUtils.set("CA-TOKEN",token,Long.valueOf(expiresIn).longValue(), TimeUnit.SECONDS);
                redisUtils.set("CA-TOKEN",token,10, TimeUnit.MINUTES);
            }else {
                throw new ErrorRequestException("获取token失败");
            }



        } catch (Exception e) {
            log.info("获取token失败，Exception=" + e.getMessage());
            throw new ErrorRequestException("获取token失败");
        }
        return token;
    }


    public String getSignAmgKnowHtml(String orderKey,Integer uid)  {

        // 1.获取token
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            throw new ErrorRequestException("获取token失败");
        }
//        if(StringUtils.isNotBlank(entity.getSignFlowId())){
//            return getSignhtml5url(urlUtil, entity.getSignFlowId(), token, entity.getPatientName());
//        }else {
        // 根据运单号判断是否在回单表中已经存在，如果不存在，则获得signFlowId，并保存至回单表
        String pdfBase64 = "";
       // String pdfpath = System.getProperty("user.dir") + File.separator + "userKnowTemplate.pdf";
      //  log.info("pdfPath={}" + pdfpath);
        /*if (StrUtil.isBlank(pdfpath)) {
            throw new ErrorRequestException("未找到模板文件！");
        }*/
       // pdfBase64 =  encodeBase64File(pdfpath);

        String newFileName = path + "rochesmapdf" + File.separator + "agreement-"+  DateUtil.today() + ".pdf";
       // String pdfPath = localUrl + "/file/static/textpdf/agreement.pdf";

        String pdfPath = localUrl + "/file/rochesmapdf/"+"agreement-"+  DateUtil.today() + ".pdf";
        if(!FileUtil.exist(newFileName)) {
            pdfService.generatePdf();
        }


        try {
            URL prfUrl =  new URL(pdfPath);
            pdfBase64 = ImageUtil.encodeImageToBase64( prfUrl);
        } catch (MalformedURLException e) {
            throw new ErrorRequestException("未找到签名模板文件！");

        }catch (Exception e) {
            throw new ErrorRequestException("签名模板文件转base64异常！");
        }

        String requestId =  IdUtil.simpleUUID();

       // String requestId = orderKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);


        JSONObject headJson = JSONUtil.createObj();
        headJson.put("clientId",clientId);
        headJson.put("accessToken",token);
        headJson.put("serviceId",serviceId);
        headJson.put("templateId",templateId);

        JSONObject bodyJson = JSONUtil.createObj();
        bodyJson.put("requestId",requestId);
        bodyJson.put("pdfBase64",pdfBase64);
        bodyJson.put("keyword","签名");
        bodyJson.put("moveType",1);
        bodyJson.put("searchOrder",2);

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("head",headJson);
        jsonObject.put("body",bodyJson);

        log.info(jsonObject.toString());
        HttpEntity requestEntity = new HttpEntity(jsonObject.toString(), headers);

        String url = scopeUrl + signPdfUrl;
        ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        String body = resultEntity.getBody();
        log.info("sendRequest 返回结果 {}",body);
        JSONObject resultJson = JSONUtil.parseObj(body);
        String signFlowId = "";
        if( "success".equals(resultJson.getStr("message"))) {
            signFlowId = resultJson.getJSONObject("data").getStr("signFlowId");
            String signhtml5url = getSignhtml5url( signFlowId, token, "上药云健康");

            // 插入用户同意书表中
            UserAgreement userAgreement = new UserAgreement();
            userAgreement.setOrderKey(orderKey);
            userAgreement.setOrderNo("");
            userAgreement.setRequestId(requestId);
            userAgreement.setSignFlowId(signFlowId);
            userAgreement.setStatus(0);
            userAgreement.setUid(uid);
            userAgreement.setCreateTime(new Date());
            userAgreementService.save(userAgreement);

            return signhtml5url;
        }else {
            throw new ErrorRequestException("获取signFlowId失败");
        }

    }


    public String getSignhtml5url( String signFlowId, String token, String subject) {
        log.info("-- 开始获取HTML签名页面");

        ArrayList signOrderIds = new ArrayList();
        signOrderIds.add(signFlowId);
        JSONObject map_t = JSONUtil.createObj();
        map_t.put("clientId", clientId);
        map_t.put("signOrderIds", signOrderIds);
        map_t.put("accessToken", token);
        map_t.put("teamName", "益药");
        map_t.put("subject", subject);
        map_t.put("publicName", subject);
        map_t.put("patientName", subject);
        log.info("请求HTML签名页面参数：" + JSONUtil.parseObj(map_t));

        // 3.获取HTML签名页面

        String url = scopeUrl + signDataPageUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity requestEntity = new HttpEntity(map_t.toString(), headers);

        ResponseEntity<String> resultEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        String body = resultEntity.getBody();
        log.info("sendRequest 返回结果 {}",body);
        JSONObject resultJson = JSONUtil.parseObj(body);

        if( "success".equals(resultJson.getStr("message"))) {
            String htmlUrl = resultJson.getJSONObject("data").getStr("url");

            return htmlUrl;
        }else {
            throw new ErrorRequestException("获取待签数据对应的h5页面url失败");
        }


    }



    /**
     * pdf文件转base64
     *
     * @param path
     * @return
     */
    public static String encodeBase64File(String path) {
        // logger.info("将pdf转换base64，文件路径=" + path);
        File file = new File(path);
        ;
        try (FileInputStream inputFile = new FileInputStream(file);) {
            byte[] buffer = new byte[(int) file.length()];
            int count = inputFile.read(buffer);
            inputFile.close();
            return new BASE64Encoder().encode(buffer);
        } catch (Exception e) {
            //  logger.info("encodeBase64File 出错，文件路径="+path);
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 将base64编码转换成PDF
     *
     * @param base64sString 1.使用BASE64Decoder对编码的字符串解码成字节数组
     *                      2.使用底层输入流ByteArrayInputStream对象从字节数组中获取数据；
     *                      3.建立从底层输入流中读取数据的BufferedInputStream缓冲输出流对象；
     *                      4.使用BufferedOutputStream和FileOutputSteam输出数据到指定的文件中
     */
    public static void base64StringToPDF(String base64sString, String fileName) {
        BufferedInputStream bin = null;
        File file = new File(fileName);
        BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        try (FileOutputStream fout = new FileOutputStream(file); BufferedOutputStream bout = new BufferedOutputStream(fout);) {
            //将base64编码的字符串解码成字节数组
            byte[] bytes = decoder.decodeBuffer(base64sString);
            //apache公司的API
            //byte[] bytes = Base64.decodeBase64(base64sString);
            //创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            //创建从底层输入流中读取数据的缓冲输入流对象
            bin = new BufferedInputStream(bais);
            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while (len != -1) {
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String querySignResult(String orderKey,String orderNo){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(orderKey)) {
            queryWrapper.eq("order_key",orderKey);
        }else{
            queryWrapper.eq("order_no",orderNo);
        }

        queryWrapper.eq("status",1);
        queryWrapper.last("limit 1");
        UserAgreement userAgreement = userAgreementService.getOne(queryWrapper,false);
        if(ObjectUtil.isNotEmpty(userAgreement)) {
            return userAgreement.getSignFilePath();
        }
        return "";

    }


}
