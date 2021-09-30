/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;

import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductAttr;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxStoreProductAttrValueService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.service.dto.YxStoreProductAttrValueDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductAttrValueQueryCriteria;
import co.yixiang.modules.shop.service.mapper.StoreProductAttrMapper;
import co.yixiang.modules.shop.service.mapper.StoreProductMapper;
import co.yixiang.modules.shop.service.mapper.YxStoreProductAttrValueMapper;
import co.yixiang.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2020-05-29
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreProductAttrValue")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreProductAttrValueServiceImpl extends BaseServiceImpl<YxStoreProductAttrValueMapper, YxStoreProductAttrValue> implements YxStoreProductAttrValueService {

    private final IGenerator generator;
    @Autowired
    private StoreProductAttrMapper storeProductAttrMapper;
    @Autowired
    private StoreProductMapper storeProductMapper;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreProductAttrValueQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreProductAttrValue> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<YxStoreProductAttrValueDto> dtoList = generator.convert(page.getList(), YxStoreProductAttrValueDto.class);

        for(YxStoreProductAttrValueDto yxStoreProductAttrValue:dtoList) {
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.select("store_name","common_name","yiyaobao_sku","spec","unit","manufacturer");
            queryWrapper1.eq("id",yxStoreProductAttrValue.getProductId());
            YxStoreProduct yxStoreProduct = storeProductMapper.selectOne(queryWrapper1);
            //   YxStoreProduct yxStoreProduct = storeProductMapper.selectById(yxStoreProductAttrValue.getProductId());
            if(yxStoreProduct!=null) {
                yxStoreProductAttrValue.setProductName(yxStoreProduct.getStoreName());
                yxStoreProductAttrValue.setCommonName(yxStoreProduct.getCommonName());
                yxStoreProductAttrValue.setYiyaobaoSku(yxStoreProduct.getYiyaobaoSku());
                yxStoreProductAttrValue.setSpec(yxStoreProduct.getSpec());
                yxStoreProductAttrValue.setUnit(yxStoreProduct.getUnit());
                yxStoreProductAttrValue.setManufacturer(yxStoreProduct.getManufacturer());
            }
        }

        map.put("content", dtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreProductAttrValue> queryAll(YxStoreProductAttrValueQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreProductAttrValue.class, criteria);
        if(StrUtil.isNotBlank(criteria.getProductName())) {
            queryWrapper.apply(" exists (select 1 from yx_store_product a where a.id = yx_store_product_attr_value.product_id and (a.store_name like concat('%',{0},'%') or a.common_name like concat('%',{1},'%')) )",criteria.getProductName(),criteria.getProductName());
        }

        /*if(StrUtil.isNotBlank(criteria.getYiyaobaoSku())) {
            queryWrapper.apply(" exists (select 1 from yx_store_product a where a.id = yx_store_product_attr_value.product_id and a.yiyaobao_sku like concat('%',{0},'%')  )",criteria.getYiyaobaoSku());
        }
*/
        if(StrUtil.isNotBlank(criteria.getStoreName()))  {
            queryWrapper.like("suk",criteria.getStoreName());
        }

        List<YxStoreProductAttrValue> result = baseMapper.selectList(queryWrapper);

        return result;
    }


    @Override
    public void download(List<YxStoreProductAttrValueDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreProductAttrValueDto yxStoreProductAttrValue : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商品ID", yxStoreProductAttrValue.getProductId());
            map.put("商品属性索引值 (attr_value|attr_value[|....])", yxStoreProductAttrValue.getSuk());
            map.put("属性对应的库存", yxStoreProductAttrValue.getStock());
            map.put("销量", yxStoreProductAttrValue.getSales());
            map.put("属性金额", yxStoreProductAttrValue.getPrice());
            map.put("图片", yxStoreProductAttrValue.getImage());
            map.put("唯一值", yxStoreProductAttrValue.getUnique());
            map.put("成本价", yxStoreProductAttrValue.getCost());
            map.put("药店id", yxStoreProductAttrValue.getStoreId());
            map.put("yx_store_product_attr.id", yxStoreProductAttrValue.getAttrId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean saveAttrValue(YxStoreProductAttrValue resources) {
        // 更新product_attr 数据
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("attr_name","药店");
        queryWrapper.eq("product_id",resources.getProductId());
        YxStoreProductAttr attr = storeProductAttrMapper.selectOne(queryWrapper);
        if(attr == null) {
            attr = new YxStoreProductAttr();
            attr.setProductId(resources.getProductId());
            attr.setAttrName("药店");
            attr.setAttrValues(resources.getSuk());
            storeProductAttrMapper.insert(attr);
        }
        // 获取药品的图片
        YxStoreProduct yxStoreProduct = storeProductMapper.selectOne( new LambdaQueryWrapper<YxStoreProduct>().eq(YxStoreProduct::getId,resources.getProductId()).select(YxStoreProduct::getYiyaobaoSku,YxStoreProduct::getImage));
        resources.setImage(yxStoreProduct.getImage());
        resources.setAttrId(attr.getId());
        resources.setYiyaobaoSku(yxStoreProduct.getYiyaobaoSku());
        YxSystemStore yxSystemStore = yxSystemStoreService.getById(resources.getStoreId());
        resources.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());

        // 判断 productid storeid 是否已经存在
        LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YxStoreProductAttrValue::getProductId,resources.getProductId());
        lambdaQueryWrapper.eq(YxStoreProductAttrValue::getStoreId,resources.getStoreId());
        lambdaQueryWrapper.eq(YxStoreProductAttrValue::getIsDel,0);
        YxStoreProductAttrValue yxStoreProductAttrValue = this.getOne(lambdaQueryWrapper,false);
        if(yxStoreProductAttrValue != null) {
            resources.setId(yxStoreProductAttrValue.getId());
        }

        // 更新或新增
        saveOrUpdate(resources);

        // 更新attr表的AttrValues
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("attr_id",attr.getId());
        List<YxStoreProductAttrValue> list = baseMapper.selectList(queryWrapper1);
        List<String> list1 = new ArrayList<>();
        list.forEach(attrValue -> list1.add(attrValue.getSuk()));
        String attrvalues = CollUtil.join(list1,",");
        attr.setAttrValues(attrvalues);

        // 更新
        return true;
    }

    /**
     * 库存
     * @param unique
     * @return
     */
    @Override
    public int uniqueByStock(String unique) {
        QueryWrapper<YxStoreProductAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("`unique`",unique);
        wrapper.eq("is_del",0);
        YxStoreProductAttrValue yxStoreProductAttrValue = baseMapper.selectOne(wrapper);
        if(yxStoreProductAttrValue != null && yxStoreProductAttrValue.getStock() != null) {
            return yxStoreProductAttrValue.getStock().intValue();
        } else {
            return  0;
        }

    }

    @Override
    public List<String> findNotGuangZhou(String name) {
        return baseMapper.findNotGuangZhou(name);
    }
}
