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
import co.yixiang.modules.taibao.domain.TbClaimClaimPay;
import co.yixiang.modules.taibao.service.TbClaimClaimPayService;
import co.yixiang.modules.taibao.service.dto.TbClaimClaimPayDto;
import co.yixiang.modules.taibao.service.dto.TbClaimClaimPayQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimClaimPayMapper;
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
//@CacheConfig(cacheNames = "tbClaimClaimPay")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimClaimPayServiceImpl extends BaseServiceImpl<TbClaimClaimPayMapper, TbClaimClaimPay> implements TbClaimClaimPayService {

    private final IGenerator generator;
    @Autowired
    private TbClaimClaimPayMapper claimClaimPayMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimClaimPayQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimClaimPay> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimClaimPayDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimClaimPay> queryAll(TbClaimClaimPayQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimClaimPay.class, criteria));
    }


    @Override
    public void download(List<TbClaimClaimPayDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimClaimPayDto tbClaimClaimPay : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" claimInfoId",  tbClaimClaimPay.getClaimInfoId());
            map.put("保单号", tbClaimClaimPay.getPolicyno());
            map.put("险种代码", tbClaimClaimPay.getClasscode());
            map.put("责任代码", tbClaimClaimPay.getDutycode());
            map.put("赔付金额", tbClaimClaimPay.getClaimpay());
            map.put("垫付金额", tbClaimClaimPay.getAdvancepayment());
            map.put("剩余年免赔额", tbClaimClaimPay.getRemaindeduction());
            map.put("创建人", tbClaimClaimPay.getCreateBy());
            map.put("创建时间", tbClaimClaimPay.getCreateTime());
            map.put("修改人", tbClaimClaimPay.getUpdateBy());
            map.put("修改时间", tbClaimClaimPay.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimClaimPay.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimClaimPay> selectByMainId(String mainId) {
        return claimClaimPayMapper.selectByMainId(Long.valueOf(mainId));
    }
}
