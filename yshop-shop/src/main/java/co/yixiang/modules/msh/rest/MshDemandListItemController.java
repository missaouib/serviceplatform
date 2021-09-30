/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.rest;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.msh.domain.MshDemandListItem;
import co.yixiang.modules.msh.service.MshDemandListItemService;
import co.yixiang.modules.msh.service.dto.MshDemandListItemDto;
import co.yixiang.modules.msh.service.dto.MshDemandListItemQueryCriteria;
import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

/**
* @author cq
* @date 2020-12-25
*/
@AllArgsConstructor
@Api(tags = "需求单详细管理")
@RestController
@RequestMapping("/api/mshDemandListItem")
public class MshDemandListItemController {

	@Autowired
    private final MshDemandListItemService mshDemandListItemService;

	@Autowired
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/list/download")
    public void download(HttpServletResponse response, MshDemandListItemQueryCriteria criteria) throws IOException {
        mshDemandListItemService.download(generator.convert(mshDemandListItemService.selectMshDemandListItemList(criteria), MshDemandListItemDto.class), response);
    }

    @GetMapping
    @Log("查询需求单详细")
    @ApiOperation("查询需求单详细")
    public ResponseEntity<Object> getMshDemandListItems(MshDemandListItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mshDemandListItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增需求单详细")
    @ApiOperation("新增需求单详细")
    public ResponseEntity<Object> create(@Validated @RequestBody MshDemandListItem resources){
        return new ResponseEntity<>(mshDemandListItemService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改需求单详细")
    @ApiOperation("修改需求单详细")
    public ResponseEntity<Object> update(@Validated @RequestBody MshDemandListItem resources){
        mshDemandListItemService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除需求单详细")
    @ApiOperation("删除需求单详细")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            mshDemandListItemService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询需求单详细列表")
    @ApiOperation("查询需求单详细列表")
    @GetMapping(value = "/list")
    public ResponseEntity<Object> getMshDemandListItemsList(MshDemandListItemQueryCriteria criteria, Pageable pageable){
    	SecurityUtils.getUsername();
        return new ResponseEntity<>(mshDemandListItemService.selectMshDemandListList(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询需求单详细生成订单用")
    @ApiOperation("查询需求单详细生成订单用")
    @PostMapping(value = "/listForMakeOrder")
    public ResponseEntity<Object> getMshDemandListItemsListForMakeOrder(@RequestBody Integer id){
        return new ResponseEntity<>(mshDemandListItemService.selectMshDemandListItemListForMakeOrder(id),HttpStatus.OK);
    }


    @Log("msh需求单附件批量打印")
    @ApiOperation("msh需求单附件批量打印")
    @PostMapping(value = "/convertImage")
    public ResponseEntity<Object> convertImage(@RequestBody Integer id){
        return new ResponseEntity<>(mshDemandListItemService.convertImage(id),HttpStatus.OK);
    }
}
