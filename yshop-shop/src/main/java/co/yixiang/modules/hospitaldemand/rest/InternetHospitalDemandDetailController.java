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
import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDetailQueryCriteria;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDetailDto;
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
* @author visazhou
* @date 2021-01-22
*/
@AllArgsConstructor
@Api(tags = "互联网医院处方明细管理")
@RestController
@RequestMapping("/api/internetHospitalDemandDetail")
public class InternetHospitalDemandDetailController {

    private final InternetHospitalDemandDetailService internetHospitalDemandDetailService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','internetHospitalDemandDetail:list')")
    public void download(HttpServletResponse response, InternetHospitalDemandDetailQueryCriteria criteria) throws IOException {
        internetHospitalDemandDetailService.download(generator.convert(internetHospitalDemandDetailService.queryAll(criteria), InternetHospitalDemandDetailDto.class), response);
    }

    @GetMapping
    @Log("查询互联网医院处方明细")
    @ApiOperation("查询互联网医院处方明细")
    @PreAuthorize("@el.check('admin','internetHospitalDemandDetail:list')")
    public ResponseEntity<Object> getInternetHospitalDemandDetails(InternetHospitalDemandDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(internetHospitalDemandDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增互联网医院处方明细")
    @ApiOperation("新增互联网医院处方明细")
    @PreAuthorize("@el.check('admin','internetHospitalDemandDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InternetHospitalDemandDetail resources){
        return new ResponseEntity<>(internetHospitalDemandDetailService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改互联网医院处方明细")
    @ApiOperation("修改互联网医院处方明细")
    @PreAuthorize("@el.check('admin','internetHospitalDemandDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InternetHospitalDemandDetail resources){
        internetHospitalDemandDetailService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除互联网医院处方明细")
    @ApiOperation("删除互联网医院处方明细")
    @PreAuthorize("@el.check('admin','internetHospitalDemandDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            internetHospitalDemandDetailService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
