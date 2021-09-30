/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.user.web.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.enums.OrderTypeEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.modules.manage.service.CheckOneService;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.service.YxStoreProductRelationService;
import co.yixiang.modules.shop.service.YxSystemGroupDataService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.*;
import co.yixiang.modules.user.web.dto.PrescriptionSimpleDTO;
import co.yixiang.modules.user.web.param.UserEditParam;
import co.yixiang.modules.user.web.param.YxUserSignQueryParam;
import co.yixiang.modules.user.web.vo.YxSystemUserLevelQueryVo;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPrescription;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.YaoshitongPrescriptionService;
import co.yixiang.modules.yiyaobao.service.impl.OrderServiceImpl;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-16
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户中心", tags = "用户:用户中心", description = "用户中心")
public class UserController extends BaseController {

    private final YxUserService yxUserService;
    private final YxSystemGroupDataService systemGroupDataService;
    private final YxStoreOrderService orderService;
    private final YxStoreProductRelationService relationService;
    private final YxUserSignService userSignService;
    private final YxUserBillService userBillService;
    private final YxSystemUserLevelService systemUserLevelService;

    private static Lock lock = new ReentrantLock(false);
    @Autowired
    private MdPharmacistServiceService pharmacistService;
    @Autowired
    private OrderServiceImpl yiyaobaoOrderService;
    @Autowired
    private InnerEmployeeService innerEmployeeService;

    @Autowired
    private YaoshitongPatientService yaoshitongPatientService;

    @Autowired
    private YaoshitongPrescriptionService yaoshitongPrescriptionService;

    @Autowired
    private CheckOneService checkOneService;
    /**
     * 用户资料
     */
    @GetMapping("/userinfo")
    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",response = YxUserQueryVo.class)
    public ApiResult<Object> userInfo(){
        int uid = SecurityUtils.getUserId().intValue();

        //update count
        yxUserService.setUserSpreadCount(uid);
        return ApiResult.ok(yxUserService.getNewYxUserById(uid,null));
    }

    /**
     * 获取个人中心菜单
     */
    @Log(value = "进入用户中心",type = 1)
    @GetMapping("/menu/user")
    @ApiOperation(value = "获取个人中心菜单",notes = "获取个人中心菜单")
    public ApiResult<Map<String,Object>> userMenu(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("routine_my_menus",systemGroupDataService.getDatas(ShopConstants.YSHOP_MY_MENUES));
        return ApiResult.ok(map);
    }

    /**
     * 个人中心
     */
    @GetMapping("/user")
    @ApiOperation(value = "个人中心",notes = "个人中心")
    public ApiResult<Object> user(@RequestParam(required = false) String projectCode){
        int uid = SecurityUtils.getUserId().intValue();

        /*QueryWrapper<YxStoreOrder> wrapper= new QueryWrapper<>();
        if(uid > 0) wrapper.eq("uid",uid);
        wrapper.eq("is_del",0).ne("type","慈善赠药").orderByDesc("add_time");

        List<YxStoreOrder> storeOrderList = orderService.list(wrapper);
        for(YxStoreOrder storeOrder : storeOrderList) {
            // 从益药宝获取订单
            if(storeOrder.getStatus() != 1 && storeOrder.getStatus() != 2 && storeOrder.getStatus() != 3 ) {
                Map<String,Integer> map = yiyaobaoOrderService.queryYiyaobaoOrderStatus(storeOrder.getOrderId());
                Integer orderStatus = map.get("status");
                Integer orderPaid = map.get("paid");
                storeOrder.setStatus(orderStatus);
                storeOrder.setPaid(orderPaid);
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("id",storeOrder.getId());
                updateWrapper.set("status",orderStatus);
                updateWrapper.set("paid",orderPaid);
                orderService.update(updateWrapper);
            }

        }
*/
        YxUserQueryVo yxUserQueryVo = yxUserService.getNewYxUserById(uid,projectCode);


        if(yxUserQueryVo.getLevel() > 0) {
            yxUserQueryVo.setVip(true);
            YxSystemUserLevelQueryVo systemUserLevelQueryVo = systemUserLevelService
                    .getYxSystemUserLevelById(yxUserQueryVo.getLevel());
            yxUserQueryVo.setVipIcon(systemUserLevelQueryVo.getIcon());
            yxUserQueryVo.setVipId(yxUserQueryVo.getLevel());
            yxUserQueryVo.setVipName(systemUserLevelQueryVo.getName());
        }



        return ApiResult.ok(yxUserQueryVo);
    }

