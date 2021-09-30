/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.yaoshitong.domain.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.domain.YaoshitongPrescription;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaoshitong.service.YaoshitongPrescriptionService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPrescriptionDto;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPrescriptionQueryCriteria;
import co.yixiang.modules.yaoshitong.service.mapper.YaoshitongPrescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
* @date 2020-07-21
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaoshitongPrescription")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongPrescriptionServiceImpl extends BaseServiceImpl<YaoshitongPrescriptionMapper, YaoshitongPrescription> implements YaoshitongPrescriptionService {

    private final IGenerator generator;

    @Autowired
    private YaoshitongPatientService patientService;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongPrescriptionQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaoshitongPrescription> page = new PageInfo<>(queryAll(criteria));
        List<YaoshitongPrescriptionDto> dtoList = new ArrayList<>();
        for(YaoshitongPrescription prescription:page.getList()) {
            YaoshitongPrescriptionDto dto =  generator.convert(prescription,YaoshitongPrescriptionDto.class);
            YaoshitongPatient patient = patientService.getById(prescription.getPatientId());
            if(patient != null) {
                dto.setName(patient.getName());
                dto.setPhone(patient.getPhone());
                dto.setBirth(patient.getBirth());
                if(StrUtil.isNotBlank(patient.getBirth())) {
                    dto.setAge(DateUtil.ageOfNow(patient.getBirth() + "01"));
                }
                dto.setAddress(patient.getAddress());

            }

            MdPharmacistService pharmacist = pharmacistService.getOne(new QueryWrapper<MdPharmacistService>().eq("uid",prescription.getPharmacistId()));

            if(pharmacist != null) {
                dto.setPharmacistName(pharmacist.getName());
                dto.setStoreName(pharmacist.getForeignName());
                dto.setPharmacistPhone(pharmacist.getPhone());
            }
            dtoList.add(dto);
        }

        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", dtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongPrescription> queryAll(YaoshitongPrescriptionQueryCriteria criteria){
        QueryWrapper queryWrapper = new QueryWrapper();
        if(CollUtil.isNotEmpty( criteria.getPrescriptionDate())){
            queryWrapper.between("prescription_date",criteria.getPrescriptionDate().get(0),criteria.getPrescriptionDate().get(1));
        }

        if(StrUtil.isNotBlank(criteria.getName())){
            queryWrapper.apply(" exists (select 1 from yaoshitong_patient a where a.id = yaoshitong_prescription.patient_id and a.name like concat('%',{0},'%'))",criteria.getName());
        }

        if(StrUtil.isNotBlank(criteria.getPrescriptionNo())) {
            queryWrapper.like("prescription_no",criteria.getPrescriptionNo());
        }

        if(StrUtil.isNotBlank(criteria.getPharmacistName())) {
            queryWrapper.apply(" EXISTS (SELECT 1 FROM md_pharmacist_service mps WHERE mps.ID = yaoshitong_prescription.pharmacist_id AND mps.NAME LIKE  CONCAT('%',{0},'%'))",criteria.getPharmacistName());
        }

        if(StrUtil.isNotBlank(criteria.getPhone())) {
            queryWrapper.apply(" exists (select 1 from yaoshitong_patient a where a.id = yaoshitong_prescription.patient_id and a.phone like concat('%',{0},'%'))",criteria.getPhone());
        }

        if(StrUtil.isNotBlank(criteria.getPharmacistId())) {
            queryWrapper.eq("pharmacist_id",criteria.getPharmacistId());
        }

        if(criteria.getPatientId() != null) {

        }
         return baseMapper.selectList(QueryHelpPlus.getPredicate(YaoshitongPrescription.class, criteria));
    }


    @Override
    public void download(List<YaoshitongPrescriptionDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongPrescriptionDto yaoshitongPrescription : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("处方编号", yaoshitongPrescription.getPrescriptionNo());
            map.put("患者id", yaoshitongPrescription.getPatientId());
            map.put("药师id", yaoshitongPrescription.getPharmacistId());
            map.put("是否处方药", yaoshitongPrescription.getIsPrescription());
            map.put("处方日期", yaoshitongPrescription.getPrescriptionDate());
            map.put("处方医院", yaoshitongPrescription.getHospitalName());
            map.put("处方医生", yaoshitongPrescription.getDoctorName());
            map.put("科室", yaoshitongPrescription.getDepartName());
            map.put("诊断", yaoshitongPrescription.getDiagnosis());
            map.put("药品明细", yaoshitongPrescription.getMedDetail());
            map.put("生成时间", yaoshitongPrescription.getCreateTime());
            map.put("更新时间", yaoshitongPrescription.getUpdateTime());
            map.put("处方图片", yaoshitongPrescription.getImagePath());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
