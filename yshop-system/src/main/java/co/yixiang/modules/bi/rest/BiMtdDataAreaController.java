/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.rest;
import java.util.Arrays;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.bi.domain.BiMtdDataArea;
import co.yixiang.modules.bi.service.BiMtdDataAreaService;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-09-28
*/
@AllArgsConstructor
@Api(tags = "MTD-大区数据管理")
@RestController
@RequestMapping("/api/biMtdDataArea")
public class BiMtdDataAreaController {

    private final BiMtdDataAreaService biMtdDataAreaService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdDataArea:list')")
    public void download(HttpServletResponse response, BiMtdDataAreaQueryCriteria criteria) throws IOException {
        biMtdDataAreaService.download(generator.convert(biMtdDataAreaService.queryAll(criteria), BiMtdDataAreaDto.class), response);
    }

    @GetMapping
    @Log("查询MTD-大区数据")
    @ApiOperation("查询MTD-大区数据")
    @AnonymousAccess
    public ResponseEntity<Object> getBiMtdDataAreas(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataAreaService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增MTD-大区数据")
    @ApiOperation("新增MTD-大区数据")
    @PreAuthorize("@el.check('admin','biMtdDataArea:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdDataArea resources){
        return new ResponseEntity<>(biMtdDataAreaService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改MTD-大区数据")
    @ApiOperation("修改MTD-大区数据")
    @PreAuthorize("@el.check('admin','biMtdDataArea:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdDataArea resources){
        biMtdDataAreaService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除MTD-大区数据")
    @ApiOperation("删除MTD-大区数据")
    @PreAuthorize("@el.check('admin','biMtdDataArea:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataAreaService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
