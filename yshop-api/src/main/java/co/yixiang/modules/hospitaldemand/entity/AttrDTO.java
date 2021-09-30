package co.yixiang.modules.hospitaldemand.entity;

import co.yixiang.modules.shop.entity.YxDrugUsers;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttrDTO {

    @ApiModelProperty(value = "uid")
    private Integer uid;

    @ApiModelProperty(value = "卡类型")
    private String cardType;

    @ApiModelProperty(value = "卡号")
    private String cardNumber;

    @ApiModelProperty(value = "原始订单号")
    private String orderNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectCode;

    @ApiModelProperty(value = "商城订单编号")
    private String orderId;

    private Integer drugUserid;
    @ApiModelProperty(value = "商城订单主键")
    private Integer id;
}
