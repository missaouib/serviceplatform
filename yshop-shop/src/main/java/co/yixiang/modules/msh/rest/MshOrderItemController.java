/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.util.Arrays;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshOrderItem;
import co.yixiang.modules.msh.service.MshOrderItemService;
import co.yixiang.modules.msh.service.dto.MshOrderItemQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshOrderItemDto;
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
* @author cq
* @date 2020-12-25
*/
@AllArgsConstructor
@Api(tags = "订单详情管理")
@RestController
@RequestMapping("/api/mshOrderItem")
public class MshOrderItemController {

    private final MshOrderItemService mshOrderItemService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, MshOrderItemQueryCriteria criteria) throws IOException {
        mshOrderItemService.download(generator.convert(mshOrderItemService.queryAll(criteria), MshOrderItemDto.class), response);
    }

    @GetMapping
    @Log("查询订单详情")
    @ApiOperation("查询订单详情")
    public ResponseEntity<Object> getMshOrderItems(MshOrderItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshOrderItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增订单详情")
    @ApiOperation("新增订单详情")
    public ResponseEntity<Object> create(@Validated @RequestBody MshOrderItem resources){
        return new ResponseEntity<>(mshOrderItemService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改订单详情")
    @ApiOperation("修改订单详情")
    public ResponseEntity<Object> update(@Validated @RequestBody MshOrderItem resources){
        mshOrderItemService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除订单详情")
    @ApiOperation("删除订单详情")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            mshOrderItemService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
