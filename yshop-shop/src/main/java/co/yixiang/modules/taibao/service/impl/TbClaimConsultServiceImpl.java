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
import co.yixiang.modules.taibao.domain.TbClaimConsult;
import co.yixiang.modules.taibao.service.TbClaimConsultService;
import co.yixiang.modules.taibao.service.dto.TbClaimConsultDto;
import co.yixiang.modules.taibao.service.dto.TbClaimConsultQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimConsultMapper;
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
//@CacheConfig(cacheNames = "tbClaimConsult")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimConsultServiceImpl extends BaseServiceImpl<TbClaimConsultMapper, TbClaimConsult> implements TbClaimConsultService {

    private final IGenerator generator;
    @Autowired
    private TbClaimConsultMapper claimConsultMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimConsultQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimConsult> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimConsultDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimConsult> queryAll(TbClaimConsultQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimConsult.class, criteria));
    }


    @Override
    public void download(List<TbClaimConsultDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimConsultDto tbClaimConsult : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimConsult.getClaimInfoId());
            map.put("协谈理赔结论（通融|和解）", tbClaimConsult.getRescode());
            map.put("协谈申请", tbClaimConsult.getApply());
            map.put("协谈结果", tbClaimConsult.getResult());
            map.put("申请日期", tbClaimConsult.getApplydate());
            map.put("反馈日期", tbClaimConsult.getBackdate());
            map.put("协谈员", tbClaimConsult.getEmp());
            map.put("创建人", tbClaimConsult.getCreateBy());
            map.put("创建时间", tbClaimConsult.getCreateTime());
            map.put("修改人", tbClaimConsult.getUpdateBy());
            map.put("修改时间", tbClaimConsult.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimConsult.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimConsult> selectByMainId(String mainId) {
        return claimConsultMapper.selectByMainId(Long.valueOf(mainId));
    }
}