    /**
     * 订单统计数据
     */
    @GetMapping("/order/data")
    @ApiOperation(value = "订单统计数据",notes = "订单统计数据")
    public ApiResult<Object> orderData(){
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(orderService.orderData(uid));
    }

    /**
     * 获取收藏产品
     */
    @GetMapping("/collect/user")
    @ApiOperation(value = "获取收藏产品",notes = "获取收藏产品")
    public ApiResult<Object> collectUser(@RequestParam(value = "page",defaultValue = "1") int page,
                                         @RequestParam(value = "limit",defaultValue = "10") int limit,
                                         @RequestParam(value = "projectCode",defaultValue = "") String projectCode
                                         ){
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(relationService.userCollectProduct(page,limit,uid,projectCode));
    }

    /**
     * 用户资金统计
     */
    @GetMapping("/user/balance")
    @ApiOperation(value = "用户资金统计",notes = "用户资金统计")
    public ApiResult<Object> collectUser(){
        int uid = SecurityUtils.getUserId().intValue();
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("now_money",yxUserService.getYxUserById(uid).getNowMoney());
        map.put("orderStatusSum",orderService.orderData(uid).getSumPrice());
        map.put("recharge",0);
        return ApiResult.ok(map);
    }

    /**
     * 获取活动状态
     */
    @AnonymousAccess
    @GetMapping("/user/activity")
    @ApiOperation(value = "获取活动状态",notes = "获取活动状态")
    @Deprecated
    public ApiResult<Object> activity(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("is_bargin",false);
        map.put("is_pink",false);
        map.put("is_seckill",false);
        return ApiResult.ok(map);
    }

    /**
     * 签到用户信息
     */
    @PostMapping("/sign/user")
    @ApiOperation(value = "签到用户信息",notes = "签到用户信息")
    public ApiResult<Object> sign(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        int uid = SecurityUtils.getUserId().intValue();
        YxUserQueryVo userQueryVo = yxUserService.getYxUserById(uid);
        int sumSignDay = userSignService.getSignSumDay(uid);
        boolean isDaySign = userSignService.getToDayIsSign(uid);
        boolean isYesterDaySign = userSignService.getYesterDayIsSign(uid);
        userQueryVo.setSumSignDay(sumSignDay);
        userQueryVo.setIsDaySign(isDaySign);
        userQueryVo.setIsYesterDaySign(isYesterDaySign);
        if(!isDaySign && !isYesterDaySign) userQueryVo.setSignNum(0);
        return ApiResult.ok(userQueryVo);
    }

    /**
     * 签到配置
     */
    @GetMapping("/sign/config")
    @ApiOperation(value = "签到配置",notes = "签到配置")
    public ApiResult<Object> signConfig(){
        return ApiResult.ok(systemGroupDataService.getDatas(ShopConstants.YSHOP_SIGN_DAY_NUM));
    }

    /**
     * 签到列表
     */
    @GetMapping("/sign/list")
    @ApiOperation(value = "签到列表",notes = "签到列表")
    public ApiResult<Object> signList(YxUserSignQueryParam queryParam){
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(userSignService.getSignList(uid,queryParam.getPage().intValue(),
                queryParam.getLimit().intValue()));
    }

