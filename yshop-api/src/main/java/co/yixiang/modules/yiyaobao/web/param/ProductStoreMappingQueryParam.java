package co.yixiang.modules.yiyaobao.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商品-药店-价格配置 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ProductStoreMappingQueryParam对象", description="商品-药店-价格配置查询参数")
public class ProductStoreMappingQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
