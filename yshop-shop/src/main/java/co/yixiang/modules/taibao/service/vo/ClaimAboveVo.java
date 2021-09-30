package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_above
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "above")
@ApiModel(value="above", description="above")
public class ClaimAboveVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "超额件审核结论（赔付|拒赔|撤案）")
    private String rescode;
    @ApiModelProperty(value = "申请日期")
    private String applydate;
    @ApiModelProperty(value = "反馈日期")
    private String backdate;
    @ApiModelProperty(value = "审核员")
    private String emp;

    public ClaimAboveVo() {
        this.rescode = "";
        this.applydate = "";
        this.backdate = "";
        this.emp = "";
    }

    public String getRescode() {
        return rescode;
    }
    @XmlElement(name = "rescode")
    public void setRescode(String rescode) {
        this.rescode = rescode;
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
