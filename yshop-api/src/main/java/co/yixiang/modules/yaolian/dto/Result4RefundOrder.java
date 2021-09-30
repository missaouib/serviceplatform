package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联退单接口返回对象", description="药联退单接口返回对象")
public class Result4RefundOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "退单接口，1表示退单成功，0表示退单失败")
    private String result;
    @ApiModelProperty(value = "订单流水号")
    private String order_id;
    @ApiModelProperty(value = "返回信息")
    private String msg;
    @ApiModelProperty(value = "退单号")
    private String refund_id;
}
