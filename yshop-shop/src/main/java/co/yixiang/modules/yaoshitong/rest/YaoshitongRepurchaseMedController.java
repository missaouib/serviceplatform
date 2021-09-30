/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.rest;
import java.util.Arrays;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseMed;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseMedService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseMedQueryCriteria;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseMedDto;
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
* @date 2020-10-21
*/
@AllArgsConstructor
@Api(tags = "复购药品数据管理")
@RestController
@RequestMapping("/api/yaoshitongRepurchaseMed")
public class YaoshitongRepurchaseMedController {

    private final YaoshitongRepurchaseMedService yaoshitongRepurchaseMedService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseMed:list')")
    public void download(HttpServletResponse response, YaoshitongRepurchaseMedQueryCriteria criteria) throws IOException {
        yaoshitongRepurchaseMedService.download(generator.convert(yaoshitongRepurchaseMedService.queryAll(criteria), YaoshitongRepurchaseMedDto.class), response);
    }

    @GetMapping
    @Log("查询复购药品数据")
    @ApiOperation("查询复购药品数据")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseMed:list')")
    public ResponseEntity<Object> getYaoshitongRepurchaseMeds(YaoshitongRepurchaseMedQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaoshitongRepurchaseMedService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增复购药品数据")
    @ApiOperation("新增复购药品数据")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseMed:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaoshitongRepurchaseMed resources){
        return new ResponseEntity<>(yaoshitongRepurchaseMedService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改复购药品数据")
    @ApiOperation("修改复购药品数据")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseMed:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaoshitongRepurchaseMed resources){
        yaoshitongRepurchaseMedService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除复购药品数据")
    @ApiOperation("删除复购药品数据")
    @PreAuthorize("@el.check('admin','yaoshitongRepurchaseMed:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongRepurchaseMedService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {
        int count = 0;

        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

            count = yaoshitongRepurchaseMedService.uploadProduct(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
