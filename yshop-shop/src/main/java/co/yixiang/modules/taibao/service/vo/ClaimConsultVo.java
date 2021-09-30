package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_consult
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "consult")
@ApiModel(value="consult", description="consult")
public class ClaimConsultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "协谈理赔结论（通融|和解）")
    private String rescode;
    @ApiModelProperty(value = "协谈申请")
    private String apply;
    @ApiModelProperty(value = "协谈结果")
    private String result;
    @ApiModelProperty(value = "申请日期")
    private String applydate;
    @ApiModelProperty(value = "反馈日期")
    private String backdate;
    @ApiModelProperty(value = "协谈员")
    private String emp;

    public ClaimConsultVo() {
        this.rescode = rescode;
        this.apply = apply;
        this.result = result;
        this.applydate = applydate;
        this.backdate = backdate;
        this.emp = emp;
    }

    public String getRescode() {
        return rescode;
    }
    @XmlElement(name = "rescode")
    public void setRescode(String rescode) {
        this.rescode = rescode;
    }

    public String getApply() {
        return apply;
    }
    @XmlElement(name = "apply")
    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getResult() {
        return result;
    }
    @XmlElement(name = "result")
    public void setResult(String result) {
        this.result = result;
    }

    public String getApplydate() {
        return applydate;
    }
    @XmlElement(name = "applydate")
    public void setApplydate(String applydate) {
        this.applydate = applydate;
    }

    public String getBackdate() {
        return backdate;
    }
    @XmlElement(name = "backdate")
    public void setBackdate(String backdate) {
        this.backdate = backdate;
    }

    public String getEmp() {
        return emp;
    }
    @XmlElement(name = "emp")
    public void setEmp(String emp) {
        this.emp = emp;
    }
}
