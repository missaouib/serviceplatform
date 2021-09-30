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
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListItemDto implements Serializable {

    private Integer id;

    /** 需求单主表ID */
    private Integer demandListId;

    /** 订单状态 */
    private String orderStatus;

    /** 订单状态Str */
    private String orderStatusStr;

    /** 订单药名 */
    private String orderMedName;

    /** 订单号 */
    private String orderId;

    /** 药房名称 */
    private String drugstoreName;

    /** 药房id */
    private Integer drugstoreId;

    /** 外部订单号 */
    private String externalOrderId;

    /** 物流单号 */
    private String logisticsNum;

    /** 物流名称 */
    private String logisticsName;

    /** 物流状态 */
    private String logisticsStatus;

    /** 患者姓名 */
    private String patientname;

    /** 患者ID */
    private String patientId;

    /** 手机号 */
    private String phone;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 详细地址 */
    private String detail;

    /** 药品名称 */
    private String medName;

    /** 药品名称 */
    private String medNameForOrder;

    /** 药品id */
    private Integer medId;

    /** 药品sku编码 */
    private String medSku;

    /** 药品通用名 */
    private String medCommonName;

    /** 药品规格 */
    private String medSpec;

    /** 药品规格 */
    private String medSpecForOrder;

    /** 药品单位 */
    private String medUnit;

    /** 药品生产厂家 */
    private String medManufacturer;

    /** 购药数量 */
    private Integer purchaseQty;

    /** 购药数量 */
    private Integer purchaseQtyForOrder;

    /** 购药数量 */
    private Integer purchaseQtyDemandItem;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 单价 */
    private BigDecimal unitPriceForOrder;

    /** 图片地址 */
    private String pictureUrl;

    /** 处方图片地址 */
    private String picUrl;

    /** 申请表 */
	private String application;

	/** 病例 */
	private String caseUrl;

    /** 创建时间 */
    private Timestamp createTime;

    /** 益药宝主键 */
    private String yiyaobaoId;

    private String memberId;

    /**
     * '来源（APP/Wechat/线下）'
     */
    private String source;
    /** '需求单号', */
    private String demandNo;
    /** '建单人' */
    private String createUser;
    /** 公司简称*/
    private String company;
    /** vip标志'*/
    private String vip;
    /** 专属客服*/
    private String perCustoService;
    /** '专属客服邮箱' */
    private String perCustoServiceEmail;
    /**  '患者邮箱',*/
    private String patientEmail;
    /** '疾病名称'*/
    private String diseaseName;
    /** 医疗文件对应医院*/
    private String fileHospital;
    /** 医疗文件开具日期*/
    private Date fileDate;
    /**  '收货人'*/
    private String receivingName;
    /** 收货人与就诊人关系（本人，助手，朋友，其它，医生，家属，代理人，保险公司管理人员）*/
    private String relationship;
    /** '收货人手机号',*/
    private String receivingPhone;
    /** 审核人 */
    private String auditName;
    /** 备注 */
    private String remarks;
    /** 取消原因 */
    private String cancelReason;

}
