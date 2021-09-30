/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.UrlConfig;
import co.yixiang.modules.shop.service.UrlConfigService;
import co.yixiang.modules.shop.service.dto.UrlConfigQueryCriteria;
import co.yixiang.modules.shop.service.dto.UrlConfigDto;
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
* @date 2020-06-10
*/
@AllArgsConstructor
@Api(tags = "url配置管理")
@RestController
@RequestMapping("/api/urlConfig")
public class UrlConfigController {

    private final UrlConfigService urlConfigService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','urlConfig:list')")
    public void download(HttpServletResponse response, UrlConfigQueryCriteria criteria) throws IOException {
        urlConfigService.download(generator.convert(urlConfigService.queryAll(criteria), UrlConfigDto.class), response);
    }

    @GetMapping
    @Log("查询url配置")
    @ApiOperation("查询url配置")
    @PreAuthorize("@el.check('admin','urlConfig:list')")
    public ResponseEntity<Object> getUrlConfigs(UrlConfigQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(urlConfigService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增url配置")
    @ApiOperation("新增url配置")
    @PreAuthorize("@el.check('admin','urlConfig:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody UrlConfig resources){
        return new ResponseEntity<>(urlConfigService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改url配置")
    @ApiOperation("修改url配置")
    @PreAuthorize("@el.check('admin','urlConfig:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody UrlConfig resources){
        urlConfigService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除url配置")
    @ApiOperation("删除url配置")
    @PreAuthorize("@el.check('admin','urlConfig:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            urlConfigService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
