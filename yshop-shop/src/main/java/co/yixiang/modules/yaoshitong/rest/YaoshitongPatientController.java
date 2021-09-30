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
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaoshitong.domain.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPatientQueryCriteria;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPatientDto;
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
@Api(tags = "药师通-患者主数据管理")
@RestController
@RequestMapping("/api/yaoshitongPatient")
public class YaoshitongPatientController {

    private final YaoshitongPatientService yaoshitongPatientService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaoshitongPatient:list')")
    public void download(HttpServletResponse response, YaoshitongPatientQueryCriteria criteria) throws IOException {
        yaoshitongPatientService.download(generator.convert(yaoshitongPatientService.queryAll(criteria), YaoshitongPatientDto.class), response);
    }

    @GetMapping
    @Log("查询药师通-患者主数据")
    @ApiOperation("查询药师通-患者主数据")
    @PreAuthorize("@el.check('admin','yaoshitongPatient:list')")
    public ResponseEntity<Object> getYaoshitongPatients(YaoshitongPatientQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaoshitongPatientService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药师通-患者主数据")
    @ApiOperation("新增药师通-患者主数据")
    @PreAuthorize("@el.check('admin','yaoshitongPatient:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaoshitongPatient resources){
        return new ResponseEntity<>(yaoshitongPatientService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药师通-患者主数据")
    @ApiOperation("修改药师通-患者主数据")
    @PreAuthorize("@el.check('admin','yaoshitongPatient:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaoshitongPatient resources){
        yaoshitongPatientService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药师通-患者主数据")
    @ApiOperation("删除药师通-患者主数据")
    @PreAuthorize("@el.check('admin','yaoshitongPatient:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongPatientService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
