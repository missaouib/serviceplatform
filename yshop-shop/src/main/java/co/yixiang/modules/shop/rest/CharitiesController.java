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
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.Charities;
import co.yixiang.modules.shop.service.CharitiesService;
import co.yixiang.modules.shop.service.dto.CharitiesQueryCriteria;
import co.yixiang.modules.shop.service.dto.CharitiesDto;
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
* @date 2020-08-20
*/
@AllArgsConstructor
@Api(tags = "慈善活动管理")
@RestController
@RequestMapping("/api/charities")
@Slf4j
public class CharitiesController {

    private final CharitiesService charitiesService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','charities:list')")
    public void download(HttpServletResponse response, CharitiesQueryCriteria criteria) throws IOException {
        charitiesService.download(generator.convert(charitiesService.queryAll(criteria), CharitiesDto.class), response);
    }

    @GetMapping
    @Log("查询慈善活动")
    @ApiOperation("查询慈善活动")
    @PreAuthorize("@el.check('admin','charities:list')")
    public ResponseEntity<Object> getCharitiess(CharitiesQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(charitiesService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增慈善活动")
    @ApiOperation("新增慈善活动")
    @PreAuthorize("@el.check('admin','charities:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Charities resources){
        return new ResponseEntity<>(charitiesService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改慈善活动")
    @ApiOperation("修改慈善活动")
    @PreAuthorize("@el.check('admin','charities:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Charities resources){
        charitiesService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除慈善活动")
    @ApiOperation("删除慈善活动")
    @PreAuthorize("@el.check('admin','charities:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            charitiesService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {

        log.info("慈善活动批量上载====================");
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

            charitiesService.uploadCharities(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
