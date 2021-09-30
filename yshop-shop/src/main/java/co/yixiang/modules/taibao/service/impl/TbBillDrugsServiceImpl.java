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
import co.yixiang.modules.taibao.domain.TbBillDrugs;
import co.yixiang.modules.taibao.service.TbBillDrugsService;
import co.yixiang.modules.taibao.service.dto.TbBillDrugsDto;
import co.yixiang.modules.taibao.service.dto.TbBillDrugsQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbBillDrugsMapper;
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
//@CacheConfig(cacheNames = "tbBillDrugs")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbBillDrugsServiceImpl extends BaseServiceImpl<TbBillDrugsMapper, TbBillDrugs> implements TbBillDrugsService {

    private final IGenerator generator;
    @Autowired
    private TbBillDrugsMapper billDrugsMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbBillDrugsQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbBillDrugs> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbBillDrugsDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbBillDrugs> queryAll(TbBillDrugsQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbBillDrugs.class, criteria));
    }


    @Override
    public void download(List<TbBillDrugsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbBillDrugsDto tbBillDrugs : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("收据信息ID", tbBillDrugs.getBillId());
            map.put("药品代码", tbBillDrugs.getDrugCode());
            map.put("药品名称", tbBillDrugs.getDrugName());
            map.put("对应账单项代码", tbBillDrugs.getDrugBillCode());
            map.put("规格", tbBillDrugs.getDrugStd());
            map.put("剂型", tbBillDrugs.getDrugType());
            map.put("单位", tbBillDrugs.getDrugUnit());
            map.put("单价", tbBillDrugs.getDrugUnitAmt());
            map.put("数量", tbBillDrugs.getDrugTotal());
            map.put("发生金额", tbBillDrugs.getDrugPay());
            map.put("医保类别", tbBillDrugs.getMedicalType());
            map.put("自付比例", tbBillDrugs.getSelfpayRate());
            map.put("自付金额", tbBillDrugs.getSelfpayAmt());
            map.put("是否剔除", tbBillDrugs.getRejectFlag());
            map.put("剔除原因", tbBillDrugs.getRejectReason());
            map.put("创建人", tbBillDrugs.getCreateBy());
            map.put("创建时间", tbBillDrugs.getCreateTime());
            map.put("修改人", tbBillDrugs.getUpdateBy());
            map.put("修改时间", tbBillDrugs.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbBillDrugs.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbBillDrugs> selectByMainId(String mainId) {
        return billDrugsMapper.selectByMainId(Long.valueOf(mainId));
    }
}
