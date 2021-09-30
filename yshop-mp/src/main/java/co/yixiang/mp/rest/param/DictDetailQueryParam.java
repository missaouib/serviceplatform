package co.yixiang.mp.rest.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据字典详情 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="DictDetailQueryParam对象", description="数据字典详情查询参数")
public class DictDetailQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    private String name;

    private String label;
}
