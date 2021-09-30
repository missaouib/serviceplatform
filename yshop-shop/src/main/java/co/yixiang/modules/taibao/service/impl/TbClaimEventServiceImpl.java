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
import co.yixiang.modules.taibao.domain.TbClaimEvent;
import co.yixiang.modules.taibao.domain.TbEventBill;
import co.yixiang.modules.taibao.service.TbClaimEventService;
import co.yixiang.modules.taibao.service.TbEventBillService;
import co.yixiang.modules.taibao.service.dto.TbClaimEventDto;
import co.yixiang.modules.taibao.service.dto.TbClaimEventQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbClaimEventMapper;
import co.yixiang.modules.taibao.service.mapper.TbEventBillMapper;
import co.yixiang.modules.taibao.service.vo.EventBillPage;
import co.yixiang.modules.taibao.util.MyBeanUtils;
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
//@CacheConfig(cacheNames = "tbClaimEvent")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimEventServiceImpl extends BaseServiceImpl<TbClaimEventMapper, TbClaimEvent> implements TbClaimEventService {

    private final IGenerator generator;
    @Autowired
    private TbClaimEventMapper claimEventMapper;

    @Autowired
    private TbEventBillService eventBillService;
    @Autowired
    private TbEventBillMapper eventBillMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimEventQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimEvent> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimEventDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimEvent> queryAll(TbClaimEventQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimEvent.class, criteria));
    }


    @Override
    public void download(List<TbClaimEventDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimEventDto tbClaimEvent : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赔案信息Id", tbClaimEvent.getClaimInfoId());
            map.put("索赔事故性质（枚举值）", tbClaimEvent.getClaimacc());
            map.put("疾病诊断", tbClaimEvent.getIllcode());
            map.put("就诊日期", tbClaimEvent.getCaredate());
            map.put("入院日期", tbClaimEvent.getIndate());
            map.put("出院日期", tbClaimEvent.getOutdate());
            map.put("住院天数", tbClaimEvent.getIndays());
            map.put("身故日期", tbClaimEvent.getDeadDate());
            map.put("伤残鉴定日期", tbClaimEvent.getDisableDate());
            map.put("就诊医院代码", tbClaimEvent.getHospitalInfo());
            map.put("就诊医院名称", tbClaimEvent.getClinical());
            map.put("主治医生姓名", tbClaimEvent.getDoctor());
            map.put("手术代码", tbClaimEvent.getSurgery());
            map.put("重疾代码", tbClaimEvent.getCritical());
            map.put("医保类型", tbClaimEvent.getMedicalType());
            map.put("是否转诊", tbClaimEvent.getReferral());
            map.put("转来医院名称", tbClaimEvent.getReferralHosp());
            map.put("科室名称", tbClaimEvent.getReferralClinical());
            map.put("医生姓名", tbClaimEvent.getReferralDoctor());
            map.put("预产期", tbClaimEvent.getEdc());
            map.put("预期是否单胎", tbClaimEvent.getIssingle());
            map.put("是否使用妊娠辅助医疗或人工授精", tbClaimEvent.getIsuseOther());
            map.put("具体情况", tbClaimEvent.getConditionInfo());
            map.put("收据总数", tbClaimEvent.getBillCnt());
            map.put("事件审核结论", tbClaimEvent.getAuditconclusion());
            map.put("事件审核意见", tbClaimEvent.getAuditoption());
            map.put("创建人", tbClaimEvent.getCreateBy());
            map.put("创建时间", tbClaimEvent.getCreateTime());
            map.put("修改人", tbClaimEvent.getUpdateBy());
            map.put("修改时间", tbClaimEvent.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbClaimEvent.getDelFlag());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TbClaimEvent> selectByMainId(String mainId) {
        return claimEventMapper.selectByMainId(Long.valueOf(mainId));
    }


    @Override
    @Transactional
    public void saveMain(TbClaimEvent claimEvent, List<EventBillPage> eventBillList) {
        claimEventMapper.insert(claimEvent);
        if(eventBillList!=null && eventBillList.size()>0) {
            for(EventBillPage entity:eventBillList) {
                //外键设置
                entity.setEventId(claimEvent.getId());

                TbEventBill eventBill= MyBeanUtils.convert(entity,TbEventBill.class);
//                eventBill.setBillDate(entity.getBillDate()==null?null:new Timestamp(entity.getBillDate().getTime()));
                eventBillService.saveMain(eventBill,entity.getBillItemList(),entity.getBillOtherItemList(),entity.getBillDrugsList());
            }
        }
    }

    @Override
    @Transactional
    public void updateMain(TbClaimEvent claimEvent, List<EventBillPage> eventBillList) {
        claimEventMapper.updateById(claimEvent);


        List<TbEventBill> eventBills= eventBillMapper.selectByMainId(claimEvent.getId());
        for (TbEventBill eventBill : eventBills) {
            eventBillService.delMain(String.valueOf(eventBill.getId()));
        }
        //1.先删除子表数据
        eventBillMapper.deleteByMainId(claimEvent.getId());

        //2.子表数据重新插入
        if(eventBillList!=null && eventBillList.size()>0) {
            for(EventBillPage entity:eventBillList) {
                //外键设置
                entity.setEventId(claimEvent.getId());
                TbEventBill eventBill= MyBeanUtils.convert(entity,TbEventBill.class);

                eventBillService.updateMain(eventBill,entity.getBillItemList(),entity.getBillOtherItemList(),entity.getBillDrugsList());
            }
        }
    }

    @Override
    @Transactional
    public void delMain(String id) {
        List<TbEventBill> eventBills= eventBillService.selectByMainId(id);
        for (TbEventBill eventBill : eventBills) {
            eventBillService.delMain(String.valueOf(eventBill.getId()));
        }
        eventBillMapper.deleteByMainId(Long.valueOf(id));
        claimEventMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for(Serializable id:idList) {
            List<TbEventBill> eventBills= eventBillService.selectByMainId(id.toString());
            for (TbEventBill eventBill : eventBills) {
                eventBillService.delMain(String.valueOf(eventBill.getId()));
            }
            eventBillMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimEventMapper.deleteById(id);
        }
    }
}
