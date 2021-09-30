/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.YxStoreDisease;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductAttrValue;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseMed;
import co.yixiang.common.service.impl.BaseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseMedService;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseMedDto;
import co.yixiang.modules.yaoshitong.service.dto.YaoshitongRepurchaseMedQueryCriteria;
import co.yixiang.modules.yaoshitong.service.mapper.YaoshitongRepurchaseMedMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2020-10-21
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaoshitongRepurchaseMed")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongRepurchaseMedServiceImpl extends BaseServiceImpl<YaoshitongRepurchaseMedMapper, YaoshitongRepurchaseMed> implements YaoshitongRepurchaseMedService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongRepurchaseMedQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaoshitongRepurchaseMed> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YaoshitongRepurchaseMedDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongRepurchaseMed> queryAll(YaoshitongRepurchaseMedQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaoshitongRepurchaseMed.class, criteria));
    }


    @Override
    public void download(List<YaoshitongRepurchaseMedDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongRepurchaseMedDto yaoshitongRepurchaseMed : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("益药宝sku", yaoshitongRepurchaseMed.getMedSku());
            map.put("商品名", yaoshitongRepurchaseMed.getMedName());
            map.put("用药周期", yaoshitongRepurchaseMed.getMedCycle());
            map.put("药品通用名", yaoshitongRepurchaseMed.getMedCommonName());
            map.put("提前几天提醒",  yaoshitongRepurchaseMed.getReminderDays());

            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public int uploadProduct(List<Map<String, Object>> readAll) {
        List<YaoshitongRepurchaseMed> medList = new ArrayList<>();

        for(Map<String,Object> data : readAll) {
            YaoshitongRepurchaseMed med = new YaoshitongRepurchaseMed();
            String medSku = "";
            String medName = "";
            Integer medCycle = 7;
            String medCommonName = "";
            Integer reminderDays = 10;

            Object sku_Object = data.get("益药宝sku");
            if(ObjectUtil.isNotEmpty(sku_Object)) {
                medSku = String.valueOf(sku_Object);
            }

            Object medName_Object = data.get("商品名");
            if(ObjectUtil.isNotEmpty(medName_Object)) {
                medName = String.valueOf(medName_Object);
            }

            Object medCycle_Object = data.get("用药周期");
            if(ObjectUtil.isNotEmpty(medCycle_Object)) {
                medCycle =  Integer.valueOf(String.valueOf(medCycle_Object));
            }

            Object medCommonName_Object = data.get("药品通用名");
            if(ObjectUtil.isNotEmpty(medCommonName_Object)) {
                medCommonName = String.valueOf(medCommonName_Object);
            }

            Object reminderDays_Object = data.get("提前几天提醒");
            if(ObjectUtil.isNotEmpty(reminderDays_Object)) {
                reminderDays = Integer.valueOf(String.valueOf(reminderDays_Object));
            }

            med.setMedCommonName(medCommonName);
            med.setMedCycle(medCycle);
            med.setMedSku(medSku);
            med.setMedName(medName);
            med.setReminderDays(reminderDays);
            medList.add(med);

        }
        this.saveBatch(medList) ;
        return readAll.size();
    }
}
