package co.yixiang.modules.shop.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 罗氏罕见病sma医院列表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2021-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RocheHospitalQueryParam对象", description="罗氏罕见病sma医院列表查询参数")
public class RocheHospitalQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @Query(type = Query.Type.INNER_LIKE)
    private String name;
    @Query
    private String status;
}
