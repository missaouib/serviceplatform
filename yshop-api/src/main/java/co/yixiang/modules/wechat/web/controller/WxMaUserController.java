/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.wechat.web.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.PhoneBindSourceEnum;
import co.yixiang.enums.RedisKeyEnum;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.notify.NotifyType;
import co.yixiang.modules.notify.SmsResult;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.security.rest.param.VerityParam;
import co.yixiang.modules.security.security.vo.JwtUser;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.entity.YxStoreCart;
import co.yixiang.modules.shop.entity.YxStoreCoupon;
import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.service.YxStoreCartService;
import co.yixiang.modules.shop.service.YxStoreCouponService;
import co.yixiang.modules.shop.service.YxStoreCouponUserService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.entity.YxWechatUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.service.YxWechatUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.wechat.web.param.BindPhoneParam;
import co.yixiang.modules.wechat.web.param.WxPhoneParam;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.mp.utils.JsonUtils;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author hupeng
 * @date 2020/02/07
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "微信其他", tags = "微信:微信其他", description = "微信其他")
@Slf4j
public class WxMaUserController {

    private final WxMaService wxMaService;
    private final YxWechatUserService wechatUserService;
    private final YxUserService userService;
    private final RedisUtils redisUtils;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreCartService yxStoreCartService;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    @Autowired
    private YaoshitongPatientService patientService;

    @PostMapping("/binding")
    @ApiOperation(value = "绑定手机号", notes = "绑定手机号")
    @AnonymousAccess
    public ApiResult<String> binding(@Validated @RequestBody BindPhoneParam param, HttpServletRequest request) {

        Object codeObj = redisUtils.get("code_" + param.getPhone());
        if(codeObj == null){
            return ApiResult.fail("请先获取验证码");
        }
        String code = codeObj.toString();


        if (!StrUtil.equals(code, param.getCaptcha())) {
            return ApiResult.fail("验证码错误");
        }

/*

        if(param.getBindSource() == null) {
            param.setBindSource(PhoneBindSourceEnum.SOOURCE_2.getValue());
        }

        if( PhoneBindSourceEnum.SOOURCE_2.getValue().equals(param.getBindSource())) {
            int uid = SecurityUtils.getUserId().intValue();
            YxUser yxUserExists= userService.getById(uid);
            // H5
            if(StrUtil.isNotBlank(yxUserExists.getPhone())){
                return ApiResult.fail("您的账号已经绑定过手机号码");
            }

            YxUser yxUser = new YxUser();
            yxUser.setPhone(param.getPhone());
            yxUser.setUid(uid);
            yxUser.setRealName(param.getRealName());
            userService.updateById(yxUser);
            return ApiResult.ok("绑定成功");
        } else if(PhoneBindSourceEnum.SOOURCE_1.getValue().equals(param.getBindSource())) {
            Object object = userService.binding(param,request);

            return ApiResult.ok(object);
        }*/

        Object object = userService.binding(param,request);

        return ApiResult.ok(object);

    //    return ApiResult.ok("绑定成功");

    }

    @PostMapping("/bindingYaoshitong")
    @ApiOperation(value = "药师通公众号绑定手机号", notes = "药师通公众号绑定手机号")
    public ApiResult<String> bindingYaoshitong(@Validated @RequestBody BindPhoneParam param) {

        MdPharmacistService pharmacist = pharmacistService.getOne(new QueryWrapper<MdPharmacistService>().eq("phone",param.getPhone()));
        if(pharmacist == null) {
            return ApiResult.fail("请联系管理员加入药师团队");
        }
        Object codeObj = redisUtils.get("code_" + param.getPhone());
        if(codeObj == null){
            return ApiResult.fail("请先获取验证码");
        }
        String code = codeObj.toString();


        if (!StrUtil.equals(code, param.getCaptcha())) {
            return ApiResult.fail("验证码错误");
        }

        int uid = SecurityUtils.getUserId().intValue();
        YxUserQueryVo userQueryVo = userService.getYxUserById(uid);
        if(StrUtil.isNotBlank(userQueryVo.getYaoshiPhone())){
            return ApiResult.fail("您的账号已经绑定过手机号码");
        }

        YxUser yxUser = new YxUser();
        yxUser.setYaoshiPhone(param.getPhone());
        //yxUser.setPhone(param.getPhone());
        yxUser.setUid(uid);
        yxUser.setRealName(pharmacist.getName());
        userService.updateById(yxUser);

        pharmacist.setUid(uid);
        pharmacistService.updateById(pharmacist);

        return ApiResult.ok("绑定成功");

    }


