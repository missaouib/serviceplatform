package co.yixiang.modules.yaoshitong.entity;

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
 * 患者对应的标签库
 * </p>
 *
 * @author visa
 * @since 2020-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YaoshitongUserLableRelation对象", description="患者对应的标签库")
public class YaoshitongUserLableRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "药师的用户id")
private Integer uid;

@ApiModelProperty(value = "患者id")
private Integer patientId;

@ApiModelProperty(value = "标签id")
private Integer lableId;

@ApiModelProperty(value = "生成时间")
private Date createTime;

@ApiModelProperty(value = "更新时间")
private Date updateTime;

private String pharmacistId;
}
