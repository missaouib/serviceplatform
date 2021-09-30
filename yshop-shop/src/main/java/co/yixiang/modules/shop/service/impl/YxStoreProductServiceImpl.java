/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.PinyinComparator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.constant.ShopConstants;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.ebs.service.EbsServiceImpl;
import co.yixiang.modules.shop.domain.*;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.shop.service.mapper.ProjectMapper;
import co.yixiang.modules.shop.service.mapper.StoreProductMapper;
import co.yixiang.mp.yiyaobao.domain.SkuSellerPriceStock;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.PinYinUtils;
import co.yixiang.utils.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author hupeng
* @date 2020-05-12
*/
@Slf4j
@Service
// @AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreProduct")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreProductServiceImpl extends BaseServiceImpl<StoreProductMapper, YxStoreProduct> implements YxStoreProductService {
    @Autowired
    private  IGenerator generator;
    @Autowired
    private  StoreProductMapper storeProductMapper;
    @Autowired
    private  YxStoreProductAttrService yxStoreProductAttrService;
    @Autowired
    private  YxStoreProductAttrValueService yxStoreProductAttrValueService;
    @Autowired
    private  YxStoreProductAttrResultService yxStoreProductAttrResultService;

    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private YxSystemStoreService storeService;

    @Autowired
    private YxStoreDiseaseService yxStoreDiseaseService;

    @Autowired
    private YxStoreProductAttrService storeProductAttrService;


    @Autowired
    private Product4projectService product4projectService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private EbsServiceImpl ebsService;

    @Autowired
    private YxStoreProductGroupService yxStoreProductGroupService;


    private ExecutorService productExecutor =  new ThreadPoolExecutor(3, 3,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1000));


    @Value("${file.localUrl}")
    private String localUrl;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreProductQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreProduct> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);

        List<YxStoreProductDto> yxStoreProductDtoList = generator.convert(page.getList(), YxStoreProductDto.class);
        for(YxStoreProductDto yxStoreProductDto:yxStoreProductDtoList) {
           if(yxStoreProductDto.getIsGroup()!= null && yxStoreProductDto.getIsGroup() == 1 ) {
               LambdaQueryWrapper<YxStoreProductGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
               lambdaQueryWrapper.eq(YxStoreProductGroup::getParentProductId,yxStoreProductDto.getId());
               List<YxStoreProductGroupDto> dtoList = generator.convert(yxStoreProductGroupService.list(lambdaQueryWrapper), YxStoreProductGroupDto.class);
               for(YxStoreProductGroupDto dto:dtoList) {
                   // 门店名称
                   YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getUnique,dto.getProductUnique()).select(YxStoreProductAttrValue::getSuk),false);
                   if(yxStoreProductAttrValue != null) {
                       dto.setStoreName(yxStoreProductAttrValue.getSuk());
                   }

                   // 商品信息
                   LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                   lambdaQueryWrapper1.eq(YxStoreProduct::getId,dto.getProductId());
                   lambdaQueryWrapper1.select(YxStoreProduct::getId,YxStoreProduct::getStoreName,YxStoreProduct::getCommonName,YxStoreProduct::getSpec,YxStoreProduct::getManufacturer,YxStoreProduct::getUnit);
                   YxStoreProduct yxStoreProduct =  this.getOne(lambdaQueryWrapper1,false);
                   if(yxStoreProduct != null) {
                       dto.setProductName(yxStoreProduct.getStoreName());
                       dto.setCommonName(yxStoreProduct.getCommonName());
                       dto.setSpec(yxStoreProduct.getSpec());
                       dto.setManufacturer(yxStoreProduct.getManufacturer());
                       dto.setUnit(yxStoreProduct.getUnit());
                   }


               }
               yxStoreProductDto.setGroupDetailList(dtoList);
           }
        }

        map.put("content", yxStoreProductDtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreProduct> queryAll(YxStoreProductQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreProduct.class, criteria);
        queryWrapper.select("id","image","slider_image","store_name","store_info","keyword","cate_id","is_show","description","add_time",
                "is_del","yiyaobao_sku","license_number","common_name","drug_form","spec","manufacturer","storage_condition",
                "tax_rate","unit","indication","directions","contraindication","disease_id","type","disease_id_cloud",
                "disease_id_common","label1","label2","label3","basis","characters","untoward_effect","attention","quality_period",
                "pregnancy_lactation_directions","children_directions","elderly_patient_directions","sort","bar_code","is_group"
                );
        //关键字搜索
        if(StrUtil.isNotEmpty(criteria.getKeyword())){
            //  wrapper.like("store_name",productQueryParam.getKeyword());
            String pinYin = PinYinUtils.getHanziPinYin(criteria.getKeyword());

            queryWrapper.apply(" ( store_name like concat('%',{0} ,'%') or common_name like concat('%',{1} ,'%')  or keyword like concat('%',{2} ,'%') or pinyin_name like concat('%',{3} ,'%') or FIND_IN_SET({4},keyword) )",criteria.getKeyword(),criteria.getKeyword(),criteria.getKeyword(),pinYin,criteria.getKeyword());
        }

        if(StrUtil.isNotBlank(criteria.getProjectCode())) {
            queryWrapper.apply("EXISTS (SELECT 1 FROM product4project p WHERE p.is_del = 0 and p.product_id = yx_store_product.id AND p.project_no = {0}) ",criteria.getProjectCode());

            queryWrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product_attr_value yspav WHERE yspav.product_id = yx_store_product.id AND yspav.is_del = 0)");
        }
        queryWrapper.orderByAsc("id");
        List<YxStoreProduct> yxStoreProductList = baseMapper.selectList(queryWrapper);


        if(StrUtil.isNotBlank(criteria.getProjectCode())) {

           Integer count = projectMapper.selectCount( new LambdaQueryWrapper<Project>().eq(Project::getGuangzhouFlag,"1").eq(Project::getProjectCode,criteria.getProjectCode()));
           if(count >0) {
               YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName,ShopConstants.STORENAME_GUANGZHOU_CLOUD).select(YxSystemStore::getId));
               for (YxStoreProduct yxStoreProduct : yxStoreProductList) {
                   LambdaQueryWrapper<YxStoreProductAttrValue> queryWrapper1 = new LambdaQueryWrapper<>();
                   queryWrapper1.eq(YxStoreProductAttrValue::getProductId,yxStoreProduct.getId());
                   queryWrapper1.eq(YxStoreProductAttrValue::getStoreId,yxSystemStore.getId());
                   queryWrapper1.eq(YxStoreProductAttrValue::getIsDel,0);
                   queryWrapper1.select(YxStoreProductAttrValue::getUnique,YxStoreProductAttrValue::getPrice);
                   YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(queryWrapper1);
                   if(yxStoreProductAttrValue == null) {
                       continue;
                   }
                   BigDecimal otPrice = yxStoreProductAttrValue.getPrice();

                   LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper();
                   lambdaQueryWrapper.eq(Product4project::getProjectNo, criteria.getProjectCode());
                   lambdaQueryWrapper.eq(Product4project::getProductId, yxStoreProduct.getId());
                  // lambdaQueryWrapper.isNotNull(Product4project::getUnitPrice);
                   lambdaQueryWrapper.eq(Product4project::getIsDel,0);
                   lambdaQueryWrapper.select(Product4project::getUnitPrice,Product4project::getIsShow,Product4project::getSettlementPrice);
                   Product4project product4project = product4projectService.getOne(lambdaQueryWrapper);

                   if(product4project == null) {
                       continue;
                   }
                   yxStoreProduct.setIsShow(product4project.getIsShow());
                   yxStoreProduct.setPrice(product4project.getUnitPrice());
                   yxStoreProduct.setOtPrice(otPrice);
                   yxStoreProduct.setUnique(yxStoreProductAttrValue.getUnique());
                   yxStoreProduct.setSettlementPrice(product4project.getSettlementPrice());

               }
           }

        }

        return yxStoreProductList;
    }


    @Override
    public void download(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException {
       //	商品名	通用名	商品规格	生产厂家	标准价格	商品类型	剂型	存储条件	单位	批准文号	主要成分
        //	药理作用	性状	保质期	药物相互作用	适应症	病种	不良反应	禁忌
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreProduct yxStoreProduct : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("益药宝sku", yxStoreProduct.getYiyaobaoSku());
            map.put("药品名称", yxStoreProduct.getStoreName());
            map.put("药品通用名", yxStoreProduct.getCommonName());
            map.put("规格", yxStoreProduct.getSpec());
            map.put("生产厂家", yxStoreProduct.getManufacturer());
            map.put("项目零售价", yxStoreProduct.getPrice());
            map.put("项目结算价", yxStoreProduct.getSettlementPrice());
            map.put("药房零售价", yxStoreProduct.getOtPrice());
            String typeName="";

            if(StrUtil.isNotBlank(yxStoreProduct.getType())) {
                typeName = storeProductMapper.queryProductTypeName(yxStoreProduct.getType());
            }

            map.put("商品类型", typeName);

            map.put("剂型", yxStoreProduct.getDrugForm());
            map.put("存储条件", yxStoreProduct.getStorageCondition());
            map.put("单位", yxStoreProduct.getUnit());
            map.put("国药准字", yxStoreProduct.getLicenseNumber());

            map.put("保质期", yxStoreProduct.getQualityPeriod());
            map.put("适用症", yxStoreProduct.getIndication());
            map.put("禁忌",yxStoreProduct.getContraindication());

            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
                map.put("85折药品标记",yxStoreProduct.getLabel1());
                map.put("88折药品标记",yxStoreProduct.getLabel2());
                map.put("5折药品标记",yxStoreProduct.getLabel3());

                /*if("N".equals(yxStoreProduct.getIsSales())) {
                    map.put("是否参与销售","N");
                } else {
                    map.put("是否参与销售","Y");
                }*/

            }


            String diseaseIds = yxStoreProduct.getDiseaseIdCloud();
            String diseaseNames = "";
            if(StrUtil.isNotBlank(diseaseIds)) {
                List<String> idList = Arrays.asList(diseaseIds.split(","));
                List<String> nameList=new ArrayList<>();
                for(String idStr:idList) {
                    Integer id = Integer.valueOf(idStr);
                    YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getById(id);
                    if(yxStoreDisease != null) {
                        nameList.add(yxStoreDisease.getCateName());
                    }
                }
                diseaseNames = CollUtil.join(nameList,",");
            }
            map.put("商品分类", diseaseNames);

            if(yxStoreProduct.getIsShow() == 0) {
                map.put("是否上架(Y/N)","N");
            } else {
                map.put("是否上架(Y/N)","Y");
            }

            map.put("主要成分",yxStoreProduct.getBasis());
            map.put("性状",yxStoreProduct.getCharacters());
            map.put("用法用量",yxStoreProduct.getDirections());
            map.put("不良反应",yxStoreProduct.getUntowardEffect());
            map.put("注意事项",yxStoreProduct.getAttention());
            map.put("孕妇及哺乳妇女用药",yxStoreProduct.getPregnancyLactationDirections());
            map.put("儿童用药",yxStoreProduct.getChildrenDirections());
            map.put("老年用药",yxStoreProduct.getElderlyPatientDirections());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public void downloadSample(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException {
        //	商品名	通用名	商品规格	生产厂家	标准价格	商品类型	剂型	存储条件	单位	批准文号	主要成分
        //	药理作用	性状	保质期	药物相互作用	适应症	病种	不良反应	禁忌
        List<Map<String, Object>> list = new ArrayList<>();
        if(CollUtil.isNotEmpty(all)) {
            for (YxStoreProduct yxStoreProduct : all) {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("益药宝sku", yxStoreProduct.getYiyaobaoSku());
                map.put("药品通用名", yxStoreProduct.getCommonName());
                if(ObjectUtil.isNull(yxStoreProduct.getPrice())) {
                    // 项目零售价为空，取药房价格
                    map.put("项目零售价", yxStoreProduct.getOtPrice());
                }else{
                    map.put("项目零售价", yxStoreProduct.getPrice());
                }

                if(ObjectUtil.isNull(yxStoreProduct.getSettlementPrice())){
                    if(ObjectUtil.isNull(yxStoreProduct.getPrice())) {
                        // 项目零售价为空，取药房价格
                        map.put("项目结算价", yxStoreProduct.getOtPrice());
                    }else{
                        map.put("项目结算价", yxStoreProduct.getPrice());
                    }
                }else {
                    map.put("项目结算价", yxStoreProduct.getSettlementPrice());
                }


                map.put("药房零售价", yxStoreProduct.getOtPrice());
                if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
                    map.put("85折药品标记",yxStoreProduct.getLabel1());
                    map.put("88折药品标记",yxStoreProduct.getLabel2());
                    map.put("5折药品标记",yxStoreProduct.getLabel3());
                }

                String diseaseIds = yxStoreProduct.getDiseaseIdCloud();
                String diseaseNames = "";
                if(StrUtil.isNotBlank(diseaseIds)) {
                    List<String> idList = Arrays.asList(diseaseIds.split(","));
                    List<String> nameList=new ArrayList<>();
                    for(String idStr:idList) {
                        Integer id = Integer.valueOf(idStr);
                        YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getById(id);
                        if(yxStoreDisease != null) {
                            nameList.add(yxStoreDisease.getCateName());
                        }
                    }
                    diseaseNames = CollUtil.join(nameList,",");
                }
                map.put("商品分类", diseaseNames);

                if(yxStoreProduct.getIsShow() == 0) {
                    map.put("是否上架(Y/N)","N");
                } else {
                    map.put("是否上架(Y/N)","Y");
                }

                list.add(map);
            }
        } else {
            // 如果数据为空，则只输出title
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("益药宝sku", "");
            map.put("项目零售价", "");
            map.put("项目结算价", "");
            map.put("药房零售价", "");
            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
                map.put("85折药品标记","");
                map.put("88折药品标记","");
                map.put("5折药品标记","");
            }
            map.put("商品分类", "");
            map.put("是否上架(Y/N)","");

            list.add(map);
        }

        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadCommon(List<YxStoreProduct> all, HttpServletResponse response) throws IOException {
        //	商品名	通用名	商品规格	生产厂家	标准价格	商品类型	剂型	存储条件	单位	批准文号	主要成分
        //	药理作用	性状	保质期	药物相互作用	适应症	病种	不良反应	禁忌
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreProduct yxStoreProduct : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("益药宝sku", yxStoreProduct.getYiyaobaoSku());
            map.put("药品名称", yxStoreProduct.getStoreName());
            map.put("药品通用名", yxStoreProduct.getCommonName());
            map.put("规格", yxStoreProduct.getSpec());
            map.put("生产厂家", yxStoreProduct.getManufacturer());
            map.put("零售价", yxStoreProduct.getPrice());
            String typeName="";

            if(StrUtil.isNotBlank(yxStoreProduct.getType())) {
                typeName = storeProductMapper.queryProductTypeName(yxStoreProduct.getType());
            }

            map.put("商品类型", typeName);
            map.put("剂型", yxStoreProduct.getDrugForm());
            map.put("存储条件", yxStoreProduct.getStorageCondition());
            map.put("单位", yxStoreProduct.getUnit());
            map.put("国药准字", yxStoreProduct.getLicenseNumber());
            map.put("主要成分", yxStoreProduct.getBasis());
            map.put("药理作用", yxStoreProduct.getPharmacologicalEffect());
            map.put("性状", yxStoreProduct.getCharacters());
            map.put("保质期", yxStoreProduct.getQualityPeriod());
            map.put("药物相互作用", yxStoreProduct.getDrugInteraction());
            map.put("适应症", yxStoreProduct.getIndication());

            String diseaseIds = yxStoreProduct.getDiseaseIdCommon();
            String diseaseNames = "";
            if(StrUtil.isNotBlank(diseaseIds)) {
                List<String> idList = Arrays.asList(diseaseIds.split(","));
                List<String> nameList=new ArrayList<>();
                for(String idStr:idList) {
                    Integer id = Integer.valueOf(idStr);
                    YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getById(id);
                    if(yxStoreDisease != null) {
                        nameList.add(yxStoreDisease.getCateName());
                    }
                }
                diseaseNames = CollUtil.join(nameList,",");
            }
            map.put("商品分类", diseaseNames);
            map.put("不良反应", yxStoreProduct.getUntowardEffect());
            map.put("禁忌", yxStoreProduct.getContraindication());
            if(yxStoreProduct.getIsShow() == 0) {
                map.put("是否上架","N");
            } else {
                map.put("是否上架","Y");
            }
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public YxStoreProduct saveProduct(YxStoreProduct storeProduct) {
     /*   if (storeProduct.getStoreCategory().getId() == null) {
            throw new BadRequestException("分类名称不能为空");
        }
        boolean check = yxStoreCategoryService
                .checkProductCategory(storeProduct.getStoreCategory().getId());
        if(!check) throw new BadRequestException("商品分类必选选择二级");
        */
     /*   String cateId = "";
        if(storeProduct.getStoreCategory().getId() != null) {
            cateId = storeProduct.getStoreCategory().getId().toString();
        }
        storeProduct.setCateId(cateId);*/
        if( StrUtil.isNotBlank(storeProduct.getDiseaseIdCommon())) {
            storeProduct.setDiseaseId(storeProduct.getDiseaseIdCommon() + "," + storeProduct.getDiseaseIdCloud());
        } else {
            storeProduct.setDiseaseId(storeProduct.getDiseaseIdCloud());
        }
        this.save(storeProduct);
        return storeProduct;
    }

    @Override
    public void recovery(Integer id) {
        storeProductMapper.updateDel(0,id);
        storeProductMapper.updateOnsale(0,id);
    }

    @Override
    public void onSale(Integer id, int status) {
        if(status == 1){
            status = 0;
        }else{
            status = 1;
        }
        storeProductMapper.updateOnsale(status,id);
    }

    @Override
    public List<ProductFormatDto> isFormatAttr(Integer id, String jsonStr) {
        if(ObjectUtil.isNull(id)) throw new BadRequestException("产品不存在");

        YxStoreProductDto yxStoreProductDTO = generator.convert(this.getById(id),YxStoreProductDto.class);
        DetailDto detailDTO = attrFormat(jsonStr);
        List<ProductFormatDto> newList = new ArrayList<>();
        for (Map<String, Map<String,String>> map : detailDTO.getRes()) {
            ProductFormatDto productFormatDTO = new ProductFormatDto();
            productFormatDTO.setDetail(map.get("detail"));
            productFormatDTO.setCost(yxStoreProductDTO.getCost().doubleValue());
            productFormatDTO.setPrice(yxStoreProductDTO.getPrice().doubleValue());
            productFormatDTO.setSales(yxStoreProductDTO.getSales());
            productFormatDTO.setPic(yxStoreProductDTO.getImage());
            productFormatDTO.setCheck(false);
            newList.add(productFormatDTO);
        }
        return newList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProductAttr(Integer id, String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        //System.out.println(jsonObject);
        List<FromatDetailDto> attrList = JSON.parseArray(
                jsonObject.get("items").toString(),
                FromatDetailDto.class);
        List<ProductFormatDto> valueList = JSON.parseArray(
                jsonObject.get("attrs").toString(),
                ProductFormatDto.class);


        List<YxStoreProductAttr> attrGroup = new ArrayList<>();
        for (FromatDetailDto fromatDetailDTO : attrList) {
            YxStoreProductAttr  yxStoreProductAttr = new YxStoreProductAttr();
            yxStoreProductAttr.setProductId(id);
            yxStoreProductAttr.setAttrName(fromatDetailDTO.getValue());
            yxStoreProductAttr.setAttrValues(StrUtil.
                    join(",",fromatDetailDTO.getDetail()));
            attrGroup.add(yxStoreProductAttr);
        }


        List<YxStoreProductAttrValue> valueGroup = new ArrayList<>();
        for (ProductFormatDto productFormatDTO : valueList) {
            YxStoreProductAttrValue yxStoreProductAttrValue = new YxStoreProductAttrValue();
            yxStoreProductAttrValue.setProductId(id);
            //productFormatDTO.getDetail().values().stream().collect(Collectors.toList());
            List<String> stringList = productFormatDTO.getDetail().values()
                    .stream().collect(Collectors.toList());
            Collections.sort(stringList);
            yxStoreProductAttrValue.setSuk(StrUtil.
                    join(",",stringList));
            yxStoreProductAttrValue.setPrice(BigDecimal.valueOf(productFormatDTO.getPrice()));
            yxStoreProductAttrValue.setCost(BigDecimal.valueOf(productFormatDTO.getCost()));
            yxStoreProductAttrValue.setStock(productFormatDTO.getSales());
            yxStoreProductAttrValue.setUnique(IdUtil.simpleUUID());
            yxStoreProductAttrValue.setImage(productFormatDTO.getPic());

            valueGroup.add(yxStoreProductAttrValue);
        }

        if(attrGroup.isEmpty() || valueGroup.isEmpty()){
            throw new BadRequestException("请设置至少一个属性!");
        }

        //如果设置sku 处理价格与库存

        ////取最小价格
        BigDecimal minPrice = valueGroup
                .stream()
                .map(YxStoreProductAttrValue::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        //计算库存
        Integer stock = valueGroup
                .stream()
                .map(YxStoreProductAttrValue::getStock)
                .reduce(Integer::sum)
                .orElse(0);

        YxStoreProduct yxStoreProduct = YxStoreProduct.builder()
                .stock(stock)
                .price(minPrice)
                .id(id)
                .build();
        this.updateById(yxStoreProduct);

        //插入之前清空
        clearProductAttr(id,false);


        //保存属性
        yxStoreProductAttrService.saveOrUpdateBatch(attrGroup);

        //保存值
        yxStoreProductAttrValueService.saveOrUpdateBatch(valueGroup);

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("attr",jsonObject.get("items"));
        map.put("value",jsonObject.get("attrs"));

        //保存结果
        setResult(map,id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setResult(Map<String, Object> map,Integer id) {
        YxStoreProductAttrResult yxStoreProductAttrResult = new YxStoreProductAttrResult();
        yxStoreProductAttrResult.setProductId(id);
        yxStoreProductAttrResult.setResult(JSON.toJSONString(map));
        yxStoreProductAttrResult.setChangeTime(OrderUtil.getSecondTimestampTwo());

        yxStoreProductAttrResultService.remove(new QueryWrapper<YxStoreProductAttrResult>().eq("product_id",id));

        yxStoreProductAttrResultService.saveOrUpdate(yxStoreProductAttrResult);
    }

    @Override
    public String getStoreProductAttrResult(Integer id) {
        YxStoreProductAttrResult yxStoreProductAttrResult = yxStoreProductAttrResultService
                .getOne(new QueryWrapper<YxStoreProductAttrResult>().eq("product_id",id));
        if(ObjectUtil.isNull(yxStoreProductAttrResult)) return "";
        return  yxStoreProductAttrResult.getResult();
    }

    @Override
    @Transactional
    public YxStoreProduct updateProduct(YxStoreProduct resources) {
        /*if(resources.getStoreCategory() == null || resources.getStoreCategory().getId() == null) throw new BadRequestException("请选择分类");
        boolean check = yxStoreCategoryService
                .checkProductCategory(resources.getStoreCategory().getId());
        if(!check) throw new BadRequestException("商品分类必选选择二级");
        resources.setCateId(resources.getStoreCategory().getId().toString());*/
        if(StrUtil.isNotBlank(resources.getYiyaobaoSku()) && resources.getId() == null) {
            int exists = this.count(new LambdaQueryWrapper<YxStoreProduct>().eq(YxStoreProduct::getYiyaobaoSku,resources.getYiyaobaoSku()));
            if(exists >0) {
                throw new BadRequestException("益药宝sku已经存在");
            }
        }

        List<String> diseaseIdList = new ArrayList<>();
        if( StrUtil.isNotBlank(resources.getDiseaseIdCommon())) {
            diseaseIdList.addAll(Arrays.asList(resources.getDiseaseIdCommon().split(",")));

        }
        if( StrUtil.isNotBlank(resources.getDiseaseIdCloud())) {
            diseaseIdList.addAll(Arrays.asList(resources.getDiseaseIdCloud().split(",")));
        }

        if(CollUtil.isNotEmpty(diseaseIdList)) {
            resources.setDiseaseId(CollUtil.join(diseaseIdList,","));
        }else {
            resources.setDiseaseId("");
        }



        String commonPinYin = PinYinUtils.getHanziPinYin(resources.getCommonName()) ;
        String namePinYin = PinYinUtils.getHanziPinYin(resources.getStoreName()) ;
        if(commonPinYin == null) {
            commonPinYin = "";
        }
        if(namePinYin == null) {
            namePinYin = "";
        }
        String pinYin = "";
        if(commonPinYin == null) {
            commonPinYin = "";
        }

        if(namePinYin ==  null) {
            namePinYin = "";
        }
        if(commonPinYin.equals(namePinYin)) {
            pinYin = commonPinYin;
        } else {
            pinYin = commonPinYin + "(" + namePinYin + ")";
        }



        String commonShortPinYin = PinYinUtils.getHanziInitials(resources.getCommonName());
        String nameShortPinYin = PinYinUtils.getHanziInitials(resources.getStoreName()) ;
        if(commonShortPinYin == null) {
            commonShortPinYin = "";
        }
        if(nameShortPinYin == null) {
            nameShortPinYin = "";
        }
        String shortPinYin = "";
        if(commonShortPinYin.equals(nameShortPinYin)) {
            shortPinYin = commonShortPinYin;
        } else {
            shortPinYin = commonShortPinYin + "(" + nameShortPinYin + ")";
        }


        resources.setPinyinName(pinYin);
        resources.setPinyinShortName(shortPinYin);

        Integer isShow = resources.getIsShow();



        LambdaUpdateWrapper<YxStoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCloud,resources.getDiseaseIdCloud());
        lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseId,resources.getDiseaseId());
        lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCommon,resources.getDiseaseIdCommon());
        lambdaUpdateWrapper.set(YxStoreProduct::getStoreInfo,resources.getStoreInfo());
        lambdaUpdateWrapper.set(YxStoreProduct::getDescription,resources.getDescription());
        if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(resources.getProjectCode())) {
            lambdaUpdateWrapper.set(YxStoreProduct::getLabel1,resources.getLabel1());
            lambdaUpdateWrapper.set(YxStoreProduct::getLabel2,resources.getLabel2());
            lambdaUpdateWrapper.set(YxStoreProduct::getLabel3,resources.getLabel3());
        }
        lambdaUpdateWrapper.eq(YxStoreProduct::getId,resources.getId());
        lambdaUpdateWrapper.set(YxStoreProduct::getUpdateTime,new Timestamp(System.currentTimeMillis()));


        if(StrUtil.isBlank(resources.getImage())) {
            String defaultImage = localUrl + "/file/static/defaultMed.jpg";
            resources.setImage(defaultImage);

        }

        if(StrUtil.isBlank(resources.getSliderImage())) {
            String defaultImage = localUrl + "/file/static/defaultMed.jpg";
            resources.setSliderImage(defaultImage);
        }
        lambdaUpdateWrapper.set(YxStoreProduct::getImage,resources.getImage());
        lambdaUpdateWrapper.set(YxStoreProduct::getSliderImage,resources.getSliderImage());
        lambdaUpdateWrapper.set(YxStoreProduct::getStoreName,resources.getStoreName());
        lambdaUpdateWrapper.set(YxStoreProduct::getKeyword,resources.getKeyword());
        lambdaUpdateWrapper.set(YxStoreProduct::getIsShow,resources.getIsShow());
        lambdaUpdateWrapper.set(YxStoreProduct::getIsDel,resources.getIsDel());
        lambdaUpdateWrapper.set(YxStoreProduct::getYiyaobaoSku,resources.getYiyaobaoSku());
        lambdaUpdateWrapper.set(YxStoreProduct::getIsDel,resources.getIsDel());
        lambdaUpdateWrapper.set(YxStoreProduct::getLicenseNumber,resources.getLicenseNumber());
        lambdaUpdateWrapper.set(YxStoreProduct::getCommonName,resources.getCommonName());
        lambdaUpdateWrapper.set(YxStoreProduct::getDrugForm,resources.getDrugForm());
        lambdaUpdateWrapper.set(YxStoreProduct::getSpec,resources.getSpec());
        lambdaUpdateWrapper.set(YxStoreProduct::getManufacturer,resources.getManufacturer());
        lambdaUpdateWrapper.set(YxStoreProduct::getStorageCondition,resources.getStorageCondition());
        lambdaUpdateWrapper.set(YxStoreProduct::getTaxRate,resources.getTaxRate());
        lambdaUpdateWrapper.set(YxStoreProduct::getUnit,resources.getUnit());
        lambdaUpdateWrapper.set(YxStoreProduct::getIndication,resources.getIndication());
        lambdaUpdateWrapper.set(YxStoreProduct::getDirections,resources.getDirections());
        lambdaUpdateWrapper.set(YxStoreProduct::getContraindication,resources.getContraindication());
        lambdaUpdateWrapper.set(YxStoreProduct::getType,resources.getType());
        lambdaUpdateWrapper.set(YxStoreProduct::getBasis,resources.getBasis());
        lambdaUpdateWrapper.set(YxStoreProduct::getCharacters,resources.getCharacters());
        lambdaUpdateWrapper.set(YxStoreProduct::getUntowardEffect,resources.getUntowardEffect());
        lambdaUpdateWrapper.set(YxStoreProduct::getAttention,resources.getAttention());
        lambdaUpdateWrapper.set(YxStoreProduct::getQualityPeriod,resources.getQualityPeriod());
        lambdaUpdateWrapper.set(YxStoreProduct::getAttention,resources.getAttention());
        lambdaUpdateWrapper.set(YxStoreProduct::getPregnancyLactationDirections,resources.getPregnancyLactationDirections());
        lambdaUpdateWrapper.set(YxStoreProduct::getPregnancyLactationDirections,resources.getPregnancyLactationDirections());
        lambdaUpdateWrapper.set(YxStoreProduct::getChildrenDirections,resources.getChildrenDirections());
        lambdaUpdateWrapper.set(YxStoreProduct::getElderlyPatientDirections,resources.getElderlyPatientDirections());
        lambdaUpdateWrapper.set(YxStoreProduct::getIsGroup,resources.getIsGroup());
      //  this.update(lambdaUpdateWrapper);

        this.saveOrUpdate(resources,lambdaUpdateWrapper);






        if(StrUtil.isNotBlank(resources.getProjectCode())) {

    /*        YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getName,ShopConstants.STORENAME_GUANGZHOU_CLOUD),false);
            if(yxSystemStore == null) {
                throw  new BadRequestException(ShopConstants.STORENAME_GUANGZHOU_CLOUD +"没有找到");
            }

            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new LambdaQueryWrapper<YxStoreProductAttrValue>().eq(YxStoreProductAttrValue::getStoreId,yxSystemStore.getId()).eq(YxStoreProductAttrValue::getYiyaobaoSku,resources.getYiyaobaoSku()),false);
            if(yxStoreProductAttrValue == null) {
                throw  new BadRequestException(ShopConstants.STORENAME_GUANGZHOU_CLOUD +"没有找到["+ resources.getYiyaobaoSku()+"],请去益药宝维护");
            }
*/
            // project
            int countExist = product4projectService.count(new LambdaQueryWrapper<Product4project>().eq(Product4project::getProductUniqueId,resources.getUnique()).eq(Product4project::getProjectNo,resources.getProjectCode()).eq(Product4project::getIsDel,0));
            if(countExist == 0) {
                throw  new BadRequestException(ShopConstants.STORENAME_GUANGZHOU_CLOUD +"没有找到["+ resources.getYiyaobaoSku()+"],请去益药宝维护");
            }

            LambdaUpdateWrapper<Product4project> updateWrapperProject = new LambdaUpdateWrapper<>();
            updateWrapperProject.set(Product4project::getUnitPrice,resources.getPrice());
            updateWrapperProject.set(Product4project::getIsDel,0);
            updateWrapperProject.set(Product4project::getIsShow,isShow);
            updateWrapperProject.set(Product4project::getUpdateTime,new Timestamp(System.currentTimeMillis()));
            updateWrapperProject.eq(Product4project::getProjectNo,resources.getProjectCode());
            updateWrapperProject.eq(Product4project::getProductUniqueId,resources.getUnique());
            updateWrapperProject.set(Product4project::getSettlementPrice,resources.getSettlementPrice());
            product4projectService.update(updateWrapperProject);

            final String productCode=resources.getProjectCode();
            productExecutor.execute(()->{
                dualProuctDisease2Redis(productCode);
            });
        }


        // 组合的情况新增子商品

        if(resources.getIsGroup() != null && resources.getIsGroup() == 1) {
            LambdaQueryWrapper<YxStoreProductGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxStoreProductGroup::getParentProductId,resources.getId());
            yxStoreProductGroupService.remove(lambdaQueryWrapper);
            if(CollUtil.isNotEmpty(resources.getGroupDetailList())) {
                for(YxStoreProductGroupDto dto:resources.getGroupDetailList()) {
                    YxStoreProductGroup yxStoreProductGroup = generator.convert(dto,YxStoreProductGroup.class);
                    yxStoreProductGroup.setParentProductId(resources.getId());
                    yxStoreProductGroup.setParentProductYiyaobaoSku(resources.getYiyaobaoSku());
                    yxStoreProductGroupService.save(yxStoreProductGroup);
                }
            }


        }

        return resources;
    }

    @Override
    public void delete(Integer id) {
        storeProductMapper.updateDel(1,id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearProductAttr(Integer id,boolean isActice) {
        if(ObjectUtil.isNull(id)) throw new BadRequestException("产品不存在");

        yxStoreProductAttrService.remove(new QueryWrapper<YxStoreProductAttr>().eq("product_id",id));
        yxStoreProductAttrValueService.remove(new QueryWrapper<YxStoreProductAttrValue>().eq("product_id",id));

        if(isActice){
            yxStoreProductAttrResultService.remove(new QueryWrapper<YxStoreProductAttrResult>().eq("product_id",id));
        }
    }
    /**
     * 组合规则属性算法
     * @param jsonStr
     * @return
     */
    public DetailDto attrFormat(String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        List<FromatDetailDto> fromatDetailDTOList = JSON.parseArray(jsonObject.get("items").toString(),
                FromatDetailDto.class);
        List<String> data = new ArrayList<>();
        List<Map<String,Map<String,String>>> res =new ArrayList<>();
        if(fromatDetailDTOList.size() > 1){
            for (int i=0; i < fromatDetailDTOList.size() - 1;i++){
                if(i == 0) data = fromatDetailDTOList.get(i).getDetail();
                List<String> tmp = new LinkedList<>();
                for (String v : data) {
                    for (String g : fromatDetailDTOList.get(i+1).getDetail()) {
                        String rep2 = "";
                        if(i == 0){
                            rep2 = fromatDetailDTOList.get(i).getValue() + "_" + v + "-"
                                    + fromatDetailDTOList.get(i+1).getValue() + "_" + g;
                        }else{
                            rep2 = v + "-"
                                    + fromatDetailDTOList.get(i+1).getValue() + "_" + g;
                        }
                        tmp.add(rep2);
                        if(i == fromatDetailDTOList.size() - 2){
                            Map<String,Map<String,String>> rep4 = new LinkedHashMap<>();
                            Map<String,String> reptemp = new LinkedHashMap<>();
                            for (String h : Arrays.asList(rep2.split("-"))) {
                                List<String> rep3 = Arrays.asList(h.split("_"));

                                if(rep3.size() > 1){
                                    reptemp.put(rep3.get(0),rep3.get(1));
                                }else{
                                    reptemp.put(rep3.get(0),"");
                                }
                            }
                            rep4.put("detail",reptemp);
                            res.add(rep4);
                        }
                    }
                }
                //System.out.println("tmp:"+tmp);
                if(!tmp.isEmpty()){
                    data = tmp;
                }
            }
        }else{
            List<String> dataArr = new ArrayList<>();

            for (FromatDetailDto fromatDetailDTO : fromatDetailDTOList) {

                for (String str : fromatDetailDTO.getDetail()) {
                    Map<String,Map<String,String>> map2 = new LinkedHashMap<>();
                    //List<Map<String,String>> list1 = new ArrayList<>();
                    dataArr.add(fromatDetailDTO.getValue()+"_"+str);
                    Map<String,String> map1 = new LinkedHashMap<>();
                    map1.put(fromatDetailDTO.getValue(),str);
                    //list1.add(map1);
                    map2.put("detail",map1);
                    res.add(map2);
                }
            }
            String s = StrUtil.join("-",dataArr);
            data.add(s);
        }
        DetailDto detailDTO = new DetailDto();
        detailDTO.setData(data);
        detailDTO.setRes(res);
        return detailDTO;
    }

    @Override
    public void updateIsShowByExistsAttr() {
        storeProductMapper.updateIsShowByExistsAttr();
    }

    @Override
    public void convertImage(){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.like("slider_image","yiyaogo");
        queryWrapper.select("id","slider_image");
       // queryWrapper.last(" limit 5");
        List<YxStoreProduct> productList = storeProductMapper.selectList(queryWrapper);
        for(YxStoreProduct product : productList) {
            List<String> imageList = Arrays.asList(product.getSliderImage().split(","));
            List<String> imageConvertList = new ArrayList<>();
            for(String imageUrl : imageList) {
                QiniuContent qiniuContent = qiNiuService.uploadByUrl(imageUrl, qiNiuService.find());

                if(qiniuContent != null ) {
                    imageConvertList.add(qiniuContent.getUrl());
                }
            }

            if(CollUtil.isNotEmpty(imageConvertList)) {
                product.setImage(imageConvertList.get(0));
                product.setSliderImage(CollUtil.join(imageConvertList,","));
            }


        }
        this.saveOrUpdateBatch(productList);
    }


    @Override
    public int uploadProduct(List<Map<String, Object>> readAll) {

        List<YxStoreProduct> yxStoreProductList = new ArrayList<>();


        for(Map<String,Object> data : readAll) {

            String yiyaobao_sku = "";
            String store_name = "";
            String common_name = "";
            String spec = "";
            String manufacturer = "";
            BigDecimal price = new BigDecimal(0);
            String type_name = "";
            String drug_form = "";
            String storage_condition = "";
            String unit = "";
            String license_number = "";
            String basis = "";
            String directions = "";
            String pharmacological_effect = "";
            String characters = "";
            String quality_period = "";
            String drug_interaction = "";
            String indication = "";
            String untoward_effect = "";
            String contraindication = "";

            String disease = "";



            Object sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(sku_Object)) {
                yiyaobao_sku = String.valueOf(sku_Object);
            }

            Object storeName_Object = data.get("药品名称");
            if(ObjectUtil.isNotEmpty(storeName_Object)) {
                store_name = String.valueOf(storeName_Object);
            }

            Object common_name_Object = data.get("药品通用名");
            if(ObjectUtil.isNotEmpty(common_name_Object)) {
                common_name = String.valueOf(common_name_Object);
            }

            Object spec_Object = data.get("规格");
            if(ObjectUtil.isNotEmpty(spec_Object)) {
                spec = String.valueOf(spec_Object);
            }else {
                throw new BadRequestException("规格不能为空");
            }

            Object manufacturer_Object = data.get("生产厂家");
            if(ObjectUtil.isNotEmpty(manufacturer_Object)) {
                manufacturer = String.valueOf(manufacturer_Object);
            }

           /* Object price_Object = data.get("标准价格");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                price = new BigDecimal(String.valueOf(price_Object)) ;
            }*/

            Object type_name_Object = data.get("商品类型");
            if(ObjectUtil.isNotEmpty(type_name_Object)) {
                type_name = String.valueOf(type_name_Object);
            }

            Object drug_form_Object = data.get("剂型");
            if(ObjectUtil.isNotEmpty(drug_form_Object)) {
                drug_form = String.valueOf(drug_form_Object);
            }

            Object storage_condition_Object = data.get("存储条件");
            if(ObjectUtil.isNotEmpty(storage_condition_Object)) {
                storage_condition = String.valueOf(storage_condition_Object);
            }

            Object unit_Object = data.get("单位");
            if(ObjectUtil.isNotEmpty(unit_Object)) {
                unit = String.valueOf(unit_Object);
            }

            Object license_number_Object = data.get("国药准字");
            if(ObjectUtil.isNotEmpty(license_number_Object)) {
                license_number = String.valueOf(license_number_Object);
            }

            Object basis_Object = data.get("主要成分");
            if(ObjectUtil.isNotEmpty(basis_Object)) {
                basis = String.valueOf(basis_Object);
            }

           /* Object directions_Object = data.get("用法用量");
            if(ObjectUtil.isNotEmpty(directions_Object)) {
                directions = String.valueOf(directions_Object);
            }*/

            Object pharmacological_effect_Object = data.get("药理作用");
            if(ObjectUtil.isNotEmpty(pharmacological_effect_Object)) {
                pharmacological_effect = String.valueOf(pharmacological_effect_Object);
            }

            Object characters_Object = data.get("性状");
            if(ObjectUtil.isNotEmpty(characters_Object)) {
                characters = String.valueOf(characters_Object);
            }

            Object quality_period_Object = data.get("保质期");
            if(ObjectUtil.isNotEmpty(quality_period_Object)) {
                quality_period = String.valueOf(quality_period_Object);
            }

            Object drug_interaction_Object = data.get("药物相互作用");
            if(ObjectUtil.isNotEmpty(drug_interaction_Object)) {
                drug_interaction = String.valueOf(drug_interaction_Object);
            }

            Object indication_Object = data.get("适应症");
            if(ObjectUtil.isNotEmpty(indication_Object)) {
                indication = String.valueOf(indication_Object);
            }

            Object untoward_effect_Object = data.get("不良反应");
            if(ObjectUtil.isNotEmpty(untoward_effect_Object)) {
                untoward_effect = String.valueOf(untoward_effect_Object);
            }

            Object contraindication_Object = data.get("禁忌");
            if(ObjectUtil.isNotEmpty(contraindication_Object)) {
                contraindication = String.valueOf(contraindication_Object);
            }

            Object disease_Object = data.get("商品分类");
            if(ObjectUtil.isNotEmpty(disease_Object)) {
                disease = String.valueOf(disease_Object);
            }
            YxStoreProduct yxStoreProduct = null;
            try {
                QueryWrapper queryWrapper = new QueryWrapper<>().eq("yiyaobao_sku",yiyaobao_sku);
                queryWrapper.select("id","disease_id_common","disease_id_cloud","label1","label2","label3","image","slider_image");
                yxStoreProduct = this.getOne(queryWrapper);

                if( ObjectUtil.isEmpty(yxStoreProduct)) {
                    throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
                }

            }catch (Exception e) {
                throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
            }

            yxStoreProduct.setYiyaobaoSku(yiyaobao_sku);
            /*if(StrUtil.isBlank(store_name)) {
                store_name = common_name;
            }*/
            yxStoreProduct.setStoreName(store_name);
            yxStoreProduct.setCommonName(common_name);
            yxStoreProduct.setSpec(spec);
            yxStoreProduct.setManufacturer(manufacturer);
            // 获取价格
            QueryWrapper queryWrapper_attr = new QueryWrapper();
            queryWrapper_attr.eq("yiyaobao_sku",yiyaobao_sku);
            queryWrapper_attr.orderByAsc("price");
            List<YxStoreProductAttrValue> list = yxStoreProductAttrValueService.list(queryWrapper_attr);
            if(CollUtil.isNotEmpty(list)) {
                price = list.get(0).getPrice();
            }
            yxStoreProduct.setPrice(price);
            String type = "";
           /* if(type_name.contains("OTC")) {
                type = 1;
            } else if(type_name.contains("处方药")){
                type = 2;
            } else {
                type = 0;
            }*/
            String typeStr = storeProductMapper.queryProductType(type_name);

            if(StrUtil.isNotBlank(typeStr)) {
                type = typeStr;
            } else {
                throw new BadRequestException("商品类型["+ type_name +"]不存在");
            }

            String is_show_str = "";
            Object is_show_Object = data.get("是否上架");
            if(ObjectUtil.isNotEmpty(is_show_Object)) {
                is_show_str = String.valueOf(is_show_Object);
                if(!"Y".equals(is_show_str) && !"N".equals(is_show_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否上架标记填写错误，应填Y/N");
                }
            }

            yxStoreProduct.setType(type);
            yxStoreProduct.setDrugForm(drug_form);
            yxStoreProduct.setStorageCondition(storage_condition);
            yxStoreProduct.setUnit(unit);
            yxStoreProduct.setUnitName(unit);
            yxStoreProduct.setLicenseNumber(license_number);
            yxStoreProduct.setBasis(basis);
            yxStoreProduct.setDirections(directions);
            yxStoreProduct.setPharmacologicalEffect(pharmacological_effect);
            yxStoreProduct.setCharacters(characters);
            yxStoreProduct.setQualityPeriod(quality_period);
            yxStoreProduct.setDrugInteraction(drug_interaction);
            yxStoreProduct.setIndication(indication);
            yxStoreProduct.setUntowardEffect(untoward_effect);
            yxStoreProduct.setContraindication(contraindication);
           // yxStoreProduct.setOtPrice(price);
           // yxStoreProduct.setVipPrice(price);
          //  yxStoreProduct.setCost(price);
            List<String> diseaseList =  Arrays.asList(disease.split(","));
            List<String> diseaseIdList = new ArrayList<>();
            for(String cate : diseaseList) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("cate_name",cate);
                queryWrapper.eq("is_del",0);
                queryWrapper.ne("pid",0);
                queryWrapper.eq("project_code","");
                queryWrapper.select("id");
                YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper);
                if(yxStoreDisease != null) {
                    diseaseIdList.add(yxStoreDisease.getId().toString());
                }else{
                    log.error("================================商品分类["+ cate +"]不存在");
                    throw new BadRequestException("商品分类["+ cate +"]不存在");
                }

            }
            String diseaseIdCommon = CollUtil.join(diseaseIdList,",");
            yxStoreProduct.setDiseaseIdCommon(diseaseIdCommon);

            String diseaseIdCloud =  yxStoreProduct.getDiseaseIdCloud();

            String diseaseId = "";
            if( StrUtil.isNotBlank(diseaseIdCloud)) {
                diseaseIdList.addAll(new ArrayList<>(Arrays.asList(diseaseIdCloud.split(","))));
                diseaseId = CollUtil.join(diseaseIdList,",");
            } else {
                diseaseId = diseaseIdCommon;
            }

            yxStoreProduct.setDiseaseId(diseaseId);
            // 商品分类大类
            List idList = new ArrayList<Integer>();
            for(String id: diseaseIdList) {
                idList.add(Integer.valueOf(id));
            }

            String diseaseParentIds = yxStoreDiseaseService.findParentids(idList);
            yxStoreProduct.setDiseaseParentId(diseaseParentIds);


            // 是否上架
            if( "N".equals(is_show_str)) {
                yxStoreProduct.setIsShow(0);
            } else {
                yxStoreProduct.setIsShow(1);
            }


            yxStoreProductList.add(yxStoreProduct);

        }
        this.updateBatchById(yxStoreProductList) ;
        return yxStoreProductList.size();
    }

    @Override
    public int uploadProduct4Project(List<Map<String, Object>> readAll,String projectCode) {

        List<YxStoreProduct> yxStoreProductList = new ArrayList<>();

        for(Map<String,Object> data : readAll) {

            // 益药宝sku
            String yiyaobao_sku = "";
            String store_name = "";
            String common_name = "";
            String spec = "";
            String manufacturer = "";

            String type = "";

            BigDecimal price = null;

            String unit = "";

            String is_sales_str = "Y";

            Object yiyaobao_sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(yiyaobao_sku_Object)) {
                yiyaobao_sku = String.valueOf(yiyaobao_sku_Object);
               // product.setYiyaobaoSku(yiyaobao_sku);
            }


            YxStoreProduct product = null;
            try {
                QueryWrapper queryWrapper = new QueryWrapper<>().eq("yiyaobao_sku",yiyaobao_sku);
                queryWrapper.select("id","disease_id_common","disease_id_cloud","image","slider_image");
                product = this.getOne(queryWrapper,false);
                if( ObjectUtil.isEmpty(product)) {
                    // throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
                    product = new YxStoreProduct();
                    product.setYiyaobaoSku(yiyaobao_sku);
                } else {

                }
            }catch (Exception e) {
                throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
            }

            String namePinYin = "";
            String nameShortPinYin = "";
            Object store_name_Object = data.get("药品名称");
            if(ObjectUtil.isNotEmpty(store_name_Object)) {
                store_name = String.valueOf(store_name_Object);
                product.setStoreName(store_name);
                namePinYin = PinYinUtils.getHanziPinYin(store_name) ;
                nameShortPinYin = PinYinUtils.getHanziInitials(store_name) ;
            }
            String commonPinYin = "";
            String commonShortPinYin = "";
            Object common_name_Object = data.get("药品通用名");
            if(ObjectUtil.isNotEmpty(common_name_Object)) {
                common_name = String.valueOf(common_name_Object);
                product.setCommonName(common_name);
                commonPinYin = PinYinUtils.getHanziPinYin(common_name) ;
                commonShortPinYin = PinYinUtils.getHanziInitials(common_name);
            }

            if(commonPinYin == null) {
                commonPinYin = "";
            }

            if(namePinYin ==  null) {
                namePinYin = "";
            }
            String pinYin = "";
            if(commonPinYin.equals(namePinYin)) {
                pinYin = commonPinYin;
            } else {
                pinYin = commonPinYin + "(" + namePinYin + ")";
            }



            if(commonShortPinYin == null) {
                commonShortPinYin = "";
            }
            if(nameShortPinYin == null) {
                nameShortPinYin = "";
            }
            String shortPinYin = "";
            if(commonShortPinYin.equals(nameShortPinYin)) {
                shortPinYin = commonShortPinYin;
            } else {
                shortPinYin = commonShortPinYin + "(" + nameShortPinYin + ")";
            }

            if(StrUtil.isNotBlank(pinYin)) {
                product.setPinyinName(pinYin);
            }

            if(StrUtil.isNotBlank(shortPinYin)) {
                product.setPinyinShortName(shortPinYin);
            }

            Object spec_Object = data.get("规格");
            if(ObjectUtil.isNotEmpty(spec_Object)) {
                spec = String.valueOf(spec_Object);
                product.setSpec(spec);
            }

            Object unit_Object = data.get("单位");
            if(ObjectUtil.isNotEmpty(unit_Object)) {
                unit = String.valueOf(unit_Object);
                product.setUnit(unit);
            }

            String drug_form = "";
            Object drug_form_Object = data.get("剂型");
            if(ObjectUtil.isNotEmpty(drug_form_Object)) {
                drug_form = String.valueOf(drug_form_Object);
                product.setDrugForm(drug_form);
            }

            Object manufacturer_Object = data.get("生产厂家");
            if(ObjectUtil.isNotEmpty(manufacturer_Object)) {
                manufacturer = String.valueOf(manufacturer_Object);
                product.setManufacturer(manufacturer);
            }


            Object price_Object = data.get("零售价");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                price = new BigDecimal(String.valueOf(price_Object));
                product.setPrice(price);
            }

            //批准文号 国药准字/进口药批号
            String licenseNumber = "";
            Object licenseNumber_Object = data.get("国药准字");
            if(ObjectUtil.isNotEmpty(licenseNumber_Object)) {
                licenseNumber = String.valueOf(licenseNumber_Object);
                product.setLicenseNumber(licenseNumber);
            }

            String storageCondition = "";
            Object storageCondition_Object = data.get("存储条件");
            if(ObjectUtil.isNotEmpty(storageCondition_Object)) {
                storageCondition = String.valueOf(storageCondition_Object);
                product.setStorageCondition(storageCondition);

            }


            String quality_period = "";
            Object quality_period_Object = data.get("保质期");
            if(ObjectUtil.isNotEmpty(quality_period_Object)) {
                quality_period = String.valueOf(quality_period_Object);
                product.setQualityPeriod(quality_period);
            }



            String indication = "";
            Object indication_Object = data.get("适用症");
            if(ObjectUtil.isNotEmpty(indication_Object)) {
                indication = String.valueOf(indication_Object);
                product.setIndication(indication);
            }

            String contraindication = "";
            Object contraindication_Object = data.get("禁忌");
            if(ObjectUtil.isNotEmpty(contraindication_Object)) {
                contraindication = String.valueOf(contraindication_Object);
                product.setContraindication(contraindication);
            }


            String is_show_str = "";
            Object is_show_Object = data.get("是否上架");
            if(ObjectUtil.isNotEmpty(is_show_Object)) {
                is_show_str = String.valueOf(is_show_Object);
                if(!"Y".equals(is_show_str) && !"N".equals(is_show_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否上架标记填写错误，应填Y/N");
                }


            }

            String type_name = "";
            Object type_name_Object = data.get("商品类型");
            if(ObjectUtil.isNotEmpty(type_name_Object)) {
                type_name = String.valueOf(type_name_Object);

                String typeStr = storeProductMapper.queryProductType(type_name);

                if(StrUtil.isNotBlank(typeStr)) {
                    type = typeStr;
                    product.setType(type);
                } else {
                    throw new BadRequestException("商品类型["+ type_name +"]不存在");
                }
            }



            product.setIsShow(1);
            product.setIsDel(0);

            String defaultImage = "";
            if (StrUtil.isBlank(product.getImage())) {
                 defaultImage = localUrl + "/file/static/defaultMed.jpg";
                product.setImage(defaultImage);
                product.setSliderImage(defaultImage);
            } else {
                 defaultImage = product.getImage();
            }


            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {

                //商品分类
                String disease = "";

                // 85折药品标记
                String label1 = "";
                // 88折药品标记
                String label2 = "";
                // 5折药品标记
                String label3 = "";

                Object label1_Object = data.get("85折药品标记");
                if(ObjectUtil.isNotEmpty(label1_Object)) {
                    label1 = String.valueOf(label1_Object);
                    if(!"Y".equals(label1) && !"N".equals(label1)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]85折药品标记填写错误，应填Y/N");
                    }
                    product.setLabel1(label1);
                }

                Object label2_Object = data.get("88折药品标记");
                if(ObjectUtil.isNotEmpty(label2_Object)) {
                    label2 = String.valueOf(label2_Object);

                    if(!"Y".equals(label2) && !"N".equals(label2)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]88折药品标记填写错误，应填Y/N");
                    }

                    product.setLabel2(label2);
                }

                Object label3_Object = data.get("5折药品标记");
                if(ObjectUtil.isNotEmpty(label3_Object)) {
                    label3 = String.valueOf(label3_Object);
                    if(!"Y".equals(label3) && !"N".equals(label3)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]5折药品标记填写错误，应填Y/N");
                    }
                    product.setLabel3(label3);
                }

                Object disease_Object = data.get("商品分类");
                if(ObjectUtil.isNotEmpty(disease_Object)) {
                    disease = String.valueOf(disease_Object);
                    List<String> diseaseList = new ArrayList<>();
                    diseaseList =  Arrays.asList(disease.split(","));

                    List<String> diseaseIdList = new ArrayList<>();

                    for(String cate : diseaseList) {
                        QueryWrapper queryWrapper_category = new QueryWrapper();
                        queryWrapper_category.eq("cate_name",cate);
                        queryWrapper_category.eq("is_del",0);
                        queryWrapper_category.ne("pid",0);
                        queryWrapper_category.eq("project_code", projectCode);
                        queryWrapper_category.select("id","pid");
                        YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper_category,false);
                        if(yxStoreDisease != null) {
                            diseaseIdList.add(yxStoreDisease.getId().toString());
                        }else{
                            log.error("================================商品分类["+ cate +"]不存在");
                            throw new BadRequestException("sku["+ yiyaobao_sku +"]商品分类["+ cate +"]不存在");
                        }
                    }
                    String diseaseIdCloud = CollUtil.join(diseaseIdList,",");

                    product.setDiseaseIdCloud(diseaseIdCloud);

                    String diseaseIdCommon = product.getDiseaseIdCommon();

                    String diseaseId = "";
                    if( StrUtil.isNotBlank(diseaseIdCommon)) {

                        diseaseIdList.addAll(new ArrayList<>( Arrays.asList(diseaseIdCommon.split(","))));

                        diseaseId = CollUtil.join(diseaseIdList,",");
                    } else {
                        diseaseId = diseaseIdCloud;
                    }

                    product.setDiseaseId(diseaseId);
                }

                Object is_sales_Object = data.get("是否参与销售");
                if(ObjectUtil.isNotEmpty(is_sales_Object)) {
                    is_sales_str = String.valueOf(is_sales_Object);
                    if(!"Y".equals(is_sales_str) && !"N".equals(is_sales_str)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]是否参与销售标记填写错误，应填Y/N");
                    }

                    if("N".equals(is_sales_str)) {
                        product.setIsSales(1);
                    } else {
                        product.setIsSales(0);
                    }
                }

            }else if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(projectCode)) {
                //商品分类
                String disease = "";
                Object disease_Object = data.get("商品分类");
                if(ObjectUtil.isNotEmpty(disease_Object)) {
                    disease = String.valueOf(disease_Object);
                    List<String> diseaseList = new ArrayList<>();
                    diseaseList =  Arrays.asList(disease.split(","));

                    List<String> diseaseIdList = new ArrayList<>();

                    for(String cate : diseaseList) {
                        QueryWrapper queryWrapper_category = new QueryWrapper();
                        queryWrapper_category.eq("cate_name",cate);
                        queryWrapper_category.eq("is_del",0);
                        queryWrapper_category.ne("pid",0);
                        queryWrapper_category.eq("project_code", ProjectNameEnum.TAIPING_LEXIANG.getValue());
                        queryWrapper_category.select("id","pid");
                        YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper_category,false);
                        if(yxStoreDisease != null) {
                            diseaseIdList.add(yxStoreDisease.getId().toString());
                        }else{
                            log.error("================================商品分类["+ cate +"]不存在");
                            throw new BadRequestException("sku["+ yiyaobao_sku +"]商品分类["+ cate +"]不存在");
                        }
                    }
                    String diseaseIdCloud = CollUtil.join(diseaseIdList,",");

                    product.setDiseaseIdCloud(diseaseIdCloud);

                    String diseaseIdCommon = product.getDiseaseIdCommon();

                    String diseaseId = "";
                    if( StrUtil.isNotBlank(diseaseIdCommon)) {

                        diseaseIdList.addAll(new ArrayList<>( Arrays.asList(diseaseIdCommon.split(","))));

                        diseaseId = CollUtil.join(diseaseIdList,",");
                    } else {
                        diseaseId = diseaseIdCloud;
                    }

                    product.setDiseaseId(diseaseId);
                }
            }

            saveOrUpdate(product);

            YxSystemStore yxSystemStore = storeService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("product_id",product.getId());
            queryWrapper.eq("store_id",yxSystemStore.getId());
            YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper,false);
            if(ObjectUtil.isEmpty(productAttrValue)) {
                productAttrValue = new YxStoreProductAttrValue();
                productAttrValue.setUnique(UUID.randomUUID().toString());
                productAttrValue.setYiyaobaoSku(yiyaobao_sku);
                productAttrValue.setStoreId(yxSystemStore.getId());
                productAttrValue.setProductId(product.getId());
                productAttrValue.setSuk(yxSystemStore.getName());
                productAttrValue.setStock(9999);
                productAttrValue.setSales(0);
                productAttrValue.setPrice(price);
                productAttrValue.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
                productAttrValue.setImage(defaultImage);
                productAttrValue.setCost(price);
                productAttrValue.setIsDel(0);

                yxStoreProductAttrValueService.save(productAttrValue);
            }

            // 加入项目表
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("project_no",projectCode);
            queryWrapper1.eq("product_unique_id",productAttrValue.getUnique());
          //  queryWrapper1.eq("is_del",0);
            Product4project product4project = product4projectService.getOne(queryWrapper1,false);
            if(ObjectUtil.isEmpty(product4project)) {
                product4project = new Product4project();
            }
            product4project.setNum(1);
            product4project.setProductName(product.getStoreName());
            product4project.setProductUniqueId(productAttrValue.getUnique());
            product4project.setProductId(product.getId());
            product4project.setProjectName(ProjectNameEnum.toType(projectCode).getDesc());
            product4project.setProjectNo(projectCode);
            product4project.setStoreId(yxSystemStore.getId());
            product4project.setStoreName(yxSystemStore.getName());
            product4project.setUnitPrice(price);
            product4project.setIsDel(0);
            product4project.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
            product4project.setYiyaobaoSku(yiyaobao_sku);
            // 是否上架
            if( "N".equals(is_show_str)) {
                product4project.setIsShow(0);
            } else {
                product4project.setIsShow(1);
            }

            product4projectService.saveOrUpdate(product4project);

        }
      //  this.updateBatchById(yxStoreProductList) ;
        return yxStoreProductList.size();
    }


    @Override
    public int uploadProduct4Project2(List<Map<String, Object>> readAll,final String projectCode) {

        for(Map<String,Object> data : readAll) {
            String spec = "";
            Object spec_Object = data.get("规格");
            if(ObjectUtil.isNotEmpty(spec_Object)) {
                spec = String.valueOf(spec_Object);
            }else {
                throw new BadRequestException("规格不能为空");
            }

            // 益药宝sku
            String yiyaobao_sku = "";

            BigDecimal price = null;

            Object yiyaobao_sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(yiyaobao_sku_Object)) {
                yiyaobao_sku = String.valueOf(yiyaobao_sku_Object);
                // product.setYiyaobaoSku(yiyaobao_sku);
            } else {
                throw new BadRequestException("益药宝sku不能为空");
            }

            YxStoreProduct product = null;
            try {
                QueryWrapper queryWrapper = new QueryWrapper<>().eq("yiyaobao_sku",yiyaobao_sku);
                queryWrapper.select("id","disease_id_common","disease_id_cloud","store_name","disease_id");
                product = this.getOne(queryWrapper,false);
                if( ObjectUtil.isEmpty(product)) {
                   //  throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
                    product = new YxStoreProduct();
                    product.setYiyaobaoSku(yiyaobao_sku);
                } else {

                }
            }catch (Exception e) {
                throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
            }


            LambdaUpdateWrapper<YxStoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(YxStoreProduct::getId,product.getId());


            Object price_Object = data.get("项目零售价");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                price = new BigDecimal(String.valueOf(price_Object));
            }

            Object settlement_price_Object = data.get("项目结算价");
            BigDecimal settlement_price = null;
            if(ObjectUtil.isNotEmpty(settlement_price_Object)) {
                settlement_price = new BigDecimal(String.valueOf(settlement_price_Object));
            }


            String is_show_str = "";
            Object is_show_Object = data.get("是否上架(Y/N)");
            if(ObjectUtil.isNotEmpty(is_show_Object)) {
                is_show_str = String.valueOf(is_show_Object);
                if(!"Y".equals(is_show_str) && !"N".equals(is_show_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否上架标记填写错误，应填Y/N");
                }


            }

//商品分类
            String disease = "";
            Object disease_Object = data.get("商品分类");
            if(ObjectUtil.isNotEmpty(disease_Object)) {
                disease = String.valueOf(disease_Object);
                List<String> diseaseList = new ArrayList<>();
                diseaseList =  Arrays.asList(disease.split(","));

                List<String> diseaseIdList = new ArrayList<>();

                for(String cate : diseaseList) {
                    QueryWrapper queryWrapper_category = new QueryWrapper();
                    queryWrapper_category.eq("cate_name",cate);
                    queryWrapper_category.eq("is_del",0);
                    queryWrapper_category.ne("pid",0);
                    queryWrapper_category.eq("project_code", ProjectNameEnum.TAIPING_LEXIANG.getValue());
                    queryWrapper_category.select("id","pid");
                    YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper_category,false);
                    if(yxStoreDisease != null) {
                        diseaseIdList.add(yxStoreDisease.getId().toString());
                    }else{
                        log.error("================================商品分类["+ cate +"]不存在");
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]商品分类["+ cate +"]不存在");
                    }
                }
                String diseaseIdCloud = CollUtil.join(diseaseIdList,",");

                product.setDiseaseIdCloud(diseaseIdCloud);

                String diseaseIdCommon = product.getDiseaseIdCommon();

                String diseaseId = "";
                if( StrUtil.isNotBlank(diseaseIdCommon)) {

                    diseaseIdList.addAll(new ArrayList<>( Arrays.asList(diseaseIdCommon.split(","))));

                    diseaseId = CollUtil.join(diseaseIdList,",");
                } else {
                    diseaseId = diseaseIdCloud;
                }

                product.setDiseaseId(diseaseId);

                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseId,diseaseId);
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCloud,diseaseIdCloud);
            } else {
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseId,product.getDiseaseId());
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCloud,product.getDiseaseIdCloud());
            }

            YxSystemStore yxSystemStore = storeService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("product_id",product.getId());
            queryWrapper.eq("store_id",yxSystemStore.getId());
            queryWrapper.select("`unique`");
            YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper,false);
            if(ObjectUtil.isEmpty(productAttrValue)) {
                throw new BadRequestException( ShopConstants.STORENAME_GUANGZHOU_CLOUD + "不销售此sku["+ yiyaobao_sku +"]");
            }

            if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(projectCode)) {
                // 85折药品标记
                String label1 = "";
                // 88折药品标记
                String label2 = "";
                // 5折药品标记
                String label3 = "";

                Object label1_Object = data.get("85折药品标记");
                if(ObjectUtil.isNotEmpty(label1_Object)) {
                    label1 = String.valueOf(label1_Object);
                    if(!"Y".equals(label1) && !"N".equals(label1)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]85折药品标记填写错误，应填Y/N");
                    }
                    product.setLabel1(label1);

                    lambdaUpdateWrapper.set(YxStoreProduct::getLabel1,label1);
                }

                Object label2_Object = data.get("88折药品标记");
                if(ObjectUtil.isNotEmpty(label2_Object)) {
                    label2 = String.valueOf(label2_Object);

                    if(!"Y".equals(label2) && !"N".equals(label2)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]88折药品标记填写错误，应填Y/N");
                    }

                    product.setLabel2(label2);

                    lambdaUpdateWrapper.set(YxStoreProduct::getLabel2,label2);
                }

                Object label3_Object = data.get("5折药品标记");
                if(ObjectUtil.isNotEmpty(label3_Object)) {
                    label3 = String.valueOf(label3_Object);
                    if(!"Y".equals(label3) && !"N".equals(label3)) {
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]5折药品标记填写错误，应填Y/N");
                    }
                    product.setLabel3(label3);


                    lambdaUpdateWrapper.set(YxStoreProduct::getLabel3,label3);
                }

            }



            // 更新商品信息
            this.update(lambdaUpdateWrapper);

            // 加入项目表
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("project_no",projectCode);
            queryWrapper1.eq("product_unique_id",productAttrValue.getUnique());
            //  queryWrapper1.eq("is_del",0);
            Product4project product4project = product4projectService.getOne(queryWrapper1,false);
            if(ObjectUtil.isEmpty(product4project)) {
                product4project = new Product4project();
                product4project.setUnitPrice(null);
                product4project.setSettlementPrice(null);
            }
            product4project.setNum(1);
            product4project.setProductName(product.getStoreName());
            product4project.setProductUniqueId(productAttrValue.getUnique());
            product4project.setProductId(product.getId());
            product4project.setProjectName(ProjectNameEnum.toType(projectCode).getDesc());
            product4project.setProjectNo(projectCode);
            product4project.setStoreId(yxSystemStore.getId());
            product4project.setStoreName(yxSystemStore.getName());
            product4project.setUnitPrice(price);
            product4project.setSettlementPrice(settlement_price);
            product4project.setIsDel(0);
            product4project.setYiyaobaoSku(yiyaobao_sku);
            product4project.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
            // 是否上架
            if( "N".equals(is_show_str)) {
                product4project.setIsShow(0);
            } else {
                product4project.setIsShow(1);
            }

            product4projectService.saveOrUpdate(product4project);

        }
        //  this.updateBatchById(yxStoreProductList) ;
        productExecutor.execute(()->{
            dualProuctDisease2Redis(projectCode);
        });
        return readAll.size();
    }

    @Override
    @Transactional
    public int uploadProduct4Lingyuanzhi(List<Map<String, Object>> readAll,String projectCode) {
        for(Map<String,Object> data : readAll) {

            // 益药宝sku
            String yiyaobao_sku = "";

            BigDecimal price = null;
            BigDecimal drugstore_price = new BigDecimal(0);
            Object yiyaobao_sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(yiyaobao_sku_Object)) {
                yiyaobao_sku = String.valueOf(yiyaobao_sku_Object);
                // product.setYiyaobaoSku(yiyaobao_sku);
            } else {
                throw new BadRequestException("益药宝sku不能为空");
            }

            YxStoreProduct product = null;

            QueryWrapper queryWrapper = new QueryWrapper<>().eq("yiyaobao_sku",yiyaobao_sku);
            queryWrapper.select("id","disease_id_common","disease_id_cloud","store_name","disease_id","yiyaobao_sku");
            product = this.getOne(queryWrapper,false);
            if( ObjectUtil.isEmpty(product)) {
                //  throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
                product = new YxStoreProduct();
                product.setYiyaobaoSku(yiyaobao_sku);
                product.setIsShow(1);
                product.setIsDel(0);
                product.setIsGroup(1);
                String defaultImage = localUrl + "/file/static/defaultMed.jpg";
                product.setImage(defaultImage);
                product.setSliderImage(defaultImage);
            } else {

            }


            LambdaUpdateWrapper<YxStoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(YxStoreProduct::getId,product.getId());


            Object price_Object = data.get("项目零售价");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                price = new BigDecimal(String.valueOf(price_Object));
            }

            Object settlement_price_Object = data.get("项目结算价");
            BigDecimal settlement_price = null;
            if(ObjectUtil.isNotEmpty(settlement_price_Object)) {
                settlement_price = new BigDecimal(String.valueOf(settlement_price_Object));
            }

            Object drugstore_price_Object = data.get("药房零售价");
            if(ObjectUtil.isNotEmpty(drugstore_price_Object)) {
                drugstore_price = new BigDecimal(String.valueOf(drugstore_price_Object));
            }

            String common_name = "";
            Object common_name_Object = data.get("药品通用名");
            if(ObjectUtil.isNotEmpty(common_name_Object)) {
                common_name = String.valueOf(common_name_Object);
                product.setCommonName(common_name);
                lambdaUpdateWrapper.set(YxStoreProduct::getCommonName,common_name);

                String commonPinYin = PinYinUtils.getHanziPinYin(common_name) ;

                product.setPinyinName(commonPinYin);
                lambdaUpdateWrapper.set(YxStoreProduct::getPinyinName,commonPinYin);
            }

            String is_show_str = "";
            Object is_show_Object = data.get("是否上架(Y/N)");
            if(ObjectUtil.isNotEmpty(is_show_Object)) {
                is_show_str = String.valueOf(is_show_Object);
                if(!"Y".equals(is_show_str) && !"N".equals(is_show_str)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]是否上架标记填写错误，应填Y/N");
                }


            }

