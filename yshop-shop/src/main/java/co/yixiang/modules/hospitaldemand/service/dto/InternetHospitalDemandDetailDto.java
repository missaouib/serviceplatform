/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.hospitaldemand.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visazhou
* @date 2021-01-22
*/
@Data
public class InternetHospitalDemandDetailDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 需求单id */
    private Integer demandId;

    /** 处方编号 */
    private String prescriptionCode;

    /** 药品编码 */
    private String drugCode;

    /** 药品名称 */
    private String drugName;

    /** 药品数量 */
    private Integer drugNum;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    /** 药品唯一码 */
    private String productAttrUnique;

    /** 益药宝sku */
    private String yiyaobaoSku;
}
