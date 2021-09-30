package co.yixiang.modules.yaoshitong.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 患者对应的标签库 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2020-08-26
 */
@Data
@ApiModel(value="YaoshitongUserLableRelationQueryVo对象", description="患者对应的标签库查询参数")
public class YaoshitongUserLableRelationQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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

}