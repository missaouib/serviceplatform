package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 医院 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="HospitalQueryParam对象", description="医院查询参数")
public class HospitalQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
