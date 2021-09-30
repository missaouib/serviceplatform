/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;

import co.yixiang.modules.taibao.service.enume.TaiBaoEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Api(tags = "太保安联-枚举")
@RestController
@RequestMapping("/api/enum")
public class TbEnumerationController {


    @ApiOperation(value = "获取外包商系统证件类型列表")
    @RequestMapping(value = "getCommunityArticleStatus", method = RequestMethod.GET)
    public ResponseEntity<Object> getOutsourcerSystemIdType(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.OutsourcerSystemIdType enumObject : TaiBaoEnum.OutsourcerSystemIdType.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }

    @ApiOperation(value = "获取报案人与被保人关系代码列表")
    @RequestMapping(value = "getInformantInsuredRelationshipCode", method = RequestMethod.GET)
    public ResponseEntity<Object> getInformantInsuredRelationshipCode(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.InformantInsuredRelationshipCode enumObject : TaiBaoEnum.InformantInsuredRelationshipCode.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }

    @ApiOperation(value = "获取索赔事故性质列表")
    @RequestMapping(value = "getNatureClaimAccident", method = RequestMethod.GET)
    public ResponseEntity<Object> getNatureClaimAccident(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.NatureClaimAccident enumObject : TaiBaoEnum.NatureClaimAccident.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }


    @ApiOperation(value = "获取赔付结论列表")
    @RequestMapping(value = "getCompensationConclusion", method = RequestMethod.GET)
    public ResponseEntity<Object> getCompensationConclusion(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.CompensationConclusion enumObject : TaiBaoEnum.CompensationConclusion.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }


    @ApiOperation(value = "获取出险地区列表")
    @RequestMapping(value = "getRiskArea", method = RequestMethod.GET)
    public ResponseEntity<Object> getRiskArea(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.RiskArea enumObject : TaiBaoEnum.RiskArea.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }

    @ApiOperation(value = "获取挂起类型列表")
    @RequestMapping(value = "getHangUpSign", method = RequestMethod.GET)
    public ResponseEntity<Object> getHangUpSign(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.HangUpSign enumObject : TaiBaoEnum.HangUpSign.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }

    @ApiOperation(value = "获取账单项目列表")
    @RequestMapping(value = "getBillingCode", method = RequestMethod.GET)
    public ResponseEntity<Object> getBillingCode(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.BillingCode enumObject : TaiBaoEnum.BillingCode.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }


    @ApiOperation(value = "获取领款人证件类型列表")
    @RequestMapping(value = "geBenefitPersonIdtype", method = RequestMethod.GET)
    public ResponseEntity<Object> geBenefitPersonIdtype(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (TaiBaoEnum.BenefitPersonIdType enumObject : TaiBaoEnum.BenefitPersonIdType.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }
}
