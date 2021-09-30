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

import cn.hutool.core.util.StrUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.MdCountry;
import co.yixiang.modules.shop.service.MdCountryService;
import co.yixiang.modules.shop.service.dto.MdCountryQueryCriteria;
import co.yixiang.modules.shop.service.dto.MdCountryDto;
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
* @date 2020-10-16
*/
@AllArgsConstructor
@Api(tags = "省市区表管理")
@RestController
@RequestMapping("/api/mdCountry")
public class MdCountryController {

    private final MdCountryService mdCountryService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mdCountry:list')")
    public void download(HttpServletResponse response, MdCountryQueryCriteria criteria) throws IOException {
        mdCountryService.download(generator.convert(mdCountryService.queryAll(criteria), MdCountryDto.class), response);
    }

    @GetMapping
    @Log("查询省市区表")
    @ApiOperation("查询省市区表")
    @AnonymousAccess
    public ResponseEntity<Object> getMdCountrys(MdCountryQueryCriteria criteria, Pageable pageable){
        if(StrUtil.isNotBlank(criteria.getParentAreaName())) {
            LambdaQueryWrapper<MdCountry> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(MdCountry::getName,criteria.getParentAreaName());
            lambdaQueryWrapper.eq(MdCountry::getTreeId,"1");
            MdCountry mdCountry = mdCountryService.getOne(lambdaQueryWrapper);
            criteria.setParentId(String.valueOf(mdCountry.getId()));
        }


        return new ResponseEntity<>(mdCountryService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增省市区表")
    @ApiOperation("新增省市区表")
    @PreAuthorize("@el.check('admin','mdCountry:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MdCountry resources){
        return new ResponseEntity<>(mdCountryService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改省市区表")
    @ApiOperation("修改省市区表")
    @PreAuthorize("@el.check('admin','mdCountry:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MdCountry resources){
        mdCountryService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除省市区表")
    @ApiOperation("删除省市区表")
    @PreAuthorize("@el.check('admin','mdCountry:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mdCountryService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/cascade")
    @ApiOperation("查询省市区表")
    @AnonymousAccess
    public ResponseEntity<Object> getMdCountrysCascade(MdCountryQueryCriteria criteria, Pageable pageable){

        return new ResponseEntity<>(mdCountryService.queryAllCascade(criteria,pageable),HttpStatus.OK);
    }


    @GetMapping("/tree")
    @ApiOperation("查询省市表")
    @AnonymousAccess
    public ResponseEntity<Object> getMdCountrysTree(MdCountryQueryCriteria criteria, Pageable pageable){

        return new ResponseEntity<>(mdCountryService.queryAllTree(criteria,pageable),HttpStatus.OK);
    }



}
