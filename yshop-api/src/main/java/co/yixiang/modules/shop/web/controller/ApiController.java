package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiRequest;
import co.yixiang.common.api.Result;
import co.yixiang.common.constant.CommonConstant;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.RequestTypeEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.entity.Project;
import co.yixiang.modules.shop.entity.YxStoreCoupon;
import co.yixiang.modules.shop.entity.YxStoreCouponCard;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.service.YxStoreCouponCardService;
import co.yixiang.modules.shop.service.YxStoreCouponService;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SignUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/yiyaomall")
@Slf4j
public class ApiController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private YxStoreCouponService yxStoreCouponService;

    @Autowired
    private YxStoreCouponCardService yxStoreCouponCardService;

    @PostMapping()
    @Log(value = "第三方接口调用")
    @AnonymousAccess
    public Result<?> post(@Validated @RequestBody String requestBody,
                          @RequestParam(value = "",required=false) String timestamp,
                          @RequestParam(value = "",required=false) String nonce,
                          @RequestParam(value = "",required=false) String signature,
                          @RequestParam(value = "",required=false) String companyId,
                          HttpServletRequest request) {
         log.info("第三方接口调用{}",requestBody);
         log.info("timestamp={},nonce={},signature={},companyId={}",timestamp,nonce,signature,companyId);

        Result apiResult = validate( requestBody, timestamp, nonce, signature, companyId, request);
        if(apiResult != null) {
            return apiResult;
        }
        ApiRequest apiRequest = JSONUtil.toBean(requestBody,ApiRequest.class);

        if( RequestTypeEnum.addCoupon.getValue().equals(apiRequest.getRequestType())) {
//  新增优惠券接口
            String requestId = apiRequest.getRequestId();
            LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Project::getId,companyId);
            Project project = projectService.getOne(lambdaQueryWrapper,false);

            String projectCode = project.getProjectCode();
            JSONObject jsonObject = JSONUtil.parseObj(apiRequest.getRequestData());
            jsonObject.setDateFormat("yyyy-MM-dd");
            String effectiveDate_str = jsonObject.getStr("effectiveDate");

            YxStoreCoupon yxStoreCoupon = JSONUtil.toBean( jsonObject, YxStoreCoupon.class);
            yxStoreCoupon.setProjectCode(projectCode);
            yxStoreCoupon.setAddTime(OrderUtil.getSecondTimestampTwo());

            if( StrUtil.isNotBlank(effectiveDate_str)) {
                yxStoreCoupon.setEffectiveDate(DateUtil.parse(effectiveDate_str));
            }

            try {

                LambdaQueryWrapper<YxStoreCoupon> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                lambdaQueryWrapper1.eq(YxStoreCoupon::getProjectCode,project);
                lambdaQueryWrapper1.eq(YxStoreCoupon::getTitle,yxStoreCoupon.getTitle());
                YxStoreCoupon yxStoreCoupon1 = yxStoreCouponService.getOne(lambdaQueryWrapper1,false);
                if(yxStoreCoupon1 == null) {
                    yxStoreCouponService.save(yxStoreCoupon);
                } else {
                    yxStoreCoupon.setId(yxStoreCoupon1.getId());
                    yxStoreCouponService.updateById(yxStoreCoupon);
                }

                return Result.OK();
            }  catch (Exception e) {
                return Result.error(SystemConfigConstants.SC_INTERNAL_SERVER_ERROR_500,e.getMessage());
            }

        }else if( RequestTypeEnum.issueCoupon.getValue().equals(apiRequest.getRequestType())) {
            // 发放优惠券
            LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Project::getId,companyId);
            Project project = projectService.getOne(lambdaQueryWrapper,false);
            String projectCode = project.getProjectCode();
            JSONObject jsonObject = JSONUtil.parseObj(apiRequest.getRequestData());


            YxStoreCouponCard yxStoreCouponCard = JSONUtil.toBean( jsonObject, YxStoreCouponCard.class);
            LambdaQueryWrapper<YxStoreCoupon> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(YxStoreCoupon::getTitle,yxStoreCouponCard.getCouponTitle());
            lambdaQueryWrapper1.eq(YxStoreCoupon::getProjectCode,projectCode);
            YxStoreCoupon yxStoreCoupon = yxStoreCouponService.getOne(lambdaQueryWrapper1,false);
            if(yxStoreCoupon == null) {
                return Result.error("优惠券["+ yxStoreCouponCard.getCouponTitle() +"]找不到");
            }
            yxStoreCouponCard.setCid(yxStoreCoupon.getId());
            yxStoreCouponCard.setProjectCode(projectCode);
            // 计算优惠券结束时间
            if(yxStoreCoupon.getEffectiveDate() != null) {
                yxStoreCouponCard.setEndTime( OrderUtil.dateToTimestamp(yxStoreCoupon.getEffectiveDate()));
            } else if(yxStoreCoupon.getCouponTime() != null) {
                yxStoreCouponCard.setEndTime(OrderUtil.dateToTimestamp(DateUtil.offsetDay(new Date(),yxStoreCoupon.getCouponTime())));
            } else {
                yxStoreCouponCard.setEndTime(OrderUtil.dateToTimestamp(DateUtil.offsetMonth(new Date(),12)));
            }



            yxStoreCouponCard.setAddTime(OrderUtil.getSecondTimestampTwo());
            yxStoreCouponCardService.save(yxStoreCouponCard);


        }




        return Result.OK();
    }

    Result<?> validate(String requestBody, String timestamp, String nonce, String signature, String companyId, HttpServletRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        // 校验报文体是否正确
        if(!JSONUtil.isJson(requestBody)){

            return Result.error(SystemConfigConstants.SC_REQUESTBODY_ERROR_503,"报文内容格式解析错误");
        }else{

            apiRequest = JSONUtil.toBean(requestBody,ApiRequest.class);
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
        lambdaQueryWrapper.eq(Project::getId,companyId);

        Project project = projectService.getOne(lambdaQueryWrapper,false);
        if(project == null) {
            return Result.error(SystemConfigConstants.SC_SIGNATURE_ERROR_501,"companyId["+ companyId+"]找不到，报文签名验证失败");
        }

        String token_comapnyid = project.getToken();

        log.info( " company toen={}",token_comapnyid);
        String signature_companyid = SignUtil.getSign(nonce,timestamp,token_comapnyid);
        log.info("signature_companyid={}",signature_companyid);
        /*if( ! signature_companyid.equals(signature)) {
            return ApiResult.error(CommonConstant.SC_SIGNATURE_ERROR_501,"安全验证错误，报文签名验证失败",apiRequest.getRequestType(), apiRequest.getRequestId(), apiRequest.getCompanyId());
        }*/

        return null;
    }




}
