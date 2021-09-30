package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: insurance_person
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlType(propOrder={"name"
        ,"sex"
        ,"idtype"
        ,"idno"
        ,"idBegdate"
        ,"idEnddate"
        ,"job"
        ,"organization"
        ,"birthdate"})
@ApiModel(value="claim_info对象", description="claim_info")
public class InsurancePersonVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "证件类别")
    private String idtype;
    @ApiModelProperty(value = "证件号码")
    private String idno;
    @ApiModelProperty(value = "证件有效期起期")
    private String idBegdate;
    @ApiModelProperty(value = "证件有效期止期")
    private String idEnddate;
    @ApiModelProperty(value = "职业")
    private String job;
    @ApiModelProperty(value = "单位")
    private String organization;
    @ApiModelProperty(value = "出生日期")
    private String birthdate;

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

    public String getJob() {
        return job;
    }

    @XmlElement(name = "job")
    public void setJob(String job) {
        this.job = job;
    }

    public String getOrganization() {
        return organization;
    }

    @XmlElement(name = "organization")
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getBirthdate() {
        return birthdate;
    }

    @XmlElement(name = "birthdate")
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public InsurancePersonVo() {
        this.name = "";
        this.sex = "";
        this.idtype = "";
        this.idno = "";
        this.idBegdate = "";
        this.idEnddate = "";
        this.job = "";
        this.organization = "";
        this.birthdate = "";
    }
}
