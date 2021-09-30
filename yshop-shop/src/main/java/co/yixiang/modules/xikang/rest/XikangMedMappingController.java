/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.xikang.rest;
import java.util.Arrays;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.xikang.domain.XikangMedMapping;
import co.yixiang.modules.xikang.service.XikangMedMappingService;
import co.yixiang.modules.xikang.service.dto.XikangMedMappingQueryCriteria;
import co.yixiang.modules.xikang.service.dto.XikangMedMappingDto;
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
* @date 2021-02-02
*/
@AllArgsConstructor
@Api(tags = "熙康医院与益药商城管理")
@RestController
@RequestMapping("/api/xikangMedMapping")
@Slf4j
public class XikangMedMappingController {

    private final XikangMedMappingService xikangMedMappingService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, XikangMedMappingQueryCriteria criteria) throws IOException {
        xikangMedMappingService.download(generator.convert(xikangMedMappingService.queryAll(criteria), XikangMedMappingDto.class), response);
    }

    @GetMapping
    @Log("查询熙康医院与益药商城")
    @ApiOperation("查询熙康医院与益药商城")
    public ResponseEntity<Object> getXikangMedMappings(XikangMedMappingQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(xikangMedMappingService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增熙康医院与益药商城")
    @ApiOperation("新增熙康医院与益药商城")
    public ResponseEntity<Object> create(@Validated @RequestBody XikangMedMapping resources){
        return new ResponseEntity<>(xikangMedMappingService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改熙康医院与益药商城")
    @ApiOperation("修改熙康医院与益药商城")
    public ResponseEntity<Object> update(@Validated @RequestBody XikangMedMapping resources){
        xikangMedMappingService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除熙康医院与益药商城")
    @ApiOperation("删除熙康医院与益药商城")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            xikangMedMappingService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {
        int count = 0;
        log.info("熙康对照批量上载开始====================");
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();
            //if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {

                count = xikangMedMappingService.uploadMapping(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("熙康对照批量上载开始,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }
}
