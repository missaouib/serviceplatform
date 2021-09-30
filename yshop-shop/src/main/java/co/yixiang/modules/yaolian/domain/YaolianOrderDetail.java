/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.yaolian.domain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author visa
* @date 2021-03-02
*/
@Data
@TableName("yaolian_order_detail")
public class YaolianOrderDetail implements Serializable {

    /** 主键 */
    @TableId
    private Integer id;


    /** 药联订单号 */
    private String orderId;


    /** 商品id，和连锁商品保持一致 */
    private String drugId;


    /** 商品通用名 */
    private String commonName;


    /** 数量 */
    private String amount;


    /** 商品原价 */
    private String price;


    /** 结算扣率 */
    private String settleDiscountRate;


    /** 商品条形码 */
    private String code;


    /** 1:使用优惠价购买0:未使用到优惠价 */
    private String activityType;

    /** 药品名称 */
    @TableField(exist = false)
    private String storeName;

    /** 生产厂家 */
    @TableField(exist = false)
    private String manufacturer;
    /** 规格 */
    @TableField(exist = false)
    private String spec;

    public void copy(YaolianOrderDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
