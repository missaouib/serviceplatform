package co.yixiang.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 用药计算器用药量变更表
 * </p>
 *
 * @author visa
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MedCalculatorDetail对象", description="用药计算器用药量变更表")
public class MedCalculatorDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
