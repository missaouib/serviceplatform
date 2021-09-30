/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import co.yixiang.modules.bi.domain.BiMtdDataPatientProvince;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.bi.domain.BiMtdDataPrescriptionSource;
import co.yixiang.modules.bi.service.dto.BiDataCycleDto;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiMtdDataPatientProvinceService;
import co.yixiang.modules.bi.service.dto.BiMtdDataPatientProvinceDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataPatientProvinceQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiMtdDataPatientProvinceMapper;
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
//@CacheConfig(cacheNames = "biMtdDataPatientProvince")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiMtdDataPatientProvinceServiceImpl extends BaseServiceImpl<BiMtdDataPatientProvinceMapper, BiMtdDataPatientProvince> implements BiMtdDataPatientProvinceService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiMtdDataPatientProvinceQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiMtdDataPatientProvince> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiMtdDataPatientProvinceDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiMtdDataPatientProvince> queryAll(BiMtdDataPatientProvinceQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiMtdDataPatientProvince.class, criteria));
    }


    @Override
    public void download(List<BiMtdDataPatientProvinceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiMtdDataPatientProvinceDto biMtdDataPatientProvince : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" infodate",  biMtdDataPatientProvince.getInfodate());
            map.put(" provinceName",  biMtdDataPatientProvince.getProvinceName());
            map.put(" qty",  biMtdDataPatientProvince.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<BiDataCycleDto> queryPatient4province() {
        List<BiMtdDataPatientProvince> sourceList = list();
        List<BiDataCycleDto> dtoList = new ArrayList<>();
        for( BiMtdDataPatientProvince source : sourceList) {
            BiDataCycleDto dto = new BiDataCycleDto();
            dto.setName(source.getProvinceName());
            dto.setValue( new BigDecimal(source.getQty()));
            dtoList.add(dto);
        }
        return dtoList;
    }
}
