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
import co.yixiang.modules.yaolian.service.YaolianServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.yaolian.domain.YaolianOrder;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderQueryCriteria;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDto;
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
@RequestMapping("/api/yaolianOrder")
public class YaolianOrderController {

    private final YaolianOrderService yaolianOrderService;
    private final IGenerator generator;

    private final YaolianServiceImpl yaolianService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','yaolianOrder:list')")
    public void download(HttpServletResponse response, YaolianOrderQueryCriteria criteria) throws IOException {
        yaolianOrderService.download(generator.convert(yaolianOrderService.queryAll(criteria), YaolianOrderDto.class), response);
    }

    @GetMapping
    @Log("查询药联接口")
    @ApiOperation("查询药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrder:list')")
    public ResponseEntity<Object> getYaolianOrders(YaolianOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(yaolianOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增药联接口")
    @ApiOperation("新增药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YaolianOrder resources){
        return new ResponseEntity<>(yaolianOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改药联接口")
    @ApiOperation("修改药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YaolianOrder resources){
        yaolianOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除药联接口")
    @ApiOperation("删除药联接口")
    @PreAuthorize("@el.check('admin','yaolianOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            yaolianOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/syncData")
    @Log("药联同步数据")
    @ApiOperation("药联同步数据")
    public ResponseEntity<Object> syncData(){
        return new ResponseEntity<>(yaolianService.syncData(),HttpStatus.CREATED);
    }
}
