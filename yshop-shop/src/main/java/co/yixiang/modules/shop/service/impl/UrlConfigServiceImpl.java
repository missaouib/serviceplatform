/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.UrlConfig;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.UrlConfigService;
import co.yixiang.modules.shop.service.dto.UrlConfigDto;
import co.yixiang.modules.shop.service.dto.UrlConfigQueryCriteria;
import co.yixiang.modules.shop.service.mapper.UrlConfigMapper;
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
* @date 2020-06-10
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "urlConfig")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UrlConfigServiceImpl extends BaseServiceImpl<UrlConfigMapper, UrlConfig> implements UrlConfigService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(UrlConfigQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UrlConfig> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), UrlConfigDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<UrlConfig> queryAll(UrlConfigQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(UrlConfig.class, criteria));
    }


    @Override
    public void download(List<UrlConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UrlConfigDto urlConfig : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("url", urlConfig.getUrl());
            map.put("图片地址", urlConfig.getImage());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
