package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@ApiModel(value="药联订单传输对象", description="药联订单传输对象")
public class OrdersDTO  implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订单")
    @NotBlank
    private List<Order> orders;
    @ApiModelProperty(value = "请求头")
    @NotBlank
    private RequestHead requestHead;
}
