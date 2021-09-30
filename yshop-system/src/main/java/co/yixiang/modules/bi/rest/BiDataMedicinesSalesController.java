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
import co.yixiang.modules.bi.domain.BiDataMedicinesSales;
import co.yixiang.modules.bi.service.BiDataMedicinesSalesService;
import co.yixiang.modules.bi.service.dto.BiDataMedicinesSalesQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiDataMedicinesSalesDto;
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
@Api(tags = "药品每月销售额管理")
@RestController
@RequestMapping("/api/biDataMedicinesSales")
public class BiDataMedicinesSalesController {

    private final BiDataMedicinesSalesService biDataMedicinesSalesService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biDataMedicinesSales:list')")
    public void download(HttpServletResponse response, BiDataMedicinesSalesQueryCriteria criteria) throws IOException {
        biDataMedicinesSalesService.download(generator.convert(biDataMedicinesSalesService.queryAll(criteria), BiDataMedicinesSalesDto.class), response);
    }

    @GetMapping
    @Log("查询药品每月销售额")
    @ApiOperation("查询药品每月销售额")
    @PreAuthorize("@el.check('admin','biDataMedicinesSales:list')")
    public ResponseEntity<Object> getBiDataMedicinesSaless(BiDataMedicinesSalesQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biDataMedicinesSalesService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药品每月销售额")
    @ApiOperation("新增药品每月销售额")
    @PreAuthorize("@el.check('admin','biDataMedicinesSales:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiDataMedicinesSales resources){
        return new ResponseEntity<>(biDataMedicinesSalesService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药品每月销售额")
    @ApiOperation("修改药品每月销售额")
    @PreAuthorize("@el.check('admin','biDataMedicinesSales:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiDataMedicinesSales resources){
        biDataMedicinesSalesService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药品每月销售额")
    @ApiOperation("删除药品每月销售额")
    @PreAuthorize("@el.check('admin','biDataMedicinesSales:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biDataMedicinesSalesService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
