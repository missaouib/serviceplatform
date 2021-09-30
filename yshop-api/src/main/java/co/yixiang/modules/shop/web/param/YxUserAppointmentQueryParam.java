package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 预约活动 查询参数对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxUserAppointmentQueryParam对象", description="预约活动查询参数")
public class YxUserAppointmentQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
