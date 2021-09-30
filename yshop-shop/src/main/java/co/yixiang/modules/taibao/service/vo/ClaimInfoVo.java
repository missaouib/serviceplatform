package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: claim_info
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@ApiModel(value = "claim_info对象", description = "claim_info")
@XmlRootElement(name = "root")
@XmlType(propOrder={
        "reportno"
        ,"batchno"
        ,"claimno"
        ,"custmco"
        ,"exptime"
        ,"medicalCode"
        ,"emailAccept"
        ,"visitDate"
        ,"advanceClosingTime"
        ,"dataCollectionDay"
        ,"insurancePersonVo"
        ,"notificationPersonVo"
        ,"claimBenefitPersonVos"
        ,"claimAccInfoVo"
        ,"claimMaterialVos"
        ,"claimThirdInsuranceVos"
        ,"claimThirdPayVos"
        ,"claimEventVos"
        ,"claimClmestimateVos"
        ,"claimAuditpolicyVos"
        ,"claimClaimPayVos"
        ,"reauditoption"
        ,"reauditdate"
        ,"hangupsign"
        ,"claimInvestVos"
        ,"claimAddMaterialVos"
        ,"claimAuditInfoVos"
        ,"claimrescode"
        ,"auditoption"
        ,"claimConsultVos"
        ,"claimAboveVos"
        ,"claimOtherVos"})
