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
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.Charities;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.domain.YxStoreCategory;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.CharitiesService;
import co.yixiang.modules.shop.service.dto.CharitiesDto;
import co.yixiang.modules.shop.service.dto.CharitiesQueryCriteria;
import co.yixiang.modules.shop.service.mapper.CharitiesMapper;
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
* @date 2020-08-20
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "charities")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CharitiesServiceImpl extends BaseServiceImpl<CharitiesMapper, Charities> implements CharitiesService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(CharitiesQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<Charities> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), CharitiesDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<Charities> queryAll(CharitiesQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(Charities.class, criteria));
    }


    @Override
    public void download(List<CharitiesDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CharitiesDto charities : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("编号", charities.getCode());
            map.put("药房名称", charities.getDrugstoreName());
            map.put("项目名称", charities.getProjectName());
            map.put("基金会名称", charities.getFoundationsName());
            map.put("电话，多个用逗号分隔", charities.getPhone());
            map.put("药品名称", charities.getProductName());
            map.put("药品通用名", charities.getCommonName());
            map.put("剂型", charities.getDrugForm());
            map.put("规格", charities.getSpec());
            map.put("生产厂商", charities.getManufacturer());
            map.put("药品发放时段", charities.getTimeInterval());
            map.put("项目展示网址", charities.getProjectWeburl());
            map.put("热线电话", charities.getHotlinePhone());
            map.put("电子邮件", charities.getEmail());
            map.put("资料邮寄地址", charities.getMailAddress());
            map.put(" createTime",  charities.getCreateTime());
            map.put(" updateTime",  charities.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public int uploadCharities(List<Map<String, Object>> readAll) {


        List<Charities> charitiesList = new ArrayList<>();

         Integer code = 0;


        /** 药房名称 */
         String drugstoreName = "";


        /** 项目名称 */
         String projectName = "";


        /** 基金会名称 */
         String foundationsName = "";


        /** 电话，多个用逗号分隔 */
         String phone = "";


        /** 药品名称 */
         String productName = "";


        /** 药品通用名 */
         String commonName = "";


        /** 剂型 */
         String drugForm = "";


        /** 规格 */
         String spec = "";


        /** 生产厂商 */
         String manufacturer = "";


        /** 药品发放时段 */
         String timeInterval = "";


        /** 项目展示网址 */
         String projectWeburl = "";


        /** 热线电话 */
         String hotlinePhone = "";


        /** 电子邮件 */
         String email = "";


        /** 资料邮寄地址 */
         String mailAddress = "";

        for(Map<String,Object> data : readAll) {



            Object code_Object = data.get("序号");
            if(ObjectUtil.isNotEmpty(code_Object)) {
                code = Integer.valueOf(String.valueOf(code_Object));
            }

            Object storeName_Object = data.get("开展药房");
            if(ObjectUtil.isNotEmpty(storeName_Object)) {
                drugstoreName = String.valueOf(storeName_Object);
            }

            Object common_name_Object = data.get("项目名称");
            if(ObjectUtil.isNotEmpty(common_name_Object)) {
                projectName = String.valueOf(common_name_Object);
            }

            Object spec_Object = data.get("项目基金会名称");
            if(ObjectUtil.isNotEmpty(spec_Object)) {
                foundationsName = String.valueOf(spec_Object);
            }

            Object manufacturer_Object = data.get("药品商品名");
            if(ObjectUtil.isNotEmpty(manufacturer_Object)) {
                productName = String.valueOf(manufacturer_Object);
            }

            Object price_Object = data.get("药品通用名");
            if(ObjectUtil.isNotEmpty(price_Object)) {
                commonName = String.valueOf(price_Object) ;
            }

            Object type_name_Object = data.get("剂型");
            if(ObjectUtil.isNotEmpty(type_name_Object)) {
                drugForm = String.valueOf(type_name_Object);
            }

            Object drug_form_Object = data.get("规格");
            if(ObjectUtil.isNotEmpty(drug_form_Object)) {
                spec = String.valueOf(drug_form_Object);
            }

            Object storage_condition_Object = data.get("生产厂商");
            if(ObjectUtil.isNotEmpty(storage_condition_Object)) {
                manufacturer = String.valueOf(storage_condition_Object);
            }

            Object unit_Object = data.get("药品发放时段");
            if(ObjectUtil.isNotEmpty(unit_Object)) {
                timeInterval = String.valueOf(unit_Object);
            }

            Object license_number_Object = data.get("项目展示网址");
            if(ObjectUtil.isNotEmpty(license_number_Object)) {
                projectWeburl = String.valueOf(license_number_Object);
            }

            Object basis_Object = data.get("援助热线");
            if(ObjectUtil.isNotEmpty(basis_Object)) {
                hotlinePhone = String.valueOf(basis_Object);
            }

            Object directions_Object = data.get("电子邮箱");
            if(ObjectUtil.isNotEmpty(directions_Object)) {
                email = String.valueOf(directions_Object);
            }

            Object pharmacological_effect_Object = data.get("资料邮寄地址");
            if(ObjectUtil.isNotEmpty(pharmacological_effect_Object)) {
                mailAddress = String.valueOf(pharmacological_effect_Object);
            }

            Charities charities = new Charities();
            QueryWrapper queryWrapper = new QueryWrapper<Charities>().eq("code",code);
            queryWrapper.select("id");
            Charities charities_tmp = this.getOne(queryWrapper);
            if(charities_tmp != null) {
                charities.setId(charities_tmp.getId());
            }
            charities.setCode(code);
            charities.setCommonName(commonName);
            charities.setDrugForm(drugForm);
            charities.setEmail(email);
            charities.setFoundationsName(foundationsName);
            charities.setDrugstoreName(drugstoreName);
            charities.setHotlinePhone(hotlinePhone);
            charities.setMailAddress(mailAddress);
            charities.setManufacturer(manufacturer);
            charities.setPhone(phone);
            charities.setProductName(productName);
            charities.setProjectWeburl(projectWeburl);
            charities.setProjectName(projectName);
            charities.setSpec(spec);
            charities.setTimeInterval(timeInterval);

            charitiesList.add(charities);

        }
        this.saveOrUpdateBatch(charitiesList);
        return 0;
    }
}
