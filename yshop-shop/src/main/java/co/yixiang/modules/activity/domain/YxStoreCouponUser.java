/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.activity.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author hupeng
* @date 2020-05-13
*/
@Data
@TableName("yx_store_coupon_user")
public class YxStoreCouponUser implements Serializable {

    /** 优惠券发放记录id */
    @TableId
    private Integer id;


    /** 兑换的项目id */
    private Integer cid;


    /** 优惠券所属用户 */
    private Integer uid;


    /** 优惠券名称 */
    private String couponTitle;


    /** 优惠券的面值 */
    private BigDecimal couponPrice;


    /** 最低消费多少金额可用优惠券 */
    private BigDecimal useMinPrice;


    /** 优惠券创建时间 */
    private Integer addTime;


    /** 优惠券结束时间 */
    private Integer endTime;


    /** 使用时间 */
    private Integer useTime;


    /** 获取方式 */
    private String type;


    /** 状态（0：未使用，1：已使用, 2:已过期） */
    private Integer status;


    /** 是否有效 */
    private Integer isFail;


    public void copy(YxStoreCouponUser source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "众安优惠券唯一标识")
    private String couponNo;

    @ApiModelProperty(value = "优惠券类型 1 满减券 2.折扣券 3.抵用券")
    private Integer couponDetailType;

    @ApiModelProperty(value = "有效起期")
    private Date couponEffectiveTime;

    @ApiModelProperty(value = "有效止期")
    private Date couponExpiryTime;

}
