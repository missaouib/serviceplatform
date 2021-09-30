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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.service.dto.YxStoreCategoryDto;
import co.yixiang.utils.OrderUtil;
import lombok.AllArgsConstructor;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.modules.shop.service.YxStoreDiseaseService;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseDto;
import org.springframework.cache.annotation.CacheEvict;
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
* @date 2020-06-02
*/
@AllArgsConstructor
@Api(tags = "病种管理")
@RestController
@RequestMapping("/api/yxStoreDisease")
public class YxStoreDiseaseController {

    private final YxStoreDiseaseService yxStoreDiseaseService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, YxStoreDiseaseQueryCriteria criteria) throws IOException {

        yxStoreDiseaseService.downloadSimple(yxStoreDiseaseService.queryAllSimple(criteria), response);
    }

    @GetMapping
    @Log("查询病种")
    @ApiOperation("查询病种")
    public ResponseEntity<Object> getYxStoreDiseases(YxStoreDiseaseQueryCriteria criteria, Pageable pageable){

		List<YxStoreDiseaseDto> diseaseList = yxStoreDiseaseService.queryAll(criteria);

		for(YxStoreDiseaseDto dto:diseaseList) {
		    if(StrUtil.isNotBlank(dto.getCateType())) {
		        dto.setCateTypeList( Arrays.asList(dto.getCateType().split(",")));
            }
        }
        return new ResponseEntity<>(yxStoreDiseaseService.buildTree(diseaseList),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增病种")
    @ApiOperation("新增病种")
    public ResponseEntity<Object> create(@Validated @RequestBody YxStoreDisease resources){
        if(CollUtil.isNotEmpty(resources.getCateTypeList())) {
            resources.setCateType(CollUtil.join(resources.getCateTypeList(),","));
        }
        resources.setAddTime(OrderUtil.getSecondTimestampTwo());
        Integer checkedCount =  yxStoreDiseaseService.selectByYxStoreDisease(resources);
        if(checkedCount>0){
            throw new BadRequestException("同一个项目下二级分类不能同名");
        }
        return new ResponseEntity<>(yxStoreDiseaseService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改病种")
    @ApiOperation("修改病种")

    public ResponseEntity<Object> update(@Validated @RequestBody YxStoreDisease resources){
        if(CollUtil.isNotEmpty(resources.getCateTypeList())) {
            resources.setCateType(CollUtil.join(resources.getCateTypeList(),","));
        }
        Integer checkedCount =  yxStoreDiseaseService.selectByYxStoreDisease(resources);
        if(checkedCount>0){
            throw new BadRequestException("同一个项目下二级分类不能同名");
        }
        yxStoreDiseaseService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除病种")
    @ApiOperation("删除病种")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            yxStoreDiseaseService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Log("删除病种分类")
    @ApiOperation(value = "删除病种分类")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable String id){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        String[] ids = id.split(",");
        for (String newId: ids) {
            yxStoreDiseaseService.removeById(Integer.valueOf(newId));
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
