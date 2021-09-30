package co.yixiang.modules.zhongan.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.shop.entity.YxDrugUsers;
import co.yixiang.modules.shop.entity.YxStoreCoupon;
import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.service.YxDrugUsersService;
import co.yixiang.modules.shop.service.YxStoreCouponService;
import co.yixiang.modules.shop.service.YxStoreCouponUserService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.wechat.web.param.BindPhoneParam;
import co.yixiang.modules.zhongan.*;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.OrderUtil;
import com.alipay.api.domain.Coupon;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 太平乐享虚拟卡 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
@Slf4j
@RestController
@RequestMapping("/zhongan")
@Api("众安 API")
public class ZhongAnController extends BaseController {

    @Autowired
    private ZhongAnMaBingServiceImpl zhongAnMaBingService;


    @Autowired
    private YxUserService userService;

    @Autowired
    private YxStoreOrderService yxStoreOrderService;

    @Autowired
    private YxStoreCouponUserService couponUserService;

    @Autowired
    private YxStoreCouponService couponService;

    @Autowired
    private YxDrugUsersService yxDrugUsersService;

    @Value("${zhonganpuyao.appKey}")
    private String appKey;

    @Value("${zhonganpuyao.url}")
    private String url;

    @Value("${zhonganpuyao.version}")
    private String version;

    @Value("${zhonganpuyao.privateKey}")
    private String privateKey;

    /**
     * 众安慢病参数解析
     */
    @AnonymousAccess
    @Log(value = "众安慢病参数解析")
    @PostMapping("/analysis")
    @ApiOperation(value = "众安慢病参数解析",notes = "众安慢病参数解析",response = ZhongAnParamDto.class)
    public ApiResult<ZhongAnParamDto> analysis(@Valid @RequestBody ZhongAnParamDto zhongAnParamDto, HttpServletRequest request) throws Exception{

        ZhongAnParamDto param  = zhongAnMaBingService.analysisParam(zhongAnParamDto);

        BindPhoneParam bindPhoneParam = new BindPhoneParam();
        bindPhoneParam.setPhone(param.getCardType());
        Object object = userService.binding(bindPhoneParam,request);
        Map<String, String> map = (Map<String, String>)object;
        String token = map.get("token");
        String expiresTime = map.get("expires_time");
        param.setToken(token);
        param.setExpiresTime(expiresTime);

        // 更新众安的用户id

        LambdaUpdateWrapper<YxUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(YxUser::getPhone,param.getCardType());
        updateWrapper.set(YxUser::getZhonganCardNumber,param.getCardNumber());

        userService.update(updateWrapper);
        //同步优惠券
        try{
            if(StringUtils.isNotEmpty(param.getProjectCode()) && ProjectNameEnum.ZHONGANPUYAO.getValue().equals(param.getProjectCode())){
                couponUserService.updateCoupon(param,null);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ApiResult.ok(param);
    }
}

