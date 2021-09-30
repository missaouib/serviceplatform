package co.yixiang.modules.shop.web.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName AttrValueDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/23
 **/

@Data
public class PriceMinMaxDTO {
    private BigDecimal priceMin;
    private BigDecimal priceMax;
}
