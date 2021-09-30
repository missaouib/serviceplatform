package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:33
 */
@Data
@ApiModel(value="美德医已完成订单传输对象", description="美德医已完成订单传输对象")
public class MeideyiRefundOrder {
    @ApiModelProperty(value = "美德医系统里的用户的唯一标识")
    private String userId;

    @ApiModelProperty(value = "投保人手机号码")
    private String mobile;

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "药房编码,固定值：\"yy01\"")
    private String providerCode;

    @ApiModelProperty(value = "退款时间,2018-04-01 00:00:00")
    private String refundTime;


}
