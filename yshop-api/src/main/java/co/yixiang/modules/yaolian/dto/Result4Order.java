package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联订单接口返回对象", description="药联订单接口返回对象")
public class Result4Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "是否收到通知，1表示确认收到可以正常下单，2表示因为业务原因导致无法销账，0为数据保存失败")
    private String result;
    @ApiModelProperty(value = "订单流水号")
    private String order_id;
    @ApiModelProperty(value = "返回信息")
    private String msg;
}
