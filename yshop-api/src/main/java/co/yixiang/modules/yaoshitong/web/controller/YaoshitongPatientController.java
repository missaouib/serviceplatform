/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.web.controller;
import java.util.Arrays;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.api.ApiResult;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.modules.yaoshitong.entity.YaoshitongUserLable;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.YaoshitongUserLableRelationService;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPatientDto;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientQueryCriteria;

import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;

import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-07-13
*/
@AllArgsConstructor
@Api(tags = "药师通-患者主数据管理")
@RestController
@RequestMapping("/yaoshitongPatient")
public class YaoshitongPatientController {

    private final YaoshitongPatientService yaoshitongPatientService;
    private final IGenerator generator;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    @Autowired
    private YxUserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private YaoshitongUserLableRelationService userLableRelationService;
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, YaoshitongPatientQueryCriteria criteria) throws IOException {
        yaoshitongPatientService.download(generator.convert(yaoshitongPatientService.queryAll(criteria), YaoshitongPatientDto.class), response);
    }

    @GetMapping
    @ApiOperation("查询药师通-患者主数据")
    public ApiResult<Object> getYaoshitongPatients(YaoshitongPatientQueryCriteria criteria, Pageable pageable){
        if( StrUtil.isBlank(criteria.getPharmacistId() )) {

            Integer uid = SecurityUtils.getUserId().intValue();
            MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);
            if(pharmacist != null) {
                criteria.setPharmacistId(pharmacist.getId());
            }
        }
        return ApiResult.ok(yaoshitongPatientService.queryAll(criteria,pageable));
    }


    @PostMapping(value = "/list")
    @ApiOperation("查询药师通-患者主数据")
    public ApiResult<Object> getYaoshitongPatients4post(@RequestBody YaoshitongPatientQueryCriteria criteria, Pageable pageable){
        if( StrUtil.isBlank(criteria.getPharmacistId() )) {

            Integer uid = SecurityUtils.getUserId().intValue();
            MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);
            if(pharmacist != null) {
                criteria.setPharmacistId(pharmacist.getId());
            }
        }
        return ApiResult.ok(yaoshitongPatientService.queryAll(criteria,pageable));
    }


    @GetMapping("/detail/{key}")
    @ApiOperation("查询药师通-患者主数据详情")
    public ApiResult<Object> getYaoshitongPatientDetail(@PathVariable Integer key){
        YaoshitongPatient patient = yaoshitongPatientService.getById(key);

        // 更新年龄
        if(patient!=null && StrUtil.isNotBlank(patient.getBirth())) {
            patient.setAge(DateUtil.ageOfNow(patient.getBirth() + "01"));
        }
        Integer uid = SecurityUtils.getUserId().intValue();
        MdPharmacistService pharmacist = pharmacistService.getMdPharmacistByUid(uid);

        // 查找患者标签
        List<YaoshitongUserLable> lableList = userLableRelationService.getUserLableRelationByUid(pharmacist.getId(),patient.getId());

        patient.setLableList(lableList);

        return ApiResult.ok(patient);
    }

    @GetMapping("/detailByPhone/{key}")
    @ApiOperation("查询药师通-患者主数据详情")
    public ApiResult<Object> getYaoshitongPatientDetailByPhone(@PathVariable String key){
        YaoshitongPatient patient = yaoshitongPatientService.getOne(new QueryWrapper<YaoshitongPatient>().eq("phone",key));
        // 更新年龄
        if( patient!=null && StrUtil.isNotBlank(patient.getBirth())) {
            patient.setAge(DateUtil.ageOfNow(patient.getBirth() + "01"));
        }
        return ApiResult.ok(patient);
    }

    @PostMapping
    @ApiOperation("新增药师通-患者主数据-药师端")
    public ApiResult<Object> create(@Validated @RequestBody YaoshitongPatient resources){

        Integer uid = SecurityUtils.getUserId().intValue();

        return ApiResult.ok(yaoshitongPatientService.savePatient(resources,uid));
    }


    @PostMapping("/createByPatient")
    @ApiOperation("新增药师通-患者主数据-患者端")
    public ApiResult<Object> createByPatient(@Validated @RequestBody YaoshitongPatient resources){

        // 有手机验证码，则先绑定手机号
        if(StrUtil.isNotBlank(resources.getCaptcha())) {
            Object codeObj = redisUtils.get("code_" + resources.getPhone());
            if(codeObj == null){
                return ApiResult.fail("请先获取验证码");
            }
            String code = codeObj.toString();


            if (!StrUtil.equals(code, resources.getCaptcha())) {
                return ApiResult.fail("验证码错误");
            }

            int uid = SecurityUtils.getUserId().intValue();
            YxUserQueryVo userQueryVo = userService.getYxUserById(uid);
            if(StrUtil.isNotBlank(userQueryVo.getPhone())){
                return ApiResult.fail("您的账号已经绑定过手机号码");
            }

            YxUser yxUser = new YxUser();
            yxUser.setPhone(resources.getPhone());
            yxUser.setUid(uid);
            yxUser.setRealName(resources.getName());
            userService.updateById(yxUser);
        }


        Integer uid = SecurityUtils.getUserId().intValue();
        resources.setUid(uid);
        return ApiResult.ok(yaoshitongPatientService.savePatientByPatient(resources));
    }

    @PutMapping
    @ApiOperation("修改药师通-患者主数据")
    public ApiResult<Object> update(@Validated @RequestBody YaoshitongPatient resources){
       // yaoshitongPatientService.savePatient(resources);
        Integer uid = SecurityUtils.getUserId().intValue();
        if(StrUtil.isBlank(resources.getPharmacistId() )) {
            MdPharmacistService pharmacist = pharmacistService.getById(resources.getPharmacistId());
            if(pharmacist == null) {
                ApiResult.fail("药师信息找不到");
            }
        }
        return ApiResult.ok(yaoshitongPatientService.savePatient(resources,uid));
    }


    @ApiOperation("删除药师通-患者主数据")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongPatientService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 用户关联药师
     */
    @PostMapping("/relation")
    @ApiOperation(value = "患者关联药师",notes = "患者关联药师")
    public ApiResult<Object> saveRelationUser(@RequestBody YaoshitongPatient resources){
        yaoshitongPatientService.savePatientByChat(resources);
        return ApiResult.ok();
    }
}
