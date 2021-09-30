package co.yixiang.modules.xikang.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 熙康医院与商城药品的映射 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="XikangMedMappingQueryParam对象", description="熙康医院与商城药品的映射查询参数")
public class XikangMedMappingQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
