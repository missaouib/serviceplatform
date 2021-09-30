/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.taibao.domain.TbDiseaseCode;
import co.yixiang.modules.taibao.service.TbDiseaseCodeService;
import co.yixiang.modules.taibao.service.dto.TbDiseaseCodeDto;
import co.yixiang.modules.taibao.service.dto.TbDiseaseCodeQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbDiseaseCodeMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author zhoujinlai
* @date 2021-05-08
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "tbDiseaseCode")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbDiseaseCodeServiceImpl extends BaseServiceImpl<TbDiseaseCodeMapper, TbDiseaseCode> implements TbDiseaseCodeService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbDiseaseCodeQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbDiseaseCode> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbDiseaseCodeDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbDiseaseCode> queryAll(TbDiseaseCodeQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(TbDiseaseCode.class, criteria);
        if(StringUtils.isNotBlank(criteria.getName())){
            queryWrapper.apply(" (name like concat('%',{0} ,'%') or code like concat('%',{1} ,'%') )",criteria.getName(),criteria.getName());
        }
        List<TbDiseaseCode> yxStoreProductList = baseMapper.selectList(queryWrapper);
        return yxStoreProductList;
    }


    @Override
    public void download(List<TbDiseaseCodeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbDiseaseCodeDto tbDiseaseCode : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("ICD10疾病代码", tbDiseaseCode.getCode());
            map.put("疾病诊断名称", tbDiseaseCode.getName());
            map.put("代码分类01意外02疾病", tbDiseaseCode.getCodeClass());
            map.put(" createTime",  tbDiseaseCode.getCreateTime());
            map.put(" updateTime",  tbDiseaseCode.getUpdateTime());
            map.put(" delFlag",  tbDiseaseCode.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
