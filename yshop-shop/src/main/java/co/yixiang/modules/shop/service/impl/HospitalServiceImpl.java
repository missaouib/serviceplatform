/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.Hospital;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.shop.service.HospitalService;
import co.yixiang.modules.shop.service.dto.HospitalDto;
import co.yixiang.modules.shop.service.dto.HospitalQueryCriteria;
import co.yixiang.modules.shop.service.mapper.HospitalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author visa
* @date 2021-06-17
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "hospital")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class HospitalServiceImpl extends BaseServiceImpl<HospitalMapper, Hospital> implements HospitalService {

    private final IGenerator generator;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(HospitalQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<Hospital> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<HospitalDto> hospitalDtoList = generator.convert(page.getList(), HospitalDto.class);
        for(HospitalDto hospitalDto:hospitalDtoList) {
            if(StrUtil.isNotBlank(hospitalDto.getStoreIds())) {
                LambdaQueryWrapper<YxSystemStore> lambdaQueryWrapper = new LambdaQueryWrapper();
                lambdaQueryWrapper.in(YxSystemStore::getId,Arrays.asList(hospitalDto.getStoreIds().split(",")));
                List<YxSystemStore> storeList = yxSystemStoreService.list(lambdaQueryWrapper);
                hospitalDto.setStoreList(storeList);
            }

        }
        map.put("content",hospitalDtoList );
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<Hospital> queryAll(HospitalQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(Hospital.class, criteria));
    }


    @Override
    public void download(List<HospitalDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HospitalDto hospital : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("医院名称", hospital.getName());
            map.put("地址", hospital.getAddress());
            map.put("logo图片", hospital.getImage());
            map.put("记录生成时间", hospital.getCreateTime());
            map.put("记录更新时间", hospital.getUpdateTime());
            map.put("站点信息", hospital.getSiteInfo());
            map.put("编码", hospital.getCode());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
