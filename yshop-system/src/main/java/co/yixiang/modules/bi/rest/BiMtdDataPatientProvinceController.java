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
import co.yixiang.modules.bi.domain.BiMtdDataPatientProvince;
import co.yixiang.modules.bi.service.BiMtdDataPatientProvinceService;
import co.yixiang.modules.bi.service.dto.BiMtdDataPatientProvinceQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataPatientProvinceDto;
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
@Api(tags = "患者分布统计管理")
@RestController
@RequestMapping("/api/biMtdDataPatientProvince")
public class BiMtdDataPatientProvinceController {

    private final BiMtdDataPatientProvinceService biMtdDataPatientProvinceService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdDataPatientProvince:list')")
    public void download(HttpServletResponse response, BiMtdDataPatientProvinceQueryCriteria criteria) throws IOException {
        biMtdDataPatientProvinceService.download(generator.convert(biMtdDataPatientProvinceService.queryAll(criteria), BiMtdDataPatientProvinceDto.class), response);
    }

    @GetMapping
    @Log("查询患者分布统计")
    @ApiOperation("查询患者分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPatientProvince:list')")
    public ResponseEntity<Object> getBiMtdDataPatientProvinces(BiMtdDataPatientProvinceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataPatientProvinceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增患者分布统计")
    @ApiOperation("新增患者分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPatientProvince:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdDataPatientProvince resources){
        return new ResponseEntity<>(biMtdDataPatientProvinceService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改患者分布统计")
    @ApiOperation("修改患者分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPatientProvince:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdDataPatientProvince resources){
        biMtdDataPatientProvinceService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除患者分布统计")
    @ApiOperation("删除患者分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPatientProvince:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataPatientProvinceService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
