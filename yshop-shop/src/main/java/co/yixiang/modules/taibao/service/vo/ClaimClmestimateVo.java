package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_clmestimate
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "clmestimate")
@XmlType(propOrder={"policyno"
        ,"classcode"
        ,"gsje"})
@ApiModel(value="clmestimate", description="clmestimate")
public class ClaimClmestimateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "赔付保单号")
    private String policyno;
    @ApiModelProperty(value = "赔付险种代码")
    private String classcode;
    @ApiModelProperty(value = "赔付预估金额")
    private String gsje;

    public ClaimClmestimateVo() {
        this.policyno = "";
        this.classcode = "";
        this.gsje = "";
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

    public String getGsje() {
        return gsje;
    }
    @XmlElement(name = "gsje")
    public void setGsje(String gsje) {
        this.gsje = gsje;
    }
}
