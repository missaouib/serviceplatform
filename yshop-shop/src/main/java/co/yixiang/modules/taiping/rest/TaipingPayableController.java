/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taiping.domain.TaipingPayable;
import co.yixiang.modules.taiping.service.TaipingPayableService;
import co.yixiang.modules.taiping.service.dto.TaipingPayableQueryCriteria;
import co.yixiang.modules.taiping.service.dto.TaipingPayableDto;
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
* @date 2020-11-03
*/
@AllArgsConstructor
@Api(tags = "太平应付款记录管理")
@RestController
@RequestMapping("/api/taipingPayable")
public class TaipingPayableController {

    private final TaipingPayableService taipingPayableService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','taipingPayable:list')")
    public void download(HttpServletResponse response, TaipingPayableQueryCriteria criteria) throws IOException {
        taipingPayableService.download(generator.convert(taipingPayableService.queryAll(criteria), TaipingPayableDto.class), response);
    }

    @GetMapping
    @Log("查询太平应付款记录")
    @ApiOperation("查询太平应付款记录")
    @PreAuthorize("@el.check('admin','taipingPayable:list')")
    public ResponseEntity<Object> getTaipingPayables(TaipingPayableQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(taipingPayableService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太平应付款记录")
    @ApiOperation("新增太平应付款记录")
    @PreAuthorize("@el.check('admin','taipingPayable:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TaipingPayable resources){
        return new ResponseEntity<>(taipingPayableService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太平应付款记录")
    @ApiOperation("修改太平应付款记录")
    @PreAuthorize("@el.check('admin','taipingPayable:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TaipingPayable resources){
        taipingPayableService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太平应付款记录")
    @ApiOperation("删除太平应付款记录")
    @PreAuthorize("@el.check('admin','taipingPayable:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            taipingPayableService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
