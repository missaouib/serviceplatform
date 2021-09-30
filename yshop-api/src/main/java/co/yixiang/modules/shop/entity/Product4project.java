package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 项目对应的药品
 * </p>
 *
 * @author visazhou
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Product4project对象", description="项目对应的药品")
public class Product4project extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "项目编号")
private String projectNo;

@ApiModelProperty(value = "项目名称")
private String projectName;

@ApiModelProperty(value = "药品id")
private Integer productId;

@ApiModelProperty(value = "药品属性唯一id")
private String productUniqueId;

@ApiModelProperty(value = "药品数量")
private Integer num;

@ApiModelProperty(value = "药品名称")
private String productName;

@ApiModelProperty(value = "药店id")
private Integer storeId;

@ApiModelProperty(value = "药店名称")
private String storeName;

private String yiyaobaoProjectCode;

@ApiModelProperty(value = "是否删除 0/否 1/是")
private Integer isDel;

    /** 零售单价 */
    @ApiModelProperty(value = "零售单价")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "组合名称")
    private String groupName;
    @ApiModelProperty(value = "药品特殊信息")
    private String remarks;
    /** 是否固定药品数量 0/否 1/是 0/否 1/是 */
    private Integer isFixNum;

    private Integer isShow;
}
