package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 病种 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreDiseaseQueryParam对象", description="病种查询参数")
public class YxStoreDiseaseQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    // 项目编码
    private String projectCode = "";

    // 药房分类  85折药房/5折药房
    private String drugStoreType = "";

    private String cardType = "";

    private String partnerCode;

    private String cateType;
}
