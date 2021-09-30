/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.rest;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.api.common.ApiResult;
import co.yixiang.modules.bi.domain.BiDataMedicinesSales;
import co.yixiang.modules.bi.domain.BiMtdDataArea;
import co.yixiang.modules.bi.domain.BiMtdDataDrugstore;
import co.yixiang.modules.bi.service.*;
import co.yixiang.modules.bi.service.dto.BiDataAreaDto;
import co.yixiang.modules.bi.service.dto.BiDataAreaMappingDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaQueryCriteria;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
* @author visa
* @date 2020-09-28
*/
@AllArgsConstructor
@Api(tags = "BI数据管理")
@RestController
@RequestMapping("/api/bi")
public class BiController {

    private final BiMtdDataAreaService biMtdDataAreaService;
    private final IGenerator generator;

    private final BiMtdDataMedicinesService biMtdDataMedicinesService;

    private final BiMtdDataDrugstoreService biMtdDataDrugstoreService;

    private final BiMtdDataPrescriptionSourceService biMtdDataPrescriptionSourceService;

    private final BiMtdDataPatientProvinceService biMtdDataPatientProvinceService;

    private final BiDataMedicinesSalesService biDataMedicinesSalesService;

    private final BiMtdDataService biMtdDataService;

    @GetMapping(value = "/area")
    @ApiOperation("查询大区数据")
    @AnonymousAccess
    public ApiResult<Object> getDataAreas(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){
        List<BiDataAreaDto> a = biMtdDataAreaService.queryBiMtdDataArea();
        List<BiDataAreaMappingDto> b = biMtdDataAreaService.queryBiDataAreaMapping();
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("areaDataArray",a);
        jsonObject.put("areaMappingArray",b);
        return ApiResult.ok(jsonObject);

    }


    @GetMapping(value = "/medicines")
    @ApiOperation("查询药品数据")
    @AnonymousAccess
    public ApiResult<Object> getDataMedicines(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){
        return ApiResult.ok(biMtdDataMedicinesService.queryBiMtdDataMedicines());

    }

    @GetMapping(value = "/drugstore")
    @ApiOperation("查询药房数据")
    @AnonymousAccess
    public ApiResult<Object> getDataDrugstore(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){
        return ApiResult.ok(biMtdDataDrugstoreService.queryBiMtdDataDrugstore());

    }

    @GetMapping(value = "/drugstore4table")
    @ApiOperation("查询药房数据")
    @AnonymousAccess
    public ApiResult<Object> getDataDrugstore4table(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("amount");

        List<BiMtdDataDrugstore> biMtdDataDrugstoreList = biMtdDataDrugstoreService.list(queryWrapper);

        for( BiMtdDataDrugstore biMtdDataDrugstore : biMtdDataDrugstoreList) {
            biMtdDataDrugstore.setAmount(biMtdDataDrugstore.getAmount().divide(new BigDecimal(1000),1,BigDecimal.ROUND_HALF_UP));
        }

        return ApiResult.ok(biMtdDataDrugstoreList);

    }

    @GetMapping(value = "/prescription4source")
    @ApiOperation("查询处方来源")
    @AnonymousAccess
    public ApiResult<Object> getDataprescription4source(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){

        return ApiResult.ok(biMtdDataPrescriptionSourceService.queryPrescriptionSource());

    }

    @GetMapping(value = "/patient4province")
    @ApiOperation("查询患者分布")
    @AnonymousAccess
    public ApiResult<Object> getDataPatient4province(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){

        return ApiResult.ok(biMtdDataPatientProvinceService.queryPatient4province());

    }

    @GetMapping(value = "/sales4month")
    @ApiOperation("查询药品销售额趋势")
    @AnonymousAccess
    public ApiResult<Object> getDataSales4month(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){

        return ApiResult.ok(biDataMedicinesSalesService.querySales4month());

    }

    @GetMapping(value = "/mtd")
    @ApiOperation("查询mtd核心数据")
    @AnonymousAccess
    public ApiResult<Object> getDataMtd(BiMtdDataAreaQueryCriteria criteria, Pageable pageable){

        return ApiResult.ok(biMtdDataService.queryData());

    }

}
