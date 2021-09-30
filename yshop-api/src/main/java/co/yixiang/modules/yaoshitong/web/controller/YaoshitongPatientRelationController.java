/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.web.controller;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientRelationService;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPatientRelationDto;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientRelationQueryCriteria;
import lombok.AllArgsConstructor;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatientRelation;

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
* @date 2020-07-13
*/
@AllArgsConstructor
@Api(tags = "药师通-患者-药师关系维护管理")
@RestController
@RequestMapping("/api/yaoshitongPatientRelation")
public class YaoshitongPatientRelationController {

    private final YaoshitongPatientRelationService yaoshitongPatientRelationService;
    private final IGenerator generator;



    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaoshitongPatientRelation:list')")
    public void download(HttpServletResponse response, YaoshitongPatientRelationQueryCriteria criteria) throws IOException {
        yaoshitongPatientRelationService.download(generator.convert(yaoshitongPatientRelationService.queryAll(criteria), YaoshitongPatientRelationDto.class), response);
    }

    @GetMapping

    @ApiOperation("查询药师通-患者-药师关系维护")
    @PreAuthorize("@el.check('admin','yaoshitongPatientRelation:list')")
    public ResponseEntity<Object> getYaoshitongPatientRelations(YaoshitongPatientRelationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaoshitongPatientRelationService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping

    @ApiOperation("新增药师通-患者-药师关系维护")
    @PreAuthorize("@el.check('admin','yaoshitongPatientRelation:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaoshitongPatientRelation resources){
        return new ResponseEntity<>(yaoshitongPatientRelationService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping

    @ApiOperation("修改药师通-患者-药师关系维护")
    @PreAuthorize("@el.check('admin','yaoshitongPatientRelation:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaoshitongPatientRelation resources){
        yaoshitongPatientRelationService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @ApiOperation("删除药师通-患者-药师关系维护")
    @PreAuthorize("@el.check('admin','yaoshitongPatientRelation:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaoshitongPatientRelationService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
