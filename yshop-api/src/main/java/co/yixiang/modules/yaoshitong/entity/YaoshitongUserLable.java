package co.yixiang.modules.yaoshitong.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * 药师通用户标签
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongUserLable对象", description="药师通用户标签")
public class YaoshitongUserLable extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "用户id")
private Integer uid;

@ApiModelProperty(value = "标签名称")
private String lableName;

@ApiModelProperty(value = "记录生成时间")
@TableField(fill= FieldFill.INSERT)
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
@TableField(fill= FieldFill.INSERT_UPDATE)
private Date updateTime;

@ApiModelProperty(value = "是否默认")
private Integer isDefault;

}
