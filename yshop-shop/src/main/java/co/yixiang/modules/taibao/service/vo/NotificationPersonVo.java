package co.yixiang.modules.taibao.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Description: notification_person
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@XmlType(propOrder={"relationship"
        ,"noticeDate"
        ,"idtype"
        ,"idno"
        ,"idBegdate"
        ,"idEnddate"
        ,"name"
        ,"sex"
        ,"mobilephone"
        ,"telephone"
        ,"email"
        ,"addr"
        ,"zip"})
@ApiModel(value="claim_info对象", description="claim_info")
public class NotificationPersonVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "与被保人关系")
    private String relationship;
    @ApiModelProperty(value = "报案日期")
    private String noticeDate;
    @ApiModelProperty(value = "证件类别")
    private String idtype;
    @ApiModelProperty(value = "证件号码")
    private String idno;
    @ApiModelProperty(value = "证件有效起期")
    private String idBegdate;
    @ApiModelProperty(value = "证件有效止期")
    private String idEnddate;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
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

    public String getRelationship() {
        return relationship;
    }

    @XmlElement(name = "relationship")
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    @XmlElement(name = "notice_date")
    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
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

    public NotificationPersonVo() {
        this.relationship = "";
        this.noticeDate =  "";
        this.idtype =  "";
        this.idno =  "";
        this.idBegdate =  "";
        this.idEnddate =  "";
        this.name = "";
        this.sex =  "";
        this.mobilephone =  "";
        this.telephone =  "";
        this.email = "";
        this.addr =  "";
        this.zip =  "";
    }
}
