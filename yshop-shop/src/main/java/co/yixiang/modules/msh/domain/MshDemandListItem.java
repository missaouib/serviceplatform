/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.msh.domain;
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
* @author cq
* @date 2020-12-25
*/
@Data
@TableName("msh_demand_list_item")
public class MshDemandListItem implements Serializable {

    @TableId
    private Integer id;


    /** 需求单主表ID */
    @NotNull
    private Integer demandListId;


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


    /** 购药数量 */
    private Integer purchaseQty;


    /** 单价 */
    private BigDecimal unitPrice;


    /** 图片地址 */
    private String pictureUrl;


    /** 创建时间 */
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    public void copy(MshDemandListItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
