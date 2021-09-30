/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.rest;
import java.util.Arrays;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.RocheHospital;
import co.yixiang.modules.shop.service.RocheHospitalService;
import co.yixiang.modules.shop.service.dto.RocheHospitalQueryCriteria;
import co.yixiang.modules.shop.service.dto.RocheHospitalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-02-05
*/
@AllArgsConstructor
@Api(tags = "罗氏罕见病sma医院列表管理")
@RestController
@RequestMapping("/api/rocheHospital")
@Slf4j
public class RocheHospitalController {

    private final RocheHospitalService rocheHospitalService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','rocheHospital:list')")
    public void download(HttpServletResponse response, RocheHospitalQueryCriteria criteria) throws IOException {
        rocheHospitalService.download(generator.convert(rocheHospitalService.queryAll(criteria), RocheHospitalDto.class), response);
    }

    @GetMapping
    @Log("查询罗氏罕见病sma医院列表")
    @ApiOperation("查询罗氏罕见病sma医院列表")
    @PreAuthorize("@el.check('admin','rocheHospital:list')")
    public ResponseEntity<Object> getRocheHospitals(RocheHospitalQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(rocheHospitalService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增罗氏罕见病sma医院列表")
    @ApiOperation("新增罗氏罕见病sma医院列表")
    @PreAuthorize("@el.check('admin','rocheHospital:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody RocheHospital resources){
        return new ResponseEntity<>(rocheHospitalService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改罗氏罕见病sma医院列表")
    @ApiOperation("修改罗氏罕见病sma医院列表")
    @PreAuthorize("@el.check('admin','rocheHospital:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody RocheHospital resources){
        rocheHospitalService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除罗氏罕见病sma医院列表")
    @ApiOperation("删除罗氏罕见病sma医院列表")
    @PreAuthorize("@el.check('admin','rocheHospital:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            rocheHospitalService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {
        int count = 0;
        log.info("罗氏罕见病sma医院列表====================");
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();
            //if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {

            count = rocheHospitalService.upload(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("罗氏罕见病sma医院列表,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }
}
