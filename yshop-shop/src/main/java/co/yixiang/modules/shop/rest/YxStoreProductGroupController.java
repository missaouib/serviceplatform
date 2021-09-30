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
import co.yixiang.modules.shop.domain.YxStoreProductGroup;
import co.yixiang.modules.shop.service.YxStoreProductGroupService;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupDto;
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
* @date 2021-08-16
*/
@AllArgsConstructor
@Api(tags = "商品组合管理")
@RestController
@RequestMapping("/api/yxStoreProductGroup")
public class YxStoreProductGroupController {

    private final YxStoreProductGroupService yxStoreProductGroupService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yxStoreProductGroup:list')")
    public void download(HttpServletResponse response, YxStoreProductGroupQueryCriteria criteria) throws IOException {
        yxStoreProductGroupService.download(generator.convert(yxStoreProductGroupService.queryAll(criteria), YxStoreProductGroupDto.class), response);
    }

    @GetMapping
    @Log("查询商品组合")
    @ApiOperation("查询商品组合")
    @PreAuthorize("@el.check('admin','yxStoreProductGroup:list')")
    public ResponseEntity<Object> getYxStoreProductGroups(YxStoreProductGroupQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yxStoreProductGroupService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增商品组合")
    @ApiOperation("新增商品组合")
    @PreAuthorize("@el.check('admin','yxStoreProductGroup:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YxStoreProductGroup resources){
        return new ResponseEntity<>(yxStoreProductGroupService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改商品组合")
    @ApiOperation("修改商品组合")
    @PreAuthorize("@el.check('admin','yxStoreProductGroup:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YxStoreProductGroup resources){
        yxStoreProductGroupService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除商品组合")
    @ApiOperation("删除商品组合")
    @PreAuthorize("@el.check('admin','yxStoreProductGroup:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxStoreProductGroupService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
