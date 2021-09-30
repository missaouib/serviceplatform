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
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.dto.Product4projectListDTO;
import co.yixiang.modules.shop.service.mapper.*;
import co.yixiang.utils.PinYinUtils;
import com.alipay.api.domain.Product;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.Product4projectService;
import co.yixiang.modules.shop.service.dto.Product4projectDto;
import co.yixiang.modules.shop.service.dto.Product4projectQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2020-11-09
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "product4project")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class Product4projectServiceImpl extends BaseServiceImpl<Product4projectMapper, Product4project> implements Product4projectService {

    private final IGenerator generator;



    @Autowired
    private StoreProductMapper storeProductMapper;

    @Autowired
    private SystemStoreMapper systemStoreMapper;

    @Autowired
    private YxStoreProductAttrValueMapper yxStoreProductAttrValueMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(Product4projectQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<Product4project> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);

        List<Product4projectDto> dtoList = generator.convert(page.getList(), Product4projectDto.class);
        for(Product4projectDto dto:dtoList) {
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.select("store_name","common_name","yiyaobao_sku","spec","unit","manufacturer");
            queryWrapper1.eq("id",dto.getProductId());
            YxStoreProduct yxStoreProduct = storeProductMapper.selectOne(queryWrapper1);
            //   YxStoreProduct yxStoreProduct = storeProductMapper.selectById(yxStoreProductAttrValue.getProductId());
            if(yxStoreProduct!=null) {
                dto.setProductName(yxStoreProduct.getStoreName());
                dto.setCommonName(yxStoreProduct.getCommonName());
                dto.setYiyaobaoSku(yxStoreProduct.getYiyaobaoSku());
                dto.setSpec(yxStoreProduct.getSpec());
                dto.setUnit(yxStoreProduct.getUnit());
                dto.setManufacturer(yxStoreProduct.getManufacturer());
            }
            LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(YxStoreProductAttrValue::getUnique,dto.getProductUniqueId());
            YxStoreProductAttrValue yxStoreProductAttrValue =  yxStoreProductAttrValueMapper.selectOne(lambdaQueryWrapper);
            if(yxStoreProductAttrValue != null) {
                dto.setUnitPrice4Store(yxStoreProductAttrValue.getPrice());
            }
        }
        map.put("content", dtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<Product4project> queryAll(Product4projectQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(Product4project.class, criteria);

        if(StrUtil.isNotBlank(criteria.getProductName())) {
            queryWrapper.apply(" exists (select 1 from yx_store_product b where product4project.product_id = b.id and  ( b.store_name like CONCAT('%',{0},'%') or b.common_name like CONCAT('%',{1},'%') ) )",criteria.getProductName(),criteria.getProductName());
        }

        if(StrUtil.isNotBlank(criteria.getYiyaobaoSku())) {
            queryWrapper.apply(" exists (select 1 from yx_store_product b where product4project.product_id = b.id and  b.yiyaobao_sku like CONCAT('%',{0},'%') )",criteria.getYiyaobaoSku());
        }

        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public void download(List<Product4projectDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        if(CollUtil.isNotEmpty(all)) {
            for (Product4projectDto product4project : all) {
                Map<String,Object> map = new LinkedHashMap<>();
                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper();
                lambdaQueryWrapper.eq(YxStoreProduct::getId,product4project.getProductId());
                lambdaQueryWrapper.select(YxStoreProduct::getStoreName,YxStoreProduct::getCommonName, YxStoreProduct::getYiyaobaoSku,YxStoreProduct::getSpec,YxStoreProduct::getManufacturer,YxStoreProduct::getUnit);
                YxStoreProduct yxStoreProduct = storeProductMapper.selectOne(lambdaQueryWrapper);
                if(yxStoreProduct != null) {
                    map.put("项目编码", product4project.getProjectNo());
                    map.put("项目名称", product4project.getProjectName());
                    map.put("药品SKU", yxStoreProduct.getYiyaobaoSku());
                    map.put("药品名称", yxStoreProduct.getStoreName());
                    map.put("药品通用名", yxStoreProduct.getCommonName());
                    map.put("规格",yxStoreProduct.getSpec());
                    map.put("单位",yxStoreProduct.getUnit());
                    map.put("生产厂家",yxStoreProduct.getManufacturer());
                    map.put("药店名称", product4project.getStoreName());
                    map.put("项目价格", product4project.getUnitPrice());


                    LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper2 = new LambdaQueryWrapper();
                    lambdaQueryWrapper2.eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId());
                    YxStoreProductAttrValue yxStoreProductAttrValue =  yxStoreProductAttrValueMapper.selectOne(lambdaQueryWrapper2);
                    if(yxStoreProductAttrValue != null) {
                        map.put("益药宝药房价格", yxStoreProductAttrValue.getPrice());
                    } else {
                        map.put("益药宝药房价格", "");
                    }


                    if(product4project.getIsShow() != null && product4project.getIsShow() == 1) {
                        map.put("是否上架（Y/N）","Y");
                    } else {
                        map.put("是否上架（Y/N）","N");
                    }

                    if(product4project.getIgnoreStock() != null && product4project.getIgnoreStock() == 1) {
                        map.put("是否忽略库存（Y/N）","Y");
                    } else {
                        map.put("是否忽略库存（Y/N）","N");
                    }
                }



                list.add(map);
            }
        } else {
            Map<String,Object> map = new LinkedHashMap<>();

            map.put("项目编码", "");
            map.put("项目名称", "");
            map.put("药品SKU", "");
            map.put("药品名称", "");
            map.put("药品通用名", "");
            map.put("规格","");
            map.put("单位","");
            map.put("生产厂家","");
            map.put("药店名称", "");
            map.put("项目价格", "");
            map.put("益药宝药房价格", "");
            map.put("是否上架（Y/N）","Y");
            map.put("是否忽略库存（Y/N）","N");
            list.add(map);
        }

        FileUtil.downloadExcel(list, response);
    }

    @Override
    public String saveList(Product4projectListDTO resources) {
        String projectCode = "";
        if(CollUtil.isNotEmpty(resources.getDetails())) {
            for(Product4project product4project: resources.getDetails()) {
                projectCode = product4project.getProjectNo();
                product4project.setUnitPrice(null);
                if(StrUtil.isBlank(product4project.getGroupName())) {
                    product4project.setGroupName("");
                }
               int existsCount = baseMapper.selectCount(new LambdaQueryWrapper<Product4project>().eq(Product4project::getProductUniqueId,product4project.getProductUniqueId()).eq(Product4project::getProjectNo,product4project.getProjectNo()).eq(Product4project::getIsDel,0));
               if(existsCount == 0) {
                   if(product4project.getNum() == null) {
                       product4project.setNum(1);
                   }
                   baseMapper.insert(product4project);
                  /* YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueMapper.selectOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,product4project.getProductUniqueId()));
                   if(yxStoreProductAttrValue != null) {
                       product4project.setProductId(yxStoreProductAttrValue.getProductId());
                       product4project.setUnitPrice(yxStoreProductAttrValue.getPrice());
                       product4project.setStoreName(yxStoreProductAttrValue.getSuk());
                       product4project.setStoreId(yxStoreProductAttrValue.getStoreId());


                   }*/
               }

            }
        }

        return projectCode;
    }

    @Override
    public boolean updateProductDelFlag(Integer id) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("is_del",1);
        updateWrapper.eq("id",id);
        return this.update(updateWrapper);

    }

    @Override
    public int upload(List<Map<String, Object>> list,String projectCode) {

        List<Product4project> product4projectList = new ArrayList<>();

        for(Map<String,Object> data : list) {

            // 益药宝sku
            String yiyaobao_sku = "";
            String pharmacyName="";
            BigDecimal price = null;
            Integer isShow = 1;




            Object project_Object = data.get("项目编码");
            if(ObjectUtil.isNotEmpty(project_Object)) {
                projectCode = String.valueOf(project_Object);
            }else{
                throw new BadRequestException("项目编码不能为空");
            }


            LambdaQueryWrapper<Project> lambdaQueryWrapper4= new LambdaQueryWrapper<>();
            lambdaQueryWrapper4.eq(Project::getProjectCode,projectCode);
            Project project = projectMapper.selectOne(lambdaQueryWrapper4);
            if( ObjectUtil.isEmpty(project)) {
                throw new BadRequestException("项目编码["+ projectCode +"]找不到");

            }

            Object yiyaobao_sku_Object = data.get("药品SKU");
            if(ObjectUtil.isNotEmpty(yiyaobao_sku_Object)) {
                yiyaobao_sku = String.valueOf(yiyaobao_sku_Object);
            }else{
                throw new BadRequestException("药品SKU不能为空");
            }

            QueryWrapper<YxStoreProduct> queryWrapper = new QueryWrapper<YxStoreProduct>().eq("yiyaobao_sku",yiyaobao_sku);
            queryWrapper.select("id","store_name");

            YxStoreProduct product = storeProductMapper.selectOne(queryWrapper);
            if( ObjectUtil.isEmpty(product)) {
                throw new BadRequestException("药品SKU["+ yiyaobao_sku +"]找不到");

            }

            Object pharmacyName_Object = data.get("药店名称");
            if(ObjectUtil.isNotEmpty(pharmacyName_Object)) {
                pharmacyName = String.valueOf(pharmacyName_Object);
                // product.setYiyaobaoSku(yiyaobao_sku);
            }else{
                throw new BadRequestException("药店名称不能为空");
            }
            LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxSystemStore::getName,pharmacyName);
            lambdaQueryWrapper.select(YxSystemStore::getId,YxSystemStore::getName,YxSystemStore::getYiyaobaoId);
            YxSystemStore yxSystemStore = systemStoreMapper.selectOne(lambdaQueryWrapper);
            if(ObjectUtil.isEmpty(yxSystemStore) ) {
                throw new BadRequestException("药店名称["+ pharmacyName +"]找不到");
            }

            LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(YxStoreProductAttrValue::getProductId,product.getId());
            lambdaQueryWrapper1.eq(YxStoreProductAttrValue::getStoreId,yxSystemStore.getId());
            lambdaQueryWrapper1.eq(YxStoreProductAttrValue::getIsDel,0);

            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueMapper.selectOne(lambdaQueryWrapper1);

            if(ObjectUtil.isEmpty(yxStoreProductAttrValue)) {
                throw new BadRequestException("药店名称["+ pharmacyName +"] 不销售此商品["+ yiyaobao_sku +"]");
            }

            Object price_Object = data.get("项目价格");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                price = new BigDecimal(String.valueOf(price_Object));

            }

            String is_show_str = "";
            Object is_show_Object = data.get("是否上架（Y/N）");
            if(ObjectUtil.isNotEmpty(is_show_Object)) {
                is_show_str = String.valueOf(is_show_Object);
                if(!"Y".equals(is_show_str) && !"N".equals(is_show_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否上架标记填写错误，应填Y/N");
                }

                // 是否上架
                if( "N".equals(is_show_str)) {
                    isShow = 0;
                } else {
                    isShow = 1;
                }
            }


            Integer ignoreStock = 0;
            String ignoreStock_str = "";
            Object ignoreStock_Object = data.get("是否忽略库存（Y/N）");
            if(ObjectUtil.isNotEmpty(ignoreStock_Object)) {
                ignoreStock_str = String.valueOf(ignoreStock_Object);
                if(!"Y".equals(ignoreStock_str) && !"N".equals(ignoreStock_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否忽略库存填写错误，应填Y/N");
                }
                // 是否上架
                if( "N".equals(ignoreStock_str)) {
                    ignoreStock = 0;
                } else {
                    ignoreStock = 1;
                }
            }

            // 判断此项目下是否已有
            LambdaQueryWrapper<Product4project> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper2.eq(Product4project::getProjectNo,projectCode);
            lambdaQueryWrapper2.eq(Product4project::getProductId,product.getId());
            lambdaQueryWrapper2.eq(Product4project::getStoreId,yxSystemStore.getId());

            Product4project product4project = this.getOne(lambdaQueryWrapper2,false);
            if(product4project == null) {
                product4project = new Product4project();

            }
            product4project.setUnitPrice(price);
            product4project.setStoreId(yxSystemStore.getId());
            product4project.setProductId(product.getId());

            product4project.setIsShow(isShow);
            product4project.setStoreName(yxSystemStore.getName());
           // saveOrUpdate(product4project);
            product4projectList.add(product4project);
            product4project.setProductName(product.getStoreName());
            product4project.setYiyaobaoProjectCode(project.getYiyaobaoProjectCode());

            product4project.setProjectNo(projectCode);
            product4project.setProjectName(project.getProjectName());
            product4project.setGroupName("");
            product4project.setIsDel(0);

            product4project.setProductUniqueId(yxStoreProductAttrValue.getUnique());
            product4project.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
            product4project.setYiyaobaoSku(yiyaobao_sku);
            product4project.setIgnoreStock(ignoreStock);

        }
          this.saveOrUpdateBatch(product4projectList) ;




        return product4projectList.size();

    }
}
