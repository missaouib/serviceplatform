package co.yixiang.mp.yiyaobao.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hupeng
 * @date 2019-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="订单查询对象", description="订单查询对象")
public class OrderQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @NotBlank(message="请填写手机号")
    private String mobile;

    private List<String> statusList;

    private List<String> orderNoList;

    private List<String> orderNoListNotExists;

    private String keyword;

    private String startDate;

    private String endDate;

}
