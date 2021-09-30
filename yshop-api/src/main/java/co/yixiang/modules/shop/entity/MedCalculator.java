package co.yixiang.modules.shop.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 用药计算器
 * </p>
 *
 * @author visa
 * @since 2021-01-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MedCalculator对象", description="用药计算器")
public class MedCalculator extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "首次日期")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private Date startDate;

@ApiModelProperty(value = "药品名称")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private String medName = "";

@ApiModelProperty(value = "药品数量")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private Integer medAmount ;

@ApiModelProperty(value = "用药剂量，单位ml")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private Integer useAmount;

@ApiModelProperty(value = "计算结果，还剩多少天")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private Integer result;

@ApiModelProperty(value = "已坚持服用了多少天")
@TableField(updateStrategy = FieldStrategy.IGNORED)
private Integer days;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;
    @ApiModelProperty(value = "剩余量")
private Integer leftAmount;
    @ApiModelProperty(value = "计算日期")
    private Date calcuDate;

    @ApiModelProperty(value = "每次计算的剩余量")
    private Integer leftAmountTemp;

    @TableField(exist = false)
    private String year;
    @TableField(exist = false)
    private String month;
    @TableField(exist = false)
    private String day;
}
