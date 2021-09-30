/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.mp.yiyaobao.rest;
import java.util.Arrays;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import lombok.AllArgsConstructor;

import co.yixiang.mp.yiyaobao.domain.OrderBatchnoDetail;
import co.yixiang.mp.yiyaobao.service.OrderBatchnoDetailService;
import co.yixiang.mp.yiyaobao.service.dto.OrderBatchnoDetailQueryCriteria;
import co.yixiang.mp.yiyaobao.service.dto.OrderBatchnoDetailDto;
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
* @date 2020-07-02
*/
@AllArgsConstructor
@Api(tags = "订单明细中药品批号管理")
@RestController
@RequestMapping("/api/orderBatchnoDetail")
public class OrderBatchnoDetailController {

    private final OrderBatchnoDetailService orderBatchnoDetailService;
    private final IGenerator generator;



    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','orderBatchnoDetail:list')")
    public void download(HttpServletResponse response, OrderBatchnoDetailQueryCriteria criteria) throws IOException {
        orderBatchnoDetailService.download(generator.convert(orderBatchnoDetailService.queryAll(criteria), OrderBatchnoDetailDto.class), response);
    }

    @GetMapping

    @ApiOperation("查询订单明细中药品批号")
    @PreAuthorize("@el.check('admin','orderBatchnoDetail:list')")
    public ResponseEntity<Object> getOrderBatchnoDetails(OrderBatchnoDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderBatchnoDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping

    @ApiOperation("新增订单明细中药品批号")
    @PreAuthorize("@el.check('admin','orderBatchnoDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderBatchnoDetail resources){
        return new ResponseEntity<>(orderBatchnoDetailService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping

    @ApiOperation("修改订单明细中药品批号")
    @PreAuthorize("@el.check('admin','orderBatchnoDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderBatchnoDetail resources){
        orderBatchnoDetailService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @ApiOperation("删除订单明细中药品批号")
    @AnonymousAccess
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            orderBatchnoDetailService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
