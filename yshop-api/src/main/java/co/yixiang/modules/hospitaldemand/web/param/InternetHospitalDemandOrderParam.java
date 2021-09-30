package co.yixiang.modules.hospitaldemand.web.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 互联网医院订单对象
 * </p>
 *
 * @author visa
 * @date 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="互联网医院订单对象", description="互联网医院订单对象")
public class InternetHospitalDemandOrderParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    private String orderId = "";
    /**
     *
     * 1 处方申请成功
     * 2 处方申请失败
     * 3 处方审核驳回
     * */
    private Integer applyFlag = 0;

    private String reason="";


}
