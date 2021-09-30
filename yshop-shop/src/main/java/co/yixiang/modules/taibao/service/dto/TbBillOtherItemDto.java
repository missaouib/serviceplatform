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
public class TbBillOtherItemDto implements Serializable {

    private Long id;

    /** 收据信息ID */
    private String billId;

    /** 项目代码 */
    private String itemCode;

    /** 项目名称 */
    private String itemName;

    /** 费用编码 */
    private String itemSubCode;

    /** 费用名称 */
    private String itemSubName;

    /** 医保类别 */
    private String medicalType;

    /** 医保编码 */
    private String medicalCode;

    /** 发生金额 */
    private BigDecimal itemPay;

    /** 自付比例 */
    private BigDecimal selfPayRate;

    /** 自付金额 */
    private BigDecimal selfPayAmt;

    /** 单价 */
    private BigDecimal itemUnitPay;

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
