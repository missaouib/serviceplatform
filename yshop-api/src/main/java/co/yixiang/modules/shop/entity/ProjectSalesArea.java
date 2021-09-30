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
 * 项目配置销售省份
 * </p>
 *
 * @author visa
 * @since 2021-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ProjectSalesArea对象", description="项目配置销售省份")
public class ProjectSalesArea extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "项目代码")
private String projectCode;

@ApiModelProperty(value = "省份名称")
private String areaName;

@ApiModelProperty(value = "免邮金额")
private Integer freePostage;

@ApiModelProperty(value = "记录生成时间")
private Date createTime;

@ApiModelProperty(value = "记录更新时间")
private Date updateTime;

@ApiModelProperty(value = "是否满额包邮 1/是 0 否")
private Integer isFree;

}
