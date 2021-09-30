/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.tools.express;

import cn.hutool.http.HttpUtil;
import co.yixiang.tools.express.config.ExpressProperties;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.tools.express.dao.Traces;
import co.yixiang.tools.express.domain.RouteResponseInfo;
import co.yixiang.tools.express.route.Body;
import co.yixiang.tools.express.route.Request;
import co.yixiang.tools.express.route.RouteRequest;
import co.yixiang.tools.express.util.HttpUtils;
import co.yixiang.tools.express.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Base64Utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流查询服务
 * <p>
 * 快递鸟即时查询API http://www.kdniao.com/api-track
 */
public class ExpressService {

    private final Log logger = LogFactory.getLog(ExpressService.class);
    //请求url
    private String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    private ExpressProperties properties;

    public ExpressProperties getProperties() {
        return properties;
    }

    public void setProperties(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取物流供应商名
     *
     * @param vendorCode
     * @return
     */
    public String getVendorName(String vendorCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendorCode))
                return item.get("name");
        }
        return null;
    }

    /**
     * 获取物流信息
     *
     * @param OrderCode
     * @param ShipperCode
     * @return
     */
    public ExpressInfo getExpressInfo(String OrderCode,String ShipperCode, String LogisticCode) {
        try {
            /*String result = getOrderTracesByJson(OrderCode,ShipperCode, LogisticCode);
            ObjectMapper objMap = new ObjectMapper();
            ExpressInfo ei = objMap.readValue(result, ExpressInfo.class);
            ei.setShipperName(getVendorName(ShipperCode));
            return ei;*/

            String shipperName = "";
            List<RouteResponseInfo.Body.RouteResponse.Route> list = queryWaybillTrace(LogisticCode);
            ExpressInfo ei = new ExpressInfo();
            ei.setLogisticCode(LogisticCode);
            ei.setOrderCode(OrderCode);
            ei.setShipperCode(ShipperCode);
            ei.setShipperName(shipperName);
            ei.setSuccess(true);
            List<Traces> tracesList= new ArrayList<>();
            for(RouteResponseInfo.Body.RouteResponse.Route route: list ){
                Traces trace = new Traces();
                trace.setAcceptStation(route.getRemark());
                trace.setAcceptTime(route.getAcceptTime());
                tracesList.add(trace);
            }
            ei.setTraces(tracesList);

            return  ei;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /****
     *  查询顺丰单的物流信息
     *
     * */
    public List<RouteResponseInfo.Body.RouteResponse.Route> queryWaybillTrace(String waybill) throws Exception {
        logger.info("==========================物流结果查询==========================");
        Request req = new Request();
        req.setService("RouteService");
        req.setLang("zh-CN");
        req.setHead("0200018754");
        RouteRequest routeRequest = new RouteRequest();
        routeRequest.setMethod_type("1");
        routeRequest.setTracking_type("1");
        routeRequest.setTracking_number(waybill);
        Body body = new Body();
        body.setRouteRequest(routeRequest);
        req.setBody(body);
        JAXBContext context = null;    // 获取上下文对象

        try{
            context = JAXBContext.newInstance(Request.class);
            Marshaller marshaller = context.createMarshaller();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(req, baos);
            String xml = new String(baos.toByteArray());
            xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
            String verifyCode = Util.md5EncryptAndBase64(xml + "ZUZ2TrHHPEgPpuNYF4TUgCtrleAK8TDL");
            Map<String, String> paramPairs = new HashMap<String, String>();
            paramPairs.put("xml", xml);
            paramPairs.put("verifyCode", verifyCode);
            String result = HttpUtils.postHttp("http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService", paramPairs);
            logger.info("========================路由报文信息:"+result);
            JAXBContext ctx = JAXBContext.newInstance(RouteResponseInfo.class);
            Unmarshaller marchaller = ctx.createUnmarshaller();
            RouteResponseInfo route = (RouteResponseInfo) marchaller.unmarshal(new StringReader(result));
            if(route!=null&&"OK".equals(route.getHead())&&route.getBody()!=null&&route.getBody().getRouteResponse()!=null) {
                return route.getBody().getRouteResponse().getRoute();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return null;
    }


    /**
     * Json方式 查询订单物流轨迹
     *
     * @throws Exception
     */
    private String getOrderTracesByJson(String OrderCode,String ShipperCode, String LogisticCode) throws Exception {
        if (!properties.isEnable()) {
            return null;
        }

        String requestData = "{'OrderCode':'"+OrderCode+"','ShipperCode':'" + ShipperCode + "','LogisticCode':'" + LogisticCode + "'}";

        Map<String, Object> params = new HashMap<>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey(), "UTF-8");
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result = HttpUtil.post(ReqURL, params);

        //根据公司业务处理返回的信息......

        return result;
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws Exception
     */
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Sign签名生成
     *
     * @param content  内容
     * @param keyValue Appkey
     * @param charset  编码方式
     * @return DataSign签名
     */
    private String encrypt(String content, String keyValue, String charset) {
        if (keyValue != null) {
            content = content + keyValue;
        }
        byte[] src;
        try {
            src = MD5(content, charset).getBytes(charset);
            return Base64Utils.encodeToString(src);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }



}
