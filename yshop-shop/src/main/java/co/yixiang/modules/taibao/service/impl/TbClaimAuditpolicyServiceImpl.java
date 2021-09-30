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
import co.yixiang.modules.taibao.domain.TbClaimAuditpolicy;
import co.yixiang.modules.taibao.service.TbClaimAuditpolicyService;
import co.yixiang.modules.taibao.service.dto.TbClaimAuditpolicyDto;
import co.yixiang.modules.taibao.service.dto.TbClaimAuditpolicyQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimAuditpolicyMapper;
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
//@CacheConfig(cacheNames = "tbClaimAuditpolicy")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimAuditpolicyServiceImpl extends BaseServiceImpl<TbClaimAuditpolicyMapper, TbClaimAuditpolicy> implements TbClaimAuditpolicyService {

    private final IGenerator generator;
    @Autowired
    private TbClaimAuditpolicyMapper claimAuditpolicyMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimAuditpolicyQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimAuditpolicy> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimAuditpolicyDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimAuditpolicy> queryAll(TbClaimAuditpolicyQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimAuditpolicy.class, criteria));
    }


    @Override
    public void download(List<TbClaimAuditpolicyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimAuditpolicyDto tbClaimAuditpolicy : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimAuditpolicy.getClaimInfoId());
            map.put("保单号", tbClaimAuditpolicy.getPolicyno());
            map.put("险种代码", tbClaimAuditpolicy.getClasscode());
            map.put("是否终止", tbClaimAuditpolicy.getReinsurancemark());
            map.put("是否解约", tbClaimAuditpolicy.getIsclause());
            map.put("是否退费", tbClaimAuditpolicy.getIsrefund());
            map.put("是否续保", tbClaimAuditpolicy.getIsrenewal());
            map.put("创建人", tbClaimAuditpolicy.getCreateBy());
            map.put("创建时间", tbClaimAuditpolicy.getCreateTime());
            map.put("修改人", tbClaimAuditpolicy.getUpdateBy());
            map.put("修改时间", tbClaimAuditpolicy.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimAuditpolicy.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimAuditpolicy> selectByMainId(String mainId) {
        return claimAuditpolicyMapper.selectByMainId(Long.valueOf(mainId));
    }
}