//商品分类
            String disease = "";
            Object disease_Object = data.get("商品分类");
            if(ObjectUtil.isNotEmpty(disease_Object)) {
                disease = String.valueOf(disease_Object);
                List<String> diseaseList = new ArrayList<>();
                diseaseList =  Arrays.asList(disease.split(","));

                List<String> diseaseIdList = new ArrayList<>();

                for(String cate : diseaseList) {
                    QueryWrapper queryWrapper_category = new QueryWrapper();
                    queryWrapper_category.eq("cate_name",cate);
                    queryWrapper_category.eq("is_del",0);
                    queryWrapper_category.ne("pid",0);
                    queryWrapper_category.eq("project_code", ProjectNameEnum.LINGYUANZHI.getValue());
                    queryWrapper_category.select("id","pid");
                    YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper_category,false);
                    if(yxStoreDisease != null) {
                        diseaseIdList.add(yxStoreDisease.getId().toString());
                    }else{
                        log.error("================================商品分类["+ cate +"]不存在");
                        throw new BadRequestException("sku["+ yiyaobao_sku +"]商品分类["+ cate +"]不存在");
                    }
                }
                String diseaseIdCloud = CollUtil.join(diseaseIdList,",");

                product.setDiseaseIdCloud(diseaseIdCloud);

                String diseaseIdCommon = product.getDiseaseIdCommon();

                String diseaseId = "";
                if( StrUtil.isNotBlank(diseaseIdCommon)) {

                    diseaseIdList.addAll(new ArrayList<>( Arrays.asList(diseaseIdCommon.split(","))));

                    diseaseId = CollUtil.join(diseaseIdList,",");
                } else {
                    diseaseId = diseaseIdCloud;
                }

                product.setDiseaseId(diseaseId);

                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseId,diseaseId);
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCloud,diseaseIdCloud);
            } else {
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseId,product.getDiseaseId());
                lambdaUpdateWrapper.set(YxStoreProduct::getDiseaseIdCloud,product.getDiseaseIdCloud());
            }
