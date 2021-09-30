package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: claim_benefit_person
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */

@ApiModel(value="claim_info对象")
@XmlType(propOrder={"bftype"
        ,"relationship"
        ,"idtype"
        ,"idno"
        ,"idBegdate"
        ,"idEnddate"
        ,"name"
        ,"sex"
        ,"birthdate"
        ,"mobilephone"
        ,"telephone"
        ,"email"
        ,"addr"
        ,"zip"
        ,"settype"
        ,"banktype"
        ,"banksubtype"
        ,"bankbranch"
        ,"banksubbranch"
        ,"provinceofbank"
        ,"cityofbank"
        ,"acctno"})
@XmlRootElement(name = "benefit_person")
public class ClaimBenefitPersonVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "领款人类型（1个人、2单位）")
    private String bftype;
    @ApiModelProperty(value = "与被保人关系")
    private String relationship;
    @ApiModelProperty(value = "证件类型")
    private String idtype;
    @ApiModelProperty(value = "证件号码")
    private String idno;
    @ApiModelProperty(value = "证件有效期起期")
    private String idBegdate;
    @ApiModelProperty(value = "证件有效期止期")
    private String idEnddate;
    @ApiModelProperty(value = "领款人姓名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "出生日期")
    private String birthdate;
    @ApiModelProperty(value = "移动电话")
    private String mobilephone;
    @ApiModelProperty(value = "固定电话")
    private String telephone;
    @ApiModelProperty(value = "邮箱地址")
    private String email;
    @ApiModelProperty(value = "联系地址")
    private String addr;
    @ApiModelProperty(value = "邮政编码")
    private String zip;
    @ApiModelProperty(value = "支付方式 （1现金2支票3转账）")
    private String settype;
    @ApiModelProperty(value = "银行名称（银行类别）提供枚举值")
    private String banktype;
    @ApiModelProperty(value = "开户行")
    private String banksubtype;
    @ApiModelProperty(value = "分行")
    private String bankbranch;
    @ApiModelProperty(value = "支行")
    private String banksubbranch;
    @ApiModelProperty(value = "银行所在省")
    private String provinceofbank;
    @ApiModelProperty(value = "银行所在市")
    private String cityofbank;
    @ApiModelProperty(value = "银行账号")
    private String acctno;

    public String getBftype() {
        return bftype;
    }
    @XmlElement(name = "bftype")
    public void setBftype(String bftype) {
        this.bftype = bftype;
    }

    public String getRelationship() {
        return relationship;
    }
    @XmlElement(name = "relationship")
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getIdtype() {
        return idtype;
    }
    @XmlElement(name = "idtype")
    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getIdno() {
        return idno;
    }
    @XmlElement(name = "idno")
    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getIdBegdate() {
        return idBegdate;
    }
    @XmlElement(name = "id_begdate")
    public void setIdBegdate(String idBegdate) {
        this.idBegdate = idBegdate;
    }

    public String getIdEnddate() {
        return idEnddate;
    }
    @XmlElement(name = "id_enddate")
    public void setIdEnddate(String idEnddate) {
        this.idEnddate = idEnddate;
    }

    public String getName() {
        return name;
    }
    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }
    @XmlElement(name = "sex")
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }
    @XmlElement(name = "birthdate")
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getMobilephone() {
        return mobilephone;
    }
    @XmlElement(name = "mobilephone")
    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getTelephone() {
        return telephone;
    }
    @XmlElement(name = "telephone")
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }
    @XmlElement(name = "email")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddr() {
        return addr;
    }
    @XmlElement(name = "addr")
    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getZip() {
        return zip;
    }
    @XmlElement(name = "zip")
    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getSettype() {
        return settype;
    }
    @XmlElement(name = "settype")
    public void setSettype(String settype) {
        this.settype = settype;
    }

    public String getBanktype() {
        return banktype;
    }
    @XmlElement(name = "banktype")
    public void setBanktype(String banktype) {
        this.banktype = banktype;
    }

    public String getBanksubtype() {
        return banksubtype;
    }
    @XmlElement(name = "banksubtype")
    public void setBanksubtype(String banksubtype) {
        this.banksubtype = banksubtype;
    }

    public String getBankbranch() {
        return bankbranch;
    }
    @XmlElement(name = "bankbranch")
    public void setBankbranch(String bankbranch) {
        this.bankbranch = bankbranch;
    }

    public String getBanksubbranch() {
        return banksubbranch;
    }
    @XmlElement(name = "banksubbranch")
    public void setBanksubbranch(String banksubbranch) {
        this.banksubbranch = banksubbranch;
    }

    public String getProvinceofbank() {
        return provinceofbank;
    }
    @XmlElement(name = "provinceofbank")
    public void setProvinceofbank(String provinceofbank) {
        this.provinceofbank = provinceofbank;
    }

    public String getCityofbank() {
        return cityofbank;
    }
    @XmlElement(name = "cityofbank")
    public void setCityofbank(String cityofbank) {
        this.cityofbank = cityofbank;
    }

    public String getAcctno() {
        return acctno;
    }
    @XmlElement(name = "acctno")
    public void setAcctno(String acctno) {
        this.acctno = acctno;
    }

    public ClaimBenefitPersonVo() {
        this.bftype = "";
        this.relationship = "";
        this.idtype = "";
        this.idno = "";
        this.idBegdate = "";
        this.idEnddate = "";
        this.name = "";
        this.sex = "";
        this.birthdate = "";
        this.mobilephone ="";
        this.telephone = "";
        this.email ="";
        this.addr = "";
        this.zip ="";
        this.settype ="";
        this.banktype = "";
        this.banksubtype ="";
        this.bankbranch = "";
        this.banksubbranch ="";
        this.provinceofbank ="";
        this.cityofbank ="";
        this.acctno ="";
    }
}
