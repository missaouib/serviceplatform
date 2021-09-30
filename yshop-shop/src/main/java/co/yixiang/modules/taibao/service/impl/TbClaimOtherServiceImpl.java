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
import co.yixiang.modules.taibao.domain.TbClaimOther;
import co.yixiang.modules.taibao.service.TbClaimOtherService;
import co.yixiang.modules.taibao.service.dto.TbClaimOtherDto;
import co.yixiang.modules.taibao.service.dto.TbClaimOtherQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimOtherMapper;
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
//@CacheConfig(cacheNames = "tbClaimOther")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimOtherServiceImpl extends BaseServiceImpl<TbClaimOtherMapper, TbClaimOther> implements TbClaimOtherService {

    private final IGenerator generator;
    @Autowired
    private TbClaimOtherMapper claimOtherMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimOtherQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimOther> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimOtherDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimOther> queryAll(TbClaimOtherQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimOther.class, criteria));
    }


    @Override
    public void download(List<TbClaimOtherDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimOtherDto tbClaimOther : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimOther.getClaimInfoId());
            map.put("审核结论", tbClaimOther.getRescode());
            map.put("申请日期", tbClaimOther.getApplydate());
            map.put("反馈日期", tbClaimOther.getBackdate());
            map.put("审核员", tbClaimOther.getEmp());
            map.put("创建人", tbClaimOther.getCreateBy());
            map.put("创建时间", tbClaimOther.getCreateTime());
            map.put("修改人", tbClaimOther.getUpdateBy());
            map.put("修改时间", tbClaimOther.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimOther.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimOther> selectByMainId(String mainId) {
        return claimOtherMapper.selectByMainId(Long.valueOf(mainId));
    }
}
