/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author visa
* @date 2021-01-08
*/
@Data
public class MedCalculatorDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 用户id */
    private Integer uid;

    /** 首次日期 */
    private Timestamp startDate;

    /** 药品名称 */
    private String medName;

    /** 药品数量 */
    private Integer medAmount;

    /** 用药剂量，单位ml */
    private Integer useAmount;

    /** 计算结果，还剩多少天 */
    private Integer result;

    /** 已坚持服用了多少天 */
    private Integer days;

    /** 记录生成时间 */
    private Timestamp createTime;

    /** 记录更新时间 */
    private Timestamp updateTime;

    /** 剩余量，每天定时更新的昨天的剩余量 */
    private Integer leftAmount;

    /** 计算日期 */
    private Timestamp calcuDate;

    /** 每次计算的剩余量 */
    private Integer leftAmountTemp;
}