    @PostMapping("/wxapp/binding")
    @ApiOperation(value = "小程序绑定手机号", notes = "小程序绑定手机号")
    public ApiResult<String> phone(@Validated @RequestBody WxPhoneParam param) {

        int uid = SecurityUtils.getUserId().intValue();
        YxUserQueryVo userQueryVo = userService.getYxUserById(uid);
        if(StrUtil.isNotBlank(userQueryVo.getPhone())){
            return ApiResult.fail("您的账号已经绑定过手机号码");
        }

        //读取redis配置
        String appId = RedisUtil.get(RedisKeyEnum.WXAPP_APPID.getValue());
        String secret = RedisUtil.get(RedisKeyEnum.WXAPP_SECRET.getValue());
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(secret)) {
            throw new ErrorRequestException("请先配置小程序");
        }
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(appId);
        wxMaConfig.setSecret(secret);
        wxMaService.setWxMaConfig(wxMaConfig);
        String phone = "";
        try {
            if(StrUtil.isNotBlank(param.getCode())) {
                log.info("根据code[{}]获取手机号,EncryptedData=[{}],Iv=[{}]",param.getCode(),param.getEncryptedData(), param.getIv());
                WxMaJscode2SessionResult session = wxMaService.getUserService()
                        .getSessionInfo(param.getCode());
                // 解密
                WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService()
                        .getPhoneNoInfo(session.getSessionKey(), param.getEncryptedData(), param.getIv());
                phone = phoneNoInfo.getPhoneNumber();
            } else if(StrUtil.isNotBlank(param.getSessionKey())) {
                log.info("根据sessionKey[{}]获取手机号,EncryptedData=[{}],Iv=[{}]",param.getSessionKey(),param.getEncryptedData(), param.getIv());
                // 解密
                WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService()
                        .getPhoneNoInfo(param.getSessionKey(), param.getEncryptedData(), param.getIv());
                log.info("phoneNoInfo={}", JSONUtil.parseObj(phoneNoInfo).toString());
                phone = phoneNoInfo.getPhoneNumber();
            } else {
                throw new ErrorRequestException("参数code和sessionKey不能同时为空");
            }
            YxUser byPhone= userService.getNewYxUserByPhone(phone);
            if(byPhone==null){
                YxUser yxUser = new YxUser();
                yxUser.setPhone(phone);
                yxUser.setUid(uid);
                userService.updateById(yxUser);
            }else{
                Integer id=byPhone.getUid();
                userService.removeById(id);
                wechatUserService.removeById(id);
                userService.updateUidAndPhoneById(id,phone,uid);
                wechatUserService.updateUidById(id,uid);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error("小程序绑定手机号异常========================================");
            return ApiResult.fail(e.getMessage());
            //e.printStackTrace();
        }catch (Exception e) {
            log.error("小程序绑定手机号异常========================================");
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("phone",phone);

       /* UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("phone",phone);
        updateWrapper.set("uid",uid);
        patientService.update(updateWrapper);*/

        return ApiResult.ok(map,"绑定成功");
    }




    @PostMapping("/bindingPatient")
    @ApiOperation(value = "患者信息", notes = "患者信息")
    public ApiResult<String> bindingPatient() {

        Integer uid = SecurityUtils.getUserId().intValue();

        //根据患者id 查是否绑定
        YxUserQueryVo userQueryVo = userService.getYxUserById(uid);
        String phone = userQueryVo.getPhone();

        if( StrUtil.isNotBlank(phone)){

            YaoshitongPatient patient = patientService.getOne(new QueryWrapper<YaoshitongPatient>().eq("phone",phone).last("limit 1"),false);
            if(patient != null) {
                return ApiResult.ok(patient);
            } else {
                YaoshitongPatient patient_tmp = new YaoshitongPatient();
                patient_tmp.setPhone(phone);
                patient_tmp.setSex("");
                patient_tmp.setIdCard("");
                patient_tmp.setName("");
                patient_tmp.setBirth("");
                patient_tmp.setDiagnosisHistory("");
                patient_tmp.setDrugAllergy("");
                patient_tmp.setMedicalHistory("");
                patient_tmp.setDrugContraindications("");
                patient_tmp.setUpdateUser("");
                patient_tmp.setSocialCard("");
                patient_tmp.setAddress("");
                patient_tmp.setAge(0);
                return ApiResult.ok(patient_tmp);
            }

        } else {
            ApiResult.ok(new YaoshitongPatient());
        }

        YaoshitongPatient patient_tmp = new YaoshitongPatient();
        patient_tmp.setPhone(phone);
        patient_tmp.setSex("");
        patient_tmp.setIdCard("");
        patient_tmp.setName("");
        patient_tmp.setBirth("");
        patient_tmp.setDiagnosisHistory("");
        patient_tmp.setDrugAllergy("");
        patient_tmp.setMedicalHistory("");
        patient_tmp.setDrugContraindications("");
        patient_tmp.setUpdateUser("");
        patient_tmp.setSocialCard("");
        patient_tmp.setAddress("");
        patient_tmp.setAge(0);
        return ApiResult.ok(patient_tmp);


    }
}
