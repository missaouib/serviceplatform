/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.tools.rest;

import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.tools.domain.AlipayConfig;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.domain.vo.TradeVo;
import co.yixiang.tools.service.AlipayConfigService;
import co.yixiang.tools.service.dto.AuthCodeDto;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.tools.utils.AliPayStatusEnum;
import co.yixiang.tools.utils.AlipayProperties;
import co.yixiang.tools.utils.AlipayUtils;
import co.yixiang.utils.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hupeng
 * @date 2018-12-31
 */
@Slf4j
@RestController
@RequestMapping("/api/aliPay")
@Api(tags = "工具：支付宝管理")
public class AliPayController {

    private final AlipayUtils alipayUtils;

    private final AlipayConfigService alipayService;

    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${file.path}")
    private String filePath;


    public AliPayController(AlipayUtils alipayUtils, AlipayConfigService alipayService) {
        this.alipayUtils = alipayUtils;
        this.alipayService = alipayService;
    }

    @GetMapping
    public ResponseEntity<AlipayConfig> get() {
        return new ResponseEntity<>(alipayService.find(), HttpStatus.OK);
    }

    @Log("配置支付宝")
    @ApiOperation("配置支付宝")
    @PutMapping
    public ResponseEntity<Object> payConfig(@Validated @RequestBody AlipayConfig alipayConfig) {
        alipayConfig.setId(1L);
        alipayService.update(alipayConfig);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("支付宝PC网页支付")
    @ApiOperation("PC网页支付")
    @PostMapping(value = "/toPayAsPC")
    public ResponseEntity<String> toPayAsPc(@Validated @RequestBody TradeVo trade) throws Exception {
        AlipayConfig aliPay = alipayService.find();
        trade.setOutTradeNo(alipayUtils.getOrderCode());
        String payUrl = alipayService.toPayAsPc(aliPay, trade);
        return ResponseEntity.ok(payUrl);
    }

    @Log("支付宝手机网页支付")
    @ApiOperation("手机网页支付")
    @PostMapping(value = "/toPayAsWeb")
    public ResponseEntity<String> toPayAsWeb(@Validated @RequestBody TradeVo trade) throws Exception {
        AlipayConfig alipay = alipayService.find();
        trade.setOutTradeNo(alipayUtils.getOrderCode());
        String payUrl = alipayService.toPayAsWeb(alipay, trade);
        return ResponseEntity.ok(payUrl);
    }

    @ApiIgnore
    @GetMapping("/return")
    @AnonymousAccess
    @ApiOperation("支付之后跳转的链接")
    public ResponseEntity<String> returnPage(HttpServletRequest request, HttpServletResponse response) {
        AlipayConfig alipay = alipayService.find();
        response.setContentType("text/html;charset=" + alipay.getCharset());
        //内容验签，防止黑客篡改参数
        if (alipayUtils.rsaCheck(request, alipay)) {
            //商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            System.out.println("商户订单号" + outTradeNo + "  " + "第三方交易号" + tradeNo);

            // 根据业务需要返回数据，这里统一返回OK
            return new ResponseEntity<>("payment successful", HttpStatus.OK);
        } else {
            // 根据业务需要返回数据
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 授权码
     * @param authCode
     * @return
     * @throws AlipayApiException
     */
    @RequestMapping("getInfo")
    public ResponseEntity<String> getInfo(@Validated @RequestBody AuthCodeDto authCode) throws AlipayApiException {
        //使用支付宝小程序的固定方法获取auth_code
        if (authCode == null || authCode.getAuthCode() ==null || authCode.getAuthCode().length() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            String userId=AlipayUtils.getUserId(authCode.getAuthCode());
            if (StringUtils.isNotEmpty(userId)) {
                return new ResponseEntity<>(userId, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @ApiIgnore
    @RequestMapping("/notify")
    @AnonymousAccess
    @SuppressWarnings("all")
    @ApiOperation("支付异步通知(要公网访问)，接收异步通知，检查通知内容app_id、out_trade_no、total_amount是否与请求中的一致，根据trade_status进行后续业务处理")
    public ResponseEntity<Object> notify(HttpServletRequest request) {
        AlipayConfig alipay = alipayService.find();
        Map<String, String[]> parameterMap = request.getParameterMap();
        //内容验签，防止黑客篡改参数
        if (alipayUtils.rsaCheck(request, alipay)) {
            //交易状态
            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            // 商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //付款金额
            String totalAmount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //验证
            if (tradeStatus.equals(AliPayStatusEnum.SUCCESS.getValue()) || tradeStatus.equals(AliPayStatusEnum.FINISHED.getValue())) {
                // 验证通过后应该根据业务需要处理订单
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @Log("支付宝小程序上传文件")
    @ApiOperation("支付宝小程序上传文件")
    @RequestMapping(value="/upload",method=RequestMethod.POST)
    public ResponseEntity<Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest req =(MultipartHttpServletRequest)request;
        MultipartFile multipartFile =  req.getFile("files");

        StringBuffer nowStr = FileUtil.fileRename();
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);

        String fileName = nowStr + "." + type;
        // 取出字节，这个就是重点。有了图片的字节，就可以进行业务处理了
        byte[] content = multipartFile.getBytes();

        String url = filePath + type +  File.separator + fileName;
        InputStream fis = new ByteArrayInputStream(content);
        FileOutputStream fs = new FileOutputStream(url);

        //执行fileOutputStream的输出操作
        int len = 1;
        byte[] b = new byte[1024];
        while ((len = fis.read(b)) != -1) {
            fs.write(b, 0, len);
        }
        fs.close();
        fis.close();

        url = localUrl + "/file/" + type+ "/" + fileName;
        log.info("uplodad url: " + url);

        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("link", url);
        return new ResponseEntity(map, HttpStatus.CREATED);
    }

    public static void main(String[] args) {
        String fileName="image.JPEG";
        String fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
        System.out.println(fileType);
    }
}
