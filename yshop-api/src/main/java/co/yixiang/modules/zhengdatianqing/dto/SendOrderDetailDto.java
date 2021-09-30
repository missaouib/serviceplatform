package co.yixiang.modules.zhengdatianqing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "正大天晴慈善赠药订单发货明细对象", description = "正大天晴慈善赠药订单发货明细对象")
public class SendOrderDetailDto {
    @ApiModelProperty(value = "药品ID")
    private String drug_id;

    @ApiModelProperty(value = "药品批号")
    private String sn;

    @ApiModelProperty(value = "药品数量")
    private Integer quantity;

    @ApiModelProperty(value = "药监码")
    private List<String> code_list;



}
