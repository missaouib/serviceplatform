package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 项目对应的药品 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-11
 */
@Data
@ApiModel(value="Product4projectQueryVo对象", description="项目对应的药品查询参数")
public class Product4projectQueryVo implements Serializable{
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

}