package co.yixiang.modules.taibao.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "垫付结果通知", description = "垫付结果通知")
@Data
public class PolicyInfoDTO {

    @ApiModelProperty(value = "保单ID")
    private Long id;
    @ApiModelProperty(value = " 01 同意 02不同意 03已垫付 04终止垫付,当为02/04时，必须给出拒赔原因")
    private String isAdopt;
    @ApiModelProperty(value = "拒绝原因")
    private String reason;

}
