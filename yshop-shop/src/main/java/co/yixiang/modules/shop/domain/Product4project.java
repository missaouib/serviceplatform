/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author visa
* @date 2020-11-09
*/
@Data
@TableName("product4project")
public class Product4project implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 项目编号 */
    private String projectNo;


    /** 项目名称 */
    private String projectName;


    /** 药品id */
    private Integer productId;


    /** 药品属性唯一id */
    private String productUniqueId;


    /** 药品数量 */
    private Integer num;


    /** 药品名称 */
    private String productName;


    /** 药店id */
    private Integer storeId;


    /** 药店名称 */
    private String storeName;


    /** 益药宝项目编码 */
    private String yiyaobaoProjectCode;


    /** 零售单价 */
    @TableField(updateStrategy = FieldStrategy.IGNORED )
    private BigDecimal unitPrice;


    /** 最低价 */
    private BigDecimal minPrice;


    /** 最高价 */
    private BigDecimal maxPrice;

    /** 是否删除 0/否 1/是 */
    private Integer isDel;

    private String groupName;

    private String remarks;

    /** 是否固定药品数量 0/否 1/是 0/否 1/是 */
    private Integer isFixNum;

    public void copy(Product4project source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    /** 是否上架 0/否 1/是 */
    public Integer isShow;

    /** 记录生成时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 记录更新时间 */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    @ApiModelProperty(value = "项目结算价")
    @TableField(updateStrategy = FieldStrategy.IGNORED )
    private BigDecimal settlementPrice;

    /** 益药宝sku */
    private String yiyaobaoSku;

    /** 益药宝药店id */
    private String yiyaobaoSellerId;

    /*是否忽略库存 0 否，1是*/
    private Integer ignoreStock;
}
