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
import co.yixiang.modules.shop.domain.EnterpriseTopics;
import co.yixiang.modules.shop.service.EnterpriseTopicsService;
import co.yixiang.modules.shop.service.dto.EnterpriseTopicsQueryCriteria;
import co.yixiang.modules.shop.service.dto.EnterpriseTopicsDto;
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
* @date 2020-06-05
*/
@AllArgsConstructor
@Api(tags = "企业信息管理")
@RestController
@RequestMapping("/api/enterpriseTopics")
public class EnterpriseTopicsController {

    private final EnterpriseTopicsService enterpriseTopicsService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','enterpriseTopics:list')")
    public void download(HttpServletResponse response, EnterpriseTopicsQueryCriteria criteria) throws IOException {
        enterpriseTopicsService.download(generator.convert(enterpriseTopicsService.queryAll(criteria), EnterpriseTopicsDto.class), response);
    }

    @GetMapping
    @Log("查询企业信息")
    @ApiOperation("查询企业信息")
    @PreAuthorize("@el.check('admin','enterpriseTopics:list')")
    public ResponseEntity<Object> getEnterpriseTopicss(EnterpriseTopicsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(enterpriseTopicsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增企业信息")
    @ApiOperation("新增企业信息")
    @PreAuthorize("@el.check('admin','enterpriseTopics:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody EnterpriseTopics resources){
        return new ResponseEntity<>(enterpriseTopicsService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改企业信息")
    @ApiOperation("修改企业信息")
    @PreAuthorize("@el.check('admin','enterpriseTopics:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody EnterpriseTopics resources){
        enterpriseTopicsService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除企业信息")
    @ApiOperation("删除企业信息")
    @PreAuthorize("@el.check('admin','enterpriseTopics:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            enterpriseTopicsService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
