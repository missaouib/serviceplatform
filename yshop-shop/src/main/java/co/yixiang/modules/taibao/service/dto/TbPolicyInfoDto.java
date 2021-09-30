/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taibao.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
public class TbPolicyInfoDto implements Serializable {
    /** id */
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
    private Date effectDate;


    /** 到期日期 */
    private Date expireDate;


    /** 客服备注 */
    private String customerServiceRemarks;


    /** 报案日期 */
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
    private Timestamp createTime;


    /** 修改人 */
    private String updateBy;


    /** 修改时间 */
    private Timestamp updateTime;


    /** 0表示未删除,1表示删除 */
    private Boolean delFlag;


    /** 拒绝原因 */
    private String reason;


    /** 赔案垫付意见类型(01 同意 02不同意 03已垫付 04终止垫付,当为02/04时，必须给出拒赔原因) */
    private String isAdopt;

    /** 保单状态 (0：待审核 1：生成订单  2：编辑订单  3：查看订单) */
    private String  policyStatus;

    /** 保单状态 (0：未结案回传 1：已结案回传) */
    private String status;

    private String orderNo;

    private String orderStatusStr;

    /** 是否已经上传至益药宝平台 /否， /是 */
    private String uploadYiyaobaoFlag;

}
