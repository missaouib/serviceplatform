package co.yixiang.modules.api.rest;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;

import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.RequestTypeEnum;
import co.yixiang.logging.aop.log.Log;

import co.yixiang.modules.api.common.ApiRequest;
import co.yixiang.modules.api.common.Result;
import co.yixiang.modules.api.param.ProductMedStockParam;
import co.yixiang.modules.baiji.service.ProductMedStockServiceImpl;
import co.yixiang.modules.msh.service.MshDemandListService;
import co.yixiang.modules.msh.service.MshOrderService;
import co.yixiang.modules.msh.service.dto.MshDemandDto;
import co.yixiang.modules.shop.domain.Project;
import co.yixiang.modules.shop.service.MdCountryService;
import co.yixiang.modules.shop.service.ProjectService;

import co.yixiang.modules.websocket.WebSocket;
import co.yixiang.modules.yaolian.service.YaolianServiceImpl;
import co.yixiang.utils.JsonUtil;
import co.yixiang.utils.SignUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/gateway")
@Slf4j
public class ApiController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MshDemandListService mshDemandListService;

    @Autowired
    private MshOrderService mshOrderService;

    @Autowired
    private MdCountryService mdCountryService;

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private YaolianServiceImpl yaolianService;

    @Autowired
    private ProductMedStockServiceImpl productMedStockService;

    @PostMapping()
    @Log(value = "第三方接口调用")
    @AnonymousAccess
    public Result<?> post(@Validated @RequestBody String requestBody,
                          @RequestParam(value = "",required=false) String timestamp,
                          @RequestParam(value = "",required=false) String nonce,
                          @RequestParam(value = "",required=false) String signature,
                          @RequestParam(value = "",required=false) String companyId,
                          HttpServletRequest request) {
        try {
            requestBody = java.net.URLDecoder.decode(requestBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("第三方接口调用{}",requestBody);
        log.info("timestamp={},nonce={},signature={},companyId={}",timestamp,nonce,signature,companyId);
        Result apiResult = validate( requestBody, timestamp, nonce, signature, companyId, request);
        if(apiResult != null) {
            return apiResult;
        }
        ApiRequest apiRequest = JsonUtil.getJsonToBean(requestBody,ApiRequest.class);

        if(RequestTypeEnum.addDemandList.getValue().equals(apiRequest.getRequestType())) {
            // msh新增需求单
            MshDemandDto mshDemandDto= JsonUtil.getJsonToBean(JsonUtil.getBeanToJson(apiRequest.getRequestData()),MshDemandDto.class);
            if(StringUtils.isEmpty(mshDemandDto.getSource())){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中source为空");
            }
            if(StringUtils.isEmpty(mshDemandDto.getDemandNo())){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中demandNo为空");
            }
            mshDemandListService.addMshDemandList(mshDemandDto);
            return Result.OK();

        }
        if(RequestTypeEnum.queryLogistics.getValue().equals(apiRequest.getRequestType())){
            // msh物流轨迹信息查询
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("phaOrderNo"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中phaOrderNo为空");
            }
            return mshOrderService.queryMshOrderLogisticsProcess(jsonObject.get("phaOrderNo").toString());

        }

        if(RequestTypeEnum.queryOrderDetail.getValue().equals(apiRequest.getRequestType())){
            // msh订单明细查询
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("phaOrderNo"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中phaOrderNo为空");
            }

            return mshOrderService.queryMshOrderDetailInfo(jsonObject.get("phaOrderNo").toString());

        }
        if(RequestTypeEnum.queryMdCountry.getValue().equals(apiRequest.getRequestType())){
            // msh省市区查询
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("parentCode"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中parentCode为空");
            }

            return mdCountryService.queryMdCountry(jsonObject.get("parentCode").toString());
        }
        return Result.error("报文体中requestType有误。");
    }

    Result<?> validate(String requestBody, String timestamp, String nonce, String signature, String companyId, HttpServletRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        // 校验报文体是否正确
        if(!JSONUtil.isJson(requestBody)){

            return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文内容格式解析错误");
        }else{

            apiRequest = JsonUtil.getJsonToBean(requestBody,ApiRequest.class);
            if( StrUtil.isBlank(apiRequest.getCompanyId())  ) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中companyId为空");
            }

            if( StrUtil.isBlank(apiRequest.getRequestType() )) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中requestType为空");
            }

            if(  StrUtil.isBlank(apiRequest.getRequestId()) ) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文体中requestId为空");
            }

        }
        // 校验requestType
        String requestType = apiRequest.getRequestType();
        RequestTypeEnum requestTypeEnum = RequestTypeEnum.toType(requestType);
        if(requestTypeEnum == null) {
            return Result.error(SystemConfigConstants.SC_REQUESTTYPE_ERROR_502,"接口类型代码找不到");
        }



        // 校验签名参数是否为空
        if(StrUtil.isBlank(timestamp) || StrUtil.isBlank(nonce) || StrUtil.isBlank(companyId) || StrUtil.isBlank(signature) ) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"签名参数为空，报文签名验证失败");
        }

        //校验时间戳是否是否小于10分钟
        long time = System.currentTimeMillis();
        log.info("current time={}",time);
        long time2 = Long.valueOf(timestamp).longValue();
        /*if(time < time2 || time - time2 > 10*60*1000 ) {
            return ApiResult.error(CommonConstant.SC_SIGNATURE_ERROR_501,"时间戳参数timestamp不对，报文签名验证失败",apiRequest.getRequestType(), apiRequest.getRequestId(), apiRequest.getCompanyId());
        }*/


        // 根据companyId 获取token
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getCompanyId,companyId);

        Project project = projectService.getOne(lambdaQueryWrapper,false);
        if(project == null) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"companyId["+ companyId+"]找不到，报文签名验证失败");
        }

        String token_comapnyid = project.getToken();

        log.info( " company toen={}",token_comapnyid);
        String signature_companyid = SignUtil.getSha1Signature(timestamp,nonce,token_comapnyid);
        log.info("signature_companyid={}",signature_companyid);
        if( !signature_companyid.equals(signature)) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"安全验证错误，报文签名验证失败");
        }
        return null;
    }


    @GetMapping("/test")
    @Log(value = "第三方接口调用")
    @AnonymousAccess
    public void test(@Validated
                          @RequestParam(value = "",required=false) String msg,
                          HttpServletRequest request) {
        webSocket.pushMessage(msg);


    }

    @GetMapping("/yaolian/sync")
    @Log(value = "第三方接口调用药联数据同步")
    @AnonymousAccess
    public Result<?> yaolian(
                     HttpServletRequest request) {
        yaolianService.syncData();

        return Result.OK();
    }


    @Log("oms 同步百济库存")
    @PostMapping("/baiji/omsSyncBaiJiStoreMedStock")
    @ApiOperation(value = "oms 同步百济库存",notes = "oms 同步百济库存",response = String.class)
    @AnonymousAccess
    public Result<?> omsSyncBaiJiStoreMedStock(@Validated @RequestBody ProductMedStockParam productMedStockParam){
        log.info("/baiji/omsSyncBaiJiStoreMedStock orderFreightParams={}",JSONUtil.parseObj(productMedStockParam));
        productMedStockService.omsSyncBaiJiStoreMedStock(productMedStockParam);
        return Result.OK();
    }

    @Log("oms 同步百济药品主数据")
    @PostMapping("/baiji/omsSyncBaiJiStoreMed")
    @ApiOperation(value = "oms 同步百济药品主数据",notes = "oms 同步百济药品主数据",response = String.class)
    @AnonymousAccess
    public  Result<?>  omsSyncBaiJiStoreMed(@Validated @RequestBody ProductMedStockParam productMedStockParam){
        log.info("/baiji/omsSyncBaiJiStoreMedStock orderFreightParams={}",JSONUtil.parseObj(productMedStockParam));
        productMedStockService.omsSyncBaiJiStoreMed(productMedStockParam);
        return Result.OK();
    }

}
