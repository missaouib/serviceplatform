package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Slf4j
@ApiModel(value="药联接口请求头对象", description="药联接口请求头对象")
public class RequestHead   implements Serializable {

    @ApiModelProperty(value = "合作伙伴代码")
    private String cooperation;
    @ApiModelProperty(value = "加密因子")
    private String nonce;
    @ApiModelProperty(value = "签名结果")
    private String sign;
    @ApiModelProperty(value = "加密因子，时间戳")
    private String timestamp;

    @ApiModelProperty(value = "交易时间")
    private String tradeDate;

}
