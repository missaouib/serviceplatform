/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.util.*;

import cn.hutool.core.util.StrUtil;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.msh.service.enume.MshStatusEnum;
import co.yixiang.modules.msh.util.MshRequestUtil;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.LocalStorageService;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.tools.service.dto.LocalStorageDto;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshDemandList;
import co.yixiang.modules.msh.service.MshDemandListService;
import co.yixiang.modules.msh.service.dto.MshDemandListQueryCriteria;
import co.yixiang.modules.msh.service.dto.ServiceResult;
import co.yixiang.modules.msh.service.dto.MshDemandListDto;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author cq
* @date 2020-12-25
*/
@Api(tags = "需求单主表管理")
@RestController
@RequestMapping("/api/mshDemandList")
public class MshDemandListController {

    @Autowired
    private MshDemandListService mshDemandListService;
    @Autowired
    private IGenerator generator;

    @Value("${file.localUrl}")
    private String localUrl;

    @Value("${msh.domainName}")
    private String domainName;

    @Value("${msh.secureKey}")
    private String secureKey;

    @Value("${msh.isUpload}")
    private String isUpload;

    @Autowired
    private LocalStorageService localStorageService;

    @Autowired
    private QiNiuService qiNiuService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/list/download")
    public void download(HttpServletResponse response, MshDemandListQueryCriteria criteria) throws IOException {
        mshDemandListService.download(generator.convert(mshDemandListService.queryAll(criteria), MshDemandListDto.class), response);
    }

    @Log("报表导出数据")
    @ApiOperation("报表导出数据")
    @GetMapping(value = "/list/reportDowload")
    public void reportDowload(HttpServletResponse response, MshDemandListQueryCriteria criteria) throws IOException {
        mshDemandListService.reportDowload(response,criteria);
    }

    @Log("查询需求单主表")
    @ApiOperation("查询需求单主表")
    @GetMapping(value = "/list")
    public ResponseEntity<Object> getMshDemandLists(MshDemandListQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshDemandListService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增需求单主表")
    @ApiOperation("新增需求单主表")
    public ResponseEntity<Object> create(@Validated @RequestBody MshDemandList resources){
        return new ResponseEntity<>(mshDemandListService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改需求单主表")
    @ApiOperation("修改需求单主表")
    public ResponseEntity<Object> update(@Validated @RequestBody MshDemandList resources){
        mshDemandListService.updateMshDemandList(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Log("修改需求单主表")
    @ApiOperation("修改需求单主表")
    @PostMapping(value = "/update")
    public ResponseEntity<Object> updateMshDemandList(@Validated @RequestBody MshDemandList resources){
        mshDemandListService.updateMshDemandList(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除需求单主表")
    @ApiOperation("删除需求单主表")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            mshDemandListService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("新增需求单相关（提交）")
    @ApiOperation("新增需求单相关（提交）")
    @PostMapping(value = "/addList")
    public ResponseEntity<Object> createmshDemandList(@Validated @RequestBody JSONObject jsonObject){
        try {
            ServiceResult<Boolean> flag = mshDemandListService.createmshDemandList(jsonObject);
            if(!flag.isOk()){
                Map<String, Object> map = new LinkedHashMap<>(2);
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", flag.getMsg());
                return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "插入数据有误！");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("新增需求单相关（保存）")
    @ApiOperation("新增需求单相关（保存）")
    @PostMapping(value = "/addListForSave")
    public ResponseEntity<Object> createmshDemandListForSave(@Validated @RequestBody JSONObject jsonObject){
        try {
        	ServiceResult<Boolean> flag = mshDemandListService.createmshDemandListForSave(jsonObject);
        	if(!flag.isOk()){
        		Map<String, Object> map = new LinkedHashMap<>(2);
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", flag.getMsg());
        		return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        	}
        }catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "插入数据有误！");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除需求单相关")
    @ApiOperation("删除需求单相关")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<Object> deleteById(@RequestBody Integer[] ids) {
    	//校验需求单是否满足删除条件
    	ServiceResult<Integer> checkFlag = mshDemandListService.checkDeleteById(ids);
    	if(!checkFlag.isOk()){
    		Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", checkFlag.getMsg());
    		return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
    	}

    	//删除需求单
    	ServiceResult<Boolean> deleteFlag = mshDemandListService.deleteById(ids);
    	if(!deleteFlag.isOk()){
    		Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", deleteFlag.getMsg());
    		return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
    	}

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询需求单详细")
    @ApiOperation("查询需求单详细")
    @PostMapping(value = "/getMshDemandListdDetails")
    public ResponseEntity<Object> getMshDemandListdDetails(@RequestBody Integer id){
        return new ResponseEntity<>(mshDemandListService.getMshDemandListdDetails(id),HttpStatus.OK);
    }

    @Log("查询需求单审核信息")
    @ApiOperation("查询需求单审核信息")
    @PostMapping(value = "/getMshDemandListAuditInfo")
    public ResponseEntity<Object> getMshDemandListAuditInfo(@RequestBody Integer id){
        return new ResponseEntity<>(mshDemandListService.getMshDemandListAuditInfo(id),HttpStatus.OK);
    }

    @Log("查询需求单所有审核人")
    @ApiOperation("查询需求单所有审核人")
    @GetMapping(value = "/getMshDemandAllAuditPerson")
    public ResponseEntity<Object> getMshDemandAllAuditPerson(){
        return new ResponseEntity<>(mshDemandListService.getMshDemandAllAuditPerson(),HttpStatus.OK);
    }

    @Log("查询需求单所有vip值")
    @ApiOperation("查询需求单所有vip值")
    @GetMapping(value = "/getMshDemandAllVip")
    public ResponseEntity<Object> getMshDemandAllVip(){
        return new ResponseEntity<>(mshDemandListService.getMshDemandAllVip(),HttpStatus.OK);
    }

    @Log("上传文件")
    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile[] files) {
        StringBuilder url = new StringBuilder();
        if (isUpload.equals("false")) { //存在走本地
            for (MultipartFile file : files) {
                LocalStorageDto localStorageDTO = localStorageService.create(name, file);
                if ("".equals(url.toString())) {
                    url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                } else {
                    url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                }
            }
        } else {
            try {
                JSONObject jsonObject=  JSONObject.parseObject(MshRequestUtil.upload(domainName,secureKey, files));
                if(jsonObject!=null && jsonObject.get("code").equals("200")){
                    JSONArray jsonArray=JSONArray.parseArray(jsonObject.get("result").toString());
                    jsonObject=JSONObject.parseObject(jsonArray.get(0).toString());
                    url=url.append(jsonObject.get("riderPath").toString());
                }else{
                    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("link", url);
        return new ResponseEntity(map, HttpStatus.CREATED);
    }

    @ApiOperation(value = "获取不通过原因")
    @RequestMapping(value = "/getCancelReason", method = RequestMethod.GET)
    public ResponseEntity<Object> geBenefitPersonIdtype(){
        List<Map<String, Object>> list = Lists.newArrayList();
        for (MshStatusEnum.CancelReason enumObject : MshStatusEnum.CancelReason.values()){
            Map<String, Object> map = Maps.newHashMap();
            map.put("value", enumObject.getCode());
            map.put("name", enumObject.getValue());
            map.put("type", enumObject.getType());
            list.add(map);
        }
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }

}
