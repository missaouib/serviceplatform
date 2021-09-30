package co.yixiang.modules.taiping.entity;

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
 * 太平乐享虚拟卡
 * </p>
 *
 * @author visa
 * @since 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="TaipingCard对象", description="太平乐享虚拟卡")
public class TaipingCard extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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

private Integer uid;

}
