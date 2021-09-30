/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2021-08-16
*/
@Data
public class YxStoreProductGroupDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 父商品sku */
    private String parentProductYiyaobaoSku;

    /** 父商品id */
    private Integer parentProductId;

    /** 商品sku */
    private String productYiyaobaoSku;

    /** 商品id */
    private Integer productId;

    /** 默认销售数量 */
    private Integer num;

    /** 销售单价 */
    private BigDecimal unitPrice;

    private Timestamp createTime;

    private Timestamp updateTime;

    /** 是否删除 (0 否 1是) */
    private Integer isDel;
    @ApiModelProperty(value = "商品门店唯一码")
    private String productUnique;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "规格")
    private String spec;

    @ApiModelProperty(value = "通用名")
    private String commonName = "";

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer = "";

    @ApiModelProperty(value = "单位(如：盒)")
    private String unit = "";

}
