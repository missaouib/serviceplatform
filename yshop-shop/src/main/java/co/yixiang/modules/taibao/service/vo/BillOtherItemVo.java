package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: bill_other_item
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name="other_item")
@ApiModel(value="其他费用清单other_item", description="其他费用清单other_item")
public class BillOtherItemVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目代码")
    private String itemCode;
    @ApiModelProperty(value = "项目名称")
    private String itemName;
    @ApiModelProperty(value = "费用编码")
    private String itemSubCode;
    @ApiModelProperty(value = "费用名称")
    private String itemSubName;
    @ApiModelProperty(value = "医保类别")
    private String medicalType;
    @ApiModelProperty(value = "医保编码")
    private String medicalCode;
    @ApiModelProperty(value = "发生金额")
    private String itemPay;
    @ApiModelProperty(value = "自付比例")
    private String selfPayRate;
    @ApiModelProperty(value = "自付金额")
    private String selfPayAmt;
    @ApiModelProperty(value = "单价")
    private String itemUnitPay;
    @ApiModelProperty(value = "是否剔除")
    private String rejectFlag;
    @ApiModelProperty(value = "剔除原因")
    private String rejectReason;

    public BillOtherItemVo() {
        this.itemCode = "";
        this.itemName = "";
        this.itemSubCode = "";
        this.itemSubName = "";
        this.medicalType ="";
        this.medicalCode = "";
        this.itemPay = "";
        this.selfPayRate = "";
        this.selfPayAmt = "";
        this.itemUnitPay = "";
        this.rejectFlag ="";
        this.rejectReason ="";
    }

    public String getItemCode() {
        return itemCode;
    }
    @XmlElement(name = "item_code")
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }
    @XmlElement(name = "item_name")
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSubCode() {
        return itemSubCode;
    }
    @XmlElement(name = "item_sub_code")
    public void setItemSubCode(String itemSubCode) {
        this.itemSubCode = itemSubCode;
    }

    public String getItemSubName() {
        return itemSubName;
    }
    @XmlElement(name = "item_sub_name")
    public void setItemSubName(String itemSubName) {
        this.itemSubName = itemSubName;
    }

    public String getMedicalType() {
        return medicalType;
    }
    @XmlElement(name = "medical_type")
    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    public String getMedicalCode() {
        return medicalCode;
    }
    @XmlElement(name = "medical_code")
    public void setMedicalCode(String medicalCode) {
        this.medicalCode = medicalCode;
    }

    public String getItemPay() {
        return itemPay;
    }
    @XmlElement(name = "item_pay")
    public void setItemPay(String itemPay) {
        this.itemPay = itemPay;
    }

    public String getSelfPayRate() {
        return selfPayRate;
    }
    @XmlElement(name = "self_pay_rate")
    public void setSelfPayRate(String selfPayRate) {
        this.selfPayRate = selfPayRate;
    }

    public String getSelfPayAmt() {
        return selfPayAmt;
    }
    @XmlElement(name = "self_pay_amt")
    public void setSelfPayAmt(String selfPayAmt) {
        this.selfPayAmt = selfPayAmt;
    }

    public String getItemUnitPay() {
        return itemUnitPay;
    }
    @XmlElement(name = "item_unit_pay")
    public void setItemUnitPay(String itemUnitPay) {
        this.itemUnitPay = itemUnitPay;
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
