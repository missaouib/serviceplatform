/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.YxStoreProductGroup;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.YxStoreProductGroupService;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupQueryCriteria;
import co.yixiang.modules.shop.service.mapper.YxStoreProductGroupMapper;
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
* @date 2021-08-16
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreProductGroup")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreProductGroupServiceImpl extends BaseServiceImpl<YxStoreProductGroupMapper, YxStoreProductGroup> implements YxStoreProductGroupService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreProductGroupQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreProductGroup> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxStoreProductGroupDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreProductGroup> queryAll(YxStoreProductGroupQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxStoreProductGroup.class, criteria));
    }


    @Override
    public void download(List<YxStoreProductGroupDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreProductGroupDto yxStoreProductGroup : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("父商品sku", yxStoreProductGroup.getParentProductYiyaobaoSku());
            map.put("父商品id", yxStoreProductGroup.getParentProductId());
            map.put("商品sku", yxStoreProductGroup.getProductYiyaobaoSku());
            map.put("商品id", yxStoreProductGroup.getProductId());
            map.put("默认销售数量", yxStoreProductGroup.getNum());
            map.put("销售单价", yxStoreProductGroup.getUnitPrice());
            map.put(" createTime",  yxStoreProductGroup.getCreateTime());
            map.put(" updateTime",  yxStoreProductGroup.getUpdateTime());
            map.put("是否删除", yxStoreProductGroup.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
