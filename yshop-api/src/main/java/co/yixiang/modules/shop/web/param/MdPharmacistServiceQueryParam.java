package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 药师在线配置表 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MdPharmacistServiceQueryParam对象", description="药师在线配置表查询参数")
public class MdPharmacistServiceQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    private String foreignId;
}
