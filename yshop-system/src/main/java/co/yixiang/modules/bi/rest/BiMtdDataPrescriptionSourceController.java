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
import co.yixiang.modules.bi.domain.BiMtdDataPrescriptionSource;
import co.yixiang.modules.bi.service.BiMtdDataPrescriptionSourceService;
import co.yixiang.modules.bi.service.dto.BiMtdDataPrescriptionSourceQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataPrescriptionSourceDto;
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
* @date 2020-10-13
*/
@AllArgsConstructor
@Api(tags = "处方来源分布统计管理")
@RestController
@RequestMapping("/api/biMtdDataPrescriptionSource")
public class BiMtdDataPrescriptionSourceController {

    private final BiMtdDataPrescriptionSourceService biMtdDataPrescriptionSourceService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdDataPrescriptionSource:list')")
    public void download(HttpServletResponse response, BiMtdDataPrescriptionSourceQueryCriteria criteria) throws IOException {
        biMtdDataPrescriptionSourceService.download(generator.convert(biMtdDataPrescriptionSourceService.queryAll(criteria), BiMtdDataPrescriptionSourceDto.class), response);
    }

    @GetMapping
    @Log("查询处方来源分布统计")
    @ApiOperation("查询处方来源分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPrescriptionSource:list')")
    public ResponseEntity<Object> getBiMtdDataPrescriptionSources(BiMtdDataPrescriptionSourceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataPrescriptionSourceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增处方来源分布统计")
    @ApiOperation("新增处方来源分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPrescriptionSource:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdDataPrescriptionSource resources){
        return new ResponseEntity<>(biMtdDataPrescriptionSourceService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改处方来源分布统计")
    @ApiOperation("修改处方来源分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPrescriptionSource:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdDataPrescriptionSource resources){
        biMtdDataPrescriptionSourceService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除处方来源分布统计")
    @ApiOperation("删除处方来源分布统计")
    @PreAuthorize("@el.check('admin','biMtdDataPrescriptionSource:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataPrescriptionSourceService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
