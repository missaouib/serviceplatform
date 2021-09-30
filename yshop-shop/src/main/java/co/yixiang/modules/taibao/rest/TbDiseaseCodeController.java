/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taibao.domain.TbDiseaseCode;
import co.yixiang.modules.taibao.service.TbDiseaseCodeService;
import co.yixiang.modules.taibao.service.dto.TbDiseaseCodeDto;
import co.yixiang.modules.taibao.service.dto.TbDiseaseCodeQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
* @author zhoujinlai
* @date 2021-05-08
*/
@AllArgsConstructor
@Api(tags = "太保安联-疾病代码管理")
@RestController
@RequestMapping("/api/tbDiseaseCode")
public class TbDiseaseCodeController {

    private final TbDiseaseCodeService tbDiseaseCodeService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbDiseaseCode:list','TBPOLICYINFO_ALL')")
    public void download(HttpServletResponse response, TbDiseaseCodeQueryCriteria criteria) throws IOException {
        tbDiseaseCodeService.download(generator.convert(tbDiseaseCodeService.queryAll(criteria), TbDiseaseCodeDto.class), response);
    }

    @GetMapping
    @Log("查询疾病代码")
    @ApiOperation("查询疾病代码")
    @PreAuthorize("@el.check('admin','tbDiseaseCode:list','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> getTbDiseaseCodes(TbDiseaseCodeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbDiseaseCodeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增疾病代码")
    @ApiOperation("新增疾病代码")
    @PreAuthorize("@el.check('admin','tbDiseaseCode:add','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbDiseaseCode resources){
        return new ResponseEntity<>(tbDiseaseCodeService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改疾病代码")
    @ApiOperation("修改疾病代码")
    @PreAuthorize("@el.check('admin','tbDiseaseCode:edit','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbDiseaseCode resources){
        tbDiseaseCodeService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除疾病代码")
    @ApiOperation("删除疾病代码")
    @PreAuthorize("@el.check('admin','tbDiseaseCode:del','TBPOLICYINFO_ALL')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbDiseaseCodeService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
