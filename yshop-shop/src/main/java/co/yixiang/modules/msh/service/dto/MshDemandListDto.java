/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.service.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
* @author cq
* @date 2020-12-25
*/
@Data
public class MshDemandListDto implements Serializable {

	private Integer id;

    /** 患者姓名 */
    private String patientname;

    /** 手机号 */
    private String phone;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 省CODE */
    private String provinceCode;

    /** 市CODE */
    private String cityCode;

    /** 区CODE */
    private String districtCode;

    /** 详细地址 */
    private String detail;

    /** 添加时间 */
    private Timestamp createTime;

    /** 审核状态 */
    private Integer auditStatus;

    /** 保存状态 */
    private Integer saveStatus;

    //关联的订单数据
    private List<MshOrderDto> orderList;

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
    /** 审核人（客服） */
    private String auditName;
    /** 审核时间 （客服） */
    private Timestamp auditTime;
    /** 备注 */
    private String remarks;
    /** 取消原因 */
    private String cancelReason;

    /** 身份证件图片 */
    private  List<String> idCardImages;
    /** 医疗文件图片*/
    private  List<String> medicalDocumentsImages;
    /** 其它文件图片 */
    private  List<String> otherImages;
    /** 备注 (临时) */
    private String remarksImp;
    /** 取消原因 （临时）*/
    private String cancelReasonImp;
    /** 审核状态（0：待审核，1：客服审核通过，2：客服审核不通过）（临时）*/
    private Integer auditStatusImp;

    /** 下发益药包（0 未下发，1已下发）*/
    private Integer lssueStatus;

    /** 最新订单跟新时间*/
    private Date updateTime;
    //关联的未下单数据
//    private List<MshDemandListItemDto> mshDemandListItemList;
}
