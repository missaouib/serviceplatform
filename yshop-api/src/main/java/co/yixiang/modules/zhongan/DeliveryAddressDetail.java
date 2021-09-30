package co.yixiang.modules.zhongan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="众安普药订单收货地址对象", description="众安普药订单收货地址对象")
public class DeliveryAddressDetail {
    @ApiModelProperty(value = "联系人名称")
    private String contactUserName;
    @ApiModelProperty(value = "联系人手机号码")
    private String contactUserPhone;
    @ApiModelProperty(value = "省份Code")
    private String provinceCode;
    @ApiModelProperty(value = "省份名称")
    private String provinceName;
    @ApiModelProperty(value = "城市Code")
    private String cityCode;
    @ApiModelProperty(value = "城市名称")
    private String cityName;
    @ApiModelProperty(value = "区县Code")
    private String countryCode;
    @ApiModelProperty(value = "区县名称")
    private String countryName;
    @ApiModelProperty(value = "详细地址")
    private String addressDetail;

}
