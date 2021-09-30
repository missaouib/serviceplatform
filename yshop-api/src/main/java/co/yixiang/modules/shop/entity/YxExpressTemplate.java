package co.yixiang.modules.shop.entity;

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
import java.util.List;

/**
 * <p>
 * 物流运费模板
 * </p>
 *
 * @author visa
 * @since 2020-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxExpressTemplate对象", description="物流运费模板")
public class YxExpressTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "模板名称")
private String templateName;

@ApiModelProperty(value = "物流商名称")
private String expressName;

@ApiModelProperty(value = "是否默认")
private Integer isDefault;

@ApiModelProperty(value = "项目代码")
private String projectCode;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "记录创建人")
private String creater;

@ApiModelProperty(value = "记录更新人")
private String maker;

@TableField(exist = false)
    private List<YxExpressTemplateDetail> details;

}
