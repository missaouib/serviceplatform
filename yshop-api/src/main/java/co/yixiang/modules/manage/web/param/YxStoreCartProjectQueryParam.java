package co.yixiang.modules.manage.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 购物车表-项目 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-08-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreCartProjectQueryParam对象", description="购物车表-项目查询参数")
public class YxStoreCartProjectQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    private String projectCode="";

    private Integer numType = 0;

    private String cardNmuber="";

    private String cardType="";
}
