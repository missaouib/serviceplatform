/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.MedCalculator;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.MedCalculatorService;
import co.yixiang.modules.shop.service.dto.MedCalculatorDto;
import co.yixiang.modules.shop.service.dto.MedCalculatorQueryCriteria;
import co.yixiang.modules.shop.service.mapper.MedCalculatorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-01-08
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "medCalculator")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MedCalculatorServiceImpl extends BaseServiceImpl<MedCalculatorMapper, MedCalculator> implements MedCalculatorService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MedCalculatorQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MedCalculator> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MedCalculatorDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MedCalculator> queryAll(MedCalculatorQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MedCalculator.class, criteria));
    }


    @Override
    public void download(List<MedCalculatorDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MedCalculatorDto medCalculator : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", medCalculator.getUid());
            map.put("首次日期", medCalculator.getStartDate());
            map.put("药品名称", medCalculator.getMedName());
            map.put("药品数量", medCalculator.getMedAmount());
            map.put("用药剂量，单位ml", medCalculator.getUseAmount());
            map.put("计算结果，还剩多少天", medCalculator.getResult());
            map.put("已坚持服用了多少天", medCalculator.getDays());
            map.put("记录生成时间", medCalculator.getCreateTime());
            map.put("记录更新时间", medCalculator.getUpdateTime());
            map.put("剩余量，每天定时更新的昨天的剩余量", medCalculator.getLeftAmount());
            map.put("计算日期", medCalculator.getCalcuDate());
            map.put("每次计算的剩余量", medCalculator.getLeftAmountTemp());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void calculator(MedCalculator medCalculator, Date calcuDate) {

        if (medCalculator.getMedAmount() == null || medCalculator.getUseAmount() == null|| medCalculator.getStartDate() == null) {
            return;
        }

        // 计算结果
        Integer days =  0;
        if(DateUtil.beginOfDay(calcuDate).isBefore(DateUtil.beginOfDay(medCalculator.getStartDate()))) {

        } else {
            days = new Long(DateUtil.betweenDay( DateUtil.beginOfDay(calcuDate),DateUtil.beginOfDay(medCalculator.getStartDate()),true )).intValue();
            days = days + 1;
        }


        Integer useTotalAmount = days * medCalculator.getUseAmount().intValue();
        Integer totalAmount = 80 * medCalculator.getMedAmount();
        // 剩余量
        Integer leftAmount = 0;
        if(medCalculator.getLeftAmount() == null ) {
            leftAmount = totalAmount - useTotalAmount;
        } else {
            leftAmount = medCalculator.getLeftAmount().intValue() - medCalculator.getUseAmount().intValue();
        }
        if(leftAmount <0) {
            leftAmount = 0;
        }
        // 还能服用多少天
        Integer result = 0;
        if(medCalculator.getUseAmount() != null && medCalculator.getUseAmount().intValue() != 0 ) {
            result = leftAmount / medCalculator.getUseAmount().intValue();
        }
        medCalculator.setResult(result);
        medCalculator.setDays(days);
        medCalculator.setLeftAmountTemp( leftAmount);
        // medCalculator.setCalcuDate(DateUtil.beginOfDay(calcuDate));
        this.updateById(medCalculator);
    }

    @Override
    public void calculatorJob() {
        Date currentDate = new Date();
        List<MedCalculator> medCalculatorList = this.list();
        for(MedCalculator medCalculator:medCalculatorList) {
            calculator(medCalculator,currentDate);
            medCalculator.setLeftAmount(medCalculator.getLeftAmountTemp());
            medCalculator.setCalcuDate(new Timestamp(currentDate.getTime()));
            updateById(medCalculator);
        }
    }
}
