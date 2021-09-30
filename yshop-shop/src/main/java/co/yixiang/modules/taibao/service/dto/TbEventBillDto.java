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
public class TbEventBillDto implements Serializable {

    private Long id;

    /** 事件信息Id */
    private String eventId;

    /** 收据号 */
    private String billSno;

    /** 收据类型（枚举值） （1 住院2 门急诊 3 药店） */
    private String billType;

    /** 币种(枚举值) */
    private String currency;

    /** 汇率 */
    private String currRate;

    /** 收据总金额 */
    private String billAmt;

    /** 发票日期 */
    private Date billDate;

    /** 统筹支付 */
    private String overallpay;

    /** 附加支付 */
    private String attachpay;

    /** 自费金额 */
    private String ownamt;

    /** 分类自负 */
    private String divamt;

    /** 第三方支付 */
    private String thirdpay;

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
