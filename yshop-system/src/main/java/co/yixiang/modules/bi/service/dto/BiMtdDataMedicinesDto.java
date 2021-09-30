/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.bi.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2020-10-12
*/
@Data
public class BiMtdDataMedicinesDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 药品名称 */
    private String medName;

    /** 通用名 */
    private String commonName;

    /** 规格 */
    private String spec;

    /** 单位 */
    private String unit;

    /** 厂家 */
    private String manufacturer;

    /** 销售额 */
    private BigDecimal amount;

    /** 销量 */
    private Integer qty;

    /** 日期 2019-01 */
    private String infodate;
}
