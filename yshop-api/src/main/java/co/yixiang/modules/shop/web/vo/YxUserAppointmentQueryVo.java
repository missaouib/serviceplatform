package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 预约活动 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-06-05
 */
@Data
@ApiModel(value="YxUserAppointmentQueryVo对象", description="预约活动查询参数")
public class YxUserAppointmentQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键id")
private Integer id;

@ApiModelProperty(value = "活动id")
private Integer eventId;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "手机号")
private String mobile;

@ApiModelProperty(value = "活动名称")
private String eventName;

private Integer addTime;

@ApiModelProperty(value = "状态，0/已预约 1/已取消")
private Integer status;

@ApiModelProperty(value = "用户名称")
private String name;

}