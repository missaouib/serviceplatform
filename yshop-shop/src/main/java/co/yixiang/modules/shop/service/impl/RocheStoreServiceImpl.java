/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import co.yixiang.modules.shop.domain.RocheStore;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.RocheStoreService;
import co.yixiang.modules.shop.service.dto.RocheStoreDto;
import co.yixiang.modules.shop.service.dto.RocheStoreQueryCriteria;
import co.yixiang.modules.shop.service.mapper.RocheStoreMapper;
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
* @date 2020-12-28
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "rocheStore")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RocheStoreServiceImpl extends BaseServiceImpl<RocheStoreMapper, RocheStore> implements RocheStoreService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(RocheStoreQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<RocheStore> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), RocheStoreDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<RocheStore> queryAll(RocheStoreQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(RocheStore.class, criteria));
    }


    @Override
    public void download(List<RocheStoreDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RocheStoreDto rocheStore : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("门店名称", rocheStore.getName());
            map.put("简介", rocheStore.getIntroduction());
            map.put("手机号码", rocheStore.getPhone());
            map.put("省市区", rocheStore.getAddress());
            map.put("详细地址", rocheStore.getDetailedAddress());
            map.put("门店logo", rocheStore.getImage());
            map.put("纬度", rocheStore.getLatitude());
            map.put("经度", rocheStore.getLongitude());
            map.put("核销有效日期", rocheStore.getValidTime());
            map.put("每日营业开关时间", rocheStore.getDayTime());
            map.put("添加时间", rocheStore.getAddTime());
            map.put("是否显示", rocheStore.getIsShow());
            map.put("是否删除", rocheStore.getIsDel());
            map.put(" dayTimeEnd",  rocheStore.getDayTimeEnd());
            map.put(" dayTimeStart",  rocheStore.getDayTimeStart());
            map.put(" validTimeEnd",  rocheStore.getValidTimeEnd());
            map.put(" validTimeStart",  rocheStore.getValidTimeStart());
            map.put("在线客户平台的组号", rocheStore.getCustomerServiceGroup());
            map.put(" yiyaobaoId",  rocheStore.getYiyaobaoId());
            map.put("省份code", rocheStore.getProvinceCode());
            map.put("省份名称", rocheStore.getProvinceName());
            map.put("城市code", rocheStore.getCityCode());
            map.put("城市name", rocheStore.getCityName());
            map.put("轮播图", rocheStore.getSliderImage());
            map.put(" createTime",  rocheStore.getCreateTime());
            map.put(" updateTime",  rocheStore.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadModel(String type,HttpServletResponse response) {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String,Object> map = new LinkedHashMap<>();
        if(type.equals("1")){
            map.put("首选供药城市", "");
            map.put("供药药房名称", "");
            map.put("供药药房地址", "");
            map.put("供药药房电话", "");
            map.put("户名", "");
            map.put("银行", "");
            map.put("账号", "");
        }else{
            map.put("服务药房名称", "");
            map.put("服务药房地址", "");
            map.put("服务药房电话", "");
        }
        list.add(map);
        try {
            FileUtil.downloadExcel(list, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
