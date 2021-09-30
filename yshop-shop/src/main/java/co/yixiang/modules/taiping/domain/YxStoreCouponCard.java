/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.taiping.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author visa
* @date 2020-12-10
*/
@Data
@TableName("yx_store_coupon_card")
public class YxStoreCouponCard implements Serializable {

    /** 优惠券发放记录id */
    @TableId
    private Integer id;


    /** 兑换的项目id */
    @NotNull
    private Integer cid;


    /** 优惠券所属卡号 */
    @NotBlank
    private String cardNumber;


    /** 优惠券名称 */
    @NotBlank
    private String couponTitle;


    /** 优惠券的面值 */
    @NotNull
    private BigDecimal couponPrice;


    /** 最低消费多少金额可用优惠券 */
    @NotNull
    private BigDecimal useMinPrice;


    /** 优惠券创建时间 */
    @NotNull
    private Integer addTime;


    /** 优惠券结束时间 */
    @NotNull
    private Integer endTime;


    /** 使用时间 */
    @NotNull
    private Integer useTime;


    /** 获取方式 */
    @NotBlank
    private String type;


    /** 状态（0：未使用，1：已使用, 2:已过期） */
    @NotNull
    private Integer status;


    /** 是否有效 */
    @NotNull
    private Integer isFail;


    /** 实际抵扣金额 */
    private BigDecimal factDeductionAmount;


    /** 最高抵扣金额 */
    private BigDecimal maxDeductionAmount;


    /** 折扣率 */
    private BigDecimal deductionRate;


    /** 是否全场通用 1 表示全场通用 0表示否 */
    private Integer couponType;


    public void copy(YxStoreCouponCard source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
