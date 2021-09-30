/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.taibao.domain.TbInsurancePerson;
import co.yixiang.modules.taibao.service.TbInsurancePersonService;
import co.yixiang.modules.taibao.service.dto.TbInsurancePersonDto;
import co.yixiang.modules.taibao.service.dto.TbInsurancePersonQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbInsurancePersonMapper;
import co.yixiang.utils.FileUtil;
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
* @author zhoujinlai
* @date 2021-04-30
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "tbInsurancePerson")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbInsurancePersonServiceImpl extends BaseServiceImpl<TbInsurancePersonMapper, TbInsurancePerson> implements TbInsurancePersonService {

    private final IGenerator generator;
    @Autowired
    private TbInsurancePersonMapper insurancePersonMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbInsurancePersonQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbInsurancePerson> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbInsurancePersonDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbInsurancePerson> queryAll(TbInsurancePersonQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbInsurancePerson.class, criteria));
    }


    @Override
    public void download(List<TbInsurancePersonDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbInsurancePersonDto tbInsurancePerson : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", tbInsurancePerson.getName());
            map.put("性别", tbInsurancePerson.getSex());
            map.put("证件类别", tbInsurancePerson.getIdtype());
            map.put("证件号码", tbInsurancePerson.getIdno());
            map.put("证件有效期起期", tbInsurancePerson.getIdBegdate());
            map.put("证件有效期止期", tbInsurancePerson.getIdEnddate());
            map.put("职业", tbInsurancePerson.getJob());
            map.put("单位", tbInsurancePerson.getOrganization());
            map.put("出生日期", tbInsurancePerson.getBirthdate());
            map.put("创建人", tbInsurancePerson.getCreateBy());
            map.put("创建时间", tbInsurancePerson.getCreateTime());
            map.put("修改人", tbInsurancePerson.getUpdateBy());
            map.put("修改时间", tbInsurancePerson.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbInsurancePerson.getDelFlag());
            map.put("赔案信息Id", tbInsurancePerson.getClaimInfoId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbInsurancePerson> selectByMainId(String mainId) {
        return insurancePersonMapper.selectByMainId(Long.valueOf(mainId));
    }
}
