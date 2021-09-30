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
public class TbBillItemDto implements Serializable {

    private Long id;

    /** 收据信息ID */
    private String billId;

    /** 账单代码 */
    private String itemCode;

    /** 账单名称 */
    private String itemName;

    /** 账单金额 */
    private BigDecimal payment;

    /** 自费金额 */
    private BigDecimal selfpay;

    /** 分类自负 */
    private BigDecimal classification;

    /** 医保给付金额 */
    private BigDecimal medicalpay;

    /** 第三方给付金额 */
    private BigDecimal thirdpay;

    /** 扣费调整金额 */
    private BigDecimal payback;

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
