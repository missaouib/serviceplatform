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

import java.util.Date;
import java.util.List;
import co.yixiang.annotation.Query;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListQueryCriteria{
    private String demandNo;
    /** 需求单状态
     0：待审核
     1：客服审核通过
     2：客服审核不通过
     3：药剂师审核
     4：药剂师审核不通过
     5：驳回
     6：取消
      */
    private List<String> demandStatus;
    /** 订单状态
     0：待审核
     1：审核通过
     2：审核不通过
     3：已发货
     4：已完成
     5：已退货
     6：驳回
     */
    private String orderStatus;

    /** 选择申请起期 */
    private String startTime;

    /** 选择申请止期 */
    private String endTime;

    /** 公司简称 */
    private String company;

    /** 申请人 */
    private String applicant;

    /** 专属客服 */
    private String custoService ;

    /** 专属客服邮箱 */
    private String custoServiceEmail;

    /** 诊人姓名 模糊 */
    private String patientname;

    /**手机号 精确 */
    private String phone;

    /** vip */
    private String vip;

    /** memberId */
    private String memberId;
    /**
     * 是否下发
     */
    private Integer lssueStatus;

    /**
     * 排序
     */
    private String orderBy;
}
