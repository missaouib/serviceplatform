package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_claim_pay
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "claim_pay")
@ApiModel(value="claim_pay", description="claim_pay")
public class ClaimClaimPayVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "保单号")
    private String policyno;
    @ApiModelProperty(value = "险种代码")
    private String classcode;
    @ApiModelProperty(value = "责任代码")
    private String dutycode;
    @ApiModelProperty(value = "赔付金额")
    private String claimpay;
    @ApiModelProperty(value = "垫付金额")
    private String advancepayment;
    @ApiModelProperty(value = "剩余年免赔额")
    private String remaindeduction;

    public ClaimClaimPayVo() {
        this.policyno = "";
        this.classcode = "";
        this.dutycode = "";
        this.claimpay = "";
        this.advancepayment = "";
        this.remaindeduction =  "";
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

    public String getDutycode() {
        return dutycode;
    }
    @XmlElement(name = "dutycode")
    public void setDutycode(String dutycode) {
        this.dutycode = dutycode;
    }

    public String getClaimpay() {
        return claimpay;
    }
    @XmlElement(name = "claimpay")
    public void setClaimpay(String claimpay) {
        this.claimpay = claimpay;
    }

    public String getAdvancepayment() {
        return advancepayment;
    }
    @XmlElement(name = "advancePayment")
    public void setAdvancepayment(String advancepayment) {
        this.advancepayment = advancepayment;
    }

    public String getRemaindeduction() {
        return remaindeduction;
    }
    @XmlElement(name = "remainDeduction")
    public void setRemaindeduction(String remaindeduction) {
        this.remaindeduction = remaindeduction;
    }
}
