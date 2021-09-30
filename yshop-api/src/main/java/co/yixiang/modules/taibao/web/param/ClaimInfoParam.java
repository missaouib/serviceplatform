package co.yixiang.modules.taibao.web.param;

import co.yixiang.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 太平乐享虚拟卡 查询参数对象
 * </p>
 *
 * @author visa
 * @date 2020-11-19
 */
@Data
@ApiModel(value="ClaimInfoParam对象", description="太保订单附件参数")
public class ClaimInfoParam {

    @ApiModelProperty(value = "订单Id")
    private Long orderId;

    @ApiModelProperty(value = "图片附件")
    private String imgPah;

}
