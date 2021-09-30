package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 优惠券发放记录表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreCouponCardQueryParam对象", description="优惠券发放记录表查询参数")
public class YxStoreCouponCardQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
