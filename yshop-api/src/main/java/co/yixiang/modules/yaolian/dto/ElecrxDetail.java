package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联处方明细对象", description="药联处方明细对象")
public class ElecrxDetail  implements Serializable {
    @ApiModelProperty(value = "药品名称")
    private String common_name;
    @ApiModelProperty(value = "药品数量")
    private String amount;
    @ApiModelProperty(value = "药品规格")
    private String form;
    @ApiModelProperty(value = "用药方式")
    private String how;
}
