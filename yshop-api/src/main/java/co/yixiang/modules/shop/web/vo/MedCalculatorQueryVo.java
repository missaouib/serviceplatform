package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用药计算器 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-01-03
 */
@Data
@ApiModel(value="MedCalculatorQueryVo对象", description="用药计算器查询参数")
public class MedCalculatorQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "首次日期")
private Date startDate;

@ApiModelProperty(value = "药品名称")
private String medName;

@ApiModelProperty(value = "药品数量")
private Integer medAmount;

@ApiModelProperty(value = "用药剂量，单位ml")
private BigDecimal useAmount;

@ApiModelProperty(value = "计算结果，还剩多少天")
private Integer result;

@ApiModelProperty(value = "已坚持服用了多少天")
private Integer days;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

}