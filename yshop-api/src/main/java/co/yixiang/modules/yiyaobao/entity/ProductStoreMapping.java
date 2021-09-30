package co.yixiang.modules.yiyaobao.entity;

import java.math.BigDecimal;
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
 * 商品-药店-价格配置
 * </p>
 *
 * @author visazhou
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ProductStoreMapping对象", description="商品-药店-价格配置")
public class ProductStoreMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "商品id")
private Integer productId;

@ApiModelProperty(value = "药店id")
private Integer storeId;

@ApiModelProperty(value = "是否删除")
private Boolean isDel;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

@ApiModelProperty(value = "商品价格")
private BigDecimal price;

@ApiModelProperty(value = "商品库存")
private Integer stock;

}