public class ClaimInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "报案号")
    private String reportno;

    @ApiModelProperty(value = "批次号")
    private String batchno;

    @ApiModelProperty(value = "赔案号")
    private String claimno;

    @ApiModelProperty(value = "收单单位代码")
    private String custmco;

    @ApiModelProperty(value = "快递签收时间")
    private String exptime;

    @ApiModelProperty(value = "医保号")
    private String medicalCode;

    @ApiModelProperty(value = "是否接受电子邮件")
    private String emailAccept;

    @ApiModelProperty(value = "收单时间")
    private String visitDate;

    @ApiModelProperty(value = "资料收齐时间")
    private String advanceClosingTime;

    @ApiModelProperty(value = "垫付结案时间")
    private String dataCollectionDay;

    @ApiModelProperty(value = "复核意见")
    private String reauditoption;

    @ApiModelProperty(value = "复核完成时间")
    private String reauditdate;

    @ApiModelProperty(value = "挂起类型(多种类型用逗号拼接)")
    private List<String> hangupsign;

    @ApiModelProperty(value = "赔案层结论")
    private String claimrescode;

    @ApiModelProperty(value = "审核意见")
    private String auditoption;

    @ApiModelProperty(value = "被保人信息")
    private InsurancePersonVo insurancePersonVo;

    @ApiModelProperty(value = "报案人信息")
    private NotificationPersonVo notificationPersonVo;

    @ApiModelProperty(value = "领款人")
    private List<ClaimBenefitPersonVo> claimBenefitPersonVos;

    @ApiModelProperty(value = "出险信息")
    private ClaimAccInfoVo claimAccInfoVo;

    @ApiModelProperty(value = "资料")
    private  List<ClaimMaterialVo> claimMaterialVos;

    @ApiModelProperty(value = "第三方投保信息")
    private  List<ClaimThirdInsuranceVo> claimThirdInsuranceVos;

    @ApiModelProperty(value = "第三方赔付情况")
    private  List<ClaimThirdPayVo> claimThirdPayVos;

    @ApiModelProperty(value = "事件信息")
    private  List<ClaimEventVo> claimEventVos;

    @ApiModelProperty(value = "理赔预估信息")
    private  List<ClaimClmestimateVo> claimClmestimateVos;


    @ApiModelProperty(value = "赔付保单后续处理")
    private  List<ClaimAuditpolicyVo> claimAuditpolicyVos;

    @ApiModelProperty(value = "责任赔付金额")
    private  List<ClaimClaimPayVo> claimClaimPayVos;

    @ApiModelProperty(value = "调查")
    private  List<ClaimInvestVo> claimInvestVos;

    @ApiModelProperty(value = "补充资料")
    private  List<ClaimAddMaterialVo> claimAddMaterialVos;

    @ApiModelProperty(value = "赔付责任理赔结论")
    private  List<ClaimAuditInfoVo> claimAuditInfoVos;

    @ApiModelProperty(value = "协谈")
    private  List<ClaimConsultVo> claimConsultVos;

    @ApiModelProperty(value = "超额件")
    private  List<ClaimAboveVo> claimAboveVos;

    @ApiModelProperty(value = "其他")
    private  List<ClaimOtherVo> claimOtherVos;

    public String getReportno() {
        return reportno;
    }

    @XmlElement(name = "reportno")
    public void setReportno(String reportno) {
        this.reportno = reportno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    @XmlElement(name = "batchno")
    public String getBatchno() {
        return batchno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    @XmlElement(name = "claimno")
    public String getClaimno() {
        return claimno;
    }

    public void setCustmco(String custmco) {
        this.custmco = custmco;
    }

    @XmlElement(name = "custmco")
    public String getCustmco() {
        return custmco;
    }

    public void setExptime(String exptime) {
        this.exptime = exptime;
    }

    @XmlElement(name = "exptime")
    public String getExptime() {
        return exptime;
    }

    public void setMedicalCode(String medicalCode) {
        this.medicalCode = medicalCode;
    }

    @XmlElement(name = "medical_code")
    public String getMedicalCode() {
        return medicalCode;
    }

    public void setEmailAccept(String emailAccept) {
        this.emailAccept = emailAccept;
    }

    @XmlElement(name = "email_accept")
    public String getEmailAccept() {
        return emailAccept;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    @XmlElement(name = "visit_date")
    public String getVisitDate() {
        return visitDate;
    }

    public String getAdvanceClosingTime() {
        return advanceClosingTime;
    }

    @XmlElement(name = "advanceClosingTime")
    public void setAdvanceClosingTime(String advanceClosingTime) {
        this.advanceClosingTime = advanceClosingTime;
    }

    public String getDataCollectionDay() {
        return dataCollectionDay;
    }

    @XmlElement(name = "dataCollectionDay")
    public void setDataCollectionDay(String dataCollectionDay) {
        this.dataCollectionDay = dataCollectionDay;
    }

    public void setReauditoption(String reauditoption) {
        this.reauditoption = reauditoption;
    }

    @XmlElement(name = "reauditOption")
    public String getReauditoption() {
        return reauditoption;
    }

    public void setReauditdate(String reauditdate) {
        this.reauditdate = reauditdate;
    }

    @XmlElement(name = "reauditDate")
    public String getReauditdate() {
        return reauditdate;
    }


    public List<String> getHangupsign() {
        return hangupsign;
    }
    @XmlElementWrapper(name = "hangUpSign")
    public void setHangupsign(List<String> hangupsign) {
        this.hangupsign = hangupsign;
    }

    public void setClaimrescode(String claimrescode) {
        this.claimrescode = claimrescode;
    }

    @XmlElement(name = "claimResCode")
    public String getClaimrescode() {
        return claimrescode;
    }

    public void setAuditoption(String auditoption) {
        this.auditoption = auditoption;
    }

    @XmlElement(name = "auditOption")
    public String getAuditoption() {
        return auditoption;
    }

    public InsurancePersonVo getInsurancePersonVo() {
        return insurancePersonVo;
    }

    @XmlElement(name = "insurance_person")
    public void setInsurancePersonVo(InsurancePersonVo insurancePersonVo) {
        this.insurancePersonVo = insurancePersonVo;
    }

    public NotificationPersonVo getNotificationPersonVo() {
        return notificationPersonVo;
    }

    @XmlElement(name = "notification_person")
    public void setNotificationPersonVo(NotificationPersonVo notificationPersonVo) {
        this.notificationPersonVo = notificationPersonVo;
    }

    public List<ClaimBenefitPersonVo> getClaimBenefitPersonVos() {
        return claimBenefitPersonVos;
    }

    @XmlElementWrapper(name = "benefit_persons")
    @XmlElements({@XmlElement(name = "benefit_person", type = ClaimBenefitPersonVo.class)})
    public void setClaimBenefitPersonVos(List<ClaimBenefitPersonVo> claimBenefitPersonVos) {
        this.claimBenefitPersonVos = claimBenefitPersonVos;
    }

    public ClaimAccInfoVo getClaimAccInfoVo() {
        return claimAccInfoVo;
    }

    @XmlElement(name = "acc_info")
    public void setClaimAccInfoVo(ClaimAccInfoVo claimAccInfoVo) {
        this.claimAccInfoVo = claimAccInfoVo;
    }

    public List<ClaimMaterialVo> getClaimMaterialVos() {
        return claimMaterialVos;
    }

    @XmlElementWrapper(name = "materials_info")
    @XmlElements({@XmlElement(name = "material", type = ClaimMaterialVo.class)})
    public void setClaimMaterialVos(List<ClaimMaterialVo> claimMaterialVos) {
        this.claimMaterialVos = claimMaterialVos;
    }

    public List<ClaimThirdInsuranceVo> getClaimThirdInsuranceVos() {
        return claimThirdInsuranceVos;
    }

    @XmlElementWrapper(name = "third_insurances")
    @XmlElements({@XmlElement(name = "third_insurance", type = ClaimThirdInsuranceVo.class)})
    public void setClaimThirdInsuranceVos(List<ClaimThirdInsuranceVo> claimThirdInsuranceVos) {
        this.claimThirdInsuranceVos = claimThirdInsuranceVos;
    }

    public List<ClaimThirdPayVo> getClaimThirdPayVos() {
        return claimThirdPayVos;
    }

    @XmlElementWrapper(name = "third_pays" )
    @XmlElements({@XmlElement(name = "third_pay", type = ClaimThirdPayVo.class)})
    public void setClaimThirdPayVos(List<ClaimThirdPayVo> claimThirdPayVos) {
        this.claimThirdPayVos = claimThirdPayVos;
    }

    public List<ClaimEventVo> getClaimEventVos() {
        return claimEventVos;
    }

    @XmlElementWrapper(name = "events")
    @XmlElements({@XmlElement(name = "event", type = ClaimEventVo.class)})
    public void setClaimEventVos(List<ClaimEventVo> claimEventVos) {
        this.claimEventVos = claimEventVos;
    }

    public List<ClaimClmestimateVo> getClaimClmestimateVos() {
        return claimClmestimateVos;
    }

    @XmlElementWrapper(name = "clmestimates")
    @XmlElements({@XmlElement(name = "clmestimate", type = ClaimClmestimateVo.class)})
    public void setClaimClmestimateVos(List<ClaimClmestimateVo> claimClmestimateVos) {
        this.claimClmestimateVos = claimClmestimateVos;
    }

    public List<ClaimAuditpolicyVo> getClaimAuditpolicyVos() {
        return claimAuditpolicyVos;
    }

    @XmlElementWrapper(name = "auditpolicys")
    @XmlElements({@XmlElement(name = "auditpolicy", type = ClaimAuditpolicyVo.class)})
    public void setClaimAuditpolicyVos(List<ClaimAuditpolicyVo> claimAuditpolicyVos) {
        this.claimAuditpolicyVos = claimAuditpolicyVos;
    }

    public List<ClaimClaimPayVo> getClaimClaimPayVos() {
        return claimClaimPayVos;
    }

    @XmlElementWrapper(name = "claims_pay")
    @XmlElements({@XmlElement(name = "claim_pay", type = ClaimClaimPayVo.class)})
    public void setClaimClaimPayVos(List<ClaimClaimPayVo> claimClaimPayVos) {
        this.claimClaimPayVos = claimClaimPayVos;
    }

    public List<ClaimInvestVo> getClaimInvestVos() {
        return claimInvestVos;
    }

    @XmlElementWrapper(name = "invests")
    @XmlElements({@XmlElement(name = "invest", type = ClaimInvestVo.class)})
    public void setClaimInvestVos(List<ClaimInvestVo> claimInvestVos) {
        this.claimInvestVos = claimInvestVos;
    }

    public List<ClaimAddMaterialVo> getClaimAddMaterialVos() {
        return claimAddMaterialVos;
    }

    @XmlElementWrapper(name = "add_materials")
    @XmlElements({@XmlElement(name = "material", type = ClaimAddMaterialVo.class)})
    public void setClaimAddMaterialVos(List<ClaimAddMaterialVo> claimAddMaterialVos) {
        this.claimAddMaterialVos = claimAddMaterialVos;
    }

    public List<ClaimAuditInfoVo> getClaimAuditInfoVos() {
        return claimAuditInfoVos;
    }

    @XmlElementWrapper(name = "audits_info")
    @XmlElements({@XmlElement(name = "audit", type = ClaimAuditInfoVo.class)})
    public void setClaimAuditInfoVos(List<ClaimAuditInfoVo> claimAuditInfoVos) {
        this.claimAuditInfoVos = claimAuditInfoVos;
    }

    public List<ClaimConsultVo> getClaimConsultVos() {
        return claimConsultVos;
    }

    @XmlElementWrapper(name = "consults")
    @XmlElements({@XmlElement(name = "consult", type = ClaimConsultVo.class)})
    public void setClaimConsultVos(List<ClaimConsultVo> claimConsultVos) {
        this.claimConsultVos = claimConsultVos;
    }

    public List<ClaimAboveVo> getClaimAboveVos() {
        return claimAboveVos;
    }

    @XmlElementWrapper(name = "aboves")
    @XmlElements({@XmlElement(name = "above", type = ClaimAboveVo.class)})
    public void setClaimAboveVos(List<ClaimAboveVo> claimAboveVos) {
        this.claimAboveVos = claimAboveVos;
    }

    public List<ClaimOtherVo> getClaimOtherVos() {
        return claimOtherVos;
    }

    @XmlElementWrapper(name = "others")
    @XmlElements({@XmlElement(name = "other", type = ClaimOtherVo.class)})
    public void setClaimOtherVos(List<ClaimOtherVo> claimOtherVos) {
        this.claimOtherVos = claimOtherVos;
    }

    public ClaimInfoVo() {
        this.reportno="";
        this.batchno ="";
        this.claimno = "";
        this.custmco ="";
        this.exptime = "";
        this.medicalCode = "";
        this.emailAccept = "";
        this.visitDate = "";
        this.advanceClosingTime="";
        this.dataCollectionDay="";
        this.reauditoption ="";
        this.reauditdate ="";
        this.hangupsign =new ArrayList<>();
        this.claimrescode = "";
        this.auditoption = "";
        this.insurancePersonVo =new InsurancePersonVo();
        this.notificationPersonVo =new NotificationPersonVo();
        this.claimBenefitPersonVos = new ArrayList<>();
        this.claimAccInfoVo = new ClaimAccInfoVo();
        this.claimMaterialVos =  new ArrayList<>();
        this.claimThirdInsuranceVos =  new ArrayList<>();
        this.claimThirdPayVos = new ArrayList<>();
        this.claimEventVos = new ArrayList<>();
        this.claimClmestimateVos =  new ArrayList<>();
        this.claimAuditpolicyVos =  new ArrayList<>();
        this.claimClaimPayVos = new ArrayList<>();
        this.claimInvestVos = new ArrayList<>();
        this.claimAddMaterialVos = new ArrayList<>();
        this.claimAuditInfoVos = new ArrayList<>();
        this.claimConsultVos = new ArrayList<>();
        this.claimAboveVos = new ArrayList<>();
        this.claimOtherVos = new ArrayList<>();
    }
}
