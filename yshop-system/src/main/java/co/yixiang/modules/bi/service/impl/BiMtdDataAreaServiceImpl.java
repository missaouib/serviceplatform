/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.bi.domain.BiMtdDataArea;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.bi.service.dto.BiDataAreaDto;
import co.yixiang.modules.bi.service.dto.BiDataAreaMappingDto;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.bi.service.BiMtdDataAreaService;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaDto;
import co.yixiang.modules.bi.service.dto.BiMtdDataAreaQueryCriteria;
import co.yixiang.modules.bi.service.mapper.BiMtdDataAreaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-09-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "biMtdDataArea")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BiMtdDataAreaServiceImpl extends BaseServiceImpl<BiMtdDataAreaMapper, BiMtdDataArea> implements BiMtdDataAreaService {

    private final IGenerator generator;

    private static Map<String,String> areaMap= new HashMap<>();

    static {
        areaMap.put("东区","上海");
        areaMap.put("江苏","江苏");
        areaMap.put("南区","江西,安徽,广东");
        areaMap.put("中区","湖南");
        areaMap.put("东北","黑龙江,吉林,辽宁,北京");
        areaMap.put("华北区","山东,山西,河北");
        areaMap.put("浙江区","浙江");
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(BiMtdDataAreaQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<BiMtdDataArea> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), BiMtdDataAreaDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<BiMtdDataArea> queryAll(BiMtdDataAreaQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(BiMtdDataArea.class, criteria));
    }


    @Override
    public void download(List<BiMtdDataAreaDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BiMtdDataAreaDto biMtdDataArea : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("业务大区名称", biMtdDataArea.getAreaname());
            map.put("销售额", biMtdDataArea.getAmount());
            map.put("callcenter呼入量", biMtdDataArea.getCallin());
            map.put("日期", biMtdDataArea.getInfoDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<BiDataAreaDto> queryBiMtdDataArea() {

        List<BiMtdDataArea> biMtdDataAreaDtoList = list();
        List<BiDataAreaDto> list = new ArrayList<>();
        for( int i= 0;i< biMtdDataAreaDtoList.size();i++){
            BiMtdDataArea biMtdDataArea = biMtdDataAreaDtoList.get(i);
            String areaName = biMtdDataArea.getAreaname();
            String provinceNames = areaMap.get(areaName);
            if(StrUtil.isNotBlank(provinceNames)) {
                for(String provinceName: provinceNames.split(",")) {
                    BiDataAreaDto biDataAreaDto = new BiDataAreaDto();
                    biDataAreaDto.setName(provinceName);
                    biDataAreaDto.setValue(i+1);
                    biDataAreaDto.setValue2(areaName);
                    biDataAreaDto.setValue3("销售额：" + biMtdDataArea.getAmount() + " 元");

                    list.add(biDataAreaDto);
                }
            }
        }


        return list;
    }

    @Override
    public List<BiDataAreaMappingDto> queryBiDataAreaMapping() {
        List<BiDataAreaMappingDto> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : areaMap.entrySet()) {
            BiDataAreaMappingDto biDataAreaMappingDto = new BiDataAreaMappingDto();
            biDataAreaMappingDto.setName(entry.getKey());
            biDataAreaMappingDto.setArr(Arrays.asList(entry.getValue().split(",")));
            list.add(biDataAreaMappingDto);
        }

        return list;
    }
}
