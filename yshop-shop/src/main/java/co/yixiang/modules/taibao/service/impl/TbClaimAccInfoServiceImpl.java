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
import co.yixiang.modules.taibao.domain.TbClaimAccInfo;
import co.yixiang.modules.taibao.service.TbClaimAccInfoService;
import co.yixiang.modules.taibao.service.dto.TbClaimAccInfoDto;
import co.yixiang.modules.taibao.service.dto.TbClaimAccInfoQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimAccInfoMapper;
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
//@CacheConfig(cacheNames = "tbClaimAccInfo")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimAccInfoServiceImpl extends BaseServiceImpl<TbClaimAccInfoMapper, TbClaimAccInfo> implements TbClaimAccInfoService {

    private final IGenerator generator;

    @Autowired
    private TbClaimAccInfoMapper claimAccInfoMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimAccInfoQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimAccInfo> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimAccInfoDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimAccInfo> queryAll(TbClaimAccInfoQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimAccInfo.class, criteria));
    }


    @Override
    public void download(List<TbClaimAccInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimAccInfoDto tbClaimAccInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("出险日期", tbClaimAccInfo.getAccDate());
            map.put("初次就诊日期", tbClaimAccInfo.getFirstDate());
            map.put("出险地区  （1. 大陆地区 2. 港澳台 3. 境外不含港澳台 ）", tbClaimAccInfo.getAccAddrType());
            map.put("出险类型 （1意外，2疾病，3其他）", tbClaimAccInfo.getAccSubtype());
            map.put("出险经过", tbClaimAccInfo.getAccInfo());
            map.put("索赔事故性质  （01 身故  02 伤残  03 重大疾病 04 门急诊医疗 05 住院医疗 06 住院补贴 07 女性生育），多个用逗号拼接", tbClaimAccInfo.getClaimacc());
            map.put("创建人", tbClaimAccInfo.getCreateBy());
            map.put("创建时间", tbClaimAccInfo.getCreateTime());
            map.put("修改人", tbClaimAccInfo.getUpdateBy());
            map.put("修改时间", tbClaimAccInfo.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimAccInfo.getDelFlag());
            map.put("赔案信息Id", tbClaimAccInfo.getClaimInfoId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimAccInfo> selectByMainId(String mainId) {
        return claimAccInfoMapper.selectByMainId(Long.valueOf(mainId));
    }
}
