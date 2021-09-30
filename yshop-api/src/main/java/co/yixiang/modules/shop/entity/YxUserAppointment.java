package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 预约活动
 * </p>
 *
 * @author visazhou
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxUserAppointment对象", description="预约活动")
public class YxUserAppointment extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键id")
@TableId(value = "id", type = IdType.AUTO)
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

private String userCode;
}
