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
import co.yixiang.modules.taibao.domain.TbClaimAbove;
import co.yixiang.modules.taibao.service.TbClaimAboveService;
import co.yixiang.modules.taibao.service.dto.TbClaimAboveDto;
import co.yixiang.modules.taibao.service.dto.TbClaimAboveQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimAboveMapper;
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
//@CacheConfig(cacheNames = "tbClaimAbove")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimAboveServiceImpl extends BaseServiceImpl<TbClaimAboveMapper, TbClaimAbove> implements TbClaimAboveService {

    private final IGenerator generator;
    @Autowired
    private TbClaimAboveMapper claimAboveMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimAboveQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimAbove> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimAboveDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimAbove> queryAll(TbClaimAboveQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimAbove.class, criteria));
    }


    @Override
    public void download(List<TbClaimAboveDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimAboveDto tbClaimAbove : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimAbove.getClaimInfoId());
            map.put("超额件审核结论（赔付|拒赔|撤案）", tbClaimAbove.getRescode());
            map.put("申请日期", tbClaimAbove.getApplydate());
            map.put("反馈日期", tbClaimAbove.getBackdate());
            map.put("审核员", tbClaimAbove.getEmp());
            map.put("创建人", tbClaimAbove.getCreateBy());
            map.put("创建时间", tbClaimAbove.getCreateTime());
            map.put("修改人", tbClaimAbove.getUpdateBy());
            map.put("修改时间", tbClaimAbove.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimAbove.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimAbove> selectByMainId(String mainId) {
        return claimAboveMapper.selectByMainId(Long.valueOf(mainId));
    }
}
