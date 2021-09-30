package co.yixiang.modules.api.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 益药宝订单状态
 * </p>
 *
 * @author visa
 * @date 2019-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="处方状态信息对象", description="处方状态信息对象")
public class PrescripStatusParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "处方号")
    private String prescripNo;

    @ApiModelProperty(value = "处方状态")
    private String prescripStatus;

  //  @ApiModelProperty(value = "处理日期")
  //  private String dealDate;
    @ApiModelProperty(value = "益药宝订单id,主键")
    private String orderId;

    @ApiModelProperty(value = "物流公司名称")
    private String deliveryName;
    @ApiModelProperty(value = "物流运单号")
    private String deliveryId;

    @ApiModelProperty(value = "益药宝订单号")
    private String orderNo;

    @ApiModelProperty(value = "审核不通过备注")
    private String checkFailRemark;

    @ApiModelProperty(value = "审核不通过原因")
    private String checkFailReason;

    private String projectName;

    private String projectCode;

    private String orderSource;

    /** 审核原因 */
    private String auditReasons;

    /** 审核人 */
    private String  auditName;

}
