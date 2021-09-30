/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.service.dto;

import co.yixiang.modules.hospitaldemand.domain.InternetHospitalDemandDetail;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @author visa
* @date 2021-01-05
*/
@Data
public class InternetHospitalDemandDto implements Serializable {

    private Integer id;

    /** 患者姓名 */
    private String patientName;

    /** 患者手机号 */
    private String phone;

    /** uid */
    private Integer uid;

    /** 卡类型 */
    private String cardType;

    /** 卡号 */
    private String cardNumber;

    /** 原始订单号 */
    private String orderNumber;

    /** 项目名称 */
    private String projectCode;

    private String prescriptionPdf;

    private String image;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    /** 处方时间 yyyyMMddHHmmss  */
    private String timeCreate;

    /** 处方编码 */
    private String prescriptionCode;

    /** 患者身份证号 */
    private String patientIdCard;

    /** 医院名称 */
    private String hospitalName;

    /** 附加字段 */
    private String attrs;

    /** 是否已经生成订单 0/否 1/是 */
    private Integer isUse;

    /** 订单号 */
    private String orderId;

    /** 缴费通知 */
    private Integer payNoticeFlag;

    /** 缴费通知时间 */
    private Timestamp payNoticeDate;

    /** 退费通知 */
    private Integer refundNoticeFlag;

    /** 退费通知时间 */
    private Timestamp refundNoticeDate;

    /** 物流通知 */
    private Integer expressNoticeFlag;

    /** 物流通知时间 */
    private Timestamp expressNoticeDate;

    /** 签收通知 */
    private Integer signNoticeFlag;

    /** 签收通知时间 */
    private Timestamp signNoticeDate;

    @TableField(exist = false)
    private List<InternetHospitalDemandDetail> drugs;
}
