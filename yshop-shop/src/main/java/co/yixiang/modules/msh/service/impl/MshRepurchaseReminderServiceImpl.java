/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.msh.domain.MshRepurchaseReminder;
import co.yixiang.modules.msh.service.MshRepurchaseReminderService;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderDto;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderQueryCriteria;
import co.yixiang.modules.msh.service.dto.MshRepurchaseReminderQueryCriteria2;
import co.yixiang.modules.msh.service.mapper.MshDemandListItemMapper;
import co.yixiang.modules.msh.service.mapper.MshRepurchaseReminderMapper;
import co.yixiang.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
* @author cq
* @date 2020-12-24
*/
@Data
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mshRepurchaseReminder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MshRepurchaseReminderServiceImpl extends BaseServiceImpl<MshRepurchaseReminderMapper, MshRepurchaseReminder> implements MshRepurchaseReminderService {

	@Autowired
    private final IGenerator generator;

	@Autowired
	private MshDemandListItemMapper mshDemandListItemMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MshRepurchaseReminderQueryCriteria2 criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MshRepurchaseReminder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshRepurchaseReminderDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MshRepurchaseReminder> queryAll(MshRepurchaseReminderQueryCriteria2 criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MshRepurchaseReminder.class, criteria));
    }


    @Override
    public void download(List<MshRepurchaseReminderDto> all, HttpServletResponse response,MshRepurchaseReminderQueryCriteria2 criteria) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
    	Date now = new Date();
    	Calendar cal1 = Calendar.getInstance();
    	cal1.setTime(now);
    	// 将时分秒,毫秒域清零
    	cal1.set(Calendar.HOUR_OF_DAY, 0);
    	cal1.set(Calendar.MINUTE, 0);
    	cal1.set(Calendar.SECOND, 0);
    	cal1.set(Calendar.MILLISECOND, 0);
        for (MshRepurchaseReminderDto mshRepurchaseReminder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("MemberId",mshRepurchaseReminder.getMemberId());
            map.put("姓名", mshRepurchaseReminder.getName());
            map.put("电话", mshRepurchaseReminder.getPhone());
            map.put("药房名称", mshRepurchaseReminder.getDrugstoreName());
            map.put("药房id", mshRepurchaseReminder.getDrugstoreId());
            map.put("上次购买日期", mshRepurchaseReminder.getLastPurchaseDate());
            map.put("下次购买日期", mshRepurchaseReminder.getNextPurchaseDate());
            int num = 0;
            if(criteria.getNextPurchaseDateTo()!=null){
            	long time = mshRepurchaseReminder.getNextPurchaseDate().getTime()-((criteria.getNextPurchaseDateTo()).getTime()-3600*1000*24*14);
                if(time>0){
                	num = (int) ((mshRepurchaseReminder.getNextPurchaseDate().getTime()-((criteria.getNextPurchaseDateTo()).getTime()-3600*1000*24*14))/(3600*1000*24));
                }
            }else{
            	long time = mshRepurchaseReminder.getNextPurchaseDate().getTime()-(cal1).getTime().getTime();
                if(time>0){
                	num = (int) ((mshRepurchaseReminder.getNextPurchaseDate().getTime()-(cal1).getTime().getTime())/(3600*1000*24));
                }
            }

            map.put("剩余药量", num);
            map.put("药品名称", mshRepurchaseReminder.getMedName());
            map.put("药品id", mshRepurchaseReminder.getMedId());
            map.put("药品sku编码", mshRepurchaseReminder.getMedSku());
            map.put("药品通用名", mshRepurchaseReminder.getMedCommonName());
            map.put("药品规格", mshRepurchaseReminder.getMedSpec());
            map.put("药品单位", mshRepurchaseReminder.getMedUnit());
            map.put("药品生产厂家", mshRepurchaseReminder.getMedManufacturer());
            map.put("状态", mshRepurchaseReminder.getStatus());
            map.put("首次购药日期", mshRepurchaseReminder.getFirstPurchaseDate());
            map.put("购药次数", mshRepurchaseReminder.getPurchaseTimes());
            map.put("总计购药数量", mshRepurchaseReminder.getPurchaseQty());
            map.put("上次购药数量", mshRepurchaseReminder.getLastPurchasseQty());
            map.put("用药周期", mshRepurchaseReminder.getMedCycle());
            map.put("创建时间", mshRepurchaseReminder.getCreateTime());
            map.put("更新时间", mshRepurchaseReminder.getUpdateTime());
            map.put("药品图片", mshRepurchaseReminder.getImage());
            map.put("益药宝药房id", mshRepurchaseReminder.getDrugstoreYiyaobaoId());
            map.put("益药宝用户id", mshRepurchaseReminder.getUserYiyaobaoId());
            map.put("单价", mshRepurchaseReminder.getUnitPrice());
            map.put("是否已购买", mshRepurchaseReminder.getRepurchaseFlag());
            map.put("没购买的原因", mshRepurchaseReminder.getRepurchaseNoReason());
            map.put("没购买的原因备注信息", mshRepurchaseReminder.getRepurchaseNoReasonRemark());
            map.put("购买方式", mshRepurchaseReminder.getRepurchaseYesMethod());
            map.put("省份", mshRepurchaseReminder.getProvinceName());
            map.put("城市", mshRepurchaseReminder.getCityName());
            map.put("区县", mshRepurchaseReminder.getDistrictName());
            map.put("收货地址", mshRepurchaseReminder.getAddress());
            map.put("收货人", mshRepurchaseReminder.getReceiver());
            map.put("收货人电话", mshRepurchaseReminder.getReceiverMobile());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


	@Override
	public Map<String, Object> queryList(MshRepurchaseReminderQueryCriteria2 criteria, Pageable pageable) {
		getPage(pageable);
        PageInfo<MshRepurchaseReminder> page = new PageInfo<>(queryList(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MshRepurchaseReminderDto.class));
        map.put("totalElements", page.getTotal());
        return map;
	}

	@Override
    public List<MshRepurchaseReminder> queryList(MshRepurchaseReminderQueryCriteria2 criteria){
        return mshDemandListItemMapper.selectListByDate(criteria);
    }
}
