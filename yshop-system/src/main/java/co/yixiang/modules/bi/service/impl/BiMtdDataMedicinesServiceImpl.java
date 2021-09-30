/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import co.yixiang.modules.bi.domain.BiMtdDataMedicines;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.bi.service.dto.BiDataHistogramDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiMtdDataMedicinesService;
import co.yixiang.modules.bi.service.dto.BiMtdDataMedicinesDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataMedicinesQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiMtdDataMedicinesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-10-12
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "biMtdDataMedicines")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiMtdDataMedicinesServiceImpl extends BaseServiceImpl<BiMtdDataMedicinesMapper, BiMtdDataMedicines> implements BiMtdDataMedicinesService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiMtdDataMedicinesQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiMtdDataMedicines> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiMtdDataMedicinesDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiMtdDataMedicines> queryAll(BiMtdDataMedicinesQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiMtdDataMedicines.class, criteria));
    }


    @Override
    public void download(List<BiMtdDataMedicinesDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiMtdDataMedicinesDto biMtdDataMedicines : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("药品名称", biMtdDataMedicines.getMedName());
            map.put("通用名", biMtdDataMedicines.getCommonName());
            map.put("规格", biMtdDataMedicines.getSpec());
            map.put("单位", biMtdDataMedicines.getUnit());
            map.put("厂家", biMtdDataMedicines.getManufacturer());
            map.put("销售额", biMtdDataMedicines.getAmount());
            map.put("销量", biMtdDataMedicines.getQty());
            map.put("日期 2019-01", biMtdDataMedicines.getInfodate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public BiDataHistogramDto queryBiMtdDataMedicines() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("amount");
        List<BiMtdDataMedicines> biMtdDataMedicinesList = list(queryWrapper);
        List<String> nameList = new ArrayList<>();
        List<BigDecimal> valueList = new ArrayList<>();
        for(BiMtdDataMedicines biMtdDataMedicines : biMtdDataMedicinesList) {
            nameList.add(biMtdDataMedicines.getMedName());
            valueList.add(biMtdDataMedicines.getAmount());
        }

        BiDataHistogramDto biDataHistogramDto = new BiDataHistogramDto();
        biDataHistogramDto.setNameList(nameList);
        biDataHistogramDto.setValueList(valueList);
        return biDataHistogramDto;
    }



}