    /**
     * 签到列表（年月）
     */
    @GetMapping("/sign/month")
    @ApiOperation(value = "签到列表（年月）",notes = "签到列表（年月）")
    public ApiResult<Object> signMonthList(YxUserSignQueryParam queryParam){
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(userBillService.getUserBillList(queryParam.getPage().intValue(),
                queryParam.getLimit().intValue(),uid,5));
    }

    /**
     * 开始签到
     */
    @PostMapping("/sign/integral")
    @ApiOperation(value = "开始签到",notes = "开始签到")
    public ApiResult<Object> signIntegral(){
        int uid = SecurityUtils.getUserId().intValue();
        boolean isDaySign = userSignService.getToDayIsSign(uid);
        if(isDaySign) return ApiResult.fail("已签到");
        int integral = 0;
        try {
            lock.lock();
            integral = userSignService.sign(uid);
        }finally {
            lock.unlock();
        }

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("integral",integral);
        return ApiResult.ok(map,"签到获得" + integral + "积分");
    }


    @PostMapping("/user/edit")
    @ApiOperation(value = "用户修改信息",notes = "用修改信息")
    public ApiResult<Object> edit(@Validated @RequestBody UserEditParam param){
        int uid = SecurityUtils.getUserId().intValue();
        YxUser yxUser =  yxUserService.getById(uid);
        if(StrUtil.isNotBlank(param.getCardId())) {
           // 实名认证
            Boolean flag = checkOneService.check(param.getCardId(),param.getRealName(),yxUser.getPhone());
            if(!flag) {
                throw new BadRequestException("实名认证失败，请正确填写姓名，手机号，身份证号");
            }

        }

      //  YxUser yxUser = new YxUser();
        yxUser.setAvatar(param.getAvatar());
        yxUser.setNickname(param.getNickname());
        yxUser.setUid(uid);
        yxUser.setInnerEmployeeCode(param.getInnerEmployeeCode());
        yxUser.setRepurchaseReminderFlag(param.getRepurchaseReminderFlag());
        yxUser.setRealName(param.getRealName());
        yxUser.setCardId(param.getCardId());
        if(param.getVipFlag() != null && StrUtil.isNotBlank(param.getCardId())) {
            yxUser.setVipFlag(param.getVipFlag());
        }
        /****
         * 判断是否内部员工
         * */
        if(StrUtil.isNotBlank(param.getInnerEmployeeCode())) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("code",param.getInnerEmployeeCode());
            int count = innerEmployeeService.count(queryWrapper);
            if(count > 0){
                yxUser.setIsInner(1);
            } else {
                yxUser.setIsInner(0);
            }
        } else {
            yxUser.setIsInner(0);
        }

        yxUserService.updateById(yxUser);

