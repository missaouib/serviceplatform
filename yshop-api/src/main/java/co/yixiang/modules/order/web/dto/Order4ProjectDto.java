package co.yixiang.modules.order.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
 * 用于二维码项目购药中计算订单金额的dto类
 *
 */

@Data
public class Order4ProjectDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /*项目编码*/
    private String projectCode;
    /*药品列表*/
    private List<OrderDetail4Project> drugList;
    /*地址*/
    private Integer addressId;
    @ApiModelProperty(value = "运费模板id")
    private Integer expressTemplateId;
}
