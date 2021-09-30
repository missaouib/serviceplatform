package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 *  查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RocheStoreQueryParam对象", description="查询参数")
public class RocheStoreQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
