/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientRelationService;
import co.yixiang.modules.yaoshitong.web.vo.YaoshitongPatientRelationDto;
import co.yixiang.modules.yaoshitong.web.param.YaoshitongPatientRelationQueryCriteria;
import co.yixiang.modules.yaoshitong.mapper.YaoshitongPatientRelationMapper;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatientRelation;
import co.yixiang.utils.FileUtil;
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
* @author visa
* @date 2020-07-13
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaoshitongPatientRelation")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongPatientRelationServiceImpl extends BaseServiceImpl<YaoshitongPatientRelationMapper, YaoshitongPatientRelation> implements YaoshitongPatientRelationService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongPatientRelationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaoshitongPatientRelation> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YaoshitongPatientRelationDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongPatientRelation> queryAll(YaoshitongPatientRelationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaoshitongPatientRelation.class, criteria));
    }


    @Override
    public void download(List<YaoshitongPatientRelationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongPatientRelationDto yaoshitongPatientRelation : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("药师id", yaoshitongPatientRelation.getPharmacistId());
            map.put("患者id", yaoshitongPatientRelation.getPatientId());
            map.put(" createTime",  yaoshitongPatientRelation.getCreateTime());
            map.put(" updateTime",  yaoshitongPatientRelation.getUpdateTime());
            map.put("是否删除", yaoshitongPatientRelation.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
