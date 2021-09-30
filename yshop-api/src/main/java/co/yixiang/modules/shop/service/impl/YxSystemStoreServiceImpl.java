/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.CommonEnum;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.mapper.YxSystemStoreMapper;
import co.yixiang.modules.shop.mapping.SystemStoreMap;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.LocationUtils;
import co.yixiang.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 门店自提 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2020-03-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxSystemStoreServiceImpl extends BaseServiceImpl<YxSystemStoreMapper, YxSystemStore> implements YxSystemStoreService {

    @Autowired
    private YxSystemStoreMapper yxSystemStoreMapper;

    @Autowired
    private SystemStoreMap storeMap;

    @Override
    public List<YxSystemStoreQueryVo> getStoreList(String latitude, String longitude, int page, int limit,List<String> countryList,String keyword,String provinceName) {

        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        List<YxSystemStoreQueryVo> list = yxSystemStoreMapper.getStoreList4County(pageModel,Double.valueOf(longitude),Double.valueOf(latitude),countryList,keyword,provinceName);
        list.forEach(item->{
            String newDis = NumberUtil.round(Double.valueOf(item.getDistance()) / 1000,2).toString();
            item.setDistance(newDis);
        });
        return list;
    }

    @Override
    public List<YxSystemStoreQueryVo> getStoreListByProductId(String latitude, String longitude, int page, int limit,Integer productId,String projectCode) {
        List<YxSystemStoreQueryVo> list = new ArrayList<>();
        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        if(StrUtil.isNotBlank(projectCode)) {
            list = yxSystemStoreMapper.getStoreListByProductIdProjectCode(pageModel,Double.valueOf(longitude),Double.valueOf(latitude),productId,projectCode);
        } else {
            list = yxSystemStoreMapper.getStoreListByProductId(pageModel,Double.valueOf(longitude),Double.valueOf(latitude),productId, ShopConstants.STORENAME_GUANGZHOU_CLOUD);
        }

        list.forEach(item->{
            String newDis = NumberUtil.round(Double.valueOf(item.getDistance()) / 1000,2).toString();
            item.setDistance(newDis);
        });
        return list;
    }


    @Override
    public List<YxSystemStoreQueryVo> getStoreListByProductIdStoreIds(String latitude, String longitude, int page, int limit,Integer productId,List<Integer> storeIds) {
        List<YxSystemStoreQueryVo> list = new ArrayList<>();
        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        list = yxSystemStoreMapper.getStoreListByProductIdStoreIds(pageModel,Double.valueOf(longitude),Double.valueOf(latitude),productId, storeIds);

        list.forEach(item->{
            String newDis = NumberUtil.round(Double.valueOf(item.getDistance()) / 1000,2).toString();
            item.setDistance(newDis);
        });
        return list;
    }

    @Override
    public List<YxSystemStoreQueryVo> getStoreListByProductIdStoreIdsNoGPS(int page, int limit, Integer productId, List<Integer> storeIds) {

        List<YxSystemStoreQueryVo> list = new ArrayList<>();
        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        list = yxSystemStoreMapper.getStoreListByProductIdStoreIdsNoGPS(pageModel,productId, storeIds);
        return list;
    }

    @Override
    public List<YxSystemStoreQueryVo> getStoreListByProductIdNoGPS(int page, int limit,Integer productId,String projectCode) {
        List<YxSystemStoreQueryVo> list = new ArrayList<>();
        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        if(StrUtil.isNotBlank(projectCode)) {
            list = yxSystemStoreMapper.getStoreListByProductIdProjectCodeNoGPS(pageModel,productId,projectCode);
        } else {
            list = yxSystemStoreMapper.getStoreListByProductIdNoGPS(pageModel,productId);
        }

        list.forEach(item->{
            String newDis = "0";
            item.setDistance(newDis);
        });
        return list;
    }

    @Override
    public YxSystemStoreQueryVo getStoreInfo(String latitude,String longitude) {
        YxSystemStore systemStore = new YxSystemStore();
        systemStore.setIsDel(CommonEnum.DEL_STATUS_0.getValue());
        systemStore.setIsShow(CommonEnum.SHOW_STATUS_1.getValue());
        YxSystemStore yxSystemStore = yxSystemStoreMapper.selectOne(
                Wrappers
                .query(systemStore)
                .orderByDesc("id")
                .last("limit 1"));
        if(yxSystemStore == null) return null;
        String mention = RedisUtil.get("store_self_mention");
        if(mention == null || Integer.valueOf(mention) == 2) return null;
        YxSystemStoreQueryVo systemStoreQueryVo = storeMap.toDto(yxSystemStore);
        if(StrUtil.isNotEmpty(latitude) && StrUtil.isNotEmpty(longitude)){
            double distance = LocationUtils.getDistance(Double.valueOf(latitude),Double.valueOf(longitude),
                    Double.valueOf(yxSystemStore.getLatitude()),Double.valueOf(yxSystemStore.getLongitude()));
            systemStoreQueryVo.setDistance(String.valueOf(distance));
        }
        return systemStoreQueryVo;
    }

    @Override
    public YxSystemStoreQueryVo getYxSystemStoreById(Serializable id){
        return yxSystemStoreMapper.getYxSystemStoreById(id);
    }

    @Override
    public Paging<YxSystemStoreQueryVo> getYxSystemStorePageList(YxSystemStoreQueryParam yxSystemStoreQueryParam){
        Page page = setPageParam(yxSystemStoreQueryParam,OrderItem.desc("add_time"));

        QueryWrapper queryWrapper = new QueryWrapper();
        if(CollUtil.isNotEmpty(yxSystemStoreQueryParam.getSelectCountrys())) {
            queryWrapper.in("city_code",yxSystemStoreQueryParam.getSelectCountrys());
        }

        if(StrUtil.isNotBlank(yxSystemStoreQueryParam.getKeyword())) {
            queryWrapper.like("name",yxSystemStoreQueryParam.getKeyword());
        }

        if(StrUtil.isNotBlank(yxSystemStoreQueryParam.getProvinceName())) {
            queryWrapper.eq("province_name",yxSystemStoreQueryParam.getProvinceName());
        }

        IPage<YxSystemStore> iPage = yxSystemStoreMapper.selectPage(page,queryWrapper);

        Paging paging = new Paging<>();
       paging.setTotal(iPage.getTotal());
       paging.setRecords(storeMap.toDto(iPage.getRecords()));
        return paging;
    }

}
