package co.yixiang.modules.msh.util;

import co.yixiang.modules.msh.service.dto.MshDemandListDto;
import co.yixiang.modules.msh.service.dto.MshOrderDto;
import co.yixiang.modules.msh.service.enume.MshStatusEnum;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MshRequestUtil {
    private final static Logger LOG = LoggerFactory.getLogger(MshRequestUtil.class);
    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String domainName = "https://apitest.mshchina.com/appwechat";

    //3.1.1	需求单明细回传接口
    public static String syncOrderDetail(String domainName, String secureKey, MshDemandListDto demandListDto) throws Exception {
        long timestamp = System.currentTimeMillis();
        String nonce = getUUID32();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", timestamp);
        map.put("nonce", nonce);
        map.put("signature", getSignature(timestamp, nonce, secureKey));

        JSONObject template = new JSONObject();
        template.put("demandNo", demandListDto.getDemandNo());
        template.put("mermberId", demandListDto.getMemberId());
        template.put("demandOrderStatus", demandListDto.getAuditStatus());
        template.put("demandOrderStatusName", MshStatusEnum.AuditStatus.getValueByCode(demandListDto.getAuditStatus().toString()));
        if(MshStatusEnum.AuditStatus.KFSHBTG.getCode() == demandListDto.getAuditStatus()
            ||  MshStatusEnum.AuditStatus.YJSSHBTG.getCode() ==  demandListDto.getAuditStatus()
                ){
            String reason="";
            if(StringUtils.isNotEmpty(demandListDto.getCancelReason())){
                if(MshStatusEnum.CancelReason.getCancelReasonValue(demandListDto.getCancelReason())==null){
                    if(MshStatusEnum.CancelReason.getCancelReason(demandListDto.getCancelReason())==null){
                        reason=MshStatusEnum.CancelReason.reason_19.getCode();
                    }
                }else{
                    reason=MshStatusEnum.CancelReason.getCancelReasonValue(demandListDto.getCancelReason()).getCode();
                }
            }
            template.put("rejectReason", reason);
        }else{
            template.put("rejectReason", "");
        }
        template.put("consigneeName", demandListDto.getReceivingName());
        template.put("relation", demandListDto.getRelationship());
        template.put("consigneePhone", demandListDto.getReceivingPhone());
        template.put("provinceCode", demandListDto.getProvinceCode());
        template.put("cityCode", demandListDto.getCityCode());
        template.put("districtCode", demandListDto.getDistrictCode());
        template.put("provinceName", demandListDto.getProvince());
        template.put("cityName", demandListDto.getCity());
        template.put("districtName", demandListDto.getDistrict());
        template.put("address", demandListDto.getDetail());
        template.put("idCardImages", demandListDto.getIdCardImages());
        template.put("medicalDocumentsImages", demandListDto.getMedicalDocumentsImages());
        template.put("otherImages", demandListDto.getOtherImages());

        JSONArray jsonArrayEntry = new JSONArray();
        for (MshOrderDto mshOrderDto : demandListDto.getOrderList()) {
            if(StringUtils.isNotEmpty(mshOrderDto.getExternalOrderId())){
                JSONObject accept = new JSONObject();
                accept.put("orderId", mshOrderDto.getExternalOrderId());
                accept.put("orderStatus", mshOrderDto.getOrderStatus());
                accept.put("orderStatusName", MshStatusEnum.OrderStatus.getValueByCode(mshOrderDto.getOrderStatus().toString()));
                if(mshOrderDto.getOrderStatus().equals(MshStatusEnum.OrderStatus.SHBTG.getCode().toString())
                        || mshOrderDto.getOrderStatus().equals(MshStatusEnum.OrderStatus.BH.getCode().toString())){
                  String orderRejectReason=  MshStatusEnum.CancelReason.getCancelReason(mshOrderDto.getAuditReasons())==null ? MshStatusEnum.CancelReason.reason_19.getCode():MshStatusEnum.CancelReason.getCancelReason(mshOrderDto.getAuditReasons().toString()).getCode();
                    accept.put("orderRejectReason",orderRejectReason );
                }else{
                    accept.put("orderRejectReason","" );
                }
                accept.put("create_time", sdf.format(mshOrderDto.getCreateTime()));
                jsonArrayEntry.add(accept);
            }
        }
        template.put("orders", jsonArrayEntry);
        LOG.info("需求单明细回传接口url:" + domainName + "/sy/syncOrderDetail?" + mapToString(map) + "," + template.toString());
        String jsonParam = HttpUtil.post(domainName + "/sy/syncOrderDetail?" + mapToString(map), template.toString());
        LOG.info("需求单明细回传接口 jsonParam:{}", jsonParam);
        return jsonParam;
    }


    //上传
    public static String upload(String domainName, String secureKey, MultipartFile[] multipartFiles) throws Exception {
        long timestamp = System.currentTimeMillis();
        String nonce = getUUID32();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", timestamp);
        map.put("nonce", nonce);
        map.put("signature", getSignature(timestamp, nonce, secureKey));

//        Map<String, MultipartFile> requestFile =new HashMap<>();
//        requestFile.put("files",multipartFiles[0]);
//        System.out.println("requestFile getOriginalFilename:" +multipartFiles[0].getOriginalFilename());
//        System.out.println("文件上传接口url:" + domainName + " /upload?" + mapToString(map));
//        String jsonParam = HttpIOUtil.sendRequest(domainName + "/upload?" + mapToString(map),null, requestFile);
//        LOG.info("文件上传接口jsonParam:{}", jsonParam);
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<String, Object>();
        List<File> files=new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File file=multipartFileToFile(multipartFile);
            files.add(file);
            multiValueMap.add("files", new FileSystemResource(file.getPath()));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<String> response = template.postForEntity(domainName + "/upload?" + mapToString(map), requestEntity, String.class);
        LOG.info("mulit返回状态：" + response.getStatusCode());
        LOG.info("mulit返回结果：" + response.getBody());
        for (File file : files) {
            file.delete();
        }
        return response.getBody();
    }

    /**
     * MultipartFile 转 File
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        LOG.info("file.getPath:"+toFile.getPath());
        return toFile;
    }

    /**
     * 获取流文件
     * @param ins
     * @param file
     */
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String transferToFile(MultipartFile multipartFile) {
//        选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file=File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("file.getPath:"+file.getPath());
        try {
            LOG.info("file.getCanonicalPath:"+file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getPath();
    }



    /**
     * 生成uuid32位
     *
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String getSignature(Long timestamp, String nonce, String secureKey) {
        String str = timestamp.toString() + nonce + secureKey;
        String signature = DigestUtils.sha1Hex(str);
        return signature;
    }

    public static String mapToString(Map<String, Object> map) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        String nonce = getUUID32();
        String signature= getSignature(timestamp, nonce, "123456");
        String companyId="1412702334751866880";

//unhvrgou8bjawl6pd0aw23hmuit35mer  123456
//41vkeefzq61eah1i9dyuxwwgqxowfsfr   1383985416380837112
        try {
//            queryOrderDetail();
           String url="https://api-oms-test.yiyaogo.com/gateway?timestamp="+timestamp+"&nonce="+nonce+"&signature="+signature+"&companyId="+companyId+"";
           String body="{\"companyId\":\""+companyId+"\",\"requestData\":{\"buyerName\":\"杨清\",\"buyerPhone\":\"15812345678\",\"deliverType\":\"00\",\"drugList\":[{\"drugCode\":\"010101189\",\"qty\":5,\"unitPrice\":1808800}],\"patientName\":\"杨清\",\"patientPhone\":\"15812345678\",\"payMethod\":\"99\",\"pharmacyCode\":\"85\",\"presciptionUrlList\":[\"http://114.67.81.133/group1/M01/0E/17/wKgADGE5wBaARayVAABzjHEcGKw575.jpg?token=3f1a5e4f1d407082205272ffe965bbdd&ts=1631256395\"],\"selfpayAmount\":1111100,\"totalAmount\":100000},\"requestId\":\"5508957376348161\",\"requestTime\":\"20210922144635\",\"requestType\":\"yiyao.oms.addOrder\"}";
            String jsonParam = HttpUtil.post(url,body);
            System.out.println(jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String queryOrderDetail() throws Exception {
        long timestamp = System.currentTimeMillis();
        String nonce = getUUID32();
        String signature= getSignature(timestamp, nonce, "123456");
        System.out.println("签名："+signature);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", timestamp);
        map.put("nonce", nonce);
        map.put("signature", signature);
        map.put("companyId", "1383985416380837112");


       JSONObject mapBody = new JSONObject();
        mapBody.put("requestType", "yiyao.yiyaomall.queryMdCountry");
        mapBody.put("requestId", nonce);
        mapBody.put("requestTime", timestamp);
        mapBody.put("companyId", "1383985416380837112");

        JSONObject requestData = new JSONObject();
        requestData.put("parentCode", "1930");
        mapBody.put("requestData", requestData);

        String jsonParam = HttpUtil.post("https://wechat-admin-api-test.yiyaogo.com/gateway?" + mapToString(map),mapBody.toString());
        LOG.info("文件上传接口jsonParam:{}", jsonParam);
        return jsonParam;
    }


}
