package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:49
 */
@Data
@ApiModel(value="美德医物流信息传输对象", description="美德医物流信息传输对象")
public class MeideyiLogistics {
    @ApiModelProperty(value = "物流单号")
    private String logisticsCode;

    @ApiModelProperty(value = "物流公司名称")
    private String company;

    @ApiModelProperty(value = "物流公司电话")
    private String mobile;

    @ApiModelProperty(value = "物流动态")
    private String message;
}
