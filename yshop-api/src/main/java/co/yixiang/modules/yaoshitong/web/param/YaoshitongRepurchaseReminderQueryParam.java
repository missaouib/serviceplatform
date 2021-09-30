package co.yixiang.modules.yaoshitong.web.param;

import co.yixiang.annotation.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 药品复购提醒 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongRepurchaseReminderQueryParam对象", description="药品复购提醒查询参数")
public class YaoshitongRepurchaseReminderQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @Query(type = Query.Type.EQUAL)
    private String status;
    @Query(type = Query.Type.EQUAL)
    private Integer drugstoreId;

    @Query(type = Query.Type.EQUAL)
    private Integer id;
}
