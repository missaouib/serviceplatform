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
 * @date 2020-12-25
 */
@Data
public class MshDemandDto implements Serializable {
    /**
     * 来源
     */
    private String source;
    /**
     * 需求单号
     */
    private String demandNo;
    /**
     * 建单人
     */
    private String maker;
    /**
     * 公司简称
     */
    private String companyShortName;
    /**
     * Member ID
     */
    private String mermberId;
    /**
     * VIP标志
     */
    private String vipFlag;
    /**
     * 专属客服
     */
    private String customerService;
    /**
     * 专属客服邮箱
     */
    private String customerServiceEmail;
    /**
     * 就诊人姓名
     */
    private String patientName;
    /**
     * 就诊人联系电话
     */
    private String patientPhone;
    /**
     * 就诊人邮箱
     */
    private String patientEmail;
    /**
     * 疾病名称
     */
    private String diseaseName;
    /**
     * 医疗文件对应医院
     */
    private String hospitalName;
    /**
     * 医疗文件开具时间,格式：2021-05-10
     */
    private String documentDate;
    /**
     * 收货人姓名
     */
    private String consigneeName;
    /**
     * 收货人与就诊人关系
     */
    private String relation;
    /**
     * 收货人手机
     */
    private String consigneePhone;
    /**
     * 收货人省份编码
     */
    private String provinceCode;
    /**
     * 收货人城市编码
     */
    private String cityCode;
    /**
     * 收货人区县编码
     */
    private String districtCode;
    /**
     * 收货人省份名称
     */
    private String provinceName;
    /**
     * 收货人城市名称
     */
    private String cityName;
    /**
     * 收货人区县编码
     */
    private String districtName;
    /**
     * 收货地址
     */
    private String address;
    /**
     * 身份证件图片
     */
    private List<String> idCardImages;
    /**
     * 医疗文件图片
     */
    private List<String> medicalDocumentsImages;
    /**
     * 其它文件图片
     */
    private List<String> otherImages;
    /**
     * 带客户签名的药房直付理赔申请书图片
     */
    private List<String> applyImages;

}
