package co.yixiang.tools.utils.mpai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bestpay.api.BestpayClient;
import com.bestpay.api.common.BestpayErrorCode;
import com.bestpay.api.common.EnvEnum;
import com.bestpay.api.exception.BestpayApiException;
import com.bestpay.api.model.request.BestpayRequest;
import com.bestpay.api.model.response.BestpayResponse;
import com.bestpay.api.util.BestpayLogger;
import com.bestpay.api.util.CryptoUtil;
import com.bestpay.api.util.HttpClientUtils;
import com.bestpay.api.util.KeyCertInfo;
import com.bestpay.api.util.SignatureUtil;
import com.bestpay.api.util.StringUtils;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class UpdateDefaultBestpayClient implements BestpayClient {
    private String userCertificateName;
    private String serviceCertificateName;
    private String passwd;
    private String alias;
    private String keyStoreType;
    private KeyCertInfo keyCertInfo;

    public UpdateDefaultBestpayClient(String userCertificateName, String serviceCertificateName, String passwd, String alias, String keyStoreType) {
        this.userCertificateName = userCertificateName;
        this.serviceCertificateName = serviceCertificateName;
        this.passwd = passwd;
        this.alias = alias;
        this.keyStoreType = keyStoreType;
        this.init();
    }

    private void init() {
        InputStream resourceAsStream = com.bestpay.api.DefaultBestpayClient.class.getClassLoader().getResourceAsStream(this.userCertificateName);
        this.keyCertInfo = CryptoUtil.fileStreamToKeyCertInfo(resourceAsStream, this.passwd, this.keyStoreType, this.alias);
    }

    public BestpayResponse execute(BestpayRequest request) throws BestpayApiException {
        String url = EnvEnum.TEST.getUrl();
        if (request.getEnv() != null) {
            url = request.getEnv().getUrl();
        }

        if (request.getVersion() != null) {
            url = url + "?" + "BESTPAY_MAPI_VERSION" + "=" + request.getVersion();
        } else {
            url = url + "?" + "BESTPAY_MAPI_VERSION" + "=" + "1.0";
        }

        String requestParamStr = JSONObject.toJSONString(request);
        JSONObject jsonObject = JSONObject.parseObject(requestParamStr);
        jsonObject.remove("env");
        jsonObject.remove("version");
        requestParamStr = jsonObject.toJSONString();

        String param;
        try {
            param = this.prepareParam(requestParamStr);
        } catch (Exception var10) {
            throw new BestpayApiException(BestpayErrorCode.ACQ_REQUEST_SIGN_ERROR);
        }

        BestpayLogger.logBizError("请求参数......param:" + param);
        String result = HttpClientUtils.restfulPost(url, param, Integer.valueOf(5000), Integer.valueOf(5000));
        BestpayLogger.logBizError("响应参数......response:" + result);
        if (!StringUtils.isEmpty(result)) {
            Map resultMap = JSON.parseObject(result,Feature.OrderedField);
            boolean isOk = SignatureUtil.checkSign(resultMap, this.serviceCertificateName);
            if (isOk) {
                BestpayResponse bestpayResponse = (BestpayResponse)JSONObject.parseObject(result, BestpayResponse.class);
                return bestpayResponse;
            } else {
                throw new BestpayApiException(BestpayErrorCode.ACQ_RESPONSE_SIGN_ERROR);
            }
        } else {
            throw new BestpayApiException(BestpayErrorCode.ACQ_SYSTEM_ERROR);
        }
    }

    public String prepareParam(String requestParam) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        Signature signature = Signature.getInstance("SHA256withRSA", bouncyCastleProvider);
        Map<String, String> translateResultData = (Map)JSONObject.parseObject(requestParam, Map.class);
        String content = SignatureUtil.assembelSignaturingData(translateResultData);
        String sign = SignatureUtil.sign(signature, content, (PrivateKey)this.keyCertInfo.getPrivateKey());
        translateResultData.put("sign", sign);
        return JSON.toJSONString(translateResultData);
    }

    public boolean checkSign(String checkSignContent) throws Exception {
        boolean isOk = false;

        try {
            if (!StringUtils.isEmpty(checkSignContent)) {
                Map resultMap = JSON.parseObject(checkSignContent);
                isOk = SignatureUtil.checkSign(resultMap, this.serviceCertificateName);
                return isOk;
            } else {
                throw new BestpayApiException(BestpayErrorCode.ACQ_SYSTEM_ERROR);
            }
        } catch (Exception var4) {
            throw new BestpayApiException(BestpayErrorCode.ACQ_RESPONSE_SIGN_ERROR);
        }
    }
}
