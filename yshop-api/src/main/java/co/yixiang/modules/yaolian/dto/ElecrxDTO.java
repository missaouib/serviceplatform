package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
@Data
@Slf4j
@ApiModel(value="药联处方传输对象", description="药联处方传输对象")
public class ElecrxDTO  implements Serializable {
    @ApiModelProperty(value = "处方")
    private List<Elecrx> elecrx;
}
