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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.Hospital;
import co.yixiang.modules.shop.service.HospitalService;
import co.yixiang.modules.shop.service.dto.HospitalQueryCriteria;
import co.yixiang.modules.shop.service.dto.HospitalDto;
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
* @date 2021-06-17
*/
@AllArgsConstructor
@Api(tags = "自费药业务医院管理")
@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','hospital:list')")
    public void download(HttpServletResponse response, HospitalQueryCriteria criteria) throws IOException {
        hospitalService.download(generator.convert(hospitalService.queryAll(criteria), HospitalDto.class), response);
    }

    @GetMapping
    @Log("查询自费药业务医院")
    @ApiOperation("查询自费药业务医院")
    @PreAuthorize("@el.check('admin','hospital:list')")
    public ResponseEntity<Object> getHospitals(HospitalQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(hospitalService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增自费药业务医院")
    @ApiOperation("新增自费药业务医院")
    @PreAuthorize("@el.check('admin','hospital:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Hospital resources){

        JSONObject jsonObject = JSON.parseObject(resources.getSiteInfo());
        resources.setSiteInfo(jsonObject.toJSONString());

        return new ResponseEntity<>(hospitalService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改自费药业务医院")
    @ApiOperation("修改自费药业务医院")
    @PreAuthorize("@el.check('admin','hospital:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Hospital resources){
        JSONObject jsonObject = JSON.parseObject(resources.getSiteInfo());
        resources.setSiteInfo(jsonObject.toJSONString());
        hospitalService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除自费药业务医院")
    @ApiOperation("删除自费药业务医院")
    @PreAuthorize("@el.check('admin','hospital:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            hospitalService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
