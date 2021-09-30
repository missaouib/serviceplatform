package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: bill_drugs
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@ApiModel(value="event_bill对象")
@Data
@XmlRootElement(name="drug")
@XmlType(propOrder={
        "drugCode"
        ,"drugName"
        ,"drugBillCode"
        ,"drugStd"
        ,"drugType"
        ,"drugUnit"
        ,"drugUnitAmt"
        ,"drugTotal"
        ,"drugPay"
        ,"medicalType"
        ,"selfpayRate"
        ,"selfpayAmt"
        ,"rejectFlag"
        ,"rejectReason"
})
public class BillDrugsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "药品代码")
    private String drugCode;
    @ApiModelProperty(value = "药品名称")
    private String drugName;
    @ApiModelProperty(value = "对应账单项代码")
    private String drugBillCode;
    @ApiModelProperty(value = "规格")
    private String drugStd;
    @ApiModelProperty(value = "剂型")
    private String drugType;
    @ApiModelProperty(value = "单位")
    private String drugUnit;
    @ApiModelProperty(value = "单价")
    private String drugUnitAmt;
    @ApiModelProperty(value = "数量")
    private String drugTotal;
    @ApiModelProperty(value = "发生金额")
    private String drugPay;
    @ApiModelProperty(value = "医保类别")
    private String medicalType;
    @ApiModelProperty(value = "自付比例")
    private String selfpayRate;
    @ApiModelProperty(value = "自付金额")
    private String selfpayAmt;
    @ApiModelProperty(value = "是否剔除")
    private String rejectFlag;
    @ApiModelProperty(value = "剔除原因")
    private String rejectReason;

    public BillDrugsVo() {
        this.drugCode = "";
        this.drugName = "";
        this.drugBillCode = "";
        this.drugStd =  "";
        this.drugType =  "";
        this.drugUnit = "";
        this.drugUnitAmt =  "";
        this.drugTotal = "";
        this.drugPay =  "";
        this.medicalType =  "";
        this.selfpayRate =  "";
        this.selfpayAmt =  "";
        this.rejectFlag = "";
        this.rejectReason =  "";
    }

    public String getDrugCode() {
        return drugCode;
    }
    @XmlElement(name = "drug_code")
    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getDrugName() {
        return drugName;
    }
    @XmlElement(name = "drug_name")
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugBillCode() {
        return drugBillCode;
    }
    @XmlElement(name = "drug_bill_code")
    public void setDrugBillCode(String drugBillCode) {
        this.drugBillCode = drugBillCode;
    }

    public String getDrugStd() {
        return drugStd;
    }
    @XmlElement(name = "drug_std")
    public void setDrugStd(String drugStd) {
        this.drugStd = drugStd;
    }

    public String getDrugType() {
        return drugType;
    }
    @XmlElement(name = "drug_type")
    public void setDrugType(String drugType) {
        this.drugType = drugType;
    }

    public String getDrugUnit() {
        return drugUnit;
    }
    @XmlElement(name = "drug_unit")
    public void setDrugUnit(String drugUnit) {
        this.drugUnit = drugUnit;
    }

    public String getDrugUnitAmt() {
        return drugUnitAmt;
    }
    @XmlElement(name = "drug_unit_amt")
    public void setDrugUnitAmt(String drugUnitAmt) {
        this.drugUnitAmt = drugUnitAmt;
    }

    public String getDrugTotal() {
        return drugTotal;
    }
    @XmlElement(name = "drug_total")
    public void setDrugTotal(String drugTotal) {
        this.drugTotal = drugTotal;
    }

    public String getDrugPay() {
        return drugPay;
    }
    @XmlElement(name = "drug_pay")
    public void setDrugPay(String drugPay) {
        this.drugPay = drugPay;
    }

    public String getMedicalType() {
        return medicalType;
    }
    @XmlElement(name = "medical_type")
    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    public String getSelfpayRate() {
        return selfpayRate;
    }
    @XmlElement(name = "selfpay_rate")
    public void setSelfpayRate(String selfpayRate) {
        this.selfpayRate = selfpayRate;
    }

    public String getSelfpayAmt() {
        return selfpayAmt;
    }
    @XmlElement(name = "selfpay_amt")
    public void setSelfpayAmt(String selfpayAmt) {
        this.selfpayAmt = selfpayAmt;
    }

    public String getRejectFlag() {
        return rejectFlag;
    }
    @XmlElement(name = "reject_flag")
    public void setRejectFlag(String rejectFlag) {
        this.rejectFlag = rejectFlag;
    }

    public String getRejectReason() {
        return rejectReason;
    }
    @XmlElement(name = "reject_reason")
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
