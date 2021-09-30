package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@ApiModel(value="药联订单对象", description="药联订单对象")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "交易流水号(即药联订单号)")
    private String id;
    @ApiModelProperty(value = "药联下单时间")
    private String create_time;
    @ApiModelProperty(value = "门店内码")
    private String store_id;
    @ApiModelProperty(value = "会员ID（需要配合会员数据录入接口实现）")
    private String member_id;
    @ApiModelProperty(value = "店员手机号")
    private String assistant_mobile;
    @ApiModelProperty(value = "店员工号")
    private String assistant_number;
    @ApiModelProperty(value = "订单总价")
    private String total_price;
    @ApiModelProperty(value = "药联直付金额")
    private String free_price;
    @ApiModelProperty(value = "顾客自付金额")
    private String sale_price;
    @ApiModelProperty(value = "超级会员日订单标示")
    private String isSuper;
    @ApiModelProperty(value = "订单是否有处方单标示，1是存在处方单，0是没有")
    private String is_prescription;
    @ApiModelProperty(value = "处方单流水号")
    private String rx_id;
    @ApiModelProperty(value = "订单明细")
    private List<OrderDetail> retailDetail;

    private Elecrx elecrx;


}
