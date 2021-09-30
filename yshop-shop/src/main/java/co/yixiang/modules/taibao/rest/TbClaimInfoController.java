/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderCartInfo;
import co.yixiang.modules.shop.domain.YxUserAddress;
import co.yixiang.modules.shop.service.*;
import co.yixiang.modules.shop.service.dto.YxStoreCartQueryVo;
import co.yixiang.modules.shop.service.dto.YxStoreProductQueryVo;
import co.yixiang.modules.shop.service.dto.YxSystemStoreQueryVo;
import co.yixiang.modules.taibao.domain.*;
import co.yixiang.modules.taibao.service.*;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoDto;
import co.yixiang.modules.taibao.service.dto.TbClaimInfoQueryCriteria;
import co.yixiang.modules.taibao.service.vo.ClaimEventPage;
import co.yixiang.modules.taibao.service.vo.ClaimInfoPage;
import co.yixiang.modules.taibao.service.vo.EventBillPage;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@AllArgsConstructor
@Api(tags = "太保安联订单—赔案信息表管理")
@RestController
@RequestMapping("/api/tbClaimInfo")
public class TbClaimInfoController {

    private final TbClaimInfoService tbClaimInfoService;
    private final IGenerator generator;
    @Autowired
    private TbNotificationPersonService notificationPersonService;
    @Autowired
    private TbInsurancePersonService insurancePersonService;
    @Autowired
    private TbClaimThirdInsuranceService claimThirdInsuranceService;
    @Autowired
    private TbClaimThirdPayService claimThirdPayService;
    @Autowired
    private TbClaimMaterialService claimMaterialService;
    @Autowired
    private TbClaimOtherService claimOtherService;
    @Autowired
    private TbClaimInvestService claimInvestService;
    @Autowired
    private TbClaimClmestimateService claimClmestimateService;
    @Autowired
    private TbClaimConsultService claimConsultService;
    @Autowired
    private TbClaimBenefitPersonService claimBenefitPersonService;
    @Autowired
    private TbClaimClaimPayService claimClaimPayService;
    @Autowired
    private TbClaimAddMaterialService claimAddMaterialService;
    @Autowired
    private TbClaimAuditInfoService claimAuditInfoService;
    @Autowired
    private TbClaimAuditpolicyService claimAuditpolicyService;
    @Autowired
    private TbClaimAboveService claimAboveService;
    @Autowired
    private TbClaimAccInfoService claimAccInfoService;
    @Autowired
    private TbClaimEventService claimEventService;
    @Autowired
    private TbEventBillService eventBillService;
    @Autowired
    private TbBillItemService billItemService;
    @Autowired
    private TbBillOtherItemService billOtherItemService;
    @Autowired
    private TbBillDrugsService billDrugsService;
    @Autowired
    private TbPolicyInfoService tbPolicyInfoService;

    @Autowired
    private YxStoreOrderService storeOrderService;
    @Autowired
    private YxStoreOrderCartInfoService storeOrderCartInfoService;
    @Autowired
    private YxUserAddressService yxUserAddressService;
    @Autowired
    private YxStoreProductService yxStoreProductService;
    @Autowired
    private YxSystemStoreService systemStoreService;

    @Autowired
    private TbBlackUserService tbBlackUserService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','tbClaimInfo:list','TBPOLICYINFO_ALL')")
    public void download(HttpServletResponse response, TbClaimInfoQueryCriteria criteria) throws IOException {
        tbClaimInfoService.download(generator.convert(tbClaimInfoService.queryAll(criteria), TbClaimInfoDto.class), response);
    }

