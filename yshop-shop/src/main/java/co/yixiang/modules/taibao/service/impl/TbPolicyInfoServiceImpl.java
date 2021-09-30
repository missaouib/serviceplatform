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
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.taibao.domain.TbClaimInfo;
import co.yixiang.modules.taibao.domain.TbPolicyInfo;
import co.yixiang.modules.taibao.service.TbClaimInfoService;
import co.yixiang.modules.taibao.service.TbPolicyInfoService;
import co.yixiang.modules.taibao.service.dto.PolicyInfoDTO;
import co.yixiang.modules.taibao.service.dto.TbPolicyInfoDto;
import co.yixiang.modules.taibao.service.dto.TbPolicyInfoQueryCriteria;
import co.yixiang.modules.taibao.service.mapper.TbPolicyInfoMapper;
import co.yixiang.modules.taibao.util.FileUtils;
import co.yixiang.modules.taibao.util.SFTPUtil;
import co.yixiang.utils.FileUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Service
@Slf4j
//@CacheConfig(cacheNames = "tbPolicyInfo")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TbPolicyInfoServiceImpl extends BaseServiceImpl<TbPolicyInfoMapper, TbPolicyInfo> implements TbPolicyInfoService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private TbPolicyInfoMapper policyInfoMapper;

    @Autowired
    private TbClaimInfoService tbClaimInfoService;

    @Autowired
    private YxStoreOrderService storeOrderService;


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

    @Value("${fpt.advancePaymentResultUrl}")
    private String advancePaymentResultUrl;

    @Value("${fpt.waitReadUrl}")
    private String waitReadUrl;

    @Value("${fpt.readUrl}")
    private String readUrl;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(TbPolicyInfoQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<TbPolicyInfo> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), TbPolicyInfoDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<TbPolicyInfo> queryAll(TbPolicyInfoQueryCriteria criteria){
        List<TbPolicyInfo> tbPolicyInfos=  baseMapper.selectList(QueryHelpPlus.getPredicate(TbPolicyInfo.class, criteria));
        for (TbPolicyInfo tbPolicyInfo : tbPolicyInfos) {
            if(tbPolicyInfo.getIsAdopt()==null){
                tbPolicyInfo.setPolicyStatus("0");
            }else{
                TbClaimInfo claimInfo= tbClaimInfoService.getByClaimno(tbPolicyInfo.getRequestCaimReportNo());
                if(claimInfo==null){
                    tbPolicyInfo.setPolicyStatus("1");
                }else{
                    tbPolicyInfo.setStatus(claimInfo.getStatus());
                    YxStoreOrder order = storeOrderService.getById(claimInfo.getOrderId());
                    if(order.getUploadYiyaobaoFlag()==0){
                        tbPolicyInfo.setPolicyStatus("2");
                    }else{
                        tbPolicyInfo.setPolicyStatus("3");
                    }
                    tbPolicyInfo.setOrderNo(order.getOrderId());
                    tbPolicyInfo.setOrderStatusStr(OrderStatusEnum.toType(order.getStatus()).getDesc());
                    tbPolicyInfo.setUploadYiyaobaoFlag(order.getUploadYiyaobaoFlag()==0?"否":"是");
                }
            }
        }
        return tbPolicyInfos;
    }


    @Override
    public void download(List<TbPolicyInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TbPolicyInfoDto tbPolicyInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("团体保单号", tbPolicyInfo.getGroupPolicyNo());
            map.put("保单号", tbPolicyInfo.getPolicyNo());
            map.put("（垫付服务请求号+理赔报案号）", tbPolicyInfo.getRequestCaimReportNo());
            map.put("姓名", tbPolicyInfo.getName());
            map.put("证件号", tbPolicyInfo.getIdNo());
            map.put("证件类型", tbPolicyInfo.getIdType());
            map.put("性别", tbPolicyInfo.getSex());
            map.put("被保人出生日期", tbPolicyInfo.getInsuredBirthday());
            map.put("产品名称", tbPolicyInfo.getProductName());
            map.put("产品代码", tbPolicyInfo.getProductCode());
            map.put("责任名称", tbPolicyInfo.getResponsibilityName());
            map.put("责任代码", tbPolicyInfo.getResponsibilityCode());
            map.put("责任余额", tbPolicyInfo.getResponsibilityTotal());
            map.put("免赔余额", tbPolicyInfo.getDeductibleTotal());
            map.put("保单特约", tbPolicyInfo.getPolicySpecialAppoint());
            map.put("个人特约", tbPolicyInfo.getPersonSpecialAppoint());
            map.put("层级特约", tbPolicyInfo.getHierarchySpecialAppoint());
            map.put("险种特约", tbPolicyInfo.getInsuranceSpecialAppoint());
            map.put("既往症特约", tbPolicyInfo.getPastDiseaseSpecialAppoint());
            map.put("联系人姓名", tbPolicyInfo.getContactsName());
            map.put("联系人电话", tbPolicyInfo.getContactsPhone());
            map.put("生效日期", tbPolicyInfo.getEffectDate());
            map.put("到期日期", tbPolicyInfo.getExpireDate());
            map.put("客服备注", tbPolicyInfo.getCustomerServiceRemarks());
            map.put("报案日期", tbPolicyInfo.getReportDate());
            map.put("保单所属机构", tbPolicyInfo.getPolicyInstitutions());
            map.put("承保中支公司", tbPolicyInfo.getUnderwritChinaBranch());
            map.put("承保四级机构", tbPolicyInfo.getUnderwritLevelFour());
            map.put("是否医保投保", tbPolicyInfo.getIsMedicalInsurance());
            map.put("保单类型(新保/续保/转保)", tbPolicyInfo.getPolicyType());
            map.put("服务起始日", tbPolicyInfo.getServiceStartDate());
            map.put("承保公司", tbPolicyInfo.getUnderwritingCompany());
            map.put("备用字段1", tbPolicyInfo.getSpareFieldOne());
            map.put("备用字段2", tbPolicyInfo.getSpareFieldTwo());
            map.put("备用字段3", tbPolicyInfo.getSpareFieldThree());
            map.put("创建人", tbPolicyInfo.getCreateBy());
            map.put("创建时间", tbPolicyInfo.getCreateTime());
            map.put("修改人", tbPolicyInfo.getUpdateBy());
            map.put("修改时间", tbPolicyInfo.getUpdateTime());
            map.put("0表示未删除,1表示删除", tbPolicyInfo.getDelFlag());
            map.put("拒绝原因", tbPolicyInfo.getReason());
            map.put("赔案垫付意见类型(01 同意 02不同意 03已垫付 04终止垫付,当为02/04时，必须给出拒赔原因)", tbPolicyInfo.getIsAdopt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void advancePaymentResult(PolicyInfoDTO policyInfoDTO) {
        if(policyInfoDTO.getId()==null || StringUtils.isEmpty(policyInfoDTO.getIsAdopt())){
            throw new BadRequestException("参数错误");
        }
        TbPolicyInfo policyInfo=   policyInfoMapper.selectById(policyInfoDTO.getId());
        if(policyInfo==null){
            throw new BadRequestException("赔案单不存在，请刷新页面重试！");
        }
        log.info("policyInfo：{}", net.sf.json.JSONObject.fromObject(policyInfo));

        if(policyInfo.getIsAdopt()!=null){
            throw new BadRequestException("赔案单已审核，请勿重复审核。");
        }
        if((policyInfoDTO.getIsAdopt().equals("02") || policyInfoDTO.getIsAdopt().equals("04") ) && StringUtils.isEmpty(policyInfoDTO.getReason()) ){
            throw new BadRequestException("不同意或终止垫付时，拒绝原因不能为空！");
        }
        policyInfo.setIsAdopt(policyInfoDTO.getIsAdopt());
        policyInfo.setReason(policyInfoDTO.getReason());

        StringBuilder builder=new StringBuilder();
        builder.append(policyInfo.getRequestCaimReportNo());
        builder.append("|");
        builder.append(policyInfo.getRequestCaimReportNo());
        builder.append("|");
        builder.append(policyInfoDTO.getIsAdopt());
        builder.append("|");
        builder.append(StringUtils.isEmpty(policyInfoDTO.getReason())?"":policyInfoDTO.getReason());

        log.info("创建文件start");
        boolean b= FileUtils.createFile(builder.toString(),path+File.separator+policyInfo.getRequestCaimReportNo()+".txt");
        log.info("创建文件end：{}",b);
        if(b){
            log.info("连接ftp start");
            b = SFTPUtil.ftpConnection(ftpHost, Integer.valueOf(ftpPort), ftpUserName, ftpPassword);
            log.info("连接ftp end：{}",b);
            if(b){
                try {
                    File file= new File(path+File.separator+policyInfo.getRequestCaimReportNo()+".txt");
                    FileInputStream in = new FileInputStream(file);
                    try {
                        b = SFTPUtil.storeFile( advancePaymentResultUrl, policyInfo.getRequestCaimReportNo()+".txt", in);
                        file.delete();
                        if(b){
                            policyInfoMapper.updateById(policyInfo);
                        }else{
                            log.error("上传FTP失败！"+b);
                            throw new BadRequestException("上传FTP失败！垫付结果通知失败！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("上传FTP失败！");
                        throw new BadRequestException("上传FTP失败！垫付结果通知失败！");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public BigDecimal findDeductibleTotalByClaimno(String claimno) {
        return policyInfoMapper.findDeductibleTotalByClaimno(claimno);
    }

    @Override
    public TbPolicyInfo getByClaimno(String claimno) {
        return policyInfoMapper.getByClaimno(claimno);
    }


    /**
     * 保单信息定时ftp同步
     * @return
     */
    public void synchPolicyJob() {
        long c1 = System.currentTimeMillis();
        log.info("***************保单信息定时ftp同步start******************");
        try {
            ftpReadFile();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("同步保单信息失败：{}", e.getMessage());
        }
        log.info("***************保单信息定时ftp同步end******************" + (System.currentTimeMillis() - c1));
    }


    public void ftpReadFile() throws Exception {
        boolean b = SFTPUtil.ftpConnection(ftpHost, Integer.valueOf(ftpPort), ftpUserName, ftpPassword);
        if (b) {
            List<String> list = new ArrayList<>();
            list = SFTPUtil.List(list, waitReadUrl);
            for (String s : list) {
                //下载至本地临时文件
                boolean flag = SFTPUtil.downFile(s.substring(s.lastIndexOf("/") + 1), s.substring(0,s.lastIndexOf("/") + 1), path,s.substring(s.lastIndexOf("/") + 1));

                File file = new File(path + File.separator + s.substring(s.lastIndexOf("/") + 1));
                if (file.exists()) {
                    //读取且传入库中
                    //////
                    List<String> strings = FileUtils.getFileContext(path + File.separator + s.substring(s.lastIndexOf("/") + 1));
                    List<TbPolicyInfo> policyInfos = new ArrayList<>();
                    for (String str : strings) {
                        String[] split = str.split("\\|", -1);
                        TbPolicyInfo policyInfo = new TbPolicyInfo(split);
                        policyInfo.setCreateBy("admin");
                        policyInfo.setDelFlag(Boolean.TRUE);
                        TbPolicyInfo tbPolicyInfo =  policyInfoMapper.getByClaimno(policyInfo.getRequestCaimReportNo());
                        if(tbPolicyInfo!=null){
                            continue;
                        }
                        policyInfos.add(policyInfo);
                    }
                    saveBatch(policyInfos);

                    SFTPUtil.delete(waitReadUrl,s.substring(s.lastIndexOf("/") + 1));
                    //存入库中后删除本地临时文件
                    System.gc();
                    file.delete();
                }
            }
        }
        SFTPUtil.close();
    }

    public static void main(String[] args) {
        System.out.println("qwdqw/12312312/12312.txt".substring(0,"qwdqw/12312312/12312.txt".lastIndexOf("/") + 1));
    }
}

