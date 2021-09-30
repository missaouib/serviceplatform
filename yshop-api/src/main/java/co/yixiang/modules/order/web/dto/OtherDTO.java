package co.yixiang.modules.order.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OtherDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/27
 **/
@Data
public class OtherDTO implements Serializable {
    //线下包邮
    private String offlinePostage;
    //积分抵扣
    private String integralRatio;

    //最大
    private String integralMax;

    //满多少
    private String integralFull;

    private Integer storeId;

    private String storeName;


    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "推荐人编码")
    private String refereeCode;

    @ApiModelProperty(value = "合作伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "医院编码")
    private String departCode;

    @ApiModelProperty(value = "太平卡号")
    private String cardNumber;

    @ApiModelProperty(value = "太平卡类型")
    private String cardType;

    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;

    @ApiModelProperty(value = "订单来源，互联网医院来的订单")
    private String orderSource;

    @ApiModelProperty(value = "互联网医院的需求单id")
    private Integer demandId;

    private Boolean needImageFlag;

}
