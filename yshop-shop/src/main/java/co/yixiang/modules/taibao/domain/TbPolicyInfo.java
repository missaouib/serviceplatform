/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
@TableName("tb_policy_info")
public class TbPolicyInfo implements Serializable {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /** id */
    @TableId
    private Long id;


    /** 团体保单号 */
    private String groupPolicyNo;


    /** 保单号 */
    private String policyNo;


    /** （垫付服务请求号+理赔报案号） */
    private String requestCaimReportNo;


    /** 姓名 */
    private String name;


    /** 证件号 */
    private String idNo;


    /** 证件类型 */
    private String idType;


    /** 性别 */
    private String sex;

    /** 被保人出生日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date insuredBirthday;



    /** 产品名称 */
    private String productName;


    /** 产品代码 */
    private String productCode;


    /** 责任名称 */
    private String responsibilityName;


    /** 责任代码 */
    private String responsibilityCode;


    /** 责任余额 */
    private BigDecimal responsibilityTotal;


    /** 免赔余额 */
    private BigDecimal deductibleTotal;


    /** 保单特约 */
    private String policySpecialAppoint;


    /** 个人特约 */
    private String personSpecialAppoint;


    /** 层级特约 */
    private String hierarchySpecialAppoint;


    /** 险种特约 */
    private String insuranceSpecialAppoint;


    /** 既往症特约 */
    private String pastDiseaseSpecialAppoint;


    /** 联系人姓名 */
    private String contactsName;


    /** 联系人电话 */
    private String contactsPhone;


    /** 生效日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date effectDate;


    /** 到期日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expireDate;


    /** 客服备注 */
    private String customerServiceRemarks;


    /** 报案日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date reportDate;


    /** 保单所属机构 */
    private String policyInstitutions;


    /** 承保中支公司 */
    private String underwritChinaBranch;


    /** 承保四级机构 */
    private String underwritLevelFour;


    /** 是否医保投保 */
    private Integer isMedicalInsurance;


    /** 保单类型(新保/续保/转保) */
    private String policyType;


    /** 服务起始日 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date serviceStartDate;


    /** 承保公司 */
    private String underwritingCompany;


    /** 备用字段1 */
    private String spareFieldOne;


    /** 备用字段2 */
    private String spareFieldTwo;


    /** 备用字段3 */
    private String spareFieldThree;


    /** 创建人 */
    private String createBy;


    /** 创建时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 修改人 */
    private String updateBy;


    /** 修改时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    /** 0表示未删除,1表示删除 */
    @TableLogic
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Boolean delFlag;


    /** 拒绝原因 */
    private String reason;


    /** 赔案垫付意见类型(01 同意 02不同意 03已垫付 04终止垫付,当为02/04时，必须给出拒赔原因) */
    private String isAdopt;

    /** 保单状态 (0：待审核 1：生成订单  2：编辑订单  3：查看订单) */
    @TableField(exist = false)
    private String policyStatus;

    /** 保单状态 (0：未结案回传 1：已结案回传) */
    @TableField(exist = false)
    private String status;

    @TableField(exist = false)
    private String orderNo;

    @TableField(exist = false)
    private String orderStatusStr;

    /** 是否已经上传至益药宝平台 0/否， 1/是 */
    @TableField(exist = false)
    private String uploadYiyaobaoFlag;


    public void copy(TbPolicyInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public TbPolicyInfo() {
    }

    public TbPolicyInfo(String[] s) {
        this.groupPolicyNo = s[0];
        this.policyNo = s[1];
        this.requestCaimReportNo = s[2];
        this.name = s[3];
        this.idNo = s[4];
        this.idType = s[5];
        this.sex = s[6];
        try {
            this.insuredBirthday = s[7].equals("")?null:simpleDateFormat.parse(s[7]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.productName = s[8];
        this.productCode = s[9];
        this.responsibilityName = s[10];
        this.responsibilityCode = s[11];
        this.responsibilityTotal = s[12].equals("")?null:new BigDecimal(s[12]);
        this.deductibleTotal =  s[13].equals("")?null:new BigDecimal(s[13]);
        this.policySpecialAppoint = s[14];
        this.personSpecialAppoint = s[15];
        this.hierarchySpecialAppoint = s[16];
        this.insuranceSpecialAppoint = s[17];
        this.pastDiseaseSpecialAppoint = s[18];
        this.contactsName = s[19];
        this.contactsPhone = s[20];
        try {
            this.effectDate = s[21].equals("")?null:simpleDateFormat.parse(s[21]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            this.expireDate = s[22].equals("")?null:simpleDateFormat.parse(s[22]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.customerServiceRemarks = s[23];
        try {
            this.reportDate = s[24].equals("")?null:simpleDateFormat.parse(s[24]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.policyInstitutions = s[25];
        this.underwritChinaBranch = s[26];
        this.underwritLevelFour = s[27];
        this.isMedicalInsurance = s[28].equals("")?0:Integer.parseInt(s[28]);
        this.policyType = s[29];
        try {
            this.serviceStartDate = s[30].equals("")?null:simpleDateFormat.parse(s[30]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.underwritingCompany = s[31];
        this.spareFieldOne = s[32];
        this.spareFieldTwo = s[33];
        this.spareFieldThree = s[34];
    }
}
