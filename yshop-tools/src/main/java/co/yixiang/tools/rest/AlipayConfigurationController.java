/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.tools.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.tools.domain.AlipayConfiguration;
import co.yixiang.tools.service.AlipayConfigurationService;
import co.yixiang.tools.service.dto.AlipayConfigurationDto;
import co.yixiang.tools.service.dto.AlipayConfigurationQueryCriteria;
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
* @date 2021-09-01
*/
@AllArgsConstructor
@Api(tags = "支付宝配置管理")
@RestController
@RequestMapping("/api/alipayConfiguration")
public class AlipayConfigurationController {

    private final AlipayConfigurationService alipayConfigurationService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','alipayConfiguration:list')")
    public void download(HttpServletResponse response, AlipayConfigurationQueryCriteria criteria) throws IOException {
        alipayConfigurationService.download(generator.convert(alipayConfigurationService.queryAll(criteria), AlipayConfigurationDto.class), response);
    }

    @GetMapping
    @Log("查询支付宝配置")
    @ApiOperation("查询支付宝配置")
    public ResponseEntity<Object> getAlipayConfigurations(AlipayConfigurationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(alipayConfigurationService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增支付宝配置")
    @ApiOperation("新增支付宝配置")
    @PreAuthorize("@el.check('admin','alipayConfiguration:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody AlipayConfiguration resources){
        return new ResponseEntity<>(alipayConfigurationService.saveAlipayConfiguration(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改支付宝配置")
    @ApiOperation("修改支付宝配置")
    @PreAuthorize("@el.check('admin','alipayConfiguration:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody AlipayConfiguration resources){
        alipayConfigurationService.updateAlipayConfiguration(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除支付宝配置")
    @ApiOperation("删除支付宝配置")
    @PreAuthorize("@el.check('admin','alipayConfiguration:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            alipayConfigurationService.deleteById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
