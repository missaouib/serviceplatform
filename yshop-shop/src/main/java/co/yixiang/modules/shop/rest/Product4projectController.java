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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.dto.Product4projectListDTO;
import co.yixiang.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.Product4project;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.dto.Product4projectQueryCriteria;
import co.yixiang.modules.shop.service.dto.Product4projectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_FORMAT;

/**
* @author visa
* @date 2020-11-09
*/

@Api(tags = "项目-药品配置管理")
@RestController
@RequestMapping("/api/product4project")
@Slf4j
public class Product4projectController {

    @Autowired
    private  Product4projectService product4projectService;

    @Autowired
    private  IGenerator generator;

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','product4project:list')")
    public void download(HttpServletResponse response, Product4projectQueryCriteria criteria) throws IOException {
        criteria.setIsDel(0);
        product4projectService.download(generator.convert(product4projectService.queryAll(criteria), Product4projectDto.class), response);
    }

    @GetMapping
    @Log("查询项目-药品配置")
    @ApiOperation("查询项目-药品配置")
    //@PreAuthorize("@el.check('admin','product4project:list')")
    @AnonymousAccess
    public ResponseEntity<Object> getProduct4projects(Product4projectQueryCriteria criteria, Pageable pageable){
        criteria.setIsDel(0);
        return new ResponseEntity<>(product4projectService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增项目-药品配置")
    @ApiOperation("新增项目-药品配置")
    @PreAuthorize("@el.check('admin','product4project:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Product4project resources){
        if(StrUtil.isBlank(resources.getGroupName())) {
            resources.setGroupName("");
        }
        return new ResponseEntity<>(product4projectService.save(resources),HttpStatus.CREATED);
    }

    @PostMapping("/addList")
    @Log("新增项目-药品配置")
    @ApiOperation("新增项目-药品配置")
    @AnonymousAccess
    public ResponseEntity<Object> addList(@Validated @RequestBody Product4projectListDTO resources){
        String projectCode = product4projectService.saveList(resources);
        yxStoreProductService.dualProuctDisease2Redis(projectCode);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping
    @Log("修改项目-药品配置")
    @ApiOperation("修改项目-药品配置")
    @PreAuthorize("@el.check('admin','product4project:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Product4project resources){
        if(StrUtil.isBlank(resources.getGroupName())) {
            resources.setGroupName("");
        }
        product4projectService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除项目-药品配置")
    @ApiOperation("删除项目-药品配置")
    @PreAuthorize("@el.check('admin','product4project:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            product4projectService.updateProductDelFlag(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "") String projectNo) {
        int count = 0;
        log.info("项目商品批量上载开始====================");
      //  String uesrname = SecurityUtils.getUsername();
        String fileName = filePath  +  "project_" + projectNo + "_" + DateUtil.format(DateUtil.date(),PURE_DATETIME_FORMAT) + "_" + file.getOriginalFilename();
        try {
            FileUtil.writeFromStream(file.getInputStream(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

            count = product4projectService.upload(readAll,projectNo);

            // 更新病种缓存
            yxStoreProductService.dualProuctDisease2Redis(projectNo);

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("项目商品批量上载结束,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }
}