        return ApiResult.ok("修改成功");
    }


    @PostMapping("/user/editCardId")
    @ApiOperation(value = "用户修改身份证信息",notes = "用户修改身份证信息")
    public ApiResult<Object> editCardId(@RequestBody UserEditParam param){
        int uid = SecurityUtils.getUserId().intValue();


        YxUserQueryVo yxUserQueryVo = yxUserService.getYxUserById(uid);

        Boolean flag = checkOneService.check(param.getCardId(),param.getRealName(),yxUserQueryVo.getPhone());
        if(!flag) {
            throw new BadRequestException("实名认证失败，请正确填写姓名，手机号，身份证号");
        }
        YxUser yxUser = new YxUser();
        yxUser.setUid(uid);
        if(StrUtil.isNotBlank(param.getCardId())) {
            yxUser.setCardId(param.getCardId());
        }

        if(StrUtil.isNotBlank(param.getRealName())) {
            yxUser.setRealName(param.getRealName());
        }

        if(param.getSex() != null) {
            yxUser.setSex(param.getSex());
        }

        yxUserService.updateById(yxUser);



        return ApiResult.ok("修改成功");
    }


    @PostMapping("/myPharmacist")
    @ApiOperation(value = "我的药师", notes = "我的药师")
    public ApiResult<String> MyPharmacist() {

        Integer uid = SecurityUtils.getUserId().intValue();
        // 获取patientId
        YxUserQueryVo userQueryVo = yxUserService.getYxUserById(uid);

        if(StrUtil.isBlank(userQueryVo.getPhone())){
            return ApiResult.fail("请绑定手机号");
        }

        QueryWrapper queryWrapper = new QueryWrapper<YaoshitongPatient>().eq("uid",uid).select("id");

        List<MdPharmacistService> pharmacistList = new ArrayList<>();
        YaoshitongPatient yaoshitongPatient = yaoshitongPatientService.getOne(queryWrapper,false);
        if(yaoshitongPatient != null) {
            Integer patientId = yaoshitongPatient.getId();
            pharmacistList = pharmacistService.getPharmacistByPatientId(patientId);
        }
        return ApiResult.ok(pharmacistList);

    }


    @PostMapping("/user/myPrescription")
    @ApiOperation(value = "我的处方", notes = "我的处方")
    public ApiResult<String> MyPrescription() {

        Integer uid = SecurityUtils.getUserId().intValue();
        // 获取patientId
        YxUserQueryVo userQueryVo = yxUserService.getYxUserById(uid);

        if(StrUtil.isBlank(userQueryVo.getPhone())){
            return ApiResult.fail("请绑定手机号");
        }

        // 获取订单中的处方照片和时间
        QueryWrapper queryWrapper = new QueryWrapper<YxStoreOrder>().eq("uid",uid).isNotNull("image_path").select("image_path","add_time");
        List<YxStoreOrder> orderList = orderService.list(queryWrapper);
        List<PrescriptionSimpleDTO> dtoList = new ArrayList<>();
        for(YxStoreOrder yxStoreOrder:orderList) {
            String imagePath = yxStoreOrder.getImagePath();
            String infoDate = OrderUtil.stampToDate(yxStoreOrder.getAddTime().toString()) ;
            PrescriptionSimpleDTO prescriptionSimpleDTO = new PrescriptionSimpleDTO();
            prescriptionSimpleDTO.setImagePath(imagePath);
            prescriptionSimpleDTO.setInfoDate(infoDate);
            prescriptionSimpleDTO.setMaker("本人");
            dtoList.add(prescriptionSimpleDTO);
        }


        // 获取药师通处方表中的照片和时间
        QueryWrapper queryWrapper2 = new QueryWrapper<YaoshitongPatient>().eq("phone",userQueryVo.getPhone()).select("id");
        Integer patientId = yaoshitongPatientService.getOne(queryWrapper2).getId();

        QueryWrapper queryWrapper3 = new QueryWrapper<YaoshitongPrescription>().eq("patient_id",patientId).select("create_time","image_path");

        List<YaoshitongPrescription> prescriptionList = yaoshitongPrescriptionService.list(queryWrapper3);
        for(YaoshitongPrescription prescription:prescriptionList) {
            String infoDate = DateUtil.format(prescription.getCreateTime(),"yyyy-MM-dd HH:mm:ss");
            String imagePath = prescription.getImagePath();
            String pharmacistId = prescription.getPharmacistId();
            MdPharmacistService pharmacist = pharmacistService.getById(pharmacistId);
            String maker = "";
            if(pharmacist!=null) {
                pharmacist.getName();
            }else {
                maker = "药师";
            }
            PrescriptionSimpleDTO prescriptionSimpleDTO = new PrescriptionSimpleDTO();
            prescriptionSimpleDTO.setImagePath(imagePath);
            prescriptionSimpleDTO.setInfoDate(infoDate);
            prescriptionSimpleDTO.setMaker(maker);
            dtoList.add(prescriptionSimpleDTO);
        }
        return ApiResult.ok(dtoList);

    }
}

