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
import co.yixiang.modules.bi.domain.BiMtdDataDrugstore;
import co.yixiang.modules.bi.service.BiMtdDataDrugstoreService;
import co.yixiang.modules.bi.service.dto.BiMtdDataDrugstoreQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataDrugstoreDto;
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
* @date 2020-10-12
*/
@AllArgsConstructor
@Api(tags = "药店数据-mtd管理")
@RestController
@RequestMapping("/api/biMtdDataDrugstore")
public class BiMtdDataDrugstoreController {

    private final BiMtdDataDrugstoreService biMtdDataDrugstoreService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdDataDrugstore:list')")
    public void download(HttpServletResponse response, BiMtdDataDrugstoreQueryCriteria criteria) throws IOException {
        biMtdDataDrugstoreService.download(generator.convert(biMtdDataDrugstoreService.queryAll(criteria), BiMtdDataDrugstoreDto.class), response);
    }

    @GetMapping
    @Log("查询药店数据-mtd")
    @ApiOperation("查询药店数据-mtd")
    @PreAuthorize("@el.check('admin','biMtdDataDrugstore:list')")
    public ResponseEntity<Object> getBiMtdDataDrugstores(BiMtdDataDrugstoreQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataDrugstoreService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药店数据-mtd")
    @ApiOperation("新增药店数据-mtd")
    @PreAuthorize("@el.check('admin','biMtdDataDrugstore:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdDataDrugstore resources){
        return new ResponseEntity<>(biMtdDataDrugstoreService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药店数据-mtd")
    @ApiOperation("修改药店数据-mtd")
    @PreAuthorize("@el.check('admin','biMtdDataDrugstore:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdDataDrugstore resources){
        biMtdDataDrugstoreService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药店数据-mtd")
    @ApiOperation("删除药店数据-mtd")
    @PreAuthorize("@el.check('admin','biMtdDataDrugstore:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataDrugstoreService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
