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
import co.yixiang.modules.taibao.domain.TbClaimThirdInsurance;
import co.yixiang.modules.taibao.service.TbClaimThirdInsuranceService;
import co.yixiang.modules.taibao.service.dto.TbClaimThirdInsuranceDto;
import co.yixiang.modules.taibao.service.dto.TbClaimThirdInsuranceQueryCriteria;
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
@Api(tags = "太保安联-第三方投保信息表管理")
@RestController
@RequestMapping("/api/tbClaimThirdInsurance")
public class TbClaimThirdInsuranceController {

    private final TbClaimThirdInsuranceService tbClaimThirdInsuranceService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbClaimThirdInsurance:list')")
    public void download(HttpServletResponse response, TbClaimThirdInsuranceQueryCriteria criteria) throws IOException {
        tbClaimThirdInsuranceService.download(generator.convert(tbClaimThirdInsuranceService.queryAll(criteria), TbClaimThirdInsuranceDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-第三方投保信息表")
    @ApiOperation("查询太保安联-第三方投保信息表")
    @PreAuthorize("@el.check('admin','tbClaimThirdInsurance:list')")
    public ResponseEntity<Object> getTbClaimThirdInsurances(TbClaimThirdInsuranceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbClaimThirdInsuranceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-第三方投保信息表")
    @ApiOperation("新增太保安联-第三方投保信息表")
    @PreAuthorize("@el.check('admin','tbClaimThirdInsurance:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbClaimThirdInsurance resources){
        return new ResponseEntity<>(tbClaimThirdInsuranceService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-第三方投保信息表")
    @ApiOperation("修改太保安联-第三方投保信息表")
    @PreAuthorize("@el.check('admin','tbClaimThirdInsurance:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbClaimThirdInsurance resources){
        tbClaimThirdInsuranceService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-第三方投保信息表")
    @ApiOperation("删除太保安联-第三方投保信息表")
    @PreAuthorize("@el.check('admin','tbClaimThirdInsurance:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbClaimThirdInsuranceService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
