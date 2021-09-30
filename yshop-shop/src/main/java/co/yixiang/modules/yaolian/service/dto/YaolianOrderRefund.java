package co.yixiang.modules.yaolian.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="药联退单对象", description="药联退单对象")
public class YaolianOrderRefund implements Serializable {
    @ApiModelProperty(value = "药联订单号")
    private String orderNo;
    @ApiModelProperty(value = "订单作废原因")
    private String reason;
    @ApiModelProperty(value = "店员工号")
    private String operatorId;
    @ApiModelProperty(value = "姓名")
    private String operatorName;
    @ApiModelProperty(value = "店员手机")
    private String operatorPhoneNumber;
}
