/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.rest;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.dto.YxStoreProductAttrValueDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductAttrValueQueryCriteria;
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
import java.util.UUID;

/**
* @author visa
* @date 2020-05-29
*/
@AllArgsConstructor
@Api(tags = "药品-药店配置管理")
@RestController
@RequestMapping("/api/yxStoreProductAttrValue")
public class YxStoreProductAttrValueController {

    private final YxStoreProductAttrValueService yxStoreProductAttrValueService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxStoreProductAttrValue:list')")
    public void download(HttpServletResponse response, YxStoreProductAttrValueQueryCriteria criteria) throws IOException {
        yxStoreProductAttrValueService.download(generator.convert(yxStoreProductAttrValueService.queryAll(criteria), YxStoreProductAttrValueDto.class), response);
    }

    @GetMapping
    @Log("查询药品-药店配置")
    @ApiOperation("查询药品-药店配置")
    @AnonymousAccess
   // @PreAuthorize("@el.check('admin','yxStoreProductAttrValue:list')")
    public ResponseEntity<Object> getYxStoreProductAttrValues(YxStoreProductAttrValueQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxStoreProductAttrValueService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药品-药店配置")
    @ApiOperation("新增药品-药店配置")
    @PreAuthorize("@el.check('admin','yxStoreProductAttrValue:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxStoreProductAttrValue resources){
        resources.setUnique(UUID.randomUUID().toString());
        resources.setCost(resources.getPrice());

        return new ResponseEntity<>(yxStoreProductAttrValueService.saveAttrValue(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药品-药店配置")
    @ApiOperation("修改药品-药店配置")
    @PreAuthorize("@el.check('admin','yxStoreProductAttrValue:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxStoreProductAttrValue resources){
        resources.setCost(resources.getPrice());
        //   yxStoreProductAttrValueService.updateById(resources);
        yxStoreProductAttrValueService.saveAttrValue(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药品-药店配置")
    @ApiOperation("删除药品-药店配置")
    @PreAuthorize("@el.check('admin','yxStoreProductAttrValue:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxStoreProductAttrValueService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
