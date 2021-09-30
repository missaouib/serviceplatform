package co.yixiang.modules.yaolian.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="药联退单传输对象", description="药联退单传输对象")
public class YaolianOrderRefundDto {
    @ApiModelProperty(value = "请求头")
    private ReqHead requestHead;
    @ApiModelProperty(value = "退单对象")
    private YaolianOrderRefund orderRefund;
}
