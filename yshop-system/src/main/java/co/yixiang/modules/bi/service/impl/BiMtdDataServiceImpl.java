/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import cn.hutool.core.collection.CollUtil;
import co.yixiang.modules.bi.domain.BiMtdData;
import co.yixiang.common.service.impl.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiMtdDataService;
import co.yixiang.modules.bi.service.dto.BiMtdDataDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiMtdDataMapper;
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
//@CacheConfig(cacheNames = "biMtdData")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiMtdDataServiceImpl extends BaseServiceImpl<BiMtdDataMapper, BiMtdData> implements BiMtdDataService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiMtdDataQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiMtdData> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiMtdDataDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiMtdData> queryAll(BiMtdDataQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiMtdData.class, criteria));
    }


    @Override
    public void download(List<BiMtdDataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiMtdDataDto biMtdData : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" sales",  biMtdData.getSales());
            map.put(" patients",  biMtdData.getPatients());
            map.put(" prescription",  biMtdData.getPrescription());
            map.put(" infodate",  biMtdData.getInfodate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public BiMtdData queryData() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("infodate");
        queryWrapper.last("limit 1");
        List<BiMtdData> biMtdDataList = list(queryWrapper);
        if(CollUtil.isNotEmpty(biMtdDataList)) {
            BiMtdData biMtdData = biMtdDataList.get(0);
            biMtdData.setSales( biMtdData.getSales().divide(new BigDecimal(10000),1,BigDecimal.ROUND_HALF_UP) );
            return biMtdDataList.get(0);
        }
        return null;
    }
}
