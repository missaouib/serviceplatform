package co.yixiang.modules.api.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 益药宝订单状态
 * </p>
 *
 * @author visa
 * @date 2019-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="订单查询对象", description="订单查询对象")
public class OrderStatusParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @NotBlank(message="请订单号")
    private String orderNO;

    private Boolean flag;

}
