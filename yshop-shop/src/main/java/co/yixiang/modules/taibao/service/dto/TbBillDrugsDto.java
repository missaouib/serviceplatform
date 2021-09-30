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

/**
* @author zhoujinlai
* @date 2021-04-30
*/
@Data
public class TbBillDrugsDto implements Serializable {

    private Long id;

    /** 收据信息ID */
    private String billId;

    /** 药品代码 */
    private String drugCode;

    /** 药品名称 */
    private String drugName;

    /** 对应账单项代码 */
    private String drugBillCode;

    /** 规格 */
    private String drugStd;

    /** 剂型 */
    private String drugType;

    /** 单位 */
    private String drugUnit;

    /** 单价 */
    private BigDecimal drugUnitAmt;

    /** 数量 */
    private BigDecimal drugTotal;

    /** 发生金额 */
    private BigDecimal drugPay;

    /** 医保类别 */
    private BigDecimal medicalType;

    /** 自付比例 */
    private BigDecimal selfpayRate;

    /** 自付金额 */
    private BigDecimal selfpayAmt;

    /** 是否剔除 */
    private String rejectFlag;

    /** 剔除原因 */
    private String rejectReason;

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
