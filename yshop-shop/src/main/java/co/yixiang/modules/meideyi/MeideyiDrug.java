package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:51
 */
@Data
@ApiModel(value="美德医药品信息传输对象", description="美德医药品信息传输对象")
public class MeideyiDrug {
    @ApiModelProperty(value = "药品编码")
    private String drugCode;

    @ApiModelProperty(value = "药品名称")
    private String drugName;

    @ApiModelProperty(value = "规格")
    private String drugSpec;

    @ApiModelProperty(value = "单价")
    private String price;

    @ApiModelProperty(value = "包装单位")
    private String unit;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "总价")
    private String totalPrice;
}
