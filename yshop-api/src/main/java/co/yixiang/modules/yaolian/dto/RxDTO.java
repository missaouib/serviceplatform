package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="药联处方查询传输对象", description="药联处方查询传输对象")
public class RxDTO implements Serializable {
    @ApiModelProperty(value = "请求头")
    private RequestHead requestHead;

    @ApiModelProperty(value = "组织代码")
    private String cooperation;

    @ApiModelProperty(value = "处方单流水号")
    private String rx_id;
}
