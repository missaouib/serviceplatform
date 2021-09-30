package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 储值记录表 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-07-05
 */
@Data
@ApiModel(value="RechargeLogQueryVo对象", description="储值记录表查询参数")
public class RechargeLogQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "手机号")
private String phone;

@ApiModelProperty(value = "金额，单位元")
private Integer money;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "名称")
private String name;

}