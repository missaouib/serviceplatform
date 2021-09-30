package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: bill_item
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "item")
@ApiModel(value="item", description="item")
public class BillItemVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账单代码")
    private String itemCode;
    @ApiModelProperty(value = "账单名称")
    private String itemName;
    @ApiModelProperty(value = "账单金额")
    private String payment;
    @ApiModelProperty(value = "自费金额")
    private String selfpay;
    @ApiModelProperty(value = "分类自负")
    private String classification;
    @ApiModelProperty(value = "医保给付金额")
    private String medicalpay;
    @ApiModelProperty(value = "第三方给付金额")
    private String thirdpay;
    @ApiModelProperty(value = "扣费调整金额")
    private String payback;

    public BillItemVo() {
        this.itemCode = "";
        this.itemName = "";
        this.payment = "";
        this.selfpay = "";
        this.classification = "";
        this.medicalpay = "";
        this.thirdpay = "";
        this.payback = "";
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

    public String getPayment() {
        return payment;
    }
    @XmlElement(name = "payment")
    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getSelfpay() {
        return selfpay;
    }
    @XmlElement(name = "selfpay")
    public void setSelfpay(String selfpay) {
        this.selfpay = selfpay;
    }

    public String getClassification() {
        return classification;
    }
    @XmlElement(name = "classification")
    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getMedicalpay() {
        return medicalpay;
    }
    @XmlElement(name = "medicalpay")
    public void setMedicalpay(String medicalpay) {
        this.medicalpay = medicalpay;
    }

    public String getThirdpay() {
        return thirdpay;
    }
    @XmlElement(name = "thirdpay")
    public void setThirdpay(String thirdpay) {
        this.thirdpay = thirdpay;
    }

    public String getPayback() {
        return payback;
    }
    @XmlElement(name = "payback")
    public void setPayback(String payback) {
        this.payback = payback;
    }
}
