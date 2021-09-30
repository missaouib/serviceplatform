package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 优惠券发放记录表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-12-10
 */
@Data
@ApiModel(value="YxStoreCouponCardQueryVo对象", description="优惠券发放记录表查询参数")
public class YxStoreCouponCardQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "优惠券发放记录id")
private Integer id;

@ApiModelProperty(value = "兑换的项目id")
private Integer cid;

@ApiModelProperty(value = "优惠券所属卡号")
private String cardNumber;

@ApiModelProperty(value = "优惠券名称")
private String couponTitle;

@ApiModelProperty(value = "优惠券的面值")
private BigDecimal couponPrice;

@ApiModelProperty(value = "最低消费多少金额可用优惠券")
private BigDecimal useMinPrice;

@ApiModelProperty(value = "优惠券创建时间")
private Integer addTime;

@ApiModelProperty(value = "优惠券结束时间")
private Integer endTime;

@ApiModelProperty(value = "使用时间")
private Integer useTime;

@ApiModelProperty(value = "获取方式")
private String type;

@ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已过期）")
private Boolean status;

@ApiModelProperty(value = "是否有效")
private Boolean isFail;

@ApiModelProperty(value = "实际抵扣金额")
private BigDecimal factDeductionAmount;

@ApiModelProperty(value = "最高抵扣金额")
private BigDecimal maxDeductionAmount;

@ApiModelProperty(value = "折扣率")
private BigDecimal deductionRate;

@ApiModelProperty(value = "是否全场通用 1 表示全场通用 0表示否")
private Integer couponType;

}