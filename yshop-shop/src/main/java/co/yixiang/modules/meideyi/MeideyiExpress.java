package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:45
 */
@Data
@ApiModel(value="美德医收货信息传输对象", description="美德医收货信息传输对象")
public class MeideyiExpress {
    @ApiModelProperty(value = "收货人姓名")
    private String name;

    @ApiModelProperty(value = "收货人电话")
    private String mobile;

    @ApiModelProperty(value = "收货人地址")
    private String address;
}
