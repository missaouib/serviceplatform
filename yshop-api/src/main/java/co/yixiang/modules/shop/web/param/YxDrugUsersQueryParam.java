package co.yixiang.modules.shop.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 用药人列表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxDrugUsersQueryParam对象", description="用药人列表查询参数")
public class YxDrugUsersQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @Query
    private Integer uid;
    @Query
    private Integer isDel;
}
