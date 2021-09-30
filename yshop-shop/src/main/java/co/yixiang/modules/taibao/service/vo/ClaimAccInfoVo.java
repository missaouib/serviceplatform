package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: claim_acc_info
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlType(propOrder={"accDate"
        ,"firstDate"
        ,"accAddrType"
        ,"accSubtype"
        ,"accInfo"
        ,"claimacc"})
@ApiModel(value="claim_info对象", description="claim_info")
public class ClaimAccInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "出险日期")
    private String accDate;
    @ApiModelProperty(value = "初次就诊日期")
    private String firstDate;
    @ApiModelProperty(value = "出险地区  （1. 大陆地区 2. 港澳台 3. 境外不含港澳台 ）")
    private String accAddrType;
    @ApiModelProperty(value = "出险类型 （1意外，2疾病，3其他）")
    private String accSubtype;
    @ApiModelProperty(value = "出险经过")
    private String accInfo;
    @ApiModelProperty(value = "索赔事故性质  （01 身故  02 伤残  03 重大疾病 04 门急诊医疗 05 住院医疗 06 住院补贴 07 女性生育），多个用逗号拼接")
    private List<String> claimacc;

    public String getAccDate() {
        return accDate;
    }

    @XmlElement(name = "acc_date")
    public void setAccDate(String accDate) {
        this.accDate = accDate;
    }

    public String getFirstDate() {
        return firstDate;
    }
    @XmlElement(name = "first_date")
    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getAccAddrType() {
        return accAddrType;
    }
    @XmlElement(name = "acc_addr_type")
    public void setAccAddrType(String accAddrType) {
        this.accAddrType = accAddrType;
    }

    public String getAccSubtype() {
        return accSubtype;
    }
    @XmlElement(name = "acc_subtype")
    public void setAccSubtype(String accSubtype) {
        this.accSubtype = accSubtype;
    }

    public String getAccInfo() {
        return accInfo;
    }
    @XmlElement(name = "acc_info")
    public void setAccInfo(String accInfo) {
        this.accInfo = accInfo;
    }

    public List<String> getClaimacc() {
        return claimacc;
    }
    @XmlElementWrapper(name = "claimacclist")
    public void setClaimacc(List<String> claimacc) {
        this.claimacc = claimacc;
    }

    public ClaimAccInfoVo() {
        this.accDate = "";
        this.firstDate = "";
        this.accAddrType ="";
        this.accSubtype ="";
        this.accInfo = "";
        this.claimacc =new ArrayList<>();
    }
}
