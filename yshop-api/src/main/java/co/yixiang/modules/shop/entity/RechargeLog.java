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
 * 储值记录表
 * </p>
 *
 * @author visa
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RechargeLog对象", description="储值记录表")
public class RechargeLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
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
    @ApiModelProperty(value = "身份证号码")
private String cardId;

}
