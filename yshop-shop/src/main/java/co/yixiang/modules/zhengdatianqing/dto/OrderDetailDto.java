package co.yixiang.modules.zhengdatianqing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "正大天晴慈善赠药订单明细对象", description = "正大天晴慈善赠药订单明细对象")
public class OrderDetailDto {
    @ApiModelProperty(value = "药品ID")
    private String drug_id;

    @ApiModelProperty(value = "药品数量")
    private Integer quantity;
}
