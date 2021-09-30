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
import co.yixiang.tools.domain.WechatConfiguration;
import co.yixiang.tools.service.WechatConfigurationService;
import co.yixiang.tools.service.dto.WechatConfigurationDto;
import co.yixiang.tools.service.dto.WechatConfigurationQueryCriteria;
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
* @date 2021-09-24
*/
@AllArgsConstructor
@Api(tags = "微信支付配置管理")
@RestController
@RequestMapping("/api/wechatConfiguration")
public class WechatConfigurationController {

    private final WechatConfigurationService wechatConfigurationService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','wechatConfiguration:list')")
    public void download(HttpServletResponse response, WechatConfigurationQueryCriteria criteria) throws IOException {
        wechatConfigurationService.download(generator.convert(wechatConfigurationService.queryAll(criteria), WechatConfigurationDto.class), response);
    }

    @GetMapping
    @Log("查询微信支付配置")
    @ApiOperation("查询微信支付配置")
    public ResponseEntity<Object> getWechatConfigurations(WechatConfigurationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wechatConfigurationService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增微信支付配置")
    @ApiOperation("新增微信支付配置")
    @PreAuthorize("@el.check('admin','wechatConfiguration:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WechatConfiguration resources){
        return new ResponseEntity<>(wechatConfigurationService.saveWechatConfiguration(resources),HttpStatus.CREATED);
}

    @PutMapping
    @Log("修改微信支付配置")
    @ApiOperation("修改微信支付配置")
    @PreAuthorize("@el.check('admin','wechatConfiguration:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WechatConfiguration resources){
        wechatConfigurationService.updateWechatConfiguration(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除微信支付配置")
    @ApiOperation("删除微信支付配置")
    @PreAuthorize("@el.check('admin','wechatConfiguration:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            wechatConfigurationService.deleteById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
