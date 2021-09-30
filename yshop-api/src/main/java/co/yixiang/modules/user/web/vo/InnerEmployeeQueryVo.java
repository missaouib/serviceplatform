package co.yixiang.modules.user.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 内部员工表 查询结果对象
 * </p>
 *
 * @author visazhou
 * @date 2020-05-20
 */
@Data
@ApiModel(value="InnerEmployeeQueryVo对象", description="内部员工表查询参数")
public class InnerEmployeeQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

private Integer id;

@ApiModelProperty(value = "名称")
private String name;

@ApiModelProperty(value = "员工工号")
private String code;

@ApiModelProperty(value = "添加时间")
private Integer addTime;

}