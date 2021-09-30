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
import co.yixiang.modules.shop.domain.YxExpressTemplate;
import co.yixiang.modules.shop.service.YxExpressTemplateService;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDto;
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
* @date 2020-11-28
*/
@AllArgsConstructor
@Api(tags = "运费模板管理")
@RestController
@RequestMapping("/api/yxExpressTemplate")
public class YxExpressTemplateController {

    private final YxExpressTemplateService yxExpressTemplateService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, YxExpressTemplateQueryCriteria criteria) throws IOException {
        yxExpressTemplateService.download(generator.convert(yxExpressTemplateService.queryAll(criteria), YxExpressTemplateDto.class), response);
    }

    @GetMapping
    @Log("查询运费模板")
    @ApiOperation("查询运费模板")
    public ResponseEntity<Object> getYxExpressTemplates(YxExpressTemplateQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxExpressTemplateService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增运费模板")
    @ApiOperation("新增运费模板")
    public ResponseEntity<Object> create(@Validated @RequestBody YxExpressTemplate resources){
        return new ResponseEntity<>(yxExpressTemplateService.saveExpressTemplate(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改运费模板")
    @ApiOperation("修改运费模板")
    public ResponseEntity<Object> update(@Validated @RequestBody YxExpressTemplate resources){
        yxExpressTemplateService.saveExpressTemplate(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除运费模板")
    @ApiOperation("删除运费模板")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxExpressTemplateService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
