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
* @date 2020-10-21
*/
@Data
public class YaoshitongRepurchaseReminderDto implements Serializable {

    private Integer id;

    /** 姓名 */
    private String name;

    /** 电话 */
    private String phone;

    /** 药房名称 */
    private String drugstoreName;

    /** 药房id */
    private Integer drugstoreId;

    /** 上次购买日期 */
    private Timestamp lastPurchaseDate;

    /** 下次购买日期 */
    private Timestamp nextPurchaseDate;

    /** 药品名称 */
    private String medName;

    /** 药品id */
    private Integer medId;

    /** 药品sku编码 */
    private String medSku;

    /** 药品通用名 */
    private String medCommonName;

    /** 药品规格 */
    private String medSpec;

    /** 药品单位 */
    private String medUnit;

    /** 药品生产厂家 */
    private String medManufacturer;

    /** 状态 */
    private String status;

    /** 首次购药日期 */
    private Timestamp firstPurchaseDate;

    /** 购药次数 */
    private Integer purchaseTimes;

    /** 总计购药数量 */
    private Integer purchaseQty;

    /** 上次购药数量 */
    private Integer lastPurchasseQty;

    /** 用药周期 */
    private Integer medCycle;

    private Timestamp createTime;

    private Timestamp updateTime;

    /** 药品图片 */
    private String image;
}
