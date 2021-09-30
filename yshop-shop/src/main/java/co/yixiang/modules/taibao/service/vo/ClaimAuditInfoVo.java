package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_audit_info
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "audit")
@ApiModel(value="audit", description="audit")
public class ClaimAuditInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "保单号")
    private String policyno;
    @ApiModelProperty(value = "险种代码")
    private String classcode;
    @ApiModelProperty(value = "责任代码")
    private String dutycode;
    @ApiModelProperty(value = "赔付结论")
    private String rescode;
    @ApiModelProperty(value = "结论原因")
    private String resreason;

    public ClaimAuditInfoVo() {
        this.policyno = "";
        this.classcode = "";
        this.dutycode = "";
        this.rescode = "";
        this.resreason = "";
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

    public String getRescode() {
        return rescode;
    }
    @XmlElement(name = "resCode")
    public void setRescode(String rescode) {
        this.rescode = rescode;
    }

    public String getResreason() {
        return resreason;
    }
    @XmlElement(name = "resReason")
    public void setResreason(String resreason) {
        this.resreason = resreason;
    }
}
