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
import co.yixiang.modules.taibao.domain.TbClaimOther;
import co.yixiang.modules.taibao.service.TbClaimOtherService;
import co.yixiang.modules.taibao.service.dto.TbClaimOtherDto;
import co.yixiang.modules.taibao.service.dto.TbClaimOtherQueryCriteria;
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
@Api(tags = "太保安联-赔案其他信息表管理")
@RestController
@RequestMapping("/api/tbClaimOther")
public class TbClaimOtherController {

    private final TbClaimOtherService tbClaimOtherService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbClaimOther:list')")
    public void download(HttpServletResponse response, TbClaimOtherQueryCriteria criteria) throws IOException {
        tbClaimOtherService.download(generator.convert(tbClaimOtherService.queryAll(criteria), TbClaimOtherDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-赔案其他信息表")
    @ApiOperation("查询太保安联-赔案其他信息表")
    @PreAuthorize("@el.check('admin','tbClaimOther:list')")
    public ResponseEntity<Object> getTbClaimOthers(TbClaimOtherQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbClaimOtherService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-赔案其他信息表")
    @ApiOperation("新增太保安联-赔案其他信息表")
    @PreAuthorize("@el.check('admin','tbClaimOther:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbClaimOther resources){
        return new ResponseEntity<>(tbClaimOtherService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-赔案其他信息表")
    @ApiOperation("修改太保安联-赔案其他信息表")
    @PreAuthorize("@el.check('admin','tbClaimOther:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbClaimOther resources){
        tbClaimOtherService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-赔案其他信息表")
    @ApiOperation("删除太保安联-赔案其他信息表")
    @PreAuthorize("@el.check('admin','tbClaimOther:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbClaimOtherService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
