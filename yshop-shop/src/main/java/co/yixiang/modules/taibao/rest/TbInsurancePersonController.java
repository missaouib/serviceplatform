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
import co.yixiang.modules.taibao.domain.TbInsurancePerson;
import co.yixiang.modules.taibao.service.TbInsurancePersonService;
import co.yixiang.modules.taibao.service.dto.TbInsurancePersonDto;
import co.yixiang.modules.taibao.service.dto.TbInsurancePersonQueryCriteria;
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
* @date 2021-04-30
*/
@AllArgsConstructor
@Api(tags = "太保安联-被保人信息表管理")
@RestController
@RequestMapping("/api/tbInsurancePerson")
public class TbInsurancePersonController {

    private final TbInsurancePersonService tbInsurancePersonService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbInsurancePerson:list')")
    public void download(HttpServletResponse response, TbInsurancePersonQueryCriteria criteria) throws IOException {
        tbInsurancePersonService.download(generator.convert(tbInsurancePersonService.queryAll(criteria), TbInsurancePersonDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-被保人信息表")
    @ApiOperation("查询太保安联-被保人信息表")
    @PreAuthorize("@el.check('admin','tbInsurancePerson:list')")
    public ResponseEntity<Object> getTbInsurancePersons(TbInsurancePersonQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbInsurancePersonService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-被保人信息表")
    @ApiOperation("新增太保安联-被保人信息表")
    @PreAuthorize("@el.check('admin','tbInsurancePerson:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbInsurancePerson resources){
        return new ResponseEntity<>(tbInsurancePersonService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-被保人信息表")
    @ApiOperation("修改太保安联-被保人信息表")
    @PreAuthorize("@el.check('admin','tbInsurancePerson:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbInsurancePerson resources){
        tbInsurancePersonService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-被保人信息表")
    @ApiOperation("删除太保安联-被保人信息表")
    @PreAuthorize("@el.check('admin','tbInsurancePerson:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbInsurancePersonService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
