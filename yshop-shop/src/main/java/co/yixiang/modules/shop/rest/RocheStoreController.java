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

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.RocheStore;
import co.yixiang.modules.shop.service.RocheStoreService;
import co.yixiang.modules.shop.service.dto.RocheStoreQueryCriteria;
import co.yixiang.modules.shop.service.dto.RocheStoreDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
* @author visazhou
* @date 2020-12-28
*/
@AllArgsConstructor
@Api(tags = "roche项目专项药房数据管理")
@RestController
@RequestMapping("/api/rocheStore")
@Slf4j
public class RocheStoreController {

    private final RocheStoreService rocheStoreService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, RocheStoreQueryCriteria criteria) throws IOException {
        rocheStoreService.download(generator.convert(rocheStoreService.queryAll(criteria), RocheStoreDto.class), response);
    }

    @GetMapping
    @Log("查询roche项目专项药房数据")
    @ApiOperation("查询roche项目专项药房数据")
    public ResponseEntity<Object> getRocheStores(RocheStoreQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(rocheStoreService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增roche项目专项药房数据")
    @ApiOperation("新增roche项目专项药房数据")
    public ResponseEntity<Object> create(@Validated @RequestBody RocheStore resources){
        return new ResponseEntity<>(rocheStoreService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改roche项目专项药房数据")
    @ApiOperation("修改roche项目专项药房数据")
    public ResponseEntity<Object> update(@Validated @RequestBody RocheStore resources){
        rocheStoreService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除roche项目专项药房数据")
    @ApiOperation("删除roche项目专项药房数据")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            rocheStoreService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String type, @RequestParam("file") MultipartFile file) {
        if(StringUtils.isEmpty(type)){
            throw new BadRequestException("请选择药房类型");
        }
        int count = 0;
        log.info("罗氏药店批量上载开始====================");
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("罗氏药店批量上载结束,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("导出模板")
    @ApiOperation("导出模板")
    @GetMapping(value = "/downloadModel")
    public void downloadModel(@RequestParam(defaultValue = "") String type,HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(type)){
            throw new BadRequestException("请选择药房类型");
        }
        rocheStoreService.downloadModel(type,response);
    }
}
