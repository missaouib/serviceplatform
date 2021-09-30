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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.modules.shop.domain.MdPharmacistService;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.domain.YxWechatUser;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.service.YxWechatUserService;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseMed;
import co.yixiang.modules.yaoshitong.domain.YaoshitongRepurchaseReminder;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseMedService;
import co.yixiang.modules.yaoshitong.service.dto.*;
import co.yixiang.mp.service.YxTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.yaoshitong.service.YaoshitongRepurchaseReminderService;
import co.yixiang.modules.yaoshitong.service.mapper.YaoshitongRepurchaseReminderMapper;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2020-10-21
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yaoshitongRepurchaseReminder")
//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YaoshitongRepurchaseReminderServiceImpl extends BaseServiceImpl<YaoshitongRepurchaseReminderMapper, YaoshitongRepurchaseReminder> implements YaoshitongRepurchaseReminderService {

    private final IGenerator generator;

    @Autowired
    private YaoshitongRepurchaseMedService yaoshitongRepurchaseMedService;

    @Autowired
    private YxSystemStoreService yxSystemStoreService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Autowired
    private MdPharmacistServiceService mdPharmacistService;

    @Autowired
    private YxWechatUserService wechatUserService;

    @Autowired
    private YxTemplateService yxTemplateService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YaoshitongRepurchaseReminderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YaoshitongRepurchaseReminder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YaoshitongRepurchaseReminderDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YaoshitongRepurchaseReminder> queryAll(YaoshitongRepurchaseReminderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YaoshitongRepurchaseReminder.class, criteria));
    }


    @Override
    public void download(List<YaoshitongRepurchaseReminderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YaoshitongRepurchaseReminderDto yaoshitongRepurchaseReminder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", yaoshitongRepurchaseReminder.getName());
            map.put("电话", yaoshitongRepurchaseReminder.getPhone());
            map.put("药房名称", yaoshitongRepurchaseReminder.getDrugstoreName());
            map.put("药房id", yaoshitongRepurchaseReminder.getDrugstoreId());
            map.put("上次购买日期", yaoshitongRepurchaseReminder.getLastPurchaseDate());
            map.put("下次购买日期", yaoshitongRepurchaseReminder.getNextPurchaseDate());
            map.put("药品名称", yaoshitongRepurchaseReminder.getMedName());
            map.put("药品id", yaoshitongRepurchaseReminder.getMedId());
            map.put("药品sku编码", yaoshitongRepurchaseReminder.getMedSku());
            map.put("药品通用名", yaoshitongRepurchaseReminder.getMedCommonName());
            map.put("药品规格", yaoshitongRepurchaseReminder.getMedSpec());
            map.put("药品单位", yaoshitongRepurchaseReminder.getMedUnit());
            map.put("药品生产厂家", yaoshitongRepurchaseReminder.getMedManufacturer());
            map.put("状态", yaoshitongRepurchaseReminder.getStatus());
            map.put("首次购药日期", yaoshitongRepurchaseReminder.getFirstPurchaseDate());
            map.put("购药次数", yaoshitongRepurchaseReminder.getPurchaseTimes());
            map.put("总计购药数量", yaoshitongRepurchaseReminder.getPurchaseQty());
            map.put("上次购药数量", yaoshitongRepurchaseReminder.getLastPurchasseQty());
            map.put("用药周期", yaoshitongRepurchaseReminder.getMedCycle());
            map.put(" createTime",  yaoshitongRepurchaseReminder.getCreateTime());
            map.put(" updateTime",  yaoshitongRepurchaseReminder.getUpdateTime());
            map.put("药品图片", yaoshitongRepurchaseReminder.getImage());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean generateReminder() {
        // 循环药品
        QueryWrapper queryWrapper = new QueryWrapper();

        List<YaoshitongRepurchaseMed> repurchaseMedList = yaoshitongRepurchaseMedService.list();
        for(YaoshitongRepurchaseMed med:repurchaseMedList) {
            String sku = med.getMedSku();
            Integer medCycle = med.getMedCycle();
            List<MedSales4DrugstoreDto> medSales4DrugstoreDtoList = baseMapper.queryMedSales4Drugstore(sku);
            if(CollUtil.isNotEmpty(medSales4DrugstoreDtoList)) {
                for(MedSales4DrugstoreDto medSales4DrugstoreDto:medSales4DrugstoreDtoList) {

                    String sellerId = medSales4DrugstoreDto.getSellerId();
                    String userId = medSales4DrugstoreDto.getUserId();
                    Timestamp lastOrderDate = medSales4DrugstoreDto.getLastOrderDate();
                    Timestamp firstOrderDate = medSales4DrugstoreDto.getFirstOrderDate();
                    Integer ttlQty = medSales4DrugstoreDto.getTtlQty();
                    Integer purchaseTimes = medSales4DrugstoreDto.getPurchaseTimes();
                    // 获取上次购药信息
                    SalesInfoDto lastSalesInfo = baseMapper.queryMedLastSales(sku,sellerId,userId,lastOrderDate);
                    Integer lastQty = lastSalesInfo.getQty();
                    String provinceName = lastSalesInfo.getProvinceName();
                    String cityName = lastSalesInfo.getCityName();
                    String districtName = lastSalesInfo.getDistrictName();
                    String receiver = lastSalesInfo.getReceiver();
                    String reveiverMobile = lastSalesInfo.getMobile();
                    String address = lastSalesInfo.getAddress();

                    BigDecimal unitPrice = medSales4DrugstoreDto.getUnitPrice();
                    String phone =  "";
                    String name = "";
                    // 获取用户信息
                    Map<String,String> userMap = baseMapper.queryUserInfoById(userId);
                    if(CollUtil.isNotEmpty(userMap) && StrUtil.isNotBlank(userMap.get("mobile")) && StrUtil.isNotBlank(userMap.get("name")) ) {
                        phone = userMap.get("mobile");
                        name = userMap.get("name");

                    } else {
                        continue;
                    }

                    // 计算是否需要复购
                    // 预计复购日期
                    DateTime nextPurchaseDate = DateUtil.offsetDay(lastOrderDate, lastQty * medCycle);

                    //今天 离 预计复购日期 差多少天
                    long betweenDay = DateUtil.between(DateUtil.date(),nextPurchaseDate, DateUnit.DAY);
                    // 相差小于10天 //插入提醒表
                    if(betweenDay <= 10 || DateUtil.date().getTime() >= nextPurchaseDate.getTime()) {


                        QueryWrapper queryWrapper1 = new QueryWrapper();
                        queryWrapper1.eq("phone",phone);
                        queryWrapper1.eq("drugstore_yiyaobao_id",sellerId);
                        queryWrapper1.eq("med_sku",sku);
                        queryWrapper1.orderByDesc("last_purchase_date");
                        YaoshitongRepurchaseReminder repurchaseReminder = this.getOne(queryWrapper1,false);
                        if( repurchaseReminder != null  ) {  // 不为空
                            // 判断已有记录的最后购药日期
                           // YaoshitongRepurchaseReminder repurchaseReminder = repurchaseReminderList.get(0);
                            if( ! repurchaseReminder.getLastPurchaseDate().equals(lastOrderDate)) {  // 最后购药日期不相等
                                // 删除旧记录
                                this.remove(queryWrapper1);

                                // 新增记录
                                YaoshitongRepurchaseReminder repurchaseReminder1 = new YaoshitongRepurchaseReminder();
                                // 获取药房信息
                                QueryWrapper queryWrapper_drugstore = new QueryWrapper();
                                queryWrapper_drugstore.eq("yiyaobao_id",sellerId);
                                YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper_drugstore,false);
                                if(yxSystemStore != null) {
                                    repurchaseReminder1.setDrugstoreId(yxSystemStore.getId());
                                    repurchaseReminder1.setDrugstoreName(yxSystemStore.getName());
                                    repurchaseReminder1.setDrugstoreYiyaobaoId(sellerId);
                                } else {
                                    continue;
                                }

                                // 获取药品信息
                                QueryWrapper queryWrapper_med = new QueryWrapper();
                                queryWrapper_med.eq("yiyaobao_sku",sku);
                                YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper_med,false);
                                if(yxStoreProduct != null) {
                                    repurchaseReminder1.setMedCommonName(yxStoreProduct.getCommonName());
                                    repurchaseReminder1.setMedCycle(medCycle);
                                    repurchaseReminder1.setMedName(yxStoreProduct.getStoreName());
                                    repurchaseReminder1.setMedId(yxStoreProduct.getId());
                                    repurchaseReminder1.setMedManufacturer(yxStoreProduct.getManufacturer());
                                    repurchaseReminder1.setMedSku(sku);
                                    repurchaseReminder1.setUnitPrice(unitPrice);
                                    repurchaseReminder1.setMedSpec(yxStoreProduct.getSpec());
                                    repurchaseReminder1.setMedUnit(yxStoreProduct.getUnit());
                                    repurchaseReminder1.setImage(yxStoreProduct.getImage().split(",")[0]);
                                }else {
                                    continue;
                                }


                                repurchaseReminder1.setName(name);
                                repurchaseReminder1.setPhone(phone);
                                repurchaseReminder1.setUserYiyaobaoId(userId);

                                // 购药信息
                                repurchaseReminder1.setFirstPurchaseDate(new Timestamp(firstOrderDate.getTime()));
                                repurchaseReminder1.setLastPurchaseDate(new Timestamp(lastOrderDate.getTime()));
                                repurchaseReminder1.setLastPurchasseQty(lastQty);
                                repurchaseReminder1.setPurchaseQty(ttlQty);
                                repurchaseReminder1.setPurchaseTimes(purchaseTimes);
                                repurchaseReminder1.setStatus("否");
                                repurchaseReminder1.setNextPurchaseDate(new Timestamp(nextPurchaseDate.getTime()));
                                repurchaseReminder1.setProvinceName(provinceName);
                                repurchaseReminder1.setCityName(cityName);
                                repurchaseReminder1.setDistrictName(districtName);
                                repurchaseReminder1.setAddress(address);
                                repurchaseReminder1.setReceiver(receiver);
                                repurchaseReminder1.setReceiverMobile(reveiverMobile);
                                save(repurchaseReminder1);

                            }
                        } else {  // 没有历史记录
                            // 新增记录
                            YaoshitongRepurchaseReminder repurchaseReminder1 = new YaoshitongRepurchaseReminder();
                            // 获取药房信息
                            QueryWrapper queryWrapper_drugstore = new QueryWrapper();
                            queryWrapper_drugstore.eq("yiyaobao_id",sellerId);
                            YxSystemStore yxSystemStore = yxSystemStoreService.getOne(queryWrapper_drugstore,false);
                            if(yxSystemStore != null) {
                                repurchaseReminder1.setDrugstoreId(yxSystemStore.getId());
                                repurchaseReminder1.setDrugstoreName(yxSystemStore.getName());
                                repurchaseReminder1.setDrugstoreYiyaobaoId(sellerId);
                            } else {
                                continue;
                            }

                            // 获取药品信息
                            QueryWrapper queryWrapper_med = new QueryWrapper();
                            queryWrapper_med.eq("yiyaobao_sku",sku);
                            YxStoreProduct yxStoreProduct = yxStoreProductService.getOne(queryWrapper_med,false);
                            if(yxStoreProduct != null) {
                                repurchaseReminder1.setMedCommonName(yxStoreProduct.getCommonName());
                                repurchaseReminder1.setMedCycle(medCycle);
                                repurchaseReminder1.setMedName(yxStoreProduct.getStoreName());
                                repurchaseReminder1.setMedId(yxStoreProduct.getId());
                                repurchaseReminder1.setMedManufacturer(yxStoreProduct.getManufacturer());
                                repurchaseReminder1.setMedSku(sku);
                                repurchaseReminder1.setUnitPrice(unitPrice);
                                repurchaseReminder1.setMedSpec(yxStoreProduct.getSpec());
                                repurchaseReminder1.setMedUnit(yxStoreProduct.getUnit());
                                repurchaseReminder1.setImage(yxStoreProduct.getImage().split(",")[0]);
                            } else {
                                continue;
                            }

                            // 获取用户信息

                            repurchaseReminder1.setName(name);
                            repurchaseReminder1.setPhone(phone);
                            repurchaseReminder1.setUserYiyaobaoId(userId);

                            // 购药信息
                            repurchaseReminder1.setFirstPurchaseDate(new Timestamp(firstOrderDate.getTime()));
                            repurchaseReminder1.setLastPurchaseDate(new Timestamp(lastOrderDate.getTime()));
                            repurchaseReminder1.setLastPurchasseQty(lastQty);
                            repurchaseReminder1.setPurchaseQty(ttlQty);
                            repurchaseReminder1.setPurchaseTimes(purchaseTimes);
                            repurchaseReminder1.setStatus("否");
                            repurchaseReminder1.setNextPurchaseDate(new Timestamp(nextPurchaseDate.getTime()));
                            repurchaseReminder1.setProvinceName(provinceName);
                            repurchaseReminder1.setCityName(cityName);
                            repurchaseReminder1.setDistrictName(districtName);
                            repurchaseReminder1.setAddress(address);
                            repurchaseReminder1.setReceiver(receiver);
                            repurchaseReminder1.setReceiverMobile(reveiverMobile);
                            save(repurchaseReminder1);
                        }


                    } else { // 预计复购日期 离今天 大于10天
                        // 删除已有的复购提醒
                        QueryWrapper queryWrapper1 = new QueryWrapper();
                        queryWrapper1.eq("phone",phone);
                        queryWrapper1.eq("drugstore_yiyaobao_id",sellerId);
                        queryWrapper1.eq("med_sku",sku);
                        // 删除旧记录
                        this.remove(queryWrapper1);
                    }

                }
            }

        }

        return true;
    }

    @Override
    public Boolean medCycleNotice() {

        List<MedCycleNoticeDto> noticeDtos = baseMapper.queryMedCycleNotice();
        for(MedCycleNoticeDto noticeDto :noticeDtos) {
            // 获取药房对应的药师
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("FOREIGN_ID",noticeDto.getDrugstoreId());
            queryWrapper.in("phone","18017890127","13818909998");
            queryWrapper.isNotNull("uid");
            List<MdPharmacistService> mdPharmacistList = mdPharmacistService.list(queryWrapper);
            for(MdPharmacistService mdPharmacist : mdPharmacistList) {

                YxWechatUser wechatUser =  wechatUserService.getById(mdPharmacist.getUid());
                if(wechatUser != null) {
                    try{
                        yxTemplateService.medCycleNotice(noticeDto.getDrugstoreName(),noticeDto.getAmount(),wechatUser.getOpenid());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        }

        return true;
    }
}
