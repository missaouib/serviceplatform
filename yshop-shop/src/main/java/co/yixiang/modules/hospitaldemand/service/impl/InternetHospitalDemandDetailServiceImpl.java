/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.service.impl;

import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemandDetail;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDetailDto;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDetailQueryCriteria;
import co.yixiang.modules.hospitaldemand.service.mapper.InternetHospitalDemandDetailMapper;
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
* @author visazhou
* @date 2021-01-22
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "internetHospitalDemandDetail")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InternetHospitalDemandDetailServiceImpl extends BaseServiceImpl<InternetHospitalDemandDetailMapper, InternetHospitalDemandDetail> implements InternetHospitalDemandDetailService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(InternetHospitalDemandDetailQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<InternetHospitalDemandDetail> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), InternetHospitalDemandDetailDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<InternetHospitalDemandDetail> queryAll(InternetHospitalDemandDetailQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(InternetHospitalDemandDetail.class, criteria));
    }


    @Override
    public void download(List<InternetHospitalDemandDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InternetHospitalDemandDetailDto internetHospitalDemandDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("需求单id", internetHospitalDemandDetail.getDemandId());
            map.put("处方编号", internetHospitalDemandDetail.getPrescriptionCode());
            map.put("药品编码", internetHospitalDemandDetail.getDrugCode());
            map.put("药品名称", internetHospitalDemandDetail.getDrugName());
            map.put("药品数量", internetHospitalDemandDetail.getDrugNum());
            map.put("记录生成时间", internetHospitalDemandDetail.getCreateTime());
            map.put("记录更新时间", internetHospitalDemandDetail.getUpdateTime());
            map.put("药品唯一码", internetHospitalDemandDetail.getProductAttrUnique());
            map.put("益药宝sku", internetHospitalDemandDetail.getYiyaobaoSku());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