    @GetMapping
    @Log("查询赔案信息表")
    @ApiOperation("查询赔案信息表")
    @PreAuthorize("@el.check('admin','tbClaimInfo:list','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> getTbClaimInfos(TbClaimInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tbClaimInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("删除赔案信息表")
    @ApiOperation("删除赔案信息表")
    @PreAuthorize("@el.check('admin','tbClaimInfo:del','TBPOLICYINFO_ALL')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            tbClaimInfoService.delMain(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *   添加
     *
     * @param claimInfoPage
     * @return
     */
    @PostMapping
    @Log("太保安联订单-添加")
    @ApiOperation("太保安联订单-添加")
    @PreAuthorize("@el.check('admin','tbClaimInfo:add','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> add(@RequestBody ClaimInfoPage claimInfoPage) {
        TbClaimInfo claimInfo = new TbClaimInfo();
        BeanUtils.copyProperties(claimInfoPage, claimInfo);
//        claimInfo.setVisitDate( new Timestamp(claimInfoPage.getVisitDate().getTime()));
        tbClaimInfoService.saveMain(claimInfo,claimInfoPage.getOrderProjectParam(), claimInfoPage.getNotificationPersonList(),claimInfoPage.getInsurancePersonList(),claimInfoPage.getClaimThirdInsuranceList(),claimInfoPage.getClaimThirdPayList(),claimInfoPage.getClaimMaterialList(),claimInfoPage.getClaimOtherList(),claimInfoPage.getClaimInvestList(),claimInfoPage.getClaimClmestimateList(),claimInfoPage.getClaimConsultList(),claimInfoPage.getClaimBenefitPersonList(),claimInfoPage.getClaimClaimPayList(),claimInfoPage.getClaimAddMaterialList(),claimInfoPage.getClaimAuditInfoList(),claimInfoPage.getClaimAuditpolicyList(),claimInfoPage.getClaimAboveList(),claimInfoPage.getClaimAccInfoList(),claimInfoPage.getClaimEventList());
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *  编辑
     *
     * @param claimInfoPage
     * @return
     */
    @PutMapping
    @Log("太保安联订单-编辑")
    @ApiOperation("太保安联订单-编辑")
    @PreAuthorize("@el.check('admin','tbClaimInfo:edit','TBPOLICYINFO_ALL')")
    public ResponseEntity<Object> edit(@RequestBody ClaimInfoPage claimInfoPage) {
        TbClaimInfo claimInfo = new TbClaimInfo();
        BeanUtils.copyProperties(claimInfoPage, claimInfo);
//        claimInfo.setVisitDate(new Timestamp(claimInfoPage.getVisitDate().getTime()));
        TbClaimInfo claimInfoEntity = tbClaimInfoService.getById(claimInfo.getId());
        if(claimInfoEntity==null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        tbClaimInfoService.updateMain(claimInfo,claimInfoPage.getOrderProjectParam(), claimInfoPage.getNotificationPersonList(),claimInfoPage.getInsurancePersonList(),claimInfoPage.getClaimThirdInsuranceList(),claimInfoPage.getClaimThirdPayList(),claimInfoPage.getClaimMaterialList(),claimInfoPage.getClaimOtherList(),claimInfoPage.getClaimInvestList(),claimInfoPage.getClaimClmestimateList(),claimInfoPage.getClaimConsultList(),claimInfoPage.getClaimBenefitPersonList(),claimInfoPage.getClaimClaimPayList(),claimInfoPage.getClaimAddMaterialList(),claimInfoPage.getClaimAuditInfoList(),claimInfoPage.getClaimAuditpolicyList(),claimInfoPage.getClaimAboveList(),claimInfoPage.getClaimAccInfoList(),claimInfoPage.getClaimEventList());
        return  new ResponseEntity<>(HttpStatus.OK);
    }


    @Log(value = "保存太保订单")
    @ApiOperation(value="保存太保订单", notes="保存太保订单")
    @GetMapping(value = "/saveTbOrder")
    public ResponseEntity<Object> saveTbOrder(@RequestBody TbOrderProjectParam orderProjectParam){
        tbClaimInfoService.saveTbOrder(orderProjectParam);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 通过id查询
     *
     * @param claimno
     * @return
     */
    @Log(value = "太保安联订单-通过claimno查询")
    @ApiOperation(value="太保安联订单-通过claimno查询", notes="太保安联订单-通过id查询")
    @GetMapping(value = "/queryByClaimno")
    public ResponseEntity<ClaimInfoPage> queryByClaimno(@RequestParam(name="claimno",required=true) String claimno) {
        TbClaimInfo claimInfo = tbClaimInfoService.getByClaimno(claimno);
        TbPolicyInfo policyInfo = tbPolicyInfoService.getByClaimno(claimno);
        if(claimInfo==null) {
            ClaimInfoPage claimInfoPage= defualClaimInfoPage(policyInfo);
            return new ResponseEntity<ClaimInfoPage>(claimInfoPage,HttpStatus.OK);
        }
        String id=claimInfo.getId().toString();
        ClaimInfoPage claimInfoPage=new ClaimInfoPage();
        BeanUtils.copyProperties(claimInfo, claimInfoPage);
        List<TbNotificationPerson> notificationPersonList = notificationPersonService.selectByMainId(id);
        List<TbInsurancePerson> insurancePersonList = insurancePersonService.selectByMainId(id);
        List<TbClaimThirdInsurance> claimThirdInsuranceList = claimThirdInsuranceService.selectByMainId(id);
        List<TbClaimThirdPay> claimThirdPayList = claimThirdPayService.selectByMainId(id);
        List<TbClaimMaterial> claimMaterialList = claimMaterialService.selectByMainId(id);
        List<TbClaimOther> claimOtherList = claimOtherService.selectByMainId(id);
        List<TbClaimInvest> claimInvestList = claimInvestService.selectByMainId(id);
        List<TbClaimClmestimate> claimClmestimateList = claimClmestimateService.selectByMainId(id);
        List<TbClaimConsult> claimConsultList = claimConsultService.selectByMainId(id);
        List<TbClaimBenefitPerson> claimBenefitPersonList = claimBenefitPersonService.selectByMainId(id);
        List<TbClaimClaimPay> claimClaimPayList = claimClaimPayService.selectByMainId(id);
        List<TbClaimAddMaterial> claimAddMaterialList = claimAddMaterialService.selectByMainId(id);
        List<TbClaimAuditInfo> claimAuditInfoList = claimAuditInfoService.selectByMainId(id);
        List<TbClaimAuditpolicy> claimAuditpolicyList = claimAuditpolicyService.selectByMainId(id);
        List<TbClaimAbove> claimAboveList = claimAboveService.selectByMainId(id);
        List<TbClaimAccInfo> claimAccInfoList = claimAccInfoService.selectByMainId(id);

        List<ClaimEventPage> claimEventPages=new ArrayList<>();
        List<TbClaimEvent> claimEventList = claimEventService.selectByMainId(id);
        for (TbClaimEvent tbClaimEvent : claimEventList) {
            ClaimEventPage claimEventPage=new ClaimEventPage();
            BeanUtils.copyProperties(tbClaimEvent, claimEventPage);

            List<EventBillPage> eventBillPages=new ArrayList<>();
            List<TbEventBill> eventBillList = eventBillService.selectByMainId(String.valueOf(tbClaimEvent.getId()));
            for (TbEventBill tbEventBill : eventBillList) {
                EventBillPage eventBillPage=new EventBillPage();
                BeanUtils.copyProperties(tbEventBill, eventBillPage);
                List<TbBillDrugs> billDrugsList = billDrugsService.selectByMainId(String.valueOf(tbEventBill.getId()));
                List<TbBillItem> billItemList = billItemService.selectByMainId(String.valueOf(tbEventBill.getId()));
                List<TbBillOtherItem> billOtherItemList = billOtherItemService.selectByMainId(String.valueOf(tbEventBill.getId()));

                eventBillPage.setBillDrugsList(billDrugsList);
                eventBillPage.setBillItemList(billItemList);
                eventBillPage.setBillOtherItemList(billOtherItemList);

                eventBillPages.add(eventBillPage);
            }

            claimEventPage.setEventBillList(eventBillPages);


            claimEventPages.add(claimEventPage);
        }


        YxStoreOrder order = storeOrderService.getById(claimInfo.getOrderId());

        YxUserAddress userAddress= yxUserAddressService.getById(order.getAddressId());
        YxSystemStoreQueryVo systemStoreQueryVo= systemStoreService.getYxSystemStoreById(order.getStoreId());
        TbOrderProjectParam orderProjectParam=new TbOrderProjectParam();
        BeanUtils.copyProperties(order, orderProjectParam);
        orderProjectParam.setDeductibleTotal(policyInfo.getDeductibleTotal());
        orderProjectParam.setPhone(policyInfo.getContactsPhone());
        orderProjectParam.setProvinceCode(userAddress.getProvinceCode());
        orderProjectParam.setCityCode(userAddress.getCityCode());
        orderProjectParam.setDistrictCode(userAddress.getDistrictCode());
        orderProjectParam.setStoreName(systemStoreQueryVo.getName());
        List<YxStoreOrderCartInfo> cartInfos = storeOrderCartInfoService.list(
                new QueryWrapper<YxStoreOrderCartInfo>().eq("oid",order.getId()));
        List<TbOrderDetailProjectParam> details=new ArrayList<>();
        for (YxStoreOrderCartInfo cartInfo : cartInfos) {
            YxStoreCartQueryVo cartQueryVo = JSON.parseObject(cartInfo.getCartInfo(),YxStoreCartQueryVo.class);
            YxStoreProductQueryVo productQueryVo= yxStoreProductService.selectById(cartInfo.getProductId());
            TbOrderDetailProjectParam cartInfoDTO = new TbOrderDetailProjectParam();
            cartInfoDTO.setProductId(cartInfo.getProductId());
            cartInfoDTO.setProductUniqueId(cartQueryVo.getProductAttrUnique());
            cartInfoDTO.setNum(cartQueryVo.getCartNum());
            cartInfoDTO.setMedCommonName(productQueryVo.getCommonName());
            cartInfoDTO.setMedManufacturer(productQueryVo.getManufacturer());
            cartInfoDTO.setMedName(productQueryVo.getStoreName());
            cartInfoDTO.setMedSku(productQueryVo.getYiyaobaoSku());
            cartInfoDTO.setMedSpec(productQueryVo.getSpec());
            cartInfoDTO.setMedUnit(productQueryVo.getUnit());
            cartInfoDTO.setUnitPrice(productQueryVo.getPrice().toString());
            cartInfoDTO.setPictureUrl(productQueryVo.getImage());
            details.add(cartInfoDTO);
        }
        orderProjectParam.setDetails(details);

        claimInfoPage.setNotificationPersonList(notificationPersonList);
        claimInfoPage.setInsurancePersonList(insurancePersonList);
        claimInfoPage.setClaimThirdInsuranceList(claimThirdInsuranceList);
        claimInfoPage.setClaimThirdPayList(claimThirdPayList);
        claimInfoPage.setClaimMaterialList(claimMaterialList);
        claimInfoPage.setClaimOtherList(claimOtherList);
        claimInfoPage.setClaimInvestList(claimInvestList);
        claimInfoPage.setClaimClmestimateList(claimClmestimateList);
        claimInfoPage.setClaimConsultList(claimConsultList);
        claimInfoPage.setClaimBenefitPersonList(claimBenefitPersonList);
        claimInfoPage.setClaimClaimPayList(claimClaimPayList);
        claimInfoPage.setClaimAddMaterialList(claimAddMaterialList);
        claimInfoPage.setClaimAuditInfoList(claimAuditInfoList);
        claimInfoPage.setClaimAuditpolicyList(claimAuditpolicyList);
        claimInfoPage.setClaimAboveList(claimAboveList);
        claimInfoPage.setClaimAccInfoList(claimAccInfoList);
        claimInfoPage.setClaimEventList(claimEventPages);
        claimInfoPage.setOrderProjectParam(orderProjectParam);

        int blackUserCount= tbBlackUserService.findByNameOrIdCard(policyInfo.getName(),policyInfo.getContactsName(),policyInfo.getIdNo());
        if(blackUserCount>0){
            claimInfoPage.setUserStatus("1");
        }else{
            claimInfoPage.setUserStatus("0");
        }
        return new ResponseEntity<>(claimInfoPage,HttpStatus.OK);

    }

    public ClaimInfoPage defualClaimInfoPage(TbPolicyInfo policyInfo){
        ClaimInfoPage claimInfoPage=new ClaimInfoPage();
        claimInfoPage.setClaimno(policyInfo.getRequestCaimReportNo());
        claimInfoPage.setBatchno(policyInfo.getRequestCaimReportNo());
        claimInfoPage.setReportno(policyInfo.getRequestCaimReportNo());
        List<TbClaimBenefitPerson> claimBenefitPersonList=new ArrayList<>();
        TbClaimBenefitPerson claimBenefitPerson=new TbClaimBenefitPerson();
        claimBenefitPerson.setBftype("2");
        claimBenefitPerson.setRelationship("408");
        claimBenefitPerson.setIdtype("1");
        claimBenefitPerson.setIdno("9131000032465377XF");
        claimBenefitPerson.setIdBegdate("");
        claimBenefitPerson.setIdEnddate("");
        claimBenefitPerson.setName("太保安联健康保险股份有限公司");
        claimBenefitPerson.setBirthdate("");
        claimBenefitPerson.setSex("");
        claimBenefitPerson.setMobilephone("");
        claimBenefitPerson.setTelephone("");
        claimBenefitPerson.setEmail("");
        claimBenefitPerson.setAddr("");
        claimBenefitPerson.setZip("");
        claimBenefitPerson.setSettype("2");
        claimBenefitPerson.setBanktype("103100092910");
        claimBenefitPerson.setBanksubtype("103100092910");
        claimBenefitPerson.setBankbranch("");
        claimBenefitPerson.setBanksubbranch("");
        claimBenefitPerson.setProvinceofbank("0000002");
        claimBenefitPerson.setCityofbank("0000041");
        claimBenefitPerson.setAcctno("03311500040032857");
        claimBenefitPersonList.add(claimBenefitPerson);
        claimInfoPage.setClaimBenefitPersonList(claimBenefitPersonList);

        List<TbInsurancePerson> insurancePersonList =new ArrayList<>();
        TbInsurancePerson insurancePerson=new TbInsurancePerson();
        insurancePerson.setName(policyInfo.getName());
        insurancePerson.setSex(policyInfo.getSex());
        insurancePerson.setIdtype(policyInfo.getIdType());
        insurancePerson.setIdno(policyInfo.getIdNo());
        insurancePerson.setBirthdate(policyInfo.getInsuredBirthday());
        insurancePersonList.add(insurancePerson);
        claimInfoPage.setInsurancePersonList(insurancePersonList);

        List<TbNotificationPerson> notificationPersonList =new ArrayList<>();
        TbNotificationPerson notificationPerson=new TbNotificationPerson();
        notificationPerson.setName(policyInfo.getContactsName());
        notificationPerson.setMobilephone(policyInfo.getContactsPhone());
        notificationPersonList.add(notificationPerson);
        claimInfoPage.setNotificationPersonList(notificationPersonList);

        List<TbClaimClmestimate> claimClmestimateList=new ArrayList<>();
        TbClaimClmestimate claimClmestimate=new TbClaimClmestimate();
        claimClmestimate.setPolicyno(policyInfo.getPolicyNo());
        claimClmestimate.setClasscode(policyInfo.getResponsibilityCode());
        claimInfoPage.setClaimClmestimateList(claimClmestimateList);

        List<TbClaimAuditInfo> claimAuditInfoList=new ArrayList<>();
        TbClaimAuditInfo claimAuditInfo=new TbClaimAuditInfo();
        claimAuditInfo.setPolicyno(policyInfo.getPolicyNo());
        claimAuditInfo.setDutycode(policyInfo.getResponsibilityCode());
        claimAuditInfo.setClasscode(policyInfo.getResponsibilityCode());
        claimInfoPage.setClaimAuditInfoList(claimAuditInfoList);


        List<TbClaimClaimPay> claimClaimPays=new ArrayList<>();
        TbClaimClaimPay claimClaimPay=new TbClaimClaimPay();
        claimClaimPay.setPolicyno(policyInfo.getPolicyNo());
        claimClaimPay.setDutycode(policyInfo.getResponsibilityCode());
        claimClaimPay.setClasscode(policyInfo.getResponsibilityCode());
        claimInfoPage.setClaimClaimPayList(claimClaimPays);


        TbOrderProjectParam orderProjectParam=new TbOrderProjectParam();
        orderProjectParam.setPhone(policyInfo.getContactsPhone());
        orderProjectParam.setDeductibleTotal(policyInfo.getDeductibleTotal());
        orderProjectParam.setUploadYiyaobaoFlag(0);
        orderProjectParam.setStoreName("广州上药益药药房有限公司（云药房）");
        claimInfoPage.setOrderProjectParam(orderProjectParam);

        int blackUserCount= tbBlackUserService.findByNameOrIdCard(policyInfo.getName(),policyInfo.getContactsName(),policyInfo.getIdNo());
        if(blackUserCount>0){
            claimInfoPage.setUserStatus("1");
        }else{
            claimInfoPage.setUserStatus("0");
        }
        return claimInfoPage;
    }

    public static void main(String[] args) {
        System.gc();
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "notification_person通过主表ID查询")
    @ApiOperation(value="notification_person主表ID查询", notes="notification_person-通主表ID查询")
    @GetMapping(value = "/queryNotificationPersonByMainId")
    public ResponseEntity<Object> queryNotificationPersonListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbNotificationPerson> notificationPersonList = notificationPersonService.selectByMainId(id);
        return new ResponseEntity<>(notificationPersonList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "insurance_person通过主表ID查询")
    @ApiOperation(value="insurance_person主表ID查询", notes="insurance_person-通主表ID查询")
    @GetMapping(value = "/queryInsurancePersonByMainId")
    public ResponseEntity<Object> queryInsurancePersonListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbInsurancePerson> insurancePersonList = insurancePersonService.selectByMainId(id);
        return new ResponseEntity<>(insurancePersonList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_third_insurance通过主表ID查询")
    @ApiOperation(value="claim_third_insurance主表ID查询", notes="claim_third_insurance-通主表ID查询")
    @GetMapping(value = "/queryClaimThirdInsuranceByMainId")
    public ResponseEntity<Object> queryClaimThirdInsuranceListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimThirdInsurance> claimThirdInsuranceList = claimThirdInsuranceService.selectByMainId(id);
        return new ResponseEntity<>(claimThirdInsuranceList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_third_pay通过主表ID查询")
    @ApiOperation(value="claim_third_pay主表ID查询", notes="claim_third_pay-通主表ID查询")
    @GetMapping(value = "/queryClaimThirdPayByMainId")
    public ResponseEntity<Object> queryClaimThirdPayListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimThirdPay> claimThirdPayList = claimThirdPayService.selectByMainId(id);
        return new ResponseEntity<>(claimThirdPayList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_material通过主表ID查询")
    @ApiOperation(value="claim_material主表ID查询", notes="claim_material-通主表ID查询")
    @GetMapping(value = "/queryClaimMaterialByMainId")
    public ResponseEntity<Object> queryClaimMaterialListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimMaterial> claimMaterialList = claimMaterialService.selectByMainId(id);
        return new ResponseEntity<>(claimMaterialList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_other通过主表ID查询")
    @ApiOperation(value="claim_other主表ID查询", notes="claim_other-通主表ID查询")
    @GetMapping(value = "/queryClaimOtherByMainId")
    public ResponseEntity<Object> queryClaimOtherListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimOther> claimOtherList = claimOtherService.selectByMainId(id);
        return new ResponseEntity<>(claimOtherList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_invest通过主表ID查询")
    @ApiOperation(value="claim_invest主表ID查询", notes="claim_invest-通主表ID查询")
    @GetMapping(value = "/queryClaimInvestByMainId")
    public ResponseEntity<Object> queryClaimInvestListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimInvest> claimInvestList = claimInvestService.selectByMainId(id);
        return new ResponseEntity<>(claimInvestList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_clmestimate通过主表ID查询")
    @ApiOperation(value="claim_clmestimate主表ID查询", notes="claim_clmestimate-通主表ID查询")
    @GetMapping(value = "/queryClaimClmestimateByMainId")
    public ResponseEntity<Object> queryClaimClmestimateListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimClmestimate> claimClmestimateList = claimClmestimateService.selectByMainId(id);
        return new ResponseEntity<>(claimClmestimateList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_consult通过主表ID查询")
    @ApiOperation(value="claim_consult主表ID查询", notes="claim_consult-通主表ID查询")
    @GetMapping(value = "/queryClaimConsultByMainId")
    public ResponseEntity<Object> queryClaimConsultListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimConsult> claimConsultList = claimConsultService.selectByMainId(id);
        return new ResponseEntity<>(claimConsultList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_benefit_person通过主表ID查询")
    @ApiOperation(value="claim_benefit_person主表ID查询", notes="claim_benefit_person-通主表ID查询")
    @GetMapping(value = "/queryClaimBenefitPersonByMainId")
    public ResponseEntity<Object> queryClaimBenefitPersonListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimBenefitPerson> claimBenefitPersonList = claimBenefitPersonService.selectByMainId(id);
        return new ResponseEntity<>(claimBenefitPersonList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_claim_pay通过主表ID查询")
    @ApiOperation(value="claim_claim_pay主表ID查询", notes="claim_claim_pay-通主表ID查询")
    @GetMapping(value = "/queryClaimClaimPayByMainId")
    public ResponseEntity<Object> queryClaimClaimPayListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimClaimPay> claimClaimPayList = claimClaimPayService.selectByMainId(id);
        return new ResponseEntity<>(claimClaimPayList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_add_material通过主表ID查询")
    @ApiOperation(value="claim_add_material主表ID查询", notes="claim_add_material-通主表ID查询")
    @GetMapping(value = "/queryClaimAddMaterialByMainId")
    public ResponseEntity<Object> queryClaimAddMaterialListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimAddMaterial> claimAddMaterialList = claimAddMaterialService.selectByMainId(id);
        return new ResponseEntity<>(claimAddMaterialList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_audit_info通过主表ID查询")
    @ApiOperation(value="claim_audit_info主表ID查询", notes="claim_audit_info-通主表ID查询")
    @GetMapping(value = "/queryClaimAuditInfoByMainId")
    public ResponseEntity<Object> queryClaimAuditInfoListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimAuditInfo> claimAuditInfoList = claimAuditInfoService.selectByMainId(id);
        return new ResponseEntity<>(claimAuditInfoList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_auditpolicy通过主表ID查询")
    @ApiOperation(value="claim_auditpolicy主表ID查询", notes="claim_auditpolicy-通主表ID查询")
    @GetMapping(value = "/queryClaimAuditpolicyByMainId")
    public ResponseEntity<Object> queryClaimAuditpolicyListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimAuditpolicy> claimAuditpolicyList = claimAuditpolicyService.selectByMainId(id);
        return new ResponseEntity<>(claimAuditpolicyList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_above通过主表ID查询")
    @ApiOperation(value="claim_above主表ID查询", notes="claim_above-通主表ID查询")
    @GetMapping(value = "/queryClaimAboveByMainId")
    public ResponseEntity<Object> queryClaimAboveListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimAbove> claimAboveList = claimAboveService.selectByMainId(id);
        return new ResponseEntity<>(claimAboveList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_acc_info通过主表ID查询")
    @ApiOperation(value="claim_acc_info主表ID查询", notes="claim_acc_info-通主表ID查询")
    @GetMapping(value = "/queryClaimAccInfoByMainId")
    public ResponseEntity<Object> queryClaimAccInfoListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimAccInfo> claimAccInfoList = claimAccInfoService.selectByMainId(id);
        return new ResponseEntity<>(claimAccInfoList,HttpStatus.OK);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Log(value = "claim_event通过主表ID查询")
    @ApiOperation(value="claim_event主表ID查询", notes="claim_event-通主表ID查询")
    @GetMapping(value = "/queryClaimEventByMainId")
    public ResponseEntity<Object> queryClaimEventListByMainId(@RequestParam(name="id",required=true) String id) {
        List<TbClaimEvent> claimEventList = claimEventService.selectByMainId(id);
        return new ResponseEntity<>(claimEventList,HttpStatus.OK);
    }

    @Log(value = "订单完成回传理赔系统")
    @ApiOperation(value="订单完成回传理赔系统", notes="订单完成回传理赔系统")
    @GetMapping(value = "/orderFilish")
    public ResponseEntity<Object> orderFilish(@RequestParam(name="id",required=true) String id) {
            tbClaimInfoService.orderFilish(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
