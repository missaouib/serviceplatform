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

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @author visa
* @date 2020-05-29
*/
@Data
public class YxStoreProductAttrValueDto implements Serializable {

    private Integer id;

    /** 商品ID */
    private Integer productId;

    /** 商品属性索引值 (attr_value|attr_value[|....]) */
    private String suk;

    /** 属性对应的库存 */
    private Integer stock;

    /** 销量 */
    private Integer sales;

    /** 属性金额 */
    private BigDecimal price;

    /** 图片 */
    private String image;

    /** 唯一值 */
    private String unique;

    /** 成本价 */
    private BigDecimal cost;

    /** 药店id */
    private Integer storeId;

    /** yx_store_product_attr.id */
    private Integer attrId;

    private String productName;

    private String yiyaobaoSku;

    private String commonName;

    private String spec;

    private String unit;

    private String manufacturer;

    private String yiyaobaoSellerId;
}
