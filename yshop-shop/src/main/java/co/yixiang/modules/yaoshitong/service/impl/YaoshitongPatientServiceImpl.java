/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.yaoshitong.domain.YaoshitongPatient;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPatientDto;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongPatientQueryCriteria;
import co.yixiang.modules.yaoshitong.service.mapper.YaoshitongPatientMapper;
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
//@CacheConfig(cacheNames = "yaoshitongPatient")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongPatientServiceImpl extends BaseServiceImpl<YaoshitongPatientMapper, YaoshitongPatient> implements YaoshitongPatientService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongPatientQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaoshitongPatient> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        for(YaoshitongPatient patient : page.getList()) {
            if(StrUtil.isNotBlank(patient.getBirth())) {
                patient.setAge(DateUtil.ageOfNow(patient.getBirth() + "01"));
            }
        }
        map.put("content", generator.convert(page.getList(), YaoshitongPatientDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongPatient> queryAll(YaoshitongPatientQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaoshitongPatient.class, criteria));
    }


    @Override
    public void download(List<YaoshitongPatientDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongPatientDto yaoshitongPatient : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", yaoshitongPatient.getName());
            map.put("手机号", yaoshitongPatient.getPhone());
            map.put("性别", yaoshitongPatient.getSex());
            map.put("年龄", yaoshitongPatient.getAge());
            map.put("身份证号", yaoshitongPatient.getIdCard());
            map.put("社保卡号", yaoshitongPatient.getSocialCard());
            map.put("病史", yaoshitongPatient.getMedicalHistory());
            map.put("诊断史", yaoshitongPatient.getDiagnosisHistory());
            map.put("用药史", yaoshitongPatient.getMedicationHistory());
            map.put("药物过敏", yaoshitongPatient.getDrugAllergy());
            map.put("用药禁忌", yaoshitongPatient.getDrugContraindications());
            map.put("生成时间", yaoshitongPatient.getCreateTime());
            map.put("更新时间", yaoshitongPatient.getUpdateTime());
            map.put("更新人", yaoshitongPatient.getUpdateUser());
            map.put("出生年月", yaoshitongPatient.getBirth());
            map.put("地址", yaoshitongPatient.getAddress());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
