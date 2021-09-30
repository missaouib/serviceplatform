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
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.service.dto.MdPharmacistServiceQueryCriteria;
import co.yixiang.modules.shop.service.dto.MdPharmacistServiceDto;
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
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-06-02
*/
@AllArgsConstructor
@Api(tags = "药师管理管理")
@RestController
@RequestMapping("/api/mdPharmacistService")
@Slf4j
public class MdPharmacistServiceController {

    private final MdPharmacistServiceService mdPharmacistServiceService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mdPharmacistService:list')")
    public void download(HttpServletResponse response, MdPharmacistServiceQueryCriteria criteria) throws IOException {
        mdPharmacistServiceService.download(generator.convert(mdPharmacistServiceService.queryAll(criteria), MdPharmacistServiceDto.class), response);
    }

    @GetMapping
    @Log("查询药师管理")
    @ApiOperation("查询药师管理")
    @PreAuthorize("@el.check('admin','mdPharmacistService:list')")
    public ResponseEntity<Object> getMdPharmacistServices(MdPharmacistServiceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mdPharmacistServiceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药师管理")
    @ApiOperation("新增药师管理")
    @PreAuthorize("@el.check('admin','mdPharmacistService:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MdPharmacistService resources){
        return new ResponseEntity<>(mdPharmacistServiceService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药师管理")
    @ApiOperation("修改药师管理")
    @PreAuthorize("@el.check('admin','mdPharmacistService:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MdPharmacistService resources){
        mdPharmacistServiceService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药师管理")
    @ApiOperation("删除药师管理")
    @PreAuthorize("@el.check('admin','mdPharmacistService:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mdPharmacistServiceService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file) {
        String result = "";
        log.info("药师批量批量上载====================");
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

            result = mdPharmacistServiceService.uploadPharmacist(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(StrUtil.isNotBlank(result)) {
            throw new BadRequestException(result);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
