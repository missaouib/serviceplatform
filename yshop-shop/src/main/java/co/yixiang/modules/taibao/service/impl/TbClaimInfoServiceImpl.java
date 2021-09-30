/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.impl;

import cn.hutool.core.util.ObjectUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.taibao.domain.*;
import co.yixiang.modules.taibao.service.TbClaimEventService;
import co.yixiang.modules.taibao.service.TbClaimInfoService;
import co.yixiang.modules.taibao.service.TbEventBillService;
import co.yixiang.modules.taibao.service.TbPolicyInfoService;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoDto;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.*;
import co.yixiang.modules.taibao.service.vo.*;
import co.yixiang.modules.taibao.util.*;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
//@AllArgsConstructor
//@CacheConfig(cacheNames = "tbClaimInfo")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbClaimInfoServiceImpl extends BaseServiceImpl<TbClaimInfoMapper, TbClaimInfo> implements TbClaimInfoService {

    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private IGenerator generator;

    @Autowired
    private TbClaimInfoMapper claimInfoMapper;
    @Autowired
    private TbNotificationPersonMapper notificationPersonMapper;
    @Autowired
    private TbInsurancePersonMapper insurancePersonMapper;
    @Autowired
    private TbClaimThirdInsuranceMapper claimThirdInsuranceMapper;
    @Autowired
    private TbClaimThirdPayMapper claimThirdPayMapper;
    @Autowired
    private TbClaimMaterialMapper claimMaterialMapper;
    @Autowired
    private TbClaimOtherMapper claimOtherMapper;
    @Autowired
    private TbClaimInvestMapper claimInvestMapper;
    @Autowired
    private TbClaimClmestimateMapper claimClmestimateMapper;
    @Autowired
    private TbClaimConsultMapper claimConsultMapper;
    @Autowired
    private TbClaimBenefitPersonMapper claimBenefitPersonMapper;
    @Autowired
    private TbClaimClaimPayMapper claimClaimPayMapper;
    @Autowired
    private TbClaimAddMaterialMapper claimAddMaterialMapper;
    @Autowired
    private TbClaimAuditInfoMapper claimAuditInfoMapper;
    @Autowired
    private TbClaimAuditpolicyMapper claimAuditpolicyMapper;
    @Autowired
    private TbClaimAboveMapper claimAboveMapper;
    @Autowired
    private TbClaimAccInfoMapper claimAccInfoMapper;
    @Autowired
    private TbClaimEventMapper claimEventMapper;
    @Autowired
    private TbBillItemMapper tbBillItemMapper;
    @Autowired
    private TbBillDrugsMapper tbBillDrugsMapper;
    @Autowired
    private TbBillOtherItemMapper tbBillOtherItemMapper;

    @Autowired
    private TbEventBillService eventBillService;
    @Autowired
    private TbClaimEventService claimEventService;

    @Autowired
    private YxStoreOrderService storeOrderService;

    @Autowired
    private TbPolicyInfoService tbPolicyInfoService;

    @Value("${file.path}")
    private String path;

    @Value("${fpt.host}")
    private String ftpHost;

    @Value("${fpt.port}")
    private String ftpPort;

    @Value("${fpt.username}")
    private String ftpUserName;

    @Value("${fpt.password}")
    private String ftpPassword;

    @Value("${fpt.lastResultXmlUrl}")
    private String lastResultXmlUrl;

    @Value("${fpt.lastResultImagesUrl}")
    private String lastResultImagesUrl;

    @Value("${fpt.lastResultPdfUrl}")
    private String lastResultPdfUrl;


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbClaimInfoQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbClaimInfo> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbClaimInfoDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbClaimInfo> queryAll(TbClaimInfoQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(TbClaimInfo.class, criteria));
    }


    @Override
    public void download(List<TbClaimInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbClaimInfoDto tbClaimInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("创建人", tbClaimInfo.getCreateBy());
            map.put("创建日期", tbClaimInfo.getCreateTime());
            map.put("更新人", tbClaimInfo.getUpdateBy());
            map.put("更新日期", tbClaimInfo.getUpdateTime());
            map.put("所属部门", tbClaimInfo.getSysOrgCode());
            map.put("报案号", tbClaimInfo.getBatchno());
            map.put("赔案号", tbClaimInfo.getClaimno());
            map.put("收单单位代码", tbClaimInfo.getCustmco());
            map.put("快递签收时间", tbClaimInfo.getExptime());
            map.put("医保号", tbClaimInfo.getMedicalCode());
            map.put("是否接受电子邮件", tbClaimInfo.getEmailAccept());
            map.put("收单时间", tbClaimInfo.getVisitDate());
            map.put("复核意见", tbClaimInfo.getReauditoption());
            map.put("复核完成时间", tbClaimInfo.getReauditdate());
            map.put("挂起类型(多种类型用逗号拼接)", tbClaimInfo.getHangupsign());
            map.put("赔案层结论", tbClaimInfo.getClaimrescode());
            map.put("审核意见", tbClaimInfo.getAuditoption());
            map.put("删除标识", tbClaimInfo.getDelFlag());
            map.put("订单编号", tbClaimInfo.getOrderId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    @Transactional
    public void saveMain(TbClaimInfo claimInfo, TbOrderProjectParam orderProjectParam, List<TbNotificationPerson> notificationPersonList, List<TbInsurancePerson> insurancePersonList, List<TbClaimThirdInsurance> claimThirdInsuranceList, List<TbClaimThirdPay> claimThirdPayList, List<TbClaimMaterial> claimMaterialList, List<TbClaimOther> claimOtherList, List<TbClaimInvest> claimInvestList, List<TbClaimClmestimate> claimClmestimateList, List<TbClaimConsult> claimConsultList, List<TbClaimBenefitPerson> claimBenefitPersonList, List<TbClaimClaimPay> claimClaimPayList, List<TbClaimAddMaterial> claimAddMaterialList, List<TbClaimAuditInfo> claimAuditInfoList, List<TbClaimAuditpolicy> claimAuditpolicyList, List<TbClaimAbove> claimAboveList, List<TbClaimAccInfo> claimAccInfoList, List<ClaimEventPage> claimEventList) {
        if(ObjectUtil.isNull(claimInfo.getClaimno())){
            throw new ErrorRequestException("赔案号为空！");
        }
        //校验
//        checkedAmount( claimInfo,  notificationPersonList,  insurancePersonList,  claimClmestimateList, claimBenefitPersonList, claimClaimPayList,  claimAuditInfoList, claimAccInfoList, claimEventList);

        TbClaimInfo tbClaimInfo=  claimInfoMapper.getByClaimno(claimInfo.getClaimno());
        if(tbClaimInfo!=null){
            return;
        }

        TbPolicyInfo tbPolicyInfo= tbPolicyInfoService.getByClaimno(claimInfo.getClaimno());
        if(tbPolicyInfo==null){
            orderProjectParam.setDeductibleTotal(new BigDecimal(0));
            orderProjectParam.setResponsibilityTotal(new BigDecimal(0));
        }else{
            orderProjectParam.setDeductibleTotal(tbPolicyInfo.getDeductibleTotal());
            orderProjectParam.setResponsibilityTotal(tbPolicyInfo.getResponsibilityTotal());

        }
        YxStoreOrder order= storeOrderService.addTbOrderProject(orderProjectParam);//addOrderProject(orderProjectParam);
        if(ObjectUtil.isNull(order)){
            throw new ErrorRequestException("订单生成失败");
        }

        claimInfo.setOrderId(Long.valueOf(order.getId()));
        String username= SecurityUtils.getUsername();
        claimInfo.setCreateBy(username);
        claimInfoMapper.insert(claimInfo);
        if(notificationPersonList!=null && notificationPersonList.size()>0) {
            for(TbNotificationPerson entity:notificationPersonList) {
                //外键设置

                entity.setClaimInfoId(claimInfo.getId());
                notificationPersonMapper.insert(entity);
            }
        }
        if(insurancePersonList!=null && insurancePersonList.size()>0) {
            for(TbInsurancePerson entity:insurancePersonList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                insurancePersonMapper.insert(entity);
            }
        }
        if(claimThirdInsuranceList!=null && claimThirdInsuranceList.size()>0) {
            for(TbClaimThirdInsurance entity:claimThirdInsuranceList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimThirdInsuranceMapper.insert(entity);
            }
        }
        if(claimThirdPayList!=null && claimThirdPayList.size()>0) {
            for(TbClaimThirdPay entity:claimThirdPayList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimThirdPayMapper.insert(entity);
            }
        }
        if(claimMaterialList!=null && claimMaterialList.size()>0) {
            for(TbClaimMaterial entity:claimMaterialList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimMaterialMapper.insert(entity);
            }
        }
        if(claimOtherList!=null && claimOtherList.size()>0) {
            for(TbClaimOther entity:claimOtherList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimOtherMapper.insert(entity);
            }
        }
        if(claimInvestList!=null && claimInvestList.size()>0) {
            for(TbClaimInvest entity:claimInvestList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimInvestMapper.insert(entity);
            }
        }
        if(claimClmestimateList!=null && claimClmestimateList.size()>0) {
            for(TbClaimClmestimate entity:claimClmestimateList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimClmestimateMapper.insert(entity);
            }
        }
        if(claimConsultList!=null && claimConsultList.size()>0) {
            for(TbClaimConsult entity:claimConsultList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimConsultMapper.insert(entity);
            }
        }
        if(claimBenefitPersonList!=null && claimBenefitPersonList.size()>0) {
            for(TbClaimBenefitPerson entity:claimBenefitPersonList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimBenefitPersonMapper.insert(entity);
            }
        }
        if(claimClaimPayList!=null && claimClaimPayList.size()>0) {
            for(TbClaimClaimPay entity:claimClaimPayList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimClaimPayMapper.insert(entity);
            }
        }
        if(claimAddMaterialList!=null && claimAddMaterialList.size()>0) {
            for(TbClaimAddMaterial entity:claimAddMaterialList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAddMaterialMapper.insert(entity);
            }
        }
        if(claimAuditInfoList!=null && claimAuditInfoList.size()>0) {
            for(TbClaimAuditInfo entity:claimAuditInfoList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAuditInfoMapper.insert(entity);
            }
        }
        if(claimAuditpolicyList!=null && claimAuditpolicyList.size()>0) {
            for(TbClaimAuditpolicy entity:claimAuditpolicyList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAuditpolicyMapper.insert(entity);
            }
        }
        if(claimAboveList!=null && claimAboveList.size()>0) {
            for(TbClaimAbove entity:claimAboveList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAboveMapper.insert(entity);
            }
        }
        if(claimAccInfoList!=null && claimAccInfoList.size()>0) {
            for(TbClaimAccInfo entity:claimAccInfoList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAccInfoMapper.insert(entity);
            }
        }
        if(claimEventList!=null && claimEventList.size()>0) {
            for(ClaimEventPage entity:claimEventList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                TbClaimEvent claimEvent= MyBeanUtils.convert(entity,TbClaimEvent.class);
//                claimEvent.setCaredate(entity.getCaredate()==null?null:new Timestamp(entity.getCaredate().getTime()));
//                claimEvent.setIndate(entity.getIndate()==null?null:new Timestamp(entity.getIndate().getTime()));
//                claimEvent.setOutdate(entity.getOutdate()==null?null:new Timestamp(entity.getOutdate().getTime()));
//                claimEvent.setDeadDate(entity.getDeadDate()==null?null:new Timestamp(entity.getDeadDate().getTime()));
//                claimEvent.setDisableDate(entity.getDisableDate()==null?null:new Timestamp(entity.getDisableDate().getTime()));
                claimEventService.saveMain(claimEvent,entity.getEventBillList());
            }
        }
    }


    @Override
    @Transactional
    public void updateMain(TbClaimInfo claimInfo, TbOrderProjectParam orderProjectParam, List<TbNotificationPerson> notificationPersonList, List<TbInsurancePerson> insurancePersonList, List<TbClaimThirdInsurance> claimThirdInsuranceList, List<TbClaimThirdPay> claimThirdPayList, List<TbClaimMaterial> claimMaterialList, List<TbClaimOther> claimOtherList, List<TbClaimInvest> claimInvestList, List<TbClaimClmestimate> claimClmestimateList, List<TbClaimConsult> claimConsultList, List<TbClaimBenefitPerson> claimBenefitPersonList, List<TbClaimClaimPay> claimClaimPayList, List<TbClaimAddMaterial> claimAddMaterialList, List<TbClaimAuditInfo> claimAuditInfoList, List<TbClaimAuditpolicy> claimAuditpolicyList, List<TbClaimAbove> claimAboveList, List<TbClaimAccInfo> claimAccInfoList, List<ClaimEventPage> claimEventList) {
        if(ObjectUtil.isNull(claimInfo.getClaimno())){
            throw new ErrorRequestException("赔案号为空！");
        }
        //校验
//        checkedAmount( claimInfo,  notificationPersonList,  insurancePersonList,  claimClmestimateList, claimBenefitPersonList, claimClaimPayList,  claimAuditInfoList, claimAccInfoList, claimEventList);

        TbPolicyInfo tbPolicyInfo= tbPolicyInfoService.getByClaimno(claimInfo.getClaimno());
        if(tbPolicyInfo==null){
            orderProjectParam.setDeductibleTotal(new BigDecimal(0));
            orderProjectParam.setResponsibilityTotal(new BigDecimal(0));
        }else{
            orderProjectParam.setDeductibleTotal(tbPolicyInfo.getDeductibleTotal());
            orderProjectParam.setResponsibilityTotal(tbPolicyInfo.getResponsibilityTotal());

        }
        YxStoreOrder order=  storeOrderService.getById(orderProjectParam.getId());
        if(order.getUploadYiyaobaoFlag()==0){
            order= storeOrderService.addTbOrderProject(orderProjectParam);//addOrderProject(orderProjectParam);
            if(ObjectUtil.isNull(order)){
                throw new ErrorRequestException("订单生成失败");
            }
        }

        claimInfoMapper.updateById(claimInfo);

        //1.先删除子表数据
        notificationPersonMapper.deleteByMainId(claimInfo.getId());
        insurancePersonMapper.deleteByMainId(claimInfo.getId());
        claimThirdInsuranceMapper.deleteByMainId(claimInfo.getId());
        claimThirdPayMapper.deleteByMainId(claimInfo.getId());
        claimMaterialMapper.deleteByMainId(claimInfo.getId());
        claimOtherMapper.deleteByMainId(claimInfo.getId());
        claimInvestMapper.deleteByMainId(claimInfo.getId());
        claimClmestimateMapper.deleteByMainId(claimInfo.getId());
        claimConsultMapper.deleteByMainId(claimInfo.getId());
        claimBenefitPersonMapper.deleteByMainId(claimInfo.getId());
        claimClaimPayMapper.deleteByMainId(claimInfo.getId());
        claimAddMaterialMapper.deleteByMainId(claimInfo.getId());
        claimAuditInfoMapper.deleteByMainId(claimInfo.getId());
        claimAuditpolicyMapper.deleteByMainId(claimInfo.getId());
        claimAboveMapper.deleteByMainId(claimInfo.getId());
        claimAccInfoMapper.deleteByMainId(claimInfo.getId());

        List<TbClaimEvent> claimEvents= claimEventService.selectByMainId(claimInfo.getId().toString());
        for (TbClaimEvent claimEvent : claimEvents) {
            claimEventService.delMain(claimEvent.getId().toString());
        }
        claimEventMapper.deleteByMainId(claimInfo.getId());

        //2.子表数据重新插入
        if(notificationPersonList!=null && notificationPersonList.size()>0) {
            for(TbNotificationPerson entity:notificationPersonList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                notificationPersonMapper.insert(entity);
            }
        }
        if(insurancePersonList!=null && insurancePersonList.size()>0) {
            for(TbInsurancePerson entity:insurancePersonList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                insurancePersonMapper.insert(entity);
            }
        }
        if(claimThirdInsuranceList!=null && claimThirdInsuranceList.size()>0) {
            for(TbClaimThirdInsurance entity:claimThirdInsuranceList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimThirdInsuranceMapper.insert(entity);
            }
        }
        if(claimThirdPayList!=null && claimThirdPayList.size()>0) {
            for(TbClaimThirdPay entity:claimThirdPayList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimThirdPayMapper.insert(entity);
            }
        }
        if(claimMaterialList!=null && claimMaterialList.size()>0) {
            for(TbClaimMaterial entity:claimMaterialList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimMaterialMapper.insert(entity);
            }
        }
        if(claimOtherList!=null && claimOtherList.size()>0) {
            for(TbClaimOther entity:claimOtherList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimOtherMapper.insert(entity);
            }
        }
        if(claimInvestList!=null && claimInvestList.size()>0) {
            for(TbClaimInvest entity:claimInvestList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimInvestMapper.insert(entity);
            }
        }
        if(claimClmestimateList!=null && claimClmestimateList.size()>0) {
            for(TbClaimClmestimate entity:claimClmestimateList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimClmestimateMapper.insert(entity);
            }
        }
        if(claimConsultList!=null && claimConsultList.size()>0) {
            for(TbClaimConsult entity:claimConsultList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimConsultMapper.insert(entity);
            }
        }
        if(claimBenefitPersonList!=null && claimBenefitPersonList.size()>0) {
            for(TbClaimBenefitPerson entity:claimBenefitPersonList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimBenefitPersonMapper.insert(entity);
            }
        }
        if(claimClaimPayList!=null && claimClaimPayList.size()>0) {
            for(TbClaimClaimPay entity:claimClaimPayList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimClaimPayMapper.insert(entity);
            }
        }
        if(claimAddMaterialList!=null && claimAddMaterialList.size()>0) {
            for(TbClaimAddMaterial entity:claimAddMaterialList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAddMaterialMapper.insert(entity);
            }
        }
        if(claimAuditInfoList!=null && claimAuditInfoList.size()>0) {
            for(TbClaimAuditInfo entity:claimAuditInfoList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAuditInfoMapper.insert(entity);
            }
        }
        if(claimAuditpolicyList!=null && claimAuditpolicyList.size()>0) {
            for(TbClaimAuditpolicy entity:claimAuditpolicyList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAuditpolicyMapper.insert(entity);
            }
        }
        if(claimAboveList!=null && claimAboveList.size()>0) {
            for(TbClaimAbove entity:claimAboveList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAboveMapper.insert(entity);
            }
        }
        if(claimAccInfoList!=null && claimAccInfoList.size()>0) {
            for(TbClaimAccInfo entity:claimAccInfoList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                claimAccInfoMapper.insert(entity);
            }
        }
        if(claimEventList!=null && claimEventList.size()>0) {
            for(ClaimEventPage entity:claimEventList) {
                //外键设置
                entity.setClaimInfoId(claimInfo.getId());
                TbClaimEvent claimEvent= MyBeanUtils.convert(entity,TbClaimEvent.class);
                claimEventService.saveMain(claimEvent,entity.getEventBillList());
            }

        }
    }

    public void checkedAmount(TbClaimInfo claimInfo, List<TbNotificationPerson> notificationPersonList, List<TbInsurancePerson> insurancePersonList,  List<TbClaimClmestimate> claimClmestimateList, List<TbClaimBenefitPerson> claimBenefitPersonList, List<TbClaimClaimPay> claimClaimPayList,  List<TbClaimAuditInfo> claimAuditInfoList, List<TbClaimAccInfo> claimAccInfoList, List<ClaimEventPage> claimEventList){
        if(StringUtils.isEmpty(claimInfo.getReportno())
           || StringUtils.isEmpty(claimInfo.getBatchno())
           || StringUtils.isEmpty(claimInfo.getClaimno())
           || StringUtils.isEmpty(claimInfo.getEmailAccept())
           || StringUtils.isEmpty(claimInfo.getPdfUrl())
                ){
            throw new ErrorRequestException("基本信息->报案号、批次号、赔案号、是否接受电子邮件、pdf附件不能为空！");
        }

        if(CollectionUtils.isEmpty(notificationPersonList)){
            throw new ErrorRequestException("报案人信息不能为空。");
        }
        TbNotificationPerson notificationPerson=notificationPersonList.get(0);
        if(StringUtils.isEmpty(notificationPerson.getMobilephone())
          && StringUtils.isEmpty(notificationPerson.getTelephone())){
            throw new ErrorRequestException("报案人信息->移动电话、固定电话不能同时为空。");
        }
        if(StringUtils.isEmpty(notificationPerson.getIdtype())
           || StringUtils.isEmpty(notificationPerson.getIdno())
                || StringUtils.isEmpty(notificationPerson.getName())
                || StringUtils.isEmpty(notificationPerson.getSex())
                || notificationPerson.getNoticeDate()==null
                ){
            throw new ErrorRequestException("报案人信息->姓名、性别、报案日期、证件类型、证件号码不能为空。");
        }

        if(CollectionUtils.isEmpty(insurancePersonList)){
            throw new ErrorRequestException("被保人信息不能为空。");
        }
        TbInsurancePerson insurancePerson=insurancePersonList.get(0);
        if(StringUtils.isEmpty(insurancePerson.getIdtype())
                || StringUtils.isEmpty(insurancePerson.getIdno())
                || StringUtils.isEmpty(insurancePerson.getName())
                || StringUtils.isEmpty(insurancePerson.getSex())
                || insurancePerson.getBirthdate()==null
                ){
            throw new ErrorRequestException("被保人->姓名、性别、出生日期、证件类型、证件号码不能为空。");
        }

        if(CollectionUtils.isEmpty(claimClmestimateList)){
            throw new ErrorRequestException("理赔预估信息不能为空。");
        }
        TbClaimClmestimate claimClmestimate=claimClmestimateList.get(0);
        if(StringUtils.isEmpty(claimClmestimate.getPolicyno())
                || StringUtils.isEmpty(claimClmestimate.getClasscode())
                || claimClmestimate.getGsje()==null
                ){
            throw new ErrorRequestException("理赔预估->赔付保单号、赔付险种代码、赔付预估金额不能为空。");
        }

        if(CollectionUtils.isEmpty(claimBenefitPersonList)){
            throw new ErrorRequestException("领款人信息不能为空。");
        }
        for (TbClaimBenefitPerson  claimBenefitPerson: claimBenefitPersonList) {
            if(StringUtils.isEmpty(claimBenefitPerson.getRelationship())
                    || StringUtils.isEmpty(claimBenefitPerson.getName())
                    || StringUtils.isEmpty(claimBenefitPerson.getBftype())
                    || StringUtils.isEmpty(claimBenefitPerson.getBanksubtype())
                    || StringUtils.isEmpty(claimBenefitPerson.getAcctno())
                    || StringUtils.isEmpty(claimBenefitPerson.getSettype())
                    ){
                throw new ErrorRequestException("领款人信息->领款人姓名、领款人类型、与被保人关系、支付方式、开户行、银行账号不能为空。");
            }
        }

        if(CollectionUtils.isEmpty(claimClaimPayList)){
            throw new ErrorRequestException("责任赔付金额信息不能为空。");
        }
        TbClaimClaimPay claimClaimPay= claimClaimPayList.get(0);
        if(StringUtils.isEmpty(claimClaimPay.getPolicyno())
                || StringUtils.isEmpty(claimClaimPay.getDutycode())
                || StringUtils.isEmpty(claimClaimPay.getClasscode())
                || claimClaimPay.getClaimpay()==null
                || claimClaimPay.getAdvancepayment()==null
                || claimClaimPay.getRemaindeduction()==null
                ){
            throw new ErrorRequestException("责任赔付金额信息->保单号、险种代码、责任代码、赔付金额、垫付金额、剩余年免赔额 不能为空。");
        }

        if(CollectionUtils.isEmpty(claimAuditInfoList)){
            throw new ErrorRequestException("赔付责任理赔结论不能为空。");
        }
        for (TbClaimAuditInfo claimAuditInfo : claimAuditInfoList) {
            if(StringUtils.isEmpty(claimAuditInfo.getPolicyno())
                    || StringUtils.isEmpty(claimAuditInfo.getDutycode())
                    || StringUtils.isEmpty(claimAuditInfo.getClasscode())
                    || StringUtils.isEmpty(claimAuditInfo.getRescode())
                    || StringUtils.isEmpty(claimAuditInfo.getResreason())
                    ){
                throw new ErrorRequestException("赔付责任理赔结论->保单号、险种代码、责任代码、赔付结论、结论原因 不能为空。");
            }
        }

        if(CollectionUtils.isEmpty(claimAccInfoList)){
            throw new ErrorRequestException("出险信息不能为空。");
        }
        TbClaimAccInfo claimAccInfo= claimAccInfoList.get(0);
        if(StringUtils.isEmpty(claimAccInfo.getAccAddrType())
                || claimAccInfo.getAccDate()==null
                ||StringUtils.isEmpty(claimAccInfo.getAccInfo())
                ||StringUtils.isEmpty(claimAccInfo.getAccSubtype())
                || claimAccInfo.getFirstDate()==null
                ||StringUtils.isEmpty(claimAccInfo.getClaimacc())
                ){
            throw new ErrorRequestException("出险信息-> 出险地区、出险日期、出险经过、出险类型、初次就诊日期、索赔事故性质 不能为空。");
        }
        List<String> claimaccs= Arrays.asList(claimAccInfo.getClaimacc().split(","));

        if(CollectionUtils.isEmpty(claimEventList)){
            throw new ErrorRequestException("出险信息不能为空。");
        }
        if(claimaccs.size()!=claimEventList.size()){
            throw new ErrorRequestException("出险信息->索赔事故性质个数需与事件->事件个数一致。");
        }
        for (ClaimEventPage claimEventPage : claimEventList) {
            TbClaimEvent claimEvent = MyBeanUtils.convert(claimEventPage, TbClaimEvent.class);
            if (StringUtils.isEmpty(claimEvent.getClaimacc())
                    || claimEvent.getCaredate() == null
                    || StringUtils.isEmpty(claimEvent.getIllcode())
                    || claimEvent.getBillCnt() == null
                    || StringUtils.isEmpty(claimEvent.getAuditconclusion())
                    || StringUtils.isEmpty(claimEvent.getAuditoption())
                    ) {
                throw new ErrorRequestException("事件-> 索赔事故性质、就诊日期、疾病诊断、收据总数、事件审核结论、事件审核意见");
            }
            List<EventBillPage> eventBillPages = claimEventPage.getEventBillList();
            if (CollectionUtils.isEmpty(eventBillPages)) {
                throw new ErrorRequestException("事件->收据信息不能为空。");
            }
            if(claimEvent.getBillCnt()!=eventBillPages.size()){
                throw new ErrorRequestException("事件->收据总数 需与 收据信息->收据个数一致。");
            }
            for (EventBillPage eventBillPage : eventBillPages) {
                TbEventBill eventBill= MyBeanUtils.convert(eventBillPage,TbEventBill.class);
                if(StringUtils.isEmpty(eventBill.getBillSno())
                        || eventBill.getBillAmt() == null
                        || StringUtils.isEmpty(eventBill.getBillType())
                        || StringUtils.isEmpty(eventBill.getCurrency())
                        || StringUtils.isEmpty(eventBill.getCurrRate())
                        || eventBill.getBillDate() == null
                        ){
                    throw new ErrorRequestException("事件->收据信息->收据号、收据总金额、收据类型、币种、汇率、发票日期不能为空。");
                }
                List<TbBillItem> billItemList = eventBillPage.getBillItemList();
                List<TbBillOtherItem> billOtherItemList = eventBillPage.getBillOtherItemList();
                List<TbBillDrugs> billDrugsList = eventBillPage.getBillDrugsList();
                if (CollectionUtils.isEmpty(billOtherItemList) && CollectionUtils.isEmpty(billDrugsList)) {
                    throw new ErrorRequestException("事件->收据信息->药品清单 与 事件->收据信息->其他费用清单 必须存在其中一项。");
                }
                if (CollectionUtils.isEmpty(billItemList)) {
                    throw new ErrorRequestException("事件->收据信息->汇总项目不能为空。");
                }
                BigDecimal paymentTotal=BigDecimal.ZERO;
                BigDecimal selfpayTotal=BigDecimal.ZERO;
                BigDecimal classificationTotal=BigDecimal.ZERO;
                BigDecimal medicalpayTotal=BigDecimal.ZERO;
                BigDecimal thirdpayTotal=BigDecimal.ZERO;
                for (TbBillItem tbBillItem : billItemList) {
                    if(StringUtils.isEmpty(tbBillItem.getItemCode())
                            || StringUtils.isEmpty(tbBillItem.getItemName())
                            || tbBillItem.getPayment() == null
                            ){
                        throw new ErrorRequestException("事件->收据信息->汇总项目 账单名称、账单代码、账单金额 不能为空。");
                    }
                    paymentTotal=paymentTotal.add(tbBillItem.getPayment()==null?BigDecimal.ZERO:tbBillItem.getPayment());
                    selfpayTotal=selfpayTotal.add(tbBillItem.getSelfpay()==null?BigDecimal.ZERO:tbBillItem.getSelfpay());
                    classificationTotal=classificationTotal.add(tbBillItem.getClassification()==null?BigDecimal.ZERO:tbBillItem.getClassification());
                    medicalpayTotal=medicalpayTotal.add(tbBillItem.getMedicalpay()==null?BigDecimal.ZERO:tbBillItem.getMedicalpay());
                    thirdpayTotal=thirdpayTotal.add(tbBillItem.getThirdpay()==null?BigDecimal.ZERO:tbBillItem.getThirdpay());
                }
                if((new BigDecimal(eventBill.getBillAmt().trim()).subtract(paymentTotal)).abs().compareTo(new BigDecimal("0.5"))>0){
                    throw new ErrorRequestException("事件->收据信息->收据总金额 需与 汇总项目->账单金额节点之和 误差不能大于0.5。");
                }
                if((StringUtils.isEmpty(eventBill.getOwnamt())?BigDecimal.ZERO:new BigDecimal(eventBill.getOwnamt().trim())).compareTo(selfpayTotal)!=0){
                    throw new ErrorRequestException("事件->收据信息->自费金额 需与 汇总项目->自费金额节点之和 相等。");
                }
                if((StringUtils.isEmpty(eventBill.getDivamt())?BigDecimal.ZERO:new BigDecimal(eventBill.getDivamt().trim())).compareTo(classificationTotal)!=0){
                    throw new ErrorRequestException("事件->收据信息->分类自负 需与 汇总项目->分类自负节点之和 相等。");
                }
                BigDecimal tongFuTotal=(StringUtils.isEmpty(eventBill.getOverallpay())?BigDecimal.ZERO:new BigDecimal(eventBill.getOverallpay().trim())).add(StringUtils.isEmpty(eventBill.getAttachpay())?BigDecimal.ZERO:new BigDecimal(eventBill.getAttachpay().trim()));
                if((tongFuTotal).compareTo(medicalpayTotal)!=0){
                    throw new ErrorRequestException("事件->收据信息->统筹支付、附加支付之和 需与 汇总项目->医保给付金额节点之和 相等。");
                }
                if((StringUtils.isEmpty(eventBill.getThirdpay())?BigDecimal.ZERO:new BigDecimal(eventBill.getThirdpay().trim())).compareTo(thirdpayTotal)!=0){
                    throw new ErrorRequestException("事件->收据信息->第三方支付 需与 汇总项目->第三方支付节点之和 相等。");
                }

                BigDecimal itemPayTotal=BigDecimal.ZERO;
                if(billOtherItemList!=null && billOtherItemList.size()>0){
                    for (TbBillOtherItem tbBillOtherItem : billOtherItemList) {
                        itemPayTotal=itemPayTotal.add(tbBillOtherItem.getItemPay()==null?BigDecimal.ZERO:tbBillOtherItem.getItemPay());
                    }
                }
                if(billDrugsList!=null && billDrugsList.size()>0){
                    for (TbBillDrugs billDrugs : billDrugsList) {
                        itemPayTotal=itemPayTotal.add(billDrugs.getDrugPay()==null?BigDecimal.ZERO:billDrugs.getDrugPay());
                    }
                }
                if((itemPayTotal.subtract(paymentTotal)).abs().compareTo(new BigDecimal("0.5"))>0){
                    throw new ErrorRequestException("事件->收据信息->药品清单与其他费用清单->自发金额节点之和 需与 汇总项目->账单金额节点之和 误差不能大于0.5。");
                }
                if(new BigDecimal(eventBill.getBillAmt()).compareTo(tongFuTotal)>= 0){
                    throw new ErrorRequestException("事件->收据信息->收据总金额需小于（统筹➕附加）之和。");
                }
                BigDecimal zifeiFeiTotal=(StringUtils.isEmpty(eventBill.getOwnamt())?BigDecimal.ZERO:new BigDecimal(eventBill.getOwnamt().trim())).add(StringUtils.isEmpty(eventBill.getDivamt())?BigDecimal.ZERO:new BigDecimal(eventBill.getDivamt().trim()));
                if(new BigDecimal(eventBill.getBillAmt().trim()).compareTo(zifeiFeiTotal)> 0){
                    throw new ErrorRequestException("事件->收据信息->收据总金额需小于等于（自费➕分类自负）之和。");
                }
            }
        }

    }

    @Override
    @Transactional
    public void delMain(String id) {
        notificationPersonMapper.deleteByMainId(Long.valueOf(id));
        insurancePersonMapper.deleteByMainId(Long.valueOf(id));
        claimThirdInsuranceMapper.deleteByMainId(Long.valueOf(id));
        claimThirdPayMapper.deleteByMainId(Long.valueOf(id));
        claimMaterialMapper.deleteByMainId(Long.valueOf(id));
        claimOtherMapper.deleteByMainId(Long.valueOf(id));
        claimInvestMapper.deleteByMainId(Long.valueOf(id));
        claimClmestimateMapper.deleteByMainId(Long.valueOf(id));
        claimConsultMapper.deleteByMainId(Long.valueOf(id));
        claimBenefitPersonMapper.deleteByMainId(Long.valueOf(id));
        claimClaimPayMapper.deleteByMainId(Long.valueOf(id));
        claimAddMaterialMapper.deleteByMainId(Long.valueOf(id));
        claimAuditInfoMapper.deleteByMainId(Long.valueOf(id));
        claimAuditpolicyMapper.deleteByMainId(Long.valueOf(id));
        claimAboveMapper.deleteByMainId(Long.valueOf(id));
        claimAccInfoMapper.deleteByMainId(Long.valueOf(id));
        List<TbClaimEvent> claimEvents= claimEventMapper.selectByMainId(Long.valueOf(id));
        for (TbClaimEvent claimEvent : claimEvents) {
            claimEventService.delMain(claimEvent.getId().toString());
        }
        claimEventMapper.deleteByMainId(Long.valueOf(id));

        claimInfoMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for(Serializable id:idList) {
            notificationPersonMapper.deleteByMainId(Long.valueOf(id.toString()));
            insurancePersonMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimThirdInsuranceMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimThirdPayMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimMaterialMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimOtherMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimInvestMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimClmestimateMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimConsultMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimBenefitPersonMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimClaimPayMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimAddMaterialMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimAuditInfoMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimAuditpolicyMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimAboveMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimAccInfoMapper.deleteByMainId(Long.valueOf(id.toString()));
            List<TbClaimEvent> claimEvents= claimEventMapper.selectByMainId(Long.valueOf(id.toString()));
            for (TbClaimEvent claimEvent : claimEvents) {
                claimEventService.delMain(claimEvent.getId().toString());
            }
            claimEventMapper.deleteByMainId(Long.valueOf(id.toString()));
            claimInfoMapper.deleteById(id);
        }
    }

    @Override
    public TbClaimInfo getByClaimno(String claimno) {
        return claimInfoMapper.getByClaimno(claimno);
    }

    @Override
    @Transactional
    public void orderFilish(String id) {
        TbClaimInfo claimInfo=claimInfoMapper.selectById(id);
//        if("1".equals(claimInfo.getStatus())){
//            throw new ErrorRequestException("赔案已完结，请刷新页面重试。");
//        }
        if(claimInfo.getImgUrl()==null){
            throw new ErrorRequestException("请上传图片附件");
        }
        if(claimInfo.getPdfUrl()==null){
            throw new ErrorRequestException("请上传pdf附件");
        }
        List<TbNotificationPerson> tbNotificationPersonList= notificationPersonMapper.selectByMainId(Long.valueOf(id));
        List<TbInsurancePerson> insurancePersonList= insurancePersonMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimThirdInsurance> claimThirdInsuranceList= claimThirdInsuranceMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimThirdPay> claimThirdPayList= claimThirdPayMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimMaterial>  claimMaterialList= claimMaterialMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimOther> claimOtherList= claimOtherMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimInvest> claimInvestList= claimInvestMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimClmestimate> claimClmestimateList= claimClmestimateMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimConsult> claimConsultList=  claimConsultMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimBenefitPerson> claimBenefitPersonList= claimBenefitPersonMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimClaimPay> claimClaimPayList= claimClaimPayMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimAddMaterial> claimAddMaterialList= claimAddMaterialMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimAuditInfo> claimAuditInfoList=  claimAuditInfoMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimAuditpolicy> claimAuditpolicyList=  claimAuditpolicyMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimAbove>  claimAboves=  claimAboveMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimAccInfo> claimAccInfoList= claimAccInfoMapper.selectByMainId(Long.valueOf(id));
        List<TbClaimEvent> claimEventList= claimEventMapper.selectByMainId(Long.valueOf(id));


        List<ClaimEventPage> claimEventPages=new ArrayList<>();

        List<ClaimEventVo>  claimEventVos=new ArrayList<>(); //MyBeanUtils.convertList(claimEventList, ClaimEventVo.class);
        for (TbClaimEvent claimEvent : claimEventList) {
            ClaimEventPage claimEventPage=generator.convert(claimEvent,ClaimEventPage.class);


            ClaimEventVo claimEventVo= MyBeanUtils.convert(claimEvent, ClaimEventVo.class);
            checkNull(claimEventVo);
            List<TbEventBill> eventBillList=  eventBillService.selectByMainId(String.valueOf(claimEvent.getId()));
            List<EventBillVo>  eventBillVos= new ArrayList<>();// MyBeanUtils.convertList(eventBillList, EventBillVo.class);
            List<EventBillPage> eventBillPages=new ArrayList<>();
            for (TbEventBill tbEventBill : eventBillList) {
                EventBillPage eventBillPage=generator.convert(tbEventBill,EventBillPage.class);
                EventBillVo eventBillVo= MyBeanUtils.convert(tbEventBill, EventBillVo.class);
                checkNull(eventBillVo);
                List<TbBillItem> billItemList= tbBillItemMapper.selectByMainId(tbEventBill.getId());
                List<TbBillDrugs> billDrugsList= tbBillDrugsMapper.selectByMainId(tbEventBill.getId());
                List<TbBillOtherItem> billOtherItemList= tbBillOtherItemMapper.selectByMainId(tbEventBill.getId());

                eventBillPage.setBillOtherItemList(billOtherItemList);
                eventBillPage.setBillItemList(billItemList);
                eventBillPage.setBillDrugsList(billDrugsList);

                List<BillItemVo>  billItemVos=new ArrayList<>();// MyBeanUtils.convertList(billItemList, BillItemVo.class);
                for (TbBillItem tbBillItem : billItemList) {
                    BillItemVo billItemVo=MyBeanUtils.convert(tbBillItem, BillItemVo.class);
                    checkNull(billItemVo);
                    billItemVo.setPayment(tbBillItem.getPayment()==null?"":tbBillItem.getPayment().toString());
                    billItemVo.setSelfpay(tbBillItem.getSelfpay()==null?"":tbBillItem.getSelfpay().toString());
                    billItemVo.setClassification(tbBillItem.getClassification()==null?"":tbBillItem.getClassification().toString());
                    billItemVo.setMedicalpay(tbBillItem.getMedicalpay()==null?"":tbBillItem.getMedicalpay().toString());
                    billItemVo.setThirdpay(tbBillItem.getThirdpay()==null?"":tbBillItem.getThirdpay().toString());
                    billItemVo.setPayback(tbBillItem.getPayback()==null?"":tbBillItem.getPayback().toString());
                    billItemVos.add(billItemVo);
                }
                List<BillDrugsVo>  billDrugsVos=new ArrayList<>();// MyBeanUtils.convertList(billDrugsList, BillDrugsVo.class);
                for (TbBillDrugs tbBillDrugs : billDrugsList) {
                    BillDrugsVo billDrugsVo=MyBeanUtils.convert(tbBillDrugs, BillDrugsVo.class);
                    checkNull(billDrugsVo);
                    billDrugsVo.setDrugUnitAmt(tbBillDrugs.getDrugUnitAmt()==null?"":tbBillDrugs.getDrugUnitAmt().toString());
                    billDrugsVo.setDrugTotal(tbBillDrugs.getDrugTotal()==null?"":tbBillDrugs.getDrugTotal().toString());
                    billDrugsVo.setDrugPay(tbBillDrugs.getDrugPay()==null?"":tbBillDrugs.getDrugPay().toString());
                    billDrugsVo.setMedicalType(tbBillDrugs.getMedicalType()==null?"":tbBillDrugs.getMedicalType().toString());
                    billDrugsVo.setSelfpayRate(tbBillDrugs.getSelfpayRate()==null?"":tbBillDrugs.getSelfpayRate().toString());
                    billDrugsVo.setSelfpayAmt(tbBillDrugs.getSelfpayAmt()==null?"":tbBillDrugs.getSelfpayAmt().toString());
                    billDrugsVos.add(billDrugsVo);
                }
                List<BillOtherItemVo>  billOtherItemVos=new ArrayList<>();// MyBeanUtils.convertList(billOtherItemList, BillOtherItemVo.class);
                for (TbBillOtherItem tbBillOtherItem : billOtherItemList) {
                    BillOtherItemVo billOtherItemVo=MyBeanUtils.convert(tbBillOtherItem, BillOtherItemVo.class);
                    checkNull(billOtherItemVo);
                    billOtherItemVo.setItemPay(tbBillOtherItem.getItemPay()==null?"":tbBillOtherItem.getItemPay().toString());
                    billOtherItemVo.setSelfPayRate(tbBillOtherItem.getSelfPayRate()==null?"":tbBillOtherItem.getSelfPayRate().toString());
                    billOtherItemVo.setSelfPayAmt(tbBillOtherItem.getSelfPayAmt()==null?"":tbBillOtherItem.getSelfPayAmt().toString());
                    billOtherItemVo.setItemUnitPay(tbBillOtherItem.getItemUnitPay()==null?"":tbBillOtherItem.getItemUnitPay().toString());
                    billOtherItemVos.add(billOtherItemVo);
                }
                eventBillVo.setBillItemVos(billItemVos);
                eventBillVo.setBillDrugsVos(billDrugsVos);
                eventBillVo.setBillOtherItemVos(billOtherItemVos);
                eventBillVos.add(eventBillVo);
                eventBillPages.add(eventBillPage);
            }
            claimEventVo.setEventBillVos(eventBillVos);
            claimEventVos.add(claimEventVo);
            claimEventPage.setEventBillList(eventBillPages);
            claimEventPages.add(claimEventPage);
        }
        //校验
        checkedAmount( claimInfo, tbNotificationPersonList,  insurancePersonList,  claimClmestimateList,  claimBenefitPersonList, claimClaimPayList,   claimAuditInfoList,  claimAccInfoList,  claimEventPages);

        ClaimInfoVo claimInfoVo = MyBeanUtils.convert(claimInfo, ClaimInfoVo.class);
        checkNull(claimInfoVo);
        List<NotificationPersonVo>  notificationPersonVos= MyBeanUtils.convertList(tbNotificationPersonList, NotificationPersonVo.class);
        NotificationPersonVo notificationPersonVo=new NotificationPersonVo();
        if (notificationPersonVos!=null && notificationPersonVos.size()>0){
            notificationPersonVo=notificationPersonVos.get(0);
            checkNull(notificationPersonVo);
            notificationPersonVo.setIdBegdate(tbNotificationPersonList.get(0).getIdBegdate()==null?"": dateFormat.format(tbNotificationPersonList.get(0).getIdBegdate()));
            notificationPersonVo.setIdEnddate(tbNotificationPersonList.get(0).getIdEnddate()==null?"": dateFormat.format(tbNotificationPersonList.get(0).getIdEnddate()));
            notificationPersonVo.setNoticeDate(tbNotificationPersonList.get(0).getNoticeDate()==null?"": dateFormat.format(tbNotificationPersonList.get(0).getNoticeDate()));
        }
        List<InsurancePersonVo>  insurancePersonVos= MyBeanUtils.convertList(insurancePersonList, InsurancePersonVo.class);
        InsurancePersonVo insurancePersonVo=new InsurancePersonVo();
        if (insurancePersonVos!=null && insurancePersonVos.size()>0){
            insurancePersonVo=insurancePersonVos.get(0);
            checkNull(insurancePersonVo);
            insurancePersonVo.setBirthdate(insurancePersonList.get(0).getBirthdate()==null?"": dateFormat.format(insurancePersonList.get(0).getBirthdate()));
            insurancePersonVo.setIdBegdate(insurancePersonList.get(0).getIdBegdate()==null?"": dateFormat.format(insurancePersonList.get(0).getIdBegdate()));
            insurancePersonVo.setIdEnddate(insurancePersonList.get(0).getIdEnddate()==null?"": dateFormat.format(insurancePersonList.get(0).getIdEnddate()));
        }

        List<ClaimThirdInsuranceVo>  claimThirdInsuranceVos= MyBeanUtils.convertList(claimThirdInsuranceList, ClaimThirdInsuranceVo.class);
        if (claimThirdInsuranceVos!=null && claimThirdInsuranceVos.size()>0){
            for (ClaimThirdInsuranceVo claimThirdInsuranceVo : claimThirdInsuranceVos) {
                checkNull(claimThirdInsuranceVo);
            }
        }
        List<ClaimThirdPayVo>  claimThirdPayVos=new ArrayList<>();// MyBeanUtils.convertList(claimThirdPayList, ClaimThirdPayVo.class);
        for (TbClaimThirdPay tbClaimThirdPay : claimThirdPayList) {
            ClaimThirdPayVo claimThirdPayVo= MyBeanUtils.convert(tbClaimThirdPay, ClaimThirdPayVo.class);
            checkNull(claimThirdPayVo);
            claimThirdPayVo.setPayAmount(tbClaimThirdPay.getPayAmount().toString());
            claimThirdPayVos.add(claimThirdPayVo);
        }

        List<ClaimMaterialVo>  claimMaterialVos= MyBeanUtils.convertList(claimMaterialList, ClaimMaterialVo.class);
        if(claimMaterialVos!=null && claimMaterialVos.size()>0){
            for (ClaimMaterialVo claimMaterialVo : claimMaterialVos) {
                checkNull(claimMaterialVo);
            }
        }
        List<ClaimOtherVo>  claimOtherVos= new ArrayList<>();//MyBeanUtils.convertList(claimOtherList, ClaimOtherVo.class);
        for (TbClaimOther tbClaimOther : claimOtherList) {
            ClaimOtherVo claimOtherVo= MyBeanUtils.convert(tbClaimOther, ClaimOtherVo.class);
            checkNull(claimOtherVo);
            claimOtherVo.setApplydate(tbClaimOther.getApplydate()==null?"":dateFormat.format(tbClaimOther.getApplydate()));
            claimOtherVo.setBackdate(tbClaimOther.getBackdate()==null?"":dateFormat.format(tbClaimOther.getBackdate()));
            claimOtherVos.add(claimOtherVo);
        }
        List<ClaimInvestVo>  claimInvestVos=new ArrayList<>();// MyBeanUtils.convertList(claimInvestList, ClaimInvestVo.class);
        for (TbClaimInvest tbClaimInvest : claimInvestList) {
            ClaimInvestVo claimInvestVo= MyBeanUtils.convert(tbClaimInvest, ClaimInvestVo.class);
            checkNull(claimInvestVo);
            claimInvestVo.setApplydate(tbClaimInvest.getApplydate()==null?"":dateFormat.format(tbClaimInvest.getApplydate()));
            claimInvestVo.setBackdate(tbClaimInvest.getBackdate()==null?"":dateFormat.format(tbClaimInvest.getBackdate()));
            claimInvestVos.add(claimInvestVo);
        }
        List<ClaimClmestimateVo>  claimClmestimateVos=new ArrayList<>();// MyBeanUtils.convertList(claimClmestimateList, ClaimClmestimateVo.class);
        for (TbClaimClmestimate claimClmestimate : claimClmestimateList) {
            ClaimClmestimateVo claimClmestimateVo= MyBeanUtils.convert(claimClmestimate, ClaimClmestimateVo.class);
            checkNull(claimClmestimateVo);
            claimClmestimateVo.setGsje(claimClmestimate.getGsje().stripTrailingZeros().toPlainString());
            claimClmestimateVos.add(claimClmestimateVo);
        }


        List<ClaimConsultVo>  claimConsultVos=new ArrayList<>();// MyBeanUtils.convertList(claimConsultList, ClaimConsultVo.class);
        for (TbClaimConsult tbClaimConsult : claimConsultList) {
            ClaimConsultVo claimConsultVo= MyBeanUtils.convert(tbClaimConsult, ClaimConsultVo.class);
            checkNull(claimConsultVo);
            claimConsultVo.setApplydate(tbClaimConsult.getApplydate()==null?"":dateFormat.format(tbClaimConsult.getApplydate()));
            claimConsultVo.setBackdate(tbClaimConsult.getBackdate()==null?"":dateFormat.format(tbClaimConsult.getBackdate()));
            claimConsultVos.add(claimConsultVo);
        }
        List<ClaimBenefitPersonVo>  claimBenefitPersonVos= MyBeanUtils.convertList(claimBenefitPersonList, ClaimBenefitPersonVo.class);
        if(claimBenefitPersonVos!=null && claimBenefitPersonVos.size()>0){
            for (ClaimBenefitPersonVo claimBenefitPersonVo : claimBenefitPersonVos) {
                checkNull(claimBenefitPersonVo);
            }
        }

        List<ClaimClaimPayVo>  claimClaimPayVos= new ArrayList<>(); // MyBeanUtils.convertList(claimClaimPayList, ClaimClaimPayVo.class);
        for (TbClaimClaimPay claimClaimPay : claimClaimPayList) {
            ClaimClaimPayVo claimClaimPayVo= MyBeanUtils.convert(claimClaimPay, ClaimClaimPayVo.class);
            checkNull(claimClaimPayVo);
            claimClaimPayVo.setClaimpay(claimClaimPay.getClaimpay()==null?"":claimClaimPay.getClaimpay().stripTrailingZeros().toPlainString());
            claimClaimPayVo.setAdvancepayment(claimClaimPay.getAdvancepayment()==null?"":claimClaimPay.getAdvancepayment().stripTrailingZeros().toPlainString());
            claimClaimPayVo.setRemaindeduction(claimClaimPay.getRemaindeduction()==null?"":claimClaimPay.getRemaindeduction().stripTrailingZeros().toPlainString());
            claimClaimPayVos.add(claimClaimPayVo);
        }

        List<ClaimAddMaterialVo>  claimAddMaterialVos = new ArrayList<>(); //MyBeanUtils.convertList(claimAddMaterialList, ClaimAddMaterialVo.class);
        for (TbClaimAddMaterial tbClaimAddMaterial : claimAddMaterialList) {
            ClaimAddMaterialVo claimAddMaterialVo= MyBeanUtils.convert(tbClaimAddMaterial, ClaimAddMaterialVo.class);
            checkNull(claimAddMaterialVo);
            claimAddMaterialVo.setApplydate(tbClaimAddMaterial.getApplydate()==null?"":dateFormat.format(tbClaimAddMaterial.getApplydate()));
            claimAddMaterialVo.setBackdate(tbClaimAddMaterial.getBackdate()==null?"":dateFormat.format(tbClaimAddMaterial.getBackdate()));
            claimAddMaterialVos.add(claimAddMaterialVo);
        }
        List<ClaimAuditInfoVo>  claimAuditInfoVos= MyBeanUtils.convertList(claimAuditInfoList, ClaimAuditInfoVo.class);
        if(claimAuditInfoVos!=null && claimAuditInfoVos.size()>0){
            for (ClaimAuditInfoVo claimAuditInfoVo : claimAuditInfoVos) {
                checkNull(claimAuditInfoVo);
            }
        }
        List<ClaimAuditpolicyVo>  claimAuditpolicyVos= MyBeanUtils.convertList(claimAuditpolicyList, ClaimAuditpolicyVo.class);
        if(claimAuditpolicyVos!=null && claimAuditpolicyVos.size()>0){
            for (ClaimAuditpolicyVo claimAuditpolicyVo : claimAuditpolicyVos) {
                checkNull(claimAuditpolicyVo);
            }
        }
        List<ClaimAboveVo>  claimAboveVos=new ArrayList<>(); //MyBeanUtils.convertList(claimAboves, ClaimAboveVo.class);
        for (TbClaimAbove claimAbove : claimAboves) {
            ClaimAboveVo claimAboveVo= MyBeanUtils.convert(claimAbove, ClaimAboveVo.class);
            checkNull(claimAboveVo);
            claimAboveVo.setApplydate(claimAbove.getApplydate()==null?"":dateFormat.format(claimAbove.getApplydate()));
            claimAboveVo.setBackdate(claimAbove.getBackdate()==null?"":dateFormat.format(claimAbove.getBackdate()));
            claimAboveVos.add(claimAboveVo);
        }

        List<ClaimAccInfoVo>  claimAccInfoVos=new ArrayList<>(); //MyBeanUtils.convertList(claimAccInfoList, ClaimAccInfoVo.class);
        for (TbClaimAccInfo tbClaimAccInfo : claimAccInfoList) {
            ClaimAccInfoVo claimAccInfoVo= MyBeanUtils.convert(tbClaimAccInfo, ClaimAccInfoVo.class);
            checkNull(claimAccInfoVo);
            claimAccInfoVo.setAccDate(tbClaimAccInfo.getAccDate()==null?"":dateFormat.format(tbClaimAccInfo.getAccDate()));
            claimAccInfoVo.setFirstDate(tbClaimAccInfo.getFirstDate()==null?"":dateFormat.format(tbClaimAccInfo.getFirstDate()));
            claimAccInfoVos.add(claimAccInfoVo);
        }


        claimInfoVo.setVisitDate(claimInfo.getVisitDate()==null?"":dateFormat.format(claimInfo.getVisitDate()));
        claimInfoVo.setAdvanceClosingTime(claimInfo.getAdvanceClosingTime()==null?"":sdf.format(claimInfo.getAdvanceClosingTime()));
        claimInfoVo.setDataCollectionDay(claimInfo.getDataCollectionDay()==null?"":sdf.format(claimInfo.getDataCollectionDay()));
        claimInfoVo.setReauditdate(claimInfo.getReauditdate()==null?"":sdf.format(claimInfo.getReauditdate()));
        claimInfoVo.setHangupsign(new ArrayList<>());
        if(claimAccInfoVos.size()>0){
            ClaimAccInfoVo accInfoVo=claimAccInfoVos.get(0);
            checkNull(accInfoVo);
            accInfoVo.setClaimacc(Arrays.asList(claimAccInfoList.get(0).getClaimacc().split(",")));
            claimInfoVo.setClaimAccInfoVo(accInfoVo);
        }
        claimInfoVo.setNotificationPersonVo(notificationPersonVo);
        claimInfoVo.setInsurancePersonVo(insurancePersonVos.size()>0?insurancePersonVos.get(0):new InsurancePersonVo());
        claimInfoVo.setClaimThirdInsuranceVos(claimThirdInsuranceVos);
        claimInfoVo.setClaimThirdPayVos(claimThirdPayVos);
        claimInfoVo.setClaimMaterialVos(claimMaterialVos);
        claimInfoVo.setClaimOtherVos(claimOtherVos);
        claimInfoVo.setClaimInvestVos(claimInvestVos);
        claimInfoVo.setClaimClmestimateVos(claimClmestimateVos);
        claimInfoVo.setClaimConsultVos(claimConsultVos);
        claimInfoVo.setClaimBenefitPersonVos(claimBenefitPersonVos);
        claimInfoVo.setClaimClaimPayVos(claimClaimPayVos);
        claimInfoVo.setClaimAddMaterialVos(claimAddMaterialVos);
        claimInfoVo.setClaimAuditInfoVos(claimAuditInfoVos);
        claimInfoVo.setClaimAuditpolicyVos(claimAuditpolicyVos);
        claimInfoVo.setClaimAboveVos(claimAboveVos);
        claimInfoVo.setClaimEventVos(claimEventVos);

        //xml 字符串
        String xmlObj= FileUtils.ObjToXmlString(claimInfoVo);
        List<String> filePaths=new ArrayList<>();
        //本地 创建xml 文件
        String xmlPath=path+File.separator+claimInfoVo.getClaimno()+".xml";
        filePaths.add(xmlPath);
        Boolean b= FileUtils.createFile(xmlObj,xmlPath);
        if(!b){
            throw new ErrorRequestException("xml文件生成失败");
        }

        //img 包 zip
        List<String> imgPath=claimInfo.getImgUrl()==null?new ArrayList<>():Arrays.asList(claimInfo.getImgUrl().split(",")) ;
        List<Map<String,Object>> maps=new ArrayList<>();
        for (int i = 0; i < imgPath.size(); i++) {
            String imgDwonPath=path+File.separator+i+imgPath.get(i).substring(imgPath.get(i).lastIndexOf("."));
            b= FileUtils.fileUrlDown(imgPath.get(i),imgDwonPath);
            if(!b){
                throw new ErrorRequestException("img文件下载失败！");
            }
            File file=new File(imgDwonPath);
            if(!file.exists()){
                throw new ErrorRequestException("img文件下载失败！");
            }
            Map<String,Object> map=new HashMap<>();
            map.put("imgUrl",imgDwonPath);
            map.put("renameUrl",claimInfoVo.getClaimno()+String.format("%03d", Integer.valueOf(i+1))+"E01"+imgPath.get(i).substring(imgPath.get(i).lastIndexOf(".")));
            maps.add(map);
        }
        String imgZipPath=path+ File.separator+claimInfoVo.getClaimno()+".zip";
        filePaths.add(imgZipPath);

        ZipCompressor zcImg = new ZipCompressor(imgZipPath);
        zcImg.compress(maps);

        for (Map<String, Object> map : maps) {
            File  fileimgUrl=new File(map.get("imgUrl").toString());
            fileimgUrl.delete();
        }
        //pdf 包
        maps.clear();

        String pdfDwonPath=path+File.separator+claimInfo.getPdfUrl().substring(claimInfo.getPdfUrl().lastIndexOf("."));
        b= FileUtils.fileUrlDown(claimInfo.getPdfUrl(),pdfDwonPath);
        if(!b){
            throw new ErrorRequestException("pdf文件下载失败！");
        }
        File pdfFile=new File(pdfDwonPath);
        if(!pdfFile.exists()){
            throw new ErrorRequestException("pdf文件下载失败！");
        }

        Map<String,Object> map=new HashMap<>();
        map.put("imgUrl",pdfDwonPath);
        map.put("renameUrl",claimInfoVo.getClaimno()+".pdf");
        maps.add(map);

        String pdfZipPath=path+ File.separator+claimInfoVo.getClaimno()+"_pdf.zip";
        filePaths.add(pdfZipPath);

        ZipCompressor zcPdf = new ZipCompressor(pdfZipPath);
        zcPdf.compress(maps);
        pdfFile.delete();

        b = SFTPUtil.ftpConnection(ftpHost, Integer.valueOf(ftpPort), ftpUserName, ftpPassword);
        if(b){
            try {
                String lastResultUrl="";
                for (String filePath : filePaths) {
                    File file= new File(filePath);
                    if (!file.exists()){
                        throw new ErrorRequestException("文件："+filePath+"不存在。");
                    }
                    FileInputStream in = new FileInputStream(file);
                    if(file.getName().contains("xml")){
                        lastResultUrl=lastResultXmlUrl;
                    }else if(file.getName().contains("pdf")){
                        lastResultUrl=lastResultPdfUrl;
                    }else{
                        lastResultUrl=lastResultImagesUrl;
                    }
                    SFTPUtil.storeFile( lastResultUrl, file.getName(), in);
                    System.gc();
                    file.delete();
                }
                SFTPUtil.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            claimInfo.setStatus("1");
            claimInfoMapper.updateById(claimInfo);
        }

    }

    @Override
    public void saveTbOrder(TbOrderProjectParam orderProjectParam) {
        YxStoreOrder order=  storeOrderService.getById(orderProjectParam.getId());
        if(order.getUploadYiyaobaoFlag()==0){
            order= storeOrderService.addTbOrderProject(orderProjectParam);//addOrderProject(orderProjectParam);
            if(ObjectUtil.isNull(order)){
                throw new ErrorRequestException("订单生成失败");
            }
        }
    }

    public static Object checkNull(Object vo) {
        Field[] field = vo.getClass().getDeclaredFields();
        for (int i = 0; i < field.length; i++) {
            field[i].setAccessible(true);
            String name = field[i].getName();
            String type = field[i].getGenericType().toString();
            name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1)
                    .toUpperCase());
            if (type.equals("class java.lang.String")) {
                // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = null;
                try {
                    m = vo.getClass().getMethod("get" + name);
                    // 调用getter方法获取属性值
                    String value = (String) m.invoke(vo);
                    if (value == null) {
                       field[i].set(vo, field[i].getType().getConstructor(field[i].getType()).newInstance(""));
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return vo;
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("5.00").stripTrailingZeros().toPlainString());
    }
}
