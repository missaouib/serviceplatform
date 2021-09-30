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
* @author zhoujinlai
* @date 2021-06-02
*/
@Data
public class MshDemandListAuditDto implements Serializable {

	private Integer id;

    /** '需求单号', */
    private String demandNo;

    /** 审核状态 */
    private Integer auditStatus;

    /** 保存状态 */
    private Integer saveStatus;

    //关联的订单数据
    private List<MshOrderAuditDto> orderList;
    /**
     * '来源（APP/Wechat/线下）'
     */
    private String source;
    /** 审核人 */
    private String auditName;
    /** 备注 */
    private String remarks;
    /** 取消原因 */
    private String cancelReason;
    /** 药师审核信息 */
    List<MshOrderAuditDto>  mshOrderAuditDtos;

    /** 备注 (临时) */
    private String remarksImp;
    /** 取消原因 （临时）*/
    private String cancelReasonImp;
    /** 审核状态（0：待审核，1：客服审核通过，2：客服审核不通过）（临时）*/
    private Integer auditStatusImp;

}
