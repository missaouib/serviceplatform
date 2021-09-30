/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.YxExpressTemplateDetail;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxExpressTemplateDetailService;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDetailDto;
import co.yixiang.modules.shop.service.dto.YxExpressTemplateDetailQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxExpressTemplateDetailMapper;
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
* @date 2020-11-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxExpressTemplateDetail")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxExpressTemplateDetailServiceImpl extends BaseServiceImpl<YxExpressTemplateDetailMapper, YxExpressTemplateDetail> implements YxExpressTemplateDetailService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxExpressTemplateDetailQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxExpressTemplateDetail> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxExpressTemplateDetailDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxExpressTemplateDetail> queryAll(YxExpressTemplateDetailQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxExpressTemplateDetail.class, criteria));
    }


    @Override
    public void download(List<YxExpressTemplateDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxExpressTemplateDetailDto yxExpressTemplateDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("模板id", yxExpressTemplateDetail.getTemplateId());
            map.put("区域名称", yxExpressTemplateDetail.getAreaName());
            map.put("价格", yxExpressTemplateDetail.getPrice());
            map.put("记录生成时间", yxExpressTemplateDetail.getCreateTime());
            map.put("更新时间", yxExpressTemplateDetail.getUpdateTime());
            map.put("记录生成人", yxExpressTemplateDetail.getCreater());
            map.put("记录更新人", yxExpressTemplateDetail.getMaker());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
