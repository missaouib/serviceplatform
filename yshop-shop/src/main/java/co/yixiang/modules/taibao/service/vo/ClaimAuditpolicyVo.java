package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_auditpolicy
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "auditpolicy")
@XmlType(propOrder={"policyno"
        ,"classcode"
        ,"reinsurancemark","isclause","isrefund","isrenewal"})
@ApiModel(value="auditpolicy", description="auditpolicy")
public class ClaimAuditpolicyVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "保单号")
    private String policyno;
    @ApiModelProperty(value = "险种代码")
    private String classcode;
    @ApiModelProperty(value = "是否终止")
    private String reinsurancemark;
    @ApiModelProperty(value = "是否解约")
    private String isclause;
    @ApiModelProperty(value = "是否退费")
    private String isrefund;
    @ApiModelProperty(value = "是否续保")
    private String isrenewal;

    public ClaimAuditpolicyVo() {
        this.policyno = "";
        this.classcode = "";
        this.reinsurancemark = "";
        this.isclause ="";
        this.isrefund = "";
        this.isrenewal = "";
    }

    public String getPolicyno() {
        return policyno;
    }
    @XmlElement(name = "policyno")
    public void setPolicyno(String policyno) {
        this.policyno = policyno;
    }

    public String getClasscode() {
        return classcode;
    }
    @XmlElement(name = "classcode")
    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getReinsurancemark() {
        return reinsurancemark;
    }
    @XmlElement(name = "reinsuranceMark")
    public void setReinsurancemark(String reinsurancemark) {
        this.reinsurancemark = reinsurancemark;
    }

    public String getIsclause() {
        return isclause;
    }
    @XmlElement(name = "isClause")
    public void setIsclause(String isclause) {
        this.isclause = isclause;
    }

    public String getIsrefund() {
        return isrefund;
    }
    @XmlElement(name = "isRefund")
    public void setIsrefund(String isrefund) {
        this.isrefund = isrefund;
    }

    public String getIsrenewal() {
        return isrenewal;
    }
    @XmlElement(name = "isRenewal")
    public void setIsrenewal(String isrenewal) {
        this.isrenewal = isrenewal;
    }
}
