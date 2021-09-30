/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaolian.domain.YaolianOrderDetail;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDetailQueryCriteria;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDetailDto;
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
* @date 2021-03-02
*/
@AllArgsConstructor
@Api(tags = "药联接口管理")
@RestController
@RequestMapping("/api/yaolianOrderDetail")
public class YaolianOrderDetailController {

    private final YaolianOrderDetailService yaolianOrderDetailService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaolianOrderDetail:list')")
    public void download(HttpServletResponse response, YaolianOrderDetailQueryCriteria criteria) throws IOException {
        yaolianOrderDetailService.download(generator.convert(yaolianOrderDetailService.queryAll(criteria), YaolianOrderDetailDto.class), response);
    }

    @GetMapping
    @Log("查询药联接口")
    @ApiOperation("查询药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrderDetail:list')")
    public ResponseEntity<Object> getYaolianOrderDetails(YaolianOrderDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaolianOrderDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药联接口")
    @ApiOperation("新增药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrderDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaolianOrderDetail resources){
        return new ResponseEntity<>(yaolianOrderDetailService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药联接口")
    @ApiOperation("修改药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrderDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaolianOrderDetail resources){
        yaolianOrderDetailService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药联接口")
    @ApiOperation("删除药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrderDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaolianOrderDetailService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
