/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.ObjectUtil;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.RocheHospital;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.xikang.domain.XikangMedMapping;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.RocheHospitalService;
import co.yixiang.modules.shop.service.dto.RocheHospitalDto;
import co.yixiang.modules.shop.service.dto.RocheHospitalQueryCriteria;
import co.yixiang.modules.shop.service.mapper.RocheHospitalMapper;
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
* @date 2021-02-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "rocheHospital")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RocheHospitalServiceImpl extends BaseServiceImpl<RocheHospitalMapper, RocheHospital> implements RocheHospitalService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(RocheHospitalQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<RocheHospital> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), RocheHospitalDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<RocheHospital> queryAll(RocheHospitalQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(RocheHospital.class, criteria));
    }


    @Override
    public void download(List<RocheHospitalDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RocheHospitalDto rocheHospital : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("医院名称", rocheHospital.getName());
            map.put("省份名称", rocheHospital.getProvinceName());
            map.put("城市名称", rocheHospital.getCityName());
            map.put("状态", rocheHospital.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public int upload(List<Map<String, Object>> list) {
        List<RocheHospital> rocheHospitalArrayList = new ArrayList<>();

        for(Map<String,Object> data : list) {

            String name = "";
            String provinceName = "";
            String cityName = "";
            String status = "";
            Object name_Object = data.get("医院名称");
            if(ObjectUtil.isNotEmpty(name_Object)) {
                name = String.valueOf(name_Object);
            } else {
                throw  new BadRequestException("医院名称不能为空");
            }

            Object province_Object = data.get("省份名称");
            if(ObjectUtil.isNotEmpty(province_Object)) {
                provinceName = String.valueOf(province_Object);
            }

            Object city_Object = data.get("城市名称");
            if(ObjectUtil.isNotEmpty(city_Object)) {
                cityName = String.valueOf(city_Object);
            }

            Object status_Object = data.get("状态");
            if(ObjectUtil.isNotEmpty(status_Object)) {
                status = String.valueOf(status_Object);
            }
            if( "有效".equals(status) || "无效".equals(status) ) {

            } else {
                throw new BadRequestException("状态填写出错，有效/无效");
            }
            LambdaQueryWrapper<RocheHospital> queryWrapper = new LambdaQueryWrapper<RocheHospital>();

            queryWrapper.eq(RocheHospital::getName,name);
            RocheHospital rocheHospital = this.getOne(queryWrapper);

            if(rocheHospital == null) {
                rocheHospital = new RocheHospital();
            }
            rocheHospital.setName(name);
            rocheHospital.setCityName(cityName);
            rocheHospital.setProvinceName(provinceName);
            rocheHospital.setStatus(status);
            rocheHospitalArrayList.add(rocheHospital);
        }
        this.saveOrUpdateBatch(rocheHospitalArrayList) ;
        return rocheHospitalArrayList.size();
    }
}
