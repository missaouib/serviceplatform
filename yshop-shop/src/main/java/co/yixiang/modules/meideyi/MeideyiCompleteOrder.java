package co.yixiang.modules.meideyi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/10 8:33
 */
@Data
@ApiModel(value="美德医已完成订单传输对象", description="美德医已完成订单传输对象")
public class MeideyiCompleteOrder {
    @ApiModelProperty(value = "美德医系统里的用户的唯一标识")
    private String userId;

    @ApiModelProperty(value = "投保人手机号码")
    private String mobile;

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "药房编码,固定值：\"yy01\"")
    private String providerCode;

    @ApiModelProperty(value = "药房名称,固定值：\"益药商城\"")
    private String providerName;

    @ApiModelProperty(value = "药房名称,固定值：\"益药商城\"")
    private String status;

    @ApiModelProperty(value = "收货地址")
    private MeideyiExpress express;

    @ApiModelProperty(value = "物流信息")
    private MeideyiLogistics logistics;

}
