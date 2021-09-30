package co.yixiang.modules.shop.web.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="项目编码对象", description="查询参数")
public class ProjectCodeQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    private String projectCode = "";

    private String cardNumber = "";

    private String cardType = "";

}
