/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.service.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
* @author hupeng
* @date 2020-05-12
*/
@Data
public class YxStoreOrder4PCDto implements Serializable {
    // 订单ID
    private Integer id;

    //状态名称
    private String statusName;

    private List<OrderDetailDto> details;

    // 订单号
    private String orderId;

    // 用户id
    private Integer uid;

    // 用户姓名
    private String realName;

    // 用户电话
    private String userPhone;

    // 详细地址
    private String userAddress;


    // 运费金额
    private BigDecimal freightPrice;

    // 订单商品总数
    private Integer totalNum;

    // 订单总价
    private BigDecimal totalPrice;

    // 邮费
    private BigDecimal totalPostage;

    // 实际支付金额
    private BigDecimal payPrice;

    // 支付邮费
    private BigDecimal payPostage;

    // 创建时间
    private Integer addTime;

    // 快递名称/送货人姓名
    private String deliveryName;

    private String deliverySn;

    // 发货类型
    private String deliveryType;

    // 快递单号/手机号
    private String deliveryId;

    // 备注
    private String mark;

    // 是否删除
    private Integer isDel;

    // 管理员备注
    private String remark;


    private String imagePath;

    private List<String> imagePathList;

    private String partnerCode;

    private String projectCode;

    private String provinceName;

    private String cityName;

    private String districtName;

    private Integer paid;

    private Integer status;
    private Integer refundStatus;

    @ApiModelProperty(value = "审核不通过原因")
    private String checkFailReason;

    @ApiModelProperty(value = "审核不通过备注")
    private String checkFailRemark;

    @ApiModelProperty(value = "审核人")
    private String checkUser;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private java.util.Date checkTime;

    @ApiModelProperty(value = "审核状态")
    private String checkStatus;
}
