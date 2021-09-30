/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import co.yixiang.modules.shop.domain.Charities;
import co.yixiang.modules.shop.domain.MdPharmacistService;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.service.dto.MdPharmacistServiceDto;
import co.yixiang.modules.shop.service.dto.MdPharmacistServiceQueryCriteria;
import co.yixiang.modules.shop.service.mapper.MdPharmacistServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-06-02
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mdPharmacistService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MdPharmacistServiceServiceImpl extends BaseServiceImpl<MdPharmacistServiceMapper, MdPharmacistService> implements MdPharmacistServiceService {

    private final IGenerator generator;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MdPharmacistServiceQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MdPharmacistService> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MdPharmacistServiceDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MdPharmacistService> queryAll(MdPharmacistServiceQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MdPharmacistService.class, criteria));
    }


    @Override
    public void download(List<MdPharmacistServiceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> phoneList = Arrays.asList("18016265639","13764921428",
                "15026889748",
                "13818909998",
                "18017890127",
                "17601359922","17621804160","18121061112");
        for (MdPharmacistServiceDto mdPharmacistService : all) {

            if( phoneList.contains(mdPharmacistService.getPhone())) {
                continue;
            }

            Map<String,Object> map = new LinkedHashMap<>();
            map.put("药房名称", mdPharmacistService.getForeignName());
            map.put("药师名称", mdPharmacistService.getName());
            map.put("药师手机号", mdPharmacistService.getPhone());
            String sex = "";
            if(mdPharmacistService.getSex() == null) {
                sex = "未知";
            } else if(mdPharmacistService.getSex() == 0 ) {
                sex = "女";
            } else if(mdPharmacistService.getSex() == 1 ){
                sex = "男";
            } else {
                sex = "未知";
            }
            map.put("性别(0-女;1-男)", sex);
            if(mdPharmacistService.getUid() == null) {
                map.put("是否注册", "否");
            } else {
                map.put("是否注册", "是");
            }

            int patientCounts = baseMapper.countPatientByPharmacistId(mdPharmacistService.getId());
            map.put("已注册患者数", patientCounts);


            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public String uploadPharmacist(List<Map<String, Object>> readAll) {

        String result = "";
        List<MdPharmacistService> pharmacistList = new ArrayList<>();




        /** 药房名称 */
        String foreignName = "";


        /** 药师名称 */
        String name = "";


        /** 药师手机号 */
        String phone = "";



        for(Map<String,Object> data : readAll) {

            Object foreignName_Object = data.get("药房名称");
            if(ObjectUtil.isNotEmpty(foreignName_Object)) {
                foreignName = String.valueOf(foreignName_Object);
            }


            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new QueryWrapper<YxSystemStore>().eq("name",foreignName).eq("is_del",0));
            if(yxSystemStore == null) {
                result = foreignName + "没有找到对应的药房主数据";
                return  result;
            }

            Object name_Object = data.get("药师名称");
            if(ObjectUtil.isNotEmpty(name_Object)) {
                name = String.valueOf(name_Object);
            }

            Object phone_Object = data.get("药师手机号");
            if(ObjectUtil.isNotEmpty(phone_Object)) {
                phone = String.valueOf(phone_Object);
            }

            MdPharmacistService pharmacist = new MdPharmacistService();
            QueryWrapper queryWrapper = new QueryWrapper<MdPharmacistService>().eq("phone",phone);
            queryWrapper.select("id");
            MdPharmacistService pharmacist_tmp = this.getOne(queryWrapper);
            if(pharmacist_tmp != null) {
                pharmacist.setId(pharmacist_tmp.getId());
            }
            pharmacist.setForeignName(foreignName);
            pharmacist.setPhone(phone);
            pharmacist.setName(name);
            pharmacist.setForeignId(yxSystemStore.getId().toString());
            pharmacist.setSex(0L);
            pharmacist.setStatus(1L);
            pharmacist.setOnline(1L);
            pharmacist.setIsDefalut(0L);
            pharmacist.setSource("01");

            pharmacistList.add(pharmacist);
        }
        this.saveOrUpdateBatch(pharmacistList);
        return result;
    }
}
