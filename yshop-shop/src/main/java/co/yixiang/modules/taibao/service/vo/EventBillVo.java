package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: event_bill
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlRootElement(name = "bill")
@ApiModel(value="收据信息bill", description="收据信息bill")
public class EventBillVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "收据号")
    private String billSno;
    @ApiModelProperty(value = "收据类型（枚举值） （1 住院2 门急诊 3 药店）")
    private String billType;
    @ApiModelProperty(value = "币种(枚举值)")
    private String currency;
    @ApiModelProperty(value = "汇率")
    private String currRate;
    @ApiModelProperty(value = "收据总金额")
    private String billAmt;
    @ApiModelProperty(value = "发票日期")
    private String billDate;
    @ApiModelProperty(value = "统筹支付")
    private String overallpay;
    @ApiModelProperty(value = "附加支付")
    private String attachpay;
    @ApiModelProperty(value = "自费金额")
    private String ownamt;
    @ApiModelProperty(value = "分类自负")
    private String divamt;
    @ApiModelProperty(value = "第三方支付")
    private String thirdpay;

    @ApiModelProperty(value = "汇总项目item")
    private List<BillItemVo> billItemVos;

    @ApiModelProperty(value = "药品信息drug")
    private List<BillDrugsVo> billDrugsVos;

    @ApiModelProperty(value = "其他费用清单other_item")
    private List<BillOtherItemVo> billOtherItemVos;

    public EventBillVo() {
        this.billSno = "";
        this.billType ="";
        this.currency ="";
        this.currRate = "";
        this.billAmt = "";
        this.billDate = "";
        this.overallpay = "";
        this.attachpay = "";
        this.ownamt ="";
        this.divamt = "";
        this.thirdpay ="";
        this.billItemVos =new ArrayList<>();
        this.billDrugsVos =new ArrayList<>();
        this.billOtherItemVos =new ArrayList<>();
    }

    public String getBillSno() {
        return billSno;
    }
    @XmlElement(name = "bill_sno")
    public void setBillSno(String billSno) {
        this.billSno = billSno;
    }

    public String getBillType() {
        return billType;
    }
    @XmlElement(name = "bill_type")
    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getCurrency() {
        return currency;
    }
    @XmlElement(name = "currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrRate() {
        return currRate;
    }
    @XmlElement(name = "curr_rate")
    public void setCurrRate(String currRate) {
        this.currRate = currRate;
    }

    public String getBillAmt() {
        return billAmt;
    }
    @XmlElement(name = "bill_amt")
    public void setBillAmt(String billAmt) {
        this.billAmt = billAmt;
    }

    public String getBillDate() {
        return billDate;
    }
    @XmlElement(name = "bill_date")
    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getOverallpay() {
        return overallpay;
    }
    @XmlElement(name = "overallPay")
    public void setOverallpay(String overallpay) {
        this.overallpay = overallpay;
    }

    public String getAttachpay() {
        return attachpay;
    }
    @XmlElement(name = "attachPay")
    public void setAttachpay(String attachpay) {
        this.attachpay = attachpay;
    }

    public String getOwnamt() {
        return ownamt;
    }
    @XmlElement(name = "ownAmt")
    public void setOwnamt(String ownamt) {
        this.ownamt = ownamt;
    }

    public String getDivamt() {
        return divamt;
    }
    @XmlElement(name = "divAmt")
    public void setDivamt(String divamt) {
        this.divamt = divamt;
    }

    public String getThirdpay() {
        return thirdpay;
    }
    @XmlElement(name = "thirdPay")
    public void setThirdpay(String thirdpay) {
        this.thirdpay = thirdpay;
    }

    public List<BillItemVo> getBillItemVos() {
        return billItemVos;
    }
    @XmlElementWrapper(name = "items")
    @XmlElements({@XmlElement(name = "item", type = BillItemVo.class)})
    public void setBillItemVos(List<BillItemVo> billItemVos) {
        this.billItemVos = billItemVos;
    }

    public List<BillDrugsVo> getBillDrugsVos() {
        return billDrugsVos;
    }
    @XmlElementWrapper(name = "drugs")
    @XmlElements({@XmlElement(name = "drug", type = BillDrugsVo.class)})
    public void setBillDrugsVos(List<BillDrugsVo> billDrugsVos) {
        this.billDrugsVos = billDrugsVos;
    }

    public List<BillOtherItemVo> getBillOtherItemVos() {
        return billOtherItemVos;
    }
    @XmlElementWrapper(name = "other_items")
    @XmlElements({@XmlElement(name = "other_item", type = BillOtherItemVo.class)})
    public void setBillOtherItemVos(List<BillOtherItemVo> billOtherItemVos) {
        this.billOtherItemVos = billOtherItemVos;
    }
}
