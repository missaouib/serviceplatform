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
@ApiModel(value="药联退单传输对象", description="药联退单传输对象")
public class RefundOrderDTO  implements Serializable {
    @ApiModelProperty(value = "药联退单")
    private List<RefundOrder> refund;

    @ApiModelProperty(value = "请求头")
    private RequestHead requestHead;
}
