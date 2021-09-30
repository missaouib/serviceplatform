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
import co.yixiang.modules.taibao.domain.TbBillItem;
import co.yixiang.modules.taibao.service.TbBillItemService;
import co.yixiang.modules.taibao.service.dto.TbBillItemDto;
import co.yixiang.modules.taibao.service.dto.TbBillItemQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbBillItemMapper;
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
//@CacheConfig(cacheNames = "tbBillItem")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbBillItemServiceImpl extends BaseServiceImpl<TbBillItemMapper, TbBillItem> implements TbBillItemService {

    private final IGenerator generator;
    @Autowired
    private TbBillItemMapper billItemMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbBillItemQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbBillItem> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbBillItemDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbBillItem> queryAll(TbBillItemQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbBillItem.class, criteria));
    }


    @Override
    public void download(List<TbBillItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbBillItemDto tbBillItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("收据信息ID", tbBillItem.getBillId());
            map.put("账单代码", tbBillItem.getItemCode());
            map.put("账单名称", tbBillItem.getItemName());
            map.put("账单金额", tbBillItem.getPayment());
            map.put("自费金额", tbBillItem.getSelfpay());
            map.put("分类自负", tbBillItem.getClassification());
            map.put("医保给付金额", tbBillItem.getMedicalpay());
            map.put("第三方给付金额", tbBillItem.getThirdpay());
            map.put("扣费调整金额", tbBillItem.getPayback());
            map.put("创建人", tbBillItem.getCreateBy());
            map.put("创建时间", tbBillItem.getCreateTime());
            map.put("修改人", tbBillItem.getUpdateBy());
            map.put("修改时间", tbBillItem.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbBillItem.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbBillItem> selectByMainId(String mainId) {
        return billItemMapper.selectByMainId(Long.valueOf(mainId));
    }
}
