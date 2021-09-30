package co.yixiang.modules.order.web.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 用于二维码项目购药中计算订单金额的dto类
 * */
@Data
public class OrderDetail4Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer qty;
    private Integer drugStoreId;
}
