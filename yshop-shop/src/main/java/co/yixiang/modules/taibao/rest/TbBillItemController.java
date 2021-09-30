/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.taibao.domain.TbBillItem;
import co.yixiang.modules.taibao.service.TbBillItemService;
import co.yixiang.modules.taibao.service.dto.TbBillItemDto;
import co.yixiang.modules.taibao.service.dto.TbBillItemQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@AllArgsConstructor
@Api(tags = "太保安联-收据信息汇总项目明细管理")
@RestController
@RequestMapping("/api/tbBillItem")
public class TbBillItemController {

    private final TbBillItemService tbBillItemService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbBillItem:list')")
    public void download(HttpServletResponse response, TbBillItemQueryCriteria criteria) throws IOException {
        tbBillItemService.download(generator.convert(tbBillItemService.queryAll(criteria), TbBillItemDto.class), response);
    }

    @GetMapping
    @Log("查询太保安联-收据信息汇总项目明细")
    @ApiOperation("查询太保安联-收据信息汇总项目明细")
    @PreAuthorize("@el.check('admin','tbBillItem:list')")
    public ResponseEntity<Object> getTbBillItems(TbBillItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbBillItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增太保安联-收据信息汇总项目明细")
    @ApiOperation("新增太保安联-收据信息汇总项目明细")
    @PreAuthorize("@el.check('admin','tbBillItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TbBillItem resources){
        return new ResponseEntity<>(tbBillItemService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改太保安联-收据信息汇总项目明细")
    @ApiOperation("修改太保安联-收据信息汇总项目明细")
    @PreAuthorize("@el.check('admin','tbBillItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TbBillItem resources){
        tbBillItemService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除太保安联-收据信息汇总项目明细")
    @ApiOperation("删除太保安联-收据信息汇总项目明细")
    @PreAuthorize("@el.check('admin','tbBillItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbBillItemService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
