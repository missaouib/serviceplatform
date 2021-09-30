package co.yixiang.modules.zhengdatianqing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@ApiModel(value = "正大天晴订单对象", description = "正大天晴订单对象")
public class OrderDto {
    @ApiModelProperty(value = "订单号")
    private String order_sn;

    @ApiModelProperty(value = "患者名称")
    private String name;

    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区县")
    private String county;

    @ApiModelProperty(value = "乡镇")
    private String townships;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "处方照片")
    private String recipel;

    @ApiModelProperty(value = "预计发货日期")
    private String pre_receive_date;

    @ApiModelProperty(value = "患者当前援助⽅式")
    private String mode_name;

    @ApiModelProperty(value = "发货详情")
    private List<OrderDetailDto> details;
}
