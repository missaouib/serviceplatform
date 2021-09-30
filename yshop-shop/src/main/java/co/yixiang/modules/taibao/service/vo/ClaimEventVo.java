package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: claim_event
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "event")
@XmlType(propOrder={"claimacc"
        ,"illcode"
        ,"caredate"
        ,"indate"
        ,"outdate"
        ,"indays"
        ,"deadDate"
        ,"disableDate"
        ,"hospitalInfo"
        ,"clinical"
        ,"doctor"
        ,"surgery"
        ,"critical"
        ,"medicalType"
        ,"referral"
        ,"referralHosp"
        ,"referralClinical"
        ,"referralDoctor"
        ,"edc"
        ,"issingle"
        ,"isuseOther"
        ,"conditionInfo"
        ,"billCnt"
        ,"auditconclusion"
        ,"auditoption"
        ,"eventBillVos"})
@ApiModel(value="claim_event对象", description="claim_event")
public class ClaimEventVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "索赔事故性质（枚举值）")
    private String claimacc;
    @ApiModelProperty(value = "疾病诊断")
    private String illcode;
    @ApiModelProperty(value = "就诊日期")
    private String caredate;
    @ApiModelProperty(value = "入院日期")
    private String indate;
    @ApiModelProperty(value = "出院日期")
    private String outdate;
    @ApiModelProperty(value = "住院天数")
    private String indays;
    @ApiModelProperty(value = "身故日期")
    private String deadDate;
    @ApiModelProperty(value = "伤残鉴定日期")
    private String disableDate;
    @ApiModelProperty(value = "就诊医院代码")
    private String hospitalInfo;
    @ApiModelProperty(value = "就诊医院名称")
    private String clinical;
    @ApiModelProperty(value = "主治医生姓名")
    private String doctor;
    @ApiModelProperty(value = "手术代码")
    private String surgery;
    @ApiModelProperty(value = "重疾代码")
    private String critical;
    @ApiModelProperty(value = "医保类型")
    private String medicalType;
    @ApiModelProperty(value = "是否转诊")
    private String referral;
    @ApiModelProperty(value = "转来医院名称")
    private String referralHosp;
    @ApiModelProperty(value = "科室名称")
    private String referralClinical;
    @ApiModelProperty(value = "医生姓名")
    private String referralDoctor;
    @ApiModelProperty(value = "预产期")
    private String edc;
    @ApiModelProperty(value = "预期是否单胎")
    private String issingle;
    @ApiModelProperty(value = "是否使用妊娠辅助医疗或人工授精")
    private String isuseOther;
    @ApiModelProperty(value = "具体情况")
    private String conditionInfo;
    @ApiModelProperty(value = "收据总数")
    private String billCnt;
    @ApiModelProperty(value = "事件审核结论")
    private String auditconclusion;
    @ApiModelProperty(value = "事件审核意见")
    private String auditoption;

    @ApiModelProperty(value = "收据信息bill")
    private List<EventBillVo> eventBillVos;

    public ClaimEventVo() {
        this.claimacc = "";
        this.illcode = "";
        this.caredate = "";
        this.indate = "";
        this.outdate = "";
        this.indays = "";
        this.deadDate = "";
        this.disableDate = "";
        this.hospitalInfo = "";
        this.clinical = "";
        this.doctor = "";
        this.surgery ="";
        this.critical = "";
        this.medicalType = "";
        this.referral = "";
        this.referralHosp = "";
        this.referralClinical = "";
        this.referralDoctor ="";
        this.edc ="";
        this.issingle = "";
        this.isuseOther = "";
        this.conditionInfo = "";
        this.billCnt = "";
        this.auditconclusion = "";
        this.auditoption ="";
        this.eventBillVos =new ArrayList<>();
    }

    public String getClaimacc() {
        return claimacc;
    }

    @XmlElement(name = "claimacc")
    public void setClaimacc(String claimacc) {
        this.claimacc = claimacc;
    }

    public String getIllcode() {
        return illcode;
    }
    @XmlElement(name = "illcode")
    public void setIllcode(String illcode) {
        this.illcode = illcode;
    }

    public String getCaredate() {
        return caredate;
    }
    @XmlElement(name = "caredate")
    public void setCaredate(String caredate) {
        this.caredate = caredate;
    }

    public String getIndate() {
        return indate;
    }
    @XmlElement(name = "indate")
    public void setIndate(String indate) {
        this.indate = indate;
    }

    public String getOutdate() {
        return outdate;
    }
    @XmlElement(name = "outdate")
    public void setOutdate(String outdate) {
        this.outdate = outdate;
    }

    public String getIndays() {
        return indays;
    }
    @XmlElement(name = "indays")
    public void setIndays(String indays) {
        this.indays = indays;
    }

    public String getDeadDate() {
        return deadDate;
    }
    @XmlElement(name = "dead_date")
    public void setDeadDate(String deadDate) {
        this.deadDate = deadDate;
    }

    public String getDisableDate() {
        return disableDate;
    }
    @XmlElement(name = "disable_date")
    public void setDisableDate(String disableDate) {
        this.disableDate = disableDate;
    }

    public String getHospitalInfo() {
        return hospitalInfo;
    }
    @XmlElement(name = "hospital_info")
    public void setHospitalInfo(String hospitalInfo) {
        this.hospitalInfo = hospitalInfo;
    }

    public String getClinical() {
        return clinical;
    }
    @XmlElement(name = "clinical")
    public void setClinical(String clinical) {
        this.clinical = clinical;
    }

    public String getDoctor() {
        return doctor;
    }
    @XmlElement(name = "doctor")
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getSurgery() {
        return surgery;
    }
    @XmlElement(name = "surgery")
    public void setSurgery(String surgery) {
        this.surgery = surgery;
    }

    public String getCritical() {
        return critical;
    }
    @XmlElement(name = "critical")
    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getMedicalType() {
        return medicalType;
    }
    @XmlElement(name = "medical_type")
    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    public String getReferral() {
        return referral;
    }
    @XmlElement(name = "referral")
    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getReferralHosp() {
        return referralHosp;
    }
    @XmlElement(name = "referral_hosp")
    public void setReferralHosp(String referralHosp) {
        this.referralHosp = referralHosp;
    }

    public String getReferralClinical() {
        return referralClinical;
    }
    @XmlElement(name = "referral_clinical")
    public void setReferralClinical(String referralClinical) {
        this.referralClinical = referralClinical;
    }

    public String getReferralDoctor() {
        return referralDoctor;
    }
    @XmlElement(name = "referral_doctor")
    public void setReferralDoctor(String referralDoctor) {
        this.referralDoctor = referralDoctor;
    }

    public String getEdc() {
        return edc;
    }
    @XmlElement(name = "edc")
    public void setEdc(String edc) {
        this.edc = edc;
    }

    public String getIssingle() {
        return issingle;
    }
    @XmlElement(name = "issingle")
    public void setIssingle(String issingle) {
        this.issingle = issingle;
    }

    public String getIsuseOther() {
        return isuseOther;
    }
    @XmlElement(name = "isuse_other")
    public void setIsuseOther(String isuseOther) {
        this.isuseOther = isuseOther;
    }

    public String getConditionInfo() {
        return conditionInfo;
    }
    @XmlElement(name = "condition")
    public void setConditionInfo(String conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    public String getBillCnt() {
        return billCnt;
    }
    @XmlElement(name = "bill_cnt")
    public void setBillCnt(String billCnt) {
        this.billCnt = billCnt;
    }

    public String getAuditconclusion() {
        return auditconclusion;
    }
    @XmlElement(name = "auditconclusion")
    public void setAuditconclusion(String auditconclusion) {
        this.auditconclusion = auditconclusion;
    }

    public String getAuditoption() {
        return auditoption;
    }
    @XmlElement(name = "auditoption")
    public void setAuditoption(String auditoption) {
        this.auditoption = auditoption;
    }

    public List<EventBillVo> getEventBillVos() {
        return eventBillVos;
    }
    @XmlElementWrapper(name = "bills")
    @XmlElements({@XmlElement(name = "bill", type = EventBillVo.class)})
    public void setEventBillVos(List<EventBillVo> eventBillVos) {
        this.eventBillVos = eventBillVos;
    }
}
