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
import co.yixiang.modules.taibao.domain.TbBillOtherItem;
import co.yixiang.modules.taibao.service.TbBillOtherItemService;
import co.yixiang.modules.taibao.service.dto.TbBillOtherItemDto;
import co.yixiang.modules.taibao.service.dto.TbBillOtherItemQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbBillOtherItemMapper;
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
//@CacheConfig(cacheNames = "tbBillOtherItem")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbBillOtherItemServiceImpl extends BaseServiceImpl<TbBillOtherItemMapper, TbBillOtherItem> implements TbBillOtherItemService {

    private final IGenerator generator;

    @Autowired
    private  TbBillOtherItemMapper billOtherItemMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbBillOtherItemQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbBillOtherItem> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbBillOtherItemDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbBillOtherItem> queryAll(TbBillOtherItemQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbBillOtherItem.class, criteria));
    }


    @Override
    public void download(List<TbBillOtherItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbBillOtherItemDto tbBillOtherItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("收据信息ID", tbBillOtherItem.getBillId());
            map.put("项目代码", tbBillOtherItem.getItemCode());
            map.put("项目名称", tbBillOtherItem.getItemName());
            map.put("费用编码", tbBillOtherItem.getItemSubCode());
            map.put("费用名称", tbBillOtherItem.getItemSubName());
            map.put("医保类别", tbBillOtherItem.getMedicalType());
            map.put("医保编码", tbBillOtherItem.getMedicalCode());
            map.put("发生金额", tbBillOtherItem.getItemPay());
            map.put("自付比例", tbBillOtherItem.getSelfPayRate());
            map.put("自付金额", tbBillOtherItem.getSelfPayAmt());
            map.put("单价", tbBillOtherItem.getItemUnitPay());
            map.put("是否剔除", tbBillOtherItem.getRejectFlag());
            map.put("剔除原因", tbBillOtherItem.getRejectReason());
            map.put("创建人", tbBillOtherItem.getCreateBy());
            map.put("创建时间", tbBillOtherItem.getCreateTime());
            map.put("修改人", tbBillOtherItem.getUpdateBy());
            map.put("修改时间", tbBillOtherItem.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbBillOtherItem.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbBillOtherItem> selectByMainId(String mainId) {
        return billOtherItemMapper.selectByMainId(Long.valueOf(mainId));
    }
}
