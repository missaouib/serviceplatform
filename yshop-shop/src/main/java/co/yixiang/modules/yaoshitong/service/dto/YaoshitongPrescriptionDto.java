/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.Date;

/**
* @author visa
* @date 2020-07-21
*/
@Data
public class YaoshitongPrescriptionDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 处方编号 */
    private String prescriptionNo;

    /** 患者id */
    private Integer patientId;

    /** 药师id */
    private String pharmacistId;

    /** 是否处方药 */
    private String isPrescription;

    /** 处方日期 */
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date prescriptionDate;

    /** 处方医院 */
    private String hospitalName;

    /** 处方医生 */
    private String doctorName;

    /** 科室 */
    private String departName;

    /** 诊断 */
    private String diagnosis;

    /** 药品明细 */
    private String medDetail;

    /** 生成时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 处方图片 */
    private String imagePath;

    @ApiModelProperty(value = "患者姓名")
    private String name;

    @ApiModelProperty(value = "患者手机号")
    private String phone;

    @ApiModelProperty(value = "患者年龄")
    private Integer age;
    @ApiModelProperty(value = "药师名称")
    private String pharmacistName;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "性别")
    private String sex;

    /*出生年月*/
    @ApiModelProperty(value = "出生年月")
    private String birth;

    /*药师对应的药房名称*/
    @ApiModelProperty(value = "药师对应的药房名称")
    private String StoreName;

    @ApiModelProperty(value = "药师手机号")
    private String pharmacistPhone;
}
