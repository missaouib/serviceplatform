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
import co.yixiang.modules.bi.domain.BiMtdDataMedicines;
import co.yixiang.modules.bi.service.BiMtdDataMedicinesService;
import co.yixiang.modules.bi.service.dto.BiMtdDataMedicinesQueryCriteria;
import co.yixiang.modules.bi.service.dto.BiMtdDataMedicinesDto;
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
@Api(tags = "co.yixiang.modules.bi管理")
@RestController
@RequestMapping("/api/biMtdDataMedicines")
public class BiMtdDataMedicinesController {

    private final BiMtdDataMedicinesService biMtdDataMedicinesService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','biMtdDataMedicines:list')")
    public void download(HttpServletResponse response, BiMtdDataMedicinesQueryCriteria criteria) throws IOException {
        biMtdDataMedicinesService.download(generator.convert(biMtdDataMedicinesService.queryAll(criteria), BiMtdDataMedicinesDto.class), response);
    }

    @GetMapping
    @Log("查询co.yixiang.modules.bi")
    @ApiOperation("查询co.yixiang.modules.bi")
    @PreAuthorize("@el.check('admin','biMtdDataMedicines:list')")
    public ResponseEntity<Object> getBiMtdDataMediciness(BiMtdDataMedicinesQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(biMtdDataMedicinesService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增co.yixiang.modules.bi")
    @ApiOperation("新增co.yixiang.modules.bi")
    @PreAuthorize("@el.check('admin','biMtdDataMedicines:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BiMtdDataMedicines resources){
        return new ResponseEntity<>(biMtdDataMedicinesService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改co.yixiang.modules.bi")
    @ApiOperation("修改co.yixiang.modules.bi")
    @PreAuthorize("@el.check('admin','biMtdDataMedicines:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BiMtdDataMedicines resources){
        biMtdDataMedicinesService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除co.yixiang.modules.bi")
    @ApiOperation("删除co.yixiang.modules.bi")
    @PreAuthorize("@el.check('admin','biMtdDataMedicines:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            biMtdDataMedicinesService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
