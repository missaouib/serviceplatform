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
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.ProjectSalesArea;
import co.yixiang.modules.shop.service.ProjectSalesAreaService;
import co.yixiang.modules.shop.service.dto.ProjectSalesAreaQueryCriteria;
import co.yixiang.modules.shop.service.dto.ProjectSalesAreaDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-04-09
*/
@AllArgsConstructor
@Api(tags = "项目配置销售省份管理")
@RestController
@RequestMapping("/api/projectSalesArea")
public class ProjectSalesAreaController {

    private final ProjectSalesAreaService projectSalesAreaService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, ProjectSalesAreaQueryCriteria criteria) throws IOException {
        projectSalesAreaService.download(generator.convert(projectSalesAreaService.queryAll(criteria), ProjectSalesAreaDto.class), response);
    }

    @GetMapping
    @Log("查询项目配置销售省份")
    @ApiOperation("查询项目配置销售省份")
    public ResponseEntity<Object> getProjectSalesAreas(ProjectSalesAreaQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(projectSalesAreaService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增项目配置销售省份")
    @ApiOperation("新增项目配置销售省份")
    public ResponseEntity<Object> create(@Validated @RequestBody ProjectSalesArea resources){
        return new ResponseEntity<>(projectSalesAreaService.save(resources),HttpStatus.CREATED);
    }

    @PostMapping("/addList/{projectCode}")
    @Log("新增项目配置销售省份")
    @ApiOperation("新增项目配置销售省份")
    public ResponseEntity<Object> createList(@Validated @RequestBody List<ProjectSalesArea> resources,@PathVariable String projectCode){

        return new ResponseEntity<>(projectSalesAreaService.saveList(resources,projectCode),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改项目配置销售省份")
    @ApiOperation("修改项目配置销售省份")
    public ResponseEntity<Object> update(@Validated @RequestBody ProjectSalesArea resources){
        projectSalesAreaService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除项目配置销售省份")
    @ApiOperation("删除项目配置销售省份")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            projectSalesAreaService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
