/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.EnterpriseTopics;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.EnterpriseTopicsService;
import co.yixiang.modules.shop.service.dto.EnterpriseTopicsDto;
import co.yixiang.modules.shop.service.dto.EnterpriseTopicsQueryCriteria;
import co.yixiang.modules.shop.service.mapper.EnterpriseTopicsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-06-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "enterpriseTopics")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class EnterpriseTopicsServiceImpl extends BaseServiceImpl<EnterpriseTopicsMapper, EnterpriseTopics> implements EnterpriseTopicsService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(EnterpriseTopicsQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<EnterpriseTopics> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), EnterpriseTopicsDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<EnterpriseTopics> queryAll(EnterpriseTopicsQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(EnterpriseTopics.class, criteria));
    }


    @Override
    public void download(List<EnterpriseTopicsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EnterpriseTopicsDto enterpriseTopics : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("企业名称", enterpriseTopics.getName());
            map.put("logo图片", enterpriseTopics.getLogo());
            map.put("企业介绍图片", enterpriseTopics.getImage());
            map.put("简介", enterpriseTopics.getSynopsis());
            map.put("长图文内容，信息活动", enterpriseTopics.getContent());
            map.put("添加时间", enterpriseTopics.getAddTime());
            map.put("是否删除", enterpriseTopics.getIsDel());
            map.put("是否显示 0/是 1/否", enterpriseTopics.getIsShow());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
