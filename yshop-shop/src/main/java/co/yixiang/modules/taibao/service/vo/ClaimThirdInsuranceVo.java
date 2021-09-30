package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_third_insurance
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */

@XmlRootElement(name = "third_insurance")
@XmlType(propOrder = {"insCorp", "poliName", "poliNo"})
@ApiModel(value = "third_insurance", description = "third_insurance")
public class ClaimThirdInsuranceVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "保险公司名称")
    private String insCorp;
    @ApiModelProperty(value = "保单名称")
    private String poliName;
    @ApiModelProperty(value = "保单号")
    private String poliNo;

    public ClaimThirdInsuranceVo() {
        this.insCorp = "";
        this.poliName = "";
        this.poliNo = "";
    }

    public String getInsCorp() {
        return insCorp;
    }

    @XmlElement(name = "ins_corp")
    public void setInsCorp(String insCorp) {
        this.insCorp = insCorp;
    }

    public String getPoliName() {
        return poliName;
    }

    @XmlElement(name = "poli_name")
    public void setPoliName(String poliName) {
        this.poliName = poliName;
    }

    public String getPoliNo() {
        return poliNo;
    }

    @XmlElement(name = "poli_no")
    public void setPoliNo(String poliNo) {
        this.poliNo = poliNo;
    }
}
