/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import co.yixiang.modules.bi.domain.BiDataMedicinesSales;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.bi.domain.BiMtdDataDrugstore;
import co.yixiang.modules.bi.service.dto.BiDataHistogramDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiDataMedicinesSalesService;
import co.yixiang.modules.bi.service.dto.BiDataMedicinesSalesDto;
import co.yixiang.modules.bi.service.dto.BiDataMedicinesSalesQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiDataMedicinesSalesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
* @date 2020-10-14
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "biDataMedicinesSales")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiDataMedicinesSalesServiceImpl extends BaseServiceImpl<BiDataMedicinesSalesMapper, BiDataMedicinesSales> implements BiDataMedicinesSalesService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiDataMedicinesSalesQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiDataMedicinesSales> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiDataMedicinesSalesDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiDataMedicinesSales> queryAll(BiDataMedicinesSalesQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiDataMedicinesSales.class, criteria));
    }


    @Override
    public void download(List<BiDataMedicinesSalesDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiDataMedicinesSalesDto biDataMedicinesSales : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("日期 yyyy-mm", biDataMedicinesSales.getInfodate());
            map.put("销售额", biDataMedicinesSales.getAmount());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public BiDataHistogramDto querySales4month() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("infodate");
        queryWrapper.last(" limit 8");
        List<BiDataMedicinesSales> biMtdDataDrugstoreList = list(queryWrapper);
        List<String> nameList = new ArrayList<>();
        List<BigDecimal> valueList = new ArrayList<>();
        for(BiDataMedicinesSales biDataMedicinesSales : biMtdDataDrugstoreList) {
            nameList.add(biDataMedicinesSales.getInfodate());
            valueList.add(biDataMedicinesSales.getAmount().divide(new BigDecimal(10000),1,BigDecimal.ROUND_HALF_UP));
        }

        BiDataHistogramDto biDataHistogramDto = new BiDataHistogramDto();
        biDataHistogramDto.setNameList(nameList);
        biDataHistogramDto.setValueList(valueList);
        return biDataHistogramDto;
    }
}
