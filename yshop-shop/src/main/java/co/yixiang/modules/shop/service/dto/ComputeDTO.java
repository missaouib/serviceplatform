package co.yixiang.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ComputeDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/27
 **/
@Data
public class ComputeDTO implements Serializable {
    private Double couponPrice;
    private Double deductionPrice;
    private Double payPostage;
    private Double payPrice;
    private Double totalPrice;
}
