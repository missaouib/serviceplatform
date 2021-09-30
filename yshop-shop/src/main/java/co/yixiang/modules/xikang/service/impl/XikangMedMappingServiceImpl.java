/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.xikang.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.xikang.domain.XikangMedMapping;
import co.yixiang.common.service.impl.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.xikang.service.XikangMedMappingService;
import co.yixiang.modules.xikang.service.dto.XikangMedMappingDto;
import co.yixiang.modules.xikang.service.dto.XikangMedMappingQueryCriteria;
import co.yixiang.modules.xikang.service.mapper.XikangMedMappingMapper;
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
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-02-02
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "xikangMedMapping")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class XikangMedMappingServiceImpl extends BaseServiceImpl<XikangMedMappingMapper, XikangMedMapping> implements XikangMedMappingService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(XikangMedMappingQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<XikangMedMapping> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), XikangMedMappingDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<XikangMedMapping> queryAll(XikangMedMappingQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(XikangMedMapping.class, criteria));
    }


    @Override
    public void download(List<XikangMedMappingDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (XikangMedMappingDto xikangMedMapping : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("熙康商品编码", xikangMedMapping.getXikangCode());
            map.put("益药商品编码", xikangMedMapping.getYiyaobaoSku());

            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public int uploadMapping(List<Map<String, Object>> list) {
        List<XikangMedMapping> xikangMedMappingArrayList = new ArrayList<>();

        for(Map<String,Object> data : list) {

            String yiyaobao_sku = "";
            String xikang_code = "";
            Object sku_Object = data.get("益药商品编码");
            if(ObjectUtil.isNotEmpty(sku_Object)) {
                yiyaobao_sku = String.valueOf(sku_Object);
            } else {
                throw  new BadRequestException("益药商品编码不能为空");
            }

            Object xikang_code_Object = data.get("熙康商品编码");
            if(ObjectUtil.isNotEmpty(xikang_code_Object)) {
                xikang_code = String.valueOf(xikang_code_Object);
            } else {
                throw  new BadRequestException("熙康商品编码不能为空");
            }

            LambdaQueryWrapper<XikangMedMapping> queryWrapper = new LambdaQueryWrapper<XikangMedMapping>();

            queryWrapper.eq(XikangMedMapping::getYiyaobaoSku,yiyaobao_sku);
            XikangMedMapping xikangMedMapping = this.getOne(queryWrapper);

            if(xikangMedMapping == null) {
                xikangMedMapping = new XikangMedMapping();
            }

            xikangMedMapping.setYiyaobaoSku(yiyaobao_sku);
            xikangMedMapping.setXikangCode(xikang_code);
            xikangMedMappingArrayList.add(xikangMedMapping);
        }
        this.saveOrUpdateBatch(xikangMedMappingArrayList) ;
        return xikangMedMappingArrayList.size();
    }
}
