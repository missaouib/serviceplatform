/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.service.impl;

import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.yaolian.domain.YaolianOrder;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.yaolian.domain.YaolianOrderDetail;
import co.yixiang.modules.yaolian.service.YaolianOrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaolian.service.YaolianOrderService;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderDto;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderQueryCriteria;
import co.yixiang.modules.yaolian.service.mapper.YaolianOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
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
* @author visa
* @date 2021-03-02
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaolianOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaolianOrderServiceImpl extends BaseServiceImpl<YaolianOrderMapper, YaolianOrder> implements YaolianOrderService {

    private final IGenerator generator;

    @Autowired
    private YaolianOrderDetailService yaolianOrderDetailService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaolianOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaolianOrder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);

        List<YaolianOrderDto> dtoList =  generator.convert(page.getList(), YaolianOrderDto.class);
        for(YaolianOrderDto dto:dtoList) {
            LambdaQueryWrapper<YaolianOrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(YaolianOrderDetail::getOrderId,dto.getId());
            List<YaolianOrderDetail> yaolianOrderDetailList =  yaolianOrderDetailService.list(lambdaQueryWrapper);

            for(YaolianOrderDetail yaolianOrderDetail:yaolianOrderDetailList) {
                LambdaQueryWrapper<YxStoreProduct> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                lambdaQueryWrapper1.eq(YxStoreProduct::getId,yaolianOrderDetail.getDrugId());
                lambdaQueryWrapper1.select(YxStoreProduct::getStoreName,YxStoreProduct::getCommonName,YxStoreProduct::getManufacturer,YxStoreProduct::getSpec);
                YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(lambdaQueryWrapper1) ;
                yaolianOrderDetail.setStoreName(yxStoreProduct.getStoreName());
                yaolianOrderDetail.setCommonName(yxStoreProduct.getCommonName());
                yaolianOrderDetail.setManufacturer(yxStoreProduct.getManufacturer());
                yaolianOrderDetail.setSpec(yxStoreProduct.getSpec());
            }

            dto.setOrderDetails(yaolianOrderDetailList);
        }
        map.put("content", dtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaolianOrder> queryAll(YaolianOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaolianOrder.class, criteria));
    }


    @Override
    public void download(List<YaolianOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaolianOrderDto yaolianOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("药联下单时间", yaolianOrder.getCreateTime());
            map.put("门店内码", yaolianOrder.getStoreId());
            map.put("会员ID（需要配合会员数据录入接口实现）", yaolianOrder.getMemberId());
            map.put("店员手机号", yaolianOrder.getAssistantMobile());
            map.put("店员工号", yaolianOrder.getAssistantNumber());
            map.put("订单总价", yaolianOrder.getTotalPrice());
            map.put("药联直付金额", yaolianOrder.getFreePrice());
            map.put("顾客自付金额", yaolianOrder.getSalePrice());
            map.put("超级会员日订单标示", yaolianOrder.getIssuper());
            map.put("订单是否有处方单标示，1是存在处方单，0是没有", yaolianOrder.getIsPrescription());
            map.put("处方单流水号", yaolianOrder.getRxId());
            map.put("益药宝订单id", yaolianOrder.getYiyaobaoOrderId());
            map.put("益药宝订单号", yaolianOrder.getYiyaobaoOrderNo());
            map.put("是否已经下发至益药宝平台 0/否， 1/是", yaolianOrder.getUploadYiyaobaoFlag());
            map.put("下发至益药宝平台的时间", yaolianOrder.getUploadYiyaobaoTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
