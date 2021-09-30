/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
* @author visa
* @date 2020-05-29
*/
@Data
@TableName("yx_store_product_attr_value")
public class YxStoreProductAttrValue implements Serializable {

    @TableId
    private Integer id;


    /** 商品ID */
    @NotNull
    private Integer productId;


    /** 商品属性索引值 (attr_value|attr_value[|....]) */
    private String suk;


    /** 属性对应的库存 */
    private Integer stock;


    /** 销量 */
    private Integer sales;


    /** 属性金额 */
    @NotNull
    private BigDecimal price;


    /** 图片 */
    private String image;


    /** 唯一值 */
@TableField(value = "`unique`")
private String unique;


    /** 成本价 */
    private BigDecimal cost;


    /** 药店id */
    private Integer storeId;


    /** yx_store_product_attr.id */
    private Integer attrId;


    public void copy(YxStoreProductAttrValue source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String commonName;

    @ApiModelProperty(value = "是否删除 0/否 1/是")
    private Integer isDel;

    private String yiyaobaoSku;

    private String yiyaobaoSellerId;

    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    private String medPartnerMedicineId;
}
