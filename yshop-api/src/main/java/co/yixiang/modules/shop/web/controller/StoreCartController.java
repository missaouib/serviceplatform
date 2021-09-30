/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.web.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxStoreCartService;
import co.yixiang.modules.shop.web.param.CartIdsParm;
import co.yixiang.modules.shop.web.param.ProjectCodeQueryParam;
import co.yixiang.modules.shop.web.param.YxStoreCartQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.StoreCartVo;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-25
 */
@Slf4j
@RestController
@Api(value = "购物车", tags = "商城:购物车", description = "购物车")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreCartController extends BaseController {

    private final YxStoreCartService storeCartService;
    private final YxStoreOrderService storeOrderService;

    /**
     * 购物车 获取数量
     */
    @GetMapping("/cart/count")
    @ApiOperation(value = "获取数量",notes = "获取数量")
    @AnonymousAccess
    public ApiResult<Map<String,Object>> count(YxStoreCartQueryParam queryParam){
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = 0 ;
        try{
             uid = SecurityUtils.getUserId().intValue();
        }catch (Exception e) {

        }

        List<String> projectCodes = new ArrayList<>();
        projectCodes.add(queryParam.getProjectCode());
        if(StrUtil.isBlank(queryParam.getProjectCode())) {
            projectCodes.add(ProjectNameEnum.HEALTHCARE.getValue());
            projectCodes.add(ProjectNameEnum.ROCHE_SMA.getValue());
        }
        map.put("count",storeCartService.getUserCartNum(uid,"product",queryParam.getNumType().intValue(),projectCodes));
        return ApiResult.ok(map);
    }

    /**
     * 购物车 添加
     */
    @Log(value = "添加购物车",type = 1)
    @PostMapping("/cart/add")
    @ApiOperation(value = "添加购物车",notes = "添加购物车")
    public ApiResult<Map<String,Object>> add(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(ObjectUtil.isNull(jsonObject.get("cartNum")) || ObjectUtil.isNull(jsonObject.get("productId"))){
            return ApiResult.fail("参数有误");
        }
        Integer cartNum = jsonObject.getInteger("cartNum");
        if(ObjectUtil.isNull(cartNum)){
            return ApiResult.fail("购物车数量参数有误");
        }
        if(cartNum <= 0){
            return ApiResult.fail("购物车数量必须大于0");
        }

        // 是否为立即购买
        int isNew = 1;
        if(ObjectUtil.isNotNull(jsonObject.get("new"))){
            isNew = jsonObject.getInteger("new");
        }
        Integer productId = jsonObject.getInteger("productId");
        if(ObjectUtil.isNull(productId)){
            return ApiResult.fail("产品参数有误");
        }
        String uniqueId = jsonObject.get("uniqueId").toString();

        //拼团
        int combinationId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("combinationId"))){
            combinationId = jsonObject.getInteger("combinationId");
        }

        //秒杀
        int seckillId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("secKillId"))){
            seckillId = jsonObject.getInteger("secKillId");
        }

        //秒杀
        int bargainId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("bargainId"))){
            bargainId = jsonObject.getInteger("bargainId");
        }
        // 项目编码
        String projectCode = "";
        if(ObjectUtil.isNotNull(jsonObject.get("projectCode"))){
            projectCode = jsonObject.getString("projectCode");
        }

        String departmentCode = "";
        String partnerCode = "";
        String refereeCode = "";


        map.put("cartId",storeCartService.addCart(uid,productId,cartNum,uniqueId
                ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode));
        return ApiResult.ok(map);
    }


    /**
     * 购物车 添加
     */
    @Log(value = "添加购物车",type = 1)
    @PostMapping("/cart/add4store")
    @ApiOperation(value = "添加购物车-多门店",notes = "添加购物车-多门店")
    public ApiResult<Map<String,Object>> add4store(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Map<String,Object> map = new LinkedHashMap<>();
        int uid = SecurityUtils.getUserId().intValue();
        if(ObjectUtil.isNull(jsonObject.get("cartNum")) || ObjectUtil.isNull(jsonObject.get("productId"))){
            return ApiResult.fail("参数有误");
        }
        Integer cartNum = jsonObject.getInteger("cartNum");
        if(ObjectUtil.isNull(cartNum)){
            return ApiResult.fail("购物车数量参数有误");
        }
        if(cartNum <= 0){
            return ApiResult.fail("购物车数量必须大于0");
        }
        int isNew = 1;
        if(ObjectUtil.isNotNull(jsonObject.get("new"))){
            isNew = jsonObject.getInteger("new");
        }
        Integer productId = jsonObject.getInteger("productId");
        if(ObjectUtil.isNull(productId)){
            return ApiResult.fail("产品参数有误");
        }

        /*Integer storeId = jsonObject.getInteger("storeId");
        if(ObjectUtil.isNull(productId)){
            return ApiResult.fail("药店参数有误");
        }*/

        String uniqueId = jsonObject.get("uniqueId").toString();

        //拼团
        int combinationId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("combinationId"))){
            combinationId = jsonObject.getInteger("combinationId");
        }

        //秒杀
        int seckillId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("secKillId"))){
            seckillId = jsonObject.getInteger("secKillId");
        }

        //秒杀
        int bargainId = 0;
        if(ObjectUtil.isNotNull(jsonObject.get("bargainId"))){
            bargainId = jsonObject.getInteger("bargainId");
        }

        String departmentCode = jsonObject.getString("departmentCode");
        String partnerCode = jsonObject.getString("partnerCode");
        String refereeCode = jsonObject.getString("refereeCode");
        String projectCode = jsonObject.getString("projectCode");
        if(departmentCode == null) {
            departmentCode = "";
        }

        if(partnerCode == null) {
            partnerCode = "";
        }

        if(refereeCode == null) {
            refereeCode = "";
        }

        if(projectCode == null) {
            projectCode = "";
        }
        if(jsonObject.get("orderNumber")!=null && StringUtils.isNotEmpty(jsonObject.get("orderNumber").toString()) && ProjectNameEnum.LINGYUANZHI.getValue().equals(projectCode)){
            YxStoreOrder yxStoreOrder= storeOrderService.findByUidAndOriginalOrderNo(uid,jsonObject.get("orderNumber").toString());
            if(yxStoreOrder!=null){
                return ApiResult.fail("不能重复领取。");
            }
        }

        map.put("cartId",storeCartService.addCart(uid,productId,cartNum,uniqueId
                ,"product",isNew,combinationId,seckillId,bargainId,departmentCode,partnerCode,refereeCode,projectCode));
        return ApiResult.ok(map);
    }


    /**
     * 购物车列表
     */
    @Log(value = "查看购物车",type = 1)
    @GetMapping("/cart/list")
    @ApiOperation(value = "购物车列表",notes = "购物车列表")
    public ApiResult<Map<String,Object>> getList(){
        int uid = SecurityUtils.getUserId().intValue();
        String projectCode = "";
        return ApiResult.ok(storeCartService.getUserProductCartList(uid,"",0,projectCode));
    }

    /**
     * 购物车列表
     */
    @Log(value = "查看购物车",type = 1)
    @GetMapping("/cart/list4Store")
    @ApiOperation(value = "查看购物车列表-多门店",notes = "查看购物车列表-多门店")
    public ApiResult<List<StoreCartVo>> getList4Store(  ProjectCodeQueryParam projectCodeQueryParam){
        int uid = SecurityUtils.getUserId().intValue();
        String projectCode = projectCodeQueryParam.getProjectCode();
        String cardNumber = projectCodeQueryParam.getCardNumber();
        String cardType =  projectCodeQueryParam.getCardType();
        return ApiResult.ok(storeCartService.getUserProductCartList4Store(uid,"",0,projectCode,cardNumber,cardType,null));
    }

    /**
     * 修改产品数量
     */
    @PostMapping("/cart/num")
    @ApiOperation(value = "修改产品数量",notes = "修改产品数量")
    public ApiResult<Object> cartNum(@RequestBody String jsonStr){
        int uid = SecurityUtils.getUserId().intValue();
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(ObjectUtil.isNull(jsonObject.get("id")) || ObjectUtil.isNull(jsonObject.get("number"))){
            ApiResult.fail("参数错误");
        }
        storeCartService.changeUserCartNum(jsonObject.getInteger("id"),
                jsonObject.getInteger("number"),uid);
        return ApiResult.ok("ok");
    }

    /**
     * 修改产品数量
     */
    @PostMapping("/cart/num4Store")
    @ApiOperation(value = "修改产品数量-多门店",notes = "修改产品数量-多门店")
    public ApiResult<Object> cartNum4Store(@RequestBody String jsonStr){
        int uid = SecurityUtils.getUserId().intValue();
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(ObjectUtil.isNull(jsonObject.get("id")) || ObjectUtil.isNull(jsonObject.get("number"))){
            ApiResult.fail("参数错误");
        }
        storeCartService.changeUserCartNum4Store(jsonObject.getInteger("id"),
                jsonObject.getInteger("number"),uid);
        return ApiResult.ok("ok");
    }

    /**
     * 购物车删除产品
     */
    @PostMapping("/cart/del")
    @ApiOperation(value = "购物车删除产品",notes = "购物车删除产品")
    public ApiResult<Object> cartDel(@Validated @RequestBody CartIdsParm parm){
        int uid = SecurityUtils.getUserId().intValue();
        storeCartService.removeUserCart(uid, parm.getIds());

        return ApiResult.ok("ok");
    }




}

