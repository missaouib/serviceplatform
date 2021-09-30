package co.yixiang.modules.shop.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;

import java.util.Date;

/**
 * <p>
 * 项目配置销售省份 查询结果对象
 * </p>
 *
 * @author visa
 * @date 2021-04-12
 */
@Data
@ApiModel(value="ProjectSalesAreaQueryVo对象", description="项目配置销售省份查询参数")
public class ProjectSalesAreaQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "主键")
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