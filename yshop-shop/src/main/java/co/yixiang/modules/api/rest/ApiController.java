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
    @Log(value = "?????????????????????")
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
        log.info("?????????????????????{}",requestBody);
        log.info("timestamp={},nonce={},signature={},companyId={}",timestamp,nonce,signature,companyId);
        Result apiResult = validate( requestBody, timestamp, nonce, signature, companyId, request);
        if(apiResult != null) {
            return apiResult;
        }
        ApiRequest apiRequest = JsonUtil.getJsonToBean(requestBody,ApiRequest.class);

        if(RequestTypeEnum.addDemandList.getValue().equals(apiRequest.getRequestType())) {
            // msh???????????????
            MshDemandDto mshDemandDto= JsonUtil.getJsonToBean(JsonUtil.getBeanToJson(apiRequest.getRequestData()),MshDemandDto.class);
            if(StringUtils.isEmpty(mshDemandDto.getSource())){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????source??????");
            }
            if(StringUtils.isEmpty(mshDemandDto.getDemandNo())){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????demandNo??????");
            }
            mshDemandListService.addMshDemandList(mshDemandDto);
            return Result.OK();

        }
        if(RequestTypeEnum.queryLogistics.getValue().equals(apiRequest.getRequestType())){
            // msh????????????????????????
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("phaOrderNo"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????phaOrderNo??????");
            }
            return mshOrderService.queryMshOrderLogisticsProcess(jsonObject.get("phaOrderNo").toString());

        }

        if(RequestTypeEnum.queryOrderDetail.getValue().equals(apiRequest.getRequestType())){
            // msh??????????????????
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("phaOrderNo"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????phaOrderNo??????");
            }

            return mshOrderService.queryMshOrderDetailInfo(jsonObject.get("phaOrderNo").toString());

        }
        if(RequestTypeEnum.queryMdCountry.getValue().equals(apiRequest.getRequestType())){
            // msh???????????????
            JSONObject jsonObject=JSONObject.fromObject(apiRequest.getRequestData());
            if(org.springframework.util.StringUtils.isEmpty(jsonObject.get("parentCode"))){
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????parentCode??????");
            }

            return mdCountryService.queryMdCountry(jsonObject.get("parentCode").toString());
        }
        return Result.error("????????????requestType?????????");
    }

    Result<?> validate(String requestBody, String timestamp, String nonce, String signature, String companyId, HttpServletRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        // ???????????????????????????
        if(!JSONUtil.isJson(requestBody)){

            return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"??????????????????????????????");
        }else{

            apiRequest = JsonUtil.getJsonToBean(requestBody,ApiRequest.class);
            if( StrUtil.isBlank(apiRequest.getCompanyId())  ) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????companyId??????");
            }

            if( StrUtil.isBlank(apiRequest.getRequestType() )) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????requestType??????");
            }

            if(  StrUtil.isBlank(apiRequest.getRequestId()) ) {
                return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"????????????requestId??????");
            }

        }
        // ??????requestType
        String requestType = apiRequest.getRequestType();
        RequestTypeEnum requestTypeEnum = RequestTypeEnum.toType(requestType);
        if(requestTypeEnum == null) {
            return Result.error(SystemConfigConstants.SC_REQUESTTYPE_ERROR_502,"???????????????????????????");
        }



        // ??????????????????????????????
        if(StrUtil.isBlank(timestamp) || StrUtil.isBlank(nonce) || StrUtil.isBlank(companyId) || StrUtil.isBlank(signature) ) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"?????????????????????????????????????????????");
        }

        //?????????????????????????????????10??????
        long time = System.currentTimeMillis();
        log.info("current time={}",time);
        long time2 = Long.valueOf(timestamp).longValue();
        /*if(time < time2 || time - time2 > 10*60*1000 ) {
            return ApiResult.error(CommonConstant.SC_SIGNATURE_ERROR_501,"???????????????timestamp?????????????????????????????????",apiRequest.getRequestType(), apiRequest.getRequestId(), apiRequest.getCompanyId());
        }*/


        // ??????companyId ??????token
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Project::getCompanyId,companyId);

        Project project = projectService.getOne(lambdaQueryWrapper,false);
        if(project == null) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"companyId["+ companyId+"]????????????????????????????????????");
        }

        String token_comapnyid = project.getToken();

        log.info( " company toen={}",token_comapnyid);
        String signature_companyid = SignUtil.getSha1Signature(timestamp,nonce,token_comapnyid);
        log.info("signature_companyid={}",signature_companyid);
        if( !signature_companyid.equals(signature)) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"?????????????????????????????????????????????");
        }
        return null;
    }


    @GetMapping("/test")
    @Log(value = "?????????????????????")
    @AnonymousAccess
    public void test(@Validated
                          @RequestParam(value = "",required=false) String msg,
                          HttpServletRequest request) {
        webSocket.pushMessage(msg);


    }

    @GetMapping("/yaolian/sync")
    @Log(value = "???????????????????????????????????????")
    @AnonymousAccess
    public Result<?> yaolian(
                     HttpServletRequest request) {
        yaolianService.syncData();

        return Result.OK();
    }


    @Log("oms ??????????????????")
    @PostMapping("/baiji/omsSyncBaiJiStoreMedStock")
    @ApiOperation(value = "oms ??????????????????",notes = "oms ??????????????????",response = String.class)
    @AnonymousAccess
    public Result<?> omsSyncBaiJiStoreMedStock(@Validated @RequestBody ProductMedStockParam productMedStockParam){
        log.info("/baiji/omsSyncBaiJiStoreMedStock orderFreightParams={}",JSONUtil.parseObj(productMedStockParam));
        productMedStockService.omsSyncBaiJiStoreMedStock(productMedStockParam);
        return Result.OK();
    }

    @Log("oms ???????????????????????????")
    @PostMapping("/baiji/omsSyncBaiJiStoreMed")
    @ApiOperation(value = "oms ???????????????????????????",notes = "oms ???????????????????????????",response = String.class)
    @AnonymousAccess
    public  Result<?>  omsSyncBaiJiStoreMed(@Validated @RequestBody ProductMedStockParam productMedStockParam){
        log.info("/baiji/omsSyncBaiJiStoreMedStock orderFreightParams={}",JSONUtil.parseObj(productMedStockParam));
        productMedStockService.omsSyncBaiJiStoreMed(productMedStockParam);
        return Result.OK();
    }

}
