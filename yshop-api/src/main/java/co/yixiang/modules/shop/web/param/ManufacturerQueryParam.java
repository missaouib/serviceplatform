package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 生产厂家主数据表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ManufacturerQueryParam对象", description="生产厂家主数据表查询参数")
public class ManufacturerQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
