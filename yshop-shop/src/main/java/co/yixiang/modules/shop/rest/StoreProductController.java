/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseDto;
import co.yixiang.modules.shop.service.dto.YxStoreDiseaseQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryCriteria;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_FORMAT;

/**
* @author hupeng
* @date 2019-10-04
*/
@Api(tags = "商城:商品管理")
@RestController
@RequestMapping("api")
@Slf4j
public class StoreProductController {

    private final YxStoreProductService yxStoreProductService;

    @Value("${file.path}")
    private String filePath;

    public StoreProductController(YxStoreProductService yxStoreProductService) {
        this.yxStoreProductService = yxStoreProductService;
    }

    @Log("查询商品")
    @ApiOperation(value = "查询商品")
    @GetMapping(value = "/yxStoreProduct")
    //@PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_SELECT')")
    @AnonymousAccess
    public ResponseEntity getYxStoreProducts(YxStoreProductQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(yxStoreProductService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增商品")
    @ApiOperation(value = "新增商品")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/yxStoreProduct")
    @PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_CREATE')")
    public ResponseEntity create(@Validated @RequestBody YxStoreProduct resources){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        resources.setAddTime(OrderUtil.getSecondTimestampTwo());
        if(ObjectUtil.isEmpty(resources.getGiveIntegral())) resources.setGiveIntegral(BigDecimal.ZERO);
        if(ObjectUtil.isEmpty(resources.getCost())) resources.setCost(BigDecimal.ZERO);
        return new ResponseEntity(yxStoreProductService.updateProduct(resources),HttpStatus.CREATED);
    }

    @Log("修改商品")
    @ApiOperation(value = "修改商品")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PutMapping(value = "/yxStoreProduct")
    @PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_EDIT')")
    public ResponseEntity update(@Validated @RequestBody YxStoreProduct resources){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        yxStoreProductService.updateProduct(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @Log("修改商品价格")
    @ApiOperation(value = "修改商品价格")
    @PostMapping(value = "/yxStoreProduct/price")
    public ResponseEntity updatePrice(@Validated @RequestBody YxStoreProduct resources){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        yxStoreProductService.updatePrice(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除商品")
    @ApiOperation(value = "删除商品")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @DeleteMapping(value = "/yxStoreProduct/{id}")
    @PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        yxStoreProductService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "恢复数据")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @DeleteMapping(value = "/yxStoreProduct/recovery/{id}")
    @PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_DELETE')")
    public ResponseEntity recovery(@PathVariable Integer id){
        yxStoreProductService.recovery(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "商品上架/下架")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/yxStoreProduct/onsale/{id}")
    public ResponseEntity onSale(@PathVariable Integer id,@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        int status = Integer.valueOf(jsonObject.get("status").toString());
        yxStoreProductService.onSale(id,status);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "生成属性")
    @PostMapping(value = "/yxStoreProduct/isFormatAttr/{id}")
    public ResponseEntity isFormatAttr(@PathVariable Integer id,@RequestBody String jsonStr){
        return new ResponseEntity(yxStoreProductService.isFormatAttr(id,jsonStr),HttpStatus.OK);
    }

    @ApiOperation(value = "设置保存属性")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/yxStoreProduct/setAttr/{id}")
    public ResponseEntity setAttr(@PathVariable Integer id,@RequestBody String jsonStr){
        yxStoreProductService.createProductAttr(id,jsonStr);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "清除属性")
    @CacheEvict(cacheNames = ShopConstants.YSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/yxStoreProduct/clearAttr/{id}")
    public ResponseEntity clearAttr(@PathVariable Integer id){
        yxStoreProductService.clearProductAttr(id,true);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "获取属性")
    @GetMapping(value = "/yxStoreProduct/attr/{id}")
    public ResponseEntity attr(@PathVariable Integer id){
        String str = yxStoreProductService.getStoreProductAttrResult(id);
        if(StrUtil.isEmpty(str)){
            return new ResponseEntity(HttpStatus.OK);
        }
        JSONObject jsonObject = JSON.parseObject(str);

        return new ResponseEntity(jsonObject,HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/yxStoreProduct/upload")
   // @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file,@RequestParam(defaultValue = "") String projectCode) {
        String uesrname = SecurityUtils.getUsername();
        int count = 0;
        log.info("商品批量上载开始====================");
        String fileName = filePath +  uesrname + "_product_" + projectCode +"_" + DateUtil.format(DateUtil.date(),PURE_DATETIME_FORMAT) + "_" + file.getOriginalFilename();
        try {
            FileUtil.writeFromStream(file.getInputStream(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();
            if(ProjectNameEnum.LINGYUANZHI.getValue().equals(projectCode)) {
                 yxStoreProductService.uploadProduct4Lingyuanzhi(readAll,projectCode);
            }else if(StrUtil.isNotBlank(projectCode)) {
                 count = yxStoreProductService.uploadProduct4Project2(readAll,projectCode);
            } else {
                 count = yxStoreProductService.uploadProduct(readAll);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("商品批量上载结束,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("查询商品")
    @ApiOperation(value = "查询商品")
    @GetMapping(value = "/yxStoreProduct/pc")
    //@PreAuthorize("@el.check('admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_SELECT')")
    @AnonymousAccess
    public ResponseEntity getYxStoreProducts4PC(YxStoreProductQueryCriteria criteria, Pageable pageable){
      /*  if(StringUtils.isEmpty(criteria.getProjectCode())){
            criteria.setProjectCode("msh");
        }*/
        return new ResponseEntity(yxStoreProductService.queryAll4pc(criteria,pageable),HttpStatus.OK);
    }


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxStoreProduct/download")
    public void download(HttpServletResponse response, YxStoreProductQueryCriteria criteria) throws IOException {
        if(StrUtil.isNotBlank(criteria.getProjectCode())) {
            yxStoreProductService.download(yxStoreProductService.queryAll(criteria),response,criteria.getProjectCode());
        } else {
            yxStoreProductService.downloadCommon(yxStoreProductService.queryAll(criteria),response);
        }

    }


    @Log("导出模板数据")
    @ApiOperation("导出模板数据")
    @GetMapping(value = "/yxStoreProduct/downloadSample")
    public void downloadSample(HttpServletResponse response, YxStoreProductQueryCriteria criteria) throws IOException {
        if(StrUtil.isNotBlank(criteria.getProjectCode())) {
            yxStoreProductService.downloadSample(yxStoreProductService.queryAll(criteria),response,criteria.getProjectCode());
        }

    }


    @GetMapping(value = "/yxStoreProduct/test3")
    @AnonymousAccess
    public ResponseEntity test3(HttpServletResponse response, YxStoreProductQueryCriteria criteria) throws IOException {

        return new ResponseEntity(yxStoreProductService.dualProuctDisease2Redis(criteria.getProjectCode()),HttpStatus.OK);

    }


    @Log("同步ebs库存")
    @ApiOperation("同步ebs库存")
    @GetMapping(value = "/yxStoreProduct/syncEBSProductStockBySku")
    public ResponseEntity<Object> syncEBSProductStockBySku(YxStoreProductQueryCriteria criteria) throws IOException {
        if(StrUtil.isNotBlank(criteria.getYiyaobaoSku())) {
            yxStoreProductService.syncEBSProductStockBySku(criteria);
        }else{
            throw new BadRequestException("药品SKU不能为空。");
        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("status", HttpStatus.OK.value());
        map.put("message", "");
        return new ResponseEntity(map,HttpStatus.OK);
    }


    @ApiOperation("上传商品组合文件")
    @PostMapping(value = "/yxStoreProduct/uploadGroup")
    // @AnonymousAccess
    public ResponseEntity<Object> uploadGroup(@RequestParam(defaultValue = "") String name, @RequestParam("file") MultipartFile file,@RequestParam(defaultValue = "") String projectCode) {
        String uesrname = SecurityUtils.getUsername();
        int count = 0;
        log.info("商品批量上载开始====================");
        String fileName = filePath +  uesrname + "_productGroup_" + projectCode +"_" + DateUtil.format(DateUtil.date(),PURE_DATETIME_FORMAT) + "_" + file.getOriginalFilename();
        try {
            FileUtil.writeFromStream(file.getInputStream(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String,Object>> readAll = reader.readAll();

            count = yxStoreProductService.uploadProductGroup(readAll);

        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        log.info("商品批量上载结束,更新条数[{}]====================",count);
        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("导出组合子商品数据")
    @ApiOperation("导出组合子商品数据")
    @GetMapping(value = "/yxStoreProduct/downloadProductGroup")
    public void downloadProductGroup(HttpServletResponse response, YxStoreProductQueryCriteria criteria) throws IOException {

        yxStoreProductService.downloadProductGroup(yxStoreProductService.queryAll(criteria),response,criteria.getProjectCode());


    }

}
