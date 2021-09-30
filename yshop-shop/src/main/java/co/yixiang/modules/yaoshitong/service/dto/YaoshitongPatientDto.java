/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2020-07-21
*/
@Data
public class YaoshitongPatientDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 名称 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 性别 */
    private String sex;

    /** 年龄 */
    private Integer age;

    /** 身份证号 */
    private String idCard;

    /** 社保卡号 */
    private String socialCard;

    /** 病史 */
    private String medicalHistory;

    /** 诊断史 */
    private String diagnosisHistory;

    /** 用药史 */
    private String medicationHistory;

    /** 药物过敏 */
    private String drugAllergy;

    /** 用药禁忌 */
    private String drugContraindications;

    /** 生成时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 更新人 */
    private String updateUser;

    /** 出生年月 */
    private String birth;

    /** 地址 */
    private String address;
}
