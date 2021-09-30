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

import co.yixiang.modules.msh.domain.MshPatientListFile;

/**
* @author cq
* @date 2020-12-18
*/
@Data
public class MshPatientInformationDto implements Serializable {

    /** 患者ID */
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
    private Timestamp addTime;

    private String memberId;
    /** 患者附件表 */
    private List<MshPatientListFile> mshPatientListFileList;

    /**  '收货人'*/
    private String receivingName;
    /** 收货人与就诊人关系（本人，助手，朋友，其它，医生，家属，代理人，保险公司管理人员）*/
    private String relationship;
    /** '收货人手机号',*/
    private String receivingPhone;

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
}
