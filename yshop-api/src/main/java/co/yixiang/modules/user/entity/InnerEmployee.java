package co.yixiang.modules.user.entity;

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
 * 内部员工表
 * </p>
 *
 * @author visazhou
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="InnerEmployee对象", description="内部员工表")
public class InnerEmployee extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "名称")
private String name;

@ApiModelProperty(value = "员工工号")
private String code;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

}
