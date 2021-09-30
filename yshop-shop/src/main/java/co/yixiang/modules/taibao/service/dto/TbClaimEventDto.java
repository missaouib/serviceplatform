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
import java.sql.Timestamp;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
public class TbClaimEventDto implements Serializable {

    private Long id;

    /** 赔案信息Id */
    private String claimInfoId;

    /** 索赔事故性质（枚举值） */
    private String claimacc;

    /** 疾病诊断 */
    private String illcode;

    /** 就诊日期 */
    private Date caredate;

    /** 入院日期 */
    private Date indate;

    /** 出院日期 */
    private Date outdate;

    /** 住院天数 */
    private Integer indays;

    /** 身故日期 */
    private Date deadDate;

    /** 伤残鉴定日期 */
    private Date disableDate;

    /** 就诊医院代码 */
    private String hospitalInfo;

    /** 就诊医院名称 */
    private String clinical;

    /** 主治医生姓名 */
    private String doctor;

    /** 手术代码 */
    private String surgery;

    /** 重疾代码 */
    private String critical;

    /** 医保类型 */
    private String medicalType;

    /** 是否转诊 */
    private String referral;

    /** 转来医院名称 */
    private String referralHosp;

    /** 科室名称 */
    private String referralClinical;

    /** 医生姓名 */
    private String referralDoctor;

    /** 预产期 */
    private String edc;

    /** 预期是否单胎 */
    private String issingle;

    /** 是否使用妊娠辅助医疗或人工授精 */
    private String isuseOther;

    /** 具体情况 */
    private String conditionInfo;

    /** 收据总数 */
    private Integer billCnt;

    /** 事件审核结论 */
    private String auditconclusion;

    /** 事件审核意见 */
    private String auditoption;

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

}
