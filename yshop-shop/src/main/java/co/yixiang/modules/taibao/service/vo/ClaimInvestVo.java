package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: claim_invest
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "invest")
@ApiModel(value="invest", description="invest")
public class ClaimInvestVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务性质（即时调查|常规调查|复杂疑难调查|反欺诈调查）")
    private String kind;
    @ApiModelProperty(value = "任务子类型（疾病死亡|意外死亡|重大疾病|疾病医疗|意外医疗|残疾失能）")
    private String investtype;
    @ApiModelProperty(value = "调查方式（现场勘查|走访调查|询问调查|住院及费用核实|住院排查|住院补贴监控|追踪调查|综合调查）")
    private String subway;
    @ApiModelProperty(value = "调查要求")
    private String demand;
    @ApiModelProperty(value = "调查结果")
    private String result;
    @ApiModelProperty(value = "申请日期")
    private String applydate;
    @ApiModelProperty(value = "反馈日期")
    private String backdate;
    @ApiModelProperty(value = "调查员")
    private String emp;

    public ClaimInvestVo() {
        this.kind = "";
        this.investtype ="";
        this.subway = "";
        this.demand = "";
        this.result = "";
        this.applydate = "";
        this.backdate ="";
        this.emp = "";
    }

    public String getKind() {
        return kind;
    }
    @XmlElement(name = "kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getInvesttype() {
        return investtype;
    }
    @XmlElement(name = "investtype")
    public void setInvesttype(String investtype) {
        this.investtype = investtype;
    }

    public String getSubway() {
        return subway;
    }
    @XmlElement(name = "subway")
    public void setSubway(String subway) {
        this.subway = subway;
    }

    public String getDemand() {
        return demand;
    }
    @XmlElement(name = "demand")
    public void setDemand(String demand) {
        this.demand = demand;
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
