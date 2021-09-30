package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 用户搜索词 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxUserSearchQueryParam对象", description="用户搜索词查询参数")
public class YxUserSearchQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    private Integer uid;
    private Integer isDel;
}
