package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/6 14:01
 */
@Data
@ApiModel(value="美德医商品传输对象", description="美德医商品传输对象")
public class Goods {
    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品编码")
    private String code;

    @ApiModelProperty(value = "通用名")
    private String generalName;

    @ApiModelProperty(value = "商品规格")
    private String normal;

    @ApiModelProperty(value = "厂家")
    private String factory;

    @ApiModelProperty(value = "批准文号")
    private String approvalNumber;

    @ApiModelProperty(value = "包装单位")
    private String packageUnit;

    @ApiModelProperty(value = "商品主图")
    private String itemImg;

    @ApiModelProperty(value = "副图 多个用逗号分隔")
    private String viceImage;

    @ApiModelProperty(value = "价格，单位：分")
    private Integer price;

    @ApiModelProperty(value = "库存")
    private String stock;
}
