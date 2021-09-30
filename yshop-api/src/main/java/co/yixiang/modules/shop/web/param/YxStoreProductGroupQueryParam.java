package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商品组合 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreProductGroupQueryParam对象", description="商品组合查询参数")
public class YxStoreProductGroupQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
