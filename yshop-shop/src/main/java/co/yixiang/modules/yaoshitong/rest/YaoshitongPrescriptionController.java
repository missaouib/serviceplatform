/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.rest;
import java.util.Arrays;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaoshitong.domain.YaoshitongPrescription;
import co.yixiang.modules.yaoshitong.service.YaoshitongPrescriptionService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPrescriptionQueryCriteria;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPrescriptionDto;
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
* @date 2020-07-21
*/
@AllArgsConstructor
@Api(tags = "药师通-处方管理管理")
@RestController
@RequestMapping("/api/yaoshitongPrescription")
public class YaoshitongPrescriptionController {

    private final YaoshitongPrescriptionService yaoshitongPrescriptionService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaoshitongPrescription:list')")
    public void download(HttpServletResponse response, YaoshitongPrescriptionQueryCriteria criteria) throws IOException {
        yaoshitongPrescriptionService.download(generator.convert(yaoshitongPrescriptionService.queryAll(criteria), YaoshitongPrescriptionDto.class), response);
    }

    @GetMapping
    @Log("查询药师通-处方管理")
    @ApiOperation("查询药师通-处方管理")
    @AnonymousAccess
    public ResponseEntity<Object> getYaoshitongPrescriptions(YaoshitongPrescriptionQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaoshitongPrescriptionService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药师通-处方管理")
    @ApiOperation("新增药师通-处方管理")
    @PreAuthorize("@el.check('admin','yaoshitongPrescription:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaoshitongPrescription resources){
        return new ResponseEntity<>(yaoshitongPrescriptionService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药师通-处方管理")
    @ApiOperation("修改药师通-处方管理")
    @PreAuthorize("@el.check('admin','yaoshitongPrescription:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaoshitongPrescription resources){
        yaoshitongPrescriptionService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药师通-处方管理")
    @ApiOperation("删除药师通-处方管理")
    @PreAuthorize("@el.check('admin','yaoshitongPrescription:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongPrescriptionService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
