/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2021-08-16
*/
@Data
@TableName("yx_store_product_group")
public class YxStoreProductGroup implements Serializable {

    /** 主键 */
    @TableId
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

    /** 是否删除 */
    private Integer isDel;

    /** 商品门店唯一码 */
    private String productUnique;


    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    private String storeName;

    private String commonName;

    public void copy(YxStoreProductGroup source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
