package co.yixiang.modules.shop.entity;

import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 优惠券表
 * </p>
 *
 * @author hupeng
 * @since 2019-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreCoupon对象", description="优惠券表")
public class YxStoreCoupon extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "优惠券表ID")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "优惠券名称")
private String title;

@ApiModelProperty(value = "兑换消耗积分值")
private Integer integral;

@ApiModelProperty(value = "兑换的优惠券面值")
private BigDecimal couponPrice;

@ApiModelProperty(value = "最低消费多少金额可用优惠券")
private BigDecimal useMinPrice;

@ApiModelProperty(value = "优惠券有效期限（单位：天）")
private Integer couponTime;

@ApiModelProperty(value = "排序")
private Integer sort;

@ApiModelProperty(value = "状态（0：关闭，1：开启）")
private Boolean status;

@ApiModelProperty(value = "兑换项目添加时间")
private Integer addTime;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

@ApiModelProperty(value = "最高抵扣金额")
private BigDecimal maxDeductionAmount;

@ApiModelProperty(value = "折扣率")
private BigDecimal deductionRate;

@ApiModelProperty(value = "是否全场通用 1 表示全场通用 0 表示否")
private Integer couponType;

    @ApiModelProperty(value = "优惠券类型 1 满减券 2.折扣券 3.抵用券")
    private Integer type;
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    @ApiModelProperty(value = "固定生效日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    @ApiModelProperty(value = "有效起期")
    private Date couponEffectiveTime;

    @ApiModelProperty(value = "有效止期")
    private Date couponExpiryTime;

}
