package co.yixiang.modules.yaolian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 药联订单明细
 * </p>
 *
 * @author visa
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaolianOrderDetail对象", description="药联订单明细")
public class YaolianOrderDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "药联订单号")
private String orderId;

@ApiModelProperty(value = "商品id，和连锁商品保持一致")
private String drugId;

@ApiModelProperty(value = "商品通用名")
private String commonName;

@ApiModelProperty(value = "数量")
private String amount;

@ApiModelProperty(value = "商品原价")
private String price;

@ApiModelProperty(value = "结算扣率")
private String settleDiscountRate;

@ApiModelProperty(value = "商品条形码")
private String code;

@ApiModelProperty(value = "1:使用优惠价购买0:未使用到优惠价")
private String activityType;

}
