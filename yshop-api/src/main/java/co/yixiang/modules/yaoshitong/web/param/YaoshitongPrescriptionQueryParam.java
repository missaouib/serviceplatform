package co.yixiang.modules.yaoshitong.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 药师通-处方信息表 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongPrescriptionQueryParam对象", description="药师通-处方信息表查询参数")
public class YaoshitongPrescriptionQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @Query(type = Query.Type.INNER_LIKE)
    private String hospitalName;

    @Query(type = Query.Type.EQUAL)
    private String pharmacistId;
}
