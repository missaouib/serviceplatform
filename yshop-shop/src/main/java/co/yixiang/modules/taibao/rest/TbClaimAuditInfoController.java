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
import co.yixiang.modules.taibao.domain.TbClaimAuditInfo;
import co.yixiang.modules.taibao.service.TbClaimAuditInfoService;
import co.yixiang.modules.taibao.service.dto.TbClaimAuditInfoDto;
import co.yixiang.modules.taibao.service.dto.TbClaimAuditInfoQueryCriteria;
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
@Api(tags = "太保安联-赔付责任理赔结论信息表管理")
@RestController
@RequestMapping("/api/tbClaimAuditInfo")
public class TbClaimAuditInfoController {

    private final TbClaimAuditInfoService tbClaimAuditInfoService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbClaimAuditInfo:list')")
    public void download(HttpServletResponse response, TbClaimAuditInfoQueryCriteria criteria) throws IOException {
        tbClaimAuditInfoService.download(generator.convert(tbClaimAuditInfoService.queryAll(criteria), TbClaimAuditInfoDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-赔付责任理赔结论信息表")
    @ApiOperation("查询太保安联-赔付责任理赔结论信息表")
    @PreAuthorize("@el.check('admin','tbClaimAuditInfo:list')")
    public ResponseEntity<Object> getTbClaimAuditInfos(TbClaimAuditInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbClaimAuditInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-赔付责任理赔结论信息表")
    @ApiOperation("新增太保安联-赔付责任理赔结论信息表")
    @PreAuthorize("@el.check('admin','tbClaimAuditInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbClaimAuditInfo resources){
        return new ResponseEntity<>(tbClaimAuditInfoService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-赔付责任理赔结论信息表")
    @ApiOperation("修改太保安联-赔付责任理赔结论信息表")
    @PreAuthorize("@el.check('admin','tbClaimAuditInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbClaimAuditInfo resources){
        tbClaimAuditInfoService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-赔付责任理赔结论信息表")
    @ApiOperation("删除太保安联-赔付责任理赔结论信息表")
    @PreAuthorize("@el.check('admin','tbClaimAuditInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbClaimAuditInfoService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
