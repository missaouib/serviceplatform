package co.yixiang.modules.zhengdatianqing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "正大天晴慈善赠药订单发货对象", description = "正大天晴慈善赠药订单发货对象")
public class SendOrderDto {
    @ApiModelProperty(value = "订单号")
    private String order_sn;

    @ApiModelProperty(value = "快递单号")
    private String tracking_number;

    @ApiModelProperty(value = "回执单号")
    private String receipt_number;

    @ApiModelProperty(value = "发货⽇期")
    private String receive_date;

    @ApiModelProperty(value = "发货详情")
    private List<SendOrderDetailDto> details;


}
