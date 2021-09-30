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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import co.yixiang.modules.msh.domain.MshOrderItem;

import java.io.Serializable;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshOrderDto implements Serializable {

    private Integer id;

    /** 需求单主表ID */
    private Integer demandListId;

    /** 订单状态 */
    private String orderStatus;

    /** 订单状态Str */
    private String orderStatusStr;

    /** 药房名称 */
    private String drugstoreName;

    /** 药房id */
    private Integer drugstoreId;

    /** 创建时间 */
    private Timestamp createTime;

    /** 物流单号 */
    private String logisticsNum;

    /** 物流状态 */
    private String logisticsStatus;

    /** 外部订单号 */
    private String externalOrderId;

    /** 益药宝主键 */
    private String yiyaobaoId;

    /** 审核原因 */
    private String auditReasons;
    /** 审核人 */
    private String  auditName;
    /** 审核时间 */
    private Date auditTime;

    private String createUser;

    /** 发货日期 */
    private Date shippingDate;

    private List<MshDemandListItemDto> orderItemList;
}
