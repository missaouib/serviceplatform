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
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.bi.domain.BiMtdData;
import co.yixiang.modules.bi.service.BiMtdDataService;
import co.yixiang.modules.bi.service.dto.BiMtdDataQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataDto;
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
* @date 2020-10-14
*/
@AllArgsConstructor
@Api(tags = "MTD核心数据管理")
@RestController
@RequestMapping("/api/biMtdData")
public class BiMtdDataController {

    private final BiMtdDataService biMtdDataService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdData:list')")
    public void download(HttpServletResponse response, BiMtdDataQueryCriteria criteria) throws IOException {
        biMtdDataService.download(generator.convert(biMtdDataService.queryAll(criteria), BiMtdDataDto.class), response);
    }

    @GetMapping
    @Log("查询MTD核心数据")
    @ApiOperation("查询MTD核心数据")
    @PreAuthorize("@el.check('admin','biMtdData:list')")
    public ResponseEntity<Object> getBiMtdDatas(BiMtdDataQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增MTD核心数据")
    @ApiOperation("新增MTD核心数据")
    @PreAuthorize("@el.check('admin','biMtdData:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdData resources){
        return new ResponseEntity<>(biMtdDataService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改MTD核心数据")
    @ApiOperation("修改MTD核心数据")
    @PreAuthorize("@el.check('admin','biMtdData:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdData resources){
        biMtdDataService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除MTD核心数据")
    @ApiOperation("删除MTD核心数据")
    @PreAuthorize("@el.check('admin','biMtdData:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
