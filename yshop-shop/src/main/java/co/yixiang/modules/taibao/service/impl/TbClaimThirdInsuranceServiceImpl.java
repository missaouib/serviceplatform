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
import co.yixiang.modules.taibao.domain.TbClaimThirdInsurance;
import co.yixiang.modules.taibao.service.TbClaimThirdInsuranceService;
import co.yixiang.modules.taibao.service.dto.TbClaimThirdInsuranceDto;
import co.yixiang.modules.taibao.service.dto.TbClaimThirdInsuranceQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimThirdInsuranceMapper;
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
//@CacheConfig(cacheNames = "tbClaimThirdInsurance")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimThirdInsuranceServiceImpl extends BaseServiceImpl<TbClaimThirdInsuranceMapper, TbClaimThirdInsurance> implements TbClaimThirdInsuranceService {

    private final IGenerator generator;
    @Autowired
    private TbClaimThirdInsuranceMapper claimThirdInsuranceMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimThirdInsuranceQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimThirdInsurance> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimThirdInsuranceDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimThirdInsurance> queryAll(TbClaimThirdInsuranceQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimThirdInsurance.class, criteria));
    }


    @Override
    public void download(List<TbClaimThirdInsuranceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimThirdInsuranceDto tbClaimThirdInsurance : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimThirdInsurance.getClaimInfoId());
            map.put("保险公司名称", tbClaimThirdInsurance.getInsCorp());
            map.put("保单名称", tbClaimThirdInsurance.getPoliName());
            map.put("保单号", tbClaimThirdInsurance.getPoliNo());
            map.put("创建人", tbClaimThirdInsurance.getCreateBy());
            map.put("创建时间", tbClaimThirdInsurance.getCreateTime());
            map.put("修改人", tbClaimThirdInsurance.getUpdateBy());
            map.put("修改时间", tbClaimThirdInsurance.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimThirdInsurance.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimThirdInsurance> selectByMainId(String mainId) {
        return claimThirdInsuranceMapper.selectByMainId(Long.valueOf(mainId));
    }
}