// 更新商品信息
            if(product.getId() == null) {
                this.save(product);
            }else {
                lambdaUpdateWrapper.set(YxStoreProduct::getIsGroup,1);
                this.update(lambdaUpdateWrapper);
            }



            YxSystemStore yxSystemStore = storeService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
            QueryWrapper queryWrapper_attrvalue = new QueryWrapper();
            queryWrapper_attrvalue.eq("yiyaobao_sku",product.getYiyaobaoSku());
            queryWrapper_attrvalue.eq("store_id",yxSystemStore.getId());
            queryWrapper_attrvalue.select("`unique`");
            YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_attrvalue,false);
            if(ObjectUtil.isEmpty(productAttrValue)) {
                productAttrValue = new YxStoreProductAttrValue();
                productAttrValue.setProductId(product.getId());
                productAttrValue.setStock(9999);
                productAttrValue.setCost( new BigDecimal(0));
                productAttrValue.setPrice(drugstore_price);
                productAttrValue.setUnique(UUID.randomUUID().toString());
                productAttrValue.setSuk(ShopConstants.STORENAME_GUANGZHOU_CLOUD);
                productAttrValue.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
                productAttrValue.setIsDel(0);
                productAttrValue.setYiyaobaoSku(yiyaobao_sku);
                productAttrValue.setStoreId(yxSystemStore.getId());
                productAttrValue.setSales(0);
                productAttrValue.setCommonName(common_name);

                yxStoreProductAttrValueService.save(productAttrValue);
            }



            // 加入项目表
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("project_no",projectCode);
            queryWrapper1.eq("product_unique_id",productAttrValue.getUnique());
            //  queryWrapper1.eq("is_del",0);
            Product4project product4project = product4projectService.getOne(queryWrapper1,false);
            if(ObjectUtil.isEmpty(product4project)) {
                product4project = new Product4project();
                product4project.setUnitPrice(null);
                product4project.setSettlementPrice(null);
            }
            product4project.setNum(1);
            product4project.setProductName(product.getStoreName());
            product4project.setProductUniqueId(productAttrValue.getUnique());
            product4project.setProductId(product.getId());
            product4project.setProjectName(ProjectNameEnum.toType(projectCode).getDesc());
            product4project.setProjectNo(projectCode);
            product4project.setStoreId(yxSystemStore.getId());
            product4project.setStoreName(yxSystemStore.getName());
            product4project.setUnitPrice(price);
            product4project.setSettlementPrice(settlement_price);
            product4project.setIsDel(0);
            product4project.setYiyaobaoSku(yiyaobao_sku);
            product4project.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
            // 是否上架
            if( "N".equals(is_show_str)) {
                product4project.setIsShow(0);
            } else {
                product4project.setIsShow(1);
            }

            product4projectService.saveOrUpdate(product4project);

        }
        //  this.updateBatchById(yxStoreProductList) ;
        productExecutor.execute(()->{
            dualProuctDisease2Redis(projectCode);
        });
        return readAll.size();
    }

    @Override
    public int uploadProduct4ProjectSimple(List<Map<String, Object>> readAll) {

        List<YxStoreProduct> yxStoreProductList = new ArrayList<>();

        for(Map<String,Object> data : readAll) {

            // 益药宝sku
            String yiyaobao_sku = "";

            //商品分类
            String disease = "";

            // 85折药品标记
            String label1 = "";
            // 88折药品标记
            String label2 = "";
            // 5折药品标记
            String label3 = "";

            Object yiyaobao_sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(yiyaobao_sku_Object)) {
                yiyaobao_sku = String.valueOf(yiyaobao_sku_Object);
            }


            Object label1_Object = data.get("85折药品标记");
            if(ObjectUtil.isNotEmpty(label1_Object)) {
                label1 = String.valueOf(label1_Object);
                if(!"Y".equals(label1) && !"N".equals(label1)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]85折药品标记填写错误，应填Y/N");
                }
            }

            Object label2_Object = data.get("88折药品标记");
            if(ObjectUtil.isNotEmpty(label2_Object)) {
                label2 = String.valueOf(label2_Object);

                if(!"Y".equals(label2) && !"N".equals(label2)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]88折药品标记填写错误，应填Y/N");
                }
            }

            Object label3_Object = data.get("5折药品标记");
            if(ObjectUtil.isNotEmpty(label3_Object)) {
                label3 = String.valueOf(label3_Object);
                if(!"Y".equals(label3) && !"N".equals(label3)) {
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]5折药品标记填写错误，应填Y/N");
                }
            }

            Object disease_Object = data.get("商品分类");
            if(ObjectUtil.isNotEmpty(disease_Object)) {
                disease = String.valueOf(disease_Object);
            }


            YxStoreProduct product = null;
            try {
                QueryWrapper queryWrapper = new QueryWrapper<>().eq("yiyaobao_sku",yiyaobao_sku);
                queryWrapper.select("id","disease_id_common","disease_id_cloud");
                product = this.getOne(queryWrapper,false);
                if( ObjectUtil.isEmpty(product)) {
                     throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
                   // product = new YxStoreProduct();
                } else {

                }
            }catch (Exception e) {
                throw new BadRequestException("益药宝sku["+ yiyaobao_sku +"]找不到");
            }

            product.setLabel1(label1);
            product.setLabel2(label2);
            product.setLabel3(label3);

            List<String> diseaseList = new ArrayList<>();
            if(StrUtil.isNotBlank(disease)) {
                diseaseList =  Arrays.asList(disease.split(","));
            }
            List<String> diseaseIdList = new ArrayList<>();

            for(String cate : diseaseList) {
                QueryWrapper queryWrapper_category = new QueryWrapper();
                queryWrapper_category.eq("cate_name",cate);
                queryWrapper_category.eq("is_del",0);
                queryWrapper_category.ne("pid",0);
                queryWrapper_category.eq("project_code", ProjectNameEnum.TAIPING_LEXIANG.getValue());
                queryWrapper_category.select("id","pid");
                YxStoreDisease yxStoreDisease = yxStoreDiseaseService.getOne(queryWrapper_category,false);
                if(yxStoreDisease != null) {
                    diseaseIdList.add(yxStoreDisease.getId().toString());
                }else{
                    log.error("================================商品分类["+ cate +"]不存在");
                    throw new BadRequestException("sku["+ yiyaobao_sku +"]商品分类["+ cate +"]不存在");
                }
            }
            String diseaseIdCloud = CollUtil.join(diseaseIdList,",");
            product.setDiseaseIdCloud(diseaseIdCloud);

            String diseaseIdCommon = product.getDiseaseIdCommon();

            String diseaseId = "";
            if( StrUtil.isNotBlank(diseaseIdCommon)) {

                diseaseIdList.addAll(new ArrayList<>( Arrays.asList(diseaseIdCommon.split(","))));

                diseaseId = CollUtil.join(diseaseIdList,",");
            } else {
                diseaseId = diseaseIdCloud;
            }

            product.setDiseaseId(diseaseId);

            //    yxStoreProductList.add(product);

            saveOrUpdate(product);

           /* YxSystemStore yxSystemStore = storeService.getOne(new QueryWrapper<YxSystemStore>().eq("name", ShopConstants.STORENAME_GUANGZHOU_CLOUD));
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("product_id",product.getId());
            queryWrapper.eq("store_id",yxSystemStore.getId());
            YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper,false);
            if(ObjectUtil.isEmpty(productAttrValue)) {
                productAttrValue = new YxStoreProductAttrValue();
                productAttrValue.setUnique(UUID.randomUUID().toString());

            }

            productAttrValue.setYiyaobaoSku(yiyaobao_sku);
            productAttrValue.setStoreId(yxSystemStore.getId());
            productAttrValue.setProductId(product.getId());
            productAttrValue.setSuk(yxSystemStore.getName());
            productAttrValue.setStock(9999);
            productAttrValue.setSales(0);
            productAttrValue.setPrice(price);
            productAttrValue.setYiyaobaoSellerId(yxSystemStore.getYiyaobaoId());
            productAttrValue.setImage("http://pic.yiyao-mall.com/%E7%9B%8A%E8%8D%AF-%E8%8D%AF%E5%93%81.jpg");
            productAttrValue.setCost(price);
            productAttrValue.setIsDel(0);


            yxStoreProductAttrValueService.saveOrUpdate(productAttrValue);


            // 加入项目表

            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("project_no",ProjectNameEnum.TAIPING_LEXIANG.getValue());
            queryWrapper1.eq("product_unique_id",productAttrValue.getUnique());
            Product4project product4project = product4projectService.getOne(queryWrapper1,false);
            if(ObjectUtil.isEmpty(product4project)) {
                product4project = new Product4project();
            }
            product4project.setNum(1);
            product4project.setProductName(product.getStoreName());
            product4project.setProductUniqueId(productAttrValue.getUnique());
            product4project.setProductId(product.getId());
            product4project.setProjectName(ProjectNameEnum.TAIPING_LEXIANG.getDesc());
            product4project.setProjectNo(ProjectNameEnum.TAIPING_LEXIANG.getValue());
            product4project.setStoreId(yxSystemStore.getId());
            product4project.setStoreName(yxSystemStore.getName());

            product4projectService.saveOrUpdate(product4project);*/

        }
        //  this.updateBatchById(yxStoreProductList) ;
        return yxStoreProductList.size();
    }


    private void dualProductAttr(YxStoreProduct yxStoreProduct) {

        YxStoreProductAttr attr = yxStoreProductAttrService.getOne(new QueryWrapper<YxStoreProductAttr>().eq("product_id",yxStoreProduct.getId()).eq("attr_name","药店"));
        if(attr == null) {
            attr = new YxStoreProductAttr();
            attr.setAttrName("药店");
            attr.setProductId(yxStoreProduct.getId());
            attr.setAttrValues("广州上医众协药房有限公司");
            yxStoreProductAttrService.save(attr);
        }

        YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(new QueryWrapper<YxStoreProductAttrValue>().eq("product_id",yxStoreProduct.getId()).eq("suk","广州上医众协药房有限公司"));

        if(yxStoreProductAttrValue == null) {
            yxStoreProductAttrValue = new YxStoreProductAttrValue();
            yxStoreProductAttrValue.setImage("http://pic.yiyao-mall.com/0e3763257b1d47a5836952bdac616e89.jpg");
            yxStoreProductAttrValue.setCost(yxStoreProduct.getCost());
            yxStoreProductAttrValue.setPrice(yxStoreProduct.getPrice());
            yxStoreProductAttrValue.setStock(100);
            yxStoreProductAttrValue.setUnique(UUID.randomUUID().toString().replace("-",""));
            yxStoreProductAttrValue.setProductId(yxStoreProduct.getId());
            yxStoreProductAttrValue.setAttrId(attr.getId());
            YxSystemStore store = storeService.getOne(new QueryWrapper<YxSystemStore>().eq("name","广州上医众协药房有限公司"));
            if(store != null) {
                yxStoreProductAttrValue.setSuk(store.getName());
                yxStoreProductAttrValue.setStoreId(store.getId());

                yxStoreProductAttrValueService.save(yxStoreProductAttrValue);
            }
        }else {
            yxStoreProductAttrValue.setCost(yxStoreProduct.getCost());
            yxStoreProductAttrValue.setPrice(yxStoreProduct.getPrice());
            yxStoreProductAttrValue.setAttrId(attr.getId());
            yxStoreProductAttrValueService.updateById(yxStoreProductAttrValue);
        }

    }

    /**
     * 返回商品库存
     * @param productId
     * @param unique
     * @return
     */
    @Override
    public int getProductStock(int productId, String unique) {
        if(StrUtil.isEmpty(unique)){
            return this.getById(productId).getStock();
        }else{
            return yxStoreProductAttrValueService.uniqueByStock(unique);
        }

    }

    /**
     * 库存与销量
     * @param num
     * @param productId
     * @param unique
     */
    @Override
    public void decProductStock(int num, int productId, String unique) {
        if(StrUtil.isNotEmpty(unique)){
            storeProductAttrService.decProductAttrStock(num,productId,unique);
            storeProductMapper.incSales(num,productId);
        }else{
            storeProductMapper.decStockIncSales(num,productId);
        }
    }



    @Override
    //@Cacheable
    public Map<String, Object> queryAll4pc(YxStoreProductQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxStoreProduct> page = new PageInfo<>(queryAll4msh(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);

        List<YxStoreProductDto> yxStoreProductDtoList = generator.convert(page.getList(), YxStoreProductDto.class);


        map.put("content", yxStoreProductDtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    //@Cacheable
    public List<YxStoreProduct> queryAll4msh(YxStoreProductQueryCriteria criteria){
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(YxStoreProduct.class, criteria);
        queryWrapper.select("id","image","slider_image","store_name","store_info","keyword","cate_id","is_show",
                "is_del","yiyaobao_sku","license_number","common_name","drug_form","spec","manufacturer"
                ,"unit"
        );
        //关键字搜索
        if(StrUtil.isNotEmpty(criteria.getKeyword())){
            //  wrapper.like("store_name",productQueryParam.getKeyword());
            String pinYin = PinYinUtils.getHanziPinYin(criteria.getKeyword());

            queryWrapper.apply(" ( store_name like concat('%',{0} ,'%') or common_name like concat('%',{1} ,'%')  or keyword like concat('%',{2} ,'%') or pinyin_name like concat('%',{3} ,'%') or FIND_IN_SET({4},keyword) )",criteria.getKeyword(),criteria.getKeyword(),criteria.getKeyword(),pinYin,criteria.getKeyword());
        }

        if(StrUtil.isNotBlank(criteria.getProjectCode()) && criteria.getIsGZStoreId()!=null && criteria.getIsGZStoreId()==1) {
            queryWrapper.apply("EXISTS (SELECT 1 FROM product4project p WHERE p.is_del = 0 and p.is_show = 1 and p.product_id = yx_store_product.id AND p.project_no = {0})",criteria.getProjectCode());

            queryWrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product_attr_value yspav WHERE yspav.product_id = yx_store_product.id AND yspav.is_del = 0)");
        }else if(StrUtil.isNotBlank(criteria.getProjectCode()) && criteria.getIsGZStoreId()!=null && criteria.getIsGZStoreId()==0){
            queryWrapper.apply(" EXISTS (SELECT 1 FROM yx_store_product_attr_value yspav WHERE yspav.product_id = yx_store_product.id AND yspav.is_del = 0 AND stock > 0 AND yspav.store_id = {0})",criteria.getStoreId());
        }

        List<YxStoreProduct> yxStoreProductList = baseMapper.selectList(queryWrapper);



        for (YxStoreProduct yxStoreProduct : yxStoreProductList) {

            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getId,criteria.getStoreId()));

            LambdaQueryWrapper<YxStoreProductAttrValue> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(YxStoreProductAttrValue::getProductId,yxStoreProduct.getId());
            queryWrapper1.eq(YxStoreProductAttrValue::getStoreId,yxSystemStore.getId());
            queryWrapper1.eq(YxStoreProductAttrValue::getIsDel,0);

            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(queryWrapper1);

            LambdaQueryWrapper<Product4project> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Product4project::getProjectNo, criteria.getProjectCode());
            lambdaQueryWrapper.eq(Product4project::getProductId, yxStoreProduct.getId());
            lambdaQueryWrapper.isNotNull(Product4project::getUnitPrice);
            lambdaQueryWrapper.eq(Product4project::getIsDel,0);
            Product4project product4project = product4projectService.getOne(lambdaQueryWrapper);
            if (product4project != null) {
                yxStoreProduct.setPrice(product4project.getUnitPrice());
                //yxStoreProduct.setOtPrice(yxStoreProductAttrValue.getPrice());
            } else {  // 项目中的价格没有维护，取门店的价格
                yxStoreProduct.setPrice(yxStoreProductAttrValue.getPrice());
               // yxStoreProduct.setOtPrice(yxStoreProductAttrValue.getPrice());
            }
            yxStoreProduct.setUnique(yxStoreProductAttrValue.getUnique());
            if(StringUtils.isEmpty(yxStoreProduct.getStoreName())){
                yxStoreProduct.setStoreName("");
            }
        }


        return yxStoreProductList;
    }

    @Override
    public void updateDideaseParent() {

        List<YxStoreProduct> productList = list();
        for(YxStoreProduct product :productList) {
            String diseaseId = product.getDiseaseId();
            if(StrUtil.isNotBlank(diseaseId)) {
                List<String> diseaseStrids = Arrays.asList(diseaseId.split(","));
                List<Integer> diseaseids = new ArrayList<>();
                for(String id:diseaseStrids){
                    diseaseids.add(Integer.valueOf(id));
                }

               String parentIds = yxStoreDiseaseService.findParentids(diseaseids);

                UpdateWrapper updateWrapper= new UpdateWrapper();
                updateWrapper.set("disease_parent_id",parentIds);
                updateWrapper.eq("id",product.getId());

                update(updateWrapper);

            }



        }
    }

    @Override
    public void updatePriceStock() {
storeProductMapper.updatePriceStock();
    }

    @Override
    public void updatePrice(YxStoreProduct resources) {
        //
        if(StrUtil.isNotBlank(resources.getProjectCode()) ) {
            LambdaUpdateWrapper<Product4project> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.eq(Product4project::getProductUniqueId,resources.getUnique());
            updateWrapper.eq(Product4project::getProjectNo,resources.getProjectCode());
            updateWrapper.set(Product4project::getUnitPrice,resources.getPrice());
            updateWrapper.set(Product4project::getUpdateTime,new Timestamp(System.currentTimeMillis()));
            updateWrapper.set(Product4project::getSettlementPrice,resources.getSettlementPrice());
            product4projectService.update(updateWrapper);
        }

    }

    @Override
    public YxStoreProductQueryVo selectById(Integer productId) {
        YxStoreProduct arg0= storeProductMapper.selectById(productId);
        if ( arg0 == null ) {
            return null;
        }
        YxStoreProductQueryVo yxStoreProductQueryVo = new YxStoreProductQueryVo();
        yxStoreProductQueryVo.setId( arg0.getId() );
        yxStoreProductQueryVo.setMerId( arg0.getMerId() );
        yxStoreProductQueryVo.setImage( arg0.getImage() );
        yxStoreProductQueryVo.setSliderImage( arg0.getSliderImage() );
        yxStoreProductQueryVo.setStoreName( arg0.getStoreName() );
        yxStoreProductQueryVo.setStoreInfo( arg0.getStoreInfo() );
        yxStoreProductQueryVo.setKeyword( arg0.getKeyword() );
        yxStoreProductQueryVo.setBarCode( arg0.getBarCode() );
        yxStoreProductQueryVo.setCateId( arg0.getCateId() );
        yxStoreProductQueryVo.setPrice( arg0.getPrice() );
        yxStoreProductQueryVo.setVipPrice( arg0.getVipPrice() );
        yxStoreProductQueryVo.setOtPrice( arg0.getOtPrice() );
        yxStoreProductQueryVo.setPostage( arg0.getPostage() );
        yxStoreProductQueryVo.setUnitName( arg0.getUnitName() );
        yxStoreProductQueryVo.setSort( arg0.getSort() );
        yxStoreProductQueryVo.setSales( arg0.getSales() );
        yxStoreProductQueryVo.setStock( arg0.getStock() );
        yxStoreProductQueryVo.setIsShow( arg0.getIsShow() );
        yxStoreProductQueryVo.setIsHot( arg0.getIsHot() );
        yxStoreProductQueryVo.setIsBenefit( arg0.getIsBenefit() );
        yxStoreProductQueryVo.setIsBest( arg0.getIsBest() );
        yxStoreProductQueryVo.setIsNew( arg0.getIsNew() );
        yxStoreProductQueryVo.setDescription( arg0.getDescription() );
        yxStoreProductQueryVo.setAddTime( arg0.getAddTime() );
        yxStoreProductQueryVo.setIsPostage( arg0.getIsPostage() );
        yxStoreProductQueryVo.setIsDel( arg0.getIsDel() );
        yxStoreProductQueryVo.setMerUse( arg0.getMerUse() );
        yxStoreProductQueryVo.setGiveIntegral( arg0.getGiveIntegral() );
        yxStoreProductQueryVo.setCost( arg0.getCost() );
        yxStoreProductQueryVo.setIsSeckill( arg0.getIsSeckill() );
        yxStoreProductQueryVo.setIsBargain( arg0.getIsBargain() );
        yxStoreProductQueryVo.setIsGood( arg0.getIsGood() );
        yxStoreProductQueryVo.setFicti( arg0.getFicti() );
        yxStoreProductQueryVo.setBrowse( arg0.getBrowse() );
        yxStoreProductQueryVo.setCodePath( arg0.getCodePath() );
        yxStoreProductQueryVo.setSoureLink( arg0.getSoureLink() );
        yxStoreProductQueryVo.setYiyaobaoSku( arg0.getYiyaobaoSku() );
        yxStoreProductQueryVo.setLicenseNumber( arg0.getLicenseNumber() );
        yxStoreProductQueryVo.setCommonName( arg0.getCommonName() );
        yxStoreProductQueryVo.setEnglishName( arg0.getEnglishName() );
        yxStoreProductQueryVo.setPinyinName( arg0.getPinyinName() );
        yxStoreProductQueryVo.setPinyinShortName( arg0.getPinyinShortName() );
        yxStoreProductQueryVo.setDrugFormCode( arg0.getDrugFormCode() );
        yxStoreProductQueryVo.setDrugForm( arg0.getDrugForm() );
        yxStoreProductQueryVo.setSpec( arg0.getSpec() );
        yxStoreProductQueryVo.setPackages( arg0.getPackages() );
        yxStoreProductQueryVo.setManufacturer( arg0.getManufacturer() );
        yxStoreProductQueryVo.setStorageCondition( arg0.getStorageCondition() );
        yxStoreProductQueryVo.setIsBasic( arg0.getIsBasic() );
        yxStoreProductQueryVo.setIsBirthControl( arg0.getIsBirthControl() );
        yxStoreProductQueryVo.setIsStimulant( arg0.getIsStimulant() );
        yxStoreProductQueryVo.setIsPsychotropic( arg0.getIsPsychotropic() );
        yxStoreProductQueryVo.setTaxRate( arg0.getTaxRate() );
        yxStoreProductQueryVo.setUnitCode( arg0.getUnitCode() );
        yxStoreProductQueryVo.setUnit( arg0.getUnit() );
        yxStoreProductQueryVo.setPackageUnit( arg0.getPackageUnit() );
        yxStoreProductQueryVo.setUnitExchange( arg0.getUnitExchange() );
        yxStoreProductQueryVo.setIsOpenStock( arg0.getIsOpenStock() );
        yxStoreProductQueryVo.setMedLength( arg0.getMedLength() );
        yxStoreProductQueryVo.setMedWidth( arg0.getMedWidth() );
        yxStoreProductQueryVo.setMedHeight( arg0.getMedHeight() );
        yxStoreProductQueryVo.setMedGrossWeight( arg0.getMedGrossWeight() );
        yxStoreProductQueryVo.setMedCapacity( arg0.getMedCapacity() );
        yxStoreProductQueryVo.setMediumAmount( arg0.getMediumAmount() );
        yxStoreProductQueryVo.setMediumUnitCode( arg0.getMediumUnitCode() );
        yxStoreProductQueryVo.setMediumUnitName( arg0.getMediumUnitName() );
        yxStoreProductQueryVo.setMediumLength( arg0.getMediumLength() );
        yxStoreProductQueryVo.setMediumWidth( arg0.getMediumWidth() );
        yxStoreProductQueryVo.setMediumHeight( arg0.getMediumHeight() );
        yxStoreProductQueryVo.setMediumWeight( arg0.getMediumWeight() );
        yxStoreProductQueryVo.setMediumCapacity( arg0.getMediumCapacity() );
        yxStoreProductQueryVo.setLargeAmount( arg0.getLargeAmount() );
        yxStoreProductQueryVo.setLargeUnitCode( arg0.getLargeUnitCode() );
        yxStoreProductQueryVo.setLargeUnitName( arg0.getLargeUnitName() );
        yxStoreProductQueryVo.setLargeLength( arg0.getLargeLength() );
        yxStoreProductQueryVo.setLargeWidth( arg0.getLargeWidth() );
        yxStoreProductQueryVo.setLargeHeight( arg0.getLargeHeight() );
        yxStoreProductQueryVo.setLargeWeight( arg0.getLargeWeight() );
        yxStoreProductQueryVo.setLargeCapacity( arg0.getLargeCapacity() );
        yxStoreProductQueryVo.setAttention( arg0.getAttention() );
        yxStoreProductQueryVo.setBasis( arg0.getBasis() );
        yxStoreProductQueryVo.setCharacters( arg0.getCharacters() );
        yxStoreProductQueryVo.setFunctionCategory( arg0.getFunctionCategory() );
        yxStoreProductQueryVo.setIndication( arg0.getIndication() );
        yxStoreProductQueryVo.setDirections( arg0.getDirections() );
        yxStoreProductQueryVo.setUntowardEffect( arg0.getUntowardEffect() );
        yxStoreProductQueryVo.setContraindication( arg0.getContraindication() );
        yxStoreProductQueryVo.setDrugInteraction( arg0.getDrugInteraction() );
        yxStoreProductQueryVo.setPharmacologicalEffect( arg0.getPharmacologicalEffect() );
        yxStoreProductQueryVo.setStorage( arg0.getStorage() );
        yxStoreProductQueryVo.setStandard( arg0.getStandard() );
        yxStoreProductQueryVo.setProductionAddress( arg0.getProductionAddress() );
        yxStoreProductQueryVo.setTel( arg0.getTel() );
        yxStoreProductQueryVo.setProductArea( arg0.getProductArea() );
        yxStoreProductQueryVo.setFunctionIndication( arg0.getFunctionIndication() );
        yxStoreProductQueryVo.setQualityPeriod( arg0.getQualityPeriod() );
        yxStoreProductQueryVo.setIsImport( arg0.getIsImport() );
        yxStoreProductQueryVo.setBusinessDirectoryCode( arg0.getBusinessDirectoryCode() );
        yxStoreProductQueryVo.setCategory( arg0.getCategory() );
        yxStoreProductQueryVo.setIsGiftBox( arg0.getIsGiftBox() );
        yxStoreProductQueryVo.setLicenseDeadline( arg0.getLicenseDeadline() );
        yxStoreProductQueryVo.setIsAuthorization( arg0.getIsAuthorization() );
        yxStoreProductQueryVo.setIsCompoundPreparation( arg0.getIsCompoundPreparation() );
        yxStoreProductQueryVo.setIsColdChain( arg0.getIsColdChain() );
        yxStoreProductQueryVo.setSeo( arg0.getSeo() );
        yxStoreProductQueryVo.setPregnancyLactationDirections( arg0.getPregnancyLactationDirections() );
        yxStoreProductQueryVo.setChildrenDirections( arg0.getChildrenDirections() );
        yxStoreProductQueryVo.setElderlyPatientDirections( arg0.getElderlyPatientDirections() );
        yxStoreProductQueryVo.setApplyCrowdDesc( arg0.getApplyCrowdDesc() );
        yxStoreProductQueryVo.setApplyCrowdCode( arg0.getApplyCrowdCode() );
        yxStoreProductQueryVo.setPhamacokinetics( arg0.getPhamacokinetics() );
        yxStoreProductQueryVo.setOverdosage( arg0.getOverdosage() );
        yxStoreProductQueryVo.setClinicalTest( arg0.getClinicalTest() );
        yxStoreProductQueryVo.setUseUnit( arg0.getUseUnit() );
        yxStoreProductQueryVo.setPharmacologyToxicology( arg0.getPharmacologyToxicology() );
        yxStoreProductQueryVo.setIsHeterotype( arg0.getIsHeterotype() );
        yxStoreProductQueryVo.setCertImagId( arg0.getCertImagId() );
        yxStoreProductQueryVo.setMedicationCycle( arg0.getMedicationCycle() );

        return yxStoreProductQueryVo;
    }

    @Override
    public Boolean dualProuctDisease2Redis(String projectCode) {

       LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<Project>();
        lambdaQueryWrapper.ne(Project::getProjectCode,"");
       if(StrUtil.isNotBlank(projectCode)) {
           lambdaQueryWrapper.eq(Project::getProjectCode,projectCode);
       }


       List<Project> projectList = projectMapper.selectList(lambdaQueryWrapper);
       for(Project project:projectList) {
           projectCode = project.getProjectCode();
           if(ProjectNameEnum.TAIPING_LEXIANG.getValue().equals(project.getProjectCode())) {
               List<String> list_1 = storeProductMapper.queryDiseaseByProjectCodeLabel1(projectCode);
               log.info("{}:label 1 获取药品条数：{}",projectCode,list_1.size());
               HashMap hashMap_1 = new HashMap();
               for(String str:list_1) {
                   List<String> stringList = Arrays.asList(str.split(","));
                   for(String id:stringList) {
                       if(StrUtil.isNotBlank(id)) {
                           hashMap_1.put(id,id);
                       }
                   }
               }
               String diseaseids_1 = CollUtil.join(hashMap_1.values(),",");
               log.info("项目{} label 1 的病种分类id:{}",projectCode,diseaseids_1);
               redisUtils.set("disease-" +  projectCode + "-label1" ,diseaseids_1);


               List<String> list_2 = storeProductMapper.queryDiseaseByProjectCodeLabel2(projectCode);
               log.info("{}:label 2 获取药品条数：{}",projectCode,list_2.size());
               HashMap hashMap_2 = new HashMap();
               for(String str:list_2) {
                   List<String> stringList = Arrays.asList(str.split(","));
                   for(String id:stringList) {
                       if(StrUtil.isNotBlank(id)) {
                           hashMap_2.put(id,id);
                       }
                   }
               }
               String diseaseids_2 = CollUtil.join(hashMap_2.values(),",");
               log.info("项目{} label 2 的病种分类id:{}",projectCode,diseaseids_2);
               redisUtils.set("disease-" +  projectCode + "-label2" ,diseaseids_2);


               List<String> list_3 = storeProductMapper.queryDiseaseByProjectCodeLabel3(projectCode);
               log.info("{}:label 3 获取药品条数：{}",projectCode,list_3.size());
               HashMap hashMap_3 = new HashMap();
               for(String str:list_3) {
                   List<String> stringList = Arrays.asList(str.split(","));
                   for(String id:stringList) {
                       if(StrUtil.isNotBlank(id)) {
                           hashMap_3.put(id,id);
                       }
                   }
               }
               String diseaseids_3 = CollUtil.join(hashMap_1.values(),",");
               log.info("项目{} label 3 的病种分类id:{}",projectCode,diseaseids_3);
               redisUtils.set("disease-" +  projectCode + "-label3" ,diseaseids_3);
           }else {
               if( StrUtil.isNotBlank(project.getStoreIds())) {
                   List<String> list = storeProductMapper.queryDiseaseByStoreids(project.getStoreIds());
                   log.info("{}:label 1 获取药品条数：{}",projectCode,list.size());
                   HashMap hashMap = new HashMap();
                   for(String str:list) {
                       List<String> stringList = Arrays.asList(str.split(","));
                       for(String id:stringList) {
                           if(StrUtil.isNotBlank(id)) {
                               hashMap.put(id,id);
                           }
                       }
                   }
                   String diseaseids = CollUtil.join(hashMap.values(),",");
                   log.info("项目{}  的病种分类id:{}",projectCode,diseaseids);
                   redisUtils.set("disease-" +  projectCode ,diseaseids);
               }else {
                   List<String> list = storeProductMapper.queryDiseaseByProjectCode(projectCode);
                   log.info("{}:获取药品条数：{}",projectCode,list.size());
                   HashMap hashMap = new HashMap();
                   for(String str:list) {
                       List<String> stringList = Arrays.asList(str.split(","));
                       for(String id:stringList) {
                           if(StrUtil.isNotBlank(id)) {
                               hashMap.put(id,id);
                           }
                       }
                   }

                   String diseaseids = CollUtil.join(hashMap.values(),",");
                   log.info("项目{}的病种分类id:{}",projectCode,diseaseids);
                   redisUtils.set("disease-" +  projectCode ,diseaseids);
               }
           }
       }





       return true;
    }

    @Override
    public void syncEBSProductStockBySku(YxStoreProductQueryCriteria criteria) {
        // 更新商城端药房的库存
        // 页码数
        Integer current=1;
        // 每页条数
        Integer size= 1000;
        boolean f=true;
        int batchNo = OrderUtil.getSecondTimestampTwo();
        List<YxStoreProductAttrValue> list = new ArrayList<>();
        do{
            List<SkuSellerPriceStock> yiyaomedLst = ebsService.queryYiyaobaoMedStock("",criteria.getStoreId()==null?"":criteria.getStoreId().toString(),current,size,criteria.getYiyaobaoSku());
            for(SkuSellerPriceStock yiyaobaoMed:yiyaomedLst) {
                yiyaobaoMed.setBatchNo(batchNo);
                // 保存药品-门店-库存数据
                saveSkuSellerStock(yiyaobaoMed,list);
            }
            if(yiyaomedLst.size()==size){
                current=1+size;
            }else{
                f=false;
            }
        }while(f);

        log.info("益药宝门店药品库存同步条数 count={}",list.size());
        if(list.size()>0){
            yxStoreProductAttrValueService.updateBatchById(list);
        }
    }


    /**
     * 定时同步EBS非广州店药品
     */
    public void syncYiyaobaoStoreMedStock() {
        long time=System.currentTimeMillis();
        log.info("定时同步EBS非广州店药品 库存start");
        YxStoreProductQueryCriteria criteria=new YxStoreProductQueryCriteria();
        List<String> list= yxStoreProductAttrValueService.findNotGuangZhou(ShopConstants.STORENAME_GUANGZHOU_CLOUD);
        for (String s : list) {
            criteria.setYiyaobaoSku(s);
            syncEBSProductStockBySku(criteria);
        }
        log.info("定时同步EBS非广州店药品 库存end:{}",System.currentTimeMillis()-time);
    }

    private void saveSkuSellerStock(SkuSellerPriceStock skuSellerPriceStock,List<YxStoreProductAttrValue> list){
        QueryWrapper queryWrapper_store = new QueryWrapper();
        queryWrapper_store.eq("yiyaobao_sku",skuSellerPriceStock.getSku());
        queryWrapper_store.eq("yiyaobao_seller_id",skuSellerPriceStock.getSellerId());
        queryWrapper_store.ne("suk",ShopConstants.STORENAME_GUANGZHOU_CLOUD);
        queryWrapper_store.select("id","stock");
        YxStoreProductAttrValue productAttrValue =  yxStoreProductAttrValueService.getOne(queryWrapper_store,false);
        if(ObjectUtil.isEmpty(productAttrValue)) {
            return;
        } else {
            YxStoreProductAttrValue yxStoreProductAttrValue = new YxStoreProductAttrValue();
            yxStoreProductAttrValue.setId(productAttrValue.getId());
            yxStoreProductAttrValue.setStock(skuSellerPriceStock.getStock());
            //yxStoreProductAttrValue.setAttrId(skuSellerPriceStock.getBatchNo());
            list.add(yxStoreProductAttrValue);
        }
    };

    @Override
    public int uploadProductGroup(List<Map<String, Object>> readAll) {
        for(Map<String,Object> data : readAll) {
            // 组合sku
            String parentSku = "";
            String parentCommonName = "";
            String sku = "";
            Integer amount = 1;
            BigDecimal price = null;
            String status = "";
            Integer parentProductId = null;
            Integer productId = null;

            Object parent_sku_Object = data.get("组合sku");
            if(ObjectUtil.isNotEmpty(parent_sku_Object)) {
                parentSku = String.valueOf(parent_sku_Object);
                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(YxStoreProduct::getYiyaobaoSku,parentSku);
                lambdaQueryWrapper.eq(YxStoreProduct::getIsDel,0);
                lambdaQueryWrapper.select(YxStoreProduct::getId);
                YxStoreProduct yxStoreProduct = this.getOne(lambdaQueryWrapper,false);
                if(yxStoreProduct == null) {
                    throw new BadRequestException("组合sku["+parentSku+"]找不到主数据");
                }
                parentProductId = yxStoreProduct.getId();

            }else{
                throw new BadRequestException("组合sku不能为空");
            }

            Object sku_Object = data.get("子商品sku");
            if(ObjectUtil.isNotEmpty(sku_Object)) {
                sku = String.valueOf(sku_Object);

                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(YxStoreProduct::getYiyaobaoSku,sku);
                lambdaQueryWrapper.eq(YxStoreProduct::getIsDel,0);
                lambdaQueryWrapper.select(YxStoreProduct::getId);
                YxStoreProduct yxStoreProduct = this.getOne(lambdaQueryWrapper,false);
                if(yxStoreProduct == null) {
                    throw new BadRequestException("子商品sku["+sku+"]找不到主数据");
                }
                productId = yxStoreProduct.getId();
            }else{
                throw new BadRequestException("子商品sku不能为空");
            }

            Object storeName_Object = data.get("门店名称");
            String storeName = "";
            Integer storeId = null;
            if(ObjectUtil.isNotEmpty(storeName_Object)) {
                storeName = String.valueOf(storeName_Object);
                LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(YxSystemStore::getName,storeName);
                lambdaQueryWrapper.select(YxSystemStore::getId);
                YxSystemStore yxSystemStore = yxSystemStoreService.getOne(lambdaQueryWrapper,false);
                if(yxSystemStore == null) {
                    throw new BadRequestException("门店名称["+storeName+"]找不到主数据");
                }
                storeId = yxSystemStore.getId();
            }else {
                throw new BadRequestException("门店名称不能为空");
            }


            // 获取门店-商品 unique码
            LambdaQueryWrapper<YxStoreProductAttrValue> lambdaQueryWrapper_attr = new LambdaQueryWrapper<>();
            lambdaQueryWrapper_attr.eq(YxStoreProductAttrValue::getProductId,productId);
            lambdaQueryWrapper_attr.eq(YxStoreProductAttrValue::getStoreId,storeId);
            lambdaQueryWrapper_attr.eq(YxStoreProductAttrValue::getIsDel,0);
            lambdaQueryWrapper_attr.select(YxStoreProductAttrValue::getUnique);
            YxStoreProductAttrValue yxStoreProductAttrValue = yxStoreProductAttrValueService.getOne(lambdaQueryWrapper_attr,false);
            if(yxStoreProductAttrValue == null) {
                throw new BadRequestException("门店["+storeName+"]中找不到商品sku["+sku+"]");
            }

            Object amount_Object = data.get("子商品数量");
            if(ObjectUtil.isNotEmpty(amount_Object)) {
                if(NumberUtil.isNumber(String.valueOf(amount_Object))) {
                    amount = Integer.valueOf(String.valueOf(amount_Object));
                }
            }

            Object price_Object = data.get("子商品价格");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                if(NumberUtil.isNumber(String.valueOf(price_Object))) {
                    price = new BigDecimal(String.valueOf(price_Object));
                }
            }

            Object status_Object = data.get("是否上架(Y/N)");
            if(ObjectUtil.isNotEmpty(status_Object)) {
                status = String.valueOf(status_Object);
            }

            if(!"Y".equals(status) && !"N".equals(status)) {
                throw new BadRequestException("sku["+ sku +"]是否上架标记填写错误，应填Y/N");
            }

            Integer isDel = 0;
            if("N".equals(status)) {
                isDel = 1;
            }


            Object product_common_name_Object = data.get("子商品通用名");
            String product_common_name = "";
            if(ObjectUtil.isNotEmpty(product_common_name_Object)) {
                product_common_name = String.valueOf(product_common_name_Object);
            }

            LambdaQueryWrapper<YxStoreProductGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(YxStoreProductGroup::getParentProductYiyaobaoSku,parentSku);
            lambdaQueryWrapper.eq(YxStoreProductGroup::getProductUnique,yxStoreProductAttrValue.getUnique());

            if(isDel == 1) {
                yxStoreProductGroupService.remove(lambdaQueryWrapper);
            }else {
                YxStoreProductGroup yxStoreProductGroup = yxStoreProductGroupService.getOne(lambdaQueryWrapper,false);
                if(yxStoreProductGroup == null) {
                    yxStoreProductGroup = new YxStoreProductGroup();
                    yxStoreProductGroup.setProductId(productId);
                    yxStoreProductGroup.setParentProductId(parentProductId);
                    yxStoreProductGroup.setIsDel(isDel);
                    yxStoreProductGroup.setNum(amount);
                    yxStoreProductGroup.setParentProductYiyaobaoSku(parentSku);
                    yxStoreProductGroup.setUnitPrice(price);
                    yxStoreProductGroup.setProductYiyaobaoSku(sku);
                    yxStoreProductGroup.setProductUnique(yxStoreProductAttrValue.getUnique());
                    yxStoreProductGroup.setCommonName(product_common_name);
                    yxStoreProductGroup.setStoreName(storeName);
                    yxStoreProductGroupService.save(yxStoreProductGroup);
                }else {
                    yxStoreProductGroup.setProductId(productId);
                    yxStoreProductGroup.setParentProductId(parentProductId);
                    yxStoreProductGroup.setIsDel(isDel);
                    yxStoreProductGroup.setNum(amount);
                    yxStoreProductGroup.setParentProductYiyaobaoSku(parentSku);
                    yxStoreProductGroup.setUnitPrice(price);
                    yxStoreProductGroup.setProductYiyaobaoSku(sku);
                    yxStoreProductGroup.setProductUnique(yxStoreProductAttrValue.getUnique());
                    yxStoreProductGroup.setCommonName(product_common_name);
                    yxStoreProductGroup.setStoreName(storeName);
                    yxStoreProductGroupService.updateById(yxStoreProductGroup);
                }
            }
        }
        return readAll.size();
    }

    @Override
    public void downloadProductGroup(List<YxStoreProduct> all, HttpServletResponse response,String projectCode) throws IOException {
        //	商品名	通用名	商品规格	生产厂家	标准价格	商品类型	剂型	存储条件	单位	批准文号	主要成分
        //	药理作用	性状	保质期	药物相互作用	适应症	病种	不良反应	禁忌
        List<Map<String, Object>> list = new ArrayList<>();
        if(CollUtil.isNotEmpty(all)) {
            for (YxStoreProduct yxStoreProduct : all) {
                // 获取组合中的子商品

                List<YxStoreProductGroup> yxStoreProductGroupList = yxStoreProductGroupService.list(new LambdaQueryWrapper<YxStoreProductGroup>().eq(YxStoreProductGroup::getParentProductYiyaobaoSku,yxStoreProduct.getYiyaobaoSku()));
                if(CollUtil.isNotEmpty(yxStoreProductGroupList)) {
                    for(YxStoreProductGroup yxStoreProductGroup:yxStoreProductGroupList) {
                        Map<String,Object> map = new LinkedHashMap<>();
                        map.put("组合sku", yxStoreProduct.getYiyaobaoSku());
                        map.put("组合名称", yxStoreProduct.getCommonName());
                        map.put("子商品sku", yxStoreProductGroup.getProductYiyaobaoSku());
                        map.put("子商品通用名", yxStoreProductGroup.getCommonName());
                        map.put("门店名称",yxStoreProductGroup.getStoreName());
                        map.put("子商品数量",yxStoreProductGroup.getNum());
                        map.put("子商品价格",yxStoreProductGroup.getUnitPrice());
                        map.put("是否上架(Y/N)","Y");

                        list.add(map);
                    }
                } else {
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("组合sku", yxStoreProduct.getYiyaobaoSku());
                    map.put("组合名称", yxStoreProduct.getCommonName());
                    map.put("子商品sku", "");
                    map.put("子商品通用名", "");
                    map.put("门店名称",ShopConstants.STORENAME_GUANGZHOU_CLOUD);
                    map.put("子商品数量",1);
                    map.put("子商品价格","");
                    map.put("是否上架(Y/N)","N");

                    list.add(map);
                }


            }
        } else {
            // 如果数据为空，则只输出title
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("组合sku", "");
            map.put("组合名称", "");
            map.put("子商品sku", "");
            map.put("子商品通用名", "");
            map.put("门店名称", ShopConstants.STORENAME_GUANGZHOU_CLOUD);
            map.put("子商品sku", "");
            map.put("子商品数量", "");
            map.put("子商品价格", "");
            map.put("是否上架(Y/N)","");

            list.add(map);
        }

        FileUtil.downloadExcel(list, response);
    }

    @Override
    public BigDecimal queryProductPrice(String skuCode, String projectCode) {
        return baseMapper.queryProductPrice(skuCode,projectCode);
    }
}
