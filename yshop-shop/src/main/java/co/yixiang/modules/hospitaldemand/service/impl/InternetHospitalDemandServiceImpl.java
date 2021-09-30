/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.service.impl;

import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemand;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemandDetail;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandDetailService;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.service.YxStoreProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.hospitaldemand.service.InternetHospitalDemandService;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandDto;
import co.yixiang.modules.hospitaldemand.service.dto.InternetHospitalDemandQueryCriteria;
import co.yixiang.modules.hospitaldemand.service.mapper.InternetHospitalDemandMapper;
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
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author visa
* @date 2021-01-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "internetHospitalDemand")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InternetHospitalDemandServiceImpl extends BaseServiceImpl<InternetHospitalDemandMapper, InternetHospitalDemand> implements InternetHospitalDemandService {

    private final IGenerator generator;

    @Autowired
    private InternetHospitalDemandDetailService internetHospitalDemandDetailService;

    @Autowired
    private YxStoreProductService yxStoreProductService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(InternetHospitalDemandQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<InternetHospitalDemand> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<InternetHospitalDemandDto> demandDtoList = generator.convert(page.getList(), InternetHospitalDemandDto.class);

        for(InternetHospitalDemandDto demandDto :demandDtoList) {
            List<InternetHospitalDemandDetail> details = internetHospitalDemandDetailService.list(new QueryWrapper<InternetHospitalDemandDetail>().eq("demand_id",demandDto.getId()));
            for(InternetHospitalDemandDetail detail :details) {
                QueryWrapper queryWrapper1 =  new QueryWrapper<>().eq("yiyaobao_sku",detail.getYiyaobaoSku());
                queryWrapper1.select("store_name","common_name","spec","manufacturer");
                YxStoreProduct yxStoreProduct =  yxStoreProductService.getOne(queryWrapper1,false);
                if(yxStoreProduct != null) {
                    detail.setStoreName(yxStoreProduct.getStoreName());
                    detail.setCommonName(yxStoreProduct.getCommonName());
                    detail.setSpec(yxStoreProduct.getSpec());
                    detail.setManufacturer(yxStoreProduct.getManufacturer());
                }
            }
            demandDto.setDrugs(details);
        }

        map.put("content", demandDtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<InternetHospitalDemand> queryAll(InternetHospitalDemandQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(InternetHospitalDemand.class, criteria));
    }


    @Override
    public void download(List<InternetHospitalDemandDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InternetHospitalDemandDto internetHospitalDemand : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("患者姓名", internetHospitalDemand.getPatientName());
            map.put("患者手机号", internetHospitalDemand.getPhone());
            map.put("uid", internetHospitalDemand.getUid());
            map.put("卡类型", internetHospitalDemand.getCardType());
            map.put("卡号", internetHospitalDemand.getCardNumber());
            map.put("原始订单号", internetHospitalDemand.getOrderNumber());
            map.put("项目名称", internetHospitalDemand.getProjectCode());
            map.put(" prescriptionPdf",  internetHospitalDemand.getPrescriptionPdf());
            map.put(" image",  internetHospitalDemand.getImage());
            map.put("记录生成时间", internetHospitalDemand.getCreateTime());
            map.put("记录更新时间", internetHospitalDemand.getUpdateTime());
            map.put("处方时间 yyyyMMddHHmmss ", internetHospitalDemand.getTimeCreate());
            map.put("处方编码", internetHospitalDemand.getPrescriptionCode());
            map.put("患者身份证号", internetHospitalDemand.getPatientIdCard());
            map.put("医院名称", internetHospitalDemand.getHospitalName());
            map.put("附加字段", internetHospitalDemand.getAttrs());
            map.put("是否已经生成订单 0/否 1/是", internetHospitalDemand.getIsUse());
            map.put("订单号", internetHospitalDemand.getOrderId());
            map.put("缴费通知", internetHospitalDemand.getPayNoticeFlag());
            map.put("缴费通知时间", internetHospitalDemand.getPayNoticeDate());
            map.put("退费通知", internetHospitalDemand.getRefundNoticeFlag());
            map.put("退费通知时间", internetHospitalDemand.getRefundNoticeDate());
            map.put("物流通知", internetHospitalDemand.getExpressNoticeFlag());
            map.put("物流通知时间", internetHospitalDemand.getExpressNoticeDate());
            map.put("签收通知", internetHospitalDemand.getSignNoticeFlag());
            map.put("签收通知时间", internetHospitalDemand.getSignNoticeDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
