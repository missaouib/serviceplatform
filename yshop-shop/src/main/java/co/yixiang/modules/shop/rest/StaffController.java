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

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.exception.ErrorRequestException;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.Staff;
import co.yixiang.modules.shop.service.StaffService;
import co.yixiang.modules.shop.service.dto.StaffQueryCriteria;
import co.yixiang.modules.shop.service.dto.StaffDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-03-04
*/
@AllArgsConstructor
@Api(tags = "员工接口管理")
@RestController
@RequestMapping("/api/staff")
@Slf4j
public class StaffController {

    private final StaffService staffService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','staff:list')")
    public void download(HttpServletResponse response, StaffQueryCriteria criteria) throws IOException {
        staffService.download(generator.convert(staffService.queryAll(criteria), StaffDto.class), response);
    }

    @GetMapping
    @Log("查询员工接口")
    @ApiOperation("查询员工接口")
    @PreAuthorize("@el.check('admin','staff:list')")
    public ResponseEntity<Object> getStaffs(StaffQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(staffService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增员工接口")
    @ApiOperation("新增员工接口")
    @PreAuthorize("@el.check('admin','staff:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Staff resources){
        Boolean hasChinese =   Validator.hasChinese(resources.getCode());
        if(hasChinese) {
            throw new ErrorRequestException("员工代码不能含有中文");
        }
        return new ResponseEntity<>(staffService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改员工接口")
    @ApiOperation("修改员工接口")
    @PreAuthorize("@el.check('admin','staff:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Staff resources){

        Boolean hasChinese =   Validator.hasChinese(resources.getCode());
        if(hasChinese) {
            throw new ErrorRequestException("员工代码不能含有中文");
        }

        staffService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除员工接口")
    @ApiOperation("删除员工接口")
    @PreAuthorize("@el.check('admin','staff:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            staffService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {
        int count = 0;
        log.info("项目员工批量上载开始====================");
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
            List<Map<String,Object>> readAll = reader.readAll();
            count = staffService.upload(readAll);


        log.info("项目员工批量上载开始,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }
}
