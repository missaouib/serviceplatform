package co.yixiang.modules.hospitaldemand.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 互联网医院导入的需求单 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="InternetHospitalDemandQueryParam对象", description="互联网医院导入的需求单查询参数")
public class InternetHospitalDemandQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @Query
    private Integer uid;

}
