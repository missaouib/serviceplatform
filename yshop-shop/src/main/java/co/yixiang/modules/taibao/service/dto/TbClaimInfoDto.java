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
public class TbClaimInfoDto implements Serializable {

    /** id */
    private Long id;

    /** 创建人 */
    private String createBy;

    /** 创建日期 */
    private Timestamp createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新日期 */
    private Timestamp updateTime;

    /** 所属部门 */
    private String sysOrgCode;

    /** 报案号 */
    private String reportno;

    /** 批次号 */
    private String batchno;

    /** 赔案号 */
    private String claimno;

    /** 收单单位代码 */
    private String custmco;

    /** 快递签收时间 */
    private String exptime;

    /** 医保号 */
    private String medicalCode;

    /** 是否接受电子邮件 */
    private String emailAccept;

    /** 收单时间 */
    private Date visitDate;

    /**资料收齐时间*/
    private Date advanceClosingTime;

    /**垫付结案时间*/
    private Date dataCollectionDay;

    /** 复核意见 */
    private String reauditoption;

    /** 复核完成时间 */
    private Date reauditdate;

    /** 挂起类型(多种类型用逗号拼接) */
    private String hangupsign;

    /** 赔案层结论 */
    private String claimrescode;

    /** 审核意见 */
    private String auditoption;

    /** 删除标识 */
    private Boolean delFlag;

    /** 订单编号 */
    private Long orderId;

    /** 图片附件 */
    private String imgUrl;

    /** pdf附件 */
    private String pdfUrl;

    /** 赔案状态  0未完结 1：已完结 */
    private String status;

}
