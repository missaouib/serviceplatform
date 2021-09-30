package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联退单对象", description="药联退单对象")
public class RefundOrder  implements Serializable {
    @ApiModelProperty(value = "药联订单号")
    private String id;

    @ApiModelProperty(value = "药联退单时间")
    private String create_time;

    @ApiModelProperty(value = "门店内码")
    private String store_id;

    @ApiModelProperty(value = "店员手机号")
    private String assistant_mobile;

    @ApiModelProperty(value = "店员工号")
    private String assistant_number;
}
