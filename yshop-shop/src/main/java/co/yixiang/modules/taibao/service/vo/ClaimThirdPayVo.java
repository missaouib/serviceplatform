package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_third_pay
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "third_pay")
@XmlType(propOrder = {"payCorp", "payAmount"})
@ApiModel(value="third_pay", description="third_pay")
public class ClaimThirdPayVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "索赔保险公司或单位")
    private String payCorp;
    @ApiModelProperty(value = "赔付金额")
    private String payAmount;

    public ClaimThirdPayVo() {
        this.payCorp = "";
        this.payAmount = "";
    }

    public String getPayCorp() {
        return payCorp;
    }
    @XmlElement(name = "pay_corp")
    public void setPayCorp(String payCorp) {
        this.payCorp = payCorp;
    }

    public String getPayAmount() {
        return payAmount;
    }
    @XmlElement(name = "pay_amount")
    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }
}
