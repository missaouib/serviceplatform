/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import co.yixiang.modules.bi.domain.BiMtdDataPrescriptionSource;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.bi.service.dto.BiDataCycleDto;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiMtdDataPrescriptionSourceService;
import co.yixiang.modules.bi.service.dto.BiMtdDataPrescriptionSourceDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataPrescriptionSourceQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiMtdDataPrescriptionSourceMapper;
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
* @date 2020-10-13
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "biMtdDataPrescriptionSource")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiMtdDataPrescriptionSourceServiceImpl extends BaseServiceImpl<BiMtdDataPrescriptionSourceMapper, BiMtdDataPrescriptionSource> implements BiMtdDataPrescriptionSourceService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiMtdDataPrescriptionSourceQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiMtdDataPrescriptionSource> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiMtdDataPrescriptionSourceDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiMtdDataPrescriptionSource> queryAll(BiMtdDataPrescriptionSourceQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiMtdDataPrescriptionSource.class, criteria));
    }


    @Override
    public void download(List<BiMtdDataPrescriptionSourceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiMtdDataPrescriptionSourceDto biMtdDataPrescriptionSource : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("日期，格式：yyyy-mm", biMtdDataPrescriptionSource.getInfodate());
            map.put("来源名称", biMtdDataPrescriptionSource.getSource());
            map.put("处方量", biMtdDataPrescriptionSource.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<BiDataCycleDto> queryPrescriptionSource() {
        List<BiMtdDataPrescriptionSource> sourceList = list();
        List<BiDataCycleDto> dtoList = new ArrayList<>();
        for( BiMtdDataPrescriptionSource source : sourceList) {
            BiDataCycleDto dto = new BiDataCycleDto();
            dto.setName(source.getSource());
            dto.setValue( new BigDecimal(source.getQty()));
            dtoList.add(dto);
        }
        return dtoList;
    }
}
