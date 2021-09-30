package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 用药计算器用药量变更表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-01-12
 */
@Data
@ApiModel(value="MedCalculatorDetailQueryVo对象", description="用药计算器用药量变更表查询参数")
public class MedCalculatorDetailQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "更改日期")
private Date modifyDate;

@ApiModelProperty(value = "用药量")
private Integer useAmount;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

}