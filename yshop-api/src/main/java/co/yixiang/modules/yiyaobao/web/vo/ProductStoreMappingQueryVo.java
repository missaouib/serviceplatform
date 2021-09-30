package co.yixiang.modules.yiyaobao.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品-药店-价格配置 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-18
 */
@Data
@ApiModel(value="ProductStoreMappingQueryVo对象", description="商品-药店-价格配置查询参数")
public class ProductStoreMappingQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

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

}