/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaoshitong.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author visa
* @date 2020-07-13
*/
@Data
public class YaoshitongPatientRelationDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 药师id */
    private String pharmacistId;

    /** 患者id */
    private Integer patientId;

    private Timestamp createTime;

    private Timestamp updateTime;

    /** 是否删除 */
    private Integer isDel;
}
