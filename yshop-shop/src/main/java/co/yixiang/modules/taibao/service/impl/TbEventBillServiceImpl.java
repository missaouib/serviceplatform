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
import co.yixiang.modules.taibao.domain.TbBillItem;
import co.yixiang.modules.taibao.domain.TbBillOtherItem;
import co.yixiang.modules.taibao.domain.TbEventBill;
import co.yixiang.modules.taibao.service.TbEventBillService;
import co.yixiang.modules.taibao.service.dto.TbEventBillDto;
import co.yixiang.modules.taibao.service.dto.TbEventBillQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbBillDrugsMapper;
import co.yixiang.modules.taibao.service.mapper.TbBillItemMapper;
import co.yixiang.modules.taibao.service.mapper.TbBillOtherItemMapper;
import co.yixiang.modules.taibao.service.mapper.TbEventBillMapper;
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
import java.io.Serializable;
import java.util.*;

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
//@CacheConfig(cacheNames = "tbEventBill")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbEventBillServiceImpl extends BaseServiceImpl<TbEventBillMapper, TbEventBill> implements TbEventBillService {

    private final IGenerator generator;
    @Autowired
    private TbEventBillMapper eventBillMapper;
    @Autowired
    private TbBillItemMapper billItemMapper;
    @Autowired
    private TbBillOtherItemMapper billOtherItemMapper;
    @Autowired
    private TbBillDrugsMapper billDrugsMapper;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbEventBillQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbEventBill> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbEventBillDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbEventBill> queryAll(TbEventBillQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbEventBill.class, criteria));
    }


    @Override
    public void download(List<TbEventBillDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbEventBillDto tbEventBill : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("事件信息Id", tbEventBill.getEventId());
            map.put("收据号", tbEventBill.getBillSno());
            map.put("收据类型（枚举值） （1 住院2 门急诊 3 药店）", tbEventBill.getBillType());
            map.put("币种(枚举值)", tbEventBill.getCurrency());
            map.put("汇率", tbEventBill.getCurrRate());
            map.put("收据总金额", tbEventBill.getBillAmt());
            map.put("发票日期", tbEventBill.getBillDate());
            map.put("统筹支付", tbEventBill.getOverallpay());
            map.put("附加支付", tbEventBill.getAttachpay());
            map.put("自费金额", tbEventBill.getOwnamt());
            map.put("分类自负", tbEventBill.getDivamt());
            map.put("第三方支付", tbEventBill.getThirdpay());
            map.put("创建人", tbEventBill.getCreateBy());
            map.put("创建时间", tbEventBill.getCreateTime());
            map.put("修改人", tbEventBill.getUpdateBy());
            map.put("修改时间", tbEventBill.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbEventBill.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbEventBill> selectByMainId(String mainId) {
        return eventBillMapper.selectByMainId(Long.valueOf(mainId));
    }


    @Override
    @Transactional
    public void saveMain(TbEventBill eventBill, List<TbBillItem> billItemList, List<TbBillOtherItem> billOtherItemList, List<TbBillDrugs> billDrugsList) {
        eventBillMapper.insert(eventBill);
        if(billItemList!=null && billItemList.size()>0) {
            for(TbBillItem entity:billItemList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billItemMapper.insert(entity);
            }
        }
        if(billOtherItemList!=null && billOtherItemList.size()>0) {
            for(TbBillOtherItem entity:billOtherItemList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billOtherItemMapper.insert(entity);
            }
        }
        if(billDrugsList!=null && billDrugsList.size()>0) {
            for(TbBillDrugs entity:billDrugsList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billDrugsMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void updateMain(TbEventBill eventBill, List<TbBillItem> billItemList, List<TbBillOtherItem> billOtherItemList, List<TbBillDrugs> billDrugsList) {
        eventBillMapper.updateById(eventBill);

        //1.先删除子表数据
        billItemMapper.deleteByMainId(eventBill.getId());
        billOtherItemMapper.deleteByMainId(eventBill.getId());
        billDrugsMapper.deleteByMainId(eventBill.getId());

        //2.子表数据重新插入
        if(billItemList!=null && billItemList.size()>0) {
            for(TbBillItem entity:billItemList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billItemMapper.insert(entity);
            }
        }
        if(billOtherItemList!=null && billOtherItemList.size()>0) {
            for(TbBillOtherItem entity:billOtherItemList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billOtherItemMapper.insert(entity);
            }
        }
        if(billDrugsList!=null && billDrugsList.size()>0) {
            for(TbBillDrugs entity:billDrugsList) {
                //外键设置
                entity.setBillId(eventBill.getId());
                billDrugsMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void delMain(String id) {
        billItemMapper.deleteByMainId(Long.valueOf(id));
        billOtherItemMapper.deleteByMainId(Long.valueOf(id));
        billDrugsMapper.deleteByMainId(Long.valueOf(id));
        eventBillMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for(Serializable id:idList) {
            billItemMapper.deleteByMainId(Long.valueOf(id.toString()));
            billOtherItemMapper.deleteByMainId(Long.valueOf(id.toString()));
            billDrugsMapper.deleteByMainId(Long.valueOf(id.toString()));
            eventBillMapper.deleteById(id);
        }
    }
}
