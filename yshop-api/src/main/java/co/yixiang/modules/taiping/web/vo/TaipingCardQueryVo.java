package co.yixiang.modules.taiping.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 太平乐享虚拟卡 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-11-19
 */
@Data
@ApiModel(value="TaipingCardQueryVo对象", description="太平乐享虚拟卡查询参数")
public class TaipingCardQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
private Integer id;

@ApiModelProperty(value = "卡号")
private String cardNumber;

@ApiModelProperty(value = "卡的具体种类")
private String cardType;

@ApiModelProperty(value = "卡渠道")
private String sellChannel;

@ApiModelProperty(value = "代理")
private String agentCate;

@ApiModelProperty(value = "组织ID")
private String organId;

@ApiModelProperty(value = "乐享同步记录时间")
private String insertTime;

private Date createTime;

private Date updateTime;

}