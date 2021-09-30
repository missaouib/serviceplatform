package co.yixiang.modules.shop.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 商品组合
 * </p>
 *
 * @author visa
 * @since 2021-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreProductGroup对象", description="商品组合")
public class YxStoreProductGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "父商品sku")
private String parentProductYiyaobaoSku;

@ApiModelProperty(value = "父商品id")
private Integer parentProductId;

@ApiModelProperty(value = "商品sku")
private String productYiyaobaoSku;

@ApiModelProperty(value = "商品id")
private Integer productId;

@ApiModelProperty(value = "默认销售数量")
private Integer num;

@ApiModelProperty(value = "销售单价")
private BigDecimal unitPrice;

private Date createTime;

private Date updateTime;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

@ApiModelProperty(value = "药品门店唯一码")
private String productUnique;

}
