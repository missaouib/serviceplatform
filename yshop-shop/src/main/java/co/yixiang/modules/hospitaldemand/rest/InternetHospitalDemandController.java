/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemand;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandQueryCriteria;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDto;
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
* @date 2021-01-05
*/
@AllArgsConstructor
@Api(tags = "互联网医院处方表管理")
@RestController
@RequestMapping("/api/internetHospitalDemand")
public class InternetHospitalDemandController {

    private final InternetHospitalDemandService internetHospitalDemandService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','internetHospitalDemand:list')")
    public void download(HttpServletResponse response, InternetHospitalDemandQueryCriteria criteria) throws IOException {
        internetHospitalDemandService.download(generator.convert(internetHospitalDemandService.queryAll(criteria), InternetHospitalDemandDto.class), response);
    }

    @GetMapping
    @Log("查询互联网医院处方表")
    @ApiOperation("查询互联网医院处方表")
    @PreAuthorize("@el.check('admin','internetHospitalDemand:list')")
    public ResponseEntity<Object> getInternetHospitalDemands(InternetHospitalDemandQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(internetHospitalDemandService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增互联网医院处方表")
    @ApiOperation("新增互联网医院处方表")
    @PreAuthorize("@el.check('admin','internetHospitalDemand:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InternetHospitalDemand resources){
        return new ResponseEntity<>(internetHospitalDemandService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改互联网医院处方表")
    @ApiOperation("修改互联网医院处方表")
    @PreAuthorize("@el.check('admin','internetHospitalDemand:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InternetHospitalDemand resources){
        internetHospitalDemandService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除互联网医院处方表")
    @ApiOperation("删除互联网医院处方表")
    @PreAuthorize("@el.check('admin','internetHospitalDemand:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            internetHospitalDemandService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
