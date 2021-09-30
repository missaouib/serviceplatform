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
import co.yixiang.modules.taibao.domain.TbClaimInvest;
import co.yixiang.modules.taibao.service.TbClaimInvestService;
import co.yixiang.modules.taibao.service.dto.TbClaimInvestDto;
import co.yixiang.modules.taibao.service.dto.TbClaimInvestQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimInvestMapper;
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
//@CacheConfig(cacheNames = "tbClaimInvest")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimInvestServiceImpl extends BaseServiceImpl<TbClaimInvestMapper, TbClaimInvest> implements TbClaimInvestService {

    private final IGenerator generator;
    @Autowired
    private TbClaimInvestMapper claimInvestMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimInvestQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimInvest> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimInvestDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimInvest> queryAll(TbClaimInvestQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimInvest.class, criteria));
    }


    @Override
    public void download(List<TbClaimInvestDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimInvestDto tbClaimInvest : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimInvest.getClaimInfoId());
            map.put("任务性质（即时调查|常规调查|复杂疑难调查|反欺诈调查）", tbClaimInvest.getKind());
            map.put("任务子类型（疾病死亡|意外死亡|重大疾病|疾病医疗|意外医疗|残疾失能）", tbClaimInvest.getInvesttype());
            map.put("调查方式（现场勘查|走访调查|询问调查|住院及费用核实|住院排查|住院补贴监控|追踪调查|综合调查）", tbClaimInvest.getSubway());
            map.put("调查要求", tbClaimInvest.getDemand());
            map.put("调查结果", tbClaimInvest.getResult());
            map.put("申请日期", tbClaimInvest.getApplydate());
            map.put("反馈日期", tbClaimInvest.getBackdate());
            map.put("调查员", tbClaimInvest.getEmp());
            map.put("创建人", tbClaimInvest.getCreateBy());
            map.put("创建时间", tbClaimInvest.getCreateTime());
            map.put("修改人", tbClaimInvest.getUpdateBy());
            map.put("修改时间", tbClaimInvest.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimInvest.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimInvest> selectByMainId(String mainId) {
        return claimInvestMapper.selectByMainId(Long.valueOf(mainId));
    }
}
