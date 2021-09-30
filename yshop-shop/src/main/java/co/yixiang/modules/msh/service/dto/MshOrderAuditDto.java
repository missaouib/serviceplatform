/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshOrderAuditDto implements Serializable {

    private Integer id;
    /** 需求单主表ID */
    private Integer demandListId;
    /** 外部订单号 */
    private String externalOrderId;
    /** 订单状态 */
    private String orderStatus;
    /** 订单状态Str */
    private String orderStatusStr;
    /** 审核原因 */
    private String auditReasons;
    /** 审核人 */
    private String  auditName;
}
